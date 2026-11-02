package com.getcapacitor;

import android.content.Context;
import android.net.Uri;
import android.util.Base64;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import androidx.core.graphics.TypefaceCompat;
import com.getcapacitor.plugin.util.CapacitorHttpUrlConnection;
import com.getcapacitor.plugin.util.HttpRequestHandler;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes3.dex */
public class WebViewLocalServer {
    private static final String capacitorContentStart = "/_capacitor_content_";
    private static final String capacitorFileStart = "/_capacitor_file_";
    private final ArrayList<String> authorities;
    private String basePath;
    private final Bridge bridge;
    private final boolean html5mode;
    private boolean isAsset;
    private final JSInjector jsInjector;
    private final AndroidProtocolHandler protocolHandler;
    private final UriMatcher uriMatcher = new UriMatcher(null);

    public static abstract class PathHandler {
        private String charset;
        private String encoding;
        protected String mimeType;
        private String reasonPhrase;
        private Map<String, String> responseHeaders;
        private int statusCode;

        public abstract InputStream handle(Uri uri);

        public PathHandler() {
            this(null, null, 200, "OK", null);
        }

        public PathHandler(String encoding, String charset, int statusCode, String reasonPhrase, Map<String, String> responseHeaders) {
            Map<String, String> tempResponseHeaders;
            this.encoding = encoding;
            this.charset = charset;
            this.statusCode = statusCode;
            this.reasonPhrase = reasonPhrase;
            if (responseHeaders == null) {
                tempResponseHeaders = new HashMap<>();
            } else {
                tempResponseHeaders = responseHeaders;
            }
            tempResponseHeaders.put("Cache-Control", "no-cache");
            this.responseHeaders = tempResponseHeaders;
        }

        public InputStream handle(WebResourceRequest request) {
            return handle(request.getUrl());
        }

        public String getEncoding() {
            return this.encoding;
        }

        public String getCharset() {
            return this.charset;
        }

        public int getStatusCode() {
            return this.statusCode;
        }

        public String getReasonPhrase() {
            return this.reasonPhrase;
        }

        @Deprecated(forRemoval = TypefaceCompat.DOWNLOADABLE_FONT_TRACING)
        public Map<String, String> getResponseHeaders() {
            return this.responseHeaders;
        }

        public Map<String, String> buildDefaultResponseHeaders() {
            return new HashMap(this.responseHeaders);
        }
    }

    WebViewLocalServer(Context context, Bridge bridge, JSInjector jsInjector, ArrayList<String> authorities, boolean html5mode) {
        this.html5mode = html5mode;
        this.protocolHandler = new AndroidProtocolHandler(context.getApplicationContext());
        this.authorities = authorities;
        this.bridge = bridge;
        this.jsInjector = jsInjector;
    }

    private static Uri parseAndVerifyUrl(String url) {
        if (url == null) {
            return null;
        }
        Uri uri = Uri.parse(url);
        if (uri == null) {
            Logger.error("Malformed URL: " + url);
            return null;
        }
        String path = uri.getPath();
        if (path == null || path.isEmpty()) {
            Logger.error("URL does not have a path: " + url);
            return null;
        }
        return uri;
    }

    public WebResourceResponse shouldInterceptRequest(WebResourceRequest request) {
        PathHandler handler;
        Uri loadingUrl = request.getUrl();
        if (loadingUrl.getPath() != null && loadingUrl.getPath().startsWith(Bridge.CAPACITOR_HTTP_INTERCEPTOR_START)) {
            Logger.debug("Handling CapacitorHttp request: " + loadingUrl);
            try {
                return handleCapacitorHttpRequest(request);
            } catch (Exception e) {
                Logger.error(e.getLocalizedMessage());
                return null;
            }
        }
        synchronized (this.uriMatcher) {
            handler = (PathHandler) this.uriMatcher.match(request.getUrl());
        }
        if (handler == null) {
            return null;
        }
        if (isLocalFile(loadingUrl) || isMainUrl(loadingUrl) || !isAllowedUrl(loadingUrl) || isErrorUrl(loadingUrl)) {
            Logger.debug("Handling local request: " + request.getUrl().toString());
            return handleLocalRequest(request, handler);
        }
        return handleProxyRequest(request, handler);
    }

    private boolean isLocalFile(Uri uri) {
        String path = uri.getPath();
        return path.startsWith("/_capacitor_content_") || path.startsWith("/_capacitor_file_");
    }

    private boolean isErrorUrl(Uri uri) {
        String url = uri.toString();
        return url.equals(this.bridge.getErrorUrl());
    }

