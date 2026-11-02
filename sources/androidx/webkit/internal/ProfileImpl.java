package androidx.webkit.internal;

import android.os.CancellationSignal;
import android.webkit.CookieManager;
import android.webkit.GeolocationPermissions;
import android.webkit.ServiceWorkerController;
import android.webkit.WebStorage;
import androidx.webkit.OutcomeReceiverCompat;
import androidx.webkit.PrefetchException;
import androidx.webkit.Profile;
import androidx.webkit.SpeculativeLoadingConfig;
import androidx.webkit.SpeculativeLoadingParameters;
import java.lang.reflect.InvocationHandler;
import java.util.concurrent.Executor;
import org.chromium.support_lib_boundary.ProfileBoundaryInterface;
import org.chromium.support_lib_boundary.util.BoundaryInterfaceReflectionUtil;

/* JADX INFO: loaded from: classes.dex */
public class ProfileImpl implements Profile {
    private final ProfileBoundaryInterface mProfileImpl;

    ProfileImpl(ProfileBoundaryInterface profileImpl) {
        this.mProfileImpl = profileImpl;
    }

    private ProfileImpl() {
        this.mProfileImpl = null;
    }

    @Override // androidx.webkit.Profile
    public String getName() {
        ApiFeature.NoFramework feature = WebViewFeatureInternal.MULTI_PROFILE;
        if (feature.isSupportedByWebView()) {
            return this.mProfileImpl.getName();
        }
        throw WebViewFeatureInternal.getUnsupportedOperationException();
    }

    @Override // androidx.webkit.Profile
    public CookieManager getCookieManager() throws IllegalStateException {
        ApiFeature.NoFramework feature = WebViewFeatureInternal.MULTI_PROFILE;
        if (feature.isSupportedByWebView()) {
            return this.mProfileImpl.getCookieManager();
        }
        throw WebViewFeatureInternal.getUnsupportedOperationException();
    }

    @Override // androidx.webkit.Profile
    public WebStorage getWebStorage() throws IllegalStateException {
        ApiFeature.NoFramework feature = WebViewFeatureInternal.MULTI_PROFILE;
        if (feature.isSupportedByWebView()) {
            return this.mProfileImpl.getWebStorage();
        }
        throw WebViewFeatureInternal.getUnsupportedOperationException();
    }

    @Override // androidx.webkit.Profile
    public GeolocationPermissions getGeolocationPermissions() throws IllegalStateException {
        ApiFeature.NoFramework feature = WebViewFeatureInternal.MULTI_PROFILE;
        if (feature.isSupportedByWebView()) {
            return this.mProfileImpl.getGeoLocationPermissions();
        }
        throw WebViewFeatureInternal.getUnsupportedOperationException();
    }

    @Override // androidx.webkit.Profile
    public ServiceWorkerController getServiceWorkerController() throws IllegalStateException {
        ApiFeature.NoFramework feature = WebViewFeatureInternal.MULTI_PROFILE;
        if (feature.isSupportedByWebView()) {
            return this.mProfileImpl.getServiceWorkerController();
        }
        throw WebViewFeatureInternal.getUnsupportedOperationException();
    }

    @Override // androidx.webkit.Profile
    public void prefetchUrlAsync(String url, CancellationSignal cancellationSignal, Executor callbackExecutor, SpeculativeLoadingParameters params, OutcomeReceiverCompat<Void, PrefetchException> callback) {
        ApiFeature.NoFramework feature = WebViewFeatureInternal.PROFILE_URL_PREFETCH;
        if (!feature.isSupportedByWebView()) {
            throw WebViewFeatureInternal.getUnsupportedOperationException();
        }
        InvocationHandler paramsBoundaryInterface = BoundaryInterfaceReflectionUtil.createInvocationHandlerFor(new SpeculativeLoadingParametersAdapter(params));
        this.mProfileImpl.prefetchUrl(url, cancellationSignal, callbackExecutor, paramsBoundaryInterface, PrefetchOperationCallbackAdapter.buildInvocationHandler(callback));
    }

    @Override // androidx.webkit.Profile
    public void prefetchUrlAsync(String url, CancellationSignal cancellationSignal, Executor callbackExecutor, OutcomeReceiverCompat<Void, PrefetchException> callback) {
        ApiFeature.NoFramework feature = WebViewFeatureInternal.PROFILE_URL_PREFETCH;
        if (feature.isSupportedByWebView()) {
            this.mProfileImpl.prefetchUrl(url, cancellationSignal, callbackExecutor, PrefetchOperationCallbackAdapter.buildInvocationHandler(callback));
            return;
        }
        throw WebViewFeatureInternal.getUnsupportedOperationException();
    }

    @Override // androidx.webkit.Profile
    public void clearPrefetchAsync(String url, Executor callbackExecutor, OutcomeReceiverCompat<Void, PrefetchException> callback) {
        ApiFeature.NoFramework feature = WebViewFeatureInternal.PROFILE_URL_PREFETCH;
        if (feature.isSupportedByWebView()) {
            this.mProfileImpl.clearPrefetch(url, callbackExecutor, PrefetchOperationCallbackAdapter.buildInvocationHandler(callback));
            return;
        }
        throw WebViewFeatureInternal.getUnsupportedOperationException();
    }

    @Override // androidx.webkit.Profile
    public void setSpeculativeLoadingConfig(SpeculativeLoadingConfig speculativeLoadingConfig) {
        ApiFeature.NoFramework feature = WebViewFeatureInternal.SPECULATIVE_LOADING_CONFIG;
        if (feature.isSupportedByWebView()) {
            InvocationHandler configInvocation = BoundaryInterfaceReflectionUtil.createInvocationHandlerFor(new SpeculativeLoadingConfigAdapter(speculativeLoadingConfig));
            this.mProfileImpl.setSpeculativeLoadingConfig(configInvocation);
            return;
        }
        throw WebViewFeatureInternal.getUnsupportedOperationException();
    }
}
