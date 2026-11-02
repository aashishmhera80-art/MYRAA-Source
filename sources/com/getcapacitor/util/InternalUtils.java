package com.getcapacitor.util;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

/* JADX INFO: loaded from: classes2.dex */
public class InternalUtils {
    public static PackageInfo getPackageInfo(PackageManager pm, String packageName) throws PackageManager.NameNotFoundException {
        return getPackageInfo(pm, packageName, 0L);
    }

    public static PackageInfo getPackageInfo(PackageManager pm, String packageName, long flags) throws PackageManager.NameNotFoundException {
        if (Build.VERSION.SDK_INT >= 33) {
            return pm.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(flags));
        }
        return getPackageInfoLegacy(pm, packageName, (int) flags);
    }

    private static PackageInfo getPackageInfoLegacy(PackageManager pm, String packageName, long flags) throws PackageManager.NameNotFoundException {
        return pm.getPackageInfo(packageName, (int) flags);
    }
}
