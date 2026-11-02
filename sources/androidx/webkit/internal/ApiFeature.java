package androidx.webkit.internal;

import android.os.Build;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.chromium.support_lib_boundary.util.BoundaryInterfaceReflectionUtil;

/* JADX INFO: loaded from: classes.dex */
public abstract class ApiFeature implements ConditionallySupportedFeature {
    private static final Set<ApiFeature> sValues = new HashSet();
    private final String mInternalFeatureValue;
    private final String mPublicFeatureValue;

    public abstract boolean isSupportedByFramework();

    ApiFeature(String publicFeatureValue, String internalFeatureValue) {
        this.mPublicFeatureValue = publicFeatureValue;
        this.mInternalFeatureValue = internalFeatureValue;
        sValues.add(this);
    }

    @Override // androidx.webkit.internal.ConditionallySupportedFeature
    public String getPublicFeatureName() {
        return this.mPublicFeatureValue;
    }

    @Override // androidx.webkit.internal.ConditionallySupportedFeature
    public boolean isSupported() {
        return isSupportedByFramework() || isSupportedByWebView();
    }

    public boolean isSupportedByWebView() {
        return BoundaryInterfaceReflectionUtil.containsFeature(LAZY_HOLDER.WEBVIEW_APK_FEATURES, this.mInternalFeatureValue);
    }

    public static Set<ApiFeature> values() {
        return Collections.unmodifiableSet(sValues);
    }

    public static Set<String> getWebViewApkFeaturesForTesting() {
        return LAZY_HOLDER.WEBVIEW_APK_FEATURES;
    }

    private static class LAZY_HOLDER {
        static final Set<String> WEBVIEW_APK_FEATURES = new HashSet(Arrays.asList(WebViewGlueCommunicator.getFactory().getWebViewFeatures()));

        private LAZY_HOLDER() {
        }
    }

    public static class NoFramework extends ApiFeature {
        NoFramework(String publicFeatureValue, String internalFeatureValue) {
            super(publicFeatureValue, internalFeatureValue);
        }

        @Override // androidx.webkit.internal.ApiFeature
        public final boolean isSupportedByFramework() {
            return false;
        }
    }

    public static class M extends ApiFeature {
        M(String publicFeatureValue, String internalFeatureValue) {
            super(publicFeatureValue, internalFeatureValue);
        }

        @Override // androidx.webkit.internal.ApiFeature
        public final boolean isSupportedByFramework() {
            return true;
        }
    }

    public static class N extends ApiFeature {
        N(String publicFeatureValue, String internalFeatureValue) {
            super(publicFeatureValue, internalFeatureValue);
        }

        @Override // androidx.webkit.internal.ApiFeature
        public final boolean isSupportedByFramework() {
            return true;
        }
    }

    public static class O extends ApiFeature {
        O(String publicFeatureValue, String internalFeatureValue) {
            super(publicFeatureValue, internalFeatureValue);
        }

        @Override // androidx.webkit.internal.ApiFeature
        public final boolean isSupportedByFramework() {
            return Build.VERSION.SDK_INT >= 26;
        }
    }

    public static class O_MR1 extends ApiFeature {
        O_MR1(String publicFeatureValue, String internalFeatureValue) {
            super(publicFeatureValue, internalFeatureValue);
        }

        @Override // androidx.webkit.internal.ApiFeature
        public final boolean isSupportedByFramework() {
            return Build.VERSION.SDK_INT >= 27;
        }
    }

    public static class P extends ApiFeature {
        P(String publicFeatureValue, String internalFeatureValue) {
            super(publicFeatureValue, internalFeatureValue);
        }

        @Override // androidx.webkit.internal.ApiFeature
        public final boolean isSupportedByFramework() {
            return Build.VERSION.SDK_INT >= 28;
        }
    }

    public static class Q extends ApiFeature {
        Q(String publicFeatureValue, String internalFeatureValue) {
            super(publicFeatureValue, internalFeatureValue);
        }

        @Override // androidx.webkit.internal.ApiFeature
        public final boolean isSupportedByFramework() {
            return Build.VERSION.SDK_INT >= 29;
        }
    }

    public static class T extends ApiFeature {
        T(String publicFeatureValue, String internalFeatureValue) {
            super(publicFeatureValue, internalFeatureValue);
        }

        @Override // androidx.webkit.internal.ApiFeature
        public final boolean isSupportedByFramework() {
            return Build.VERSION.SDK_INT >= 33;
        }
    }
}
