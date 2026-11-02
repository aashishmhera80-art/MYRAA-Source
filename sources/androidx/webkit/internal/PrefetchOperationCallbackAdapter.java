package androidx.webkit.internal;

import androidx.webkit.OutcomeReceiverCompat;
import androidx.webkit.PrefetchException;
import androidx.webkit.PrefetchNetworkException;
import java.lang.reflect.InvocationHandler;
import org.chromium.support_lib_boundary.PrefetchOperationCallbackBoundaryInterface;
import org.chromium.support_lib_boundary.util.BoundaryInterfaceReflectionUtil;

/* JADX INFO: loaded from: classes.dex */
public class PrefetchOperationCallbackAdapter {
    public static InvocationHandler buildInvocationHandler(final OutcomeReceiverCompat<Void, PrefetchException> callback) {
        PrefetchOperationCallbackBoundaryInterface operationCallback = new PrefetchOperationCallbackBoundaryInterface() { // from class: androidx.webkit.internal.PrefetchOperationCallbackAdapter.1
            @Override // org.chromium.support_lib_boundary.PrefetchOperationCallbackBoundaryInterface
            public void onSuccess() {
                callback.onResult(null);
            }

            @Override // org.chromium.support_lib_boundary.PrefetchOperationCallbackBoundaryInterface
            public void onFailure(int type, String message, int networkErrorCode) {
                if (type == 1) {
                    callback.onError(new PrefetchNetworkException(message, networkErrorCode));
                } else {
                    callback.onError(new PrefetchException(message));
                }
            }
        };
        return BoundaryInterfaceReflectionUtil.createInvocationHandlerFor(operationCallback);
    }
}
