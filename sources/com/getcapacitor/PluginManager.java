package com.getcapacitor;

import android.content.res.AssetManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes3.dex */
public class PluginManager {
    private final AssetManager assetManager;

    public PluginManager(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public List<Class<? extends Plugin>> loadPluginClasses() throws PluginLoadException {
        JSONArray pluginsJSON = parsePluginsJSON();
        ArrayList arrayList = new ArrayList();
        try {
            int size = pluginsJSON.length();
            for (int i = 0; i < size; i++) {
                JSONObject pluginJSON = pluginsJSON.getJSONObject(i);
                String classPath = pluginJSON.getString("classpath");
                Class<?> c = Class.forName(classPath);
                arrayList.add(c.asSubclass(Plugin.class));
            }
            return arrayList;
        } catch (ClassNotFoundException e) {
            throw new PluginLoadException("Could not find class by class path: " + e.getMessage());
        } catch (JSONException e2) {
            throw new PluginLoadException("Could not parse capacitor.plugins.json as JSON");
        }
    }

    private JSONArray parsePluginsJSON() throws PluginLoadException {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.assetManager.open("capacitor.plugins.json")));
            try {
                StringBuilder builder = new StringBuilder();
                while (true) {
                    String line = reader.readLine();
                    if (line != null) {
                        builder.append(line);
                    } else {
                        String jsonString = builder.toString();
                        JSONArray jSONArray = new JSONArray(jsonString);
                        reader.close();
                        return jSONArray;
                    }
                    throw new PluginLoadException("Could not load capacitor.plugins.json");
                }
            } catch (Throwable th) {
                try {
                    reader.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        } catch (IOException e) {
            throw new PluginLoadException("Could not load capacitor.plugins.json");
        } catch (JSONException e2) {
            throw new PluginLoadException("Could not parse capacitor.plugins.json as JSON");
        }
    }
}
