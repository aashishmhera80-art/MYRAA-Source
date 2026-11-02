package androidx.webkit.internal;

import android.os.Handler;
import android.webkit.WebMessage;
import android.webkit.WebMessagePort;
import androidx.webkit.WebMessageCompat;
import androidx.webkit.WebMessagePortCompat;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import org.chromium.support_lib_boundary.WebMessagePortBoundaryInterface;
import org.chromium.support_lib_boundary.util.BoundaryInterfaceReflectionUtil;

/* JADX INFO: loaded from: classes.dex */
public class WebMessagePortImpl extends WebMessagePortCompat {
    private WebMessagePortBoundaryInterface mBoundaryInterface;
    private WebMessagePort mFrameworksImpl;

    public WebMessagePortImpl(WebMessagePort frameworksImpl) {
        this.mFrameworksImpl = frameworksImpl;
    }

    public WebMessagePortImpl(InvocationHandler invocationHandler) {
        this.mBoundaryInterface = (WebMessagePortBoundaryInterface) BoundaryInterfaceReflectionUtil.castToSuppLibClass(WebMessagePortBoundaryInterface.class, invocationHandler);
    }

    private WebMessagePort getFrameworksImpl() {
        if (this.mFrameworksImpl == null) {
            this.mFrameworksImpl = WebViewGlueCommunicator.getCompatConverter().convertWebMessagePort(Proxy.getInvocationHandler(this.mBoundaryInterface));
        }
        return this.mFrameworksImpl;
    }

    private WebMessagePortBoundaryInterface getBoundaryInterface() {
        if (this.mBoundaryInterface == null) {
            this.mBoundaryInterface = (WebMessagePortBoundaryInterface) BoundaryInterfaceReflectionUtil.castToSuppLibClass(WebMessagePortBoundaryInterface.class, WebViewGlueCommunicator.getCompatConverter().convertWebMessagePort(this.mFrameworksImpl));
        }
        return this.mBoundaryInterface;
    }

    @Override // androidx.webkit.WebMessagePortCompat
    public void postMessage(WebMessageCompat message) {
        ApiFeature.M feature = WebViewFeatureInternal.WEB_MESSAGE_PORT_POST_MESSAGE;
        if (feature.isSupportedByFramework() && message.getType() == 0) {
            ApiHelperForM.postMessage(getFrameworksImpl(), compatToFrameworkMessage(message));
        } else {
            if (feature.isSupportedByWebView() && WebMessageAdapter.isMessagePayloadTypeSupportedByWebView(message.getType())) {
                getBoundaryInterface().postMessage(BoundaryInterfaceReflectionUtil.createInvocationHandlerFor(new WebMessageAdapter(message)));
                return;
            }
            throw WebViewFeatureInternal.getUnsupportedOperationException();
        }
    }

    @Override // androidx.webkit.WebMessagePortCompat
    public void close() {
        ApiFeature.M feature = WebViewFeatureInternal.WEB_MESSAGE_PORT_CLOSE;
        if (feature.isSupportedByFramework()) {
            ApiHelperForM.close(getFrameworksImpl());
        } else {
            if (feature.isSupportedByWebView()) {
                getBoundaryInterface().close();
                return;
            }
            throw WebViewFeatureInternal.getUnsupportedOperationException();
        }
    }

    @Override // androidx.webkit.WebMessagePortCompat
    public void setWebMessageCallback(WebMessagePortCompat.WebMessageCallbackCompat callback) {
        ApiFeature.M feature = WebViewFeatureInternal.WEB_MESSAGE_PORT_SET_MESSAGE_CALLBACK;
        if (feature.isSupportedByWebView()) {
            getBoundaryInterface().setWebMessageCallback(BoundaryInterfaceReflectionUtil.createInvocationHandlerFor(new WebMessageCallbackAdapter(callback)));
        } else {
            if (feature.isSupportedByFramework()) {
                ApiHelperForM.setWebMessageCallback(getFrameworksImpl(), callback);
                return;
            }
            throw WebViewFeatureInternal.getUnsupportedOperationException();
        }
    }

    @Override // androidx.webkit.WebMessagePortCompat
    public void setWebMessageCallback(Handler handler, WebMessagePortCompat.WebMessageCallbackCompat callback) {
        ApiFeature.M feature = WebViewFeatureInternal.CREATE_WEB_MESSAGE_CHANNEL;
        if (feature.isSupportedByWebView()) {
            getBoundaryInterface().setWebMessageCallback(BoundaryInterfaceReflectionUtil.createInvocationHandlerFor(new WebMessageCallbackAdapter(callback)), handler);
        } else {
            if (feature.isSupportedByFramework()) {
                ApiHelperForM.setWebMessageCallback(getFrameworksImpl(), callback, handler);
                return;
            }
            throw WebViewFeatureInternal.getUnsupportedOperationException();
        }
    }

    @Override // androidx.webkit.WebMessagePortCompat
    public WebMessagePort getFrameworkPort() {
        return getFrameworksImpl();
    }

    @Override // androidx.webkit.WebMessagePortCompat
    public InvocationHandler getInvocationHandler() {
        return Proxy.getInvocationHandler(getBoundaryInterface());
    }

    public static WebMessagePortCompat[] portsToCompat(WebMessagePort[] ports) {
        if (ports == null) {
            return null;
        }
        WebMessagePortCompat[] compatPorts = new WebMessagePortCompat[ports.length];
        for (int n = 0; n < ports.length; n++) {
            compatPorts[n] = new WebMessagePortImpl(ports[n]);
        }
        return compatPorts;
    }

    public static WebMessagePort[] compatToPorts(WebMessagePortCompat[] compatPorts) {
        if (compatPorts == null) {
            return null;
        }
        WebMessagePort[] ports = new WebMessagePort[compatPorts.length];
        for (int n = 0; n < ports.length; n++) {
            ports[n] = compatPorts[n].getFrameworkPort();
        }
        return ports;
    }

    public static WebMessage compatToFrameworkMessage(WebMessageCompat message) {
        return ApiHelperForM.createWebMessage(message);
    }

    public static WebMessageCompat frameworkMessageToCompat(WebMessage message) {
        return ApiHelperForM.createWebMessageCompat(message);
    }
}
