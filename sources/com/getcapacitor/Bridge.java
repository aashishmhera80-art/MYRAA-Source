package com.getcapacitor;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.webkit.ServiceWorkerClient;
import android.webkit.ServiceWorkerController;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.pm.PackageInfoCompat;
import androidx.fragment.app.Fragment;
import androidx.webkit.WebViewCompat;
import androidx.webkit.WebViewFeature;
import com.getcapacitor.android.R;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.cordova.MockCordovaInterfaceImpl;
import com.getcapacitor.cordova.MockCordovaWebViewImpl;
import com.getcapacitor.plugin.CapacitorCookies;
import com.getcapacitor.plugin.CapacitorHttp;
import com.getcapacitor.plugin.SystemBars;
import com.getcapacitor.util.HostMask;
import com.getcapacitor.util.InternalUtils;
import com.getcapacitor.util.PermissionHelper;
import com.getcapacitor.util.WebColor;
import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.cordova.ConfigXmlParser;
import org.apache.cordova.CordovaPreferences;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginEntry;
import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes3.dex */
public class Bridge {
    private static final String BUNDLE_LAST_PLUGIN_CALL_METHOD_NAME_KEY = "capacitorLastActivityPluginMethod";
    private static final String BUNDLE_LAST_PLUGIN_ID_KEY = "capacitorLastActivityPluginId";
    private static final String BUNDLE_PLUGIN_CALL_BUNDLE_KEY = "capacitorLastPluginCallBundle";
    private static final String BUNDLE_PLUGIN_CALL_OPTIONS_SAVED_KEY = "capacitorLastPluginCallOptions";
    public static final String CAPACITOR_CONTENT_START = "/_capacitor_content_";
    public static final String CAPACITOR_FILE_START = "/_capacitor_file_";

    @Deprecated
    public static final String CAPACITOR_HTTPS_INTERCEPTOR_START = "/_capacitor_https_interceptor_";
    public static final String CAPACITOR_HTTPS_SCHEME = "https";
    public static final String CAPACITOR_HTTP_INTERCEPTOR_START = "/_capacitor_http_interceptor_";
    public static final String CAPACITOR_HTTP_INTERCEPTOR_URL_PARAM = "u";
    public static final String CAPACITOR_HTTP_SCHEME = "http";
    public static final int DEFAULT_ANDROID_WEBVIEW_VERSION = 60;
    public static final int DEFAULT_HUAWEI_WEBVIEW_VERSION = 10;
    public static final String DEFAULT_WEB_ASSET_DIR = "public";
    private static final String LAST_BINARY_VERSION_CODE = "lastBinaryVersionCode";
    private static final String LAST_BINARY_VERSION_NAME = "lastBinaryVersionName";
    private static final String MINIMUM_ANDROID_WEBVIEW_ERROR = "System WebView is not supported";
    public static final int MINIMUM_ANDROID_WEBVIEW_VERSION = 55;
    public static final int MINIMUM_HUAWEI_WEBVIEW_VERSION = 10;
    private static final String PERMISSION_PREFS_NAME = "PluginPermStates";
    private Set<String> allowedOriginRules;
    private App app;
    private HostMask appAllowNavigationMask;
    private String appUrl;
    private String appUrlConfig;
    private ArrayList<String> authorities;
    private Boolean canInjectJS;
    private CapConfig config;
    private final AppCompatActivity context;
    public final MockCordovaInterfaceImpl cordovaInterface;
    private CordovaWebView cordovaWebView;
    private final Fragment fragment;
    private final HandlerThread handlerThread;
    private final List<Class<? extends Plugin>> initialPlugins;
    private Uri intentUri;
    private WebViewLocalServer localServer;
    private String localUrl;
    private ArrayList<String> miscJSFileInjections;
    private final MessageHandler msgHandler;
    private PluginCall pluginCallForLastActivity;
    private final List<Plugin> pluginInstances;
    private Map<String, PluginHandle> plugins;
    private CordovaPreferences preferences;
    private RouteProcessor routeProcessor;
    private Map<String, PluginCall> savedCalls;
    private Map<String, LinkedList<String>> savedPermissionCallIds;
    private ServerPath serverPath;
    private Handler taskHandler;
    private final WebView webView;
    private BridgeWebViewClient webViewClient;
    private List<WebViewListener> webViewListeners;

    @Deprecated
    public Bridge(AppCompatActivity context, WebView webView, List<Class<? extends Plugin>> initialPlugins, MockCordovaInterfaceImpl cordovaInterface, org.apache.cordova.PluginManager pluginManager, CordovaPreferences preferences, CapConfig config) {
        this(context, null, null, webView, initialPlugins, new ArrayList(), cordovaInterface, pluginManager, preferences, config);
    }

    private Bridge(AppCompatActivity context, ServerPath serverPath, Fragment fragment, WebView webView, List<Class<? extends Plugin>> initialPlugins, List<Plugin> pluginInstances, MockCordovaInterfaceImpl cordovaInterface, org.apache.cordova.PluginManager pluginManager, CordovaPreferences preferences, CapConfig config) {
        this.allowedOriginRules = new HashSet();
        this.authorities = new ArrayList<>();
        this.miscJSFileInjections = new ArrayList<>();
        this.canInjectJS = true;
        this.handlerThread = new HandlerThread("CapacitorPlugins");
        this.taskHandler = null;
        this.plugins = new HashMap();
        this.savedCalls = new HashMap();
        this.savedPermissionCallIds = new HashMap();
        this.webViewListeners = new ArrayList();
        this.app = new App();
        this.serverPath = serverPath;
        this.context = context;
        this.fragment = fragment;
        this.webView = webView;
        this.webViewClient = new BridgeWebViewClient(this);
        this.initialPlugins = initialPlugins;
        this.pluginInstances = pluginInstances;
        this.cordovaInterface = cordovaInterface;
        this.preferences = preferences;
        this.handlerThread.start();
        this.taskHandler = new Handler(this.handlerThread.getLooper());
        this.config = config != null ? config : CapConfig.loadDefault(getActivity());
        Logger.init(this.config);
        initWebView();
        setAllowedOriginRules();
        this.msgHandler = new MessageHandler(this, webView, pluginManager);
        Intent intent = context.getIntent();
        this.intentUri = intent.getData();
        registerAllPlugins();
        loadWebView();
    }

