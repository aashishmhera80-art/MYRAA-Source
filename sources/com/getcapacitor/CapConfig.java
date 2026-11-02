package com.getcapacitor;

import android.content.Context;
import android.content.res.AssetManager;
import com.getcapacitor.util.JSONUtils;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes3.dex */
public class CapConfig {
    private static final String LOG_BEHAVIOR_DEBUG = "debug";
    private static final String LOG_BEHAVIOR_NONE = "none";
    private static final String LOG_BEHAVIOR_PRODUCTION = "production";
    private boolean allowMixedContent;
    private String[] allowNavigation;
    private String androidScheme;
    private String appendedUserAgentString;
    private String backgroundColor;
    private boolean captureInput;
    private JSONObject configJSON;
    private String errorPath;
    private String hostname;
    private boolean html5mode;
    private boolean initialFocus;
    private boolean loggingEnabled;
    private int minHuaweiWebViewVersion;
    private int minWebViewVersion;
    private String overriddenUserAgentString;
    private Map<String, PluginConfig> pluginsConfiguration;
    private boolean resolveServiceWorkerRequests;
    private String serverUrl;
    private String startPath;
    private boolean useLegacyBridge;
    private boolean webContentsDebuggingEnabled;
    private boolean zoomableWebView;

    private CapConfig() {
        this.html5mode = true;
        this.hostname = "localhost";
        this.androidScheme = "https";
        this.allowMixedContent = false;
        this.captureInput = false;
        this.webContentsDebuggingEnabled = false;
        this.loggingEnabled = true;
        this.initialFocus = true;
        this.useLegacyBridge = false;
        this.minWebViewVersion = 60;
        this.minHuaweiWebViewVersion = 10;
        this.zoomableWebView = false;
        this.resolveServiceWorkerRequests = true;
        this.pluginsConfiguration = null;
        this.configJSON = new JSONObject();
    }

    @Deprecated
    public CapConfig(AssetManager assetManager, JSONObject config) {
        this.html5mode = true;
        this.hostname = "localhost";
        this.androidScheme = "https";
        this.allowMixedContent = false;
        this.captureInput = false;
        this.webContentsDebuggingEnabled = false;
        this.loggingEnabled = true;
        this.initialFocus = true;
        this.useLegacyBridge = false;
        this.minWebViewVersion = 60;
        this.minHuaweiWebViewVersion = 10;
        this.zoomableWebView = false;
        this.resolveServiceWorkerRequests = true;
        this.pluginsConfiguration = null;
        this.configJSON = new JSONObject();
        if (config != null) {
            this.configJSON = config;
        } else {
            loadConfigFromAssets(assetManager, null);
        }
        deserializeConfig(null);
    }

    public static CapConfig loadDefault(Context context) {
        CapConfig config = new CapConfig();
        if (context == null) {
            Logger.error("Capacitor Config could not be created from file. Context must not be null.");
            return config;
        }
        config.loadConfigFromAssets(context.getAssets(), null);
        config.deserializeConfig(context);
        return config;
    }

    public static CapConfig loadFromAssets(Context context, String path) {
        CapConfig config = new CapConfig();
        if (context == null) {
            Logger.error("Capacitor Config could not be created from file. Context must not be null.");
            return config;
        }
        config.loadConfigFromAssets(context.getAssets(), path);
        config.deserializeConfig(context);
        return config;
    }

    public static CapConfig loadFromFile(Context context, String path) {
        CapConfig config = new CapConfig();
        if (context == null) {
            Logger.error("Capacitor Config could not be created from file. Context must not be null.");
            return config;
        }
        config.loadConfigFromFile(path);
        config.deserializeConfig(context);
        return config;
    }