    private boolean isMainUrl(Uri loadingUrl) {
        return this.bridge.getServerUrl() == null && loadingUrl.getHost().equalsIgnoreCase(this.bridge.getHost());
    }

    private boolean isAllowedUrl(Uri loadingUrl) {
        return this.bridge.getServerUrl() != null || this.bridge.getAppAllowNavigationMask().matches(loadingUrl.getHost());
    }

    private String getReasonPhraseFromResponseCode(int code) {
        switch (code) {
            case 100:
                return "Continue";
            case 101:
                return "Switching Protocols";
            case 200:
                return "OK";
            case 201:
                return "Created";
            case 202:
                return "Accepted";
            case 203:
                return "Non-Authoritative Information";
            case 204:
                return "No Content";
            case 205:
                return "Reset Content";
            case 206:
                return "Partial Content";
            case 300:
                return "Multiple Choices";
            case 301:
                return "Moved Permanently";
            case 302:
                return "Found";
            case 303:
                return "See Other";
            case 304:
                return "Not Modified";
            case 400:
                return "Bad Request";
            case 401:
                return "Unauthorized";
            case 403:
                return "Forbidden";
            case 404:
                return "Not Found";
            case 405:
                return "Method Not Allowed";
            case 406:
                return "Not Acceptable";
            case 407:
                return "Proxy Authentication Required";
            case 408:
                return "Request Timeout";
            case 409:
                return "Conflict";
            case 410:
                return "Gone";
            case 500:
                return "Internal Server Error";
            case 501:
                return "Not Implemented";
            case 502:
                return "Bad Gateway";
            case 503:
                return "Service Unavailable";
            case 504:
                return "Gateway Timeout";
            case 505:
                return "HTTP Version Not Supported";
            default:
                return "Unknown";
        }
    }

    private WebResourceResponse handleCapacitorHttpRequest(WebResourceRequest request) throws IOException {
        String mimeType;
        String urlString;
        String urlString2 = request.getUrl().getQueryParameter(Bridge.CAPACITOR_HTTP_INTERCEPTOR_URL_PARAM);
        URL url = new URL(urlString2);
        JSObject headers = new JSObject();
        for (Map.Entry<String, String> header : request.getRequestHeaders().entrySet()) {
            headers.put(header.getKey(), header.getValue());
        }
        String userAgentValue = headers.getString("x-cap-user-agent");
        if (userAgentValue != null) {
            headers.put("User-Agent", userAgentValue);
        }
        headers.remove("x-cap-user-agent");
        HttpRequestHandler.HttpURLConnectionBuilder connectionBuilder = new HttpRequestHandler.HttpURLConnectionBuilder().setUrl(url).setMethod(request.getMethod()).setHeaders(headers).openConnection();
        CapacitorHttpUrlConnection connection = connectionBuilder.build();
        if (!HttpRequestHandler.isDomainExcludedFromSSL(this.bridge, url).booleanValue()) {
            connection.setSSLSocketFactory(this.bridge);
        }
        connection.connect();
        String mimeType2 = null;
        Map<String, String> responseHeaders = new LinkedHashMap<>();
        String encoding = null;
        for (Map.Entry<String, List<String>> entry : connection.getHeaderFields().entrySet()) {
            StringBuilder builder = new StringBuilder();
            for (String value : entry.getValue()) {
                builder.append(value);
                builder.append(", ");
            }
            builder.setLength(builder.length() - 2);
            if ("Content-Type".equalsIgnoreCase(entry.getKey())) {
                String[] contentTypeParts = builder.toString().split(";");
                mimeType2 = contentTypeParts[0].trim();
                if (contentTypeParts.length <= 1) {
                    urlString = urlString2;
                } else {
                    String[] encodingParts = contentTypeParts[1].split("=");
                    urlString = urlString2;
                    if (encodingParts.length > 1) {
                        encoding = encodingParts[1].trim();
                    }
                }
            } else {
                urlString = urlString2;
                responseHeaders.put(entry.getKey(), builder.toString());
            }
            urlString2 = urlString;
        }
        InputStream inputStream = connection.getErrorStream();
        if (inputStream == null) {
            inputStream = connection.getInputStream();
        }
        if (mimeType2 == null) {
            String mimeType3 = getMimeType(request.getUrl().getPath(), inputStream);
            mimeType = mimeType3;
        } else {
            mimeType = mimeType2;
        }
        int responseCode = connection.getResponseCode();
        String reasonPhrase = getReasonPhraseFromResponseCode(responseCode);
        return new WebResourceResponse(mimeType, encoding, responseCode, reasonPhrase, responseHeaders, inputStream);
    }

