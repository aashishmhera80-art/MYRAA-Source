package com.getcapacitor.cordova;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import java.util.List;
import java.util.Map;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPreferences;
import org.apache.cordova.CordovaResourceApi;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewEngine;
import org.apache.cordova.ICordovaCookieManager;
import org.apache.cordova.NativeToJsMessageQueue;
import org.apache.cordova.PluginEntry;
import org.apache.cordova.PluginManager;
import org.apache.cordova.PluginResult;

/* JADX INFO: loaded from: classes2.dex */
public class MockCordovaWebViewImpl implements CordovaWebView {
    private Context context;
    private CapacitorCordovaCookieManager cookieManager;
    private CordovaInterface cordova;
    private boolean hasPausedEver;
    private NativeToJsMessageQueue nativeToJsMessageQueue;
    private PluginManager pluginManager;
    private CordovaPreferences preferences;
    private CordovaResourceApi resourceApi;
    private WebView webView;

    public MockCordovaWebViewImpl(Context context) {
        this.context = context;
    }

    @Override // org.apache.cordova.CordovaWebView
    public void init(CordovaInterface cordova, List<PluginEntry> pluginEntries, CordovaPreferences preferences) {
        this.cordova = cordova;
        this.preferences = preferences;
        this.pluginManager = new PluginManager(this, this.cordova, pluginEntries);
        this.resourceApi = new CordovaResourceApi(this.context, this.pluginManager);
        this.pluginManager.init();
    }

    public void init(CordovaInterface cordova, List<PluginEntry> pluginEntries, CordovaPreferences preferences, WebView webView) {
        this.cordova = cordova;
        this.webView = webView;
        this.preferences = preferences;
        this.pluginManager = new PluginManager(this, this.cordova, pluginEntries);
        this.resourceApi = new CordovaResourceApi(this.context, this.pluginManager);
        this.nativeToJsMessageQueue = new NativeToJsMessageQueue();
        this.nativeToJsMessageQueue.addBridgeMode(new CapacitorEvalBridgeMode(webView, this.cordova));
        this.nativeToJsMessageQueue.setBridgeMode(0);
        this.cookieManager = new CapacitorCordovaCookieManager(webView);
        this.pluginManager.init();
    }

    public static class CapacitorEvalBridgeMode extends NativeToJsMessageQueue.BridgeMode {
        private final CordovaInterface cordova;
        private final WebView webView;

        public CapacitorEvalBridgeMode(WebView webView, CordovaInterface cordova) {
            this.webView = webView;
            this.cordova = cordova;
        }

