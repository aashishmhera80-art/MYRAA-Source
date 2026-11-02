package org.apache.cordova.engine;

import android.webkit.CookieManager;
import android.webkit.WebView;
import org.apache.cordova.ICordovaCookieManager;

/* JADX INFO: loaded from: classes.dex */
class SystemCookieManager implements ICordovaCookieManager {
    private final CookieManager cookieManager = CookieManager.getInstance();
    protected final WebView webView;

    public SystemCookieManager(WebView webview) {
        this.webView = webview;
        this.cookieManager.setAcceptThirdPartyCookies(this.webView, true);
    }

    public void setAcceptFileSchemeCookies() {
        CookieManager.setAcceptFileSchemeCookies(true);
    }

    @Override // org.apache.cordova.ICordovaCookieManager
    public void setCookiesEnabled(boolean accept) {
        this.cookieManager.setAcceptCookie(accept);
    }

    @Override // org.apache.cordova.ICordovaCookieManager
    public void setCookie(String url, String value) {
        this.cookieManager.setCookie(url, value);
    }

    @Override // org.apache.cordova.ICordovaCookieManager
    public String getCookie(String url) {
        return this.cookieManager.getCookie(url);
    }

    @Override // org.apache.cordova.ICordovaCookieManager
    public void clearCookies() {
        this.cookieManager.removeAllCookies(null);
    }

    @Override // org.apache.cordova.ICordovaCookieManager
    public void flush() {
        this.cookieManager.flush();
    }
}
