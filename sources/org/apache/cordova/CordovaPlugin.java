package org.apache.cordova;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.WebView;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONException;

/* JADX INFO: loaded from: classes.dex */
public class CordovaPlugin {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public CordovaInterface cordova;
    protected CordovaPreferences preferences;
    private String serviceName;
    public CordovaWebView webView;

    public final void privateInitialize(String serviceName, CordovaInterface cordova, CordovaWebView webView, CordovaPreferences preferences) {
        if (this.cordova != null) {
            throw new AssertionError();
        }
        this.serviceName = serviceName;
        this.cordova = cordova;
        this.webView = webView;
        this.preferences = preferences;
        initialize(cordova, webView);
        pluginInitialize();
    }

    @Deprecated
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    }

    protected void pluginInitialize() {
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public boolean execute(String action, String rawArgs, CallbackContext callbackContext) throws JSONException {
        JSONArray args = new JSONArray(rawArgs);
        return execute(action, args, callbackContext);
    }

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        CordovaArgs cordovaArgs = new CordovaArgs(args);
        return execute(action, cordovaArgs, callbackContext);
    }

    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        return false;
    }

    public void onPause(boolean multitasking) {
    }

    public void onResume(boolean multitasking) {
    }

    public void onStart() {
    }

    public void onStop() {
    }

    public void onNewIntent(Intent intent) {
    }

    public void onDestroy() {
    }

    public Bundle onSaveInstanceState() {
        return null;
    }

    public void onRestoreStateForActivityResult(Bundle state, CallbackContext callbackContext) {
    }

    public Object onMessage(String id, Object data) {
        return null;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    }

    public Boolean shouldAllowRequest(String url) {
        return null;
    }

    public Boolean shouldAllowNavigation(String url) {
        return null;
    }

    public Boolean shouldAllowBridgeAccess(String url) {
        return shouldAllowNavigation(url);
    }

    public Boolean shouldOpenExternalUrl(String url) {
        return null;
    }

    public boolean onOverrideUrlLoading(String url) {
        return false;
    }

    public Uri remapUri(Uri uri) {
        return null;
    }

    public CordovaResourceApi.OpenForReadResult handleOpenForRead(Uri uri) throws IOException {
        throw new FileNotFoundException("Plugin can't handle uri: " + uri);
    }

    protected Uri toPluginUri(Uri origUri) {
        return new Uri.Builder().scheme(CordovaResourceApi.PLUGIN_URI_SCHEME).authority(this.serviceName).appendQueryParameter("origUri", origUri.toString()).build();
    }

    protected Uri fromPluginUri(Uri pluginUri) {
        return Uri.parse(pluginUri.getQueryParameter("origUri"));
    }

    public void onReset() {
    }

    public boolean onReceivedHttpAuthRequest(CordovaWebView view, ICordovaHttpAuthHandler handler, String host, String realm) {
        return false;
    }

    public boolean onReceivedClientCertRequest(CordovaWebView view, ICordovaClientCertRequest request) {
        return false;
    }

    public void onConfigurationChanged(Configuration newConfig) {
    }

    public void requestPermissions(int requestCode) {
    }

    public boolean hasPermisssion() {
        return true;
    }

    @Deprecated
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
    }

    public CordovaPluginPathHandler getPathHandler() {
        return null;
    }

    public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
        return false;
    }
}
