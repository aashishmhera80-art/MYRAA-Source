package androidx.webkit;

import android.content.Context;
import androidx.lifecycle.LifecycleKt$$ExternalSyntheticBackportWithForwarding0;
import androidx.webkit.internal.ApiHelperForP;
import androidx.webkit.internal.StartupApiFeature;
import androidx.webkit.internal.WebViewFeatureInternal;
import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;
import org.chromium.support_lib_boundary.ProcessGlobalConfigConstants;

/* JADX INFO: loaded from: classes.dex */
public class ProcessGlobalConfig {
    String mCacheDirectoryBasePath;
    String mDataDirectoryBasePath;
    String mDataDirectorySuffix;
    Boolean mPartitionedCookiesEnabled;
    private static final AtomicReference<HashMap<String, Object>> sProcessGlobalConfig = new AtomicReference<>();
    private static final Object sLock = new Object();
    private static boolean sApplyCalled = false;

    public ProcessGlobalConfig setDataDirectorySuffix(Context context, String suffix) {
        StartupApiFeature.P feature = WebViewFeatureInternal.STARTUP_FEATURE_SET_DATA_DIRECTORY_SUFFIX;
        if (!feature.isSupported(context)) {
            throw WebViewFeatureInternal.getUnsupportedOperationException();
        }
        if (suffix.equals("")) {
            throw new IllegalArgumentException("Suffix cannot be an empty string");
        }
        if (suffix.indexOf(File.separatorChar) >= 0) {
            throw new IllegalArgumentException("Suffix " + suffix + " contains a path separator");
        }
        this.mDataDirectorySuffix = suffix;
        return this;
    }

    public ProcessGlobalConfig setDirectoryBasePaths(Context context, File dataDirectoryBasePath, File cacheDirectoryBasePath) {
        StartupApiFeature.NoFramework feature = WebViewFeatureInternal.STARTUP_FEATURE_SET_DIRECTORY_BASE_PATH;
        if (!feature.isSupported(context)) {
            throw WebViewFeatureInternal.getUnsupportedOperationException();
        }
        if (!dataDirectoryBasePath.isAbsolute()) {
            throw new IllegalArgumentException("dataDirectoryBasePath must be a non-empty absolute path");
        }
        if (!cacheDirectoryBasePath.isAbsolute()) {
            throw new IllegalArgumentException("cacheDirectoryBasePath must be a non-empty absolute path");
        }
        this.mDataDirectoryBasePath = dataDirectoryBasePath.getAbsolutePath();
        this.mCacheDirectoryBasePath = cacheDirectoryBasePath.getAbsolutePath();
        return this;
    }

    public ProcessGlobalConfig setPartitionedCookiesEnabled(Context context, boolean isEnabled) {
        StartupApiFeature.NoFramework feature = WebViewFeatureInternal.STARTUP_FEATURE_CONFIGURE_PARTITIONED_COOKIES;
        if (!feature.isSupported(context)) {
            throw WebViewFeatureInternal.getUnsupportedOperationException();
        }
        this.mPartitionedCookiesEnabled = Boolean.valueOf(isEnabled);
        return this;
    }

    public static void apply(ProcessGlobalConfig config) {
        synchronized (sLock) {
            if (sApplyCalled) {
                throw new IllegalStateException("ProcessGlobalConfig#apply was called more than once, which is an illegal operation. The configuration settings provided by ProcessGlobalConfig take effect only once, when WebView is first loaded into the current process. Every process should only ever create a single instance of ProcessGlobalConfig and apply it once, before any calls to android.webkit APIs, such as during early app startup.");
            }
            sApplyCalled = true;
        }
        HashMap<String, Object> configMap = new HashMap<>();
        if (webViewCurrentlyLoaded()) {
            throw new IllegalStateException("WebView has already been loaded in the current process, so any attempt to apply the settings in ProcessGlobalConfig will have no effect. ProcessGlobalConfig#apply needs to be called before any calls to android.webkit APIs, such as during early app startup.");
        }
        if (config.mDataDirectorySuffix != null) {
            StartupApiFeature.P feature = WebViewFeatureInternal.STARTUP_FEATURE_SET_DATA_DIRECTORY_SUFFIX;
            if (feature.isSupportedByFramework()) {
                ApiHelperForP.setDataDirectorySuffix(config.mDataDirectorySuffix);
            } else {
                configMap.put(ProcessGlobalConfigConstants.DATA_DIRECTORY_SUFFIX, config.mDataDirectorySuffix);
            }
        }
        if (config.mDataDirectoryBasePath != null) {
            configMap.put(ProcessGlobalConfigConstants.DATA_DIRECTORY_BASE_PATH, config.mDataDirectoryBasePath);
        }
        if (config.mCacheDirectoryBasePath != null) {
            configMap.put(ProcessGlobalConfigConstants.CACHE_DIRECTORY_BASE_PATH, config.mCacheDirectoryBasePath);
        }
        if (config.mPartitionedCookiesEnabled != null) {
            configMap.put(ProcessGlobalConfigConstants.CONFIGURE_PARTITIONED_COOKIES, config.mPartitionedCookiesEnabled);
        }
        if (!LifecycleKt$$ExternalSyntheticBackportWithForwarding0.m(sProcessGlobalConfig, null, configMap)) {
            throw new RuntimeException("Attempting to set ProcessGlobalConfig#sProcessGlobalConfig when it was already set");
        }
    }

    private static boolean webViewCurrentlyLoaded() {
        try {
            Class<?> webViewFactoryClass = Class.forName("android.webkit.WebViewFactory");
            Field providerInstanceField = webViewFactoryClass.getDeclaredField("sProviderInstance");
            providerInstanceField.setAccessible(true);
            if (providerInstanceField.get(null) == null) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
