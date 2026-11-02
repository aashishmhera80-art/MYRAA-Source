package androidx.webkit.internal;

import androidx.webkit.WebViewRenderProcess;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import org.chromium.support_lib_boundary.WebViewRendererBoundaryInterface;
import org.chromium.support_lib_boundary.util.BoundaryInterfaceReflectionUtil;

/* JADX INFO: loaded from: classes.dex */
public class WebViewRenderProcessImpl extends WebViewRenderProcess {
    private static final WeakHashMap<android.webkit.WebViewRenderProcess, WebViewRenderProcessImpl> sFrameworkMap = new WeakHashMap<>();
    private WebViewRendererBoundaryInterface mBoundaryInterface;
    private WeakReference<android.webkit.WebViewRenderProcess> mFrameworkObject;

    public WebViewRenderProcessImpl(WebViewRendererBoundaryInterface boundaryInterface) {
        this.mBoundaryInterface = boundaryInterface;
    }

    public WebViewRenderProcessImpl(android.webkit.WebViewRenderProcess frameworkRenderer) {
        this.mFrameworkObject = new WeakReference<>(frameworkRenderer);
    }

    public static WebViewRenderProcessImpl forInvocationHandler(InvocationHandler invocationHandler) {
        final WebViewRendererBoundaryInterface boundaryInterface = (WebViewRendererBoundaryInterface) BoundaryInterfaceReflectionUtil.castToSuppLibClass(WebViewRendererBoundaryInterface.class, invocationHandler);
        return (WebViewRenderProcessImpl) boundaryInterface.getOrCreatePeer(new Callable() { // from class: androidx.webkit.internal.WebViewRenderProcessImpl$$ExternalSyntheticLambda0
            @Override // java.util.concurrent.Callable
            public final Object call() {
                return WebViewRenderProcessImpl.lambda$forInvocationHandler$0(boundaryInterface);
            }
        });
    }

    static /* synthetic */ Object lambda$forInvocationHandler$0(WebViewRendererBoundaryInterface boundaryInterface) throws Exception {
        return new WebViewRenderProcessImpl(boundaryInterface);
    }

    public static WebViewRenderProcessImpl forFrameworkObject(android.webkit.WebViewRenderProcess frameworkRenderer) {
        WebViewRenderProcessImpl renderer = sFrameworkMap.get(frameworkRenderer);
        if (renderer != null) {
            return renderer;
        }
        WebViewRenderProcessImpl renderer2 = new WebViewRenderProcessImpl(frameworkRenderer);
        sFrameworkMap.put(frameworkRenderer, renderer2);
        return renderer2;
    }

    @Override // androidx.webkit.WebViewRenderProcess
    public boolean terminate() {
        ApiFeature.Q feature = WebViewFeatureInternal.WEB_VIEW_RENDERER_TERMINATE;
        if (feature.isSupportedByFramework()) {
            android.webkit.WebViewRenderProcess renderer = this.mFrameworkObject.get();
            return renderer != null && ApiHelperForQ.terminate(renderer);
        }
        if (feature.isSupportedByWebView()) {
            return this.mBoundaryInterface.terminate();
        }
        throw WebViewFeatureInternal.getUnsupportedOperationException();
    }
}
