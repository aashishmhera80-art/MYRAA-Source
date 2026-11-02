package ai.myraa.mobile;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.Locale;

/* JADX INFO: loaded from: classes6.dex */
public class MyraaBackgroundService extends Service {
    public static final String ACTION_PAUSE = "ai.myraa.mobile.PAUSE";
    public static final String ACTION_RESUME = "ai.myraa.mobile.RESUME";
    public static final String ACTION_STOP = "ai.myraa.mobile.STOP";
    private static final String CHANNEL = "myraa_background_voice";
    private static final int NOTIFICATION_ID = 7301;
    private boolean paused;
    private SpeechRecognizer recognizer;
    private String wakePhrase = "hey myraa";

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        createChannel();
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent == null ? null : intent.getAction();
        if (ACTION_STOP.equals(action)) {
            stopListening();
            stopForeground(1);
            stopSelf();
            return 2;
        }
        if (ACTION_PAUSE.equals(action)) {
            this.paused = true;
            stopListening();
            updateNotification("MYRAA background voice is paused.");
            return 2;
        }
        if (ACTION_RESUME.equals(action)) {
            this.paused = false;
        }
        if (intent != null && intent.getStringExtra("wakePhrase") != null) {
            this.wakePhrase = intent.getStringExtra("wakePhrase").toLowerCase(Locale.ROOT).trim();
        }
        startForeground(NOTIFICATION_ID, buildNotification(getString(R.string.background_notification)));
        if (!this.paused) {
            startListening();
        }
        return 2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startListening() {
        if (this.paused || ContextCompat.checkSelfPermission(this, "android.permission.RECORD_AUDIO") != 0) {
            updateNotification("Microphone permission is required for background voice.");
            return;
        }
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            updateNotification("Speech recognition is unavailable on this phone.");
            return;
        }
        stopListening();
        this.recognizer = SpeechRecognizer.createSpeechRecognizer(this);
        this.recognizer.setRecognitionListener(new RecognitionListener() { // from class: ai.myraa.mobile.MyraaBackgroundService.1
            @Override // android.speech.RecognitionListener
            public void onReadyForSpeech(Bundle params) {
            }

            @Override // android.speech.RecognitionListener
            public void onBeginningOfSpeech() {
            }

            @Override // android.speech.RecognitionListener
            public void onRmsChanged(float rmsdB) {
            }

            @Override // android.speech.RecognitionListener
            public void onBufferReceived(byte[] buffer) {
            }

            @Override // android.speech.RecognitionListener
            public void onEndOfSpeech() {
            }

            @Override // android.speech.RecognitionListener
            public void onError(int error) {
                MyraaBackgroundService.this.restartSoon();
            }

            @Override // android.speech.RecognitionListener
            public void onResults(Bundle results) {
                MyraaBackgroundService.this.inspect(results);
                MyraaBackgroundService.this.restartSoon();
            }

            @Override // android.speech.RecognitionListener
            public void onPartialResults(Bundle partialResults) {
                MyraaBackgroundService.this.inspect(partialResults);
            }

            @Override // android.speech.RecognitionListener
            public void onEvent(int eventType, Bundle params) {
            }
        });
        Intent listen = new Intent("android.speech.action.RECOGNIZE_SPEECH");
        listen.putExtra("android.speech.extra.LANGUAGE_MODEL", "free_form");
        listen.putExtra("android.speech.extra.LANGUAGE", "en-IN");
        listen.putExtra("android.speech.extra.PARTIAL_RESULTS", true);
        listen.putExtra("android.speech.extra.MAX_RESULTS", 5);
        this.recognizer.startListening(listen);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void inspect(Bundle bundle) {
        ArrayList<String> phrases;
        if (bundle == null || (phrases = bundle.getStringArrayList("results_recognition")) == null) {
            return;
        }
        for (String phrase : phrases) {
            if (phrase != null && phrase.toLowerCase(Locale.ROOT).contains(this.wakePhrase)) {
                this.paused = true;
                stopListening();
                updateNotification("MYRAA heard you. Tap Open to continue your command.");
                MyraaNativePlugin.emitBackgroundWake(phrase);
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void restartSoon() {
        if (!this.paused) {
            new Handler(getMainLooper()).postDelayed(new Runnable() { // from class: ai.myraa.mobile.MyraaBackgroundService$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.startListening();
                }
            }, 700L);
        }
    }

    private void stopListening() {
        if (this.recognizer != null) {
            try {
                this.recognizer.cancel();
                this.recognizer.destroy();
            } catch (Exception e) {
            }
            this.recognizer = null;
        }
    }

    private Notification buildNotification(String text) {
        Intent open = new Intent(this, (Class<?>) MainActivity.class).addFlags(536870912);
        PendingIntent openPi = PendingIntent.getActivity(this, 1, open, 201326592);
        PendingIntent pausePi = servicePending(ACTION_PAUSE, 2);
        PendingIntent resumePi = servicePending(ACTION_RESUME, 3);
        PendingIntent stopPi = servicePending(ACTION_STOP, 4);
        return new NotificationCompat.Builder(this, CHANNEL).setSmallIcon(R.drawable.ic_stat_myraa).setContentTitle("MYRAA Background Voice").setContentText(text).setOngoing(true).setOnlyAlertOnce(true).setContentIntent(openPi).addAction(0, "Pause", pausePi).addAction(0, "Resume", resumePi).addAction(0, "Open", openPi).addAction(0, "Stop", stopPi).build();
    }

    private PendingIntent servicePending(String action, int requestCode) {
        Intent intent = new Intent(this, (Class<?>) MyraaBackgroundService.class).setAction(action);
        return PendingIntent.getService(this, requestCode, intent, 201326592);
    }

    private void updateNotification(String text) {
        ((NotificationManager) getSystemService("notification")).notify(NOTIFICATION_ID, buildNotification(text));
    }

    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL, getString(R.string.background_channel), 2);
        channel.setDescription("Visible status and controls for MYRAA's optional wake phrase service.");
        ((NotificationManager) getSystemService("notification")).createNotificationChannel(channel);
    }

    @Override // android.app.Service
    public void onDestroy() {
        stopListening();
        super.onDestroy();
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }
}
