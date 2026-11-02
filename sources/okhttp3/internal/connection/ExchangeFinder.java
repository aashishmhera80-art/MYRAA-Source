package okhttp3.internal.connection;

import androidx.core.app.NotificationCompat;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.Address;
import okhttp3.EventListener;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Route;
import okhttp3.internal.Util;
import okhttp3.internal.http.ExchangeCodec;
import okhttp3.internal.http.RealInterceptorChain;
import okhttp3.internal.http2.ConnectionShutdownException;
import okhttp3.internal.http2.ErrorCode;
import okhttp3.internal.http2.StreamResetException;

/* JADX INFO: compiled from: ExchangeFinder.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000r\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t¢\u0006\u0002\u0010\nJ\u0016\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001cJ0\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020\u000e2\u0006\u0010 \u001a\u00020\u000e2\u0006\u0010!\u001a\u00020\u000e2\u0006\u0010\"\u001a\u00020\u000e2\u0006\u0010#\u001a\u00020$H\u0002J8\u0010%\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020\u000e2\u0006\u0010 \u001a\u00020\u000e2\u0006\u0010!\u001a\u00020\u000e2\u0006\u0010\"\u001a\u00020\u000e2\u0006\u0010#\u001a\u00020$2\u0006\u0010&\u001a\u00020$H\u0002J\u0006\u0010'\u001a\u00020$J\n\u0010(\u001a\u0004\u0018\u00010\u0010H\u0002J\u000e\u0010)\u001a\u00020$2\u0006\u0010*\u001a\u00020+J\u000e\u0010,\u001a\u00020-2\u0006\u0010.\u001a\u00020/R\u0014\u0010\u0004\u001a\u00020\u0005X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u000f\u001a\u0004\u0018\u00010\u0010X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u000eX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u000eX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0013\u001a\u0004\u0018\u00010\u0014X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0015\u001a\u0004\u0018\u00010\u0016X\u0082\u000e¢\u0006\u0002\n\u0000¨\u00060"}, d2 = {"Lokhttp3/internal/connection/ExchangeFinder;", "", "connectionPool", "Lokhttp3/internal/connection/RealConnectionPool;", "address", "Lokhttp3/Address;", NotificationCompat.CATEGORY_CALL, "Lokhttp3/internal/connection/RealCall;", "eventListener", "Lokhttp3/EventListener;", "(Lokhttp3/internal/connection/RealConnectionPool;Lokhttp3/Address;Lokhttp3/internal/connection/RealCall;Lokhttp3/EventListener;)V", "getAddress$okhttp", "()Lokhttp3/Address;", "connectionShutdownCount", "", "nextRouteToTry", "Lokhttp3/Route;", "otherFailureCount", "refusedStreamCount", "routeSelection", "Lokhttp3/internal/connection/RouteSelector$Selection;", "routeSelector", "Lokhttp3/internal/connection/RouteSelector;", "find", "Lokhttp3/internal/http/ExchangeCodec;", "client", "Lokhttp3/OkHttpClient;", "chain", "Lokhttp3/internal/http/RealInterceptorChain;", "findConnection", "Lokhttp3/internal/connection/RealConnection;", "connectTimeout", "readTimeout", "writeTimeout", "pingIntervalMillis", "connectionRetryEnabled", "", "findHealthyConnection", "doExtensiveHealthChecks", "retryAfterFailure", "retryRoute", "sameHostAndPort", "url", "Lokhttp3/HttpUrl;", "trackFailure", "", "e", "Ljava/io/IOException;", "okhttp"}, k = 1, mv = {1, 8, 0}, xi = 48)
public final class ExchangeFinder {
    private final Address address;
    private final RealCall call;
    private final RealConnectionPool connectionPool;
    private int connectionShutdownCount;
    private final EventListener eventListener;
    private Route nextRouteToTry;
    private int otherFailureCount;
    private int refusedStreamCount;
    private RouteSelector.Selection routeSelection;
    private RouteSelector routeSelector;

    public ExchangeFinder(RealConnectionPool connectionPool, Address address, RealCall call, EventListener eventListener) {
        Intrinsics.checkNotNullParameter(connectionPool, "connectionPool");
        Intrinsics.checkNotNullParameter(address, "address");
        Intrinsics.checkNotNullParameter(call, "call");
        Intrinsics.checkNotNullParameter(eventListener, "eventListener");
        this.connectionPool = connectionPool;
        this.address = address;
        this.call = call;
        this.eventListener = eventListener;
    }

    /* JADX INFO: renamed from: getAddress$okhttp, reason: from getter */
    public final Address getAddress() {
        return this.address;
    }

    public final ExchangeCodec find(OkHttpClient client, RealInterceptorChain chain) {
        Intrinsics.checkNotNullParameter(client, "client");
        Intrinsics.checkNotNullParameter(chain, "chain");
        try {
            try {
                RealConnection resultConnection = findHealthyConnection(chain.getConnectTimeoutMillis$okhttp(), chain.getReadTimeoutMillis(), chain.getWriteTimeoutMillis(), client.pingIntervalMillis(), client.retryOnConnectionFailure(), !Intrinsics.areEqual(chain.getRequest().method(), "GET"));
                return resultConnection.newCodec$okhttp(client, chain);
            } catch (IOException e) {
                e = e;
                trackFailure(e);
                throw new RouteException(e);
            } catch (RouteException e2) {
                e = e2;
                trackFailure(e.getLastConnectException());
                throw e;
            }
        } catch (IOException e3) {
            e = e3;
        } catch (RouteException e4) {
            e = e4;
        }
    }

    private final RealConnection findHealthyConnection(int connectTimeout, int readTimeout, int writeTimeout, int pingIntervalMillis, boolean connectionRetryEnabled, boolean doExtensiveHealthChecks) throws IOException {
        while (true) {
            RealConnection candidate = findConnection(connectTimeout, readTimeout, writeTimeout, pingIntervalMillis, connectionRetryEnabled);
            connectionRetryEnabled = connectionRetryEnabled;
            pingIntervalMillis = pingIntervalMillis;
            writeTimeout = writeTimeout;
            readTimeout = readTimeout;
            connectTimeout = connectTimeout;
            if (candidate.isHealthy(doExtensiveHealthChecks)) {
                return candidate;
            }
            candidate.noNewExchanges$okhttp();
            if (this.nextRouteToTry == null) {
                RouteSelector.Selection selection = this.routeSelection;
                boolean routesLeft = selection != null ? selection.hasNext() : true;
                if (!routesLeft) {
                    RouteSelector routeSelector = this.routeSelector;
                    boolean routesSelectionLeft = routeSelector != null ? routeSelector.hasNext() : true;
                    if (!routesSelectionLeft) {
                        throw new IOException("exhausted all routes");
                    }
                }
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:43:0x00bd  */
    /* JADX WARN: Code duplicated, block: B:45:0x00c1  */
    /* JADX WARN: Code duplicated, block: B:48:0x00ed  */
    /* JADX WARN: Code duplicated, block: B:50:0x00f9  */
    /* JADX WARN: Code duplicated, block: B:52:0x010f  */
    /* JADX WARN: Code duplicated, block: B:71:0x01a9  */
    private final RealConnection findConnection(int connectTimeout, int readTimeout, int writeTimeout, int pingIntervalMillis, boolean connectionRetryEnabled) throws IOException {
        RouteSelector localRouteSelector;
        RouteSelector.Selection localRouteSelection;
        List<Route> routes;
        Route route;
        List<Route> list;
        if (this.call.getCanceled()) {
            throw new IOException("Canceled");
        }
        RealConnection callConnection = this.call.getConnection();
        if (callConnection != null) {
            Socket socketReleaseConnectionNoEvents$okhttp = null;
            synchronized (callConnection) {
                if (callConnection.getNoNewExchanges() || !sameHostAndPort(callConnection.getRoute().address().url())) {
                    socketReleaseConnectionNoEvents$okhttp = this.call.releaseConnectionNoEvents$okhttp();
                }
                Unit unit = Unit.INSTANCE;
            }
            if (this.call.getConnection() != null) {
                if (!(socketReleaseConnectionNoEvents$okhttp == null)) {
                    throw new IllegalStateException("Check failed.".toString());
                }
                return callConnection;
            }
            if (socketReleaseConnectionNoEvents$okhttp != null) {
                Util.closeQuietly(socketReleaseConnectionNoEvents$okhttp);
            }
            this.eventListener.connectionReleased(this.call, callConnection);
        }
        this.refusedStreamCount = 0;
        this.connectionShutdownCount = 0;
        this.otherFailureCount = 0;
        if (this.connectionPool.callAcquirePooledConnection(this.address, this.call, null, false)) {
            RealConnection result = this.call.getConnection();
            Intrinsics.checkNotNull(result);
            this.eventListener.connectionAcquired(this.call, result);
            return result;
        }
        if (this.nextRouteToTry != null) {
            list = null;
            route = this.nextRouteToTry;
            Intrinsics.checkNotNull(route);
            this.nextRouteToTry = null;
        } else if (this.routeSelection != null) {
            RouteSelector.Selection selection = this.routeSelection;
            Intrinsics.checkNotNull(selection);
            if (selection.hasNext()) {
                list = null;
                RouteSelector.Selection selection2 = this.routeSelection;
                Intrinsics.checkNotNull(selection2);
                route = selection2.next();
            } else {
                localRouteSelector = this.routeSelector;
                if (localRouteSelector == null) {
                    localRouteSelector = new RouteSelector(this.address, this.call.getClient().getRouteDatabase(), this.call, this.eventListener);
                    this.routeSelector = localRouteSelector;
                }
                localRouteSelection = localRouteSelector.next();
                this.routeSelection = localRouteSelection;
                routes = localRouteSelection.getRoutes();
                if (!this.call.getCanceled()) {
                    if (this.connectionPool.callAcquirePooledConnection(this.address, this.call, routes, false)) {
                        RealConnection result2 = this.call.getConnection();
                        Intrinsics.checkNotNull(result2);
                        this.eventListener.connectionAcquired(this.call, result2);
                        return result2;
                    }
                    route = localRouteSelection.next();
                    list = routes;
                } else {
                    throw new IOException("Canceled");
                }
            }
        } else {
            localRouteSelector = this.routeSelector;
            if (localRouteSelector == null) {
                localRouteSelector = new RouteSelector(this.address, this.call.getClient().getRouteDatabase(), this.call, this.eventListener);
                this.routeSelector = localRouteSelector;
            }
            localRouteSelection = localRouteSelector.next();
            this.routeSelection = localRouteSelection;
            routes = localRouteSelection.getRoutes();
            if (!this.call.getCanceled()) {
                if (this.connectionPool.callAcquirePooledConnection(this.address, this.call, routes, false)) {
                    RealConnection result3 = this.call.getConnection();
                    Intrinsics.checkNotNull(result3);
                    this.eventListener.connectionAcquired(this.call, result3);
                    return result3;
                }
                route = localRouteSelection.next();
                list = routes;
            } else {
                throw new IOException("Canceled");
            }
        }
        RealConnection newConnection = new RealConnection(this.connectionPool, route);
        this.call.setConnectionToCancel(newConnection);
        try {
            newConnection.connect(connectTimeout, readTimeout, writeTimeout, pingIntervalMillis, connectionRetryEnabled, this.call, this.eventListener);
            this.call.setConnectionToCancel(null);
            this.call.getClient().getRouteDatabase().connected(newConnection.getRoute());
            if (this.connectionPool.callAcquirePooledConnection(this.address, this.call, list, true)) {
                RealConnection result4 = this.call.getConnection();
                Intrinsics.checkNotNull(result4);
                this.nextRouteToTry = route;
                Util.closeQuietly(newConnection.socket());
                this.eventListener.connectionAcquired(this.call, result4);
                return result4;
            }
            synchronized (newConnection) {
                this.connectionPool.put(newConnection);
                this.call.acquireConnectionNoEvents(newConnection);
                Unit unit2 = Unit.INSTANCE;
            }
            this.eventListener.connectionAcquired(this.call, newConnection);
            return newConnection;
        } catch (Throwable th) {
            this.call.setConnectionToCancel(null);
            throw th;
        }
    }

    public final void trackFailure(IOException e) {
        Intrinsics.checkNotNullParameter(e, "e");
        this.nextRouteToTry = null;
        if ((e instanceof StreamResetException) && ((StreamResetException) e).errorCode == ErrorCode.REFUSED_STREAM) {
            this.refusedStreamCount++;
        } else if (e instanceof ConnectionShutdownException) {
            this.connectionShutdownCount++;
        } else {
            this.otherFailureCount++;
        }
    }

    public final boolean retryAfterFailure() {
        RouteSelector localRouteSelector;
        boolean z = false;
        if (this.refusedStreamCount == 0 && this.connectionShutdownCount == 0 && this.otherFailureCount == 0) {
            return false;
        }
        if (this.nextRouteToTry != null) {
            return true;
        }
        Route retryRoute = retryRoute();
        if (retryRoute != null) {
            this.nextRouteToTry = retryRoute;
            return true;
        }
        RouteSelector.Selection selection = this.routeSelection;
        if (selection != null && selection.hasNext()) {
            z = true;
        }
        if (z || (localRouteSelector = this.routeSelector) == null) {
            return true;
        }
        return localRouteSelector.hasNext();
    }

    private final Route retryRoute() {
        RealConnection connection;
        if (this.refusedStreamCount > 1 || this.connectionShutdownCount > 1 || this.otherFailureCount > 0 || (connection = this.call.getConnection()) == null) {
            return null;
        }
        synchronized (connection) {
            if (connection.getRouteFailureCount() != 0) {
                return null;
            }
            if (Util.canReuseConnectionFor(connection.getRoute().address().url(), this.address.url())) {
                return connection.getRoute();
            }
            return null;
        }
    }

    public final boolean sameHostAndPort(HttpUrl url) {
        Intrinsics.checkNotNullParameter(url, "url");
        HttpUrl routeUrl = this.address.url();
        return url.port() == routeUrl.port() && Intrinsics.areEqual(url.host(), routeUrl.host());
    }
}
