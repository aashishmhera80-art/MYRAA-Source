package androidx.webkit;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Looper;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.webkit.internal.ApiFeature;
import androidx.webkit.internal.ApiHelperForM;
import androidx.webkit.internal.ApiHelperForO;
import androidx.webkit.internal.ApiHelperForOMR1;
import androidx.webkit.internal.ApiHelperForP;
import androidx.webkit.internal.ApiHelperForQ;
import androidx.webkit.internal.WebMessageAdapter;
import androidx.webkit.internal.WebMessagePortImpl;
import androidx.webkit.internal.WebViewFeatureInternal;
import androidx.webkit.internal.WebViewGlueCommunicator;
import androidx.webkit.internal.WebViewProviderAdapter;
import androidx.webkit.internal.WebViewProviderFactory;
import androidx.webkit.internal.WebViewRenderProcessClientFrameworkAdapter;
import androidx.webkit.internal.WebViewRenderProcessImpl;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;
import org.chromium.support_lib_boundary.WebViewProviderBoundaryInterface;

/* JADX INFO: loaded from: classes.dex */
public class WebViewCompat {
    private static final Uri WILDCARD_URI = Uri.parse(ProxyConfig.MATCH_ALL_SCHEMES);
    private static final Uri EMPTY_URI = Uri.parse("");
    private static boolean sShouldCacheProvider = true;
    private static final WeakHashMap<WebView, WebViewProviderAdapter> sProviderAdapterCache = new WeakHashMap<>();

    @Target({ElementType.METHOD, ElementType.TYPE, ElementType.FIELD})
    @Retention(RetentionPolicy.CLASS)
    public @interface ExperimentalAsyncStartUp {
    }

    @Target({ElementType.METHOD, ElementType.TYPE, ElementType.FIELD})
    @Retention(RetentionPolicy.CLASS)
    public @interface ExperimentalCacheProvider {
    }

    @Target({ElementType.METHOD, ElementType.TYPE, ElementType.FIELD})
    @Retention(RetentionPolicy.CLASS)
    public @interface ExperimentalSaveState {
    }

    @Target({ElementType.METHOD, ElementType.TYPE, ElementType.FIELD})
    @Retention(RetentionPolicy.CLASS)
    public @interface ExperimentalUrlPrerender {
    }

    public interface VisualStateCallback {
        void onComplete(long j);
    }

    public interface WebMessageListener {
        void onPostMessage(WebView webView, WebMessageCompat webMessageCompat, Uri uri, boolean z, JavaScriptReplyProxy javaScriptReplyProxy);
    }

    public interface WebViewStartUpCallback {
        void onSuccess(WebViewStartUpResult webViewStartUpResult);
    }

    private WebViewCompat() {
    }

    public static void postVisualStateCallback(WebView webview, long requestId, VisualStateCallback callback) {
        ApiFeature.M feature = WebViewFeatureInternal.VISUAL_STATE_CALLBACK;
        if (feature.isSupportedByFramework()) {
            ApiHelperForM.postVisualStateCallback(webview, requestId, callback);
        } else {
            if (feature.isSupportedByWebView()) {
                checkThread(webview);
                getProvider(webview).insertVisualStateCallback(requestId, callback);
                return;
            }
            throw WebViewFeatureInternal.getUnsupportedOperationException();
        }
    }

    @Deprecated
    public static void startSafeBrowsing(Context context, ValueCallback<Boolean> callback) {
        ApiFeature.O_MR1 feature = WebViewFeatureInternal.START_SAFE_BROWSING;
        if (feature.isSupportedByFramework()) {
            ApiHelperForOMR1.startSafeBrowsing(context, callback);
        } else {
            if (feature.isSupportedByWebView()) {
                getFactory().getStatics().initSafeBrowsing(context, callback);
                return;
            }
            throw WebViewFeatureInternal.getUnsupportedOperationException();
        }
    }

