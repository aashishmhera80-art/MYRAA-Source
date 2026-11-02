package androidx.webkit.internal;

import androidx.webkit.TracingConfig;
import androidx.webkit.TracingController;
import java.io.OutputStream;
import java.util.concurrent.Executor;
import org.chromium.support_lib_boundary.TracingControllerBoundaryInterface;

/* JADX INFO: loaded from: classes.dex */
public class TracingControllerImpl extends TracingController {
    private TracingControllerBoundaryInterface mBoundaryInterface;
    private android.webkit.TracingController mFrameworksImpl;

    public TracingControllerImpl() {
        ApiFeature.P feature = WebViewFeatureInternal.TRACING_CONTROLLER_BASIC_USAGE;
        if (feature.isSupportedByFramework()) {
            this.mFrameworksImpl = ApiHelperForP.getTracingControllerInstance();
            this.mBoundaryInterface = null;
        } else {
            if (feature.isSupportedByWebView()) {
                this.mFrameworksImpl = null;
                this.mBoundaryInterface = WebViewGlueCommunicator.getFactory().getTracingController();
                return;
            }
            throw WebViewFeatureInternal.getUnsupportedOperationException();
        }
    }

    private android.webkit.TracingController getFrameworksImpl() {
        if (this.mFrameworksImpl == null) {
            this.mFrameworksImpl = ApiHelperForP.getTracingControllerInstance();
        }
        return this.mFrameworksImpl;
    }

    private TracingControllerBoundaryInterface getBoundaryInterface() {
        if (this.mBoundaryInterface == null) {
            this.mBoundaryInterface = WebViewGlueCommunicator.getFactory().getTracingController();
        }
        return this.mBoundaryInterface;
    }

    @Override // androidx.webkit.TracingController
    public boolean isTracing() {
        ApiFeature.P feature = WebViewFeatureInternal.TRACING_CONTROLLER_BASIC_USAGE;
        if (feature.isSupportedByFramework()) {
            return ApiHelperForP.isTracing(getFrameworksImpl());
        }
        if (feature.isSupportedByWebView()) {
            return getBoundaryInterface().isTracing();
        }
        throw WebViewFeatureInternal.getUnsupportedOperationException();
    }

    @Override // androidx.webkit.TracingController
    public void start(TracingConfig tracingConfig) {
        if (tracingConfig == null) {
            throw new IllegalArgumentException("Tracing config must be non null");
        }
        ApiFeature.P feature = WebViewFeatureInternal.TRACING_CONTROLLER_BASIC_USAGE;
        if (feature.isSupportedByFramework()) {
            ApiHelperForP.start(getFrameworksImpl(), tracingConfig);
        } else {
            if (feature.isSupportedByWebView()) {
                getBoundaryInterface().start(tracingConfig.getPredefinedCategories(), tracingConfig.getCustomIncludedCategories(), tracingConfig.getTracingMode());
                return;
            }
            throw WebViewFeatureInternal.getUnsupportedOperationException();
        }
    }

    @Override // androidx.webkit.TracingController
    public boolean stop(OutputStream outputStream, Executor executor) {
        ApiFeature.P feature = WebViewFeatureInternal.TRACING_CONTROLLER_BASIC_USAGE;
        if (feature.isSupportedByFramework()) {
            return ApiHelperForP.stop(getFrameworksImpl(), outputStream, executor);
        }
        if (feature.isSupportedByWebView()) {
            return getBoundaryInterface().stop(outputStream, executor);
        }
        throw WebViewFeatureInternal.getUnsupportedOperationException();
    }
}
