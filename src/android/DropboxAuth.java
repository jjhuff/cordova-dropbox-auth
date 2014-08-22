package net.mspin.DropboxAuth;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.android.AuthActivity;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;

public class DropboxAuth extends CordovaPlugin {

    private static final String TAG = "DropboxAuth";

    private DropboxAPI<AndroidAuthSession> _DBApi;
    private CallbackContext _authCallbackContext;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        String appKey = "";
        try {
            Activity activity = this.cordova.getActivity();
            ApplicationInfo appliInfo = activity.getPackageManager().getApplicationInfo(activity.getPackageName(), PackageManager.GET_META_DATA);
            appKey = appliInfo.metaData.getString("net.mspin.DropboxAuth.DropboxKey");
        } catch (NameNotFoundException e) {}

        AppKeyPair appKeys = new AppKeyPair(appKey, "");
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        _DBApi = new DropboxAPI<AndroidAuthSession>(session);
    }

    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);

        AndroidAuthSession session = _DBApi.getSession();

        if (this._authCallbackContext != null && session.authenticationSuccessful()) {
            try {
                // Mandatory call to complete the auth
                session.finishAuthentication();

                // Grab the accessToken
                String accessToken = session.getOAuth2AccessToken();
                this._authCallbackContext.success(accessToken);
                this._authCallbackContext = null;
            } catch (IllegalStateException e) {
                Log.i(TAG, "Error authenticating", e);
                this._authCallbackContext.error(e.toString());
            }
        }
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        AndroidAuthSession session = _DBApi.getSession();
        if (action.equals("isAuthenticated")) {
            if (session.getOAuth2AccessToken() != null) {
                callbackContext.success();
            } else {
                callbackContext.error("User not authenticated yet");
            }
            return true;
        } else if (action.equals("auth")) {
            if (this._authCallbackContext != null) {
                this._authCallbackContext.error("Another auth was started");
            }
            this._authCallbackContext = callbackContext;
            session.startOAuth2Authentication(this.cordova.getActivity());
            return true;
        } else if (action.equals("unlink")) {
            session.unlink();
            callbackContext.success();
            return true;
        } else if (action.equals("getToken")) {
            String token = session.getOAuth2AccessToken();
            if (token != null) {
                callbackContext.success(token);
            } else {
                callbackContext.error("User not authenticated yet");
            }
            return true;
        }
        return false;
    }

}