    public static void setSafeBrowsingAllowlist(Set<String> hosts, ValueCallback<Boolean> callback) {
        ApiFeature.O_MR1 preferredFeature = WebViewFeatureInternal.SAFE_BROWSING_ALLOWLIST_PREFERRED_TO_PREFERRED;
        ApiFeature.O_MR1 deprecatedFeature = WebViewFeatureInternal.SAFE_BROWSING_ALLOWLIST_PREFERRED_TO_DEPRECATED;
        if (preferredFeature.isSupportedByWebView()) {
            getFactory().getStatics().setSafeBrowsingAllowlist(hosts, callback);
            return;
        }
        List<String> hostsList = new ArrayList<>(hosts);
        if (deprecatedFeature.isSupportedByFramework()) {
            ApiHelperForOMR1.setSafeBrowsingWhitelist(hostsList, callback);
        } else {
            if (deprecatedFeature.isSupportedByWebView()) {
                getFactory().getStatics().setSafeBrowsingWhitelist(hostsList, callback);
                return;
            }
            throw WebViewFeatureInternal.getUnsupportedOperationException();
        }
    }

    @Deprecated
    public static void setSafeBrowsingWhitelist(List<String> hosts, ValueCallback<Boolean> callback) {
        setSafeBrowsingAllowlist(new HashSet(hosts), callback);
    }

    public static Uri getSafeBrowsingPrivacyPolicyUrl() {
        ApiFeature.O_MR1 feature = WebViewFeatureInternal.SAFE_BROWSING_PRIVACY_POLICY_URL;
        if (feature.isSupportedByFramework()) {
            return ApiHelperForOMR1.getSafeBrowsingPrivacyPolicyUrl();
        }
        if (feature.isSupportedByWebView()) {
            return getFactory().getStatics().getSafeBrowsingPrivacyPolicyUrl();
        }
        throw WebViewFeatureInternal.getUnsupportedOperationException();
    }

    public static PackageInfo getCurrentWebViewPackage(Context context) {
        PackageInfo info = getCurrentLoadedWebViewPackage();
        return info != null ? info : getNotYetLoadedWebViewPackageInfo(context);
    }

    public static PackageInfo getCurrentLoadedWebViewPackage() {
        if (Build.VERSION.SDK_INT >= 26) {
            return ApiHelperForO.getCurrentWebViewPackage();
        }
        try {
            return getLoadedWebViewPackageInfo();
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            return null;
        }
    }

    private static PackageInfo getLoadedWebViewPackageInfo() throws IllegalAccessException, NoSuchMethodException, ClassNotFoundException, InvocationTargetException {
        Class<?> webViewFactoryClass = Class.forName("android.webkit.WebViewFactory");
        return (PackageInfo) webViewFactoryClass.getMethod("getLoadedPackageInfo", new Class[0]).invoke(null, new Object[0]);
    }

