package com.getcapacitor;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes3.dex */
public class PluginCall {
    public static final String CALLBACK_ID_DANGLING = "-1";
    private final String callbackId;
    private final JSObject data;
    private final String methodName;
    private final MessageHandler msgHandler;
    private final String pluginId;
    private boolean keepAlive = false;

    @Deprecated
    private boolean isReleased = false;

    public PluginCall(MessageHandler msgHandler, String pluginId, String callbackId, String methodName, JSObject data) {
        this.msgHandler = msgHandler;
        this.pluginId = pluginId;
        this.callbackId = callbackId;
        this.methodName = methodName;
        this.data = data;
    }

    public void successCallback(PluginResult successResult) {
        if (CALLBACK_ID_DANGLING.equals(this.callbackId)) {
            return;
        }
        this.msgHandler.sendResponseMessage(this, successResult, null);
    }

    public void resolve(JSObject data) {
        PluginResult result = new PluginResult(data);
        this.msgHandler.sendResponseMessage(this, result, null);
    }

    public void resolve() {
        this.msgHandler.sendResponseMessage(this, null, null);
    }

    public void errorCallback(String msg) {
        PluginResult errorResult = new PluginResult();
        try {
            errorResult.put("message", msg);
        } catch (Exception jsonEx) {
            Logger.error(Logger.tags("Plugin"), jsonEx.toString(), null);
        }
        this.msgHandler.sendResponseMessage(this, null, errorResult);
    }

    public void reject(String msg, String code, Exception ex, JSObject data) {
        PluginResult errorResult = new PluginResult();
        if (ex != null) {
            Logger.error(Logger.tags("Plugin"), msg, ex);
        }
        try {
            errorResult.put("message", msg);
            errorResult.put("code", code);
            if (data != null) {
                errorResult.put("data", data);
            }
        } catch (Exception jsonEx) {
            Logger.error(Logger.tags("Plugin"), jsonEx.getMessage(), jsonEx);
        }
        this.msgHandler.sendResponseMessage(this, null, errorResult);
    }

    public void reject(String msg, Exception ex, JSObject data) {
        reject(msg, null, ex, data);
    }

    public void reject(String msg, String code, JSObject data) {
        reject(msg, code, null, data);
    }

    public void reject(String msg, String code, Exception ex) {
        reject(msg, code, ex, null);
    }

    public void reject(String msg, JSObject data) {
        reject(msg, null, null, data);
    }

    public void reject(String msg, Exception ex) {
        reject(msg, null, ex, null);
    }

    public void reject(String msg, String code) {
        reject(msg, code, null, null);
    }

    public void reject(String msg) {
        reject(msg, null, null, null);
    }

    public void unimplemented() {
        unimplemented("not implemented");
    }

    public void unimplemented(String msg) {
        reject(msg, "UNIMPLEMENTED", null, null);
    }

    public void unavailable() {
        unavailable("not available");
    }

    public void unavailable(String msg) {
        reject(msg, "UNAVAILABLE", null, null);
    }

    public String getPluginId() {
        return this.pluginId;
    }

    public String getCallbackId() {
        return this.callbackId;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public JSObject getData() {
        return this.data;
    }

    public String getString(String name) {
        return getString(name, null);
    }

    public String getString(String name, String defaultValue) {
        Object value = this.data.opt(name);
        if (value != null && (value instanceof String)) {
            return (String) value;
        }
        return defaultValue;
    }

    public Integer getInt(String name) {
        return getInt(name, null);
    }

    public Integer getInt(String name, Integer defaultValue) {
        Object value = this.data.opt(name);
        if (value != null && (value instanceof Integer)) {
            return (Integer) value;
        }
        return defaultValue;
    }

    public Long getLong(String name) {
        return getLong(name, null);
    }

    public Long getLong(String name, Long defaultValue) {
        Object value = this.data.opt(name);
        if (value != null && (value instanceof Long)) {
            return (Long) value;
        }
        return defaultValue;
    }

    public Float getFloat(String name) {
        return getFloat(name, null);
    }

    public Float getFloat(String name, Float defaultValue) {
        Object value = this.data.opt(name);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Float) {
            return (Float) value;
        }
        if (value instanceof Double) {
            return Float.valueOf(((Double) value).floatValue());
        }
        if (value instanceof Integer) {
            return Float.valueOf(((Integer) value).floatValue());
        }
        return defaultValue;
    }

    public Double getDouble(String name) {
        return getDouble(name, null);
    }

    public Double getDouble(String name, Double defaultValue) {
        Object value = this.data.opt(name);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Double) {
            return (Double) value;
        }
        if (value instanceof Float) {
            return Double.valueOf(((Float) value).doubleValue());
        }
        if (value instanceof Integer) {
            return Double.valueOf(((Integer) value).doubleValue());
        }
        return defaultValue;
    }

    public Boolean getBoolean(String name) {
        return getBoolean(name, null);
    }

    public Boolean getBoolean(String name, Boolean defaultValue) {
        Object value = this.data.opt(name);
        if (value != null && (value instanceof Boolean)) {
            return (Boolean) value;
        }
        return defaultValue;
    }

    public JSObject getObject(String name) {
        return getObject(name, null);
    }

    public JSObject getObject(String name, JSObject defaultValue) {
        Object value = this.data.opt(name);
        if (value != null && (value instanceof JSONObject)) {
            try {
                return JSObject.fromJSONObject((JSONObject) value);
            } catch (JSONException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public JSArray getArray(String name) {
        return getArray(name, null);
    }

    public JSArray getArray(String name, JSArray defaultValue) {
        Object value = this.data.opt(name);
        if (value != null && (value instanceof JSONArray)) {
            try {
                JSONArray valueArray = (JSONArray) value;
                List<Object> items = new ArrayList<>();
                for (int i = 0; i < valueArray.length(); i++) {
                    items.add(valueArray.get(i));
                }
                return new JSArray(items.toArray());
            } catch (JSONException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    @Deprecated
    public boolean hasOption(String name) {
        return this.data.has(name);
    }

    @Deprecated
    public void save() {
        setKeepAlive(true);
    }

    public void setKeepAlive(Boolean keepAlive) {
        this.keepAlive = keepAlive.booleanValue();
    }

    public void release(Bridge bridge) {
        this.keepAlive = false;
        bridge.releaseCall(this);
        this.isReleased = true;
    }

    @Deprecated
    public boolean isSaved() {
        return isKeptAlive();
    }

    public boolean isKeptAlive() {
        return this.keepAlive;
    }

    @Deprecated
    public boolean isReleased() {
        return this.isReleased;
    }

    class PluginCallDataTypeException extends Exception {
        PluginCallDataTypeException(String m) {
            super(m);
        }
    }
}
