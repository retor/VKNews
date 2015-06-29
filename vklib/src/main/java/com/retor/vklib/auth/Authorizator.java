package com.retor.vklib.auth;

import android.app.Activity;
import android.preference.PreferenceManager;
import android.util.Log;

import com.retor.vklib.Const;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.api.VKError;
import com.vk.sdk.dialogs.VKCaptchaDialog;

/**
 * Created by retor on 22.06.2015.
 */
public class Authorizator implements IAuth {
    private Activity activity;
    private IAuthListener authListener;

    private VKSdkListener sdkListener = new VKSdkListener() {
        @Override
        public void onCaptchaError(VKError vkError) {
            new VKCaptchaDialog(vkError).show(activity);
        }

        @Override
        public void onTokenExpired(VKAccessToken vkAccessToken) {
            VKSdk.authorize(Const.SCOPES);
        }

        @Override
        public void onAccessDenied(VKError vkError) {
            authListener.onError(vkError.errorMessage);
        }

        @Override
        public void onReceiveNewToken(VKAccessToken newToken) {
            super.onReceiveNewToken(newToken);
            onSuccess(newToken);
        }

        @Override
        public void onAcceptUserToken(VKAccessToken token) {
            super.onAcceptUserToken(token);
            onSuccess(token);
        }

        @Override
        public void onRenewAccessToken(VKAccessToken token) {
            super.onRenewAccessToken(token);
            onSuccess(token);
        }
    };

    private void onSuccess(VKAccessToken token) {
        token.saveTokenToSharedPreferences(activity, Const.TOKEN);
        authListener.onLogin(Integer.parseInt(token.userId));
    }

    public Authorizator(Activity activity, IAuthListener authListener) {
        this.activity = activity;
        this.authListener = authListener;
    }

    @Override
    public void authorization() {
        if (VKSdk.getAccessToken() == null)
            if (VKAccessToken.tokenFromSharedPreferences(activity, Const.TOKEN) != null) {
                VKSdk.initialize(sdkListener, String.valueOf(Const.APP_ID), VKAccessToken.tokenFromSharedPreferences(activity, Const.TOKEN));
            } else {
                VKSdk.initialize(sdkListener, String.valueOf(Const.APP_ID));
                VKSdk.authorize(Const.SCOPES, true, false);
            }
    }

    @Override
    public void authWithLogin() {
        VKSdk.initialize(sdkListener, String.valueOf(Const.APP_ID));
        VKSdk.authorize(Const.SCOPES, true, true);
    }

    @Override
    public void logout() {
        VKSdk.logout();
        PreferenceManager.getDefaultSharedPreferences(activity).edit().remove(Const.TOKEN).commit();
        Log.d("Token", String.valueOf((VKSdk.getAccessToken() != null)));
    }
}
