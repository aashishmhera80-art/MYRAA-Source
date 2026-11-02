package androidx.webkit.internal;

import android.webkit.SafeBrowsingResponse;
import androidx.webkit.SafeBrowsingResponseCompat;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import org.chromium.support_lib_boundary.SafeBrowsingResponseBoundaryInterface;
import org.chromium.support_lib_boundary.util.BoundaryInterfaceReflectionUtil;

/* JADX INFO: loaded from: classes.dex */
public class SafeBrowsingResponseImpl extends SafeBrowsingResponseCompat {
    private SafeBrowsingResponseBoundaryInterface mBoundaryInterface;
    private SafeBrowsingResponse mFrameworksImpl;

    public SafeBrowsingResponseImpl(InvocationHandler invocationHandler) {
        this.mBoundaryInterface = (SafeBrowsingResponseBoundaryInterface) BoundaryInterfaceReflectionUtil.castToSuppLibClass(SafeBrowsingResponseBoundaryInterface.class, invocationHandler);
    }

    public SafeBrowsingResponseImpl(SafeBrowsingResponse response) {
        this.mFrameworksImpl = response;
    }

    private SafeBrowsingResponse getFrameworksImpl() {
        if (this.mFrameworksImpl == null) {
            this.mFrameworksImpl = WebViewGlueCommunicator.getCompatConverter().convertSafeBrowsingResponse(Proxy.getInvocationHandler(this.mBoundaryInterface));
        }
        return this.mFrameworksImpl;
    }

    private SafeBrowsingResponseBoundaryInterface getBoundaryInterface() {
        if (this.mBoundaryInterface == null) {
            this.mBoundaryInterface = (SafeBrowsingResponseBoundaryInterface) BoundaryInterfaceReflectionUtil.castToSuppLibClass(SafeBrowsingResponseBoundaryInterface.class, WebViewGlueCommunicator.getCompatConverter().convertSafeBrowsingResponse(this.mFrameworksImpl));
        }
        return this.mBoundaryInterface;
    }

    @Override // androidx.webkit.SafeBrowsingResponseCompat
    public void showInterstitial(boolean allowReporting) {
        ApiFeature.O_MR1 feature = WebViewFeatureInternal.SAFE_BROWSING_RESPONSE_SHOW_INTERSTITIAL;
        if (feature.isSupportedByFramework()) {
            ApiHelperForOMR1.showInterstitial(getFrameworksImpl(), allowReporting);
        } else {
            if (feature.isSupportedByWebView()) {
                getBoundaryInterface().showInterstitial(allowReporting);
                return;
            }
            throw WebViewFeatureInternal.getUnsupportedOperationException();
        }
    }

    @Override // androidx.webkit.SafeBrowsingResponseCompat
    public void proceed(boolean report) {
        ApiFeature.O_MR1 feature = WebViewFeatureInternal.SAFE_BROWSING_RESPONSE_PROCEED;
        if (feature.isSupportedByFramework()) {
            ApiHelperForOMR1.proceed(getFrameworksImpl(), report);
        } else {
            if (feature.isSupportedByWebView()) {
                getBoundaryInterface().proceed(report);
                return;
            }
            throw WebViewFeatureInternal.getUnsupportedOperationException();
        }
    }

    @Override // androidx.webkit.SafeBrowsingResponseCompat
    public void backToSafety(boolean report) {
        ApiFeature.O_MR1 feature = WebViewFeatureInternal.SAFE_BROWSING_RESPONSE_BACK_TO_SAFETY;
        if (feature.isSupportedByFramework()) {
            ApiHelperForOMR1.backToSafety(getFrameworksImpl(), report);
        } else {
            if (feature.isSupportedByWebView()) {
                getBoundaryInterface().backToSafety(report);
                return;
            }
            throw WebViewFeatureInternal.getUnsupportedOperationException();
        }
    }
}
