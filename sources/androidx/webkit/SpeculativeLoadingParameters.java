package androidx.webkit;

import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public final class SpeculativeLoadingParameters {
    private final Map<String, String> mAdditionalHeaders;
    private final NoVarySearchHeader mExpectedNoVarySearchHeader;
    private final boolean mIsJavaScriptEnabled;

    private SpeculativeLoadingParameters(Map<String, String> additionalHeaders, NoVarySearchHeader noVarySearchHeader, boolean isJavaScriptEnabled) {
        this.mAdditionalHeaders = additionalHeaders;
        this.mExpectedNoVarySearchHeader = noVarySearchHeader;
        this.mIsJavaScriptEnabled = isJavaScriptEnabled;
    }

    public Map<String, String> getAdditionalHeaders() {
        return this.mAdditionalHeaders;
    }

    public NoVarySearchHeader getExpectedNoVarySearchData() {
        return this.mExpectedNoVarySearchHeader;
    }

    public boolean isJavaScriptEnabled() {
        return this.mIsJavaScriptEnabled;
    }

    public static final class Builder {
        private final Map<String, String> mAdditionalHeaders = new HashMap();
        private NoVarySearchHeader mExpectedNoVarySearchHeader = null;
        private boolean mIsJavaScriptEnabled = false;

        public SpeculativeLoadingParameters build() {
            return new SpeculativeLoadingParameters(this.mAdditionalHeaders, this.mExpectedNoVarySearchHeader, this.mIsJavaScriptEnabled);
        }

        public Builder addAdditionalHeader(String key, String value) {
            this.mAdditionalHeaders.put(key, value);
            return this;
        }

        public Builder addAdditionalHeaders(Map<String, String> additionalHeaders) {
            this.mAdditionalHeaders.putAll(additionalHeaders);
            return this;
        }

        public Builder setExpectedNoVarySearchData(NoVarySearchHeader expectedNoVarySearchHeader) {
            this.mExpectedNoVarySearchHeader = expectedNoVarySearchHeader;
            return this;
        }

        public Builder setJavaScriptEnabled(boolean javaScriptEnabled) {
            this.mIsJavaScriptEnabled = javaScriptEnabled;
            return this;
        }
    }
}
