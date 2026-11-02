package org.apache.cordova;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.WebView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import org.json.JSONException;

/* JADX INFO: loaded from: classes.dex */
public class PluginManager {
    private static final int SLOW_EXEC_WARNING_THRESHOLD;
    private final CordovaWebView app;
    private final CordovaInterface ctx;
    private boolean isInitialized;
    private CordovaPlugin permissionRequester;
    private static String TAG = "PluginManager";
    private static String SCHEME_HTTPS = "https";
    private static String DEFAULT_HOSTNAME = "localhost";
    private final Map<String, CordovaPlugin> pluginMap = Collections.synchronizedMap(new LinkedHashMap());
    private final Map<String, PluginEntry> entryMap = Collections.synchronizedMap(new LinkedHashMap());

    static {
        SLOW_EXEC_WARNING_THRESHOLD = Debug.isDebuggerConnected() ? 60 : 16;
    }

    public PluginManager(CordovaWebView cordovaWebView, CordovaInterface cordova, Collection<PluginEntry> pluginEntries) {
        this.ctx = cordova;
        this.app = cordovaWebView;
        setPluginEntries(pluginEntries);
    }

    public Collection<PluginEntry> getPluginEntries() {
        return this.entryMap.values();
    }

    public void setPluginEntries(Collection<PluginEntry> pluginEntries) {
        if (this.isInitialized) {
            onPause(false);
            onDestroy();
            this.pluginMap.clear();
            this.entryMap.clear();
        }
        for (PluginEntry entry : pluginEntries) {
            addService(entry);
        }
        if (this.isInitialized) {
            startupPlugins();
        }
    }

    public void init() {
        LOG.d(TAG, "init()");
        this.isInitialized = true;
        onPause(false);
        onDestroy();
        this.pluginMap.clear();
        startupPlugins();
    }

    private void startupPlugins() {
        synchronized (this.entryMap) {
            for (PluginEntry entry : this.entryMap.values()) {
                if (entry.onload) {
                    getPlugin(entry.service);
                } else {
                    LOG.d(TAG, "startupPlugins: put - " + entry.service);
                    this.pluginMap.put(entry.service, null);
                }
            }
        }
    }

    public void exec(String service, String action, String callbackId, String rawArgs) {
        CordovaPlugin plugin = getPlugin(service);
        if (plugin == null) {
            LOG.d(TAG, "exec() call to unknown plugin: " + service);
            PluginResult cr = new PluginResult(PluginResult.Status.CLASS_NOT_FOUND_EXCEPTION);
            this.app.sendPluginResult(cr, callbackId);
            return;
        }
        CallbackContext callbackContext = new CallbackContext(callbackId, this.app);
        try {
            long pluginStartTime = System.currentTimeMillis();
            boolean wasValidAction = plugin.execute(action, rawArgs, callbackContext);
            long duration = System.currentTimeMillis() - pluginStartTime;
            if (duration > SLOW_EXEC_WARNING_THRESHOLD) {
                LOG.w(TAG, "THREAD WARNING: exec() call to " + service + "." + action + " blocked the main thread for " + duration + "ms. Plugin should use CordovaInterface.getThreadPool().");
            }
            if (!wasValidAction) {
                PluginResult cr2 = new PluginResult(PluginResult.Status.INVALID_ACTION);
                callbackContext.sendPluginResult(cr2);
            }
        } catch (JSONException e) {
            PluginResult cr3 = new PluginResult(PluginResult.Status.JSON_EXCEPTION);
            callbackContext.sendPluginResult(cr3);
        } catch (Exception e2) {
            LOG.e(TAG, "Uncaught exception from plugin", e2);
            callbackContext.error(e2.getMessage());
        }
    }

    public CordovaPlugin getPlugin(String service) {
        CordovaPlugin ret = this.pluginMap.get(service);
        if (ret == null) {
            PluginEntry pe = this.entryMap.get(service);
            if (pe == null) {
                return null;
            }
            if (pe.plugin != null) {
                ret = pe.plugin;
            } else {
                ret = instantiatePlugin(pe.pluginClass);
            }
            ret.privateInitialize(service, this.ctx, this.app, this.app.getPreferences());
            LOG.d(TAG, "getPlugin - put: " + service);
            this.pluginMap.put(service, ret);
        }
        return ret;
    }

