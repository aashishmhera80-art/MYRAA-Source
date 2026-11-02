package androidx.webkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public final class UserAgentMetadata {
    public static final int BITNESS_DEFAULT = 0;
    private final String mArchitecture;
    private int mBitness;
    private final List<BrandVersion> mBrandVersionList;
    private final String mFullVersion;
    private boolean mMobile;
    private final String mModel;
    private final String mPlatform;
    private final String mPlatformVersion;
    private boolean mWow64;

    private UserAgentMetadata(List<BrandVersion> brandVersionList, String fullVersion, String platform, String platformVersion, String architecture, String model, boolean mobile, int bitness, boolean wow64) {
        this.mMobile = true;
        this.mBitness = 0;
        this.mWow64 = false;
        this.mBrandVersionList = brandVersionList;
        this.mFullVersion = fullVersion;
        this.mPlatform = platform;
        this.mPlatformVersion = platformVersion;
        this.mArchitecture = architecture;
        this.mModel = model;
        this.mMobile = mobile;
        this.mBitness = bitness;
        this.mWow64 = wow64;
    }

    public List<BrandVersion> getBrandVersionList() {
        return this.mBrandVersionList;
    }

    public String getFullVersion() {
        return this.mFullVersion;
    }

    public String getPlatform() {
        return this.mPlatform;
    }

    public String getPlatformVersion() {
        return this.mPlatformVersion;
    }

    public String getArchitecture() {
        return this.mArchitecture;
    }

    public String getModel() {
        return this.mModel;
    }

    public boolean isMobile() {
        return this.mMobile;
    }

    public int getBitness() {
        return this.mBitness;
    }

    public boolean isWow64() {
        return this.mWow64;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserAgentMetadata)) {
            return false;
        }
        UserAgentMetadata that = (UserAgentMetadata) o;
        return this.mMobile == that.mMobile && this.mBitness == that.mBitness && this.mWow64 == that.mWow64 && Objects.equals(this.mBrandVersionList, that.mBrandVersionList) && Objects.equals(this.mFullVersion, that.mFullVersion) && Objects.equals(this.mPlatform, that.mPlatform) && Objects.equals(this.mPlatformVersion, that.mPlatformVersion) && Objects.equals(this.mArchitecture, that.mArchitecture) && Objects.equals(this.mModel, that.mModel);
    }

    public int hashCode() {
        return Objects.hash(this.mBrandVersionList, this.mFullVersion, this.mPlatform, this.mPlatformVersion, this.mArchitecture, this.mModel, Boolean.valueOf(this.mMobile), Integer.valueOf(this.mBitness), Boolean.valueOf(this.mWow64));
    }

    public static final class BrandVersion {
        private final String mBrand;
        private final String mFullVersion;
        private final String mMajorVersion;

        private BrandVersion(String brand, String majorVersion, String fullVersion) {
            this.mBrand = brand;
            this.mMajorVersion = majorVersion;
            this.mFullVersion = fullVersion;
        }

        public String getBrand() {
            return this.mBrand;
        }

        public String getMajorVersion() {
            return this.mMajorVersion;
        }

        public String getFullVersion() {
            return this.mFullVersion;
        }

        public String toString() {
            return this.mBrand + "," + this.mMajorVersion + "," + this.mFullVersion;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof BrandVersion)) {
                return false;
            }
            BrandVersion that = (BrandVersion) o;
            return Objects.equals(this.mBrand, that.mBrand) && Objects.equals(this.mMajorVersion, that.mMajorVersion) && Objects.equals(this.mFullVersion, that.mFullVersion);
        }

        public int hashCode() {
            return Objects.hash(this.mBrand, this.mMajorVersion, this.mFullVersion);
        }

        public static final class Builder {
            private String mBrand;
            private String mFullVersion;
            private String mMajorVersion;

            public Builder() {
            }

            public Builder(BrandVersion brandVersion) {
                this.mBrand = brandVersion.getBrand();
                this.mMajorVersion = brandVersion.getMajorVersion();
                this.mFullVersion = brandVersion.getFullVersion();
            }

            public BrandVersion build() {
                if (this.mBrand == null || this.mBrand.trim().isEmpty() || this.mMajorVersion == null || this.mMajorVersion.trim().isEmpty() || this.mFullVersion == null || this.mFullVersion.trim().isEmpty()) {
                    throw new IllegalStateException("Brand name, major version and full version should not be null or blank.");
                }
                return new BrandVersion(this.mBrand, this.mMajorVersion, this.mFullVersion);
            }

            public Builder setBrand(String brand) {
                if (brand.trim().isEmpty()) {
                    throw new IllegalArgumentException("Brand should not be blank.");
                }
                this.mBrand = brand;
                return this;
            }

            public Builder setMajorVersion(String majorVersion) {
                if (majorVersion.trim().isEmpty()) {
                    throw new IllegalArgumentException("MajorVersion should not be blank.");
                }
                this.mMajorVersion = majorVersion;
                return this;
            }

            public Builder setFullVersion(String fullVersion) {
                if (fullVersion.trim().isEmpty()) {
                    throw new IllegalArgumentException("FullVersion should not be blank.");
                }
                this.mFullVersion = fullVersion;
                return this;
            }
        }
    }

    public static final class Builder {
        private String mArchitecture;
        private int mBitness;
        private List<BrandVersion> mBrandVersionList;
        private String mFullVersion;
        private boolean mMobile;
        private String mModel;
        private String mPlatform;
        private String mPlatformVersion;
        private boolean mWow64;

        public Builder() {
            this.mBrandVersionList = new ArrayList();
            this.mMobile = true;
            this.mBitness = 0;
            this.mWow64 = false;
        }

        public Builder(UserAgentMetadata uaMetadata) {
            this.mBrandVersionList = new ArrayList();
            this.mMobile = true;
            this.mBitness = 0;
            this.mWow64 = false;
            this.mBrandVersionList = uaMetadata.getBrandVersionList();
            this.mFullVersion = uaMetadata.getFullVersion();
            this.mPlatform = uaMetadata.getPlatform();
            this.mPlatformVersion = uaMetadata.getPlatformVersion();
            this.mArchitecture = uaMetadata.getArchitecture();
            this.mModel = uaMetadata.getModel();
            this.mMobile = uaMetadata.isMobile();
            this.mBitness = uaMetadata.getBitness();
            this.mWow64 = uaMetadata.isWow64();
        }

        public UserAgentMetadata build() {
            return new UserAgentMetadata(this.mBrandVersionList, this.mFullVersion, this.mPlatform, this.mPlatformVersion, this.mArchitecture, this.mModel, this.mMobile, this.mBitness, this.mWow64);
        }

        public Builder setBrandVersionList(List<BrandVersion> brandVersions) {
            this.mBrandVersionList = brandVersions;
            return this;
        }

        public Builder setFullVersion(String fullVersion) {
            if (fullVersion == null) {
                this.mFullVersion = null;
                return this;
            }
            if (fullVersion.trim().isEmpty()) {
                throw new IllegalArgumentException("Full version should not be blank.");
            }
            this.mFullVersion = fullVersion;
            return this;
        }

        public Builder setPlatform(String platform) {
            if (platform == null) {
                this.mPlatform = null;
                return this;
            }
            if (platform.trim().isEmpty()) {
                throw new IllegalArgumentException("Platform should not be blank.");
            }
            this.mPlatform = platform;
            return this;
        }

        public Builder setPlatformVersion(String platformVersion) {
            this.mPlatformVersion = platformVersion;
            return this;
        }

        public Builder setArchitecture(String architecture) {
            this.mArchitecture = architecture;
            return this;
        }

        public Builder setModel(String model) {
            this.mModel = model;
            return this;
        }

        public Builder setMobile(boolean mobile) {
            this.mMobile = mobile;
            return this;
        }

        public Builder setBitness(int bitness) {
            this.mBitness = bitness;
            return this;
        }

        public Builder setWow64(boolean wow64) {
            this.mWow64 = wow64;
            return this;
        }
    }
}