    private static PackageInfo getNotYetLoadedWebViewPackageInfo(Context context) {
        try {
            Class<?> webviewUpdateServiceClass = Class.forName("android.webkit.WebViewUpdateService");
            String webviewPackageName = (String) webviewUpdateServiceClass.getMethod("getCurrentWebViewPackageName", new Class[0]).invoke(null, new Object[0]);
            if (webviewPackageName == null) {
                return null;
            }
            PackageManager pm = context.getPackageManager();
            try {
                return pm.getPackageInfo(webviewPackageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                return null;
            }
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e2) {
            return null;
        }
    }

    private static WebViewProviderAdapter getProvider(WebView webview) {
        ApiFeature.NoFramework feature = WebViewFeatureInternal.CACHE_PROVIDER;
        if (feature.isSupportedByWebView() && sShouldCacheProvider) {
            WebViewProviderAdapter adapter = sProviderAdapterCache.get(webview);
            if (adapter == null) {
                WebViewProviderAdapter adapter2 = new WebViewProviderAdapter(createProvider(webview));
                sProviderAdapterCache.put(webview, adapter2);
                return adapter2;
            }
            return adapter;
        }
        return new WebViewProviderAdapter(createProvider(webview));
    }

    public static WebMessagePortCompat[] createWebMessageChannel(WebView webview) {
        ApiFeature.M feature = WebViewFeatureInternal.CREATE_WEB_MESSAGE_CHANNEL;
        if (feature.isSupportedByFramework()) {
            return WebMessagePortImpl.portsToCompat(ApiHelperForM.createWebMessageChannel(webview));
        }
        if (feature.isSupportedByWebView()) {
            checkThread(webview);
            return getProvider(webview).createWebMessageChannel();
        }
        throw WebViewFeatureInternal.getUnsupportedOperationException();
    }

    public static void postWebMessage(WebView webview, WebMessageCompat message, Uri targetOrigin) {
        if (WILDCARD_URI.equals(targetOrigin)) {
            targetOrigin = EMPTY_URI;
        }
        ApiFeature.M feature = WebViewFeatureInternal.POST_WEB_MESSAGE;
        if (feature.isSupportedByFramework() && message.getType() == 0) {
            ApiHelperForM.postWebMessage(webview, WebMessagePortImpl.compatToFrameworkMessage(message), targetOrigin);
        } else {
            if (feature.isSupportedByWebView() && WebMessageAdapter.isMessagePayloadTypeSupportedByWebView(message.getType())) {
                checkThread(webview);
                getProvider(webview).postWebMessage(message, targetOrigin);
                return;
            }
            throw WebViewFeatureInternal.getUnsupportedOperationException();
        }
    }

    public static void addWebMessageListener(WebView webView, String jsObjectName, Set<String> allowedOriginRules, WebMessageListener listener) {
        ApiFeature.NoFramework feature = WebViewFeatureInternal.WEB_MESSAGE_LISTENER;
        if (feature.isSupportedByWebView()) {
            getProvider(webView).addWebMessageListener(jsObjectName, (String[]) allowedOriginRules.toArray(new String[0]), listener);
            return;
        }
        throw WebViewFeatureInternal.getUnsupportedOperationException();
    }

    public static void removeWebMessageListener(WebView webview, String jsObjectName) {
        ApiFeature.NoFramework feature = WebViewFeatureInternal.WEB_MESSAGE_LISTENER;
        if (feature.isSupportedByWebView()) {
            getProvider(webview).removeWebMessageListener(jsObjectName);
            return;
        }
        throw WebViewFeatureInternal.getUnsupportedOperationException();
    }

    public static ScriptHandler addDocumentStartJavaScript(WebView webview, String script, Set<String> allowedOriginRules) {
        ApiFeature.NoFramework feature = WebViewFeatureInternal.DOCUMENT_START_SCRIPT;
        if (feature.isSupportedByWebView()) {
            return getProvider(webview).addDocumentStartJavaScript(script, (String[]) allowedOriginRules.toArray(new String[0]));
        }
        throw WebViewFeatureInternal.getUnsupportedOperationException();
    }

    public static WebViewClient getWebViewClient(WebView webview) {
        ApiFeature.O feature = WebViewFeatureInternal.GET_WEB_VIEW_CLIENT;
        if (feature.isSupportedByFramework()) {
            return ApiHelperForO.getWebViewClient(webview);
        }
        if (feature.isSupportedByWebView()) {
            checkThread(webview);
            return getProvider(webview).getWebViewClient();
        }
        throw WebViewFeatureInternal.getUnsupportedOperationException();
    }

    public static WebChromeClient getWebChromeClient(WebView webview) {
        ApiFeature.O feature = WebViewFeatureInternal.GET_WEB_CHROME_CLIENT;
        if (feature.isSupportedByFramework()) {
            return ApiHelperForO.getWebChromeClient(webview);
        }
        if (feature.isSupportedByWebView()) {
            checkThread(webview);
            return getProvider(webview).getWebChromeClient();
        }
        throw WebViewFeatureInternal.getUnsupportedOperationException();
    }

    public static WebViewRenderProcess getWebViewRenderProcess(WebView webview) {
        ApiFeature.Q feature = WebViewFeatureInternal.GET_WEB_VIEW_RENDERER;
        if (feature.isSupportedByFramework()) {
            android.webkit.WebViewRenderProcess renderer = ApiHelperForQ.getWebViewRenderProcess(webview);
            if (renderer != null) {
                return WebViewRenderProcessImpl.forFrameworkObject(renderer);
            }
            return null;
        }
        if (feature.isSupportedByWebView()) {
            checkThread(webview);
            return getProvider(webview).getWebViewRenderProcess();
        }
        throw WebViewFeatureInternal.getUnsupportedOperationException();
    }

    public static void setWebViewRenderProcessClient(WebView webview, Executor executor, WebViewRenderProcessClient webViewRenderProcessClient) {
        ApiFeature.Q feature = WebViewFeatureInternal.WEB_VIEW_RENDERER_CLIENT_BASIC_USAGE;
        if (feature.isSupportedByFramework()) {
            ApiHelperForQ.setWebViewRenderProcessClient(webview, executor, webViewRenderProcessClient);
        } else {
            if (feature.isSupportedByWebView()) {
                checkThread(webview);
                getProvider(webview).setWebViewRenderProcessClient(executor, webViewRenderProcessClient);
                return;
            }
            throw WebViewFeatureInternal.getUnsupportedOperationException();
        }
    }

    public static void setWebViewRenderProcessClient(WebView webview, WebViewRenderProcessClient webViewRenderProcessClient) {
        ApiFeature.Q feature = WebViewFeatureInternal.WEB_VIEW_RENDERER_CLIENT_BASIC_USAGE;
        if (feature.isSupportedByFramework()) {
            ApiHelperForQ.setWebViewRenderProcessClient(webview, webViewRenderProcessClient);
        } else {
            if (feature.isSupportedByWebView()) {
                checkThread(webview);
                getProvider(webview).setWebViewRenderProcessClient(null, webViewRenderProcessClient);
                return;
            }
            throw WebViewFeatureInternal.getUnsupportedOperationException();
        }
    }

    public static WebViewRenderProcessClient getWebViewRenderProcessClient(WebView webview) {
        ApiFeature.Q feature = WebViewFeatureInternal.WEB_VIEW_RENDERER_CLIENT_BASIC_USAGE;
        if (feature.isSupportedByFramework()) {
            android.webkit.WebViewRenderProcessClient renderer = ApiHelperForQ.getWebViewRenderProcessClient(webview);
            if (renderer == null || !(renderer instanceof WebViewRenderProcessClientFrameworkAdapter)) {
                return null;
            }
            return ((WebViewRenderProcessClientFrameworkAdapter) renderer).getFrameworkRenderProcessClient();
        }
        if (feature.isSupportedByWebView()) {
            checkThread(webview);
            return getProvider(webview).getWebViewRenderProcessClient();
        }
        throw WebViewFeatureInternal.getUnsupportedOperationException();
    }

    public static boolean isMultiProcessEnabled() {
        ApiFeature.NoFramework feature = WebViewFeatureInternal.MULTI_PROCESS;
        if (feature.isSupportedByWebView()) {
            return getFactory().getStatics().isMultiProcessEnabled();
        }
        throw WebViewFeatureInternal.getUnsupportedOperationException();
    }

    public static String getVariationsHeader() {
        ApiFeature.NoFramework feature = WebViewFeatureInternal.GET_VARIATIONS_HEADER;
        if (feature.isSupportedByWebView()) {
            return getFactory().getStatics().getVariationsHeader();
        }
        throw WebViewFeatureInternal.getUnsupportedOperationException();
    }

    public static void setProfile(WebView webView, String profileName) {
        ApiFeature.NoFramework feature = WebViewFeatureInternal.MULTI_PROFILE;
        if (feature.isSupportedByWebView()) {
            getProvider(webView).setProfileWithName(profileName);
            return;
        }
        throw WebViewFeatureInternal.getUnsupportedOperationException();
    }

    public static Profile getProfile(WebView webView) {
        ApiFeature.NoFramework feature = WebViewFeatureInternal.MULTI_PROFILE;
        if (feature.isSupportedByWebView()) {
            return getProvider(webView).getProfile();
        }
        throw WebViewFeatureInternal.getUnsupportedOperationException();
    }

    public static boolean isAudioMuted(WebView webView) {
        ApiFeature.NoFramework feature = WebViewFeatureInternal.MUTE_AUDIO;
        if (feature.isSupportedByWebView()) {
            return getProvider(webView).isAudioMuted();
        }
        throw WebViewFeatureInternal.getUnsupportedOperationException();
    }

    public static void setAudioMuted(WebView webView, boolean mute) {
        ApiFeature.NoFramework feature = WebViewFeatureInternal.MUTE_AUDIO;
        if (feature.isSupportedByWebView()) {
            getProvider(webView).setAudioMuted(mute);
            return;
        }
        throw WebViewFeatureInternal.getUnsupportedOperationException();
    }

    public static void startUpWebView(final Context context, final WebViewStartUpConfig config, final WebViewStartUpCallback callback) {
        config.getBackgroundExecutor().execute(new Runnable() { // from class: androidx.webkit.WebViewCompat$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                WebViewCompat.lambda$startUpWebView$3(config, callback, context);
            }
        });
    }

