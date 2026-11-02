package org.apache.cordova.engine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import org.apache.cordova.CordovaBridge;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPreferences;
import org.apache.cordova.CordovaResourceApi;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewEngine;
import org.apache.cordova.ICordovaCookieManager;
import org.apache.cordova.LOG;
import org.apache.cordova.NativeToJsMessageQueue;
import org.apache.cordova.PluginManager;

/* JADX INFO: loaded from: classes.dex */
public class SystemWebViewEngine implements CordovaWebViewEngine {
    public static final String TAG = "SystemWebViewEngine";
    protected CordovaBridge bridge;
    protected CordovaWebViewEngine.Client client;
    protected final SystemCookieManager cookieManager;
    protected CordovaInterface cordova;
    protected NativeToJsMessageQueue nativeToJsMessageQueue;
    protected CordovaWebView parentWebView;
    protected PluginManager pluginManager;
    protected CordovaPreferences preferences;
    private BroadcastReceiver receiver;
    protected CordovaResourceApi resourceApi;
    protected final SystemWebView webView;

    public SystemWebViewEngine(Context context, CordovaPreferences preferences) {
        this(new SystemWebView(context), preferences);
    }

    public SystemWebViewEngine(SystemWebView webView) {
        this(webView, (CordovaPreferences) null);
    }

    public SystemWebViewEngine(SystemWebView webView, CordovaPreferences preferences) {
        this.preferences = preferences;
        this.webView = webView;
        this.cookieManager = new SystemCookieManager(webView);
    }

    @Override // org.apache.cordova.CordovaWebViewEngine
    public void init(CordovaWebView parentWebView, CordovaInterface cordova, CordovaWebViewEngine.Client client, CordovaResourceApi resourceApi, PluginManager pluginManager, NativeToJsMessageQueue nativeToJsMessageQueue) {
        if (this.cordova != null) {
            throw new IllegalStateException();
        }
        if (this.preferences == null) {
            this.preferences = parentWebView.getPreferences();
        }
        this.parentWebView = parentWebView;
        this.cordova = cordova;
        this.client = client;
        this.resourceApi = resourceApi;
        this.pluginManager = pluginManager;
        this.nativeToJsMessageQueue = nativeToJsMessageQueue;
        this.webView.init(this, cordova);
        initWebViewSettings();
        nativeToJsMessageQueue.addBridgeMode(new NativeToJsMessageQueue.OnlineEventsBridgeMode(new NativeToJsMessageQueue.OnlineEventsBridgeMode.OnlineEventsBridgeModeDelegate() { // from class: org.apache.cordova.engine.SystemWebViewEngine.1
            @Override // org.apache.cordova.NativeToJsMessageQueue.OnlineEventsBridgeMode.OnlineEventsBridgeModeDelegate
            public void setNetworkAvailable(boolean value) {
                if (SystemWebViewEngine.this.webView != null) {
                    SystemWebViewEngine.this.webView.setNetworkAvailable(value);
                }
            }

            @Override // org.apache.cordova.NativeToJsMessageQueue.OnlineEventsBridgeMode.OnlineEventsBridgeModeDelegate
            public void runOnUiThread(Runnable r) {
                SystemWebViewEngine.this.cordova.getActivity().runOnUiThread(r);
            }
        }));
        nativeToJsMessageQueue.addBridgeMode(new NativeToJsMessageQueue.EvalBridgeMode(this, cordova));
        this.bridge = new CordovaBridge(pluginManager, nativeToJsMessageQueue);
        exposeJsInterface(this.webView, this.bridge);
    }

    @Override // org.apache.cordova.CordovaWebViewEngine
    public CordovaWebView getCordovaWebView() {
        return this.parentWebView;
    }

    @Override // org.apache.cordova.CordovaWebViewEngine
    public ICordovaCookieManager getCookieManager() {
        return this.cookieManager;
    }

    @Override // org.apache.cordova.CordovaWebViewEngine
    public View getView() {
        return this.webView;
    }

