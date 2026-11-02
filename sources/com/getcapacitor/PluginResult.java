package com.getcapacitor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes3.dex */
public class PluginResult {
    private final JSObject json;

    public PluginResult() {
        this(new JSObject());
    }

    public PluginResult(JSObject json) {
        this.json = json;
    }

    public PluginResult put(String name, boolean value) {
        return jsonPut(name, Boolean.valueOf(value));
    }

    public PluginResult put(String name, double value) {
        return jsonPut(name, Double.valueOf(value));
    }

    public PluginResult put(String name, int value) {
        return jsonPut(name, Integer.valueOf(value));
    }

    public PluginResult put(String name, long value) {
        return jsonPut(name, Long.valueOf(value));
    }

    public PluginResult put(String name, Date value) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        df.setTimeZone(tz);
        return jsonPut(name, df.format(value));
    }

    public PluginResult put(String name, Object value) {
        return jsonPut(name, value);
    }

    public PluginResult put(String name, PluginResult value) {
        return jsonPut(name, value.json);
    }

    PluginResult jsonPut(String name, Object value) {
        try {
            this.json.put(name, value);
        } catch (Exception ex) {
            Logger.error(Logger.tags("Plugin"), "", ex);
        }
        return this;
    }

    public String toString() {
        return this.json.toString();
    }

    public JSObject getWrappedResult() {
        JSObject ret = new JSObject();
        ret.put("pluginId", this.json.getString("pluginId"));
        ret.put("methodName", this.json.getString("methodName"));
        ret.put("success", (Object) this.json.getBoolean("success", false));
        ret.put("data", (Object) this.json.getJSObject("data"));
        ret.put("error", (Object) this.json.getJSObject("error"));
        return ret;
    }
}
