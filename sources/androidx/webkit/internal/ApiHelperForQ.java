package androidx.webkit.internal;

import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewRenderProcess;
import androidx.webkit.WebViewRenderProcessClient;
import java.util.concurrent.Executor;

/* JADX INFO: loaded from: classes.dex */
public class ApiHelperForQ {
    private ApiHelperForQ() {
    }

    @Deprecated
    public static void setForceDark(WebSettings webSettings, int forceDark) {
        webSettings.setForceDark(forceDark);
    }

    @Deprecated
    public static int getForceDark(WebSettings webSettings) {
        return webSettings.getForceDark();
    }

    public static WebViewRenderProcess getWebViewRenderProcess(WebView webView) {
        return webView.getWebViewRenderProcess();
    }

    public static boolean terminate(WebViewRenderProcess webViewRenderProcess) {
        return webViewRenderProcess.terminate();
    }

    public static void setWebViewRenderProcessClient(WebView webView, Executor executor, WebViewRenderProcessClient client) {
        WebViewRenderProcessClientFrameworkAdapter clientAdapter = client != null ? new WebViewRenderProcessClientFrameworkAdapter(client) : null;
        webView.setWebViewRenderProcessClient(executor, clientAdapter);
    }

    public static void setWebViewRenderProcessClient(WebView webView, WebViewRenderProcessClient client) {
        WebViewRenderProcessClientFrameworkAdapter clientAdapter = client != null ? new WebViewRenderProcessClientFrameworkAdapter(client) : null;
        webView.setWebViewRenderProcessClient(clientAdapter);
    }

    public static android.webkit.WebViewRenderProcessClient getWebViewRenderProcessClient(WebView webView) {
        return webView.getWebViewRenderProcessClient();
    }
}
