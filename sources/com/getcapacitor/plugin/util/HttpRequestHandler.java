package com.getcapacitor.plugin.util;

import android.text.TextUtils;
import android.util.Base64;
import androidx.core.app.NotificationCompat;
import com.getcapacitor.Bridge;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.JSValue;
import com.getcapacitor.PluginCall;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes4.dex */
public class HttpRequestHandler {

    @FunctionalInterface
    public interface ProgressEmitter {
        void emit(Integer num, Integer num2);
    }

    public enum ResponseType {
        ARRAY_BUFFER("arraybuffer"),
        BLOB("blob"),
        DOCUMENT("document"),
        JSON("json"),
        TEXT("text");

        private final String name;
        static final ResponseType DEFAULT = TEXT;

        ResponseType(String name) {
            this.name = name;
        }

        public static ResponseType parse(String value) {
            for (ResponseType responseType : values()) {
                if (responseType.name.equalsIgnoreCase(value)) {
                    return responseType;
                }
            }
            return DEFAULT;
        }
    }

    public static class HttpURLConnectionBuilder {
        public Integer connectTimeout;
        public CapacitorHttpUrlConnection connection;
        public Boolean disableRedirects;
        public JSObject headers;
        public String method;
        public Integer readTimeout;
        public URL url;

        public HttpURLConnectionBuilder setConnectTimeout(Integer connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public HttpURLConnectionBuilder setReadTimeout(Integer readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public HttpURLConnectionBuilder setDisableRedirects(Boolean disableRedirects) {
            this.disableRedirects = disableRedirects;
            return this;
        }

        public HttpURLConnectionBuilder setHeaders(JSObject headers) {
            this.headers = headers;
            return this;
        }

        public HttpURLConnectionBuilder setMethod(String method) {
            this.method = method;
            return this;
        }

        public HttpURLConnectionBuilder setUrl(URL url) {
            this.url = url;
            return this;
        }

        public HttpURLConnectionBuilder openConnection() throws IOException {
            this.connection = new CapacitorHttpUrlConnection((HttpURLConnection) this.url.openConnection());
            this.connection.setAllowUserInteraction(false);
            this.connection.setRequestMethod(this.method);
            if (this.connectTimeout != null) {
                this.connection.setConnectTimeout(this.connectTimeout.intValue());
            }
            if (this.readTimeout != null) {
                this.connection.setReadTimeout(this.readTimeout.intValue());
            }
            if (this.disableRedirects != null) {
                this.connection.setDisableRedirects(this.disableRedirects.booleanValue());
            }
            this.connection.setRequestHeaders(this.headers);
            return this;
        }

        public HttpURLConnectionBuilder setUrlParams(JSObject params) throws JSONException, MalformedURLException, URISyntaxException {
            return setUrlParams(params, true);
        }

        public HttpURLConnectionBuilder setUrlParams(JSObject params, boolean shouldEncode) throws MalformedURLException, URISyntaxException, UnsupportedEncodingException {
            String initialQuery = this.url.getQuery();
            String initialQueryBuilderStr = initialQuery == null ? "" : initialQuery;
            Iterator<String> keys = params.keys();
            if (!keys.hasNext()) {
                return this;
            }
            StringBuilder urlQueryBuilder = new StringBuilder(initialQueryBuilderStr);
            while (keys.hasNext()) {
                String key = keys.next();
                try {
                    StringBuilder value = new StringBuilder();
                    JSONArray arr = params.getJSONArray(key);
                    for (int x = 0; x < arr.length(); x++) {
                        addUrlParam(value, key, arr.getString(x), shouldEncode);
                        if (x != arr.length() - 1) {
                            value.append("&");
                        }
                    }
                    int x2 = urlQueryBuilder.length();
                    if (x2 > 0) {
                        urlQueryBuilder.append("&");
                    }
                    urlQueryBuilder.append((CharSequence) value);
                } catch (JSONException e) {
                    if (urlQueryBuilder.length() > 0) {
                        urlQueryBuilder.append("&");
                    }
                    addUrlParam(urlQueryBuilder, key, params.getString(key), shouldEncode);
                }
            }
            String urlQuery = urlQueryBuilder.toString();
            URI uri = this.url.toURI();
            String unEncodedUrlString = uri.getScheme() + "://" + uri.getAuthority() + uri.getPath() + (!urlQuery.equals("") ? "?" + urlQuery : "") + (uri.getFragment() != null ? uri.getFragment() : "");
            this.url = new URL(unEncodedUrlString);
            return this;
        }

        private static void addUrlParam(StringBuilder sb, String key, String value, boolean shouldEncode) throws UnsupportedEncodingException {
            if (shouldEncode) {
                try {
                    key = URLEncoder.encode(key, "UTF-8");
                    value = URLEncoder.encode(value, "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    throw new RuntimeException(ex.getCause());
                }
            }
            sb.append(key).append("=").append(value);
        }

        public CapacitorHttpUrlConnection build() {
            return this.connection;
        }
    }

    public static JSObject buildResponse(CapacitorHttpUrlConnection connection) throws JSONException, IOException {
        return buildResponse(connection, ResponseType.DEFAULT);
    }

    public static JSObject buildResponse(CapacitorHttpUrlConnection connection, ResponseType responseType) throws JSONException, IOException {
        int statusCode = connection.getResponseCode();
        JSObject output = new JSObject();
        output.put(NotificationCompat.CATEGORY_STATUS, statusCode);
        output.put("headers", (Object) buildResponseHeaders(connection));
        output.put("url", (Object) connection.getURL());
        output.put("data", readData(connection, responseType));
        InputStream errorStream = connection.getErrorStream();
        if (errorStream != null) {
            output.put("error", true);
        }
        return output;
    }

    public static Object readData(ICapacitorHttpUrlConnection connection, ResponseType responseType) throws JSONException, IOException {
        InputStream errorStream = connection.getErrorStream();
        String contentType = connection.getHeaderField("Content-Type");
        if (errorStream != null) {
            if (isOneOf(contentType, MimeType.APPLICATION_JSON, MimeType.APPLICATION_VND_API_JSON)) {
                return parseJSON(readStreamAsString(errorStream));
            }
            return readStreamAsString(errorStream);
        }
        if (contentType != null && contentType.contains(MimeType.APPLICATION_JSON.getValue())) {
            return parseJSON(readStreamAsString(connection.getInputStream()));
        }
        InputStream stream = connection.getInputStream();
        switch (responseType) {
            case ARRAY_BUFFER:
            case BLOB:
                return readStreamAsBase64(stream);
            case DOCUMENT:
            default:
                return readStreamAsString(stream);
            case JSON:
                return parseJSON(readStreamAsString(stream));
        }
    }

    public static boolean isOneOf(String contentType, MimeType... mimeTypes) {
        if (contentType != null) {
            for (MimeType mimeType : mimeTypes) {
                if (contentType.contains(mimeType.getValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static JSObject buildResponseHeaders(CapacitorHttpUrlConnection connection) {
        JSObject output = new JSObject();
        for (Map.Entry<String, List<String>> entry : connection.getHeaderFields().entrySet()) {
            String valuesString = TextUtils.join(", ", entry.getValue());
            output.put(entry.getKey(), valuesString);
        }
        return output;
    }

    public static Object parseJSON(String input) throws JSONException {
        new JSONObject();
        try {
            if ("null".equals(input.trim())) {
                return JSONObject.NULL;
            }
            if ("true".equals(input.trim())) {
                return true;
            }
            if ("false".equals(input.trim())) {
                return false;
            }
            if (input.trim().length() <= 0) {
                return "";
            }
            if (input.trim().matches("^\".*\"$")) {
                return input.trim().substring(1, input.trim().length() - 1);
            }
            if (input.trim().matches("^-?\\d+$")) {
                return Integer.valueOf(Integer.parseInt(input.trim()));
            }
            if (input.trim().matches("^-?\\d+(\\.\\d+)?$")) {
                return Double.valueOf(Double.parseDouble(input.trim()));
            }
            try {
                return new JSObject(input);
            } catch (JSONException e) {
                return new JSArray(input);
            }
        } catch (JSONException e2) {
            return input;
        }
    }

    public static String readStreamAsBase64(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            while (true) {
                int readBytes = in.read(buffer);
                if (readBytes != -1) {
                    out.write(buffer, 0, readBytes);
                } else {
                    byte[] result = out.toByteArray();
                    String strEncodeToString = Base64.encodeToString(result, 0, result.length, 0);
                    out.close();
                    return strEncodeToString;
                }
            }
        } catch (Throwable th) {
            try {
                out.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    public static String readStreamAsString(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        try {
            StringBuilder builder = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                builder.append(line);
                line = reader.readLine();
                if (line != null) {
                    builder.append(System.getProperty("line.separator"));
                }
            }
            String string = builder.toString();
            reader.close();
            return string;
        } catch (Throwable th) {
            try {
                reader.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    public static JSObject request(PluginCall call, String httpMethod, Bridge bridge) throws JSONException, URISyntaxException, IOException {
        String urlString = call.getString("url", "");
        JSObject headers = call.getObject("headers", new JSObject());
        JSObject params = call.getObject("params", new JSObject());
        Integer connectTimeout = call.getInt("connectTimeout");
        Integer readTimeout = call.getInt("readTimeout");
        Boolean disableRedirects = call.getBoolean("disableRedirects");
        Boolean shouldEncode = call.getBoolean("shouldEncodeUrlParams", true);
        ResponseType responseType = ResponseType.parse(call.getString("responseType"));
        String dataType = call.getString("dataType");
        String method = httpMethod != null ? httpMethod.toUpperCase(Locale.ROOT) : call.getString("method", "GET").toUpperCase(Locale.ROOT);
        boolean isHttpMutate = method.equals("DELETE") || method.equals("PATCH") || method.equals("POST") || method.equals("PUT");
        String userAgentValue = headers.getString("x-cap-user-agent");
        if (userAgentValue != null) {
            headers.put("User-Agent", userAgentValue);
        }
        headers.remove("x-cap-user-agent");
        if (!headers.has("User-Agent") && !headers.has("user-agent")) {
            headers.put("User-Agent", bridge.getConfig().getOverriddenUserAgentString());
        }
        URL url = new URL(urlString);
        HttpURLConnectionBuilder connectionBuilder = new HttpURLConnectionBuilder().setUrl(url).setMethod(method).setHeaders(headers).setUrlParams(params, shouldEncode.booleanValue()).setConnectTimeout(connectTimeout).setReadTimeout(readTimeout).setDisableRedirects(disableRedirects).openConnection();
        CapacitorHttpUrlConnection connection = connectionBuilder.build();
        if (bridge != null && !isDomainExcludedFromSSL(bridge, url).booleanValue()) {
            connection.setSSLSocketFactory(bridge);
        }
        if (isHttpMutate) {
            JSValue data = new JSValue(call, "data");
            if (data.getValue() != null) {
                connection.setDoOutput(true);
                connection.setRequestBody(call, data, dataType);
            }
        }
        call.getData().put("activeCapacitorHttpUrlConnection", (Object) connection);
        connection.connect();
        JSObject response = buildResponse(connection, responseType);
        connection.disconnect();
        call.getData().remove("activeCapacitorHttpUrlConnection");
        return response;
    }

    public static Boolean isDomainExcludedFromSSL(Bridge bridge, URL url) {
        try {
            Class<?> sslPinningImpl = Class.forName("io.ionic.sslpinning.SSLPinning");
            Method method = sslPinningImpl.getDeclaredMethod("isDomainExcluded", Bridge.class, URL.class);
            return (Boolean) method.invoke(sslPinningImpl.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]), bridge, url);
        } catch (Exception e) {
            return false;
        }
    }
}
