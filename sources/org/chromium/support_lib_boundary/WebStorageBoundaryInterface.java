package org.chromium.support_lib_boundary;

import java.util.concurrent.Executor;
import org.jspecify.annotations.NullMarked;

/* JADX INFO: loaded from: classes.dex */
@NullMarked
public interface WebStorageBoundaryInterface {
    void deleteBrowsingData(Executor executor, Runnable runnable);

    String deleteBrowsingDataForSite(String str, Executor executor, Runnable runnable);
}
