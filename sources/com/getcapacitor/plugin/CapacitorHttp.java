package com.getcapacitor.plugin;

import android.webkit.JavascriptInterface;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginConfig;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.plugin.util.CapacitorHttpUrlConnection;
import com.getcapacitor.plugin.util.HttpRequestHandler;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* JADX INFO: loaded from: classes4.dex */
@CapacitorPlugin(permissions = {@Permission(alias = "HttpWrite", strings = {"android.permission.WRITE_EXTERNAL_STORAGE"}), @Permission(alias = "HttpRead", strings = {"android.permission.READ_EXTERNAL_STORAGE"})})
public class CapacitorHttp extends Plugin {
    private final Map<Runnable, PluginCall> activeRequests = new ConcurrentHashMap();
    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Override // com.getcapacitor.Plugin
    public void load() {
        this.bridge.getWebView().addJavascriptInterface(this, "CapacitorHttpAndroidInterface");
        super.load();
    }

    @Override // com.getcapacitor.Plugin
    protected void handleOnDestroy() {
        super.handleOnDestroy();
        for (Map.Entry<Runnable, PluginCall> entry : this.activeRequests.entrySet()) {
            entry.getKey();
            PluginCall call = entry.getValue();
            if (call.getData().has("activeCapacitorHttpUrlConnection")) {
                try {
                    CapacitorHttpUrlConnection connection = (CapacitorHttpUrlConnection) call.getData().get("activeCapacitorHttpUrlConnection");
                    connection.disconnect();
                    call.getData().remove("activeCapacitorHttpUrlConnection");
                } catch (Exception e) {
                }
            }
            getBridge().releaseCall(call);
        }
        this.activeRequests.clear();
        this.executor.shutdownNow();
    }

    private void http(final PluginCall call, final String httpMethod) {
        Runnable asyncHttpCall = new Runnable() { // from class: com.getcapacitor.plugin.CapacitorHttp.1
            @Override // java.lang.Runnable
            public void run() {
                try {
                    try {
                        JSObject response = HttpRequestHandler.request(call, httpMethod, CapacitorHttp.this.getBridge());
                        call.resolve(response);
                    } catch (Exception e) {
                        call.reject(e.getLocalizedMessage(), e.getClass().getSimpleName(), e);
                    }
                } finally {
                    CapacitorHttp.this.activeRequests.remove(this);
                }
            }
        };
        if (!this.executor.isShutdown()) {
            this.activeRequests.put(asyncHttpCall, call);
            this.executor.submit(asyncHttpCall);
        } else {
            call.reject("Failed to execute request - Http Plugin was shutdown");
        }
    }

    @JavascriptInterface
    public boolean isEnabled() {
        PluginConfig pluginConfig = getBridge().getConfig().getPluginConfiguration("CapacitorHttp");
        return pluginConfig.getBoolean("enabled", false);
    }

    @PluginMethod
    public void request(PluginCall call) {
        http(call, null);
    }

    @PluginMethod
    public void get(PluginCall call) {
        http(call, "GET");
    }

    @PluginMethod
    public void post(PluginCall call) {
        http(call, "POST");
    }

    @PluginMethod
    public void put(PluginCall call) {
        http(call, "PUT");
    }

    @PluginMethod
    public void patch(PluginCall call) {
        http(call, "PATCH");
    }

    @PluginMethod
    public void delete(PluginCall call) {
        http(call, "DELETE");
    }
}