        @Override // org.apache.cordova.NativeToJsMessageQueue.BridgeMode
        public void onNativeToJsMessageAvailable(final NativeToJsMessageQueue queue) {
            this.cordova.getActivity().runOnUiThread(new Runnable() { // from class: com.getcapacitor.cordova.MockCordovaWebViewImpl$CapacitorEvalBridgeMode$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onNativeToJsMessageAvailable$0(queue);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onNativeToJsMessageAvailable$0(NativeToJsMessageQueue queue) {
            String js = queue.popAndEncodeAsJs();
            if (js != null) {
                this.webView.evaluateJavascript(js, null);
            }
        }
    }

    @Override // org.apache.cordova.CordovaWebView
    public boolean isInitialized() {
        return this.cordova != null;
    }

    @Override // org.apache.cordova.CordovaWebView
    public View getView() {
        return this.webView;
    }

    @Override // org.apache.cordova.CordovaWebView
    public void loadUrlIntoView(String url, boolean recreatePlugins) {
        if (url.equals("about:blank") || url.startsWith("javascript:")) {
            this.webView.loadUrl(url);
        }
    }

    @Override // org.apache.cordova.CordovaWebView
    public void stopLoading() {
    }

    @Override // org.apache.cordova.CordovaWebView
    public boolean canGoBack() {
        return false;
    }

    @Override // org.apache.cordova.CordovaWebView
    public void clearCache() {
    }

    @Override // org.apache.cordova.CordovaWebView
    @Deprecated
    public void clearCache(boolean b) {
    }

    @Override // org.apache.cordova.CordovaWebView
    public void clearHistory() {
    }

    @Override // org.apache.cordova.CordovaWebView
    public boolean backHistory() {
        return false;
    }

    @Override // org.apache.cordova.CordovaWebView
    public void handlePause(boolean keepRunning) {
        if (!isInitialized()) {
            return;
        }
        this.hasPausedEver = true;
        this.pluginManager.onPause(keepRunning);
        triggerDocumentEvent("pause");
        if (!keepRunning) {
            setPaused(true);
        }
    }

    @Override // org.apache.cordova.CordovaWebView
    public void onNewIntent(Intent intent) {
        if (this.pluginManager != null) {
            this.pluginManager.onNewIntent(intent);
        }
    }

    @Override // org.apache.cordova.CordovaWebView
    public void handleResume(boolean keepRunning) {
        if (!isInitialized()) {
            return;
        }
        setPaused(false);
        this.pluginManager.onResume(keepRunning);
        if (this.hasPausedEver) {
            triggerDocumentEvent("resume");
        }
    }

    @Override // org.apache.cordova.CordovaWebView
    public void handleStart() {
        if (!isInitialized()) {
            return;
        }
        this.pluginManager.onStart();
    }

    @Override // org.apache.cordova.CordovaWebView
    public void handleStop() {
        if (!isInitialized()) {
            return;
        }
        this.pluginManager.onStop();
    }

    @Override // org.apache.cordova.CordovaWebView
    public void handleDestroy() {
        if (!isInitialized()) {
            return;
        }
        this.pluginManager.onDestroy();
    }

    @Override // org.apache.cordova.CordovaWebView
    @Deprecated
    public void sendJavascript(String statememt) {
        this.nativeToJsMessageQueue.addJavaScript(statememt);
    }

    public void eval(final String js, final ValueCallback<String> callback) {
        Handler mainHandler = new Handler(this.context.getMainLooper());
        mainHandler.post(new Runnable() { // from class: com.getcapacitor.cordova.MockCordovaWebViewImpl$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$eval$0(js, callback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$eval$0(String js, ValueCallback callback) {
        this.webView.evaluateJavascript(js, callback);
    }

    static /* synthetic */ void lambda$triggerDocumentEvent$1(String s) {
    }

    public void triggerDocumentEvent(String eventName) {
        eval("window.Capacitor.triggerEvent('" + eventName + "', 'document');", new ValueCallback() { // from class: com.getcapacitor.cordova.MockCordovaWebViewImpl$$ExternalSyntheticLambda0
            @Override // android.webkit.ValueCallback
            public final void onReceiveValue(Object obj) {
                MockCordovaWebViewImpl.lambda$triggerDocumentEvent$1((String) obj);
            }
        });
    }

    @Override // org.apache.cordova.CordovaWebView
    public void showWebPage(String url, boolean openExternal, boolean clearHistory, Map<String, Object> params) {
    }

    @Override // org.apache.cordova.CordovaWebView
    @Deprecated
    public boolean isCustomViewShowing() {
        return false;
    }

    @Override // org.apache.cordova.CordovaWebView
    @Deprecated
    public void showCustomView(View view, WebChromeClient.CustomViewCallback callback) {
    }

    @Override // org.apache.cordova.CordovaWebView
    @Deprecated
    public void hideCustomView() {
    }

    @Override // org.apache.cordova.CordovaWebView
    public CordovaResourceApi getResourceApi() {
        return this.resourceApi;
    }

    @Override // org.apache.cordova.CordovaWebView
    public void setButtonPlumbedToJs(int keyCode, boolean override) {
    }

    @Override // org.apache.cordova.CordovaWebView
    public boolean isButtonPlumbedToJs(int keyCode) {
        return false;
    }

    @Override // org.apache.cordova.CordovaWebView
    public void sendPluginResult(PluginResult cr, String callbackId) {
        this.nativeToJsMessageQueue.addPluginResult(cr, callbackId);
    }

    @Override // org.apache.cordova.CordovaWebView
    public PluginManager getPluginManager() {
        return this.pluginManager;
    }

    @Override // org.apache.cordova.CordovaWebView
    public CordovaWebViewEngine getEngine() {
        return null;
    }

    @Override // org.apache.cordova.CordovaWebView
    public CordovaPreferences getPreferences() {
        return this.preferences;
    }

    @Override // org.apache.cordova.CordovaWebView
    public ICordovaCookieManager getCookieManager() {
        return this.cookieManager;
    }

    @Override // org.apache.cordova.CordovaWebView
    public String getUrl() {
        return this.webView.getUrl();
    }

    @Override // org.apache.cordova.CordovaWebView
    public Context getContext() {
        return this.webView.getContext();
    }

    @Override // org.apache.cordova.CordovaWebView
    public void loadUrl(String url) {
        loadUrlIntoView(url, true);
    }

    @Override // org.apache.cordova.CordovaWebView
    public Object postMessage(String id, Object data) {
        return this.pluginManager.postMessage(id, data);
    }

    public void setPaused(boolean value) {
        if (value) {
            this.webView.onPause();
            this.webView.pauseTimers();
        } else {
            this.webView.onResume();
            this.webView.resumeTimers();
        }
    }
}
