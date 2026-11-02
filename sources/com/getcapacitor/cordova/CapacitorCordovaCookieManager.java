package com.getcapacitor.cordova;

import android.webkit.CookieManager;
import android.webkit.WebView;
import org.apache.cordova.ICordovaCookieManager;

/* JADX INFO: loaded from: classes2.dex */
class CapacitorCordovaCookieManager implements ICordovaCookieManager {
    private final CookieManager cookieManager = CookieManager.getInstance();
    protected final WebView webView;

    public CapacitorCordovaCookieManager(WebView webview) {
        this.webView = webview;
        this.cookieManager.setAcceptThirdPartyCookies(this.webView, true);
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
