package com.getcapacitor.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* JADX INFO: loaded from: classes2.dex */
@Retention(RetentionPolicy.RUNTIME)
public @interface CapacitorPlugin {
    String name() default "";

    Permission[] permissions() default {};

    int[] requestCodes() default {};
}
