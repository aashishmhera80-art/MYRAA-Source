package androidx.webkit.internal;

import android.net.Uri;
import android.os.Handler;
import android.webkit.WebMessage;
import android.webkit.WebMessagePort;
import android.webkit.WebResourceError;
import android.webkit.WebSettings;
import android.webkit.WebView;
import androidx.webkit.WebMessageCompat;
import androidx.webkit.WebMessagePortCompat;
import androidx.webkit.WebViewCompat;

/* JADX INFO: loaded from: classes.dex */
public class ApiHelperForM {
    private ApiHelperForM() {
    }

    public static void postMessage(WebMessagePort webMessagePort, WebMessage webMessage) {
        webMessagePort.postMessage(webMessage);
    }

    public static void close(WebMessagePort webMessagePort) {
        webMessagePort.close();
    }

    public static void setWebMessageCallback(WebMessagePort frameworksImpl, final WebMessagePortCompat.WebMessageCallbackCompat callback) {
        frameworksImpl.setWebMessageCallback(new WebMessagePort.WebMessageCallback() { // from class: androidx.webkit.internal.ApiHelperForM.1
            @Override // android.webkit.WebMessagePort.WebMessageCallback
            public void onMessage(WebMessagePort port, WebMessage message) {
                callback.onMessage(new WebMessagePortImpl(port), WebMessagePortImpl.frameworkMessageToCompat(message));
            }
        });
    }

    public static void setWebMessageCallback(WebMessagePort frameworksImpl, final WebMessagePortCompat.WebMessageCallbackCompat callback, Handler handler) {
        frameworksImpl.setWebMessageCallback(new WebMessagePort.WebMessageCallback() { // from class: androidx.webkit.internal.ApiHelperForM.2
            @Override // android.webkit.WebMessagePort.WebMessageCallback
            public void onMessage(WebMessagePort port, WebMessage message) {
                callback.onMessage(new WebMessagePortImpl(port), WebMessagePortImpl.frameworkMessageToCompat(message));
            }
        }, handler);
    }

    public static WebMessage createWebMessage(WebMessageCompat message) {
        return new WebMessage(message.getData(), WebMessagePortImpl.compatToPorts(message.getPorts()));
    }

    public static WebMessageCompat createWebMessageCompat(WebMessage webMessage) {
        return new WebMessageCompat(webMessage.getData(), WebMessagePortImpl.portsToCompat(webMessage.getPorts()));
    }

    public static int getErrorCode(WebResourceError webResourceError) {
        return webResourceError.getErrorCode();
    }

    public static CharSequence getDescription(WebResourceError webResourceError) {
        return webResourceError.getDescription();
    }

    public static void setOffscreenPreRaster(WebSettings webSettings, boolean b) {
        webSettings.setOffscreenPreRaster(b);
    }

    public static boolean getOffscreenPreRaster(WebSettings webSettings) {
        return webSettings.getOffscreenPreRaster();
    }

    public static void postVisualStateCallback(WebView webView, long requestId, final WebViewCompat.VisualStateCallback callback) {
        webView.postVisualStateCallback(requestId, new WebView.VisualStateCallback() { // from class: androidx.webkit.internal.ApiHelperForM.3
            @Override // android.webkit.WebView.VisualStateCallback
            public void onComplete(long l) {
                callback.onComplete(l);
            }
        });
    }

    public static void postWebMessage(WebView webView, WebMessage message, Uri targetOrigin) {
        webView.postWebMessage(message, targetOrigin);
    }

    public static WebMessagePort[] createWebMessageChannel(WebView webView) {
        return webView.createWebMessageChannel();
    }
}
