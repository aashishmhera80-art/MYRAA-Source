package ai.myraa.mobile;

import android.content.ActivityNotFoundException;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.security.keystore.KeyGenParameterSpec;
import android.util.Base64;
import android.view.KeyEvent;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.PermissionState;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;
import java.lang.ref.WeakReference;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.net.ssl.SSLHandshakeException;
import kotlin.UByte;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.internal.ws.WebSocketProtocol;
import okio.ByteString;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes6.dex */
@CapacitorPlugin(name = "MyraaNative", permissions = {@Permission(alias = "contacts", strings = {"android.permission.READ_CONTACTS"}), @Permission(alias = "microphone", strings = {"android.permission.RECORD_AUDIO"}), @Permission(alias = "camera", strings = {"android.permission.CAMERA"}), @Permission(alias = "notifications", strings = {"android.permission.POST_NOTIFICATIONS"})})
public class MyraaNativePlugin extends Plugin {
    private static final String KEY_ALIAS = "myraa_gemini_aes_key";
    private static final String LIVE_MODEL = "gemini-3.1-flash-live-preview";
    private static final String MEMORY_MODEL = "gemini-3.5-flash";
    private static final String PREFS = "myraa_secure_mobile";
    private static final String PREF_ENCRYPTED_KEY = "gemini_key_v1";
    private static final String PREF_SETUP = "setup_complete";
    private static final String PREF_VALIDATED = "gemini_validated";
    private volatile boolean geminiReady;
    private Runnable geminiSetupTimeout;
    private WebSocket geminiSocket;
    private static final MediaType JSON_MEDIA = MediaType.get("application/json; charset=utf-8");
    private static final Set<String> APPROVED_ACTIONS = Collections.unmodifiableSet(new HashSet(Arrays.asList("OPEN_APP", "OPEN_CAMERA", "OPEN_GALLERY", "OPEN_SETTINGS", "OPEN_WIFI_SETTINGS", "OPEN_BLUETOOTH_SETTINGS", "OPEN_BATTERY_SETTINGS", "OPEN_DISPLAY_SETTINGS", "OPEN_STORAGE_SETTINGS", "OPEN_HOTSPOT_SETTINGS", "OPEN_ACCESSIBILITY_SETTINGS", "OPEN_NOTIFICATION_SETTINGS", "OPEN_DND_SETTINGS", "CHANGE_BRIGHTNESS", "CLOSE_MYRAAA", "OPEN_MAPS", "START_NAVIGATION", "START_TIMER", "CREATE_ALARM", "CREATE_REMINDER", "CREATE_CALENDAR_EVENT", "CALL_CONTACT", "PREPARE_SMS", "SEND_SMS", "PREPARE_WHATSAPP_MESSAGE", "SEND_WHATSAPP_MESSAGE", "WEB_SEARCH", "YOUTUBE_SEARCH", "MEDIA_PLAY", "MEDIA_PAUSE", "MEDIA_NEXT", "MEDIA_PREVIOUS", "CHANGE_MEDIA_VOLUME", "FLASHLIGHT_ON", "FLASHLIGHT_OFF", "READ_NOTIFICATION", "CONTROL_3D_CAMERA", "OPEN_MYRAAA_SETTINGS", "OPEN_API_SETUP", "START_BACKGROUND_MODE", "STOP_BACKGROUND_MODE", "STOP_SPEAKING", "CANCEL_PENDING_ACTION")));
    private static final Set<String> SENSITIVE_ACTIONS = Collections.unmodifiableSet(new HashSet(Arrays.asList("CALL_CONTACT", "SEND_SMS", "SEND_WHATSAPP_MESSAGE", "CREATE_REMINDER", "CREATE_CALENDAR_EVENT")));
    private static WeakReference<MyraaNativePlugin> activePlugin = new WeakReference<>(null);
    private final OkHttpClient http = new OkHttpClient.Builder().pingInterval(20, TimeUnit.SECONDS).build();
    private final Map<String, PendingConfirmation> confirmations = new ConcurrentHashMap();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override // com.getcapacitor.Plugin
    public void load() {
        activePlugin = new WeakReference<>(this);
    }

    public static void emitBackgroundWake(String phrase) {
        MyraaNativePlugin plugin = activePlugin.get();
        if (plugin == null) {
            return;
        }
        JSObject event = new JSObject();
        event.put("phrase", phrase == null ? "" : phrase);
        plugin.notifyListeners("backgroundWake", event, true);
    }

    @PluginMethod
    public void getApiStatus(PluginCall call) {
        call.resolve(apiStatus());
    }

