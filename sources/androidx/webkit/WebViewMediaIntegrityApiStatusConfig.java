package androidx.webkit;

import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class WebViewMediaIntegrityApiStatusConfig {
    public static final int WEBVIEW_MEDIA_INTEGRITY_API_DISABLED = 0;
    public static final int WEBVIEW_MEDIA_INTEGRITY_API_ENABLED = 2;
    public static final int WEBVIEW_MEDIA_INTEGRITY_API_ENABLED_WITHOUT_APP_IDENTITY = 1;
    private final int mDefaultStatus;
    private final Map<String, Integer> mOverrideRules;

    public WebViewMediaIntegrityApiStatusConfig(Builder builder) {
        this.mDefaultStatus = builder.mDefaultStatus;
        this.mOverrideRules = builder.mOverrideRules;
    }

    public static final class Builder {
        private final int mDefaultStatus;
        private Map<String, Integer> mOverrideRules = new HashMap();

        public Builder(int defaultStatus) {
            this.mDefaultStatus = defaultStatus;
        }

        public Builder addOverrideRule(String originPattern, int permission) {
            this.mOverrideRules.put(originPattern, Integer.valueOf(permission));
            return this;
        }

        public Builder setOverrideRules(Map<String, Integer> overrideRules) {
            this.mOverrideRules = overrideRules;
            return this;
        }

        public WebViewMediaIntegrityApiStatusConfig build() {
            return new WebViewMediaIntegrityApiStatusConfig(this);
        }
    }

    public int getDefaultStatus() {
        return this.mDefaultStatus;
    }

    public Map<String, Integer> getOverrideRules() {
        return this.mOverrideRules;
    }
}