    public void addService(String service, String className) {
        addService(service, className, false);
    }

    public void addService(String service, String className, boolean onload) {
        PluginEntry entry = new PluginEntry(service, className, onload);
        addService(entry);
    }

    public void addService(PluginEntry entry) {
        this.entryMap.put(entry.service, entry);
        if (entry.plugin != null) {
            entry.plugin.privateInitialize(entry.service, this.ctx, this.app, this.app.getPreferences());
            LOG.d(TAG, "addService: put - " + entry.service);
            this.pluginMap.put(entry.service, entry.plugin);
        }
    }

    public void onPause(boolean multitasking) {
        synchronized (this.pluginMap) {
            for (CordovaPlugin plugin : this.pluginMap.values()) {
                if (plugin != null) {
                    plugin.onPause(multitasking);
                }
            }
        }
    }

    public boolean onReceivedHttpAuthRequest(CordovaWebView view, ICordovaHttpAuthHandler handler, String host, String realm) {
        synchronized (this.pluginMap) {
            for (CordovaPlugin plugin : this.pluginMap.values()) {
                if (plugin != null && plugin.onReceivedHttpAuthRequest(this.app, handler, host, realm)) {
                    return true;
                }
            }
            return false;
        }
    }

    public boolean onReceivedClientCertRequest(CordovaWebView view, ICordovaClientCertRequest request) {
        synchronized (this.pluginMap) {
            for (CordovaPlugin plugin : this.pluginMap.values()) {
                if (plugin != null && plugin.onReceivedClientCertRequest(this.app, request)) {
                    return true;
                }
            }
            return false;
        }
    }

    public void onResume(boolean multitasking) {
        synchronized (this.pluginMap) {
            for (CordovaPlugin plugin : this.pluginMap.values()) {
                if (plugin != null) {
                    plugin.onResume(multitasking);
                }
            }
        }
    }

    public void onStart() {
        synchronized (this.pluginMap) {
            for (CordovaPlugin plugin : this.pluginMap.values()) {
                if (plugin != null) {
                    plugin.onStart();
                }
            }
        }
    }

    public void onStop() {
        synchronized (this.pluginMap) {
            for (CordovaPlugin plugin : this.pluginMap.values()) {
                if (plugin != null) {
                    plugin.onStop();
                }
            }
        }
    }

    public void onDestroy() {
        synchronized (this.pluginMap) {
            for (CordovaPlugin plugin : this.pluginMap.values()) {
                if (plugin != null) {
                    plugin.onDestroy();
                }
            }
        }
    }

