/**
 * Copyright (c) 2014, Airbitz Inc
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms are permitted provided that
 * the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Redistribution or use of modified source code requires the express written
 *    permission of Airbitz Inc.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the Airbitz Project.
 */

package com.airbitz;

import android.app.Application;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.airbitz.objects.AirbitzRequestHandler;
import com.airbitz.api.CoreAPI;

import com.squareup.picasso.Picasso;

import java.util.Stack;
import java.util.UUID;

/**
 * Created by tom on 6/17/14.
 * Holds App statics for login info during the app lifecycle
 */
public class AirbitzApplication extends Application {

    public static String PREFS = "com.airbitz.prefs";
    public static String LOGIN_NAME = "com.airbitz.login_name";
    private static String BITCOIN_MODE = "com.airbitz.application.bitcoinmode";
    private static String LOCATION_MODE = "com.airbitz.application.locationmode";
    private static String ARCHIVE_HEADER_STATE = "archiveClosed";
    public static final String WALLET_CHECK_PREF = "com.airbitz.walletcheck";

    private static Login airbitzLogin = new Login();
    private static long mBackgroundedTime = 0;
    private static long mLoginTime = 0;
    private static Context mContext;
    private static int mLastNavTab = 0;
    private static String mClientId;
    private static String mUserAgent;
    private static String mWalletUuid;
    private static Picasso mPicasso;
    private static Stack<Fragment>[] mFragmentStack = null;
    private static int mStackThreadId = -1;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

        Picasso.Builder builder = new Picasso.Builder(mContext);
        builder.addRequestHandler(new AirbitzRequestHandler(mContext));
        mPicasso = builder.build();
    }

    public static Picasso getPicasso() {
        return mPicasso;
    }

    public static boolean isLoggedIn() {
        return airbitzLogin.getUsername() != null;
    }

    public static boolean isDebugging() {
        ApplicationInfo appInfo = mContext.getApplicationInfo();
        return (appInfo != null && ((appInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0));
    }

    public static void Login(String uname, String password) {
        if (uname != null) {
            airbitzLogin.setUsername(uname);
            airbitzLogin.setPassword(password);
            CoreAPI.getApi().setCredentials(uname, password);
            mLoginTime = System.currentTimeMillis();
            SharedPreferences.Editor editor = mContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit();
            editor.putString(LOGIN_NAME, uname);
            editor.apply();
        }
    }

    public static void Logout() {
        setCurrentWallet(null);
        airbitzLogin = new Login();
        CoreAPI.getApi().setCredentials(null, null);
    }

    public static String getUsername() {
        return airbitzLogin.getUsername();
    }

    public static String getPassword() {
        return airbitzLogin.getPassword();
    }

    public static String getCurrentWallet() {
        return mWalletUuid;
    }

    public static void setCurrentWallet(String uuid) {
        mWalletUuid = uuid;
    }

    private static String CLIENT_ID_PREF = "client_id";
    public static String getClientID() {
        if (mClientId == null) {
            SharedPreferences prefs = AirbitzApplication.getContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            mClientId = prefs.getString(CLIENT_ID_PREF, null);
            if (mClientId == null) {
                mClientId = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(CLIENT_ID_PREF, mClientId);
                editor.apply();
            }
        }
        return mClientId;
    }

    public static String getUserAgent() {
        if (mUserAgent == null) {
            Context ctx = AirbitzApplication.getContext();
            PackageInfo pInfo = null;
            try {
                pInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            mUserAgent = System.getProperty("http.agent");
            if (pInfo != null) {
                mUserAgent = " AirBitz " + pInfo.versionName + "(" + pInfo.versionCode + ") " + mUserAgent;
            }
        }
        return mUserAgent;
    }

    public static Context getContext() {
        return mContext;
    }

    public static void setLastNavTab(int tab) {
        mLastNavTab = tab;
    }

    public static int getLastNavTab() {
        return mLastNavTab;
    }

    public static void setBackgroundedTime(long time) {
        mBackgroundedTime = time;
    }

    public static long getmBackgroundedTime() {
        return mBackgroundedTime;
    }

    public static boolean recentlyLoggedIn() {
        return System.currentTimeMillis() - mLoginTime <= 120000;
    }

    static Boolean sBitcoinMode = null;
    public static boolean getBitcoinSwitchMode() {
        if (sBitcoinMode == null) {
            SharedPreferences prefs = AirbitzApplication.getContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            sBitcoinMode = prefs.getBoolean(BITCOIN_MODE, true);
        }
        return sBitcoinMode;
    }

    public static void setBitcoinSwitchMode(final boolean state) {
        sBitcoinMode = state;
        new Thread(new Runnable() {
            public void run() {
                SharedPreferences.Editor editor = mContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit();
                editor.putBoolean(BITCOIN_MODE, state);
                editor.apply();
            }
        }).start();
    }

    static Boolean sArchiveHeader = null;
    public static boolean getArchivedMode() {
        if (sArchiveHeader == null) {
            SharedPreferences prefs = AirbitzApplication.getContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            sArchiveHeader = prefs.getBoolean(ARCHIVE_HEADER_STATE, false);
        }
        return sArchiveHeader;
    }

    public static void setArchivedMode(final boolean state) {
        sArchiveHeader = state;
        new Thread(new Runnable() {
            public void run() {
                SharedPreferences prefs = AirbitzApplication.getContext().getSharedPreferences(AirbitzApplication.PREFS, Context.MODE_PRIVATE);
                prefs.edit().putBoolean(ARCHIVE_HEADER_STATE, state).apply();
            }
        });
    }

    public static boolean getLocationWarn() {
        SharedPreferences prefs = AirbitzApplication.getContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        boolean state = prefs.getBoolean(LOCATION_MODE, true);
        return state;
    }

    public static void setLocationWarn(boolean state) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit();
        editor.putBoolean(LOCATION_MODE, state);
        editor.apply();
    }

    /*
     * Save or get the fragment stack is used when switching orientation
     */
    public static void setFragmentStack(Stack<Fragment>[] fragmentStack) {
        mFragmentStack = fragmentStack;
    }

    public static Stack<Fragment>[] getFragmentStack() {
        return mFragmentStack;
    }

    private static class Login {
        private String mUsername = null;
        private String mPassword = null;
        private String mWalletUuid = null;

        public String getUsername() {
            return mUsername;
        }

        public void setUsername(String name) {
            mUsername = name;
        }

        public String getPassword() {
            return mPassword;
        }

        public void setPassword(String password) {
            mPassword = password;
        }
    }
}
