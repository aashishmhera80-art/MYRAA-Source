package com.getcapacitor.cordova;

import android.util.Pair;
import androidx.appcompat.app.AppCompatActivity;
import java.util.concurrent.Executors;
import org.apache.cordova.CordovaInterfaceImpl;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONException;

/* JADX INFO: loaded from: classes2.dex */
public class MockCordovaInterfaceImpl extends CordovaInterfaceImpl {
    public MockCordovaInterfaceImpl(AppCompatActivity activity) {
        super(activity, Executors.newCachedThreadPool());
    }

    public CordovaPlugin getActivityResultCallback() {
        return this.activityResultCallback;
    }

    public boolean handlePermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
        Pair<CordovaPlugin, Integer> callback = this.permissionResultCallbacks.getAndRemoveCallback(requestCode);
        if (callback != null) {
            ((CordovaPlugin) callback.first).onRequestPermissionResult(((Integer) callback.second).intValue(), permissions, grantResults);
            return true;
        }
        return false;
    }
}
