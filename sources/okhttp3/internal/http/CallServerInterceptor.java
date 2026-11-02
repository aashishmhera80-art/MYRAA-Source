package okhttp3.internal.http;

import java.io.IOException;
import java.net.ProtocolException;
import kotlin.ExceptionsKt;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.Util;
import okhttp3.internal.connection.Exchange;
import okhttp3.internal.http2.ConnectionShutdownException;
import okio.BufferedSink;
import okio.Okio;

/* JADX INFO: compiled from: CallServerInterceptor.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\u0010\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0016J\u0010\u0010\t\u001a\u00020\u00032\u0006\u0010\n\u001a\u00020\u000bH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\f"}, d2 = {"Lokhttp3/internal/http/CallServerInterceptor;", "Lokhttp3/Interceptor;", "forWebSocket", "", "(Z)V", "intercept", "Lokhttp3/Response;", "chain", "Lokhttp3/Interceptor$Chain;", "shouldIgnoreAndWaitForRealResponse", "code", "", "okhttp"}, k = 1, mv = {1, 8, 0}, xi = 48)
public final class CallServerInterceptor implements Interceptor {
    private final boolean forWebSocket;

    public CallServerInterceptor(boolean forWebSocket) {
        this.forWebSocket = forWebSocket;
    }

    /* JADX WARN: Code duplicated, block: B:107:0x00f7 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:43:0x00ed A[Catch: IOException -> 0x01d8, TRY_LEAVE, TryCatch #1 {IOException -> 0x01d8, blocks: (B:41:0x00d6, B:43:0x00ed), top: B:97:0x00d6 }] */
    /* JADX WARN: Code duplicated, block: B:55:0x0137  */
    /* JADX WARN: Code duplicated, block: B:61:0x0158 A[Catch: IOException -> 0x01d5, TryCatch #3 {IOException -> 0x01d5, blocks: (B:52:0x011d, B:56:0x013e, B:60:0x0149, B:62:0x0168, B:64:0x0179, B:71:0x0190, B:73:0x0196, B:77:0x01a3, B:79:0x01c0, B:80:0x01c8, B:81:0x01d3, B:66:0x0185, B:61:0x0158), top: B:101:0x011d }] */
    /* JADX WARN: Code duplicated, block: B:66:0x0185 A[Catch: IOException -> 0x01d5, TryCatch #3 {IOException -> 0x01d5, blocks: (B:52:0x011d, B:56:0x013e, B:60:0x0149, B:62:0x0168, B:64:0x0179, B:71:0x0190, B:73:0x0196, B:77:0x01a3, B:79:0x01c0, B:80:0x01c8, B:81:0x01d3, B:66:0x0185, B:61:0x0158), top: B:101:0x011d }] */
    /* JADX WARN: Code duplicated, block: B:71:0x0190 A[Catch: IOException -> 0x01d5, TryCatch #3 {IOException -> 0x01d5, blocks: (B:52:0x011d, B:56:0x013e, B:60:0x0149, B:62:0x0168, B:64:0x0179, B:71:0x0190, B:73:0x0196, B:77:0x01a3, B:79:0x01c0, B:80:0x01c8, B:81:0x01d3, B:66:0x0185, B:61:0x0158), top: B:101:0x011d }] */
    /* JADX WARN: Code duplicated, block: B:73:0x0196 A[Catch: IOException -> 0x01d5, TryCatch #3 {IOException -> 0x01d5, blocks: (B:52:0x011d, B:56:0x013e, B:60:0x0149, B:62:0x0168, B:64:0x0179, B:71:0x0190, B:73:0x0196, B:77:0x01a3, B:79:0x01c0, B:80:0x01c8, B:81:0x01d3, B:66:0x0185, B:61:0x0158), top: B:101:0x011d }] */
    /* JADX WARN: Code duplicated, block: B:74:0x019b  */
    /* JADX WARN: Code duplicated, block: B:77:0x01a3 A[Catch: IOException -> 0x01d5, TryCatch #3 {IOException -> 0x01d5, blocks: (B:52:0x011d, B:56:0x013e, B:60:0x0149, B:62:0x0168, B:64:0x0179, B:71:0x0190, B:73:0x0196, B:77:0x01a3, B:79:0x01c0, B:80:0x01c8, B:81:0x01d3, B:66:0x0185, B:61:0x0158), top: B:101:0x011d }] */
    /* JADX WARN: Code duplicated, block: B:79:0x01c0 A[Catch: IOException -> 0x01d5, TryCatch #3 {IOException -> 0x01d5, blocks: (B:52:0x011d, B:56:0x013e, B:60:0x0149, B:62:0x0168, B:64:0x0179, B:71:0x0190, B:73:0x0196, B:77:0x01a3, B:79:0x01c0, B:80:0x01c8, B:81:0x01d3, B:66:0x0185, B:61:0x0158), top: B:101:0x011d }] */
    @Override // okhttp3.Interceptor
    public Response intercept(Interceptor.Chain chain) throws IOException {
        boolean invokeStartEvent;
        Response response;
        int code;
        Response response2;
        ResponseBody responseBodyBody;
        long contentLength;
        Intrinsics.checkNotNullParameter(chain, "chain");
        RealInterceptorChain realChain = (RealInterceptorChain) chain;
        Exchange exchange = realChain.getExchange();
        Intrinsics.checkNotNull(exchange);
        Request request = realChain.getRequest();
        RequestBody requestBody = request.body();
        long sentRequestMillis = System.currentTimeMillis();
        boolean invokeStartEvent2 = true;
        Response.Builder responseBuilder = null;
        IOException sendRequestException = null;
        try {
            exchange.writeRequestHeaders(request);
            if (!HttpMethod.permitsRequestBody(request.method()) || requestBody == null) {
                exchange.noRequestBody();
            } else {
                if (StringsKt.equals("100-continue", request.header("Expect"), true)) {
                    exchange.flushRequest();
                    responseBuilder = exchange.readResponseHeaders(true);
                    exchange.responseHeadersStart();
                    invokeStartEvent2 = false;
                }
                if (responseBuilder != null) {
                    exchange.noRequestBody();
                    if (!exchange.getConnection().isMultiplexed$okhttp()) {
                        exchange.noNewExchangesOnConnection();
                    }
                } else if (requestBody.isDuplex()) {
                    exchange.flushRequest();
                    requestBody.writeTo(Okio.buffer(exchange.createRequestBody(request, true)));
                } else {
                    BufferedSink bufferedRequestBody = Okio.buffer(exchange.createRequestBody(request, false));
                    requestBody.writeTo(bufferedRequestBody);
                    bufferedRequestBody.close();
                }
            }
            if (requestBody == null || !requestBody.isDuplex()) {
                exchange.finishRequest();
            }
        } catch (IOException e) {
            if ((e instanceof ConnectionShutdownException) || !exchange.getHasFailure()) {
                throw e;
            }
            sendRequestException = e;
        }
        if (responseBuilder != null) {
            invokeStartEvent = invokeStartEvent2;
            response = responseBuilder.request(request).handshake(exchange.getConnection().getHandshake()).sentRequestAtMillis(sentRequestMillis).receivedResponseAtMillis(System.currentTimeMillis()).build();
            code = response.code();
            if (shouldIgnoreAndWaitForRealResponse(code)) {
                Response.Builder responseBuilder2 = exchange.readResponseHeaders(false);
                Intrinsics.checkNotNull(responseBuilder2);
                if (invokeStartEvent) {
                    exchange.responseHeadersStart();
                }
                response = responseBuilder2.request(request).handshake(exchange.getConnection().getHandshake()).sentRequestAtMillis(sentRequestMillis).receivedResponseAtMillis(System.currentTimeMillis()).build();
                code = response.code();
            }
            exchange.responseHeadersEnd(response);
            if (this.forWebSocket) {
                response2 = response.newBuilder().body(exchange.openResponseBody(response)).build();
            } else {
                response2 = response.newBuilder().body(exchange.openResponseBody(response)).build();
            }
            if (StringsKt.equals("close", response2.request().header("Connection"), true)) {
                exchange.noNewExchangesOnConnection();
            } else {
                exchange.noNewExchangesOnConnection();
            }
            if (code != 204) {
                responseBodyBody = response2.body();
                if (responseBodyBody != null) {
                    contentLength = responseBodyBody.getContentLength();
                } else {
                    contentLength = -1;
                }
                if (contentLength > 0) {
                    StringBuilder sbAppend = new StringBuilder().append("HTTP ").append(code).append(" had non-zero Content-Length: ");
                    ResponseBody responseBodyBody2 = response2.body();
                    throw new ProtocolException(sbAppend.append(responseBodyBody2 != null ? Long.valueOf(responseBodyBody2.getContentLength()) : null).toString());
                }
            } else {
                responseBodyBody = response2.body();
                if (responseBodyBody != null) {
                    contentLength = responseBodyBody.getContentLength();
                } else {
                    contentLength = -1;
                }
                if (contentLength > 0) {
                    StringBuilder sbAppend2 = new StringBuilder().append("HTTP ").append(code).append(" had non-zero Content-Length: ");
                    ResponseBody responseBodyBody3 = response2.body();
                    throw new ProtocolException(sbAppend2.append(responseBodyBody3 != null ? Long.valueOf(responseBodyBody3.getContentLength()) : null).toString());
                }
            }
            return response2;
        }
        try {
            Response.Builder responseHeaders = exchange.readResponseHeaders(false);
            Intrinsics.checkNotNull(responseHeaders);
            responseBuilder = responseHeaders;
            if (invokeStartEvent2) {
                exchange.responseHeadersStart();
                invokeStartEvent2 = false;
            }
            try {
                invokeStartEvent = invokeStartEvent2;
                try {
                    response = responseBuilder.request(request).handshake(exchange.getConnection().getHandshake()).sentRequestAtMillis(sentRequestMillis).receivedResponseAtMillis(System.currentTimeMillis()).build();
                    code = response.code();
                    if (shouldIgnoreAndWaitForRealResponse(code)) {
                        Response.Builder responseBuilder3 = exchange.readResponseHeaders(false);
                        Intrinsics.checkNotNull(responseBuilder3);
                        if (invokeStartEvent) {
                            try {
                                exchange.responseHeadersStart();
                            } catch (IOException e2) {
                                e = e2;
                            }
                        }
                        try {
                            try {
                                response = responseBuilder3.request(request).handshake(exchange.getConnection().getHandshake()).sentRequestAtMillis(sentRequestMillis).receivedResponseAtMillis(System.currentTimeMillis()).build();
                                code = response.code();
                            } catch (IOException e3) {
                                e = e3;
                            }
                        } catch (IOException e4) {
                            e = e4;
                        }
                    }
                    exchange.responseHeadersEnd(response);
                    if (this.forWebSocket || code != 101) {
                        response2 = response.newBuilder().body(exchange.openResponseBody(response)).build();
                    } else {
                        response2 = response.newBuilder().body(Util.EMPTY_RESPONSE).build();
                    }
                    if (StringsKt.equals("close", response2.request().header("Connection"), true) || StringsKt.equals("close", Response.header$default(response2, "Connection", null, 2, null), true)) {
                        exchange.noNewExchangesOnConnection();
                    }
                    if (code != 204 || code == 205) {
                        responseBodyBody = response2.body();
                        if (responseBodyBody != null) {
                            contentLength = responseBodyBody.getContentLength();
                        } else {
                            contentLength = -1;
                        }
                        if (contentLength > 0) {
                            StringBuilder sbAppend3 = new StringBuilder().append("HTTP ").append(code).append(" had non-zero Content-Length: ");
                            ResponseBody responseBodyBody4 = response2.body();
                            throw new ProtocolException(sbAppend3.append(responseBodyBody4 != null ? Long.valueOf(responseBodyBody4.getContentLength()) : null).toString());
                        }
                    }
                    return response2;
                } catch (IOException e5) {
                    e = e5;
                }
            } catch (IOException e6) {
                e = e6;
            }
        } catch (IOException e7) {
            e = e7;
        }
        if (sendRequestException == null) {
            throw e;
        }
        ExceptionsKt.addSuppressed(sendRequestException, e);
        throw sendRequestException;
    }

    private final boolean shouldIgnoreAndWaitForRealResponse(int code) {
        if (code == 100) {
            return true;
        }
        return 102 <= code && code < 200;
    }
}