    private void setAllowedOriginRules() {
        String[] appAllowNavigationConfig = this.config.getAllowNavigation();
        String authority = getHost();
        String scheme = getScheme();
        this.allowedOriginRules.add(scheme + "://" + authority);
        if (getServerUrl() != null) {
            this.allowedOriginRules.add(getServerUrl());
        }
        if (appAllowNavigationConfig != null) {
            for (String allowNavigation : appAllowNavigationConfig) {
                if (!allowNavigation.startsWith("http")) {
                    this.allowedOriginRules.add("https://" + allowNavigation);
                } else {
                    this.allowedOriginRules.add(allowNavigation);
                }
            }
            this.authorities.addAll(Arrays.asList(appAllowNavigationConfig));
        }
        this.appAllowNavigationMask = HostMask.Parser.parse(appAllowNavigationConfig);
    }

    public App getApp() {
        return this.app;
    }

    private void loadWebView() {
        JSInjector injector;
        boolean html5mode = this.config.isHTML5Mode();
        JSInjector injector2 = getJSInjector();
        if (WebViewFeature.isFeatureSupported(WebViewFeature.DOCUMENT_START_SCRIPT)) {
            String allowedOrigin = Uri.parse(this.appUrl).buildUpon().path(null).fragment(null).clearQuery().build().toString();
            try {
                WebViewCompat.addDocumentStartJavaScript(this.webView, injector2.getScriptString(), Collections.singleton(allowedOrigin));
                injector = null;
            } catch (IllegalArgumentException e) {
                Logger.warn("Invalid url, using fallback");
                injector = injector2;
            }
        } else {
            injector = injector2;
        }
        this.localServer = new WebViewLocalServer(this.context, this, injector, this.authorities, html5mode);
        this.localServer.hostAssets(DEFAULT_WEB_ASSET_DIR);
        Logger.debug("Loading app at " + this.appUrl);
        this.webView.setWebChromeClient(new BridgeWebChromeClient(this));
        this.webView.setWebViewClient(this.webViewClient);
        if (this.config.isResolveServiceWorkerRequests()) {
            ServiceWorkerController swController = ServiceWorkerController.getInstance();
            swController.setServiceWorkerClient(new ServiceWorkerClient() { // from class: com.getcapacitor.Bridge.1
                @Override // android.webkit.ServiceWorkerClient
                public WebResourceResponse shouldInterceptRequest(WebResourceRequest request) {
                    return Bridge.this.getLocalServer().shouldInterceptRequest(request);
                }
            });
        }
        if (!isDeployDisabled() && !isNewBinary()) {
            SharedPreferences prefs = getContext().getSharedPreferences(com.getcapacitor.plugin.WebView.WEBVIEW_PREFS_NAME, 0);
            String path = prefs.getString(com.getcapacitor.plugin.WebView.CAP_SERVER_PATH, null);
            if (path != null && !path.isEmpty() && new File(path).exists()) {
                setServerBasePath(path);
            }
        }
        if (!isMinimumWebViewInstalled()) {
            String errorUrl = getErrorUrl();
            if (errorUrl != null) {
                this.webView.loadUrl(errorUrl);
                return;
            }
            Logger.error(MINIMUM_ANDROID_WEBVIEW_ERROR);
        }
        if (this.serverPath != null) {
            if (this.serverPath.getType() == ServerPath.PathType.ASSET_PATH) {
                setServerAssetPath(this.serverPath.getPath());
                return;
            } else {
                setServerBasePath(this.serverPath.getPath());
                return;
            }
        }
        this.webView.loadUrl(this.appUrl);
    }

    public boolean isMinimumWebViewInstalled() {
        PackageManager pm = getContext().getPackageManager();
        if (Build.VERSION.SDK_INT >= 26) {
            PackageInfo info = WebView.getCurrentWebViewPackage();
            Pattern pattern = Pattern.compile("(\\d+)");
            Matcher matcher = pattern.matcher(info.versionName);
            if (!matcher.find()) {
                return false;
            }
            String majorVersionStr = matcher.group(0);
            int majorVersion = Integer.parseInt(majorVersionStr);
            if (info.packageName.equals("com.huawei.webview")) {
                return majorVersion >= this.config.getMinHuaweiWebViewVersion();
            }
            return majorVersion >= this.config.getMinWebViewVersion();
        }
        try {
            PackageInfo info2 = InternalUtils.getPackageInfo(pm, "com.android.chrome");
            String majorVersionStr2 = info2.versionName.split("\\.")[0];
            int majorVersion2 = Integer.parseInt(majorVersionStr2);
            return majorVersion2 >= this.config.getMinWebViewVersion();
        } catch (Exception ex) {
            Logger.warn("Unable to get package info for 'com.google.android.webview'" + ex.toString());
            try {
                PackageInfo info3 = InternalUtils.getPackageInfo(pm, "com.android.webview");
                String majorVersionStr3 = info3.versionName.split("\\.")[0];
                int majorVersion3 = Integer.parseInt(majorVersionStr3);
                return majorVersion3 >= this.config.getMinWebViewVersion();
            } catch (Exception ex2) {
                Logger.warn("Unable to get package info for 'com.android.webview'" + ex2.toString());
                int amazonFireMajorWebViewVersion = extractWebViewMajorVersion(pm, "com.amazon.webview.chromium");
                return amazonFireMajorWebViewVersion >= this.config.getMinWebViewVersion();
            }
        }
    }

