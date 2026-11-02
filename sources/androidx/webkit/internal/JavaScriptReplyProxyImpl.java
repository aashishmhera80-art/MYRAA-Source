package androidx.webkit.internal;

import androidx.webkit.JavaScriptReplyProxy;
import java.lang.reflect.InvocationHandler;
import java.util.Objects;
import java.util.concurrent.Callable;
import org.chromium.support_lib_boundary.JsReplyProxyBoundaryInterface;
import org.chromium.support_lib_boundary.util.BoundaryInterfaceReflectionUtil;

/* JADX INFO: loaded from: classes.dex */
public class JavaScriptReplyProxyImpl extends JavaScriptReplyProxy {
    private final JsReplyProxyBoundaryInterface mBoundaryInterface;

    public JavaScriptReplyProxyImpl(JsReplyProxyBoundaryInterface boundaryInterface) {
        this.mBoundaryInterface = boundaryInterface;
    }

    public static JavaScriptReplyProxyImpl forInvocationHandler(InvocationHandler invocationHandler) {
        final JsReplyProxyBoundaryInterface boundaryInterface = (JsReplyProxyBoundaryInterface) BoundaryInterfaceReflectionUtil.castToSuppLibClass(JsReplyProxyBoundaryInterface.class, invocationHandler);
        return (JavaScriptReplyProxyImpl) boundaryInterface.getOrCreatePeer(new Callable() { // from class: androidx.webkit.internal.JavaScriptReplyProxyImpl$$ExternalSyntheticLambda0
            @Override // java.util.concurrent.Callable
            public final Object call() {
                return JavaScriptReplyProxyImpl.lambda$forInvocationHandler$0(boundaryInterface);
            }
        });
    }

    static /* synthetic */ Object lambda$forInvocationHandler$0(JsReplyProxyBoundaryInterface boundaryInterface) throws Exception {
        return new JavaScriptReplyProxyImpl(boundaryInterface);
    }

    @Override // androidx.webkit.JavaScriptReplyProxy
    public void postMessage(String message) {
        ApiFeature.NoFramework feature = WebViewFeatureInternal.WEB_MESSAGE_LISTENER;
        if (feature.isSupportedByWebView()) {
            this.mBoundaryInterface.postMessage(message);
            return;
        }
        throw WebViewFeatureInternal.getUnsupportedOperationException();
    }

    @Override // androidx.webkit.JavaScriptReplyProxy
    public void postMessage(byte[] arrayBuffer) {
        Objects.requireNonNull(arrayBuffer, "ArrayBuffer must be non-null");
        ApiFeature.NoFramework feature = WebViewFeatureInternal.WEB_MESSAGE_ARRAY_BUFFER;
        if (feature.isSupportedByWebView()) {
            this.mBoundaryInterface.postMessageWithPayload(BoundaryInterfaceReflectionUtil.createInvocationHandlerFor(new WebMessagePayloadAdapter(arrayBuffer)));
            return;
        }
        throw WebViewFeatureInternal.getUnsupportedOperationException();
    }
}
