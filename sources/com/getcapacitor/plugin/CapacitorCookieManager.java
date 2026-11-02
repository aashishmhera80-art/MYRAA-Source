package com.getcapacitor.plugin;

import com.getcapacitor.Bridge;
import com.getcapacitor.Logger;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/* JADX INFO: loaded from: classes4.dex */
public class CapacitorCookieManager extends CookieManager {
    private final String TAG;
    private final String localUrl;
    private final String serverUrl;
    private final android.webkit.CookieManager webkitCookieManager;

    public CapacitorCookieManager(Bridge bridge) {
        this(null, null, bridge);
    }

    public CapacitorCookieManager(CookieStore store, CookiePolicy policy, Bridge bridge) {
        super(store, policy);
        this.TAG = "CapacitorCookies";
        this.webkitCookieManager = android.webkit.CookieManager.getInstance();
        this.localUrl = bridge.getLocalUrl();
        this.serverUrl = bridge.getServerUrl();
    }

    public void removeSessionCookies() {
        this.webkitCookieManager.removeSessionCookies(null);
    }

    public String getSanitizedDomain(String url) throws Exception {
        if (this.serverUrl != null && !this.serverUrl.isEmpty() && (url == null || url.isEmpty() || this.serverUrl.contains(url))) {
            url = this.serverUrl;
        } else if (this.localUrl != null && !this.localUrl.isEmpty() && (url == null || url.isEmpty() || this.localUrl.contains(url))) {
            url = this.localUrl;
        } else {
            try {
                URI uri = new URI(url);
                String scheme = uri.getScheme();
                if (scheme == null || scheme.isEmpty()) {
                    url = "https://" + url;
                }
            } catch (URISyntaxException e) {
                Logger.error("CapacitorCookies", "Failed to get scheme from URL.", e);
            }
        }
        try {
            new URI(url);
            return url;
        } catch (Exception error) {
            Logger.error("CapacitorCookies", "Failed to get sanitized URL.", error);
            throw error;
        }
    }

    private String getDomainFromCookieString(String cookie) throws URISyntaxException {
        String[] domain = cookie.toLowerCase(Locale.ROOT).split("domain=");
        return getSanitizedDomain(domain.length <= 1 ? null : domain[1].split(";")[0].trim());
    }

    public String getCookieString(String url) {
        try {
            String url2 = getSanitizedDomain(url);
            Logger.info("CapacitorCookies", "Getting cookies at: '" + url2 + "'");
            return this.webkitCookieManager.getCookie(url2);
        } catch (Exception error) {
            Logger.error("CapacitorCookies", "Failed to get cookies at the given URL.", error);
            return null;
        }
    }

    public HttpCookie getCookie(String url, String key) {
        HttpCookie[] cookies = getCookies(url);
        for (HttpCookie cookie : cookies) {
            if (cookie.getName().equals(key)) {
                return cookie;
            }
        }
        return null;
    }

    public HttpCookie[] getCookies(String url) {
        try {
            ArrayList<HttpCookie> cookieList = new ArrayList<>();
            String cookieString = getCookieString(url);
            if (cookieString != null) {
                String[] singleCookie = cookieString.split(";");
                for (String c : singleCookie) {
                    HttpCookie parsed = HttpCookie.parse(c).get(0);
                    parsed.setValue(parsed.getValue());
                    cookieList.add(parsed);
                }
            }
            HttpCookie[] cookies = new HttpCookie[cookieList.size()];
            return (HttpCookie[]) cookieList.toArray(cookies);
        } catch (Exception e) {
            return new HttpCookie[0];
        }
    }

    public void setCookie(String url, String value) {
        try {
            String url2 = getSanitizedDomain(url);
            Logger.info("CapacitorCookies", "Setting cookie '" + value + "' at: '" + url2 + "'");
            this.webkitCookieManager.setCookie(url2, value);
            flush();
        } catch (Exception error) {
            Logger.error("CapacitorCookies", "Failed to set cookie.", error);
        }
    }

    public void setCookie(String url, String key, String value) {
        String cookieValue = key + "=" + value;
        setCookie(url, cookieValue);
    }

    public void setCookie(String url, String key, String value, String expires, String path) {
        String cookieValue = key + "=" + value + "; expires=" + expires + "; path=" + path;
        setCookie(url, cookieValue);
    }

    public void removeAllCookies() {
        this.webkitCookieManager.removeAllCookies(null);
        flush();
    }

    public void flush() {
        this.webkitCookieManager.flush();
    }

    @Override // java.net.CookieManager, java.net.CookieHandler
    public void put(URI uri, Map<String, List<String>> responseHeaders) {
        if (uri == null || responseHeaders == null) {
            return;
        }
        for (String headerKey : responseHeaders.keySet()) {
            if (headerKey != null && (headerKey.equalsIgnoreCase("Set-Cookie2") || headerKey.equalsIgnoreCase("Set-Cookie"))) {
                for (String headerValue : (List) Objects.requireNonNull(responseHeaders.get(headerKey))) {
                    try {
                        setCookie(uri.toString(), headerValue);
                        setCookie(getDomainFromCookieString(headerValue), headerValue);
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    @Override // java.net.CookieManager, java.net.CookieHandler
    public Map<String, List<String>> get(URI uri, Map<String, List<String>> requestHeaders) {
        if (uri == null || requestHeaders == null) {
            throw new IllegalArgumentException("Argument is null");
        }
        String url = uri.toString();
        Map<String, List<String>> res = new HashMap<>();
        String cookie = getCookieString(url);
        if (cookie != null) {
            res.put("Cookie", Collections.singletonList(cookie));
        }
        return res;
    }

    @Override // java.net.CookieManager
    public CookieStore getCookieStore() {
        throw new UnsupportedOperationException();
    }
}