    /* JADX WARN: Unreachable blocks removed: 2, instructions: 3 */
    private WebResourceResponse handleLocalRequest(WebResourceRequest request, PathHandler handler) {
        InputStream responseStream;
        InputStream responseStream2;
        InputStream responseStream3;
        int statusCode;
        int range;
        String path = request.getUrl().getPath();
        Map<String, String> requestHeaders = request.getRequestHeaders();
        String rangeString = requestHeaders.get(requestHeaders.get("Range") == null ? "range" : "Range");
        if (rangeString == null) {
            if (isLocalFile(request.getUrl()) || isErrorUrl(request.getUrl())) {
                InputStream responseStream4 = new LollipopLazyInputStream(handler, request);
                String mimeType = getMimeType(request.getUrl().getPath(), responseStream4);
                int statusCode2 = getStatusCode(responseStream4, handler.getStatusCode());
                return new WebResourceResponse(mimeType, handler.getEncoding(), statusCode2, handler.getReasonPhrase(), handler.buildDefaultResponseHeaders(), responseStream4);
            }
            if (path.equals("/cordova.js")) {
                return new WebResourceResponse("application/javascript", handler.getEncoding(), handler.getStatusCode(), handler.getReasonPhrase(), handler.buildDefaultResponseHeaders(), null);
            }
            if (path.equals("/") || (!request.getUrl().getLastPathSegment().contains(".") && this.html5mode)) {
                try {
                    String startPath = this.basePath + "/index.html";
                    if (this.bridge.getRouteProcessor() != null) {
                        ProcessedRoute processedRoute = this.bridge.getRouteProcessor().process(this.basePath, "/index.html");
                        startPath = processedRoute.getPath();
                        this.isAsset = processedRoute.isAsset();
                    }
                    if (this.isAsset) {
                        responseStream = this.protocolHandler.openAsset(startPath);
                    } else {
                        responseStream = this.protocolHandler.openFile(startPath);
                    }
                    if (this.jsInjector == null) {
                        responseStream2 = responseStream;
                    } else {
                        responseStream2 = this.jsInjector.getInjectedStream(responseStream);
                    }
                    int statusCode3 = getStatusCode(responseStream2, handler.getStatusCode());
                    return new WebResourceResponse("text/html", handler.getEncoding(), statusCode3, handler.getReasonPhrase(), handler.buildDefaultResponseHeaders(), responseStream2);
                } catch (IOException e) {
                    Logger.error("Unable to open index.html", e);
                    return null;
                }
            }
            if ("/favicon.ico".equalsIgnoreCase(path)) {
                try {
                    return new WebResourceResponse("image/png", null, null);
                } catch (Exception e2) {
                    Logger.error("favicon handling failed", e2);
                }
            }
            int periodIndex = path.lastIndexOf(".");
            if (periodIndex < 0) {
                return null;
            }
            String ext = path.substring(path.lastIndexOf("."));
            InputStream responseStream5 = new LollipopLazyInputStream(handler, request);
            if (ext.equals(".html") && this.jsInjector != null) {
                responseStream3 = this.jsInjector.getInjectedStream(responseStream5);
            } else {
                responseStream3 = responseStream5;
            }
            String mimeType2 = getMimeType(path, responseStream3);
            int statusCode4 = getStatusCode(responseStream3, handler.getStatusCode());
            return new WebResourceResponse(mimeType2, handler.getEncoding(), statusCode4, handler.getReasonPhrase(), handler.buildDefaultResponseHeaders(), responseStream3);
        }
        InputStream responseStream6 = new LollipopLazyInputStream(handler, request);
        String mimeType3 = getMimeType(path, responseStream6);
        Map<String, String> tempResponseHeaders = handler.buildDefaultResponseHeaders();
        try {
            int totalRange = responseStream6.available();
            String[] parts = rangeString.split("=");
            String[] streamParts = parts[1].split("-");
            String fromRange = streamParts[0];
            int range2 = totalRange - 1;
            if (streamParts.length <= 1) {
                range = range2;
            } else {
                try {
                    range = Integer.parseInt(streamParts[1]);
                } catch (IOException e3) {
                    statusCode = 404;
                    return new WebResourceResponse(mimeType3, handler.getEncoding(), statusCode, handler.getReasonPhrase(), tempResponseHeaders, responseStream6);
                }
            }
            try {
                tempResponseHeaders.put("Accept-Ranges", "bytes");
                try {
                    tempResponseHeaders.put("Content-Range", "bytes " + fromRange + "-" + range + "/" + totalRange);
                    statusCode = 206;
                } catch (IOException e4) {
                    statusCode = 404;
                }
            } catch (IOException e5) {
                statusCode = 404;
                return new WebResourceResponse(mimeType3, handler.getEncoding(), statusCode, handler.getReasonPhrase(), tempResponseHeaders, responseStream6);
            }
        } catch (IOException e6) {
            statusCode = 404;
            return new WebResourceResponse(mimeType3, handler.getEncoding(), statusCode, handler.getReasonPhrase(), tempResponseHeaders, responseStream6);
        }
        return new WebResourceResponse(mimeType3, handler.getEncoding(), statusCode, handler.getReasonPhrase(), tempResponseHeaders, responseStream6);
    }

