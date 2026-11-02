package com.getcapacitor.plugin.util;

import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: classes4.dex */
public interface ICapacitorHttpUrlConnection {
    InputStream getErrorStream();

    String getHeaderField(String str);

    InputStream getInputStream() throws IOException;
}
