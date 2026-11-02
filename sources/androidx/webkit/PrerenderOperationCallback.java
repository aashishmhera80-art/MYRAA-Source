package androidx.webkit;

/* JADX INFO: loaded from: classes.dex */
public interface PrerenderOperationCallback {
    void onError(PrerenderException prerenderException);

    void onPrerenderActivated();
}
