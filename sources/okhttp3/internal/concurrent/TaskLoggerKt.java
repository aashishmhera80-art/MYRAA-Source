package okhttp3.internal.concurrent;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.StringCompanionObject;
import kotlin.time.DurationKt;
import okhttp3.internal.http2.Http2Connection;

/* JADX INFO: compiled from: TaskLogger.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000*\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a\u000e\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003\u001a \u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u0001H\u0002\u001a5\u0010\u000b\u001a\u0002H\f\"\u0004\b\u0000\u0010\f2\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\f\u0010\r\u001a\b\u0012\u0004\u0012\u0002H\f0\u000eH\u0080\bø\u0001\u0000¢\u0006\u0002\u0010\u000f\u001a*\u0010\u0010\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00010\u000eH\u0080\bø\u0001\u0000\u0082\u0002\u0007\n\u0005\b\u009920\u0001¨\u0006\u0012"}, d2 = {"formatDuration", "", "ns", "", "log", "", "task", "Lokhttp3/internal/concurrent/Task;", "queue", "Lokhttp3/internal/concurrent/TaskQueue;", "message", "logElapsed", "T", "block", "Lkotlin/Function0;", "(Lokhttp3/internal/concurrent/Task;Lokhttp3/internal/concurrent/TaskQueue;Lkotlin/jvm/functions/Function0;)Ljava/lang/Object;", "taskLog", "messageBlock", "okhttp"}, k = 2, mv = {1, 8, 0}, xi = 48)
public final class TaskLoggerKt {
    public static final void taskLog(Task task, TaskQueue queue, Function0<String> messageBlock) {
        Intrinsics.checkNotNullParameter(task, "task");
        Intrinsics.checkNotNullParameter(queue, "queue");
        Intrinsics.checkNotNullParameter(messageBlock, "messageBlock");
        if (TaskRunner.INSTANCE.getLogger().isLoggable(Level.FINE)) {
            log(task, queue, messageBlock.invoke());
        }
    }

    public static final <T> T logElapsed(Task task, TaskQueue queue, Function0<? extends T> block) {
        Intrinsics.checkNotNullParameter(task, "task");
        Intrinsics.checkNotNullParameter(queue, "queue");
        Intrinsics.checkNotNullParameter(block, "block");
        long startNs = -1;
        boolean loggingEnabled = TaskRunner.INSTANCE.getLogger().isLoggable(Level.FINE);
        if (loggingEnabled) {
            startNs = queue.getTaskRunner().getBackend().nanoTime();
            log(task, queue, "starting");
        }
        try {
            T tInvoke = block.invoke();
            if (loggingEnabled) {
                log(task, queue, "finished run in " + formatDuration(queue.getTaskRunner().getBackend().nanoTime() - startNs));
            }
            return tInvoke;
        } catch (Throwable th) {
            if (loggingEnabled) {
                long elapsedNs = queue.getTaskRunner().getBackend().nanoTime() - startNs;
                if (0 != 0) {
                    log(task, queue, "finished run in " + formatDuration(elapsedNs));
                } else {
                    log(task, queue, "failed a run in " + formatDuration(elapsedNs));
                }
            }
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void log(Task task, TaskQueue queue, String message) {
        Logger logger = TaskRunner.INSTANCE.getLogger();
        StringBuilder sbAppend = new StringBuilder().append(queue.getName()).append(' ');
        StringCompanionObject stringCompanionObject = StringCompanionObject.INSTANCE;
        String str = String.format("%-22s", Arrays.copyOf(new Object[]{message}, 1));
        Intrinsics.checkNotNullExpressionValue(str, "format(format, *args)");
        logger.fine(sbAppend.append(str).append(": ").append(task.getName()).toString());
    }

    public static final String formatDuration(long ns) {
        String s;
        if (ns <= -999500000) {
            s = ((ns - ((long) 500000000)) / ((long) Http2Connection.DEGRADED_PONG_TIMEOUT_NS)) + " s ";
        } else if (ns <= -999500) {
            s = ((ns - ((long) 500000)) / ((long) DurationKt.NANOS_IN_MILLIS)) + " ms";
        } else if (ns <= 0) {
            s = ((ns - ((long) 500)) / ((long) 1000)) + " µs";
        } else if (ns < 999500) {
            s = ((((long) 500) + ns) / ((long) 1000)) + " µs";
        } else {
            s = ns < 999500000 ? ((((long) 500000) + ns) / ((long) DurationKt.NANOS_IN_MILLIS)) + " ms" : ((((long) 500000000) + ns) / ((long) Http2Connection.DEGRADED_PONG_TIMEOUT_NS)) + " s ";
        }
        StringCompanionObject stringCompanionObject = StringCompanionObject.INSTANCE;
        String str = String.format("%6s", Arrays.copyOf(new Object[]{s}, 1));
        Intrinsics.checkNotNullExpressionValue(str, "format(format, *args)");
        return str;
    }
}
