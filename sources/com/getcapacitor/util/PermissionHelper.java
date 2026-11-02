package com.getcapacitor.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class PermissionHelper {
    public static boolean hasPermissions(Context context, String[] permissions) {
        for (String perm : permissions) {
            if (ActivityCompat.checkSelfPermission(context, perm) != 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasDefinedPermission(Context context, String permission) {
        String[] requestedPermissions = getManifestPermissions(context);
        if (requestedPermissions == null || requestedPermissions.length <= 0) {
            return false;
        }
        List<String> requestedPermissionsList = Arrays.asList(requestedPermissions);
        ArrayList<String> requestedPermissionsArrayList = new ArrayList<>(requestedPermissionsList);
        if (!requestedPermissionsArrayList.contains(permission)) {
            return false;
        }
        return true;
    }

    public static boolean hasDefinedPermissions(Context context, String[] permissions) {
        for (String permission : permissions) {
            if (!hasDefinedPermission(context, permission)) {
                return false;
            }
        }
        return true;
    }

    public static String[] getManifestPermissions(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = InternalUtils.getPackageInfo(pm, context.getPackageName(), 4096L);
            if (packageInfo == null) {
                return null;
            }
            String[] requestedPermissions = packageInfo.requestedPermissions;
            return requestedPermissions;
        } catch (Exception e) {
            return null;
        }
    }

    public static String[] getUndefinedPermissions(Context context, String[] neededPermissions) {
        ArrayList<String> undefinedPermissions = new ArrayList<>();
        String[] requestedPermissions = getManifestPermissions(context);
        if (requestedPermissions != null && requestedPermissions.length > 0) {
            List<String> requestedPermissionsList = Arrays.asList(requestedPermissions);
            ArrayList<String> requestedPermissionsArrayList = new ArrayList<>(requestedPermissionsList);
            for (String permission : neededPermissions) {
                if (!requestedPermissionsArrayList.contains(permission)) {
                    undefinedPermissions.add(permission);
                }
            }
            String[] undefinedPermissionArray = new String[undefinedPermissions.size()];
            return (String[]) undefinedPermissions.toArray(undefinedPermissionArray);
        }
        return neededPermissions;
    }
}