    public InputStream getJavaScriptInjectedStream(InputStream original) {
        if (this.jsInjector != null) {
            return this.jsInjector.getInjectedStream(original);
        }
        return original;
    }

    private WebResourceResponse handleProxyRequest(WebResourceRequest request, PathHandler handler) {
        if (this.jsInjector != null) {
            String method = request.getMethod();
            if (method.equals("GET")) {
                try {
                    String url = request.getUrl().toString();
                    Map<String, String> headers = request.getRequestHeaders();
                    boolean isHtmlText = false;
                    for (Map.Entry<String, String> header : headers.entrySet()) {
                        if (header.getKey().equalsIgnoreCase("Accept") && header.getValue().toLowerCase().contains("text/html")) {
                            isHtmlText = true;
                            break;
                        }
                    }
                    if (isHtmlText) {
                        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                        for (Map.Entry<String, String> header2 : headers.entrySet()) {
                            conn.setRequestProperty(header2.getKey(), header2.getValue());
                        }
                        String getCookie = CookieManager.getInstance().getCookie(url);
                        if (getCookie != null) {
                            conn.setRequestProperty("Cookie", getCookie);
                        }
                        conn.setRequestMethod(method);
                        conn.setReadTimeout(30000);
                        conn.setConnectTimeout(30000);
                        if (request.getUrl().getUserInfo() != null) {
                            byte[] userInfoBytes = request.getUrl().getUserInfo().getBytes(StandardCharsets.UTF_8);
                            String base64 = Base64.encodeToString(userInfoBytes, 2);
                            conn.setRequestProperty("Authorization", "Basic " + base64);
                        }
                        List<String> cookies = conn.getHeaderFields().get("Set-Cookie");
                        if (cookies != null) {
                            for (String cookie : cookies) {
                                CookieManager.getInstance().setCookie(url, cookie);
                            }
                        }
                        InputStream responseStream = conn.getInputStream();
                        return new WebResourceResponse("text/html", handler.getEncoding(), handler.getStatusCode(), handler.getReasonPhrase(), handler.buildDefaultResponseHeaders(), this.jsInjector.getInjectedStream(responseStream));
                    }
                    return null;
                } catch (Exception ex) {
                    this.bridge.handleAppUrlLoadError(ex);
                    return null;
                }
            }
            return null;
        }
        return null;
    }

    private String getMimeType(String path, InputStream stream) {
        String mimeType = null;
        try {
            String mimeType2 = URLConnection.guessContentTypeFromName(path);
            if (mimeType2 != null && path.endsWith(".js") && mimeType2.equals("image/x-icon")) {
                Logger.debug("We shouldn't be here");
            }
            if (mimeType2 != null) {
                return mimeType2;
            }
            if (!path.endsWith(".js") && !path.endsWith(".mjs")) {
                if (path.endsWith(".wasm")) {
                    return "application/wasm";
                }
                mimeType = URLConnection.guessContentTypeFromStream(stream);
                return mimeType;
            }
            return "application/javascript";
        } catch (Exception ex) {
            Logger.error("Unable to get mime type" + path, ex);
            return mimeType;
        }
    }

    private int getStatusCode(InputStream stream, int defaultCode) {
        try {
            if (stream.available() != -1) {
                return defaultCode;
            }
            return 404;
        } catch (IOException e) {
            return 500;
        }
    }

    void register(Uri uri, PathHandler handler) {
        synchronized (this.uriMatcher) {
            this.uriMatcher.addURI(uri.getScheme(), uri.getAuthority(), uri.getPath(), handler);
        }
    }

    public void hostAssets(String assetPath) {
        this.isAsset = true;
        this.basePath = assetPath;
        createHostingDetails();
    }

    public void hostFiles(String basePath) {
        this.isAsset = false;
        this.basePath = basePath;
        createHostingDetails();
    }

