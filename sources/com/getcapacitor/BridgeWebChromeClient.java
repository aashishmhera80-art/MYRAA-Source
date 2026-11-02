package com.getcapacitor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.MimeTypeMap;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;
import com.getcapacitor.util.PermissionHelper;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes3.dex */
public class BridgeWebChromeClient extends WebChromeClient {
    private ActivityResultLauncher activityLauncher;
    private ActivityResultListener activityListener;
    private Bridge bridge;
    private ActivityResultLauncher permissionLauncher;
    private PermissionListener permissionListener;

    /* JADX INFO: Access modifiers changed from: private */
    interface ActivityResultListener {
        void onActivityResult(ActivityResult activityResult);
    }

    /* JADX INFO: Access modifiers changed from: private */
    interface PermissionListener {
        void onPermissionSelect(Boolean bool);
    }

    public BridgeWebChromeClient(Bridge bridge) {
        this.bridge = bridge;
        ActivityResultCallback<Map<String, Boolean>> permissionCallback = new ActivityResultCallback() { // from class: com.getcapacitor.BridgeWebChromeClient$$ExternalSyntheticLambda5
            @Override // androidx.activity.result.ActivityResultCallback
            public final void onActivityResult(Object obj) {
                this.f$0.lambda$new$0((Map) obj);
            }
        };
        this.permissionLauncher = bridge.registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissionCallback);
        this.activityLauncher = bridge.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback() { // from class: com.getcapacitor.BridgeWebChromeClient$$ExternalSyntheticLambda6
            @Override // androidx.activity.result.ActivityResultCallback
            public final void onActivityResult(Object obj) {
                this.f$0.lambda$new$1((ActivityResult) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(Map isGranted) {
        if (this.permissionListener != null) {
            boolean granted = true;
            for (Map.Entry<String, Boolean> permission : isGranted.entrySet()) {
                if (!permission.getValue().booleanValue()) {
                    granted = false;
                }
            }
            this.permissionListener.onPermissionSelect(Boolean.valueOf(granted));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(ActivityResult result) {
        if (this.activityListener != null) {
            this.activityListener.onActivityResult(result);
        }
    }

    @Override // android.webkit.WebChromeClient
    public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        callback.onCustomViewHidden();
        super.onShowCustomView(view, callback);
    }

    @Override // android.webkit.WebChromeClient
    public void onHideCustomView() {
        super.onHideCustomView();
    }

    @Override // android.webkit.WebChromeClient
    public void onPermissionRequest(final PermissionRequest request) {
        List<String> permissionList = new ArrayList<>();
        if (Arrays.asList(request.getResources()).contains("android.webkit.resource.VIDEO_CAPTURE")) {
            permissionList.add("android.permission.CAMERA");
        }
        if (Arrays.asList(request.getResources()).contains("android.webkit.resource.AUDIO_CAPTURE")) {
            permissionList.add("android.permission.MODIFY_AUDIO_SETTINGS");
            permissionList.add("android.permission.RECORD_AUDIO");
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = (String[]) permissionList.toArray(new String[0]);
            this.permissionListener = new PermissionListener() { // from class: com.getcapacitor.BridgeWebChromeClient$$ExternalSyntheticLambda11
                @Override // com.getcapacitor.BridgeWebChromeClient.PermissionListener
                public final void onPermissionSelect(Boolean bool) {
                    BridgeWebChromeClient.lambda$onPermissionRequest$2(request, bool);
                }
            };
            this.permissionLauncher.launch(permissions);
            return;
        }
        request.grant(request.getResources());
    }

    static /* synthetic */ void lambda$onPermissionRequest$2(PermissionRequest request, Boolean isGranted) {
        if (isGranted.booleanValue()) {
            request.grant(request.getResources());
        } else {
            request.deny();
        }
    }

    @Override // android.webkit.WebChromeClient
    public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
        if (this.bridge.getActivity().isFinishing()) {
            return true;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setMessage(message).setPositiveButton("OK", new DialogInterface.OnClickListener() { // from class: com.getcapacitor.BridgeWebChromeClient$$ExternalSyntheticLambda1
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                BridgeWebChromeClient.lambda$onJsAlert$3(result, dialogInterface, i);
            }
        }).setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.getcapacitor.BridgeWebChromeClient$$ExternalSyntheticLambda2
            @Override // android.content.DialogInterface.OnCancelListener
            public final void onCancel(DialogInterface dialogInterface) {
                BridgeWebChromeClient.lambda$onJsAlert$4(result, dialogInterface);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        return true;
    }

    static /* synthetic */ void lambda$onJsAlert$3(JsResult result, DialogInterface dialog, int buttonIndex) {
        dialog.dismiss();
        result.confirm();
    }

    static /* synthetic */ void lambda$onJsAlert$4(JsResult result, DialogInterface dialog) {
        dialog.dismiss();
        result.cancel();
    }

    @Override // android.webkit.WebChromeClient
    public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
        if (this.bridge.getActivity().isFinishing()) {
            return true;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setMessage(message).setPositiveButton("OK", new DialogInterface.OnClickListener() { // from class: com.getcapacitor.BridgeWebChromeClient$$ExternalSyntheticLambda12
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                BridgeWebChromeClient.lambda$onJsConfirm$5(result, dialogInterface, i);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() { // from class: com.getcapacitor.BridgeWebChromeClient$$ExternalSyntheticLambda13
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                BridgeWebChromeClient.lambda$onJsConfirm$6(result, dialogInterface, i);
            }
        }).setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.getcapacitor.BridgeWebChromeClient$$ExternalSyntheticLambda14
            @Override // android.content.DialogInterface.OnCancelListener
            public final void onCancel(DialogInterface dialogInterface) {
                BridgeWebChromeClient.lambda$onJsConfirm$7(result, dialogInterface);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        return true;
    }

    static /* synthetic */ void lambda$onJsConfirm$5(JsResult result, DialogInterface dialog, int buttonIndex) {
        dialog.dismiss();
        result.confirm();
    }

    static /* synthetic */ void lambda$onJsConfirm$6(JsResult result, DialogInterface dialog, int buttonIndex) {
        dialog.dismiss();
        result.cancel();
    }

    static /* synthetic */ void lambda$onJsConfirm$7(JsResult result, DialogInterface dialog) {
        dialog.dismiss();
        result.cancel();
    }

    @Override // android.webkit.WebChromeClient
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
        if (this.bridge.getActivity().isFinishing()) {
            return true;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        final EditText input = new EditText(view.getContext());
        builder.setMessage(message).setView(input).setPositiveButton("OK", new DialogInterface.OnClickListener() { // from class: com.getcapacitor.BridgeWebChromeClient$$ExternalSyntheticLambda8
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                BridgeWebChromeClient.lambda$onJsPrompt$8(input, result, dialogInterface, i);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() { // from class: com.getcapacitor.BridgeWebChromeClient$$ExternalSyntheticLambda9
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                BridgeWebChromeClient.lambda$onJsPrompt$9(result, dialogInterface, i);
            }
        }).setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.getcapacitor.BridgeWebChromeClient$$ExternalSyntheticLambda10
            @Override // android.content.DialogInterface.OnCancelListener
            public final void onCancel(DialogInterface dialogInterface) {
                BridgeWebChromeClient.lambda$onJsPrompt$10(result, dialogInterface);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        return true;
    }

    static /* synthetic */ void lambda$onJsPrompt$8(EditText input, JsPromptResult result, DialogInterface dialog, int buttonIndex) {
        dialog.dismiss();
        String inputText1 = input.getText().toString().trim();
        result.confirm(inputText1);
    }

    static /* synthetic */ void lambda$onJsPrompt$9(JsPromptResult result, DialogInterface dialog, int buttonIndex) {
        dialog.dismiss();
        result.cancel();
    }

    static /* synthetic */ void lambda$onJsPrompt$10(JsPromptResult result, DialogInterface dialog) {
        dialog.dismiss();
        result.cancel();
    }

    @Override // android.webkit.WebChromeClient
    public void onGeolocationPermissionsShowPrompt(final String origin, final GeolocationPermissions.Callback callback) {
        super.onGeolocationPermissionsShowPrompt(origin, callback);
        Logger.debug("onGeolocationPermissionsShowPrompt: DOING IT HERE FOR ORIGIN: " + origin);
        String[] geoPermissions = {"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"};
        if (!PermissionHelper.hasPermissions(this.bridge.getContext(), geoPermissions)) {
            this.permissionListener = new PermissionListener() { // from class: com.getcapacitor.BridgeWebChromeClient$$ExternalSyntheticLambda4
                @Override // com.getcapacitor.BridgeWebChromeClient.PermissionListener
                public final void onPermissionSelect(Boolean bool) {
                    this.f$0.lambda$onGeolocationPermissionsShowPrompt$11(callback, origin, bool);
                }
            };
            this.permissionLauncher.launch(geoPermissions);
        } else {
            callback.invoke(origin, true, false);
            Logger.debug("onGeolocationPermissionsShowPrompt: has required permission");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onGeolocationPermissionsShowPrompt$11(GeolocationPermissions.Callback callback, String origin, Boolean isGranted) {
        if (isGranted.booleanValue()) {
            callback.invoke(origin, true, false);
            return;
        }
        String[] coarsePermission = {"android.permission.ACCESS_COARSE_LOCATION"};
        if (Build.VERSION.SDK_INT >= 31 && PermissionHelper.hasPermissions(this.bridge.getContext(), coarsePermission)) {
            callback.invoke(origin, true, false);
        } else {
            callback.invoke(origin, false, false);
        }
    }

    @Override // android.webkit.WebChromeClient
    public boolean onShowFileChooser(WebView webView, final ValueCallback<Uri[]> filePathCallback, final WebChromeClient.FileChooserParams fileChooserParams) {
        List<String> acceptTypes = Arrays.asList(fileChooserParams.getAcceptTypes());
        boolean captureEnabled = fileChooserParams.isCaptureEnabled();
        boolean capturePhoto = captureEnabled && acceptTypes.contains("image/*");
        final boolean captureVideo = captureEnabled && acceptTypes.contains("video/*");
        if (capturePhoto || captureVideo) {
            if (isMediaCaptureSupported()) {
                showMediaCaptureOrFilePicker(filePathCallback, fileChooserParams, captureVideo);
            } else {
                this.permissionListener = new PermissionListener() { // from class: com.getcapacitor.BridgeWebChromeClient$$ExternalSyntheticLambda3
                    @Override // com.getcapacitor.BridgeWebChromeClient.PermissionListener
                    public final void onPermissionSelect(Boolean bool) {
                        this.f$0.lambda$onShowFileChooser$12(filePathCallback, fileChooserParams, captureVideo, bool);
                    }
                };
                String[] camPermission = {"android.permission.CAMERA"};
                this.permissionLauncher.launch(camPermission);
            }
        } else {
            showFilePicker(filePathCallback, fileChooserParams);
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onShowFileChooser$12(ValueCallback filePathCallback, WebChromeClient.FileChooserParams fileChooserParams, boolean captureVideo, Boolean isGranted) {
        if (isGranted.booleanValue()) {
            showMediaCaptureOrFilePicker(filePathCallback, fileChooserParams, captureVideo);
        } else {
            Logger.warn(Logger.tags("FileChooser"), "Camera permission not granted");
            filePathCallback.onReceiveValue(null);
        }
    }

    private boolean isMediaCaptureSupported() {
        String[] permissions = {"android.permission.CAMERA"};
        return PermissionHelper.hasPermissions(this.bridge.getContext(), permissions) || !PermissionHelper.hasDefinedPermission(this.bridge.getContext(), "android.permission.CAMERA");
    }

    private void showMediaCaptureOrFilePicker(ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams, boolean isVideo) {
        boolean shown;
        if (isVideo) {
            shown = showVideoCapturePicker(filePathCallback);
        } else {
            shown = showImageCapturePicker(filePathCallback);
        }
        if (!shown) {
            Logger.warn(Logger.tags("FileChooser"), "Media capture intent could not be launched. Falling back to default file picker.");
            showFilePicker(filePathCallback, fileChooserParams);
        }
    }

    private boolean showImageCapturePicker(final ValueCallback<Uri[]> filePathCallback) {
        Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (takePictureIntent.resolveActivity(this.bridge.getActivity().getPackageManager()) == null) {
            return false;
        }
        try {
            final Uri imageFileUri = createImageFileUri();
            takePictureIntent.putExtra("output", imageFileUri);
            takePictureIntent.addFlags(3);
            this.activityListener = new ActivityResultListener() { // from class: com.getcapacitor.BridgeWebChromeClient$$ExternalSyntheticLambda7
                @Override // com.getcapacitor.BridgeWebChromeClient.ActivityResultListener
                public final void onActivityResult(ActivityResult activityResult) {
                    BridgeWebChromeClient.lambda$showImageCapturePicker$13(imageFileUri, filePathCallback, activityResult);
                }
            };
            this.activityLauncher.launch(takePictureIntent);
            return true;
        } catch (Exception ex) {
            Logger.error("Unable to create temporary media capture file: " + ex.getMessage());
            return false;
        }
    }

    static /* synthetic */ void lambda$showImageCapturePicker$13(Uri imageFileUri, ValueCallback filePathCallback, ActivityResult activityResult) {
        Uri[] result = null;
        if (activityResult.getResultCode() == -1) {
            result = new Uri[]{imageFileUri};
        }
        filePathCallback.onReceiveValue(result);
    }

    private boolean showVideoCapturePicker(final ValueCallback<Uri[]> filePathCallback) {
        Intent takeVideoIntent = new Intent("android.media.action.VIDEO_CAPTURE");
        if (takeVideoIntent.resolveActivity(this.bridge.getActivity().getPackageManager()) == null) {
            return false;
        }
        this.activityListener = new ActivityResultListener() { // from class: com.getcapacitor.BridgeWebChromeClient$$ExternalSyntheticLambda0
            @Override // com.getcapacitor.BridgeWebChromeClient.ActivityResultListener
            public final void onActivityResult(ActivityResult activityResult) {
                BridgeWebChromeClient.lambda$showVideoCapturePicker$14(filePathCallback, activityResult);
            }
        };
        this.activityLauncher.launch(takeVideoIntent);
        return true;
    }

    static /* synthetic */ void lambda$showVideoCapturePicker$14(ValueCallback filePathCallback, ActivityResult activityResult) {
        Uri[] result = null;
        if (activityResult.getResultCode() == -1) {
            result = new Uri[]{activityResult.getData().getData()};
        }
        filePathCallback.onReceiveValue(result);
    }

    private void showFilePicker(final ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
        Intent intent = fileChooserParams.createIntent();
        if (fileChooserParams.getMode() == 1) {
            intent.putExtra("android.intent.extra.ALLOW_MULTIPLE", true);
        }
        if (fileChooserParams.getAcceptTypes().length > 1 || intent.getType().startsWith(".")) {
            String[] validTypes = getValidTypes(fileChooserParams.getAcceptTypes());
            intent.putExtra("android.intent.extra.MIME_TYPES", validTypes);
            if (intent.getType().startsWith(".")) {
                intent.setType(validTypes[0]);
            }
        }
        try {
            this.activityListener = new ActivityResultListener() { // from class: com.getcapacitor.BridgeWebChromeClient$$ExternalSyntheticLambda15
                @Override // com.getcapacitor.BridgeWebChromeClient.ActivityResultListener
                public final void onActivityResult(ActivityResult activityResult) {
                    BridgeWebChromeClient.lambda$showFilePicker$15(filePathCallback, activityResult);
                }
            };
            this.activityLauncher.launch(intent);
        } catch (ActivityNotFoundException e) {
            filePathCallback.onReceiveValue(null);
        }
    }

    static /* synthetic */ void lambda$showFilePicker$15(ValueCallback filePathCallback, ActivityResult activityResult) {
        Uri[] result;
        Intent resultIntent = activityResult.getData();
        if (activityResult.getResultCode() == -1 && resultIntent.getClipData() != null) {
            int numFiles = resultIntent.getClipData().getItemCount();
            result = new Uri[numFiles];
            for (int i = 0; i < numFiles; i++) {
                result[i] = resultIntent.getClipData().getItemAt(i).getUri();
            }
        } else {
            result = WebChromeClient.FileChooserParams.parseResult(activityResult.getResultCode(), resultIntent);
        }
        filePathCallback.onReceiveValue(result);
    }

    private String[] getValidTypes(String[] currentTypes) {
        List<String> validTypes = new ArrayList<>();
        MimeTypeMap mtm = MimeTypeMap.getSingleton();
        for (String mime : currentTypes) {
            if (mime.startsWith(".")) {
                String extension = mime.substring(1);
                String extensionMime = mtm.getMimeTypeFromExtension(extension);
                if (extensionMime != null && !validTypes.contains(extensionMime)) {
                    validTypes.add(extensionMime);
                }
            } else if (!validTypes.contains(mime)) {
                validTypes.add(mime);
            }
        }
        Object[] validObj = validTypes.toArray();
        return (String[]) Arrays.copyOf(validObj, validObj.length, String[].class);
    }

    @Override // android.webkit.WebChromeClient
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        String tag = Logger.tags("Console");
        if (consoleMessage.message() != null && isValidMsg(consoleMessage.message())) {
            String msg = String.format("File: %s - Line %d - Msg: %s", consoleMessage.sourceId(), Integer.valueOf(consoleMessage.lineNumber()), consoleMessage.message());
            String level = consoleMessage.messageLevel().name();
            if ("ERROR".equalsIgnoreCase(level)) {
                Logger.error(tag, msg, null);
            } else if ("WARNING".equalsIgnoreCase(level)) {
                Logger.warn(tag, msg);
            } else if ("TIP".equalsIgnoreCase(level)) {
                Logger.debug(tag, msg);
            } else {
                Logger.info(tag, msg);
            }
        }
        return true;
    }

    public boolean isValidMsg(String msg) {
        return (msg.contains("%cresult %c") || msg.contains("%cnative %c") || msg.equalsIgnoreCase("console.groupEnd")) ? false : true;
    }

    private Uri createImageFileUri() throws IOException {
        Activity activity = this.bridge.getActivity();
        File photoFile = createImageFile(activity);
        return FileProvider.getUriForFile(activity, this.bridge.getContext().getPackageName() + ".fileprovider", photoFile);
    }

    private File createImageFile(Activity activity) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }
}
