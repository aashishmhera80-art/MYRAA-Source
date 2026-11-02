package com.getcapacitor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* JADX INFO: loaded from: classes3.dex */
@Retention(RetentionPolicy.RUNTIME)
public @interface PluginMethod {
    public static final String RETURN_CALLBACK = "callback";
    public static final String RETURN_NONE = "none";
    public static final String RETURN_PROMISE = "promise";

    String returnType() default "promise";
}
