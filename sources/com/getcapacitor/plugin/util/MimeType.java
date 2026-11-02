package com.getcapacitor.plugin.util;

/* JADX INFO: loaded from: classes4.dex */
enum MimeType {
    APPLICATION_JSON("application/json"),
    APPLICATION_VND_API_JSON("application/vnd.api+json"),
    TEXT_HTML("text/html");

    private final String value;

    MimeType(String value) {
        this.value = value;
    }

    String getValue() {
        return this.value;
    }
}