    static /* synthetic */ void lambda$startUpWebView$3(WebViewStartUpConfig config, final WebViewStartUpCallback callback, Context context) {
        WebViewGlueCommunicator.getWebViewClassLoader();
        if (WebViewFeatureInternal.ASYNC_WEBVIEW_STARTUP.isSupportedByWebView()) {
            getFactory().startUpWebView(config, new WebViewStartUpCallback() { // from class: androidx.webkit.WebViewCompat$$ExternalSyntheticLambda2
                @Override // androidx.webkit.WebViewCompat.WebViewStartUpCallback
                public final void onSuccess(WebViewStartUpResult webViewStartUpResult) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: androidx.webkit.WebViewCompat$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            webViewStartUpCallback.onSuccess(webViewStartUpResult);
                        }
                    });
                }
            });
            return;
        }
        if (config.shouldRunUiThreadStartUpTasks()) {
            WebSettings.getDefaultUserAgent(context.getApplicationContext());
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: androidx.webkit.WebViewCompat$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                callback.onSuccess(new WebViewCompat.NullReturningWebViewStartUpResult());
            }
        });
    }

    public static void setDefaultTrafficStatsTag(int tag) {
        ApiFeature.NoFramework feature = WebViewFeatureInternal.DEFAULT_TRAFFICSTATS_TAGGING;
        if (feature.isSupportedByWebView()) {
            getFactory().getStatics().setDefaultTrafficStatsTag(tag);
            return;
        }
        throw WebViewFeatureInternal.getUnsupportedOperationException();
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class NullReturningWebViewStartUpResult implements WebViewStartUpResult {
        private NullReturningWebViewStartUpResult() {
        }

        @Override // androidx.webkit.WebViewStartUpResult
        public Long getTotalTimeInUiThreadMillis() {
            return null;
        }

        @Override // androidx.webkit.WebViewStartUpResult
        public Long getMaxTimePerTaskInUiThreadMillis() {
            return null;
        }

        @Override // androidx.webkit.WebViewStartUpResult
        public List<BlockingStartUpLocation> getBlockingStartUpLocations() {
            return null;
        }
    }

    public static void prerenderUrlAsync(WebView webView, String url, CancellationSignal cancellationSignal, Executor callbackExecutor, PrerenderOperationCallback callback) {
        ApiFeature.NoFramework feature = WebViewFeatureInternal.PRERENDER_WITH_URL;
        if (feature.isSupportedByWebView()) {
            getProvider(webView).prerenderUrlAsync(url, cancellationSignal, callbackExecutor, callback);
            return;
        }
        throw WebViewFeatureInternal.getUnsupportedOperationException();
    }

    public static void prerenderUrlAsync(WebView webView, String url, CancellationSignal cancellationSignal, Executor callbackExecutor, SpeculativeLoadingParameters params, PrerenderOperationCallback callback) {
        ApiFeature.NoFramework feature = WebViewFeatureInternal.PRERENDER_WITH_URL;
        if (!feature.isSupportedByWebView()) {
            throw WebViewFeatureInternal.getUnsupportedOperationException();
        }
        getProvider(webView).prerenderUrlAsync(url, cancellationSignal, callbackExecutor, params, callback);
    }

    public static void saveState(WebView webView, Bundle outState, int maxSizeBytes, boolean includeForwardState) {
        ApiFeature.NoFramework feature = WebViewFeatureInternal.SAVE_STATE;
        if (feature.isSupportedByWebView()) {
            getProvider(webView).saveState(outState, maxSizeBytes, includeForwardState);
            return;
        }
        throw WebViewFeatureInternal.getUnsupportedOperationException();
    }

    public static void setShouldCacheProvider(boolean shouldCacheProvider) {
        ApiFeature.NoFramework feature = WebViewFeatureInternal.CACHE_PROVIDER;
        if (feature.isSupportedByWebView()) {
            sShouldCacheProvider = shouldCacheProvider;
            return;
        }
        throw WebViewFeatureInternal.getUnsupportedOperationException();
    }

    public static void setWebNavigationClient(WebView webView, WebNavigationClient client) {
        ApiFeature.NoFramework feature = WebViewFeatureInternal.NAVIGATION_CALLBACK_BASIC;
        if (feature.isSupportedByWebView()) {
            getProvider(webView).setWebNavigationClient(client);
            return;
        }
        throw WebViewFeatureInternal.getUnsupportedOperationException();
    }

    public static WebNavigationClient getWebNavigationClient(WebView webView) {
        ApiFeature.NoFramework feature = WebViewFeatureInternal.NAVIGATION_CALLBACK_BASIC;
        if (feature.isSupportedByWebView()) {
            return getProvider(webView).getWebNavigationClient();
        }
        throw WebViewFeatureInternal.getUnsupportedOperationException();
    }

    private static WebViewProviderFactory getFactory() {
        return WebViewGlueCommunicator.getFactory();
    }

    private static WebViewProviderBoundaryInterface createProvider(WebView webview) {
        return getFactory().createWebView(webview);
    }

    private static void checkThread(WebView webview) {
        if (Build.VERSION.SDK_INT >= 28) {
            Looper webViewLooper = ApiHelperForP.getWebViewLooper(webview);
            if (webViewLooper != Looper.myLooper()) {
                throw new RuntimeException("A WebView method was called on thread '" + Thread.currentThread().getName() + "'. All WebView methods must be called on the same thread. (Expected Looper " + webViewLooper + " called on " + Looper.myLooper() + ", FYI main Looper is " + Looper.getMainLooper() + ")");
            }
        } else {
            try {
                Method checkThreadMethod = WebView.class.getDeclaredMethod("checkThread", new Class[0]);
                checkThreadMethod.setAccessible(true);
                checkThreadMethod.invoke(webview, new Object[0]);
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static WeakHashMap<WebView, WebViewProviderAdapter> getProviderAdapterCacheForTesting() {
        return sProviderAdapterCache;
    }
}