    private int extractWebViewMajorVersion(PackageManager pm, String webViewPackageName) {
        try {
            PackageInfo info = InternalUtils.getPackageInfo(pm, webViewPackageName);
            String majorVersionStr = info.versionName.split("\\.")[0];
            int majorVersion = Integer.parseInt(majorVersionStr);
            return majorVersion;
        } catch (Exception ex) {
            Logger.warn(String.format("Unable to get package info for '%s' with err '%s'", webViewPackageName, ex));
            return 0;
        }
    }

    public boolean launchIntent(Uri url) {
        Boolean shouldOverrideLoad;
        for (Map.Entry<String, PluginHandle> entry : this.plugins.entrySet()) {
            Plugin plugin = entry.getValue().getInstance();
            if (plugin != null && (shouldOverrideLoad = plugin.shouldOverrideLoad(url)) != null) {
                return shouldOverrideLoad.booleanValue();
            }
        }
        if (url.getScheme().equals("data") || url.getScheme().equals("blob")) {
            return false;
        }
        Uri appUri = Uri.parse(this.appUrl);
        if ((appUri.getHost().equals(url.getHost()) && url.getScheme().equals(appUri.getScheme())) || this.appAllowNavigationMask.matches(url.getHost())) {
            return false;
        }
        try {
            Intent openIntent = new Intent("android.intent.action.VIEW", url);
            getContext().startActivity(openIntent);
            return true;
        } catch (ActivityNotFoundException e) {
            return true;
        }
    }