    public Object postMessage(final String id, final Object data) {
        LOG.d(TAG, "postMessage: " + id);
        synchronized (this.pluginMap) {
            this.pluginMap.forEach(new BiConsumer() { // from class: org.apache.cordova.PluginManager$$ExternalSyntheticLambda0
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    PluginManager.lambda$postMessage$0(id, data, (String) obj, (CordovaPlugin) obj2);
                }
            });
        }
        return this.ctx.onMessage(id, data);
    }

    static /* synthetic */ void lambda$postMessage$0(String id, Object data, String s, CordovaPlugin plugin) {
        if (plugin != null) {
            plugin.onMessage(id, data);
        }
    }

    public void onNewIntent(Intent intent) {
        synchronized (this.pluginMap) {
            for (CordovaPlugin plugin : this.pluginMap.values()) {
                if (plugin != null) {
                    plugin.onNewIntent(intent);
                }
            }
        }
    }

    private String getLaunchUrlPrefix() {
        if (!this.app.getPreferences().getBoolean("AndroidInsecureFileModeEnabled", false)) {
            String scheme = this.app.getPreferences().getString("scheme", SCHEME_HTTPS).toLowerCase();
            String hostname = this.app.getPreferences().getString("hostname", DEFAULT_HOSTNAME).toLowerCase();
            return scheme + "://" + hostname + '/';
        }
        return "file://";
    }

    public boolean shouldAllowRequest(String url) {
        Boolean result;
        synchronized (this.entryMap) {
            for (PluginEntry entry : this.entryMap.values()) {
                CordovaPlugin plugin = this.pluginMap.get(entry.service);
                if (plugin != null && (result = plugin.shouldAllowRequest(url)) != null) {
                    return result.booleanValue();
                }
            }
            if (url.startsWith("blob:") || url.startsWith("data:") || url.startsWith("about:blank") || url.startsWith("https://ssl.gstatic.com/accessibility/javascript/android/")) {
                return true;
            }
            if (url.startsWith("file://")) {
                return !url.contains("/app_webview/");
            }
            return false;
        }
    }

    public boolean shouldAllowNavigation(String url) {
        Boolean result;
        synchronized (this.entryMap) {
            for (PluginEntry entry : this.entryMap.values()) {
                CordovaPlugin plugin = this.pluginMap.get(entry.service);
                if (plugin != null && (result = plugin.shouldAllowNavigation(url)) != null) {
                    return result.booleanValue();
                }
            }
            return url.startsWith(getLaunchUrlPrefix()) || url.startsWith("about:blank");
        }
    }

    public boolean shouldAllowBridgeAccess(String url) {
        Boolean result;
        synchronized (this.entryMap) {
            for (PluginEntry entry : this.entryMap.values()) {
                CordovaPlugin plugin = this.pluginMap.get(entry.service);
                if (plugin != null && (result = plugin.shouldAllowBridgeAccess(url)) != null) {
                    return result.booleanValue();
                }
            }
            return url.startsWith(getLaunchUrlPrefix());
        }
    }

    public Boolean shouldOpenExternalUrl(String url) {
        Boolean result;
        synchronized (this.entryMap) {
            for (PluginEntry entry : this.entryMap.values()) {
                CordovaPlugin plugin = this.pluginMap.get(entry.service);
                if (plugin != null && (result = plugin.shouldOpenExternalUrl(url)) != null) {
                    return result;
                }
            }
            return false;
        }
    }

    public boolean onOverrideUrlLoading(String url) {
        synchronized (this.entryMap) {
            for (PluginEntry entry : this.entryMap.values()) {
                CordovaPlugin plugin = this.pluginMap.get(entry.service);
                if (plugin != null && plugin.onOverrideUrlLoading(url)) {
                    return true;
                }
            }
            return false;
        }
    }

    public void onReset() {
        synchronized (this.pluginMap) {
            for (CordovaPlugin plugin : this.pluginMap.values()) {
                if (plugin != null) {
                    plugin.onReset();
                }
            }
        }
    }

    Uri remapUri(Uri uri) {
        Uri ret;
        synchronized (this.pluginMap) {
            for (CordovaPlugin plugin : this.pluginMap.values()) {
                if (plugin != null && (ret = plugin.remapUri(uri)) != null) {
                    return ret;
                }
            }
            return null;
        }
    }

    private CordovaPlugin instantiatePlugin(String className) {
        Class<?> c = null;
        if (className != null) {
            try {
                if (!"".equals(className)) {
                    c = Class.forName(className);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error adding plugin " + className + ".");
                return null;
            }
        }
        if (!((c != null) & CordovaPlugin.class.isAssignableFrom(c))) {
            return null;
        }
        CordovaPlugin ret = (CordovaPlugin) c.newInstance();
        return ret;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        synchronized (this.pluginMap) {
            for (CordovaPlugin plugin : this.pluginMap.values()) {
                if (plugin != null) {
                    plugin.onConfigurationChanged(newConfig);
                }
            }
        }
    }

    public Bundle onSaveInstanceState() {
        Bundle pluginState;
        Bundle state = new Bundle();
        synchronized (this.pluginMap) {
            for (CordovaPlugin plugin : this.pluginMap.values()) {
                if (plugin != null && (pluginState = plugin.onSaveInstanceState()) != null) {
                    state.putBundle(plugin.getServiceName(), pluginState);
                }
            }
        }
        return state;
    }

    public ArrayList<CordovaPluginPathHandler> getPluginPathHandlers() {
        ArrayList<CordovaPluginPathHandler> handlers = new ArrayList<>();
        for (CordovaPlugin plugin : this.pluginMap.values()) {
            if (plugin != null && plugin.getPathHandler() != null) {
                handlers.add(plugin.getPathHandler());
            }
        }
        return handlers;
    }

    public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
        boolean result = false;
        synchronized (this.entryMap) {
            for (PluginEntry entry : this.entryMap.values()) {
                CordovaPlugin plugin = this.pluginMap.get(entry.service);
                if (plugin != null && plugin.onRenderProcessGone(view, detail)) {
                    result = true;
                }
            }
        }
        return result;
    }
}
