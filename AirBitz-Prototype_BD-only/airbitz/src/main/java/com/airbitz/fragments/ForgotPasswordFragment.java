package com.airbitz.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbitz.R;
import com.airbitz.activities.NavigationActivity;
import com.airbitz.api.CoreAPI;
import com.airbitz.api.SWIGTYPE_p_void;
import com.airbitz.api.core;
import com.airbitz.api.tABC_Error;
import com.airbitz.api.tABC_QuestionChoices;
import com.airbitz.api.tABC_RequestResults;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 2/10/14.
 */
public class ForgotPasswordFragment extends Fragment {

    private Button mSubmitButton;

    private ImageButton mBackButton;
    private ImageButton mHelpButton;

    private TextView mTitleTextView;

    private LinearLayout mItemsLayout;

    private Map mRecoveryQA;

    private String mUsername;
    private FetchQuestionsTask mFetchQuestionsTask;

    private CoreAPI mCoreAPI;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_forgot_password, container, false);

        Bundle bundle = getArguments();
        if(bundle !=null) {
            mUsername = bundle.getString(SignUpFragment.KEY_USERNAME);
        }

        if(mCoreAPI==null)
            mCoreAPI = CoreAPI.getApi();


        mSubmitButton = (Button)view.findViewById(R.id.submitButton);
        mBackButton = (ImageButton) view.findViewById(R.id.fragment_category_button_back);
        mHelpButton = (ImageButton) view.findViewById(R.id.fragment_category_button_help);

        mTitleTextView = (TextView) view.findViewById(R.id.fragment_category_textview_title);
        mTitleTextView.setTypeface(NavigationActivity.montserratBoldTypeFace);

        mSubmitButton.setTypeface(NavigationActivity.montserratBoldTypeFace);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(answersCorrect(mRecoveryQA)) {
//                   startActivity(new Intent(ForgotPasswordFragment.this, NavigationActivity.class));
//                   finish();
                }
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        mHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mItemsLayout = (LinearLayout) view.findViewById(R.id.forgot_questions_layout);

        mFetchQuestionsTask = new FetchQuestionsTask(mUsername);
        mFetchQuestionsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);

        return view;
    }

    private boolean answersCorrect(Map<String, String> map) {
        boolean truth = true;
        for(int i=0; i<mItemsLayout.getChildCount(); i++) {
            View v = mItemsLayout.getChildAt(i);
            String question = ((TextView) ((ViewGroup)v).getChildAt(0)).getText().toString();
            String userAnswer = ((TextView) ((ViewGroup)v).getChildAt(1)).getText().toString();
            String realAnswer = map.get(question);
            if(!userAnswer.equals(realAnswer))
                truth = false;
        }
        return truth;
    }

    private void populateQuestions(Map<String, String> map) {
        for(String s: map.keySet()) {
            mItemsLayout.addView(getQueryView(s));
        }
    }

    private View getQueryView(String question) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.item_password_forgot, null);
        TextView questionTextView = (TextView)view.findViewById(R.id.item_password_forgot_question);
        questionTextView.setText(question);

        return view;
    }

    /**
     * An asynchronous question fetch task
     */
    public class FetchQuestionsTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;
        Map<String, String> questionMap = new HashMap<String, String>();

        tABC_Error pError = new tABC_Error();
        tABC_RequestResults pData = new tABC_RequestResults();
        SWIGTYPE_p_void pVoid = core.requestResultsp_to_voidp(pData);

        FetchQuestionsTask(String username) {
            mUsername = username;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

//            tABC_CC result = core.ABC_GetQuestionChoices(mUsername, null, pVoid, pError);
//            boolean success = result == tABC_CC.ABC_CC_Ok? true: false;
//
//            if(success) {
//                QuestionChoices qc = new QuestionChoices(pData.getPRetData());
//                long num = qc.getNumChoices();
//
//                if(num>0) {
//                    //TODO setup the map of questions and answers.
//
//                } else {
//                    success = false;
//                }
//            }
//            return success;
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mFetchQuestionsTask = null;
            populateQuestions(mRecoveryQA);

            if (success) {
                populateQuestions(questionMap);
            } else {
                showNoQuestionsDialog();
            }
        }

        @Override
        protected void onCancelled() {
            mFetchQuestionsTask = null;
        }
    }


    private class QuestionChoices extends tABC_QuestionChoices {
        private boolean ok=true;
        public QuestionChoices(SWIGTYPE_p_void pv) {
            super(PVoid.getPtr(pv), false);
            if(PVoid.getPtr(pv)==0) {
                ok = false;
            }
        }
        public long getNumChoices() {
            if(ok)
                return super.getNumChoices();
            else
                return 0;
        }

    }

    private static class PVoid extends SWIGTYPE_p_void {
        public static long getPtr(SWIGTYPE_p_void p) { return getCPtr(p); }
    }

    private void showNoQuestionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom));
        builder.setMessage(getResources().getString(R.string.activity_forgot_no_questions))
                .setCancelable(false)
                .setNeutralButton(getResources().getString(R.string.string_ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                getActivity().onBackPressed();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

}