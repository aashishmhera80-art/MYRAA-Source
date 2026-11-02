package org.apache.cordova;

import java.util.Arrays;
import org.json.JSONException;

/* JADX INFO: loaded from: classes.dex */
public class PermissionHelper {
    private static final String LOG_TAG = "CordovaPermissionHelper";

    public static void requestPermission(CordovaPlugin plugin, int requestCode, String permission) {
        requestPermissions(plugin, requestCode, new String[]{permission});
    }

    public static void requestPermissions(CordovaPlugin plugin, int requestCode, String[] permissions) {
        plugin.cordova.requestPermissions(plugin, requestCode, permissions);
    }

    public static boolean hasPermission(CordovaPlugin plugin, String permission) {
        return plugin.cordova.hasPermission(permission);
    }

    private static void deliverPermissionResult(CordovaPlugin plugin, int requestCode, String[] permissions) {
        int[] requestResults = new int[permissions.length];
        Arrays.fill(requestResults, 0);
        try {
            plugin.onRequestPermissionResult(requestCode, permissions, requestResults);
            plugin.onRequestPermissionsResult(requestCode, permissions, requestResults);
        } catch (JSONException e) {
            LOG.e(LOG_TAG, "JSONException when delivering permissions results", e);
        }
    }
}
