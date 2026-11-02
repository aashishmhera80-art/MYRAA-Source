package androidx.webkit.internal;

import androidx.webkit.ProxyConfig;
import androidx.webkit.ProxyController;
import java.lang.reflect.Array;
import java.util.List;
import java.util.concurrent.Executor;
import org.chromium.support_lib_boundary.ProxyControllerBoundaryInterface;

/* JADX INFO: loaded from: classes.dex */
public class ProxyControllerImpl extends ProxyController {
    private ProxyControllerBoundaryInterface mBoundaryInterface;

    @Override // androidx.webkit.ProxyController
    public void setProxyOverride(ProxyConfig proxyConfig, Executor executor, Runnable listener) {
        ApiFeature.NoFramework proxyOverride = WebViewFeatureInternal.PROXY_OVERRIDE;
        ApiFeature.NoFramework reverseBypass = WebViewFeatureInternal.PROXY_OVERRIDE_REVERSE_BYPASS;
        String[][] proxyRuleArray = proxyRulesToStringArray(proxyConfig.getProxyRules());
        String[] bypassRuleArray = (String[]) proxyConfig.getBypassRules().toArray(new String[0]);
        if (proxyOverride.isSupportedByWebView() && !proxyConfig.isReverseBypassEnabled()) {
            getBoundaryInterface().setProxyOverride(proxyRuleArray, bypassRuleArray, listener, executor);
        } else {
            if (proxyOverride.isSupportedByWebView() && reverseBypass.isSupportedByWebView()) {
                getBoundaryInterface().setProxyOverride(proxyRuleArray, bypassRuleArray, listener, executor, proxyConfig.isReverseBypassEnabled());
                return;
            }
            throw WebViewFeatureInternal.getUnsupportedOperationException();
        }
    }

    @Override // androidx.webkit.ProxyController
    public void clearProxyOverride(Executor executor, Runnable listener) {
        ApiFeature.NoFramework feature = WebViewFeatureInternal.PROXY_OVERRIDE;
        if (feature.isSupportedByWebView()) {
            getBoundaryInterface().clearProxyOverride(listener, executor);
            return;
        }
        throw WebViewFeatureInternal.getUnsupportedOperationException();
    }

    public static String[][] proxyRulesToStringArray(List<ProxyConfig.ProxyRule> proxyRuleList) {
        String[][] proxyRuleArray = (String[][]) Array.newInstance((Class<?>) String.class, proxyRuleList.size(), 2);
        for (int i = 0; i < proxyRuleList.size(); i++) {
            proxyRuleArray[i][0] = proxyRuleList.get(i).getSchemeFilter();
            proxyRuleArray[i][1] = proxyRuleList.get(i).getUrl();
        }
        return proxyRuleArray;
    }

    private ProxyControllerBoundaryInterface getBoundaryInterface() {
        if (this.mBoundaryInterface == null) {
            this.mBoundaryInterface = WebViewGlueCommunicator.getFactory().getProxyController();
        }
        return this.mBoundaryInterface;
    }
}
