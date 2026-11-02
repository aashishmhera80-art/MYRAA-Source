package androidx.webkit.internal;

import androidx.webkit.NoVarySearchHeader;
import androidx.webkit.SpeculativeLoadingParameters;
import java.lang.reflect.InvocationHandler;
import java.util.HashMap;
import java.util.Map;
import org.chromium.support_lib_boundary.SpeculativeLoadingParametersBoundaryInterface;
import org.chromium.support_lib_boundary.util.BoundaryInterfaceReflectionUtil;

/* JADX INFO: loaded from: classes.dex */
public class SpeculativeLoadingParametersAdapter implements SpeculativeLoadingParametersBoundaryInterface {
    private final SpeculativeLoadingParameters mSpeculativeLoadingParameters;

    public SpeculativeLoadingParametersAdapter(SpeculativeLoadingParameters impl) {
        this.mSpeculativeLoadingParameters = impl;
    }

    @Override // org.chromium.support_lib_boundary.SpeculativeLoadingParametersBoundaryInterface
    public Map<String, String> getAdditionalHeaders() {
        return this.mSpeculativeLoadingParameters == null ? new HashMap() : this.mSpeculativeLoadingParameters.getAdditionalHeaders();
    }

    @Override // org.chromium.support_lib_boundary.SpeculativeLoadingParametersBoundaryInterface
    public InvocationHandler getNoVarySearchData() {
        NoVarySearchHeader noVarySearchHeader;
        if (this.mSpeculativeLoadingParameters == null || (noVarySearchHeader = this.mSpeculativeLoadingParameters.getExpectedNoVarySearchData()) == null) {
            return null;
        }
        return BoundaryInterfaceReflectionUtil.createInvocationHandlerFor(new NoVarySearchHeaderAdapter(noVarySearchHeader));
    }

    @Override // org.chromium.support_lib_boundary.SpeculativeLoadingParametersBoundaryInterface
    public boolean isJavaScriptEnabled() {
        if (this.mSpeculativeLoadingParameters == null) {
            return false;
        }
        return this.mSpeculativeLoadingParameters.isJavaScriptEnabled();
    }
}
