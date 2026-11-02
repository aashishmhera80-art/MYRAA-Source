package org.apache.cordova.engine;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.cordova.CordovaDialogsHelper;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public class SystemWebChromeClient extends WebChromeClient {
    private static final int FILECHOOSER_RESULTCODE = 5173;
    private static final String LOG_TAG = "SystemWebChromeClient";
    private long MAX_QUOTA = 104857600;
    private Context appContext;
    private CordovaDialogsHelper dialogsHelper;
    private View mCustomView;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    private View mVideoProgressView;
    protected final SystemWebViewEngine parentEngine;

    public SystemWebChromeClient(SystemWebViewEngine parentEngine) {
        this.parentEngine = parentEngine;
        this.appContext = parentEngine.webView.getContext();
        this.dialogsHelper = new CordovaDialogsHelper(this.appContext);
    }

    @Override // android.webkit.WebChromeClient
    public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
        this.dialogsHelper.showAlert(message, new CordovaDialogsHelper.Result() { // from class: org.apache.cordova.engine.SystemWebChromeClient.1
            @Override // org.apache.cordova.CordovaDialogsHelper.Result
            public void gotResult(boolean success, String value) {
                if (success) {
                    result.confirm();
                } else {
                    result.cancel();
                }
            }
        });
        return true;
    }

    @Override // android.webkit.WebChromeClient
    public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
        this.dialogsHelper.showConfirm(message, new CordovaDialogsHelper.Result() { // from class: org.apache.cordova.engine.SystemWebChromeClient.2
            @Override // org.apache.cordova.CordovaDialogsHelper.Result
            public void gotResult(boolean success, String value) {
                if (success) {
                    result.confirm();
                } else {
                    result.cancel();
                }
            }
        });
        return true;
    }

    @Override // android.webkit.WebChromeClient
    public boolean onJsPrompt(WebView view, String origin, String message, String defaultValue, final JsPromptResult result) {
        String handledRet = this.parentEngine.bridge.promptOnJsPrompt(origin, message, defaultValue);
        if (handledRet != null) {
            result.confirm(handledRet);
            return true;
        }
        this.dialogsHelper.showPrompt(message, defaultValue, new CordovaDialogsHelper.Result() { // from class: org.apache.cordova.engine.SystemWebChromeClient.3
            @Override // org.apache.cordova.CordovaDialogsHelper.Result
            public void gotResult(boolean success, String value) {
                if (success) {
                    result.confirm(value);
                } else {
                    result.cancel();
                }
            }
        });
        return true;
    }

    @Override // android.webkit.WebChromeClient
    public void onExceededDatabaseQuota(String url, String databaseIdentifier, long currentQuota, long estimatedSize, long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater) {
        LOG.d(LOG_TAG, "onExceededDatabaseQuota estimatedSize: %d  currentQuota: %d  totalUsedQuota: %d", Long.valueOf(estimatedSize), Long.valueOf(currentQuota), Long.valueOf(totalUsedQuota));
        quotaUpdater.updateQuota(this.MAX_QUOTA);
    }

    @Override // android.webkit.WebChromeClient
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        super.onGeolocationPermissionsShowPrompt(origin, callback);
        callback.invoke(origin, true, false);
        CordovaPlugin geolocation = this.parentEngine.pluginManager.getPlugin("Geolocation");
        if (geolocation != null && !geolocation.hasPermisssion()) {
            geolocation.requestPermissions(0);
        }
    }

    @Override // android.webkit.WebChromeClient
    public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        this.parentEngine.getCordovaWebView().showCustomView(view, callback);
    }

    @Override // android.webkit.WebChromeClient
    public void onHideCustomView() {
        this.parentEngine.getCordovaWebView().hideCustomView();
    }

    @Override // android.webkit.WebChromeClient
    public View getVideoLoadingProgressView() {
        if (this.mVideoProgressView == null) {
            LinearLayout linearLayout = new LinearLayout(this.parentEngine.getView().getContext());
            linearLayout.setOrientation(1);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-2, -2);
            layoutParams.addRule(13);
            linearLayout.setLayoutParams(layoutParams);
            ProgressBar bar = new ProgressBar(this.parentEngine.getView().getContext());
            LinearLayout.LayoutParams barLayoutParams = new LinearLayout.LayoutParams(-2, -2);
            barLayoutParams.gravity = 17;
            bar.setLayoutParams(barLayoutParams);
            linearLayout.addView(bar);
            this.mVideoProgressView = linearLayout;
        }
        return this.mVideoProgressView;
    }

    @Override // android.webkit.WebChromeClient
    public boolean onShowFileChooser(WebView webView, final ValueCallback<Uri[]> filePathsCallback, WebChromeClient.FileChooserParams fileChooserParams) throws XmlPullParserException {
        Boolean selectMultiple;
        Intent captureIntent;
        Intent fileIntent = fileChooserParams.createIntent();
        if (fileChooserParams.getMode() != 1) {
            selectMultiple = false;
        } else {
            selectMultiple = true;
        }
        fileIntent.putExtra("android.intent.extra.ALLOW_MULTIPLE", selectMultiple);
        String[] acceptTypes = fileChooserParams.getAcceptTypes();
        if (acceptTypes.length > 1) {
            fileIntent.setType("*/*");
            fileIntent.putExtra("android.intent.extra.MIME_TYPES", acceptTypes);
        }
        Uri tempUri = null;
        if (!fileChooserParams.isCaptureEnabled()) {
            captureIntent = null;
        } else {
            captureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
            Context context = this.parentEngine.getView().getContext();
            if (context.getPackageManager().hasSystemFeature("android.hardware.camera.any") && captureIntent.resolveActivity(context.getPackageManager()) != null) {
                try {
                    File tempFile = createTempFile(context);
                    LOG.d(LOG_TAG, "Temporary photo capture file: " + tempFile);
                    tempUri = createUriForFile(context, tempFile);
                    LOG.d(LOG_TAG, "Temporary photo capture URI: " + tempUri);
                    captureIntent.putExtra("output", tempUri);
                } catch (IOException e) {
                    LOG.e(LOG_TAG, "Unable to create temporary file for photo capture", e);
                    captureIntent = null;
                }
            } else {
                LOG.w(LOG_TAG, "Device does not support photo capture");
                captureIntent = null;
            }
        }
        final Uri captureUri = tempUri;
        Intent chooserIntent = Intent.createChooser(fileIntent, null);
        if (captureIntent != null) {
            chooserIntent.putExtra("android.intent.extra.INITIAL_INTENTS", new Intent[]{captureIntent});
        }
        try {
            LOG.i(LOG_TAG, "Starting intent for file chooser");
            this.parentEngine.cordova.startActivityForResult(new CordovaPlugin() { // from class: org.apache.cordova.engine.SystemWebChromeClient.4
                @Override // org.apache.cordova.CordovaPlugin
                public void onActivityResult(int requestCode, int resultCode, Intent intent) {
                    Uri[] result = null;
                    if (resultCode == -1) {
                        List<Uri> uris = new ArrayList<>();
                        if (intent != null && intent.getData() != null) {
                            LOG.v(SystemWebChromeClient.LOG_TAG, "Adding file (single): " + intent.getData());
                            uris.add(intent.getData());
                        } else if (captureUri != null) {
                            LOG.v(SystemWebChromeClient.LOG_TAG, "Adding camera capture: " + captureUri);
                            uris.add(captureUri);
                        } else if (intent != null && intent.getClipData() != null) {
                            ClipData clipData = intent.getClipData();
                            int count = clipData.getItemCount();
                            for (int i = 0; i < count; i++) {
                                Uri uri = clipData.getItemAt(i).getUri();
                                LOG.v(SystemWebChromeClient.LOG_TAG, "Adding file (multiple): " + uri);
                                if (uri != null) {
                                    uris.add(uri);
                                }
                            }
                        }
                        if (!uris.isEmpty()) {
                            LOG.d(SystemWebChromeClient.LOG_TAG, "Receive file chooser URL: " + uris.toString());
                            result = (Uri[]) uris.toArray(new Uri[uris.size()]);
                        }
                    }
                    filePathsCallback.onReceiveValue(result);
                }
            }, chooserIntent, FILECHOOSER_RESULTCODE);
        } catch (ActivityNotFoundException e2) {
            LOG.w(LOG_TAG, "No activity found to handle file chooser intent.", e2);
            filePathsCallback.onReceiveValue(null);
        }
        return true;
    }

    private File createTempFile(Context context) throws IOException {
        File tempFile = File.createTempFile("temp", ".jpg", context.getCacheDir());
        return tempFile;
    }

    private Uri createUriForFile(Context context, File tempFile) throws XmlPullParserException, IOException {
        String appId = context.getPackageName();
        Uri uri = FileProvider.getUriForFile(context, appId + ".cdv.core.file.provider", tempFile);
        return uri;
    }

    @Override // android.webkit.WebChromeClient
    public void onPermissionRequest(PermissionRequest request) {
        LOG.d(LOG_TAG, "onPermissionRequest: " + Arrays.toString(request.getResources()));
        request.grant(request.getResources());
    }

    public void destroyLastDialog() {
        this.dialogsHelper.destroyLastDialog();
    }
}
