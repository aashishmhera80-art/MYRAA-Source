package org.chromium.support_lib_boundary;

import org.jspecify.annotations.NullMarked;

/* JADX INFO: loaded from: classes.dex */
@NullMarked
public interface SafeBrowsingResponseBoundaryInterface {
    void backToSafety(boolean z);

    void proceed(boolean z);

    void showInterstitial(boolean z);
}
