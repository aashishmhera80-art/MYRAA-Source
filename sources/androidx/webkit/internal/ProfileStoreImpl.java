package androidx.webkit.internal;

import androidx.webkit.Profile;
import androidx.webkit.ProfileStore;
import java.lang.reflect.InvocationHandler;
import java.util.List;
import org.chromium.support_lib_boundary.ProfileBoundaryInterface;
import org.chromium.support_lib_boundary.ProfileStoreBoundaryInterface;
import org.chromium.support_lib_boundary.util.BoundaryInterfaceReflectionUtil;

/* JADX INFO: loaded from: classes.dex */
public class ProfileStoreImpl implements ProfileStore {
    private static ProfileStore sInstance;
    private final ProfileStoreBoundaryInterface mProfileStoreImpl;

    private ProfileStoreImpl(ProfileStoreBoundaryInterface profileStoreImpl) {
        this.mProfileStoreImpl = profileStoreImpl;
    }

    private ProfileStoreImpl() {
        this.mProfileStoreImpl = null;
    }

    public static ProfileStore getInstance() {
        if (sInstance == null) {
            sInstance = new ProfileStoreImpl(WebViewGlueCommunicator.getFactory().getProfileStore());
        }
        return sInstance;
    }

    @Override // androidx.webkit.ProfileStore
    public Profile getOrCreateProfile(String name) {
        ApiFeature.NoFramework feature = WebViewFeatureInternal.MULTI_PROFILE;
        if (feature.isSupportedByWebView()) {
            return new ProfileImpl((ProfileBoundaryInterface) BoundaryInterfaceReflectionUtil.castToSuppLibClass(ProfileBoundaryInterface.class, this.mProfileStoreImpl.getOrCreateProfile(name)));
        }
        throw WebViewFeatureInternal.getUnsupportedOperationException();
    }

    @Override // androidx.webkit.ProfileStore
    public Profile getProfile(String name) {
        ApiFeature.NoFramework feature = WebViewFeatureInternal.MULTI_PROFILE;
        if (feature.isSupportedByWebView()) {
            InvocationHandler profileBoundaryInterface = this.mProfileStoreImpl.getProfile(name);
            if (profileBoundaryInterface != null) {
                return new ProfileImpl((ProfileBoundaryInterface) BoundaryInterfaceReflectionUtil.castToSuppLibClass(ProfileBoundaryInterface.class, profileBoundaryInterface));
            }
            return null;
        }
        throw WebViewFeatureInternal.getUnsupportedOperationException();
    }

    @Override // androidx.webkit.ProfileStore
    public List<String> getAllProfileNames() {
        ApiFeature.NoFramework feature = WebViewFeatureInternal.MULTI_PROFILE;
        if (feature.isSupportedByWebView()) {
            return this.mProfileStoreImpl.getAllProfileNames();
        }
        throw WebViewFeatureInternal.getUnsupportedOperationException();
    }

    @Override // androidx.webkit.ProfileStore
    public boolean deleteProfile(String name) throws IllegalStateException {
        ApiFeature.NoFramework feature = WebViewFeatureInternal.MULTI_PROFILE;
        if (feature.isSupportedByWebView()) {
            return this.mProfileStoreImpl.deleteProfile(name);
        }
        throw WebViewFeatureInternal.getUnsupportedOperationException();
    }
}