    private CapConfig(Builder builder) {
        this.html5mode = true;
        this.hostname = "localhost";
        this.androidScheme = "https";
        this.allowMixedContent = false;
        this.captureInput = false;
        this.webContentsDebuggingEnabled = false;
        this.loggingEnabled = true;
        this.initialFocus = true;
        this.useLegacyBridge = false;
        this.minWebViewVersion = 60;
        this.minHuaweiWebViewVersion = 10;
        this.zoomableWebView = false;
        this.resolveServiceWorkerRequests = true;
        this.pluginsConfiguration = null;
        this.configJSON = new JSONObject();
        this.html5mode = builder.html5mode;
        this.serverUrl = builder.serverUrl;
        this.hostname = builder.hostname;
        if (validateScheme(builder.androidScheme)) {
            this.androidScheme = builder.androidScheme;
        }
        this.allowNavigation = builder.allowNavigation;
        this.overriddenUserAgentString = builder.overriddenUserAgentString;
        this.appendedUserAgentString = builder.appendedUserAgentString;
        this.backgroundColor = builder.backgroundColor;
        this.allowMixedContent = builder.allowMixedContent;
        this.captureInput = builder.captureInput;
        this.webContentsDebuggingEnabled = builder.webContentsDebuggingEnabled.booleanValue();
        this.loggingEnabled = builder.loggingEnabled;
        this.initialFocus = builder.initialFocus;
        this.useLegacyBridge = builder.useLegacyBridge;
        this.minWebViewVersion = builder.minWebViewVersion;
        this.minHuaweiWebViewVersion = builder.minHuaweiWebViewVersion;
        this.errorPath = builder.errorPath;
        this.zoomableWebView = builder.zoomableWebView;
        this.resolveServiceWorkerRequests = builder.resolveServiceWorkerRequests;
        this.startPath = builder.startPath;
        this.pluginsConfiguration = builder.pluginsConfiguration;
    }

    private void loadConfigFromAssets(AssetManager assetManager, String path) {
        if (path == null) {
            path = "";
        } else if (path.charAt(path.length() - 1) != '/') {
            path = path + "/";
        }
        try {
            String jsonString = FileUtils.readFileFromAssets(assetManager, path + "capacitor.config.json");
            this.configJSON = new JSONObject(jsonString);
        } catch (IOException ex) {
            Logger.error("Unable to load capacitor.config.json. Run npx cap copy first", ex);
        } catch (JSONException ex2) {
            Logger.error("Unable to parse capacitor.config.json. Make sure it's valid json", ex2);
        }
    }

