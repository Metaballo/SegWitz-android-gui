package com.airbitz.objects;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.airbitz.R;
import com.airbitz.api.CoreAPI;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.util.List;

/**
 * Created by tom on 2/6/15.
 */
public class QRCamera implements
        Camera.PreviewCallback {
    final String TAG = getClass().getSimpleName();

    public static final int RESULT_LOAD_IMAGE = 678;
    final int FOCUS_MILLIS = 2000;

    CameraManager mCameraManager;
    Fragment mFragment;
    CameraSurfacePreview mPreview;
    FrameLayout mPreviewFrame, mPreviewObscura;
    View mCameraLayout;
    Handler mHandler;

    //************** Callback for notification of a QR code scan result
    OnScanResult mOnScanResult;
    public interface OnScanResult {
        public void onScanResult(String info);
    }

    public void setOnScanResultListener(OnScanResult listener) {
        mOnScanResult = listener;
    }

    Runnable cameraFocusRunner = new Runnable() {
        @Override
        public void run() {
            mCameraManager.autoFocus();
            mHandler.postDelayed(cameraFocusRunner, FOCUS_MILLIS);
        }
    };

    public QRCamera(Fragment frag, View cameraLayout) {
        mFragment = frag;
        mCameraLayout = cameraLayout;
        mCameraManager = CameraManager.instance();

        mHandler = new Handler();
        mPreviewFrame = (FrameLayout) mCameraLayout.findViewById(R.id.layout_camera_preview);
        mPreviewObscura = (FrameLayout) mCameraLayout.findViewById(R.id.layout_camera_obscura);
    }

    Runnable mCameraStartRunner = new Runnable() {
        @Override
        public void run() {
            if (mCameraManager.getCamera() != null) {
                if (mCameraManager.isPreviewing()) {
                    stopCamera();
                }
                mHandler.postDelayed(mCameraStartRunner, 200);
            } else {
                startupTheCamera();
            }
        }
    };

    public void startScanning() {
        mCameraManager.previewOn();
    }

    public void stopScanning() {
        mCameraManager.previewOff();
    }

    public void startCamera() {
        mHandler.post(mCameraStartRunner);
    }

    private int pickCamera() {
        // Get back camera unless there is none, then try the front camera - fix for Nexus 7
        int numCameras = Camera.getNumberOfCameras();
        if (numCameras == 0) {
            Log.d(TAG, "No cameras!");
            return -1;
        }

        int cameraIndex = 0;
        while (cameraIndex < numCameras) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(cameraIndex, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                break;
            }
            cameraIndex++;
        }

        if (cameraIndex >= numCameras) {
            cameraIndex = 0; //Front facing camera if no other camera index returned
        }
        return cameraIndex;
    }

    private void startupTheCamera() {
        if (mFragment.getActivity() == null) {
            return;
        }
        final int cameraIndex = pickCamera();
        if (cameraIndex < 0) {
            return;
        }
        mCameraManager.startCamera(cameraIndex, new CameraManager.Callback() {
            public void success(final Camera camera) {
                mHandler.post(new Runnable() {
                    public void run() {
                        finishOpenCamera();
                    }
                });
            }
            public void error(Exception e) {
                Log.e(TAG, "Camera Does Not exist", e);
                mHandler.postDelayed(mCameraStartRunner, 500);
            }
        });
    }

    private void finishOpenCamera() {
        if (mFragment.getActivity() == null || null == mCameraManager.getCamera()) {
            return;
        }
        mPreview = new CameraSurfacePreview(mFragment.getActivity());
        mCameraManager.postToCameraThread(new Runnable() {
            public void run() {
                setupCameraParams();
            }
        });
        mCameraManager.previewOn();
        mHandler.post(new Runnable() {
            public void run() {
                mPreviewFrame.addView(mPreview);
            }
        });
    }

    public void stopCamera() {
        if (mCameraManager.getCamera() != null) {
            mPreviewFrame.removeView(mPreview);
        }
        Log.d(TAG, "stopping camera");
        mCameraManager.previewOff();
        mCameraManager.stopPreview();
        mCameraManager.release();
    }

    public boolean isFlashOn() {
        return mCameraManager.isFlashOn();
    }

    public void setFlashOn(boolean on) {
        mCameraManager.flash(on);
    }

    // delegated from the containing fragment
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = mFragment.getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));

            String info = attemptDecodePicture(thumbnail);
            if (mOnScanResult != null) {
                mOnScanResult.onScanResult(info);
            }
        }
    }

    // Select a picture from the Gallery
    private void PickAPicture() {
        Intent in = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        mFragment.startActivityForResult(in, RESULT_LOAD_IMAGE);
    }

    // Post Result on main thread
    private void receivedQrCode(final String info) {
        mHandler.post(new Runnable() {
            public void run() {
                if (mOnScanResult != null) {
                    mOnScanResult.onScanResult(info);
                }
            }
        });
    }

    private void setupCameraParams() {
        if (mCameraManager.getCamera() != null) {
            Camera camera = mCameraManager.getCamera();
            camera.setPreviewCallback(this);
            Camera.Parameters params = camera.getParameters();
            if (params != null) {
                List<String> supportedFocusModes = camera.getParameters().getSupportedFocusModes();
                if (supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                } else if (supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO) &&
                        !supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_FIXED)) {
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                    mHandler.post(cameraFocusRunner);
                }
                camera.setParameters(params);
            }
        }
    }

    private void obscuraUp() {
        mHandler.post(new Runnable() {
            public void run() {
                if (mPreviewObscura.getVisibility() != View.VISIBLE) {
                    mPreviewObscura.setAlpha(0f);
                    mPreviewObscura.animate().alpha(1f).setDuration(200)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                mPreviewObscura.setVisibility(View.VISIBLE);
                            }
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mPreviewFrame.removeView(mPreview);
                            }
                        });
                }
            }
        });
    }

    private void obscuraDown() {
        mHandler.post(new Runnable() {
            public void run() {
                if (mPreviewObscura.getVisibility() != View.GONE) {
                    mPreviewObscura.animate().alpha(0f).setDuration(200)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mPreviewObscura.setVisibility(View.GONE);
                            }
                        });
                }
            }
        });
    }

    @Override
    public void onPreviewFrame(final byte[] bytes, final Camera camera) {
        if (!mCameraManager.isPreviewing()) {
            return;
        }
        obscuraDown();
        tryBytes(bytes, camera);
    }

    private void tryBytes(final byte[] bytes, final Camera camera) {
        if (mCameraManager.getCamera() == null || !mCameraManager.isPreviewing()) {
            return;
        }
        String info = attemptDecodeBytes(bytes, camera);
        if (info == null) {
            return;
        }
        mCameraManager.previewOff();
        receivedQrCode(info);
    }

    public String attemptDecodeBytes(byte[] bytes, Camera camera) {
        Result rawResult = null;
        Reader reader = new QRCodeReader();
        int w = camera.getParameters().getPreviewSize().width;
        int h = camera.getParameters().getPreviewSize().height;
        PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(bytes, w, h, 0, 0, w, h, false);
        if (source.getMatrix() != null) {
            BinaryBitmap bitmap = new BinaryBitmap(new GlobalHistogramBinarizer(source));
            try {
                rawResult = reader.decode(bitmap);
            } catch (ReaderException re) {
                // nothing to do here
            } finally {
                reader.reset();
            }
        }
        if (rawResult != null) {
            Log.d(TAG, "QR code found " + rawResult.getText());
            return rawResult.getText();
        } else {
            return null;
        }
    }

    public String attemptDecodePicture(Bitmap thumbnail) {
        if (thumbnail == null) {
            Log.d(TAG, "No picture selected");
        } else {
            Log.d(TAG, "Picture selected");
            Result rawResult = null;
            Reader reader = new QRCodeReader();
            int w = thumbnail.getWidth();
            int h = thumbnail.getHeight();
            int maxOneDimension = 500;
            if(w * h > maxOneDimension * maxOneDimension) { //too big, reduce
                float bitmapRatio = (float)w / (float) h;
                if (bitmapRatio > 0) {
                    w = maxOneDimension;
                    h = (int) (w / bitmapRatio);
                } else {
                    h = maxOneDimension;
                    w = (int) (h * bitmapRatio);
                }
                thumbnail = Bitmap.createScaledBitmap(thumbnail, w, h, true);

            }
            int[] pixels = new int[w * h];
            thumbnail.getPixels(pixels, 0, w, 0, 0, w, h);
            RGBLuminanceSource source = new RGBLuminanceSource(w, h, pixels);
            if (source.getMatrix() != null) {
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                try {
                    rawResult = reader.decode(bitmap);
                } catch (ReaderException re) {
                    re.printStackTrace();
                } finally {
                    reader.reset();
                }
            }
            if (rawResult != null) {
                Log.d(TAG, "QR code found " + rawResult.getText());
                return rawResult.getText();
            } else {
                Log.d(TAG, "Picture No QR code found");
            }
        }
        return null;
    }
}
