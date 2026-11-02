package androidx.webkit;

import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public interface WebViewStartUpResult {
    List<BlockingStartUpLocation> getBlockingStartUpLocations();

    Long getMaxTimePerTaskInUiThreadMillis();

    Long getTotalTimeInUiThreadMillis();
}
