package com.getcapacitor;

import com.getcapacitor.annotation.CapacitorPlugin;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes3.dex */
public class PluginHandle {
    private final Bridge bridge;
    private Plugin instance;
    private NativePlugin legacyPluginAnnotation;
    private CapacitorPlugin pluginAnnotation;
    private final Class<? extends Plugin> pluginClass;
    private final String pluginId;
    private final Map<String, PluginMethodHandle> pluginMethods;

    private PluginHandle(Class<? extends Plugin> clazz, Bridge bridge) throws InvalidPluginException {
        this.pluginMethods = new HashMap();
        this.bridge = bridge;
        this.pluginClass = clazz;
        CapacitorPlugin pluginAnnotation = (CapacitorPlugin) this.pluginClass.getAnnotation(CapacitorPlugin.class);
        if (pluginAnnotation != null) {
            if (!pluginAnnotation.name().equals("")) {
                this.pluginId = pluginAnnotation.name();
            } else {
                this.pluginId = this.pluginClass.getSimpleName();
            }
            this.pluginAnnotation = pluginAnnotation;
        } else {
            NativePlugin legacyPluginAnnotation = (NativePlugin) this.pluginClass.getAnnotation(NativePlugin.class);
            if (legacyPluginAnnotation != null) {
                if (!legacyPluginAnnotation.name().equals("")) {
                    this.pluginId = legacyPluginAnnotation.name();
                } else {
                    this.pluginId = this.pluginClass.getSimpleName();
                }
                this.legacyPluginAnnotation = legacyPluginAnnotation;
            } else {
                throw new InvalidPluginException("No @CapacitorPlugin annotation found for plugin " + this.pluginClass.getName());
            }
        }
        indexMethods(clazz);
    }

    public PluginHandle(Bridge bridge, Class<? extends Plugin> pluginClass) throws InvalidPluginException, PluginLoadException {
        this(pluginClass, bridge);
        load();
    }

    public PluginHandle(Bridge bridge, Plugin plugin) throws InvalidPluginException {
        this((Class<? extends Plugin>) plugin.getClass(), bridge);
        loadInstance(plugin);
    }

    public Class<? extends Plugin> getPluginClass() {
        return this.pluginClass;
    }

    public String getId() {
        return this.pluginId;
    }

    public NativePlugin getLegacyPluginAnnotation() {
        return this.legacyPluginAnnotation;
    }

    public CapacitorPlugin getPluginAnnotation() {
        return this.pluginAnnotation;
    }

    public Plugin getInstance() {
        return this.instance;
    }

    public Collection<PluginMethodHandle> getMethods() {
        return this.pluginMethods.values();
    }

    public Plugin load() throws PluginLoadException {
        if (this.instance != null) {
            return this.instance;
        }
        try {
            this.instance = this.pluginClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            return loadInstance(this.instance);
        } catch (Exception e) {
            throw new PluginLoadException("Unable to load plugin instance. Ensure plugin is publicly accessible");
        }
    }

    public Plugin loadInstance(Plugin plugin) {
        this.instance = plugin;
        this.instance.setPluginHandle(this);
        this.instance.setBridge(this.bridge);
        this.instance.load();
        this.instance.initializeActivityLaunchers();
        return this.instance;
    }

    public void invoke(String methodName, PluginCall call) throws IllegalAccessException, PluginLoadException, InvalidPluginMethodException, InvocationTargetException {
        if (this.instance == null) {
            load();
        }
        PluginMethodHandle methodMeta = this.pluginMethods.get(methodName);
        if (methodMeta == null) {
            throw new InvalidPluginMethodException("No method " + methodName + " found for plugin " + this.pluginClass.getName());
        }
        methodMeta.getMethod().invoke(this.instance, call);
    }

    private void indexMethods(Class<? extends Plugin> plugin) {
        Method[] methods = this.pluginClass.getMethods();
        for (Method methodReflect : methods) {
            PluginMethod method = (PluginMethod) methodReflect.getAnnotation(PluginMethod.class);
            if (method != null) {
                PluginMethodHandle methodMeta = new PluginMethodHandle(methodReflect, method);
                this.pluginMethods.put(methodReflect.getName(), methodMeta);
            }
        }
    }
}
