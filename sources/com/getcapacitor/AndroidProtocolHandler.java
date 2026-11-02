package com.getcapacitor;

import android.content.Context;
import android.net.Uri;
import android.util.TypedValue;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public class AndroidProtocolHandler {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private Context context;

    public AndroidProtocolHandler(Context context) {
        this.context = context;
    }

    public InputStream openAsset(String path) throws IOException {
        return this.context.getAssets().open(path, 2);
    }

    public InputStream openResource(Uri uri) {
        if (uri.getPath() == null) {
            throw new AssertionError();
        }
        List<String> pathSegments = uri.getPathSegments();
        String assetType = pathSegments.get(pathSegments.size() - 2);
        String assetName = pathSegments.get(pathSegments.size() - 1);
        String assetName2 = assetName.split("\\.")[0];
        try {
            if (this.context.getApplicationContext() != null) {
                this.context = this.context.getApplicationContext();
            }
            int fieldId = getFieldId(this.context, assetType, assetName2);
            int valueType = getValueType(this.context, fieldId);
            if (valueType == 3) {
                return this.context.getResources().openRawResource(fieldId);
            }
            Logger.error("Asset not of type string: " + uri);
            return null;
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) {
            Logger.error("Unable to open resource URL: " + uri, e);
            return null;
        }
    }

    private static int getFieldId(Context context, String assetType, String assetName) throws IllegalAccessException, NoSuchFieldException, ClassNotFoundException {
        Class<?> d = context.getClassLoader().loadClass(context.getPackageName() + ".R$" + assetType);
        Field field = d.getField(assetName);
        return field.getInt(null);
    }

    public InputStream openFile(String filePath) throws IOException {
        String realPath = filePath.replace(Bridge.CAPACITOR_FILE_START, "");
        File localFile = new File(realPath);
        return new FileInputStream(localFile);
    }

    public InputStream openContentUrl(Uri uri) throws IOException {
        Integer port = Integer.valueOf(uri.getPort());
        String baseUrl = uri.getScheme() + "://" + uri.getHost();
        if (port.intValue() != -1) {
            baseUrl = baseUrl + ":" + port;
        }
        String realPath = uri.toString().replace(baseUrl + Bridge.CAPACITOR_CONTENT_START, "content:/");
        try {
            InputStream stream = this.context.getContentResolver().openInputStream(Uri.parse(realPath));
            return stream;
        } catch (SecurityException e) {
            Logger.error("Unable to open content URL: " + uri, e);
            return null;
        }
    }

    private static int getValueType(Context context, int fieldId) {
        TypedValue value = new TypedValue();
        context.getResources().getValue(fieldId, value, true);
        return value.type;
    }
}
