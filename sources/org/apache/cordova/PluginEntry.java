package org.apache.cordova;

/* JADX INFO: loaded from: classes.dex */
public final class PluginEntry {
    public final boolean onload;
    public final CordovaPlugin plugin;
    public final String pluginClass;
    public final String service;

    public PluginEntry(String service, CordovaPlugin plugin) {
        this(service, plugin.getClass().getName(), true, plugin);
    }

    public PluginEntry(String service, CordovaPlugin plugin, boolean onload) {
        this(service, plugin.getClass().getName(), onload, plugin);
    }

    public PluginEntry(String service, String pluginClass, boolean onload) {
        this(service, pluginClass, onload, null);
    }

    private PluginEntry(String service, String pluginClass, boolean onload, CordovaPlugin plugin) {
        this.service = service;
        this.pluginClass = pluginClass;
        this.onload = onload;
        this.plugin = plugin;
    }
}
