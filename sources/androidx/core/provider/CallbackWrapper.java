package androidx.core.provider;

import android.graphics.Typeface;
import java.util.concurrent.Executor;

/* JADX INFO: loaded from: classes.dex */
class CallbackWrapper {
    private final FontsContractCompat.FontRequestCallback mCallback;
    private final Executor mExecutor;

    CallbackWrapper(FontsContractCompat.FontRequestCallback callback, Executor executor) {
        this.mCallback = callback;
        this.mExecutor = executor;
    }

    CallbackWrapper(FontsContractCompat.FontRequestCallback callback) {
        this(callback, RequestExecutor.createHandlerExecutor(CalleeHandler.create()));
    }

    private void onTypefaceRetrieved(final Typeface typeface) {
        final FontsContractCompat.FontRequestCallback callback = this.mCallback;
        this.mExecutor.execute(new Runnable() { // from class: androidx.core.provider.CallbackWrapper.1
            @Override // java.lang.Runnable
            public void run() {
                callback.onTypefaceRetrieved(typeface);
            }
        });
    }

    private void onTypefaceRequestFailed(final int reason) {
        final FontsContractCompat.FontRequestCallback callback = this.mCallback;
        this.mExecutor.execute(new Runnable() { // from class: androidx.core.provider.CallbackWrapper.2
            @Override // java.lang.Runnable
            public void run() {
                callback.onTypefaceRequestFailed(reason);
            }
        });
    }

    void onTypefaceResult(FontRequestWorker.TypefaceResult typefaceResult) {
        if (typefaceResult.isSuccess()) {
            onTypefaceRetrieved(typefaceResult.mTypeface);
        } else {
            onTypefaceRequestFailed(typefaceResult.mResult);
        }
    }
}
