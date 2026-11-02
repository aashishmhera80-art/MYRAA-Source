package androidx.webkit.internal;

import androidx.webkit.WebMessageCompat;
import androidx.webkit.WebMessagePortCompat;
import java.lang.reflect.InvocationHandler;
import java.util.Objects;
import org.chromium.support_lib_boundary.WebMessageBoundaryInterface;
import org.chromium.support_lib_boundary.WebMessagePayloadBoundaryInterface;
import org.chromium.support_lib_boundary.util.BoundaryInterfaceReflectionUtil;

/* JADX INFO: loaded from: classes.dex */
public class WebMessageAdapter implements WebMessageBoundaryInterface {
    private static final String[] sFeatures = {"WEB_MESSAGE_ARRAY_BUFFER"};
    private final WebMessageCompat mWebMessageCompat;

    public WebMessageAdapter(WebMessageCompat webMessage) {
        this.mWebMessageCompat = webMessage;
    }

    @Override // org.chromium.support_lib_boundary.WebMessageBoundaryInterface
    @Deprecated
    public String getData() {
        return this.mWebMessageCompat.getData();
    }

    @Override // org.chromium.support_lib_boundary.WebMessageBoundaryInterface
    public InvocationHandler getMessagePayload() {
        WebMessagePayloadAdapter adapter;
        switch (this.mWebMessageCompat.getType()) {
            case 0:
                adapter = new WebMessagePayloadAdapter(this.mWebMessageCompat.getData());
                break;
            case 1:
                adapter = new WebMessagePayloadAdapter((byte[]) Objects.requireNonNull(this.mWebMessageCompat.getArrayBuffer()));
                break;
            default:
                throw new IllegalStateException("Unknown web message payload type: " + this.mWebMessageCompat.getType());
        }
        return BoundaryInterfaceReflectionUtil.createInvocationHandlerFor(adapter);
    }

    @Override // org.chromium.support_lib_boundary.WebMessageBoundaryInterface
    public InvocationHandler[] getPorts() {
        WebMessagePortCompat[] ports = this.mWebMessageCompat.getPorts();
        if (ports == null) {
            return null;
        }
        InvocationHandler[] invocationHandlers = new InvocationHandler[ports.length];
        for (int n = 0; n < ports.length; n++) {
            invocationHandlers[n] = ports[n].getInvocationHandler();
        }
        return invocationHandlers;
    }

    @Override // org.chromium.support_lib_boundary.FeatureFlagHolderBoundaryInterface
    public String[] getSupportedFeatures() {
        return sFeatures;
    }

    public static boolean isMessagePayloadTypeSupportedByWebView(int type) {
        if (type != 0) {
            return type == 1 && WebViewFeatureInternal.WEB_MESSAGE_ARRAY_BUFFER.isSupportedByWebView();
        }
        return true;
    }

    public static WebMessageCompat webMessageCompatFromBoundaryInterface(WebMessageBoundaryInterface boundaryInterface) {
        WebMessagePortCompat[] ports = toWebMessagePortCompats(boundaryInterface.getPorts());
        if (WebViewFeatureInternal.WEB_MESSAGE_ARRAY_BUFFER.isSupportedByWebView()) {
            WebMessagePayloadBoundaryInterface payloadInterface = (WebMessagePayloadBoundaryInterface) BoundaryInterfaceReflectionUtil.castToSuppLibClass(WebMessagePayloadBoundaryInterface.class, boundaryInterface.getMessagePayload());
            int type = payloadInterface.getType();
            switch (type) {
                case 0:
                    return new WebMessageCompat(payloadInterface.getAsString(), ports);
                case 1:
                    return new WebMessageCompat(payloadInterface.getAsArrayBuffer(), ports);
                default:
                    return null;
            }
        }
        return new WebMessageCompat(boundaryInterface.getData(), ports);
    }

    private static WebMessagePortCompat[] toWebMessagePortCompats(InvocationHandler[] ports) {
        WebMessagePortCompat[] compatPorts = new WebMessagePortCompat[ports.length];
        for (int n = 0; n < ports.length; n++) {
            compatPorts[n] = new WebMessagePortImpl(ports[n]);
        }
        return compatPorts;
    }
}
