package androidx.webkit.internal;

import org.chromium.support_lib_boundary.WebResourceRequestBoundaryInterface;

/* JADX INFO: loaded from: classes.dex */
public class WebResourceRequestAdapter {
    private final WebResourceRequestBoundaryInterface mBoundaryInterface;

    public WebResourceRequestAdapter(WebResourceRequestBoundaryInterface boundaryInterface) {
        this.mBoundaryInterface = boundaryInterface;
    }

    public boolean isRedirect() {
        return this.mBoundaryInterface.isRedirect();
    }
}
