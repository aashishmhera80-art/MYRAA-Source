package org.apache.cordova.engine;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.webkit.ClientCertRequest;
import android.webkit.HttpAuthHandler;
import android.webkit.MimeTypeMap;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.ServiceWorkerClient;
import android.webkit.ServiceWorkerController;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.webkit.WebViewAssetLoader;
import androidx.webkit.internal.AssetHelper;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import org.apache.cordova.AuthenticationToken;
import org.apache.cordova.CordovaClientCertRequest;
import org.apache.cordova.CordovaHttpAuthHandler;
import org.apache.cordova.CordovaPluginPathHandler;
import org.apache.cordova.CordovaResourceApi;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginManager;

/* JADX INFO: loaded from: classes.dex */
public class SystemWebViewClient extends WebViewClient {
    private static final String TAG = "SystemWebViewClient";
    private final WebViewAssetLoader assetLoader;
    boolean isCurrentlyLoading;
    protected final SystemWebViewEngine parentEngine;
    private boolean doClearHistory = false;
    private Hashtable<String, AuthenticationToken> authenticationTokens = new Hashtable<>();

    public SystemWebViewClient(final SystemWebViewEngine parentEngine) {
        this.parentEngine = parentEngine;
        WebViewAssetLoader.Builder assetLoaderBuilder = new WebViewAssetLoader.Builder().setDomain(parentEngine.preferences.getString("hostname", "localhost").toLowerCase()).setHttpAllowed(true);
        assetLoaderBuilder.addPathHandler("/", new WebViewAssetLoader.PathHandler() { // from class: org.apache.cordova.engine.SystemWebViewClient$$ExternalSyntheticLambda0
            @Override // androidx.webkit.WebViewAssetLoader.PathHandler
            public final WebResourceResponse handle(String str) {
                return this.f$0.m1907lambda$new$0$orgapachecordovaengineSystemWebViewClient(parentEngine, str);
            }
        });
        this.assetLoader = assetLoaderBuilder.build();
        boolean setAsServiceWorkerClient = parentEngine.preferences.getBoolean("ResolveServiceWorkerRequests", true);
        if (setAsServiceWorkerClient) {
            ServiceWorkerController controller = ServiceWorkerController.getInstance();
            controller.setServiceWorkerClient(new ServiceWorkerClient() { // from class: org.apache.cordova.engine.SystemWebViewClient.1
                @Override // android.webkit.ServiceWorkerClient
                public WebResourceResponse shouldInterceptRequest(WebResourceRequest request) {
                    return SystemWebViewClient.this.assetLoader.shouldInterceptRequest(request.getUrl());
                }
            });
        }
    }

    /* JADX INFO: renamed from: lambda$new$0$org-apache-cordova-engine-SystemWebViewClient, reason: not valid java name */
    /* synthetic */ WebResourceResponse m1907lambda$new$0$orgapachecordovaengineSystemWebViewClient(SystemWebViewEngine parentEngine, String path) {
        WebResourceResponse response;
        try {
            PluginManager pluginManager = this.parentEngine.pluginManager;
            if (pluginManager != null) {
                for (CordovaPluginPathHandler handler : pluginManager.getPluginPathHandlers()) {
                    if (handler.getPathHandler() != null && (response = handler.getPathHandler().handle(path)) != null) {
                        return response;
                    }
                }
            }
            if (path.isEmpty()) {
                path = "index.html";
            }
            InputStream is = parentEngine.webView.getContext().getAssets().open("www/" + path, 2);
            String mimeType = "text/html";
            String extension = MimeTypeMap.getFileExtensionFromUrl(path);
            if (extension != null) {
                if (path.endsWith(".js") || path.endsWith(".mjs")) {
                    mimeType = "application/javascript";
                } else if (path.endsWith(".wasm")) {
                    mimeType = "application/wasm";
                } else {
                    mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                }
            }
            return new WebResourceResponse(mimeType, null, is);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.e(TAG, e.getMessage());
            return null;
        }
    }

