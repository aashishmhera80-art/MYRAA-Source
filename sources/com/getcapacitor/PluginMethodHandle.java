package com.getcapacitor;

import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes3.dex */
public class PluginMethodHandle {
    private final Method method;
    private final String name;
    private final String returnType;

    public PluginMethodHandle(Method method, PluginMethod methodDecorator) {
        this.method = method;
        this.name = method.getName();
        this.returnType = methodDecorator.returnType();
    }

    public String getReturnType() {
        return this.returnType;
    }

    public String getName() {
        return this.name;
    }

    public Method getMethod() {
        return this.method;
    }
}
