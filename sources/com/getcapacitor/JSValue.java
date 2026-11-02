package com.getcapacitor;

import org.json.JSONException;

/* JADX INFO: loaded from: classes3.dex */
public class JSValue {
    private final Object value;

    public JSValue(PluginCall call, String name) {
        this.value = toValue(call, name);
    }

    public Object getValue() {
        return this.value;
    }

    public String toString() {
        return getValue().toString();
    }

    public JSObject toJSObject() throws JSONException {
        if (this.value instanceof JSObject) {
            return (JSObject) this.value;
        }
        throw new JSONException("JSValue could not be coerced to JSObject.");
    }

    public JSArray toJSArray() throws JSONException {
        if (this.value instanceof JSArray) {
            return (JSArray) this.value;
        }
        throw new JSONException("JSValue could not be coerced to JSArray.");
    }

    private Object toValue(PluginCall call, String name) {
        Object value = call.getArray(name, null);
        if (value != null) {
            return value;
        }
        Object value2 = call.getObject(name, null);
        if (value2 != null) {
            return value2;
        }
        Object value3 = call.getString(name, null);
        return value3 != null ? value3 : call.getData().opt(name);
    }
}
