package androidx.webkit;

import android.webkit.WebResourceRequest;
import androidx.webkit.internal.ApiFeature;
import androidx.webkit.internal.ApiHelperForN;
import androidx.webkit.internal.WebResourceRequestAdapter;
import androidx.webkit.internal.WebViewFeatureInternal;
import androidx.webkit.internal.WebViewGlueCommunicator;

/* JADX INFO: loaded from: classes.dex */
public class WebResourceRequestCompat {
    private WebResourceRequestCompat() {
    }

    public static boolean isRedirect(WebResourceRequest request) {
        ApiFeature.N feature = WebViewFeatureInternal.WEB_RESOURCE_REQUEST_IS_REDIRECT;
        if (feature.isSupportedByFramework()) {
            return ApiHelperForN.isRedirect(request);
        }
        if (feature.isSupportedByWebView()) {
            return getAdapter(request).isRedirect();
        }
        throw WebViewFeatureInternal.getUnsupportedOperationException();
    }

    private static WebResourceRequestAdapter getAdapter(WebResourceRequest request) {
        return WebViewGlueCommunicator.getCompatConverter().convertWebResourceRequest(request);
    }
}
