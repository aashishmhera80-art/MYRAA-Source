package androidx.webkit.internal;

import android.webkit.WebView;
import android.webkit.WebViewRenderProcess;
import android.webkit.WebViewRenderProcessClient;

/* JADX INFO: loaded from: classes.dex */
public class WebViewRenderProcessClientFrameworkAdapter extends WebViewRenderProcessClient {
    private final androidx.webkit.WebViewRenderProcessClient mWebViewRenderProcessClient;

    public WebViewRenderProcessClientFrameworkAdapter(androidx.webkit.WebViewRenderProcessClient client) {
        this.mWebViewRenderProcessClient = client;
    }

    @Override // android.webkit.WebViewRenderProcessClient
    public void onRenderProcessUnresponsive(WebView view, WebViewRenderProcess renderer) {
        this.mWebViewRenderProcessClient.onRenderProcessUnresponsive(view, WebViewRenderProcessImpl.forFrameworkObject(renderer));
    }

    @Override // android.webkit.WebViewRenderProcessClient
    public void onRenderProcessResponsive(WebView view, WebViewRenderProcess renderer) {
        this.mWebViewRenderProcessClient.onRenderProcessResponsive(view, WebViewRenderProcessImpl.forFrameworkObject(renderer));
    }

    public androidx.webkit.WebViewRenderProcessClient getFrameworkRenderProcessClient() {
        return this.mWebViewRenderProcessClient;
    }
}
