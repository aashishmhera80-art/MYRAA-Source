package androidx.webkit.internal;

import android.webkit.WebView;
import androidx.webkit.WebViewRenderProcess;
import androidx.webkit.WebViewRenderProcessClient;
import java.lang.reflect.InvocationHandler;
import java.util.concurrent.Executor;
import org.chromium.support_lib_boundary.WebViewRendererClientBoundaryInterface;

/* JADX INFO: loaded from: classes.dex */
public class WebViewRenderProcessClientAdapter implements WebViewRendererClientBoundaryInterface {
    private static final String[] sSupportedFeatures = {"WEB_VIEW_RENDERER_CLIENT_BASIC_USAGE"};
    private final Executor mExecutor;
    private final WebViewRenderProcessClient mWebViewRenderProcessClient;

    public WebViewRenderProcessClientAdapter(Executor executor, WebViewRenderProcessClient webViewRenderProcessClient) {
        this.mExecutor = executor;
        this.mWebViewRenderProcessClient = webViewRenderProcessClient;
    }

    public WebViewRenderProcessClient getWebViewRenderProcessClient() {
        return this.mWebViewRenderProcessClient;
    }

    @Override // org.chromium.support_lib_boundary.FeatureFlagHolderBoundaryInterface
    public final String[] getSupportedFeatures() {
        return sSupportedFeatures;
    }

    @Override // org.chromium.support_lib_boundary.WebViewRendererClientBoundaryInterface
    public final void onRendererUnresponsive(final WebView view, InvocationHandler renderer) {
        final WebViewRenderProcess rendererObject = WebViewRenderProcessImpl.forInvocationHandler(renderer);
        final WebViewRenderProcessClient client = this.mWebViewRenderProcessClient;
        if (this.mExecutor == null) {
            client.onRenderProcessUnresponsive(view, rendererObject);
        } else {
            this.mExecutor.execute(new Runnable() { // from class: androidx.webkit.internal.WebViewRenderProcessClientAdapter$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    client.onRenderProcessUnresponsive(view, rendererObject);
                }
            });
        }
    }

    @Override // org.chromium.support_lib_boundary.WebViewRendererClientBoundaryInterface
    public final void onRendererResponsive(final WebView view, InvocationHandler renderer) {
        final WebViewRenderProcess rendererObject = WebViewRenderProcessImpl.forInvocationHandler(renderer);
        final WebViewRenderProcessClient client = this.mWebViewRenderProcessClient;
        if (this.mExecutor == null) {
            client.onRenderProcessResponsive(view, rendererObject);
        } else {
            this.mExecutor.execute(new Runnable() { // from class: androidx.webkit.internal.WebViewRenderProcessClientAdapter$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    client.onRenderProcessResponsive(view, rendererObject);
                }
            });
        }
    }
}
