package com.getcapacitor.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class JSONUtils {
    public static String getString(JSONObject jsonObject, String key, String defaultValue) {
        String k = getDeepestKey(key);
        try {
            JSONObject o = getDeepestObject(jsonObject, key);
            String value = o.getString(k);
            if (value == null) {
                return defaultValue;
            }
            return value;
        } catch (JSONException e) {
            return defaultValue;
        }
    }

    public static boolean getBoolean(JSONObject jsonObject, String key, boolean defaultValue) {
        String k = getDeepestKey(key);
        try {
            JSONObject o = getDeepestObject(jsonObject, key);
            return o.getBoolean(k);
        } catch (JSONException e) {
            return defaultValue;
        }
    }

    public static int getInt(JSONObject jsonObject, String key, int defaultValue) {
        String k = getDeepestKey(key);
        try {
            JSONObject o = getDeepestObject(jsonObject, key);
            return o.getInt(k);
        } catch (JSONException e) {
            return defaultValue;
        }
    }

    public static double getDouble(JSONObject jsonObject, String key, double defaultValue) {
        String k = getDeepestKey(key);
        try {
            JSONObject o = getDeepestObject(jsonObject, key);
            return o.getDouble(k);
        } catch (JSONException e) {
            return defaultValue;
        }
    }

    public static JSONObject getObject(JSONObject jsonObject, String key) {
        String k = getDeepestKey(key);
        try {
            JSONObject o = getDeepestObject(jsonObject, key);
            return o.getJSONObject(k);
        } catch (JSONException e) {
            return null;
        }
    }

    public static String[] getArray(JSONObject jsonObject, String key, String[] defaultValue) {
        String k = getDeepestKey(key);
        try {
            JSONObject o = getDeepestObject(jsonObject, key);
            JSONArray a = o.getJSONArray(k);
            if (a == null) {
                return defaultValue;
            }
            int l = a.length();
            String[] value = new String[l];
            for (int i = 0; i < l; i++) {
                value[i] = (String) a.get(i);
            }
            return value;
        } catch (JSONException e) {
            return defaultValue;
        }
    }

    private static String getDeepestKey(String key) {
        String[] parts = key.split("\\.");
        if (parts.length > 0) {
            return parts[parts.length - 1];
        }
        return null;
    }

    private static JSONObject getDeepestObject(JSONObject jsonObject, String key) throws JSONException {
        String[] parts = key.split("\\.");
        JSONObject o = jsonObject;
        for (int i = 0; i < parts.length - 1; i++) {
            String k = parts[i];
            o = o.getJSONObject(k);
        }
        return o;
    }
}
