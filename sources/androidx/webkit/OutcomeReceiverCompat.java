package androidx.webkit;

import java.lang.Throwable;

/* JADX INFO: loaded from: classes.dex */
public interface OutcomeReceiverCompat<T, E extends Throwable> {
    void onResult(T t);

    default void onError(E error) {
    }
}