    private boolean isNewBinary() {
        String versionCode = "";
        String versionName = "";
        SharedPreferences prefs = getContext().getSharedPreferences(com.getcapacitor.plugin.WebView.WEBVIEW_PREFS_NAME, 0);
        String lastVersionCode = prefs.getString(LAST_BINARY_VERSION_CODE, null);
        String lastVersionName = prefs.getString(LAST_BINARY_VERSION_NAME, null);
        try {
            PackageManager pm = getContext().getPackageManager();
            PackageInfo pInfo = InternalUtils.getPackageInfo(pm, getContext().getPackageName());
            versionCode = Integer.toString((int) PackageInfoCompat.getLongVersionCode(pInfo));
            versionName = pInfo.versionName != null ? pInfo.versionName : "";
        } catch (Exception ex) {
            Logger.error("Unable to get package info", ex);
        }
        if (versionCode.equals(lastVersionCode) && versionName.equals(lastVersionName)) {
            return false;
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(LAST_BINARY_VERSION_CODE, versionCode);
        editor.putString(LAST_BINARY_VERSION_NAME, versionName);
        editor.putString(com.getcapacitor.plugin.WebView.CAP_SERVER_PATH, "");
        editor.apply();
        return true;
    }

    public boolean isDeployDisabled() {
        return this.preferences.getBoolean("DisableDeploy", false);
    }

    public boolean shouldKeepRunning() {
        return this.preferences.getBoolean("KeepRunning", true);
    }

    public void handleAppUrlLoadError(Exception ex) {
        if (ex instanceof SocketTimeoutException) {
            Logger.error("Unable to load app. Ensure the server is running at " + this.appUrl + ", or modify the appUrl setting in capacitor.config.json (make sure to npx cap copy after to commit changes).", ex);
        }
    }

    public boolean isDevMode() {
        return (getActivity().getApplicationInfo().flags & 2) != 0;
    }

    protected void setCordovaWebView(CordovaWebView cordovaWebView) {
        this.cordovaWebView = cordovaWebView;
    }

    public Context getContext() {
        return this.context;
    }

    public AppCompatActivity getActivity() {
        return this.context;
    }

    public Fragment getFragment() {
        return this.fragment;
    }

    public WebView getWebView() {
        return this.webView;
    }

    public Uri getIntentUri() {
        return this.intentUri;
    }

    public String getScheme() {
        return this.config.getAndroidScheme();
    }

    public String getHost() {
        return this.config.getHostname();
    }

    public String getServerUrl() {
        return this.config.getServerUrl();
    }

    public String getErrorUrl() {
        String errorPath = this.config.getErrorPath();
        if (errorPath != null && !errorPath.trim().isEmpty()) {
            String authority = getHost();
            String scheme = getScheme();
            String localUrl = scheme + "://" + authority;
            return localUrl + "/" + errorPath;
        }
        return null;
    }

    public String getAppUrl() {
        return this.appUrl;
    }

    public CapConfig getConfig() {
        return this.config;
    }

    public void reset() {
        this.savedCalls = new HashMap();
        for (PluginHandle handle : this.plugins.values()) {
            handle.getInstance().removeAllListeners();
        }
    }

    private void initWebView() {
        WebSettings settings = this.webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setGeolocationEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        if (this.config.isMixedContentAllowed()) {
            settings.setMixedContentMode(0);
        }
        String appendUserAgent = this.config.getAppendedUserAgentString();
        if (appendUserAgent != null) {
            String defaultUserAgent = settings.getUserAgentString();
            settings.setUserAgentString(defaultUserAgent + " " + appendUserAgent);
        }
        String overrideUserAgent = this.config.getOverriddenUserAgentString();
        if (overrideUserAgent != null) {
            settings.setUserAgentString(overrideUserAgent);
        }
        String backgroundColor = this.config.getBackgroundColor();
        if (backgroundColor != null) {
            try {
                this.webView.setBackgroundColor(WebColor.parseColor(backgroundColor));
            } catch (IllegalArgumentException e) {
                Logger.debug("WebView background color not applied");
            }
        }
        settings.setDisplayZoomControls(false);
        settings.setBuiltInZoomControls(this.config.isZoomableWebView());
        if (this.config.isInitialFocus()) {
            this.webView.requestFocusFromTouch();
        }
        WebView.setWebContentsDebuggingEnabled(this.config.isWebContentsDebuggingEnabled());
        this.appUrlConfig = getServerUrl();
        String authority = getHost();
        this.authorities.add(authority);
        String scheme = getScheme();
        this.localUrl = scheme + "://" + authority;
        if (this.appUrlConfig != null) {
            try {
                URL appUrlObject = new URL(this.appUrlConfig);
                this.authorities.add(appUrlObject.getAuthority());
                this.localUrl = appUrlObject.getProtocol() + "://" + appUrlObject.getAuthority();
                this.appUrl = this.appUrlConfig;
            } catch (Exception ex) {
                Logger.error("Provided server url is invalid: " + ex.getMessage());
                return;
            }
        } else {
            this.appUrl = this.localUrl;
            if (!scheme.equals("http") && !scheme.equals("https")) {
                this.appUrl += "/";
            }
        }
        String appUrlPath = this.config.getStartPath();
        if (appUrlPath != null && !appUrlPath.trim().isEmpty()) {
            this.appUrl += appUrlPath;
        }
    }

    private void registerAllPlugins() {
        registerPlugin(CapacitorCookies.class);
        registerPlugin(com.getcapacitor.plugin.WebView.class);
        registerPlugin(CapacitorHttp.class);
        registerPlugin(SystemBars.class);
        for (Class<? extends Plugin> pluginClass : this.initialPlugins) {
            registerPlugin(pluginClass);
        }
        for (Plugin plugin : this.pluginInstances) {
            registerPluginInstance(plugin);
        }
    }

    public void registerPlugins(Class<? extends Plugin>[] pluginClasses) {
        for (Class<? extends Plugin> plugin : pluginClasses) {
            registerPlugin(plugin);
        }
    }

    public void registerPluginInstances(Plugin[] pluginInstances) {
        for (Plugin plugin : pluginInstances) {
            registerPluginInstance(plugin);
        }
    }

    private String getLegacyPluginName(Class<? extends Plugin> pluginClass) {
        NativePlugin legacyPluginAnnotation = (NativePlugin) pluginClass.getAnnotation(NativePlugin.class);
        if (legacyPluginAnnotation == null) {
            Logger.error("Plugin doesn't have the @CapacitorPlugin annotation. Please add it");
            return null;
        }
        return legacyPluginAnnotation.name();
    }

    public void registerPlugin(Class<? extends Plugin> pluginClass) {
        String pluginId = pluginId(pluginClass);
        if (pluginId == null) {
            return;
        }
        try {
            this.plugins.put(pluginId, new PluginHandle(this, pluginClass));
        } catch (InvalidPluginException e) {
            logInvalidPluginException(pluginClass);
        } catch (PluginLoadException ex) {
            logPluginLoadException(pluginClass, ex);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void registerPluginInstance(Plugin plugin) {
        Class<?> cls = plugin.getClass();
        String pluginId = pluginId(cls);
        if (pluginId == null) {
            return;
        }
        try {
            this.plugins.put(pluginId, new PluginHandle(this, plugin));
        } catch (InvalidPluginException e) {
            logInvalidPluginException(cls);
        }
    }

    private String pluginId(Class<? extends Plugin> clazz) {
        String pluginName = pluginName(clazz);
        String pluginId = clazz.getSimpleName();
        if (pluginName == null) {
            return null;
        }
        if (!pluginName.equals("")) {
            pluginId = pluginName;
        }
        Logger.debug("Registering plugin instance: " + pluginId);
        return pluginId;
    }

    private String pluginName(Class<? extends Plugin> clazz) {
        CapacitorPlugin pluginAnnotation = (CapacitorPlugin) clazz.getAnnotation(CapacitorPlugin.class);
        if (pluginAnnotation == null) {
            String pluginName = getLegacyPluginName(clazz);
            return pluginName;
        }
        String pluginName2 = pluginAnnotation.name();
        return pluginName2;
    }

    private void logInvalidPluginException(Class<? extends Plugin> clazz) {
        Logger.error("NativePlugin " + clazz.getName() + " is invalid. Ensure the @CapacitorPlugin annotation exists on the plugin class and the class extends Plugin");
    }

    private void logPluginLoadException(Class<? extends Plugin> clazz, Exception ex) {
        Logger.error("NativePlugin " + clazz.getName() + " failed to load", ex);
    }

    public PluginHandle getPlugin(String pluginId) {
        return this.plugins.get(pluginId);
    }

    @Deprecated
    public PluginHandle getPluginWithRequestCode(int requestCode) {
        for (PluginHandle handle : this.plugins.values()) {
            CapacitorPlugin pluginAnnotation = handle.getPluginAnnotation();
            int i = 0;
            if (pluginAnnotation == null) {
                NativePlugin legacyPluginAnnotation = handle.getLegacyPluginAnnotation();
                if (legacyPluginAnnotation == null) {
                    continue;
                } else {
                    if (legacyPluginAnnotation.permissionRequestCode() == requestCode) {
                        return handle;
                    }
                    int[] requestCodes = legacyPluginAnnotation.requestCodes();
                    int length = requestCodes.length;
                    while (i < length) {
                        int rc = requestCodes[i];
                        if (rc != requestCode) {
                            i++;
                        } else {
                            return handle;
                        }
                    }
                }
            } else {
                int[] requestCodes2 = pluginAnnotation.requestCodes();
                int length2 = requestCodes2.length;
                while (i < length2) {
                    int rc2 = requestCodes2[i];
                    if (rc2 != requestCode) {
                        i++;
                    } else {
                        return handle;
                    }
                }
            }
        }
        return null;
    }

    public void callPluginMethod(String pluginId, final String methodName, final PluginCall call) {
        try {
            final PluginHandle plugin = getPlugin(pluginId);
            if (plugin == null) {
                Logger.error("unable to find plugin : " + pluginId);
                call.errorCallback("unable to find plugin : " + pluginId);
            } else {
                if (Logger.shouldLog()) {
                    Logger.verbose("callback: " + call.getCallbackId() + ", pluginId: " + plugin.getId() + ", methodName: " + methodName + ", methodData: " + call.getData().toString());
                }
                Runnable currentThreadTask = new Runnable() { // from class: com.getcapacitor.Bridge$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$callPluginMethod$0(plugin, methodName, call);
                    }
                };
                this.taskHandler.post(currentThreadTask);
            }
        } catch (Exception ex) {
            Logger.error(Logger.tags("callPluginMethod"), "error : " + ex, null);
            call.errorCallback(ex.toString());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$callPluginMethod$0(PluginHandle plugin, String methodName, PluginCall call) {
        try {
            plugin.invoke(methodName, call);
            if (call.isKeptAlive()) {
                saveCall(call);
            }
        } catch (InvalidPluginMethodException e) {
            ex = e;
            Logger.error("Unable to execute plugin method", ex);
        } catch (PluginLoadException e2) {
            ex = e2;
            Logger.error("Unable to execute plugin method", ex);
        } catch (Exception ex) {
            Logger.error("Serious error executing plugin", ex);
            throw new RuntimeException(ex);
        }
    }

    public void eval(final String js, final ValueCallback<String> callback) {
        Handler mainHandler = new Handler(this.context.getMainLooper());
        mainHandler.post(new Runnable() { // from class: com.getcapacitor.Bridge$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$eval$1(js, callback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$eval$1(String js, ValueCallback callback) {
        this.webView.evaluateJavascript(js, callback);
    }

    public void logToJs(String message, String level) {
        eval("window.Capacitor.logJs(\"" + message + "\", \"" + level + "\")", null);
    }

    public void logToJs(String message) {
        logToJs(message, "log");
    }

    static /* synthetic */ void lambda$triggerJSEvent$2(String s) {
    }

    public void triggerJSEvent(String eventName, String target) {
        eval("window.Capacitor.triggerEvent(\"" + eventName + "\", \"" + target + "\")", new ValueCallback() { // from class: com.getcapacitor.Bridge$$ExternalSyntheticLambda2
            @Override // android.webkit.ValueCallback
            public final void onReceiveValue(Object obj) {
                Bridge.lambda$triggerJSEvent$2((String) obj);
            }
        });
    }

    static /* synthetic */ void lambda$triggerJSEvent$3(String s) {
    }

    public void triggerJSEvent(String eventName, String target, String data) {
        eval("window.Capacitor.triggerEvent(\"" + eventName + "\", \"" + target + "\", " + data + ")", new ValueCallback() { // from class: com.getcapacitor.Bridge$$ExternalSyntheticLambda4
            @Override // android.webkit.ValueCallback
            public final void onReceiveValue(Object obj) {
                Bridge.lambda$triggerJSEvent$3((String) obj);
            }
        });
    }

    public void triggerWindowJSEvent(String eventName) {
        triggerJSEvent(eventName, "window");
    }

    public void triggerWindowJSEvent(String eventName, String data) {
        triggerJSEvent(eventName, "window", data);
    }

    public void triggerDocumentJSEvent(String eventName) {
        triggerJSEvent(eventName, "document");
    }

    public void triggerDocumentJSEvent(String eventName, String data) {
        triggerJSEvent(eventName, "document", data);
    }

    public void execute(Runnable runnable) {
        this.taskHandler.post(runnable);
    }

    public void executeOnMainThread(Runnable runnable) {
        Handler mainHandler = new Handler(this.context.getMainLooper());
        mainHandler.post(runnable);
    }

    public void saveCall(PluginCall call) {
        this.savedCalls.put(call.getCallbackId(), call);
    }

    public PluginCall getSavedCall(String callbackId) {
        if (callbackId == null) {
            return null;
        }
        return this.savedCalls.get(callbackId);
    }

    PluginCall getPluginCallForLastActivity() {
        PluginCall pluginCallForLastActivity = this.pluginCallForLastActivity;
        this.pluginCallForLastActivity = null;
        return pluginCallForLastActivity;
    }

    void setPluginCallForLastActivity(PluginCall pluginCallForLastActivity) {
        this.pluginCallForLastActivity = pluginCallForLastActivity;
    }

    public void releaseCall(PluginCall call) {
        releaseCall(call.getCallbackId());
    }

    public void releaseCall(String callbackId) {
        this.savedCalls.remove(callbackId);
    }

    protected PluginCall getPermissionCall(String pluginId) {
        LinkedList<String> permissionCallIds = this.savedPermissionCallIds.get(pluginId);
        String savedCallId = null;
        if (permissionCallIds != null) {
            String savedCallId2 = permissionCallIds.poll();
            savedCallId = savedCallId2;
        }
        return getSavedCall(savedCallId);
    }

    protected void savePermissionCall(PluginCall call) {
        if (call != null) {
            if (!this.savedPermissionCallIds.containsKey(call.getPluginId())) {
                this.savedPermissionCallIds.put(call.getPluginId(), new LinkedList<>());
            }
            this.savedPermissionCallIds.get(call.getPluginId()).add(call.getCallbackId());
            saveCall(call);
        }
    }

    public <I, O> ActivityResultLauncher<I> registerForActivityResult(ActivityResultContract<I, O> contract, ActivityResultCallback<O> callback) {
        if (this.fragment != null) {
            return this.fragment.registerForActivityResult(contract, callback);
        }
        return this.context.registerForActivityResult(contract, callback);
    }

    private JSInjector getJSInjector() {
        try {
            String globalJS = JSExport.getGlobalJS(this.context, this.config.isLoggingEnabled(), isDevMode());
            String bridgeJS = JSExport.getBridgeJS(this.context);
            String pluginJS = JSExport.getPluginJS(this.plugins.values());
            String cordovaJS = JSExport.getCordovaJS(this.context);
            String cordovaPluginsJS = JSExport.getCordovaPluginJS(this.context);
            String cordovaPluginsFileJS = JSExport.getCordovaPluginsFileJS(this.context);
            String localUrlJS = "window.WEBVIEW_SERVER_URL = '" + this.localUrl + "';";
            String miscJS = JSExport.getMiscFileJS(this.miscJSFileInjections, this.context);
            this.miscJSFileInjections = new ArrayList<>();
            this.canInjectJS = false;
            return new JSInjector(globalJS, bridgeJS, pluginJS, cordovaJS, cordovaPluginsJS, cordovaPluginsFileJS, localUrlJS, miscJS);
        } catch (Exception ex) {
            Logger.error("Unable to export Capacitor JS. App will not function!", ex);
            return null;
        }
    }

    public void injectScriptBeforeLoad(String path) {
        if (this.canInjectJS.booleanValue()) {
            this.miscJSFileInjections.add(path);
        }
    }

    public void restoreInstanceState(Bundle savedInstanceState) {
        String lastPluginId = savedInstanceState.getString(BUNDLE_LAST_PLUGIN_ID_KEY);
        String lastPluginCallMethod = savedInstanceState.getString(BUNDLE_LAST_PLUGIN_CALL_METHOD_NAME_KEY);
        String lastOptionsJson = savedInstanceState.getString(BUNDLE_PLUGIN_CALL_OPTIONS_SAVED_KEY);
        if (lastPluginId != null) {
            if (lastOptionsJson != null) {
                try {
                    JSObject options = new JSObject(lastOptionsJson);
                    this.pluginCallForLastActivity = new PluginCall(this.msgHandler, lastPluginId, PluginCall.CALLBACK_ID_DANGLING, lastPluginCallMethod, options);
                } catch (JSONException ex) {
                    Logger.error("Unable to restore plugin call, unable to parse persisted JSON object", ex);
                }
            }
            Bundle bundleData = savedInstanceState.getBundle(BUNDLE_PLUGIN_CALL_BUNDLE_KEY);
            PluginHandle lastPlugin = getPlugin(lastPluginId);
            if (bundleData != null && lastPlugin != null) {
                lastPlugin.getInstance().restoreState(bundleData);
            } else {
                Logger.error("Unable to restore last plugin call");
            }
        }
    }

    public void saveInstanceState(Bundle outState) {
        Logger.debug("Saving instance state!");
        if (this.pluginCallForLastActivity != null) {
            PluginCall call = this.pluginCallForLastActivity;
            PluginHandle handle = getPlugin(call.getPluginId());
            if (handle != null) {
                Bundle bundle = handle.getInstance().saveInstanceState();
                if (bundle != null) {
                    outState.putString(BUNDLE_LAST_PLUGIN_ID_KEY, call.getPluginId());
                    outState.putString(BUNDLE_LAST_PLUGIN_CALL_METHOD_NAME_KEY, call.getMethodName());
                    outState.putString(BUNDLE_PLUGIN_CALL_OPTIONS_SAVED_KEY, call.getData().toString());
                    outState.putBundle(BUNDLE_PLUGIN_CALL_BUNDLE_KEY, bundle);
                    return;
                }
                Logger.error("Couldn't save last " + call.getPluginId() + "'s Plugin " + call.getMethodName() + " call");
            }
        }
    }

    @Deprecated
    public void startActivityForPluginWithResult(PluginCall call, Intent intent, int requestCode) {
        Logger.debug("Starting activity for result");
        this.pluginCallForLastActivity = call;
        getActivity().startActivityForResult(intent, requestCode);
    }

    boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PluginHandle plugin = getPluginWithRequestCode(requestCode);
        if (plugin == null) {
            Logger.debug("Unable to find a Capacitor plugin to handle permission requestCode, trying Cordova plugins " + requestCode);
            try {
                boolean permissionHandled = this.cordovaInterface.handlePermissionResult(requestCode, permissions, grantResults);
                return permissionHandled;
            } catch (JSONException e) {
                Logger.debug("Error on Cordova plugin permissions request " + e.getMessage());
                return false;
            }
        }
        if (plugin.getPluginAnnotation() == null) {
            plugin.getInstance().handleRequestPermissionsResult(requestCode, permissions, grantResults);
            return true;
        }
        return false;
    }

    protected boolean validatePermissions(Plugin plugin, PluginCall savedCall, Map<String, Boolean> permissions) {
        SharedPreferences prefs = getContext().getSharedPreferences(PERMISSION_PREFS_NAME, 0);
        for (Map.Entry<String, Boolean> permission : permissions.entrySet()) {
            String permString = permission.getKey();
            boolean isGranted = permission.getValue().booleanValue();
            if (isGranted) {
                String state = prefs.getString(permString, null);
                if (state != null) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.remove(permString);
                    editor.apply();
                }
            } else {
                SharedPreferences.Editor editor2 = prefs.edit();
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permString)) {
                    editor2.putString(permString, PermissionState.PROMPT_WITH_RATIONALE.toString());
                } else {
                    editor2.putString(permString, PermissionState.DENIED.toString());
                }
                editor2.apply();
            }
        }
        String[] permStrings = (String[]) permissions.keySet().toArray(new String[0]);
        if (!PermissionHelper.hasDefinedPermissions(getContext(), permStrings)) {
            StringBuilder builder = new StringBuilder();
            builder.append("Missing the following permissions in AndroidManifest.xml:\n");
            String[] missing = PermissionHelper.getUndefinedPermissions(getContext(), permStrings);
            for (String perm : missing) {
                builder.append(perm + "\n");
            }
            savedCall.reject(builder.toString());
            return false;
        }
        return true;
    }

    protected Map<String, PermissionState> getPermissionStates(Plugin plugin) {
        PermissionState permissionStatus;
        Map<String, PermissionState> permissionsResults = new HashMap<>();
        CapacitorPlugin annotation = plugin.getPluginHandle().getPluginAnnotation();
        for (Permission perm : annotation.permissions()) {
            if (perm.strings().length == 0 || (perm.strings().length == 1 && perm.strings()[0].isEmpty())) {
                String key = perm.alias();
                if (!key.isEmpty() && permissionsResults.get(key) == null) {
                    permissionsResults.put(key, PermissionState.GRANTED);
                }
            } else {
                for (String permString : perm.strings()) {
                    String key2 = perm.alias().isEmpty() ? permString : perm.alias();
                    if (ActivityCompat.checkSelfPermission(getContext(), permString) == 0) {
                        permissionStatus = PermissionState.GRANTED;
                    } else {
                        permissionStatus = PermissionState.PROMPT;
                        SharedPreferences prefs = getContext().getSharedPreferences(PERMISSION_PREFS_NAME, 0);
                        String state = prefs.getString(permString, null);
                        if (state != null) {
                            permissionStatus = PermissionState.byState(state);
                        }
                    }
                    PermissionState existingResult = permissionsResults.get(key2);
                    if (existingResult == null || existingResult == PermissionState.GRANTED) {
                        permissionsResults.put(key2, permissionStatus);
                    }
                }
            }
        }
        return permissionsResults;
    }

    boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        PluginHandle plugin = getPluginWithRequestCode(requestCode);
        if (plugin == null || plugin.getInstance() == null) {
            Logger.debug("Unable to find a Capacitor plugin to handle requestCode, trying Cordova plugins " + requestCode);
            return this.cordovaInterface.onActivityResult(requestCode, resultCode, data);
        }
        PluginCall lastCall = plugin.getInstance().getSavedCall();
        if (lastCall == null && this.pluginCallForLastActivity != null) {
            plugin.getInstance().saveCall(this.pluginCallForLastActivity);
        }
        plugin.getInstance().handleOnActivityResult(requestCode, resultCode, data);
        this.pluginCallForLastActivity = null;
        return true;
    }

    public void onNewIntent(Intent intent) {
        for (PluginHandle plugin : this.plugins.values()) {
            plugin.getInstance().handleOnNewIntent(intent);
        }
        if (this.cordovaWebView != null) {
            this.cordovaWebView.onNewIntent(intent);
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        for (PluginHandle plugin : this.plugins.values()) {
            plugin.getInstance().handleOnConfigurationChanged(newConfig);
        }
    }

    public void onRestart() {
        for (PluginHandle plugin : this.plugins.values()) {
            plugin.getInstance().handleOnRestart();
        }
    }

    public void onStart() {
        for (PluginHandle plugin : this.plugins.values()) {
            plugin.getInstance().handleOnStart();
        }
        if (this.cordovaWebView != null) {
            this.cordovaWebView.handleStart();
        }
    }

    public void onResume() {
        for (PluginHandle plugin : this.plugins.values()) {
            plugin.getInstance().handleOnResume();
        }
        if (this.cordovaWebView != null) {
            this.cordovaWebView.handleResume(shouldKeepRunning());
        }
    }

    public void onPause() {
        for (PluginHandle plugin : this.plugins.values()) {
            plugin.getInstance().handleOnPause();
        }
        if (this.cordovaWebView != null) {
            boolean keepRunning = shouldKeepRunning() || this.cordovaInterface.getActivityResultCallback() != null;
            this.cordovaWebView.handlePause(keepRunning);
        }
    }

    public void onStop() {
        for (PluginHandle plugin : this.plugins.values()) {
            plugin.getInstance().handleOnStop();
        }
        if (this.cordovaWebView != null) {
            this.cordovaWebView.handleStop();
        }
    }

    public void onDestroy() {
        for (PluginHandle plugin : this.plugins.values()) {
            plugin.getInstance().handleOnDestroy();
        }
        this.handlerThread.quitSafely();
        if (this.cordovaWebView != null) {
            this.cordovaWebView.handleDestroy();
        }
    }

    public void onDetachedFromWindow() {
        this.webView.removeAllViews();
        this.webView.destroy();
    }

    public String getServerBasePath() {
        return this.localServer.getBasePath();
    }

    public void setServerBasePath(String path) {
        this.localServer.hostFiles(path);
        this.webView.post(new Runnable() { // from class: com.getcapacitor.Bridge$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$setServerBasePath$4();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setServerBasePath$4() {
        this.webView.loadUrl(this.appUrl);
    }

    public void setServerAssetPath(String path) {
        this.localServer.hostAssets(path);
        this.webView.post(new Runnable() { // from class: com.getcapacitor.Bridge$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$setServerAssetPath$5();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setServerAssetPath$5() {
        this.webView.loadUrl(this.appUrl);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$reload$6() {
        this.webView.loadUrl(this.appUrl);
    }

    public void reload() {
        this.webView.post(new Runnable() { // from class: com.getcapacitor.Bridge$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$reload$6();
            }
        });
    }

    public String getLocalUrl() {
        return this.localUrl;
    }

    public WebViewLocalServer getLocalServer() {
        return this.localServer;
    }

    public HostMask getAppAllowNavigationMask() {
        return this.appAllowNavigationMask;
    }

    public Set<String> getAllowedOriginRules() {
        return this.allowedOriginRules;
    }

    public BridgeWebViewClient getWebViewClient() {
        return this.webViewClient;
    }

    public void setWebViewClient(BridgeWebViewClient client) {
        this.webViewClient = client;
        this.webView.setWebViewClient(client);
    }

    List<WebViewListener> getWebViewListeners() {
        return this.webViewListeners;
    }

    void setWebViewListeners(List<WebViewListener> webViewListeners) {
        this.webViewListeners = webViewListeners;
    }

    RouteProcessor getRouteProcessor() {
        return this.routeProcessor;
    }

    void setRouteProcessor(RouteProcessor routeProcessor) {
        this.routeProcessor = routeProcessor;
    }

    ServerPath getServerPath() {
        return this.serverPath;
    }

    public void addWebViewListener(WebViewListener webViewListener) {
        this.webViewListeners.add(webViewListener);
    }

    public void removeWebViewListener(WebViewListener webViewListener) {
        this.webViewListeners.remove(webViewListener);
    }

    public static class Builder {
        private AppCompatActivity activity;
        private Fragment fragment;
        private RouteProcessor routeProcessor;
        private ServerPath serverPath;
        private Bundle instanceState = null;
        private CapConfig config = null;
        private List<Class<? extends Plugin>> plugins = new ArrayList();
        private List<Plugin> pluginInstances = new ArrayList();
        private final List<WebViewListener> webViewListeners = new ArrayList();

        public Builder(AppCompatActivity activity) {
            this.activity = activity;
        }

        public Builder(Fragment fragment) {
            this.activity = (AppCompatActivity) fragment.getActivity();
            this.fragment = fragment;
        }

        public Builder setInstanceState(Bundle instanceState) {
            this.instanceState = instanceState;
            return this;
        }

        public Builder setConfig(CapConfig config) {
            this.config = config;
            return this;
        }

        public Builder setPlugins(List<Class<? extends Plugin>> plugins) {
            this.plugins = plugins;
            return this;
        }

        public Builder addPlugin(Class<? extends Plugin> plugin) {
            this.plugins.add(plugin);
            return this;
        }

        public Builder addPlugins(List<Class<? extends Plugin>> plugins) {
            for (Class<? extends Plugin> cls : plugins) {
                addPlugin(cls);
            }
            return this;
        }

        public Builder addPluginInstance(Plugin plugin) {
            this.pluginInstances.add(plugin);
            return this;
        }

        public Builder addPluginInstances(List<Plugin> plugins) {
            this.pluginInstances.addAll(plugins);
            return this;
        }

        public Builder addWebViewListener(WebViewListener webViewListener) {
            this.webViewListeners.add(webViewListener);
            return this;
        }

        public Builder addWebViewListeners(List<WebViewListener> webViewListeners) {
            for (WebViewListener listener : webViewListeners) {
                addWebViewListener(listener);
            }
            return this;
        }

        public Builder setRouteProcessor(RouteProcessor routeProcessor) {
            this.routeProcessor = routeProcessor;
            return this;
        }

        public Builder setServerPath(ServerPath serverPath) {
            this.serverPath = serverPath;
            return this;
        }

        public Bridge create() throws XmlPullParserException, IOException {
            ConfigXmlParser parser = new ConfigXmlParser();
            parser.parse(this.activity.getApplicationContext());
            CordovaPreferences preferences = parser.getPreferences();
            preferences.setPreferencesBundle(this.activity.getIntent().getExtras());
            List<PluginEntry> pluginEntries = parser.getPluginEntries();
            MockCordovaInterfaceImpl cordovaInterface = new MockCordovaInterfaceImpl(this.activity);
            if (this.instanceState != null) {
                cordovaInterface.restoreInstanceState(this.instanceState);
            }
            WebView webView = (WebView) (this.fragment != null ? this.fragment.getView().findViewById(R.id.webview) : this.activity.findViewById(R.id.webview));
            MockCordovaWebViewImpl mockWebView = new MockCordovaWebViewImpl(this.activity.getApplicationContext());
            mockWebView.init(cordovaInterface, pluginEntries, preferences, webView);
            org.apache.cordova.PluginManager pluginManager = mockWebView.getPluginManager();
            cordovaInterface.onCordovaInit(pluginManager);
            Bridge bridge = new Bridge(this.activity, this.serverPath, this.fragment, webView, this.plugins, this.pluginInstances, cordovaInterface, pluginManager, preferences, this.config);
            if (webView instanceof CapacitorWebView) {
                CapacitorWebView capacitorWebView = (CapacitorWebView) webView;
                capacitorWebView.setBridge(bridge);
            }
            bridge.setCordovaWebView(mockWebView);
            bridge.setWebViewListeners(this.webViewListeners);
            bridge.setRouteProcessor(this.routeProcessor);
            if (this.instanceState != null) {
                bridge.restoreInstanceState(this.instanceState);
            }
            return bridge;
        }
    }
}
