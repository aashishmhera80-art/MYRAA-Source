package org.apache.cordova;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import androidx.appcompat.app.AppCompatActivity;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class CordovaInterfaceImpl implements CordovaInterface {
    private static final String TAG = "CordovaInterfaceImpl";
    protected AppCompatActivity activity;
    protected CordovaPlugin activityResultCallback;
    protected int activityResultRequestCode;
    protected boolean activityWasDestroyed;
    protected String initCallbackService;
    protected CallbackMap permissionResultCallbacks;
    protected PluginManager pluginManager;
    protected Bundle savedPluginState;
    protected ActivityResultHolder savedResult;
    protected ExecutorService threadPool;

    public CordovaInterfaceImpl(AppCompatActivity activity) {
        this(activity, Executors.newCachedThreadPool());
    }

    public CordovaInterfaceImpl(AppCompatActivity activity, ExecutorService threadPool) {
        this.activityWasDestroyed = false;
        this.activity = activity;
        this.threadPool = threadPool;
        this.permissionResultCallbacks = new CallbackMap();
    }

    @Override // org.apache.cordova.CordovaInterface
    public void startActivityForResult(CordovaPlugin command, Intent intent, int requestCode) {
        setActivityResultCallback(command);
        try {
            this.activity.startActivityForResult(intent, requestCode);
        } catch (RuntimeException e) {
            this.activityResultCallback = null;
            throw e;
        }
    }

    @Override // org.apache.cordova.CordovaInterface
    public void setActivityResultCallback(CordovaPlugin plugin) {
        if (this.activityResultCallback != null) {
            this.activityResultCallback.onActivityResult(this.activityResultRequestCode, 0, null);
        }
        this.activityResultCallback = plugin;
    }

    @Override // org.apache.cordova.CordovaInterface
    public AppCompatActivity getActivity() {
        return this.activity;
    }

    @Override // org.apache.cordova.CordovaInterface
    public Context getContext() {
        return this.activity;
    }

    @Override // org.apache.cordova.CordovaInterface
    public Object onMessage(String id, Object data) {
        if ("exit".equals(id)) {
            this.activity.finish();
            return null;
        }
        return null;
    }

    @Override // org.apache.cordova.CordovaInterface
    public ExecutorService getThreadPool() {
        return this.threadPool;
    }

    public void onCordovaInit(PluginManager pluginManager) {
        CoreAndroid appPlugin;
        this.pluginManager = pluginManager;
        if (this.savedResult != null) {
            onActivityResult(this.savedResult.requestCode, this.savedResult.resultCode, this.savedResult.intent);
            return;
        }
        if (this.activityWasDestroyed) {
            this.activityWasDestroyed = false;
            if (pluginManager != null && (appPlugin = (CoreAndroid) pluginManager.getPlugin(CoreAndroid.PLUGIN_NAME)) != null) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("action", "resume");
                } catch (JSONException e) {
                    LOG.e(TAG, "Failed to create event message", e);
                }
                appPlugin.sendResumeEvent(new PluginResult(PluginResult.Status.OK, obj));
            }
        }
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent intent) {
        CordovaPlugin callback = this.activityResultCallback;
        if (callback == null && this.initCallbackService != null) {
            this.savedResult = new ActivityResultHolder(requestCode, resultCode, intent);
            if (this.pluginManager != null && (callback = this.pluginManager.getPlugin(this.initCallbackService)) != null) {
                callback.onRestoreStateForActivityResult(this.savedPluginState.getBundle(callback.getServiceName()), new ResumeCallback(callback.getServiceName(), this.pluginManager));
            }
        }
        this.activityResultCallback = null;
        if (callback != null) {
            LOG.d(TAG, "Sending activity result to plugin");
            this.initCallbackService = null;
            this.savedResult = null;
            callback.onActivityResult(requestCode, resultCode, intent);
            return true;
        }
        LOG.w(TAG, "Got an activity result, but no plugin was registered to receive it" + (this.savedResult != null ? " yet!" : "."));
        return false;
    }

    public void setActivityResultRequestCode(int requestCode) {
        this.activityResultRequestCode = requestCode;
    }

    public void onSaveInstanceState(Bundle outState) {
        if (this.activityResultCallback != null) {
            String serviceName = this.activityResultCallback.getServiceName();
            outState.putString("callbackService", serviceName);
        }
        if (this.pluginManager != null) {
            outState.putBundle("plugin", this.pluginManager.onSaveInstanceState());
        }
    }

    public void restoreInstanceState(Bundle savedInstanceState) {
        this.initCallbackService = savedInstanceState.getString("callbackService");
        this.savedPluginState = savedInstanceState.getBundle("plugin");
        this.activityWasDestroyed = true;
    }

    private static class ActivityResultHolder {
        private Intent intent;
        private int requestCode;
        private int resultCode;

        public ActivityResultHolder(int requestCode, int resultCode, Intent intent) {
            this.requestCode = requestCode;
            this.resultCode = resultCode;
            this.intent = intent;
        }
    }

    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
        Pair<CordovaPlugin, Integer> callback = this.permissionResultCallbacks.getAndRemoveCallback(requestCode);
        if (callback != null) {
            ((CordovaPlugin) callback.first).onRequestPermissionResult(((Integer) callback.second).intValue(), permissions, grantResults);
        }
    }

    @Override // org.apache.cordova.CordovaInterface
    public void requestPermission(CordovaPlugin plugin, int requestCode, String permission) {
        String[] permissions = {permission};
        requestPermissions(plugin, requestCode, permissions);
    }

    @Override // org.apache.cordova.CordovaInterface
    public void requestPermissions(CordovaPlugin plugin, int requestCode, String[] permissions) {
        int mappedRequestCode = this.permissionResultCallbacks.registerCallback(plugin, requestCode);
        getActivity().requestPermissions(permissions, mappedRequestCode);
    }

    @Override // org.apache.cordova.CordovaInterface
    public boolean hasPermission(String permission) {
        return this.activity.checkSelfPermission(permission) == 0;
    }
}
