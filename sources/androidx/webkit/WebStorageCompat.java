package androidx.webkit;

import android.os.Handler;
import android.os.Looper;
import android.webkit.WebStorage;
import androidx.webkit.internal.ApiFeature;
import androidx.webkit.internal.WebStorageAdapter;
import androidx.webkit.internal.WebViewFeatureInternal;
import androidx.webkit.internal.WebViewGlueCommunicator;
import java.util.concurrent.Executor;

/* JADX INFO: loaded from: classes.dex */
public final class WebStorageCompat {
    private WebStorageCompat() {
    }

    public static void deleteBrowsingData(WebStorage instance, Executor executor, Runnable doneCallback) {
        ApiFeature.NoFramework feature = WebViewFeatureInternal.DELETE_BROWSING_DATA;
        if (feature.isSupportedByWebView()) {
            getAdapter(instance).deleteBrowsingData(executor, doneCallback);
            return;
        }
        throw WebViewFeatureInternal.getUnsupportedOperationException();
    }

    public static void deleteBrowsingData(WebStorage instance, Runnable doneCallback) {
        deleteBrowsingData(instance, new Executor() { // from class: androidx.webkit.WebStorageCompat$$ExternalSyntheticLambda1
            @Override // java.util.concurrent.Executor
            public final void execute(Runnable runnable) {
                new Handler(Looper.getMainLooper()).post(runnable);
            }
        }, doneCallback);
    }

    public static String deleteBrowsingDataForSite(WebStorage instance, String site, Executor executor, Runnable doneCallback) {
        ApiFeature.NoFramework feature = WebViewFeatureInternal.DELETE_BROWSING_DATA;
        if (feature.isSupportedByWebView()) {
            return getAdapter(instance).deleteBrowsingDataForSite(site, executor, doneCallback);
        }
        throw WebViewFeatureInternal.getUnsupportedOperationException();
    }

    public static String deleteBrowsingDataForSite(WebStorage instance, String site, Runnable doneCallback) {
        return deleteBrowsingDataForSite(instance, site, new Executor() { // from class: androidx.webkit.WebStorageCompat$$ExternalSyntheticLambda0
            @Override // java.util.concurrent.Executor
            public final void execute(Runnable runnable) {
                new Handler(Looper.getMainLooper()).post(runnable);
            }
        }, doneCallback);
    }

    private static WebStorageAdapter getAdapter(WebStorage webStorage) {
        return WebViewGlueCommunicator.getCompatConverter().convertWebStorage(webStorage);
    }
}