    private void initWebViewSettings() {
        this.webView.setInitialScale(0);
        this.webView.setVerticalScrollBarEnabled(false);
        final WebSettings settings = this.webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        String manufacturer = Build.MANUFACTURER;
        LOG.d(TAG, "CordovaWebView is running on device made by: " + manufacturer);
        settings.setSaveFormData(false);
        if (this.preferences.getBoolean("AndroidInsecureFileModeEnabled", false)) {
            LOG.d(TAG, "Enabled insecure file access");
            settings.setAllowFileAccess(true);
            settings.setAllowUniversalAccessFromFileURLs(true);
            this.cookieManager.setAcceptFileSchemeCookies();
        }
        settings.setMediaPlaybackRequiresUserGesture(false);
        String databasePath = this.webView.getContext().getApplicationContext().getDir("database", 0).getPath();
        settings.setDatabaseEnabled(true);
        String inspectableWebview = this.preferences.getString("InspectableWebview", null);
        boolean shouldEnableInspector = false;
        if (inspectableWebview == null) {
            ApplicationInfo appInfo = this.webView.getContext().getApplicationContext().getApplicationInfo();
            shouldEnableInspector = (appInfo.flags & 2) != 0;
        } else if ("true".equals(inspectableWebview)) {
            shouldEnableInspector = true;
        }
        if (shouldEnableInspector) {
            enableRemoteDebugging();
        }
        settings.setGeolocationDatabasePath(databasePath);
        settings.setDomStorageEnabled(true);
        settings.setGeolocationEnabled(true);
        String defaultUserAgent = settings.getUserAgentString();
        String overrideUserAgent = this.preferences.getString("OverrideUserAgent", null);
        if (overrideUserAgent != null) {
            settings.setUserAgentString(overrideUserAgent);
        } else {
            String appendUserAgent = this.preferences.getString("AppendUserAgent", null);
            if (appendUserAgent != null) {
                settings.setUserAgentString(defaultUserAgent + " " + appendUserAgent);
            }
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.CONFIGURATION_CHANGED");
        if (this.receiver == null) {
            this.receiver = new BroadcastReceiver() { // from class: org.apache.cordova.engine.SystemWebViewEngine.2
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context, Intent intent) {
                    settings.getUserAgentString();
                }
            };
            this.webView.getContext().registerReceiver(this.receiver, intentFilter);
        }
    }

    private void enableRemoteDebugging() {
        try {
            WebView.setWebContentsDebuggingEnabled(true);
        } catch (IllegalArgumentException e) {
            LOG.d(TAG, "You have one job! To turn on Remote Web Debugging! YOU HAVE FAILED! ");
            e.printStackTrace();
        }
    }

    private static void exposeJsInterface(WebView webView, CordovaBridge bridge) {
        SystemExposedJsApi exposedJsApi = new SystemExposedJsApi(bridge);
        webView.addJavascriptInterface(exposedJsApi, "_cordovaNative");
    }

    @Override // org.apache.cordova.CordovaWebViewEngine
    public void loadUrl(String url, boolean clearNavigationStack) {
        this.webView.loadUrl(url);
    }

    @Override // org.apache.cordova.CordovaWebViewEngine
    public String getUrl() {
        return this.webView.getUrl();
    }

    @Override // org.apache.cordova.CordovaWebViewEngine
    public void stopLoading() {
        this.webView.stopLoading();
    }

    @Override // org.apache.cordova.CordovaWebViewEngine
    public void clearCache() {
        this.webView.clearCache(true);
    }

    @Override // org.apache.cordova.CordovaWebViewEngine
    public void clearHistory() {
        this.webView.clearHistory();
    }

    @Override // org.apache.cordova.CordovaWebViewEngine
    public boolean canGoBack() {
        return this.webView.canGoBack();
    }

    @Override // org.apache.cordova.CordovaWebViewEngine
    public boolean goBack() {
        if (this.webView.canGoBack()) {
            this.webView.goBack();
            return true;
        }
        return false;
    }

    @Override // org.apache.cordova.CordovaWebViewEngine
    public void setPaused(boolean value) {
        if (value) {
            this.webView.onPause();
            this.webView.pauseTimers();
        } else {
            this.webView.onResume();
            this.webView.resumeTimers();
        }
    }

    @Override // org.apache.cordova.CordovaWebViewEngine
    public void destroy() {
        this.webView.chromeClient.destroyLastDialog();
        this.webView.destroy();
        if (this.receiver != null) {
            try {
                this.webView.getContext().unregisterReceiver(this.receiver);
            } catch (Exception e) {
                LOG.e(TAG, "Error unregistering configuration receiver: " + e.getMessage(), e);
            }
        }
    }

    @Override // org.apache.cordova.CordovaWebViewEngine
    public void evaluateJavascript(String js, ValueCallback<String> callback) {
        this.webView.evaluateJavascript(js, callback);
    }
}
