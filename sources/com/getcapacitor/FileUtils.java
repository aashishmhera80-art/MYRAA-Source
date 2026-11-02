package com.getcapacitor;

import android.content.ContentUris;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/* JADX INFO: loaded from: classes3.dex */
public class FileUtils {
    private static String CapacitorFileScheme = Bridge.CAPACITOR_FILE_START;

    public enum Type {
        IMAGE("image");

        private String type;

        Type(String type) {
            this.type = type;
        }
    }

    public static String getPortablePath(Context c, String host, Uri u) {
        String path = getFileUrlForUri(c, u);
        if (path.startsWith("file://")) {
            path = path.replace("file://", "");
        }
        return host + Bridge.CAPACITOR_FILE_START + path;
    }

    public static String getFileUrlForUri(Context context, Uri uri) {
        if (DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                if ("primary".equalsIgnoreCase(split[0])) {
                    return legacyPrimaryPath(split[1]);
                }
                int splitIndex = docId.indexOf(58, 1);
                String tag = docId.substring(0, splitIndex);
                String path = docId.substring(splitIndex + 1);
                String nonPrimaryVolume = getPathToNonPrimaryVolume(context, tag);
                if (nonPrimaryVolume != null) {
                    String result = nonPrimaryVolume + "/" + path;
                    File file = new File(result);
                    if (file.exists() && file.canRead()) {
                        return result;
                    }
                    return null;
                }
            } else {
                if (isDownloadsDocument(uri)) {
                    String id = DocumentsContract.getDocumentId(uri);
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id).longValue());
                    return getDataColumn(context, contentUri, null, null);
                }
                if (isMediaDocument(uri)) {
                    String[] split2 = DocumentsContract.getDocumentId(uri).split(":");
                    String type = split2[0];
                    Uri contentUri2 = null;
                    if ("image".equals(type)) {
                        contentUri2 = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri2 = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri2 = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    String[] selectionArgs = {split2[1]};
                    return getDataColumn(context, contentUri2, "_id=?", selectionArgs);
                }
            }
        } else {
            if ("content".equalsIgnoreCase(uri.getScheme())) {
                return isGooglePhotosUri(uri) ? uri.getLastPathSegment() : getDataColumn(context, uri, null, null);
            }
            if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        }
        return null;
    }

    private static String legacyPrimaryPath(String pathPart) {
        return Environment.getExternalStorageDirectory() + "/" + pathPart;
    }

    static String readFileFromAssets(AssetManager assetManager, String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
        try {
            StringBuilder buffer = new StringBuilder();
            while (true) {
                String line = reader.readLine();
                if (line != null) {
                    buffer.append(line).append("\n");
                } else {
                    String string = buffer.toString();
                    reader.close();
                    return string;
                }
            }
        } catch (Throwable th) {
            try {
                reader.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    static String readFileFromDisk(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        try {
            StringBuilder buffer = new StringBuilder();
            while (true) {
                String line = reader.readLine();
                if (line != null) {
                    buffer.append(line).append("\n");
                } else {
                    String string = buffer.toString();
                    reader.close();
                    return string;
                }
            }
        } catch (Throwable th) {
            try {
                reader.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    /* JADX WARN: Code duplicated, block: B:30:0x0055  */
    /* JADX WARN: Code duplicated, block: B:36:? A[SYNTHETIC] */
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        IllegalArgumentException ex;
        Uri uri2;
        String path = null;
        Cursor cursor = null;
        String[] projection = {"_data"};
        try {
            try {
                uri2 = uri;
                try {
                    cursor = context.getContentResolver().query(uri2, projection, selection, selectionArgs, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        int index = cursor.getColumnIndexOrThrow("_data");
                        path = cursor.getString(index);
                    }
                    if (cursor != null) {
                        cursor.close();
                    }
                    return path == null ? getCopyFilePath(uri2, context) : path;
                } catch (IllegalArgumentException e) {
                    String selection2 = getCopyFilePath(uri2, context);
                    if (cursor != null) {
                        cursor.close();
                    }
                    return selection2;
                }
            } catch (Throwable th) {
                ex = th;
                if (0 != 0) {
                    throw ex;
                }
                cursor.close();
                throw ex;
            }
        } catch (IllegalArgumentException e2) {
            uri2 = uri;
        } catch (Throwable th2) {
            ex = th2;
            if (0 != 0) {
                throw ex;
            }
            cursor.close();
            throw ex;
        }
    }

    private static String getCopyFilePath(Uri uri, Context context) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        int nameIndex = cursor.getColumnIndex("_display_name");
        cursor.moveToFirst();
        String name = cursor.getString(nameIndex);
        String fileName = sanitizeFilename(name);
        File file = new File(context.getFilesDir(), fileName);
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            int bufferSize = Math.min(inputStream.available(), 1048576);
            byte[] buffers = new byte[bufferSize];
            while (true) {
                int read = inputStream.read(buffers);
                if (read == -1) {
                    break;
                }
                outputStream.write(buffers, 0, read);
            }
            inputStream.close();
            outputStream.close();
            return file.getPath();
        } catch (Exception e) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    private static String getPathToNonPrimaryVolume(Context context, String tag) {
        String path;
        int index;
        File[] volumes = context.getExternalCacheDirs();
        if (volumes != null) {
            for (File volume : volumes) {
                if (volume != null && (path = volume.getAbsolutePath()) != null && (index = path.indexOf(tag)) != -1) {
                    return path.substring(0, index) + tag;
                }
            }
            return null;
        }
        return null;
    }

    private static String sanitizeFilename(String displayName) {
        String[] badCharacters = {"..", "/"};
        String[] segments = displayName.split("/");
        String fileName = segments[segments.length - 1];
        for (String suspString : badCharacters) {
            fileName = fileName.replace(suspString, "_");
        }
        return fileName;
    }
}
