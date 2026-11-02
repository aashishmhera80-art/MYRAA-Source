package org.apache.cordova;

import android.content.Context;
import java.lang.reflect.Field;

/* JADX INFO: loaded from: classes.dex */
public class BuildHelper {
    private static String TAG = "BuildHelper";

    public static Object getBuildConfigValue(Context ctx, String key) {
        try {
            String packageName = ctx.getApplicationInfo().packageName;
            Class<?> clazz = Class.forName(packageName + ".BuildConfig");
            Field field = clazz.getField(key);
            return field.get(null);
        } catch (ClassNotFoundException e) {
            LOG.d(TAG, "Unable to get the BuildConfig, is this built with ANT?");
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e2) {
            LOG.d(TAG, "Illegal Access Exception: Let's print a stack trace.");
            e2.printStackTrace();
            return null;
        } catch (NoSuchFieldException e3) {
            LOG.d(TAG, key + " is not a valid field. Check your build.gradle");
            return null;
        } catch (NullPointerException e4) {
            LOG.d(TAG, "Null Pointer Exception: Let's print a stack trace.");
            e4.printStackTrace();
            return null;
        }
    }
}
