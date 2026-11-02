package com.getcapacitor;

import android.net.Uri;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import androidx.core.app.NotificationCompat;
import androidx.webkit.JavaScriptReplyProxy;
import androidx.webkit.WebMessageCompat;
import androidx.webkit.WebViewCompat;
import androidx.webkit.WebViewFeature;

/* JADX INFO: loaded from: classes3.dex */
public class MessageHandler {
    private Bridge bridge;
    private org.apache.cordova.PluginManager cordovaPluginManager;
    private JavaScriptReplyProxy javaScriptReplyProxy;
    private WebView webView;

    public MessageHandler(Bridge bridge, WebView webView, org.apache.cordova.PluginManager cordovaPluginManager) {
        this.bridge = bridge;
        this.webView = webView;
        this.cordovaPluginManager = cordovaPluginManager;
        if (WebViewFeature.isFeatureSupported("WEB_MESSAGE_LISTENER") && !bridge.getConfig().isUsingLegacyBridge()) {
            WebViewCompat.WebMessageListener capListener = new WebViewCompat.WebMessageListener() { // from class: com.getcapacitor.MessageHandler$$ExternalSyntheticLambda2
                @Override // androidx.webkit.WebViewCompat.WebMessageListener
                public final void onPostMessage(WebView webView2, WebMessageCompat webMessageCompat, Uri uri, boolean z, JavaScriptReplyProxy javaScriptReplyProxy) {
                    this.f$0.lambda$new$0(webView2, webMessageCompat, uri, z, javaScriptReplyProxy);
                }
            };
            try {
                WebViewCompat.addWebMessageListener(webView, "androidBridge", bridge.getAllowedOriginRules(), capListener);
                return;
            } catch (Exception e) {
                webView.addJavascriptInterface(this, "androidBridge");
                return;
            }
        }
        webView.addJavascriptInterface(this, "androidBridge");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(WebView view, WebMessageCompat message, Uri sourceOrigin, boolean isMainFrame, JavaScriptReplyProxy replyProxy) {
        if (isMainFrame) {
            postMessage(message.getData());
            this.javaScriptReplyProxy = replyProxy;
        } else {
            Logger.warn("Plugin execution is allowed in Main Frame only");
        }
    }

    @JavascriptInterface
    public void postMessage(String jsonStr) {
        try {
            JSObject postData = new JSObject(jsonStr);
            String type = postData.getString("type");
            boolean typeIsNotNull = type != null;
            boolean isCordovaPlugin = typeIsNotNull && type.equals("cordova");
            boolean isJavaScriptError = typeIsNotNull && type.equals("js.error");
            String callbackId = postData.getString("callbackId");
            if (isCordovaPlugin) {
                String service = postData.getString(NotificationCompat.CATEGORY_SERVICE);
                String action = postData.getString("action");
                String actionArgs = postData.getString("actionArgs");
                Logger.verbose(Logger.tags("Plugin"), "To native (Cordova plugin): callbackId: " + callbackId + ", service: " + service + ", action: " + action + ", actionArgs: " + actionArgs);
                callCordovaPluginMethod(callbackId, service, action, actionArgs);
                return;
            }
            if (isJavaScriptError) {
                Logger.error("JavaScript Error: " + jsonStr);
                return;
            }
            String pluginId = postData.getString("pluginId");
            String methodName = postData.getString("methodName");
            JSObject methodData = postData.getJSObject("options", new JSObject());
            Logger.verbose(Logger.tags("Plugin"), "To native (Capacitor plugin): callbackId: " + callbackId + ", pluginId: " + pluginId + ", methodName: " + methodName);
            callPluginMethod(callbackId, pluginId, methodName, methodData);
        } catch (Exception ex) {
            Logger.error("Post message error:", ex);
        }
    }

    public void sendResponseMessage(PluginCall call, PluginResult successResult, PluginResult errorResult) {
        try {
            PluginResult data = new PluginResult();
            data.put("save", call.isKeptAlive());
            data.put("callbackId", call.getCallbackId());
            data.put("pluginId", call.getPluginId());
            data.put("methodName", call.getMethodName());
            boolean pluginResultInError = errorResult != null;
            if (pluginResultInError) {
                data.put("success", false);
                data.put("error", errorResult);
                Logger.debug("Sending plugin error: " + data.toString());
            } else {
                data.put("success", true);
                if (successResult != null) {
                    data.put("data", successResult);
                }
            }
            boolean isValidCallbackId = !call.getCallbackId().equals(PluginCall.CALLBACK_ID_DANGLING);
            if (isValidCallbackId) {
                if (!this.bridge.getConfig().isUsingLegacyBridge() && WebViewFeature.isFeatureSupported("WEB_MESSAGE_LISTENER") && this.javaScriptReplyProxy != null) {
                    this.javaScriptReplyProxy.postMessage(data.toString());
                } else {
                    legacySendResponseMessage(data);
                }
            } else {
                this.bridge.getApp().fireRestoredResult(data);
            }
        } catch (Exception ex) {
            Logger.error("sendResponseMessage: error: " + ex);
        }
        if (!call.isKeptAlive()) {
            call.release(this.bridge);
        }
    }

    private void legacySendResponseMessage(PluginResult data) {
        final String runScript = "window.Capacitor.fromNative(" + data.toString() + ")";
        final WebView webView = this.webView;
        webView.post(new Runnable() { // from class: com.getcapacitor.MessageHandler$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                webView.evaluateJavascript(runScript, null);
            }
        });
    }

    private void callPluginMethod(String callbackId, String pluginId, String methodName, JSObject methodData) {
        PluginCall call = new PluginCall(this, pluginId, callbackId, methodName, methodData);
        this.bridge.callPluginMethod(pluginId, methodName, call);
    }

    private void callCordovaPluginMethod(final String callbackId, final String service, final String action, final String actionArgs) {
        this.bridge.execute(new Runnable() { // from class: com.getcapacitor.MessageHandler$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$callCordovaPluginMethod$2(service, action, callbackId, actionArgs);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$callCordovaPluginMethod$2(String service, String action, String callbackId, String actionArgs) {
        this.cordovaPluginManager.exec(service, action, callbackId, actionArgs);
    }
}