    @Override // android.webkit.WebViewClient
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return this.parentEngine.client.onNavigationAttempt(url);
    }

    @Override // android.webkit.WebViewClient
    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
        AuthenticationToken token = getAuthenticationToken(host, realm);
        if (token != null) {
            handler.proceed(token.getUserName(), token.getPassword());
            return;
        }
        PluginManager pluginManager = this.parentEngine.pluginManager;
        if (pluginManager != null && pluginManager.onReceivedHttpAuthRequest(null, new CordovaHttpAuthHandler(handler), host, realm)) {
            this.parentEngine.client.clearLoadTimeoutTimer();
        } else {
            super.onReceivedHttpAuthRequest(view, handler, host, realm);
        }
    }

    @Override // android.webkit.WebViewClient
    public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
        PluginManager pluginManager = this.parentEngine.pluginManager;
        if (pluginManager != null && pluginManager.onReceivedClientCertRequest(null, new CordovaClientCertRequest(request))) {
            this.parentEngine.client.clearLoadTimeoutTimer();
        } else {
            super.onReceivedClientCertRequest(view, request);
        }
    }

    @Override // android.webkit.WebViewClient
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        this.isCurrentlyLoading = true;
        this.parentEngine.bridge.reset();
        this.parentEngine.client.onPageStarted(url);
    }

    @Override // android.webkit.WebViewClient
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (!this.isCurrentlyLoading && !url.startsWith("about:")) {
            return;
        }
        this.isCurrentlyLoading = false;
        if (this.doClearHistory) {
            view.clearHistory();
            this.doClearHistory = false;
        }
        this.parentEngine.client.onPageFinishedLoading(url);
    }

    @Override // android.webkit.WebViewClient
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        if (!this.isCurrentlyLoading) {
            return;
        }
        LOG.d(TAG, "CordovaWebViewClient.onReceivedError: Error code=%s Description=%s URL=%s", Integer.valueOf(errorCode), description, failingUrl);
        if (errorCode == -10) {
            this.parentEngine.client.clearLoadTimeoutTimer();
            if (view.canGoBack()) {
                view.goBack();
                return;
            }
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
        this.parentEngine.client.onReceivedError(errorCode, description, failingUrl);
    }

    @Override // android.webkit.WebViewClient
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        String packageName = this.parentEngine.cordova.getActivity().getPackageName();
        PackageManager pm = this.parentEngine.cordova.getActivity().getPackageManager();
        try {
            ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 128);
            if ((appInfo.flags & 2) != 0) {
                handler.proceed();
            } else {
                super.onReceivedSslError(view, handler, error);
            }
        } catch (PackageManager.NameNotFoundException e) {
            super.onReceivedSslError(view, handler, error);
        }
    }

    public void setAuthenticationToken(AuthenticationToken authenticationToken, String host, String realm) {
        if (host == null) {
            host = "";
        }
        if (realm == null) {
            realm = "";
        }
        this.authenticationTokens.put(host.concat(realm), authenticationToken);
    }

    public AuthenticationToken removeAuthenticationToken(String host, String realm) {
        return this.authenticationTokens.remove(host.concat(realm));
    }

    public AuthenticationToken getAuthenticationToken(String host, String realm) {
        AuthenticationToken token = this.authenticationTokens.get(host.concat(realm));
        if (token == null) {
            AuthenticationToken token2 = this.authenticationTokens.get(host);
            if (token2 == null) {
                token2 = this.authenticationTokens.get(realm);
            }
            if (token2 == null) {
                return this.authenticationTokens.get("");
            }
            return token2;
        }
        return token;
    }

    public void clearAuthenticationTokens() {
        this.authenticationTokens.clear();
    }

    @Override // android.webkit.WebViewClient
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        try {
            if (!this.parentEngine.pluginManager.shouldAllowRequest(url)) {
                LOG.w(TAG, "URL blocked by allow list: " + url);
                return new WebResourceResponse(AssetHelper.DEFAULT_MIME_TYPE, "UTF-8", null);
            }
            CordovaResourceApi resourceApi = this.parentEngine.resourceApi;
            Uri origUri = Uri.parse(url);
            Uri remappedUri = resourceApi.remapUri(origUri);
            if (origUri.equals(remappedUri) && !needsSpecialsInAssetUrlFix(origUri) && !needsContentUrlFix(origUri)) {
                return null;
            }
            CordovaResourceApi.OpenForReadResult result = resourceApi.openForRead(remappedUri, true);
            return new WebResourceResponse(result.mimeType, "UTF-8", result.inputStream);
        } catch (IOException e) {
            if (!(e instanceof FileNotFoundException)) {
                LOG.e(TAG, "Error occurred while loading a file (returning a 404).", e);
            }
            return new WebResourceResponse(AssetHelper.DEFAULT_MIME_TYPE, "UTF-8", null);
        }
    }

    private static boolean needsContentUrlFix(Uri uri) {
        return "content".equals(uri.getScheme());
    }

    private static boolean needsSpecialsInAssetUrlFix(Uri uri) {
        if (CordovaResourceApi.getUriType(uri) != 1) {
            return false;
        }
        if (uri.getQuery() == null && uri.getFragment() == null) {
            return !uri.toString().contains("%") ? false : false;
        }
        return true;
    }

    @Override // android.webkit.WebViewClient
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        return this.assetLoader.shouldInterceptRequest(request.getUrl());
    }

    @Override // android.webkit.WebViewClient
    public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
        PluginManager pluginManager = this.parentEngine.pluginManager;
        if (pluginManager != null && pluginManager.onRenderProcessGone(view, detail)) {
            return true;
        }
        return super.onRenderProcessGone(view, detail);
    }
}