    private void createHostingDetails() {
        final String assetPath = this.basePath;
        if (assetPath.indexOf(42) != -1) {
            throw new IllegalArgumentException("assetPath cannot contain the '*' character.");
        }
        PathHandler handler = new PathHandler() { // from class: com.getcapacitor.WebViewLocalServer.1
            @Override // com.getcapacitor.WebViewLocalServer.PathHandler
            public InputStream handle(Uri url) {
                String path = url.getPath();
                RouteProcessor routeProcessor = WebViewLocalServer.this.bridge.getRouteProcessor();
                boolean ignoreAssetPath = false;
                if (routeProcessor != null) {
                    ProcessedRoute processedRoute = WebViewLocalServer.this.bridge.getRouteProcessor().process("", path);
                    path = processedRoute.getPath();
                    WebViewLocalServer.this.isAsset = processedRoute.isAsset();
                    ignoreAssetPath = processedRoute.isIgnoreAssetPath();
                }
                try {
                    if (path.startsWith("/_capacitor_content_")) {
                        InputStream stream = WebViewLocalServer.this.protocolHandler.openContentUrl(url);
                        return stream;
                    }
                    if (path.startsWith("/_capacitor_file_")) {
                        InputStream stream2 = WebViewLocalServer.this.protocolHandler.openFile(path);
                        return stream2;
                    }
                    if (!WebViewLocalServer.this.isAsset) {
                        if (routeProcessor == null) {
                            path = WebViewLocalServer.this.basePath + url.getPath();
                        }
                        InputStream stream3 = WebViewLocalServer.this.protocolHandler.openFile(path);
                        return stream3;
                    }
                    if (ignoreAssetPath) {
                        InputStream stream4 = WebViewLocalServer.this.protocolHandler.openAsset(path);
                        return stream4;
                    }
                    InputStream stream5 = WebViewLocalServer.this.protocolHandler.openAsset(assetPath + path);
                    return stream5;
                } catch (IOException e) {
                    Logger.error("Unable to open asset URL: " + url);
                    return null;
                }
            }
        };
        for (String authority : this.authorities) {
            registerUriForScheme("http", handler, authority);
            registerUriForScheme("https", handler, authority);
            String customScheme = this.bridge.getScheme();
            if (!customScheme.equals("http") && !customScheme.equals("https")) {
                registerUriForScheme(customScheme, handler, authority);
            }
        }
    }

    private void registerUriForScheme(String scheme, PathHandler handler, String authority) {
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme(scheme);
        uriBuilder.authority(authority);
        uriBuilder.path("");
        Uri uriPrefix = uriBuilder.build();
        register(Uri.withAppendedPath(uriPrefix, "/"), handler);
        register(Uri.withAppendedPath(uriPrefix, "**"), handler);
    }

    private static abstract class LazyInputStream extends InputStream {
        protected final PathHandler handler;
        private InputStream is = null;

        protected abstract InputStream handle();

        public LazyInputStream(PathHandler handler) {
            this.handler = handler;
        }

        private InputStream getInputStream() {
            if (this.is == null) {
                this.is = handle();
            }
            return this.is;
        }

        @Override // java.io.InputStream
        public int available() throws IOException {
            InputStream is = getInputStream();
            if (is != null) {
                return is.available();
            }
            return -1;
        }

        @Override // java.io.InputStream
        public int read() throws IOException {
            InputStream is = getInputStream();
            if (is != null) {
                return is.read();
            }
            return -1;
        }

        @Override // java.io.InputStream
        public int read(byte[] b) throws IOException {
            InputStream is = getInputStream();
            if (is != null) {
                return is.read(b);
            }
            return -1;
        }

        @Override // java.io.InputStream
        public int read(byte[] b, int off, int len) throws IOException {
            InputStream is = getInputStream();
            if (is != null) {
                return is.read(b, off, len);
            }
            return -1;
        }

        @Override // java.io.InputStream
        public long skip(long n) throws IOException {
            InputStream is = getInputStream();
            if (is != null) {
                return is.skip(n);
            }
            return 0L;
        }
    }

    private static class LollipopLazyInputStream extends LazyInputStream {
        private InputStream is;
        private WebResourceRequest request;

        public LollipopLazyInputStream(PathHandler handler, WebResourceRequest request) {
            super(handler);
            this.request = request;
        }

        @Override // com.getcapacitor.WebViewLocalServer.LazyInputStream
        protected InputStream handle() {
            return this.handler.handle(this.request);
        }
    }

    public String getBasePath() {
        return this.basePath;
    }
}
