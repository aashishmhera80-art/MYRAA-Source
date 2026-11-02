package androidx.webkit;

import android.webkit.CookieManager;
import androidx.webkit.internal.ApiFeature;
import androidx.webkit.internal.CookieManagerAdapter;
import androidx.webkit.internal.WebViewFeatureInternal;
import androidx.webkit.internal.WebViewGlueCommunicator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class CookieManagerCompat {
    private CookieManagerCompat() {
    }

    public static List<String> getCookieInfo(CookieManager cookieManager, String url) {
        ApiFeature.NoFramework feature = WebViewFeatureInternal.GET_COOKIE_INFO;
        if (feature.isSupportedByWebView()) {
            return getAdapter(cookieManager).getCookieInfo(url);
        }
        throw WebViewFeatureInternal.getUnsupportedOperationException();
    }

    private static CookieManagerAdapter getAdapter(CookieManager cookieManager) {
        return WebViewGlueCommunicator.getCompatConverter().convertCookieManager(cookieManager);
    }
}
