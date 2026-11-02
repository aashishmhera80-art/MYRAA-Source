package org.chromium.support_lib_boundary;

import java.lang.reflect.InvocationHandler;
import java.util.List;
import org.jspecify.annotations.NullMarked;

/* JADX INFO: loaded from: classes.dex */
@NullMarked
public interface ProfileStoreBoundaryInterface {
    boolean deleteProfile(String str);

    List<String> getAllProfileNames();

    InvocationHandler getOrCreateProfile(String str);

    InvocationHandler getProfile(String str);
}
