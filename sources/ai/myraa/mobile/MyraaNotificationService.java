package ai.myraa.mobile;

import android.app.Notification;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import androidx.core.app.NotificationCompat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Locale;

/* JADX INFO: loaded from: classes6.dex */
public class MyraaNotificationService extends NotificationListenerService {
    private static final Deque<String> RECENT = new ArrayDeque();

    @Override // android.service.notification.NotificationListenerService
    public void onNotificationPosted(StatusBarNotification sbn) {
        Notification n = sbn.getNotification();
        if (n == null) {
            return;
        }
        String title = String.valueOf(n.extras.getCharSequence(NotificationCompat.EXTRA_TITLE, ""));
        String text = String.valueOf(n.extras.getCharSequence(NotificationCompat.EXTRA_TEXT, ""));
        String combined = (title + ": " + text).trim();
        if (combined.length() < 2 || containsSensitiveCode(combined)) {
            return;
        }
        synchronized (RECENT) {
            RECENT.addFirst(combined.length() > 500 ? combined.substring(0, 500) : combined);
            while (RECENT.size() > 20) {
                RECENT.removeLast();
            }
        }
    }

    public static List<String> recent(int limit) {
        List<String> result;
        synchronized (RECENT) {
            result = new ArrayList<>();
            for (String item : RECENT) {
                if (result.size() >= limit) {
                    break;
                }
                result.add(item);
            }
        }
        return result;
    }

    private static boolean containsSensitiveCode(String value) {
        String s = value.toLowerCase(Locale.ROOT);
        return s.contains("otp") || s.contains("one-time password") || s.contains("verification code") || s.contains("security code") || s.matches(".*\\b\\d{4,8}\\b.*(login|verify|authentication).*?");
    }
}