    @PluginMethod
    public void testAndSaveGeminiKey(final PluginCall call) {
        final String candidate = safe(call.getString("apiKey", "")).trim();
        if (candidate.isEmpty()) {
            call.reject("Enter a Gemini API key first.");
        } else {
            new Thread(new Runnable() { // from class: ai.myraa.mobile.MyraaNativePlugin$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$testAndSaveGeminiKey$0(candidate, call);
                }
            }, "myraa-api-test").start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$testAndSaveGeminiKey$0(String candidate, PluginCall call) {
        try {
            try {
                Request request = new Request.Builder().url("https://generativelanguage.googleapis.com/v1beta/models?pageSize=1000&key=" + Uri.encode(candidate)).get().build();
                Response response = this.http.newCall(request).execute();
                try {
                    if (!response.isSuccessful()) {
                        String message = googleApiError(response, candidate);
                        JSObject result = apiStatus();
                        result.put("ok", false);
                        result.put("message", message);
                        call.resolve(result);
                        if (response != null) {
                            response.close();
                            return;
                        }
                        return;
                    }
                    if (response != null) {
                        response.close();
                    }
                    try {
                        String encrypted = encryptWithRecovery(candidate);
                        encryptedPrefs().edit().putString(PREF_ENCRYPTED_KEY, encrypted).putBoolean(PREF_VALIDATED, true).apply();
                        JSObject result2 = apiStatus();
                        result2.put("ok", true);
                        result2.put("message", "API connected successfully.");
                        call.resolve(result2);
                    } catch (Exception storageError) {
                        JSObject result3 = apiStatus();
                        result3.put("ok", false);
                        result3.put("message", "Google accepted the key, but Android secure storage failed (" + storageError.getClass().getSimpleName() + "). Restart MYRAA and try again.");
                        call.resolve(result3);
                    }
                } catch (Throwable th) {
                    if (response != null) {
                        try {
                            response.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } catch (Exception error) {
                JSObject result4 = apiStatus();
                result4.put("ok", false);
                result4.put("message", "Gemini validation failed (" + error.getClass().getSimpleName() + "). Please try again.");
                call.resolve(result4);
            }
        } catch (ConnectException e) {
            JSObject result5 = apiStatus();
            result5.put("ok", false);
            result5.put("message", "The phone could not reach Google Gemini. Try another network or disable VPN/private DNS.");
            call.resolve(result5);
        } catch (SocketTimeoutException e2) {
            JSObject result6 = apiStatus();
            result6.put("ok", false);
            result6.put("message", "Google Gemini timed out. Check your connection and try again.");
            call.resolve(result6);
        } catch (UnknownHostException e3) {
            JSObject result7 = apiStatus();
            result7.put("ok", false);
            result7.put("message", "Internet connection is unavailable.");
            call.resolve(result7);
        } catch (SSLHandshakeException e4) {
            JSObject result8 = apiStatus();
            result8.put("ok", false);
            result8.put("message", "Secure connection to Google failed. Check the phone's date, time, VPN, or private DNS.");
            call.resolve(result8);
        }
    }

    @PluginMethod
    public void completeApiSetup(PluginCall call) {
        JSObject status = apiStatus();
        if (!status.optBoolean("configured") || !status.optBoolean("validated")) {
            call.reject("A tested Gemini key is required.");
            return;
        }
        encryptedPrefs().edit().putBoolean(PREF_SETUP, true).apply();
        JSObject result = new JSObject();
        result.put("ok", true);
        call.resolve(result);
    }

    @PluginMethod
    public void deleteGeminiKey(PluginCall call) {
        if (!Boolean.TRUE.equals(call.getBoolean("confirmed", false))) {
            call.reject("Confirmation is required.");
            return;
        }
        stopGeminiInternal();
        encryptedPrefs().edit().remove(PREF_ENCRYPTED_KEY).putBoolean(PREF_VALIDATED, false).putBoolean(PREF_SETUP, false).apply();
        JSObject result = new JSObject();
        result.put("ok", true);
        call.resolve(result);
    }

    @PluginMethod
    public void openOfficialApiPage(PluginCall call) {
        startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://aistudio.google.com/app/apikey")));
        call.resolve();
    }

    @PluginMethod
    public void readClipboard(PluginCall call) {
        CharSequence value;
        JSObject result = new JSObject();
        String text = "";
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService("clipboard");
        if (clipboard != null && clipboard.hasPrimaryClip() && clipboard.getPrimaryClip() != null && clipboard.getPrimaryClip().getItemCount() > 0 && (value = clipboard.getPrimaryClip().getItemAt(0).coerceToText(getContext())) != null) {
            text = value.toString();
        }
        result.put("text", text);
        call.resolve(result);
    }

    @PluginMethod
    public void requestMicrophonePermission(PluginCall call) {
        if (getPermissionState("microphone") != PermissionState.GRANTED) {
            requestPermissionForAlias("microphone", call, "microphonePermissionCallback");
            return;
        }
        JSObject result = new JSObject();
        result.put("granted", true);
        call.resolve(result);
    }

    @PermissionCallback
    private void microphonePermissionCallback(PluginCall call) {
        JSObject result = new JSObject();
        result.put("granted", getPermissionState("microphone") == PermissionState.GRANTED);
        call.resolve(result);
    }

    @PluginMethod
    public void startGemini(PluginCall call) {
        String prompt = safe(call.getString("systemInstruction", ""));
        JSArray tools = call.getArray("tools", new JSArray());
        if (prompt.isEmpty()) {
            call.reject("The preserved MYRAA system instruction is missing.");
            return;
        }
        try {
            String apiKey = decryptSavedKey();
            if (apiKey.isEmpty()) {
                call.reject("No Gemini API key is configured. Open API Setup.");
                return;
            }
            stopGeminiInternal();
            emitStatus("connecting_gemini");
            try {
                JSONObject generation = new JSONObject().put("responseModalities", new JSONArray().put("AUDIO")).put("speechConfig", new JSONObject().put("voiceConfig", new JSONObject().put("prebuiltVoiceConfig", new JSONObject().put("voiceName", "Aoede"))));
                JSONObject setup = new JSONObject().put("model", "models/gemini-3.1-flash-live-preview").put("generationConfig", generation).put("inputAudioTranscription", new JSONObject()).put("outputAudioTranscription", new JSONObject()).put("systemInstruction", new JSONObject().put("parts", new JSONArray().put(new JSONObject().put("text", prompt)))).put("tools", tools);
                String setupMessage = new JSONObject().put("setup", setup).toString();
                Request request = new Request.Builder().url("wss://generativelanguage.googleapis.com/ws/google.ai.generativelanguage.v1beta.GenerativeService.BidiGenerateContent?key=" + Uri.encode(apiKey)).build();
                this.geminiSocket = this.http.newWebSocket(request, new AnonymousClass1(setupMessage));
                JSObject result = new JSObject();
                result.put("ok", true);
                call.resolve(result);
            } catch (Exception e) {
                call.reject("Gemini Live could not be initialized.");
            }
        } catch (Exception e2) {
            call.reject("The saved Gemini API key could not be decrypted. Open API Setup.");
        }
    }

    /* JADX INFO: renamed from: ai.myraa.mobile.MyraaNativePlugin$1, reason: invalid class name */
    class AnonymousClass1 extends WebSocketListener {
        final /* synthetic */ String val$setupMessage;

        AnonymousClass1(String str) {
            this.val$setupMessage = str;
        }

        @Override // okhttp3.WebSocketListener
        public void onOpen(final WebSocket webSocket, Response response) {
            webSocket.send(this.val$setupMessage);
            MyraaNativePlugin.this.cancelGeminiSetupTimeout();
            MyraaNativePlugin.this.geminiSetupTimeout = new Runnable() { // from class: ai.myraa.mobile.MyraaNativePlugin$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onOpen$0(webSocket);
                }
            };
            MyraaNativePlugin.this.mainHandler.postDelayed(MyraaNativePlugin.this.geminiSetupTimeout, 15000L);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onOpen$0(WebSocket webSocket) {
            if (MyraaNativePlugin.this.geminiSocket != webSocket || MyraaNativePlugin.this.geminiReady) {
                return;
            }
            MyraaNativePlugin.this.geminiSocket = null;
            webSocket.cancel();
            MyraaNativePlugin.this.emitError("Gemini Live setup timed out. Check the network and tap ON again.", "TIMEOUT");
        }

        @Override // okhttp3.WebSocketListener
        public void onMessage(WebSocket webSocket, String text) {
            MyraaNativePlugin.this.handleGeminiMessage(text);
        }

        @Override // okhttp3.WebSocketListener
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            MyraaNativePlugin.this.handleGeminiMessage(bytes.utf8());
        }

        @Override // okhttp3.WebSocketListener
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(code, reason);
        }

        @Override // okhttp3.WebSocketListener
        public void onClosed(WebSocket webSocket, int code, String reason) {
            MyraaNativePlugin.this.cancelGeminiSetupTimeout();
            MyraaNativePlugin.this.geminiReady = false;
            if (MyraaNativePlugin.this.geminiSocket == webSocket) {
                MyraaNativePlugin.this.geminiSocket = null;
            }
            if (code != 1000) {
                MyraaNativePlugin.this.emitError("Gemini Live closed (" + code + "). Try Again or open API Setup.", code == 1008 ? "INVALID_API_KEY" : "LIVE_CLOSED");
            }
        }

        @Override // okhttp3.WebSocketListener
        public void onFailure(WebSocket webSocket, Throwable error, Response response) {
            MyraaNativePlugin.this.cancelGeminiSetupTimeout();
            MyraaNativePlugin.this.geminiReady = false;
            if (MyraaNativePlugin.this.geminiSocket == webSocket) {
                MyraaNativePlugin.this.geminiSocket = null;
            }
            int code = response != null ? response.code() : 0;
            MyraaNativePlugin.this.emitError((code == 401 || code == 403) ? "Google rejected the saved Gemini API key. Update or test it in API Setup." : "Gemini Live connection failed. Check the internet connection and try again.", (code == 401 || code == 403) ? "INVALID_API_KEY" : "NETWORK");
        }
    }

    @PluginMethod
    public void sendGeminiAudio(PluginCall call) {
        sendRealtime("audio/pcm;rate=16000", call.getString("base64", ""));
        call.resolve();
    }

    @PluginMethod
    public void sendGeminiVideo(PluginCall call) {
        sendRealtime("image/jpeg", call.getString("base64", ""));
        call.resolve();
    }

    @PluginMethod
    public void sendGeminiText(PluginCall call) {
        String text = safe(call.getString("text", "")).trim();
        if (!text.isEmpty() && this.geminiSocket != null) {
            try {
                this.geminiSocket.send(new JSONObject().put("realtimeInput", new JSONObject().put("text", text)).toString());
                JSObject event = new JSObject();
                event.put("type", "transcription");
                event.put("role", "user");
                event.put("text", text);
                notifyListeners("geminiMessage", event);
            } catch (JSONException e) {
            }
        }
        call.resolve();
    }

    @PluginMethod
    public void sendToolResponse(PluginCall call) {
        if (this.geminiSocket == null) {
            call.resolve();
            return;
        }
        try {
            JSONObject response = new JSONObject().put("id", call.getString("id", "")).put("name", call.getString("name", "")).put("response", new JSONObject().put("output", call.getData().opt("output")));
            this.geminiSocket.send(new JSONObject().put("toolResponse", new JSONObject().put("functionResponses", new JSONArray().put(response))).toString());
        } catch (JSONException e) {
        }
        call.resolve();
    }

    @PluginMethod
    public void stopGemini(PluginCall call) {
        stopGeminiInternal();
        call.resolve();
    }

    @PluginMethod
    public void consolidateMemories(final PluginCall call) {
        final String prompt = safe(call.getString("prompt", ""));
        new Thread(new Runnable() { // from class: ai.myraa.mobile.MyraaNativePlugin$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$consolidateMemories$1(prompt, call);
            }
        }, "myraa-memory").start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$consolidateMemories$1(String prompt, PluginCall call) {
        try {
            String key = decryptSavedKey();
            JSONObject requestJson = new JSONObject().put("contents", new JSONArray().put(new JSONObject().put("parts", new JSONArray().put(new JSONObject().put("text", prompt))))).put("generationConfig", new JSONObject().put("responseMimeType", "application/json"));
            Request request = new Request.Builder().url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent").header("x-goog-api-key", key).post(RequestBody.create(requestJson.toString(), JSON_MEDIA)).build();
            Response response = this.http.newCall(request).execute();
            try {
                if (!response.isSuccessful() || response.body() == null) {
                    throw new IllegalStateException("Memory service unavailable");
                }
                JSONObject body = new JSONObject(response.body().string());
                String json = body.getJSONArray("candidates").getJSONObject(0).getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text");
                JSObject result = new JSObject();
                result.put("json", json);
                call.resolve(result);
                if (response != null) {
                    response.close();
                }
            } catch (Throwable th) {
                if (response != null) {
                    try {
                        response.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        } catch (Exception e) {
            call.reject("Memory consolidation is temporarily unavailable.");
        }
    }

    @PluginMethod
    public void listLaunchableApps(PluginCall pluginCall) {
        JSArray apps = new JSArray();
        for (AppEntry app : installedApps()) {
            JSObject item = new JSObject();
            item.put("label", app.label);
            item.put("packageName", app.packageName);
            apps.put(item);
        }
        JSObject result = new JSObject();
        result.put("apps", (Object) apps);
        pluginCall.resolve(result);
    }

    @PluginMethod
    public void resolveContacts(PluginCall call) {
        if (getPermissionState("contacts") != PermissionState.GRANTED) {
            requestPermissionForAlias("contacts", call, "contactsPermissionCallback");
        } else {
            resolveContactsNow(call);
        }
    }

    @PermissionCallback
    private void contactsPermissionCallback(PluginCall call) {
        if (getPermissionState("contacts") != PermissionState.GRANTED) {
            call.reject("Contacts permission was denied.");
        } else {
            resolveContactsNow(call);
        }
    }

    private void resolveContactsNow(PluginCall pluginCall) {
        JSArray contacts = new JSArray();
        for (ContactEntry entry : findContacts(pluginCall.getString("query", ""))) {
            JSObject item = new JSObject();
            item.put("name", entry.name);
            item.put("phone", entry.phone);
            contacts.put(item);
        }
        JSObject result = new JSObject();
        result.put("contacts", (Object) contacts);
        pluginCall.resolve(result);
    }

    @PluginMethod
    public void createConfirmation(PluginCall call) {
        JSObject action = call.getObject("action");
        if (action == null || !APPROVED_ACTIONS.contains(action.optString("action"))) {
            call.reject("Unknown action rejected.");
            return;
        }
        String token = UUID.randomUUID().toString();
        long expiresAt = System.currentTimeMillis() + 60000;
        this.confirmations.put(token, new PendingConfirmation(action.optString("action"), fingerprint(action), expiresAt));
        purgeConfirmations();
        JSObject result = new JSObject();
        result.put("token", token);
        result.put("expiresAt", expiresAt);
        call.resolve(result);
    }

    @PluginMethod
    public void executeAction(PluginCall call) {
        JSObject action = call.getObject("action");
        if (action == null) {
            call.reject("Missing Android action.");
            return;
        }
        String name = action.optString("action", "");
        if (!APPROVED_ACTIONS.contains(name)) {
            call.resolve(actionResult(false, "Unknown action rejected by the native allowlist."));
            return;
        }
        if (needsContacts(name) && getPermissionState("contacts") != PermissionState.GRANTED) {
            requestPermissionForAlias("contacts", call, "executeAfterContactsPermission");
            return;
        }
        if (("FLASHLIGHT_ON".equals(name) || "FLASHLIGHT_OFF".equals(name)) && getPermissionState("camera") != PermissionState.GRANTED) {
            requestPermissionForAlias("camera", call, "executeAfterCameraPermission");
        } else if (!SENSITIVE_ACTIONS.contains(name) || validConfirmation(action)) {
            call.resolve(executeActionNow(action));
        } else {
            call.resolve(actionResult(false, "Fresh confirmation is required before this action."));
        }
    }

    @PermissionCallback
    private void executeAfterContactsPermission(PluginCall call) {
        if (getPermissionState("contacts") != PermissionState.GRANTED) {
            call.resolve(actionResult(false, "Contacts permission was denied."));
        } else {
            executeAction(call);
        }
    }

    @PermissionCallback
    private void executeAfterCameraPermission(PluginCall call) {
        if (getPermissionState("camera") != PermissionState.GRANTED) {
            call.resolve(actionResult(false, "Camera permission was denied."));
        } else {
            executeAction(call);
        }
    }

    @PluginMethod
    public void startBackgroundMode(PluginCall call) {
        if (getPermissionState("microphone") != PermissionState.GRANTED) {
            requestPermissionForAlias("microphone", call, "startBackgroundAfterPermission");
        } else if (Build.VERSION.SDK_INT < 33 || getPermissionState("notifications") == PermissionState.GRANTED) {
            startBackgroundNow(call);
        } else {
            requestPermissionForAlias("notifications", call, "startBackgroundAfterNotificationPermission");
        }
    }

    @PermissionCallback
    private void startBackgroundAfterPermission(PluginCall call) {
        if (getPermissionState("microphone") != PermissionState.GRANTED) {
            call.resolve(actionResult(false, "Microphone permission was denied."));
        } else if (Build.VERSION.SDK_INT < 33 || getPermissionState("notifications") == PermissionState.GRANTED) {
            startBackgroundNow(call);
        } else {
            requestPermissionForAlias("notifications", call, "startBackgroundAfterNotificationPermission");
        }
    }

    @PermissionCallback
    private void startBackgroundAfterNotificationPermission(PluginCall call) {
        if (getPermissionState("notifications") != PermissionState.GRANTED) {
            call.resolve(actionResult(false, "Notification permission is required so Background Voice Mode stays visible."));
        } else {
            startBackgroundNow(call);
        }
    }

    private void startBackgroundNow(PluginCall call) {
        Intent service = new Intent(getContext(), (Class<?>) MyraaBackgroundService.class).setAction(MyraaBackgroundService.ACTION_RESUME).putExtra("wakePhrase", call.getString("wakePhrase", "hey myraa"));
        ContextCompat.startForegroundService(getContext(), service);
        call.resolve(actionResult(true, "Background Voice Mode started with a visible notification."));
    }

    @PluginMethod
    public void stopBackgroundMode(PluginCall call) {
        getContext().startService(new Intent(getContext(), (Class<?>) MyraaBackgroundService.class).setAction(MyraaBackgroundService.ACTION_STOP));
        call.resolve(actionResult(true, "Background Voice Mode stopped."));
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:5:0x0011  */
    private JSObject executeActionNow(JSObject action) {
        String name = action.optString("action");
        try {
            switch (name) {
                case "OPEN_APP":
                    return openApp(action.optString("target"));
                case "OPEN_CAMERA":
                    startActivity(new Intent("android.media.action.IMAGE_CAPTURE"));
                    return actionResult(true, "Camera opened. Android keeps capture under your control.");
                case "OPEN_GALLERY":
                    startActivity(new Intent("android.intent.action.PICK", MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
                    return actionResult(true, "Gallery opened.");
                case "OPEN_SETTINGS":
                    return openSettings("android.settings.SETTINGS", "Android settings opened.");
                case "OPEN_WIFI_SETTINGS":
                    return openSettings("android.settings.WIFI_SETTINGS", "Wi-Fi settings opened. Android requires you to confirm changes.");
                case "OPEN_BLUETOOTH_SETTINGS":
                    return openSettings("android.settings.BLUETOOTH_SETTINGS", "Bluetooth settings opened. Android requires you to confirm changes.");
                case "OPEN_BATTERY_SETTINGS":
                    return openSettings("android.settings.BATTERY_SAVER_SETTINGS", "Battery settings opened.");
                case "OPEN_DISPLAY_SETTINGS":
                    return openSettings("android.settings.DISPLAY_SETTINGS", "Display settings opened.");
                case "OPEN_STORAGE_SETTINGS":
                    return openSettings("android.settings.INTERNAL_STORAGE_SETTINGS", "Storage settings opened.");
                case "OPEN_HOTSPOT_SETTINGS":
                    return openSettings("android.settings.WIRELESS_SETTINGS", "Wireless and hotspot settings opened.");
                case "OPEN_ACCESSIBILITY_SETTINGS":
                    return openSettings("android.settings.ACCESSIBILITY_SETTINGS", "Accessibility settings opened. MYRAA cannot enable access for itself.");
                case "OPEN_NOTIFICATION_SETTINGS":
                    return openSettings("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS", "Notification Access opened. Enable MYRAA only if you want notification commands.");
                case "OPEN_DND_SETTINGS":
                    return openSettings("android.settings.NOTIFICATION_POLICY_ACCESS_SETTINGS", "Do Not Disturb access settings opened.");
                case "CHANGE_BRIGHTNESS":
                    return changeBrightness(action.optInt("volume", action.optInt("value", 50)));
                case "CLOSE_MYRAAA":
                    getActivity().moveTaskToBack(true);
                    return actionResult(true, "MYRAA moved to the background.");
                case "OPEN_MAPS":
                    return openUri("geo:0,0?q=" + Uri.encode(first(action, "destination", "query", "target")), null, "Maps opened.");
                case "START_NAVIGATION":
                    return openNavigation(first(action, "destination", "query", "target"));
                case "START_TIMER":
                    return startTimer(action.optInt("durationSeconds", 0), action.optString("title", "MYRAA timer"));
                case "CREATE_ALARM":
                    return createAlarm(action);
                case "CREATE_REMINDER":
                case "CREATE_CALENDAR_EVENT":
                    return createCalendar(action);
                case "CALL_CONTACT":
                    return prepareCall(action.optString("target"));
                case "PREPARE_SMS":
                case "SEND_SMS":
                    return prepareSms(action.optString("target"), action.optString("message"));
                case "PREPARE_WHATSAPP_MESSAGE":
                case "SEND_WHATSAPP_MESSAGE":
                    return prepareWhatsApp(action.optString("target"), action.optString("message"));
                case "WEB_SEARCH":
                    return webSearch(first(action, "query", "target"));
                case "YOUTUBE_SEARCH":
                    return openUri("https://www.youtube.com/results?search_query=" + Uri.encode(first(action, "query", "target")), "com.google.android.youtube", "YouTube search opened.");
                case "MEDIA_PLAY":
                    return mediaKey(WebSocketProtocol.PAYLOAD_SHORT, "Play command sent to the active media session.");
                case "MEDIA_PAUSE":
                    return mediaKey(127, "Pause command sent to the active media session.");
                case "MEDIA_NEXT":
                    return mediaKey(87, "Next command sent to the active media session.");
                case "MEDIA_PREVIOUS":
                    return mediaKey(88, "Previous command sent to the active media session.");
                case "CHANGE_MEDIA_VOLUME":
                    return changeVolume(action.optInt("volume", 50));
                case "FLASHLIGHT_ON":
                    return setTorch(true);
                case "FLASHLIGHT_OFF":
                    return setTorch(false);
                case "READ_NOTIFICATION":
                    return readNotifications();
                case "START_BACKGROUND_MODE":
                    Intent service = new Intent(getContext(), (Class<?>) MyraaBackgroundService.class).setAction(MyraaBackgroundService.ACTION_RESUME);
                    ContextCompat.startForegroundService(getContext(), service);
                    return actionResult(true, "Background Voice Mode started with a visible notification.");
                case "STOP_BACKGROUND_MODE":
                    getContext().startService(new Intent(getContext(), (Class<?>) MyraaBackgroundService.class).setAction(MyraaBackgroundService.ACTION_STOP));
                    return actionResult(true, "Background Voice Mode stopped.");
                case "CANCEL_PENDING_ACTION":
                    this.confirmations.clear();
                    return actionResult(true, "Pending Android actions cancelled.");
                case "CONTROL_3D_CAMERA":
                case "OPEN_MYRAAA_SETTINGS":
                case "OPEN_API_SETUP":
                case "STOP_SPEAKING":
                    return actionResult(true, "MYRAA interface action acknowledged.");
                default:
                    return actionResult(false, "This action is not implemented on this Android build.");
            }
        } catch (ActivityNotFoundException e) {
            return actionResult(false, "No compatible application is installed for this action.");
        } catch (SecurityException e2) {
            return actionResult(false, "Android permission is unavailable for this action.");
        } catch (Exception e3) {
            return actionResult(false, "Android could not complete this action.");
        }
    }

    private JSObject openApp(String query) {
        List<AppEntry> apps = installedApps();
        final String strNormalize = normalize(query);
        List<AppEntry> matches = new ArrayList<>();
        for (AppEntry app : apps) {
            String strNormalize2 = normalize(app.label);
            if (app.packageName.equals(query) || strNormalize2.equals(strNormalize) || strNormalize2.contains(strNormalize) || strNormalize.contains(strNormalize2)) {
                matches.add(app);
            }
        }
        if (matches.isEmpty()) {
            apps.sort(Comparator.comparingInt(new ToIntFunction() { // from class: ai.myraa.mobile.MyraaNativePlugin$$ExternalSyntheticLambda0
                @Override // java.util.function.ToIntFunction
                public final int applyAsInt(Object obj) {
                    return MyraaNativePlugin.levenshtein(strNormalize, MyraaNativePlugin.normalize(((MyraaNativePlugin.AppEntry) obj).label));
                }
            }));
            if (!apps.isEmpty() && levenshtein(strNormalize, normalize(apps.get(0).label)) <= Math.max(2, strNormalize.length() / 3)) {
                matches.add(apps.get(0));
            }
        }
        if (matches.isEmpty()) {
            return actionResult(false, "I couldn't find " + query + " on this phone.");
        }
        if (matches.size() > 1) {
            StringBuilder names = new StringBuilder();
            for (int i = 0; i < Math.min(4, matches.size()); i++) {
                if (i > 0) {
                    names.append(", ");
                }
                names.append(matches.get(i).label);
            }
            return actionResult(false, "I found more than one matching app: " + ((Object) names) + ". Which one should I open?");
        }
        AppEntry match = matches.get(0);
        Intent launch = getContext().getPackageManager().getLaunchIntentForPackage(match.packageName);
        if (launch == null) {
            return actionResult(false, match.label + " could not be launched.");
        }
        startActivity(launch);
        return actionResult(true, match.label + " opened.");
    }

    private List<AppEntry> installedApps() {
        List<ResolveInfo> infos;
        Intent intent = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.LAUNCHER");
        if (Build.VERSION.SDK_INT >= 33) {
            infos = getContext().getPackageManager().queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(131072L));
        } else {
            infos = getContext().getPackageManager().queryIntentActivities(intent, 131072);
        }
        Map<String, AppEntry> unique = new HashMap<>();
        for (ResolveInfo info : infos) {
            String packageName = info.activityInfo.packageName;
            String label = String.valueOf(info.loadLabel(getContext().getPackageManager()));
            unique.put(packageName, new AppEntry(label, packageName));
        }
        List<AppEntry> result = new ArrayList<>(unique.values());
        result.sort(Comparator.comparing(new Function() { // from class: ai.myraa.mobile.MyraaNativePlugin$$ExternalSyntheticLambda2
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((MyraaNativePlugin.AppEntry) obj).label.toLowerCase(Locale.ROOT);
            }
        }));
        return result;
    }

    private List<ContactEntry> findContacts(String query) {
        List<ContactEntry> result = new ArrayList<>();
        String needle = normalize(query);
        String[] projection = {"display_name", "data1"};
        Cursor cursor = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null, "display_name ASC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                try {
                    String name = cursor.getString(0);
                    String phone = cursor.getString(1);
                    if (name != null && phone != null && (needle.isEmpty() || normalize(name).contains(needle) || needle.contains(normalize(name)))) {
                        result.add(new ContactEntry(name, phone));
                    }
                } catch (Throwable th) {
                    if (cursor == null) {
                        throw th;
                    }
                    try {
                        cursor.close();
                        throw th;
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                        throw th;
                    }
                }
            }
            if (cursor != null) {
                cursor.close();
            }
            return result;
        }
        if (cursor != null) {
            cursor.close();
        }
        return result;
    }

    private ContactEntry uniqueContact(String target, JSObject failureHolder) {
        List<ContactEntry> matches = findContacts(target);
        Map<String, ContactEntry> unique = new HashMap<>();
        for (ContactEntry item : matches) {
            unique.put(item.name + item.phone, item);
        }
        List<ContactEntry> matches2 = new ArrayList<>(unique.values());
        if (matches2.isEmpty()) {
            failureHolder.put("message", "I couldn't find a contact named " + target + ".");
            return null;
        }
        if (matches2.size() <= 1) {
            return matches2.get(0);
        }
        StringBuilder names = new StringBuilder();
        for (int i = 0; i < Math.min(5, matches2.size()); i++) {
            if (i > 0) {
                names.append(", ");
            }
            names.append(matches2.get(i).name);
        }
        failureHolder.put("message", "I found more than one contact: " + ((Object) names) + ". Which one should I use?");
        return null;
    }

    private JSObject prepareCall(String target) {
        JSObject failure = actionResult(false, "Contact not found.");
        ContactEntry contact = uniqueContact(target, failure);
        if (contact == null) {
            return failure;
        }
        startActivity(new Intent("android.intent.action.DIAL", Uri.parse("tel:" + Uri.encode(contact.phone))));
        return userTapResult("Opened " + contact.name + " in the dialer. Android requires the final Call tap.");
    }

    private JSObject prepareSms(String target, String message) {
        JSObject failure = actionResult(false, "Contact not found.");
        ContactEntry contact = uniqueContact(target, failure);
        if (contact == null) {
            return failure;
        }
        Intent intent = new Intent("android.intent.action.SENDTO", Uri.parse("smsto:" + Uri.encode(contact.phone))).putExtra("sms_body", message);
        startActivity(intent);
        return userTapResult("Prepared an SMS to " + contact.name + ". Review it and tap Send.");
    }

    private JSObject prepareWhatsApp(String target, String message) {
        JSObject failure = actionResult(false, "Contact not found.");
        ContactEntry contact = uniqueContact(target, failure);
        if (contact == null) {
            return failure;
        }
        String number = contact.phone.replaceAll("[^0-9]", "");
        String packageName = "com.whatsapp";
        if (!packageInstalled("com.whatsapp")) {
            packageName = "com.whatsapp.w4b";
            if (!packageInstalled("com.whatsapp.w4b")) {
                packageName = null;
            }
        }
        return packageName == null ? actionResult(false, "WhatsApp is not installed.") : openUri("https://wa.me/" + number + "?text=" + Uri.encode(message), packageName, "Prepared a WhatsApp message to " + contact.name + ". Review it and tap Send.", true);
    }

    private JSObject startTimer(int seconds, String title) {
        if (seconds <= 0) {
            return actionResult(false, "I couldn't understand the timer duration.");
        }
        Intent intent = new Intent("android.intent.action.SET_TIMER").putExtra("android.intent.extra.alarm.LENGTH", seconds).putExtra("android.intent.extra.alarm.MESSAGE", title).putExtra("android.intent.extra.alarm.SKIP_UI", false);
        startActivity(intent);
        return actionResult(true, "Timer prepared for " + seconds + " seconds.");
    }

    private JSObject createAlarm(JSObject action) {
        long timestamp = action.optLong("timestamp", 0L);
        if (timestamp <= 0) {
            return actionResult(false, "I couldn't understand the alarm time. Please include AM or PM.");
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        Intent intent = new Intent("android.intent.action.SET_ALARM").putExtra("android.intent.extra.alarm.HOUR", calendar.get(11)).putExtra("android.intent.extra.alarm.MINUTES", calendar.get(12)).putExtra("android.intent.extra.alarm.MESSAGE", action.optString("title", "MYRAA alarm"));
        startActivity(intent);
        return actionResult(true, "Alarm prepared for " + calendar.getTime() + ".");
    }

    private JSObject createCalendar(JSObject action) {
        long timestamp = action.optLong("timestamp", 0L);
        if (timestamp <= 0) {
            return actionResult(false, "A clear date and time are required.");
        }
        Intent intent = new Intent("android.intent.action.INSERT").setData(CalendarContract.Events.CONTENT_URI).putExtra("title", action.optString("title", action.optString("message", "MYRAA reminder"))).putExtra("beginTime", timestamp);
        startActivity(intent);
        return userTapResult("Calendar event prepared. Review the details and save it in your calendar app.");
    }

    private JSObject openNavigation(String destination) {
        if (destination.trim().isEmpty()) {
            return actionResult(false, "A destination is required.");
        }
        try {
            return openUri("google.navigation:q=" + Uri.encode(destination), "com.google.android.apps.maps", "Navigation opened to " + destination + ".");
        } catch (Exception e) {
            return openUri("geo:0,0?q=" + Uri.encode(destination), null, "Maps opened to " + destination + ".");
        }
    }

    private JSObject webSearch(String query) {
        if (query.trim().isEmpty()) {
            return actionResult(false, "A search query is required.");
        }
        Intent intent = new Intent("android.intent.action.WEB_SEARCH").putExtra("query", query);
        startActivity(intent);
        return actionResult(true, "Web search opened for " + query + ".");
    }

    private JSObject mediaKey(int code, String message) {
        AudioManager audio = (AudioManager) getContext().getSystemService("audio");
        audio.dispatchMediaKeyEvent(new KeyEvent(0, code));
        audio.dispatchMediaKeyEvent(new KeyEvent(1, code));
        return actionResult(true, message);
    }

    private JSObject changeVolume(int percent) {
        AudioManager audio = (AudioManager) getContext().getSystemService("audio");
        int max = audio.getStreamMaxVolume(3);
        int target = Math.max(0, Math.min(max, Math.round((Math.max(0, Math.min(100, percent)) * max) / 100.0f)));
        audio.setStreamVolume(3, target, 1);
        return actionResult(true, "Media volume set to " + percent + " percent.");
    }

    private JSObject changeBrightness(int percent) {
        if (!Settings.System.canWrite(getContext())) {
            Intent intent = new Intent("android.settings.action.MANAGE_WRITE_SETTINGS", Uri.parse("package:" + getContext().getPackageName()));
            startActivity(intent);
            return userTapResult("Android requires permission before MYRAA can change brightness. The exact settings page is open.");
        }
        int safePercent = Math.max(1, Math.min(100, percent));
        Settings.System.putInt(getContext().getContentResolver(), "screen_brightness_mode", 0);
        Settings.System.putInt(getContext().getContentResolver(), "screen_brightness", Math.round((safePercent * 255) / 100.0f));
        return actionResult(true, "Screen brightness set to " + safePercent + " percent.");
    }

    /* JADX WARN: Code duplicated, block: B:10:0x0045  */
    private JSObject setTorch(boolean enabled) throws Exception {
        CameraManager manager = (CameraManager) getContext().getSystemService("camera");
        String selected = null;
        for (String id : manager.getCameraIdList()) {
            Boolean flash = (Boolean) manager.getCameraCharacteristics(id).get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            Integer facing = (Integer) manager.getCameraCharacteristics(id).get(CameraCharacteristics.LENS_FACING);
            if (Boolean.TRUE.equals(flash)) {
                if (selected != null) {
                    Integer num = 1;
                    if (num.equals(facing)) {
                        selected = id;
                    }
                } else {
                    selected = id;
                }
            }
        }
        if (selected == null) {
            return actionResult(false, "This phone does not report an available flashlight.");
        }
        manager.setTorchMode(selected, enabled);
        return actionResult(true, enabled ? "Flashlight turned on." : "Flashlight turned off.");
    }

    private JSObject readNotifications() {
        List<String> recent = MyraaNotificationService.recent(5);
        if (recent.isEmpty()) {
            startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
            return userTapResult("No readable notifications are available. Notification Access is open if you want to enable it for MYRAA. OTPs remain suppressed.");
        }
        JSObject result = actionResult(true, "Here are the latest safe notifications.");
        JSArray items = new JSArray();
        for (String item : recent) {
            items.put(item);
        }
        result.put("data", (Object) items);
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleGeminiMessage(String text) {
        JSONArray parts;
        JSONArray parts2;
        try {
            JSONObject root = new JSONObject(text);
            if (root.has("setupComplete") || root.has(PREF_SETUP)) {
                cancelGeminiSetupTimeout();
                this.geminiReady = true;
                emitStatus("connected");
            }
            JSONObject content = objectEither(root, "serverContent", "server_content");
            if (content != null) {
                if (content.optBoolean("interrupted")) {
                    emitSimple("interrupted");
                }
                if (content.optBoolean("turnComplete") || content.optBoolean("turn_complete")) {
                    emitSimple("turnComplete");
                }
                JSONObject modelTurn = objectEither(content, "modelTurn", "model_turn");
                if (modelTurn != null && (parts = modelTurn.optJSONArray("parts")) != null) {
                    int i = 0;
                    while (true) {
                        JSONObject modelTurn2 = modelTurn;
                        if (i >= parts.length()) {
                            break;
                        }
                        JSONObject part = parts.optJSONObject(i);
                        if (part == null) {
                            parts2 = parts;
                            i = i;
                        } else {
                            parts2 = parts;
                            JSONObject inline = objectEither(part, "inlineData", "inline_data");
                            if (inline != null && inline.has("data")) {
                                JSObject event = new JSObject();
                                event.put("type", "audio");
                                event.put("audio", inline.optString("data"));
                                notifyListeners("geminiMessage", event);
                            }
                            if (part.has("text")) {
                                emitTranscription("model", part.optString("text"));
                            }
                        }
                        i++;
                        modelTurn = modelTurn2;
                        parts = parts2;
                    }
                }
                JSONObject output = objectEither(content, "outputTranscription", "output_transcription");
                if (output != null && output.has("text")) {
                    emitTranscription("model", output.optString("text"));
                }
                JSONObject input = objectEither(content, "inputTranscription", "input_transcription");
                if (input != null && input.has("text")) {
                    emitTranscription("user", input.optString("text"));
                }
            }
            JSONObject toolCall = objectEither(root, "toolCall", "tool_call");
            if (toolCall != null) {
                JSONArray calls = toolCall.optJSONArray("functionCalls");
                if (calls == null) {
                    calls = toolCall.optJSONArray("function_calls");
                }
                if (calls != null) {
                    for (int i2 = 0; i2 < calls.length(); i2++) {
                        JSONObject function = calls.optJSONObject(i2);
                        if (function != null) {
                            JSObject event2 = new JSObject();
                            event2.put("type", "toolCall");
                            event2.put("callId", function.optString("id"));
                            event2.put("name", function.optString("name"));
                            event2.put("args", (Object) (function.optJSONObject("args") == null ? new JSONObject() : function.optJSONObject("args")));
                            notifyListeners("geminiMessage", event2);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            emitError("Gemini returned an unreadable live event.", "PROTOCOL");
        }
    }

    private void sendRealtime(String mimeType, String base64) {
        if (this.geminiSocket == null || base64 == null || base64.isEmpty()) {
            return;
        }
        try {
            JSONObject chunk = new JSONObject().put("mimeType", mimeType).put("data", base64);
            String field = mimeType.startsWith("audio/") ? "audio" : "video";
            this.geminiSocket.send(new JSONObject().put("realtimeInput", new JSONObject().put(field, chunk)).toString());
        } catch (JSONException e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancelGeminiSetupTimeout() {
        if (this.geminiSetupTimeout != null) {
            this.mainHandler.removeCallbacks(this.geminiSetupTimeout);
        }
        this.geminiSetupTimeout = null;
    }

    private void stopGeminiInternal() {
        cancelGeminiSetupTimeout();
        this.geminiReady = false;
        if (this.geminiSocket != null) {
            this.geminiSocket.close(1000, "MYRAA session closed");
            this.geminiSocket = null;
        }
    }

    private void emitStatus(String status) {
        JSObject event = new JSObject();
        event.put("type", NotificationCompat.CATEGORY_STATUS);
        event.put(NotificationCompat.CATEGORY_STATUS, status);
        notifyListeners("geminiMessage", event);
    }

    private void emitSimple(String type) {
        JSObject event = new JSObject();
        event.put("type", type);
        notifyListeners("geminiMessage", event);
    }

    private void emitTranscription(String role, String text) {
        if (text == null || text.isEmpty()) {
            return;
        }
        JSObject event = new JSObject();
        event.put("type", "transcription");
        event.put("role", role);
        event.put("text", text);
        notifyListeners("geminiMessage", event);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void emitError(String message, String code) {
        JSObject event = new JSObject();
        event.put("type", "error");
        event.put("error", message);
        event.put("code", code);
        notifyListeners("geminiMessage", event, true);
    }

    private JSObject apiStatus() {
        SharedPreferences prefs = encryptedPrefs();
        String encrypted = prefs.getString(PREF_ENCRYPTED_KEY, "");
        JSObject result = new JSObject();
        result.put("configured", !encrypted.isEmpty());
        result.put("validated", prefs.getBoolean(PREF_VALIDATED, false));
        result.put("setupComplete", prefs.getBoolean(PREF_SETUP, false));
        if (!encrypted.isEmpty()) {
            try {
                result.put("maskedKey", mask(decrypt(encrypted)));
            } catch (Exception e) {
                result.put("maskedKey", "Saved securely");
            }
        }
        return result;
    }

    private SharedPreferences encryptedPrefs() {
        return getContext().getSharedPreferences(PREFS, 0);
    }

    private String googleApiError(Response response, String candidate) {
        String base;
        String detail = "";
        try {
            String body = response.body() == null ? "" : response.body().string();
            JSONObject error = new JSONObject(body).optJSONObject("error");
            if (error != null) {
                detail = error.optString("message", "").replace(candidate, "[key hidden]").replaceAll("\\s+", " ").trim();
            }
        } catch (Exception e) {
        }
        if (response.code() == 400 || response.code() == 401) {
            base = "Google rejected this API key. Copy a current Gemini key from Google AI Studio.";
        } else if (response.code() == 403) {
            base = "Google rejected this key or its API restrictions. Enable the Generative Language API and check key restrictions.";
        } else {
            base = response.code() == 429 ? "The Gemini quota or request limit has been reached." : "Google Gemini returned HTTP " + response.code() + ".";
        }
        if (detail.isEmpty()) {
            return base;
        }
        return base + " " + (detail.length() > 220 ? detail.substring(0, 220) + "..." : detail);
    }

    private String encryptWithRecovery(String value) throws Exception {
        try {
            return encrypt(value);
        } catch (Exception e) {
            KeyStore store = KeyStore.getInstance("AndroidKeyStore");
            store.load(null);
            if (store.containsAlias(KEY_ALIAS)) {
                store.deleteEntry(KEY_ALIAS);
            }
            return encrypt(value);
        }
    }

    private SecretKey keystoreKey() throws Exception {
        KeyStore store = KeyStore.getInstance("AndroidKeyStore");
        store.load(null);
        if (!store.containsAlias(KEY_ALIAS)) {
            KeyGenerator generator = KeyGenerator.getInstance("AES", "AndroidKeyStore");
            generator.init(new KeyGenParameterSpec.Builder(KEY_ALIAS, 3).setBlockModes("GCM").setEncryptionPaddings("NoPadding").setKeySize(256).build());
            return generator.generateKey();
        }
        return (SecretKey) store.getKey(KEY_ALIAS, null);
    }

    private String encrypt(String value) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(1, keystoreKey());
        byte[] encrypted = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
        byte[] iv = cipher.getIV();
        byte[] joined = new byte[iv.length + 1 + encrypted.length];
        joined[0] = (byte) iv.length;
        System.arraycopy(iv, 0, joined, 1, iv.length);
        System.arraycopy(encrypted, 0, joined, iv.length + 1, encrypted.length);
        return Base64.encodeToString(joined, 2);
    }

    private String decrypt(String encoded) throws Exception {
        byte[] joined = Base64.decode(encoded, 2);
        int ivLength = joined[0] & UByte.MAX_VALUE;
        byte[] iv = Arrays.copyOfRange(joined, 1, ivLength + 1);
        byte[] encrypted = Arrays.copyOfRange(joined, ivLength + 1, joined.length);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(2, keystoreKey(), new GCMParameterSpec(128, iv));
        return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
    }

    private String decryptSavedKey() throws Exception {
        String encoded = encryptedPrefs().getString(PREF_ENCRYPTED_KEY, "");
        return encoded.isEmpty() ? "" : decrypt(encoded);
    }

    private boolean validConfirmation(JSObject action) {
        if (!action.optBoolean("confirmed", false)) {
            return false;
        }
        String token = action.optString("confirmationToken", "");
        PendingConfirmation pending = this.confirmations.remove(token);
        return pending != null && pending.expiresAt >= System.currentTimeMillis() && pending.action.equals(action.optString("action")) && pending.fingerprint.equals(fingerprint(action));
    }

    static /* synthetic */ boolean lambda$purgeConfirmations$4(long now, Map.Entry entry) {
        return ((PendingConfirmation) entry.getValue()).expiresAt < now;
    }

    private void purgeConfirmations() {
        final long now = System.currentTimeMillis();
        this.confirmations.entrySet().removeIf(new Predicate() { // from class: ai.myraa.mobile.MyraaNativePlugin$$ExternalSyntheticLambda3
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return MyraaNativePlugin.lambda$purgeConfirmations$4(now, (Map.Entry) obj);
            }
        });
    }

    private String fingerprint(JSObject action) {
        return action.optString("action") + "|" + action.optString("target") + "|" + action.optString("message") + "|" + action.optString("title") + "|" + action.optLong("timestamp") + "|" + action.optInt("durationSeconds");
    }

    private boolean needsContacts(String name) {
        return "CALL_CONTACT".equals(name) || "PREPARE_SMS".equals(name) || "SEND_SMS".equals(name) || "PREPARE_WHATSAPP_MESSAGE".equals(name) || "SEND_WHATSAPP_MESSAGE".equals(name);
    }

    private boolean packageInstalled(String packageName) {
        try {
            getContext().getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void startActivity(Intent intent) {
        intent.addFlags(268435456);
        getContext().startActivity(intent);
    }

    private JSObject openSettings(String action, String message) {
        startActivity(new Intent(action));
        return actionResult(true, message);
    }

    private JSObject openUri(String uri, String packageName, String message) {
        return openUri(uri, packageName, message, false);
    }

    private JSObject openUri(String uri, String packageName, String message, boolean userTap) {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(uri));
        if (packageName != null) {
            intent.setPackage(packageName);
        }
        startActivity(intent);
        return userTap ? userTapResult(message) : actionResult(true, message);
    }

    private JSObject actionResult(boolean ok, String message) {
        JSObject result = new JSObject();
        result.put("ok", ok);
        result.put("message", message);
        return result;
    }

    private JSObject userTapResult(String message) {
        JSObject result = actionResult(true, message);
        result.put("requiresUserTap", true);
        return result;
    }

    private String first(JSObject object, String... keys) {
        for (String key : keys) {
            String value = object.optString(key, "").trim();
            if (!value.isEmpty()) {
                return value;
            }
        }
        return "";
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }

    private static String mask(String key) {
        return key.length() < 8 ? "••••••••" : key.substring(0, Math.min(4, key.length())) + "••••••••••••" + key.substring(key.length() - 3);
    }

    private static String normalize(String input) {
        String value = Normalizer.normalize(safe(input), Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
        return value;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int levenshtein(String a, String b) {
        int[] prev = new int[b.length() + 1];
        for (int j = 0; j <= b.length(); j++) {
            prev[j] = j;
        }
        for (int i = 1; i <= a.length(); i++) {
            int[] cur = new int[b.length() + 1];
            cur[0] = i;
            for (int j2 = 1; j2 <= b.length(); j2++) {
                cur[j2] = Math.min(Math.min(cur[j2 - 1] + 1, prev[j2] + 1), prev[j2 - 1] + (a.charAt(i + (-1)) == b.charAt(j2 + (-1)) ? 0 : 1));
            }
            prev = cur;
        }
        return prev[b.length()];
    }

    private static JSONObject objectEither(JSONObject parent, String camel, String snake) {
        JSONObject value = parent.optJSONObject(camel);
        return value == null ? parent.optJSONObject(snake) : value;
    }

    @Override // com.getcapacitor.Plugin
    protected void handleOnDestroy() {
        stopGeminiInternal();
        if (activePlugin.get() == this) {
            activePlugin.clear();
        }
        super.handleOnDestroy();
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class AppEntry {
        final String label;
        final String packageName;

        AppEntry(String label, String packageName) {
            this.label = label;
            this.packageName = packageName;
        }
    }

    private static class ContactEntry {
        final String name;
        final String phone;

        ContactEntry(String name, String phone) {
            this.name = name;
            this.phone = phone;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class PendingConfirmation {
        final String action;
        final long expiresAt;
        final String fingerprint;

        PendingConfirmation(String action, String fingerprint, long expiresAt) {
            this.action = action;
            this.fingerprint = fingerprint;
            this.expiresAt = expiresAt;
        }
    }
}
