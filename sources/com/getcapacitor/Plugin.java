package com.getcapacitor;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.getcapacitor.annotation.ActivityCallback;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;
import com.getcapacitor.util.PermissionHelper;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import org.json.JSONException;

/* JADX INFO: loaded from: classes3.dex */
public class Plugin {
    private static final String BUNDLE_PERSISTED_OPTIONS_JSON_KEY = "_json";
    protected Bridge bridge;
    protected PluginHandle handle;
    private String lastPluginCallId;

    @Deprecated
    protected PluginCall savedLastCall;
    private final Map<String, ActivityResultLauncher<Intent>> activityLaunchers = new HashMap();
    private final Map<String, ActivityResultLauncher<String[]>> permissionLaunchers = new HashMap();
    private final Map<String, List<PluginCall>> eventListeners = new HashMap();
    private final Map<String, List<JSObject>> retainedEventArguments = new HashMap();

    public void load() {
    }

    void initializeActivityLaunchers() {
        List<Method> pluginClassMethods = new ArrayList<>();
        for (Class<?> pluginCursor = getClass(); !pluginCursor.getName().equals(Object.class.getName()); pluginCursor = pluginCursor.getSuperclass()) {
            pluginClassMethods.addAll(Arrays.asList(pluginCursor.getDeclaredMethods()));
        }
        for (final Method method : pluginClassMethods) {
            if (method.isAnnotationPresent(ActivityCallback.class)) {
                ActivityResultLauncher<Intent> launcher = this.bridge.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback() { // from class: com.getcapacitor.Plugin$$ExternalSyntheticLambda0
                    @Override // androidx.activity.result.ActivityResultCallback
                    public final void onActivityResult(Object obj) {
                        this.f$0.lambda$initializeActivityLaunchers$0(method, (ActivityResult) obj);
                    }
                });
                this.activityLaunchers.put(method.getName(), launcher);
            } else if (method.isAnnotationPresent(PermissionCallback.class)) {
                ActivityResultLauncher<String[]> launcher2 = this.bridge.registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback() { // from class: com.getcapacitor.Plugin$$ExternalSyntheticLambda1
                    @Override // androidx.activity.result.ActivityResultCallback
                    public final void onActivityResult(Object obj) {
                        this.f$0.lambda$initializeActivityLaunchers$1(method, (Map) obj);
                    }
                });
                this.permissionLaunchers.put(method.getName(), launcher2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: triggerPermissionCallback, reason: merged with bridge method [inline-methods] */
    public void lambda$initializeActivityLaunchers$1(Method method, Map<String, Boolean> permissionResultMap) {
        PluginCall savedCall = this.bridge.getPermissionCall(this.handle.getId());
        if (this.bridge.validatePermissions(this, savedCall, permissionResultMap)) {
            try {
                method.setAccessible(true);
                method.invoke(this, savedCall);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: triggerActivityCallback, reason: merged with bridge method [inline-methods] */
    public void lambda$initializeActivityLaunchers$0(Method method, ActivityResult result) {
        PluginCall savedCall = this.bridge.getSavedCall(this.lastPluginCallId);
        if (savedCall == null) {
            savedCall = this.bridge.getPluginCallForLastActivity();
        }
        try {
            method.setAccessible(true);
            method.invoke(this, savedCall, result);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void startActivityForResult(PluginCall call, Intent intent, String callbackName) {
        ActivityResultLauncher<Intent> activityResultLauncher = getActivityLauncherOrReject(call, callbackName);
        if (activityResultLauncher == null) {
            return;
        }
        this.bridge.setPluginCallForLastActivity(call);
        this.lastPluginCallId = call.getCallbackId();
        this.bridge.saveCall(call);
        activityResultLauncher.launch(intent);
    }

    private void permissionActivityResult(PluginCall call, String[] permissionStrings, String callbackName) {
        ActivityResultLauncher<String[]> permissionResultLauncher = getPermissionLauncherOrReject(call, callbackName);
        if (permissionResultLauncher == null) {
            return;
        }
        this.bridge.savePermissionCall(call);
        permissionResultLauncher.launch(permissionStrings);
    }

    public Context getContext() {
        return this.bridge.getContext();
    }

    public AppCompatActivity getActivity() {
        return this.bridge.getActivity();
    }

    public void setBridge(Bridge bridge) {
        this.bridge = bridge;
    }

    public Bridge getBridge() {
        return this.bridge;
    }

    public void setPluginHandle(PluginHandle pluginHandle) {
        this.handle = pluginHandle;
    }

    public PluginHandle getPluginHandle() {
        return this.handle;
    }

    public String getAppId() {
        return getContext().getPackageName();
    }

    @Deprecated
    public void saveCall(PluginCall lastCall) {
        this.savedLastCall = lastCall;
    }

    @Deprecated
    public void freeSavedCall() {
        this.savedLastCall.release(this.bridge);
        this.savedLastCall = null;
    }

    @Deprecated
    public PluginCall getSavedCall() {
        return this.savedLastCall;
    }

    public PluginConfig getConfig() {
        return this.bridge.getConfig().getPluginConfiguration(this.handle.getId());
    }

    @Deprecated
    public Object getConfigValue(String key) {
        try {
            PluginConfig pluginConfig = getConfig();
            return pluginConfig.getConfigJSON().get(key);
        } catch (JSONException e) {
            return null;
        }
    }

    @Deprecated
    public boolean hasDefinedPermissions(String[] permissions) {
        for (String permission : permissions) {
            if (!PermissionHelper.hasDefinedPermission(getContext(), permission)) {
                return false;
            }
        }
        return true;
    }

    @Deprecated
    public boolean hasDefinedRequiredPermissions() {
        CapacitorPlugin annotation = this.handle.getPluginAnnotation();
        if (annotation == null) {
            NativePlugin legacyAnnotation = this.handle.getLegacyPluginAnnotation();
            return hasDefinedPermissions(legacyAnnotation.permissions());
        }
        for (Permission perm : annotation.permissions()) {
            for (String permString : perm.strings()) {
                if (!PermissionHelper.hasDefinedPermission(getContext(), permString)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isPermissionDeclared(String alias) {
        CapacitorPlugin annotation = this.handle.getPluginAnnotation();
        if (annotation != null) {
            for (Permission perm : annotation.permissions()) {
                if (alias.equalsIgnoreCase(perm.alias())) {
                    boolean result = true;
                    for (String permString : perm.strings()) {
                        result = result && PermissionHelper.hasDefinedPermission(getContext(), permString);
                    }
                    return result;
                }
            }
        }
        Logger.error(String.format("isPermissionDeclared: No alias defined for %s or missing @CapacitorPlugin annotation.", alias));
        return false;
    }

    @Deprecated
    public boolean hasPermission(String permission) {
        return ActivityCompat.checkSelfPermission(getContext(), permission) == 0;
    }

    @Deprecated
    public boolean hasRequiredPermissions() {
        CapacitorPlugin annotation = this.handle.getPluginAnnotation();
        if (annotation == null) {
            NativePlugin legacyAnnotation = this.handle.getLegacyPluginAnnotation();
            for (String perm : legacyAnnotation.permissions()) {
                if (ActivityCompat.checkSelfPermission(getContext(), perm) != 0) {
                    return false;
                }
            }
            return true;
        }
        for (Permission perm2 : annotation.permissions()) {
            for (String permString : perm2.strings()) {
                if (ActivityCompat.checkSelfPermission(getContext(), permString) != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    protected void requestAllPermissions(PluginCall call, String callbackName) {
        CapacitorPlugin annotation = this.handle.getPluginAnnotation();
        if (annotation != null) {
            HashSet<String> perms = new HashSet<>();
            for (Permission perm : annotation.permissions()) {
                perms.addAll(Arrays.asList(perm.strings()));
            }
            permissionActivityResult(call, (String[]) perms.toArray(new String[0]), callbackName);
        }
    }

    protected void requestPermissionForAlias(String alias, PluginCall call, String callbackName) {
        requestPermissionForAliases(new String[]{alias}, call, callbackName);
    }

    protected void requestPermissionForAliases(String[] aliases, PluginCall call, String callbackName) {
        if (aliases.length == 0) {
            Logger.error("No permission alias was provided");
            return;
        }
        String[] permissions = getPermissionStringsForAliases(aliases);
        if (permissions.length > 0) {
            permissionActivityResult(call, permissions, callbackName);
        }
    }

    private String[] getPermissionStringsForAliases(String[] aliases) {
        CapacitorPlugin annotation = this.handle.getPluginAnnotation();
        HashSet<String> perms = new HashSet<>();
        for (Permission perm : annotation.permissions()) {
            if (Arrays.asList(aliases).contains(perm.alias())) {
                perms.addAll(Arrays.asList(perm.strings()));
            }
        }
        return (String[]) perms.toArray(new String[0]);
    }

    private ActivityResultLauncher<Intent> getActivityLauncherOrReject(PluginCall call, String methodName) {
        ActivityResultLauncher<Intent> activityLauncher = this.activityLaunchers.get(methodName);
        if (activityLauncher == null) {
            String registerError = String.format(Locale.US, "There is no ActivityCallback method registered for the name: %s. Please define a callback method annotated with @ActivityCallback that receives arguments: (PluginCall, ActivityResult)", methodName);
            Logger.error(registerError);
            call.reject(registerError);
            return null;
        }
        return activityLauncher;
    }

    private ActivityResultLauncher<String[]> getPermissionLauncherOrReject(PluginCall call, String methodName) {
        ActivityResultLauncher<String[]> permissionLauncher = this.permissionLaunchers.get(methodName);
        if (permissionLauncher == null) {
            String registerError = String.format(Locale.US, "There is no PermissionCallback method registered for the name: %s. Please define a callback method annotated with @PermissionCallback that receives arguments: (PluginCall)", methodName);
            Logger.error(registerError);
            call.reject(registerError);
            return null;
        }
        return permissionLauncher;
    }

    @Deprecated
    public void pluginRequestAllPermissions() {
        NativePlugin legacyAnnotation = this.handle.getLegacyPluginAnnotation();
        ActivityCompat.requestPermissions(getActivity(), legacyAnnotation.permissions(), legacyAnnotation.permissionRequestCode());
    }

    @Deprecated
    public void pluginRequestPermission(String permission, int requestCode) {
        ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);
    }

    @Deprecated
    public void pluginRequestPermissions(String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(getActivity(), permissions, requestCode);
    }

    public PermissionState getPermissionState(String alias) {
        return getPermissionStates().get(alias);
    }

    public Map<String, PermissionState> getPermissionStates() {
        return this.bridge.getPermissionStates(this);
    }

    private void addEventListener(String eventName, PluginCall call) {
        List<PluginCall> listeners = this.eventListeners.get(eventName);
        if (listeners == null || listeners.isEmpty()) {
            List<PluginCall> listeners2 = new ArrayList<>();
            this.eventListeners.put(eventName, listeners2);
            listeners2.add(call);
            sendRetainedArgumentsForEvent(eventName);
            return;
        }
        listeners.add(call);
    }

    private void removeEventListener(String eventName, PluginCall call) {
        List<PluginCall> listeners = this.eventListeners.get(eventName);
        if (listeners == null) {
            return;
        }
        listeners.remove(call);
    }

    protected void notifyListeners(String eventName, JSObject data, boolean retainUntilConsumed) {
        Logger.verbose(getLogTag(), "Notifying listeners for event " + eventName);
        List<PluginCall> listeners = this.eventListeners.get(eventName);
        if (listeners == null || listeners.isEmpty()) {
            Logger.debug(getLogTag(), "No listeners found for event " + eventName);
            if (retainUntilConsumed) {
                List<JSObject> argList = this.retainedEventArguments.get(eventName);
                if (argList == null) {
                    argList = new ArrayList();
                }
                argList.add(data);
                this.retainedEventArguments.put(eventName, argList);
                return;
            }
            return;
        }
        CopyOnWriteArrayList<PluginCall> listenersCopy = new CopyOnWriteArrayList<>(listeners);
        for (PluginCall call : listenersCopy) {
            call.resolve(data);
        }
    }

    protected void notifyListeners(String eventName, JSObject data) {
        notifyListeners(eventName, data, false);
    }

    protected boolean hasListeners(String eventName) {
        List<PluginCall> listeners = this.eventListeners.get(eventName);
        if (listeners == null) {
            return false;
        }
        return !listeners.isEmpty();
    }

    private void sendRetainedArgumentsForEvent(String eventName) {
        List<JSObject> retainedArgs = this.retainedEventArguments.get(eventName);
        if (retainedArgs == null) {
            return;
        }
        this.retainedEventArguments.remove(eventName);
        for (JSObject retained : retainedArgs) {
            notifyListeners(eventName, retained);
        }
    }

    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void addListener(PluginCall call) {
        String eventName = call.getString("eventName");
        call.setKeepAlive(true);
        addEventListener(eventName, call);
    }

    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void removeListener(PluginCall call) {
        String eventName = call.getString("eventName");
        String callbackId = call.getString("callbackId");
        PluginCall savedCall = this.bridge.getSavedCall(callbackId);
        if (savedCall != null) {
            removeEventListener(eventName, savedCall);
            this.bridge.releaseCall(savedCall);
        }
    }

    @PluginMethod(returnType = PluginMethod.RETURN_PROMISE)
    public void removeAllListeners(PluginCall call) {
        this.eventListeners.clear();
        call.resolve();
    }

    public void removeAllListeners() {
        this.eventListeners.clear();
    }

    @PluginMethod
    @PermissionCallback
    public void checkPermissions(PluginCall pluginCall) {
        Map<String, PermissionState> permissionsResult = getPermissionStates();
        if (permissionsResult.size() == 0) {
            pluginCall.resolve();
            return;
        }
        JSObject permissionsResultJSON = new JSObject();
        for (Map.Entry<String, PermissionState> entry : permissionsResult.entrySet()) {
            permissionsResultJSON.put(entry.getKey(), (Object) entry.getValue());
        }
        pluginCall.resolve(permissionsResultJSON);
    }

    @PluginMethod
    public void requestPermissions(PluginCall call) throws JSONException {
        CapacitorPlugin annotation = this.handle.getPluginAnnotation();
        if (annotation == null) {
            handleLegacyPermission(call);
            return;
        }
        String[] permAliases = null;
        Set<String> autoGrantPerms = new HashSet<>();
        JSArray providedPerms = call.getArray("permissions");
        List<String> providedPermsList = null;
        if (providedPerms != null) {
            try {
                providedPermsList = providedPerms.toList();
            } catch (JSONException e) {
            }
        }
        Set<String> aliasSet = new HashSet<>();
        if (providedPermsList == null || providedPermsList.isEmpty()) {
            for (Permission perm : annotation.permissions()) {
                if (perm.strings().length == 0 || (perm.strings().length == 1 && perm.strings()[0].isEmpty())) {
                    if (!perm.alias().isEmpty()) {
                        autoGrantPerms.add(perm.alias());
                    }
                } else {
                    aliasSet.add(perm.alias());
                }
            }
            permAliases = (String[]) aliasSet.toArray(new String[0]);
        } else {
            for (Permission perm2 : annotation.permissions()) {
                if (providedPermsList.contains(perm2.alias())) {
                    aliasSet.add(perm2.alias());
                }
            }
            if (aliasSet.isEmpty()) {
                call.reject("No valid permission alias was requested of this plugin.");
            } else {
                permAliases = (String[]) aliasSet.toArray(new String[0]);
            }
        }
        if (permAliases != null && permAliases.length > 0) {
            requestPermissionForAliases(permAliases, call, "checkPermissions");
            return;
        }
        if (!autoGrantPerms.isEmpty()) {
            JSObject permissionsResults = new JSObject();
            Iterator<String> it = autoGrantPerms.iterator();
            while (it.hasNext()) {
                permissionsResults.put(it.next(), PermissionState.GRANTED.toString());
            }
            call.resolve(permissionsResults);
            return;
        }
        call.resolve();
    }

    private void handleLegacyPermission(PluginCall call) {
        NativePlugin legacyAnnotation = this.handle.getLegacyPluginAnnotation();
        String[] perms = legacyAnnotation.permissions();
        if (perms.length > 0) {
            saveCall(call);
            pluginRequestPermissions(perms, legacyAnnotation.permissionRequestCode());
        } else {
            call.resolve();
        }
    }

    @Deprecated
    protected void handleRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (!hasDefinedPermissions(permissions)) {
            StringBuilder builder = new StringBuilder();
            builder.append("Missing the following permissions in AndroidManifest.xml:\n");
            String[] missing = PermissionHelper.getUndefinedPermissions(getContext(), permissions);
            for (String perm : missing) {
                builder.append(perm + "\n");
            }
            this.savedLastCall.reject(builder.toString());
            this.savedLastCall = null;
        }
    }

    protected Bundle saveInstanceState() {
        PluginCall savedCall = this.bridge.getSavedCall(this.lastPluginCallId);
        if (savedCall == null) {
            return null;
        }
        Bundle ret = new Bundle();
        JSObject callData = savedCall.getData();
        if (callData != null) {
            ret.putString(BUNDLE_PERSISTED_OPTIONS_JSON_KEY, callData.toString());
        }
        return ret;
    }

    protected void restoreState(Bundle state) {
    }

    @Deprecated
    protected void handleOnActivityResult(int requestCode, int resultCode, Intent data) {
    }

    protected void handleOnNewIntent(Intent intent) {
    }

    protected void handleOnConfigurationChanged(Configuration newConfig) {
    }

    protected void handleOnStart() {
    }

    protected void handleOnRestart() {
    }

    protected void handleOnResume() {
    }

    protected void handleOnPause() {
    }

    protected void handleOnStop() {
    }

    protected void handleOnDestroy() {
    }

    public Boolean shouldOverrideLoad(Uri url) {
        return null;
    }

    @Deprecated
    protected void startActivityForResult(PluginCall call, Intent intent, int resultCode) {
        this.bridge.startActivityForPluginWithResult(call, intent, resultCode);
    }

    public void execute(Runnable runnable) {
        this.bridge.execute(runnable);
    }

    protected String getLogTag(String... subTags) {
        return Logger.tags(subTags);
    }

    protected String getLogTag() {
        return Logger.tags(getClass().getSimpleName());
    }
}
