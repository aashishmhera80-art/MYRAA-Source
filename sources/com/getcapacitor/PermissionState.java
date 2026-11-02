package com.getcapacitor;

import java.util.Locale;

/* JADX INFO: loaded from: classes3.dex */
public enum PermissionState {
    GRANTED("granted"),
    DENIED("denied"),
    PROMPT("prompt"),
    PROMPT_WITH_RATIONALE("prompt-with-rationale");

    private String state;

    PermissionState(String state) {
        this.state = state;
    }

    @Override // java.lang.Enum
    public String toString() {
        return this.state;
    }

    public static PermissionState byState(String state) {
        return valueOf(state.toUpperCase(Locale.ROOT).replace('-', '_'));
    }
}
