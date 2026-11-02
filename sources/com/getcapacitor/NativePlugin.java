package com.getcapacitor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* JADX INFO: loaded from: classes3.dex */
@Retention(RetentionPolicy.RUNTIME)
@Deprecated
public @interface NativePlugin {
    String name() default "";

    int permissionRequestCode() default 9000;

    String[] permissions() default {};

    int[] requestCodes() default {};
}