    private void loadConfigFromFile(String path) {
        if (path == null) {
            path = "";
        } else if (path.charAt(path.length() - 1) != '/') {
            path = path + "/";
        }
        try {
            File configFile = new File(path + "capacitor.config.json");
            String jsonString = FileUtils.readFileFromDisk(configFile);
            this.configJSON = new JSONObject(jsonString);
        } catch (IOException ex) {
            Logger.error("Unable to load capacitor.config.json.", ex);
        } catch (JSONException ex2) {
            Logger.error("Unable to parse capacitor.config.json. Make sure it's valid json", ex2);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:20:0x0138  */
    private void deserializeConfig(Context context) {
        boolean isDebug = (context == null || (context.getApplicationInfo().flags & 2) == 0) ? false : true;
        this.html5mode = JSONUtils.getBoolean(this.configJSON, "server.html5mode", this.html5mode);
        this.serverUrl = JSONUtils.getString(this.configJSON, "server.url", null);
        this.hostname = JSONUtils.getString(this.configJSON, "server.hostname", this.hostname);
        this.errorPath = JSONUtils.getString(this.configJSON, "server.errorPath", null);
        this.startPath = JSONUtils.getString(this.configJSON, "server.appStartPath", null);
        String configSchema = JSONUtils.getString(this.configJSON, "server.androidScheme", this.androidScheme);
        if (validateScheme(configSchema)) {
            this.androidScheme = configSchema;
        }
        this.allowNavigation = JSONUtils.getArray(this.configJSON, "server.allowNavigation", null);
        this.overriddenUserAgentString = JSONUtils.getString(this.configJSON, "android.overrideUserAgent", JSONUtils.getString(this.configJSON, "overrideUserAgent", null));
        this.appendedUserAgentString = JSONUtils.getString(this.configJSON, "android.appendUserAgent", JSONUtils.getString(this.configJSON, "appendUserAgent", null));
        this.backgroundColor = JSONUtils.getString(this.configJSON, "android.backgroundColor", JSONUtils.getString(this.configJSON, "backgroundColor", null));
        this.allowMixedContent = JSONUtils.getBoolean(this.configJSON, "android.allowMixedContent", JSONUtils.getBoolean(this.configJSON, "allowMixedContent", this.allowMixedContent));
        this.minWebViewVersion = JSONUtils.getInt(this.configJSON, "android.minWebViewVersion", 60);
        this.minHuaweiWebViewVersion = JSONUtils.getInt(this.configJSON, "android.minHuaweiWebViewVersion", 10);
        this.captureInput = JSONUtils.getBoolean(this.configJSON, "android.captureInput", this.captureInput);
        this.useLegacyBridge = JSONUtils.getBoolean(this.configJSON, "android.useLegacyBridge", this.useLegacyBridge);
        this.webContentsDebuggingEnabled = JSONUtils.getBoolean(this.configJSON, "android.webContentsDebuggingEnabled", isDebug);
        this.zoomableWebView = JSONUtils.getBoolean(this.configJSON, "android.zoomEnabled", JSONUtils.getBoolean(this.configJSON, "zoomEnabled", false));
        this.resolveServiceWorkerRequests = JSONUtils.getBoolean(this.configJSON, "android.resolveServiceWorkerRequests", true);
        String logBehavior = JSONUtils.getString(this.configJSON, "android.loggingBehavior", JSONUtils.getString(this.configJSON, "loggingBehavior", LOG_BEHAVIOR_DEBUG));
        switch (logBehavior.toLowerCase(Locale.ROOT)) {
            case "production":
                this.loggingEnabled = true;
                break;
            case "none":
                this.loggingEnabled = false;
                break;
            default:
                this.loggingEnabled = isDebug;
                break;
        }
        this.initialFocus = JSONUtils.getBoolean(this.configJSON, "android.initialFocus", JSONUtils.getBoolean(this.configJSON, "initialFocus", this.initialFocus));
        this.pluginsConfiguration = deserializePluginsConfig(JSONUtils.getObject(this.configJSON, "plugins"));
    }

    private boolean validateScheme(String scheme) {
        List<String> invalidSchemes = Arrays.asList("file", "ftp", "ftps", "ws", "wss", "about", "blob", "data");
        if (invalidSchemes.contains(scheme)) {
            Logger.warn(scheme + " is not an allowed scheme.  Defaulting to https.");
            return false;
        }
        if (!scheme.equals("http") && !scheme.equals("https")) {
            Logger.warn("Using a non-standard scheme: " + scheme + " for Android. This is known to cause issues as of Android Webview 117.");
        }
        return true;
    }

    public boolean isHTML5Mode() {
        return this.html5mode;
    }

    public String getServerUrl() {
        return this.serverUrl;
    }

    public String getErrorPath() {
        return this.errorPath;
    }

    public String getHostname() {
        return this.hostname;
    }

    public String getStartPath() {
        return this.startPath;
    }

    public String getAndroidScheme() {
        return this.androidScheme;
    }

    public String[] getAllowNavigation() {
        return this.allowNavigation;
    }

    public String getOverriddenUserAgentString() {
        return this.overriddenUserAgentString;
    }

    public String getAppendedUserAgentString() {
        return this.appendedUserAgentString;
    }

    public String getBackgroundColor() {
        return this.backgroundColor;
    }

    public boolean isMixedContentAllowed() {
        return this.allowMixedContent;
    }

    public boolean isInputCaptured() {
        return this.captureInput;
    }

    public boolean isResolveServiceWorkerRequests() {
        return this.resolveServiceWorkerRequests;
    }

    public boolean isWebContentsDebuggingEnabled() {
        return this.webContentsDebuggingEnabled;
    }

    public boolean isZoomableWebView() {
        return this.zoomableWebView;
    }

    public boolean isLoggingEnabled() {
        return this.loggingEnabled;
    }

    public boolean isInitialFocus() {
        return this.initialFocus;
    }

    public boolean isUsingLegacyBridge() {
        return this.useLegacyBridge;
    }

    public int getMinWebViewVersion() {
        if (this.minWebViewVersion < 55) {
            Logger.warn("Specified minimum webview version is too low, defaulting to 55");
            return 55;
        }
        return this.minWebViewVersion;
    }

    public int getMinHuaweiWebViewVersion() {
        if (this.minHuaweiWebViewVersion < 10) {
            Logger.warn("Specified minimum Huawei webview version is too low, defaulting to 10");
            return 10;
        }
        return this.minHuaweiWebViewVersion;
    }

    public PluginConfig getPluginConfiguration(String pluginId) {
        PluginConfig pluginConfig = this.pluginsConfiguration.get(pluginId);
        if (pluginConfig == null) {
            return new PluginConfig(new JSONObject());
        }
        return pluginConfig;
    }

    @Deprecated
    public JSONObject getObject(String key) {
        try {
            return this.configJSON.getJSONObject(key);
        } catch (Exception e) {
            return null;
        }
    }

    @Deprecated
    public String getString(String key) {
        return JSONUtils.getString(this.configJSON, key, null);
    }

    @Deprecated
    public String getString(String key, String defaultValue) {
        return JSONUtils.getString(this.configJSON, key, defaultValue);
    }

    @Deprecated
    public boolean getBoolean(String key, boolean defaultValue) {
        return JSONUtils.getBoolean(this.configJSON, key, defaultValue);
    }

    @Deprecated
    public int getInt(String key, int defaultValue) {
        return JSONUtils.getInt(this.configJSON, key, defaultValue);
    }

    @Deprecated
    public String[] getArray(String key) {
        return JSONUtils.getArray(this.configJSON, key, null);
    }

    @Deprecated
    public String[] getArray(String key, String[] defaultValue) {
        return JSONUtils.getArray(this.configJSON, key, defaultValue);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Map<String, PluginConfig> deserializePluginsConfig(JSONObject pluginsConfig) {
        Map<String, PluginConfig> pluginsMap = new HashMap<>();
        if (pluginsConfig == null) {
            return pluginsMap;
        }
        Iterator<String> pluginIds = pluginsConfig.keys();
        while (pluginIds.hasNext()) {
            String pluginId = pluginIds.next();
            try {
                JSONObject value = pluginsConfig.getJSONObject(pluginId);
                PluginConfig pluginConfig = new PluginConfig(value);
                pluginsMap.put(pluginId, pluginConfig);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return pluginsMap;
    }

    public static class Builder {
        private String[] allowNavigation;
        private String appendedUserAgentString;
        private String backgroundColor;
        private Context context;
        private String errorPath;
        private String overriddenUserAgentString;
        private String serverUrl;
        private boolean html5mode = true;
        private String hostname = "localhost";
        private String androidScheme = "https";
        private boolean allowMixedContent = false;
        private boolean captureInput = false;
        private Boolean webContentsDebuggingEnabled = null;
        private boolean loggingEnabled = true;
        private boolean initialFocus = false;
        private boolean useLegacyBridge = false;
        private int minWebViewVersion = 60;
        private int minHuaweiWebViewVersion = 10;
        private boolean zoomableWebView = false;
        private boolean resolveServiceWorkerRequests = true;
        private String startPath = null;
        private Map<String, PluginConfig> pluginsConfiguration = new HashMap();

        public Builder(Context context) {
            this.context = context;
        }

        public CapConfig create() {
            if (this.webContentsDebuggingEnabled == null) {
                this.webContentsDebuggingEnabled = Boolean.valueOf((this.context.getApplicationInfo().flags & 2) != 0);
            }
            return new CapConfig(this);
        }

        public Builder setPluginsConfiguration(JSONObject pluginsConfiguration) {
            this.pluginsConfiguration = CapConfig.deserializePluginsConfig(pluginsConfiguration);
            return this;
        }

        public Builder setHTML5mode(boolean html5mode) {
            this.html5mode = html5mode;
            return this;
        }

        public Builder setServerUrl(String serverUrl) {
            this.serverUrl = serverUrl;
            return this;
        }

        public Builder setErrorPath(String errorPath) {
            this.errorPath = errorPath;
            return this;
        }

        public Builder setHostname(String hostname) {
            this.hostname = hostname;
            return this;
        }

        public Builder setStartPath(String path) {
            this.startPath = path;
            return this;
        }

        public Builder setAndroidScheme(String androidScheme) {
            this.androidScheme = androidScheme;
            return this;
        }

        public Builder setAllowNavigation(String[] allowNavigation) {
            this.allowNavigation = allowNavigation;
            return this;
        }

        public Builder setOverriddenUserAgentString(String overriddenUserAgentString) {
            this.overriddenUserAgentString = overriddenUserAgentString;
            return this;
        }

        public Builder setAppendedUserAgentString(String appendedUserAgentString) {
            this.appendedUserAgentString = appendedUserAgentString;
            return this;
        }

        public Builder setBackgroundColor(String backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public Builder setAllowMixedContent(boolean allowMixedContent) {
            this.allowMixedContent = allowMixedContent;
            return this;
        }

        public Builder setCaptureInput(boolean captureInput) {
            this.captureInput = captureInput;
            return this;
        }

        public Builder setUseLegacyBridge(boolean useLegacyBridge) {
            this.useLegacyBridge = useLegacyBridge;
            return this;
        }

        public Builder setResolveServiceWorkerRequests(boolean resolveServiceWorkerRequests) {
            this.resolveServiceWorkerRequests = resolveServiceWorkerRequests;
            return this;
        }

        public Builder setWebContentsDebuggingEnabled(boolean webContentsDebuggingEnabled) {
            this.webContentsDebuggingEnabled = Boolean.valueOf(webContentsDebuggingEnabled);
            return this;
        }

        public Builder setZoomableWebView(boolean zoomableWebView) {
            this.zoomableWebView = zoomableWebView;
            return this;
        }

        public Builder setLoggingEnabled(boolean enabled) {
            this.loggingEnabled = enabled;
            return this;
        }

        public Builder setInitialFocus(boolean focus) {
            this.initialFocus = focus;
            return this;
        }
    }
}
