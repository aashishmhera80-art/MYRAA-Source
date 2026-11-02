package com.getcapacitor.plugin;

import android.content.SharedPreferences;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

/* JADX INFO: loaded from: classes4.dex */
@CapacitorPlugin
public class WebView extends Plugin {
    public static final String CAP_SERVER_PATH = "serverBasePath";
    public static final String WEBVIEW_PREFS_NAME = "CapWebViewSettings";

    @PluginMethod
    public void setServerAssetPath(PluginCall call) {
        String path = call.getString("path");
        this.bridge.setServerAssetPath(path);
        call.resolve();
    }

    @PluginMethod
    public void setServerBasePath(PluginCall call) {
        String path = call.getString("path");
        this.bridge.setServerBasePath(path);
        call.resolve();
    }

    @PluginMethod
    public void getServerBasePath(PluginCall call) {
        String path = this.bridge.getServerBasePath();
        JSObject ret = new JSObject();
        ret.put("path", path);
        call.resolve(ret);
    }

    @PluginMethod
    public void persistServerBasePath(PluginCall call) {
        String path = this.bridge.getServerBasePath();
        SharedPreferences prefs = getContext().getSharedPreferences(WEBVIEW_PREFS_NAME, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(CAP_SERVER_PATH, path);
        editor.apply();
        call.resolve();
    }
}
