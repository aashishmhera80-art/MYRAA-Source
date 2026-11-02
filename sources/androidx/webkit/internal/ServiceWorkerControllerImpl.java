package androidx.webkit.internal;

import android.webkit.ServiceWorkerController;
import androidx.webkit.ServiceWorkerClientCompat;
import androidx.webkit.ServiceWorkerControllerCompat;
import androidx.webkit.ServiceWorkerWebSettingsCompat;
import org.chromium.support_lib_boundary.ServiceWorkerControllerBoundaryInterface;
import org.chromium.support_lib_boundary.util.BoundaryInterfaceReflectionUtil;

/* JADX INFO: loaded from: classes.dex */
public class ServiceWorkerControllerImpl extends ServiceWorkerControllerCompat {
    private ServiceWorkerControllerBoundaryInterface mBoundaryInterface;
    private ServiceWorkerController mFrameworksImpl;
    private final ServiceWorkerWebSettingsCompat mWebSettings;

    public ServiceWorkerControllerImpl() {
        ApiFeature.N feature = WebViewFeatureInternal.SERVICE_WORKER_BASIC_USAGE;
        if (feature.isSupportedByFramework()) {
            this.mFrameworksImpl = ApiHelperForN.getServiceWorkerControllerInstance();
            this.mBoundaryInterface = null;
            this.mWebSettings = ApiHelperForN.getServiceWorkerWebSettingsImpl(getFrameworksImpl());
        } else {
            if (feature.isSupportedByWebView()) {
                this.mFrameworksImpl = null;
                this.mBoundaryInterface = WebViewGlueCommunicator.getFactory().getServiceWorkerController();
                this.mWebSettings = new ServiceWorkerWebSettingsImpl(this.mBoundaryInterface.getServiceWorkerWebSettings());
                return;
            }
            throw WebViewFeatureInternal.getUnsupportedOperationException();
        }
    }

    private ServiceWorkerController getFrameworksImpl() {
        if (this.mFrameworksImpl == null) {
            this.mFrameworksImpl = ApiHelperForN.getServiceWorkerControllerInstance();
        }
        return this.mFrameworksImpl;
    }

    private ServiceWorkerControllerBoundaryInterface getBoundaryInterface() {
        if (this.mBoundaryInterface == null) {
            this.mBoundaryInterface = WebViewGlueCommunicator.getFactory().getServiceWorkerController();
        }
        return this.mBoundaryInterface;
    }

    @Override // androidx.webkit.ServiceWorkerControllerCompat
    public ServiceWorkerWebSettingsCompat getServiceWorkerWebSettings() {
        return this.mWebSettings;
    }

    @Override // androidx.webkit.ServiceWorkerControllerCompat
    public void setServiceWorkerClient(ServiceWorkerClientCompat client) {
        ApiFeature.N feature = WebViewFeatureInternal.SERVICE_WORKER_BASIC_USAGE;
        if (feature.isSupportedByFramework()) {
            if (client == null) {
                ApiHelperForN.setServiceWorkerClient(getFrameworksImpl(), null);
                return;
            } else {
                ApiHelperForN.setServiceWorkerClientCompat(getFrameworksImpl(), client);
                return;
            }
        }
        if (feature.isSupportedByWebView()) {
            if (client == null) {
                getBoundaryInterface().setServiceWorkerClient(null);
                return;
            } else {
                getBoundaryInterface().setServiceWorkerClient(BoundaryInterfaceReflectionUtil.createInvocationHandlerFor(new ServiceWorkerClientAdapter(client)));
                return;
            }
        }
        throw WebViewFeatureInternal.getUnsupportedOperationException();
    }
}
