package org.chromium.support_lib_boundary;

import java.util.concurrent.Executor;
import org.jspecify.annotations.NullMarked;

/* JADX INFO: loaded from: classes.dex */
@NullMarked
public interface ProxyControllerBoundaryInterface {
    void clearProxyOverride(Runnable runnable, Executor executor);

    void setProxyOverride(String[][] strArr, String[] strArr2, Runnable runnable, Executor executor);

    void setProxyOverride(String[][] strArr, String[] strArr2, Runnable runnable, Executor executor, boolean z);
}
