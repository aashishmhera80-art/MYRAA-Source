package org.apache.cordova;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Looper;
import android.util.Base64;
import android.webkit.MimeTypeMap;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.Locale;
import java.util.zip.GZIPInputStream;

/* JADX INFO: loaded from: classes.dex */
public class CordovaResourceApi {
    private static final String[] LOCAL_FILE_PROJECTION = {"_data"};
    private static final String LOG_TAG = "CordovaResourceApi";
    public static final String PLUGIN_URI_SCHEME = "cdvplugin";
    public static final int URI_TYPE_ASSET = 1;
    public static final int URI_TYPE_CONTENT = 2;
    public static final int URI_TYPE_DATA = 4;
    public static final int URI_TYPE_FILE = 0;
    public static final int URI_TYPE_HTTP = 5;
    public static final int URI_TYPE_HTTPS = 6;
    public static final int URI_TYPE_PLUGIN = 7;
    public static final int URI_TYPE_RESOURCE = 3;
    public static final int URI_TYPE_UNKNOWN = -1;
    public static Thread jsThread;
    private final AssetManager assetManager;
    private final ContentResolver contentResolver;
    private final PluginManager pluginManager;
    private boolean threadCheckingEnabled = true;

    public CordovaResourceApi(Context context, PluginManager pluginManager) {
        this.contentResolver = context.getContentResolver();
        this.assetManager = context.getAssets();
        this.pluginManager = pluginManager;
    }

    public void setThreadCheckingEnabled(boolean value) {
        this.threadCheckingEnabled = value;
    }

    public boolean isThreadCheckingEnabled() {
        return this.threadCheckingEnabled;
    }

    public static int getUriType(Uri uri) {
        assertNonRelative(uri);
        String scheme = uri.getScheme();
        if ("content".equalsIgnoreCase(scheme)) {
            return 2;
        }
        if ("android.resource".equalsIgnoreCase(scheme)) {
            return 3;
        }
        if ("file".equalsIgnoreCase(scheme)) {
            if (uri.getPath().startsWith("/android_asset/")) {
                return 1;
            }
            return 0;
        }
        if ("data".equalsIgnoreCase(scheme)) {
            return 4;
        }
        if ("http".equalsIgnoreCase(scheme)) {
            return 5;
        }
        if ("https".equalsIgnoreCase(scheme)) {
            return 6;
        }
        if (PLUGIN_URI_SCHEME.equalsIgnoreCase(scheme)) {
            return 7;
        }
        return -1;
    }

    public Uri remapUri(Uri uri) {
        assertNonRelative(uri);
        Uri pluginUri = this.pluginManager.remapUri(uri);
        return pluginUri != null ? pluginUri : uri;
    }

    public String remapPath(String path) {
        return remapUri(Uri.fromFile(new File(path))).getPath();
    }

    public File mapUriToFile(Uri uri) {
        assertBackgroundThread();
        switch (getUriType(uri)) {
            case 0:
                return new File(uri.getPath());
            case 1:
            default:
                return null;
            case 2:
                Cursor cursor = this.contentResolver.query(uri, LOCAL_FILE_PROJECTION, null, null, null);
                if (cursor != null) {
                    try {
                        int columnIndex = cursor.getColumnIndex(LOCAL_FILE_PROJECTION[0]);
                        if (columnIndex != -1 && cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            String realPath = cursor.getString(columnIndex);
                            if (realPath != null) {
                                return new File(realPath);
                            }
                        }
                        return null;
                    } finally {
                        cursor.close();
                    }
                }
                return null;
        }
    }

    public String getMimeType(Uri uri) {
        switch (getUriType(uri)) {
            case 0:
            case 1:
                return getMimeTypeFromPath(uri.getPath());
            case 2:
            case 3:
                return this.contentResolver.getType(uri);
            case 4:
                return getDataUriMimeType(uri);
            case 5:
            case 6:
                try {
                    HttpURLConnection conn = (HttpURLConnection) new URL(uri.toString()).openConnection();
                    conn.setDoInput(false);
                    conn.setRequestMethod("HEAD");
                    String mimeType = conn.getHeaderField("Content-Type");
                    if (mimeType != null) {
                        return mimeType.split(";")[0];
                    }
                    return mimeType;
                } catch (IOException e) {
                    return null;
                }
            default:
                return null;
        }
    }

    private String getMimeTypeFromPath(String path) {
        String extension = path;
        int lastDot = extension.lastIndexOf(46);
        if (lastDot != -1) {
            extension = extension.substring(lastDot + 1);
        }
        String extension2 = extension.toLowerCase(Locale.getDefault());
        if (extension2.equals("3ga")) {
            return "audio/3gpp";
        }
        if (extension2.equals("js")) {
            return "text/javascript";
        }
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension2);
    }

    public OpenForReadResult openForRead(Uri uri) throws IOException {
        return openForRead(uri, false);
    }

    public OpenForReadResult openForRead(Uri uri, boolean skipThreadCheck) throws IOException {
        long length;
        InputStream inputStream;
        AssetFileDescriptor assetFd;
        String mimeType;
        InputStream inputStream2;
        if (!skipThreadCheck) {
            assertBackgroundThread();
        }
        switch (getUriType(uri)) {
            case 0:
                FileInputStream inputStream3 = new FileInputStream(uri.getPath());
                String mimeType2 = getMimeTypeFromPath(uri.getPath());
                long length2 = inputStream3.getChannel().size();
                return new OpenForReadResult(uri, inputStream3, mimeType2, length2, null);
            case 1:
                String assetPath = uri.getPath().substring(15);
                AssetFileDescriptor assetFd2 = null;
                try {
                    assetFd2 = this.assetManager.openFd(assetPath);
                    InputStream inputStream4 = assetFd2.createInputStream();
                    long length3 = assetFd2.getLength();
                    inputStream = inputStream4;
                    length = length3;
                    assetFd = assetFd2;
                } catch (FileNotFoundException e) {
                    InputStream inputStream5 = this.assetManager.open(assetPath);
                    long length4 = inputStream5.available();
                    length = length4;
                    inputStream = inputStream5;
                    assetFd = assetFd2;
                }
                String mimeType3 = getMimeTypeFromPath(assetPath);
                return new OpenForReadResult(uri, inputStream, mimeType3, length, assetFd);
            case 2:
            case 3:
                String mimeType4 = this.contentResolver.getType(uri);
                AssetFileDescriptor assetFd3 = this.contentResolver.openAssetFileDescriptor(uri, "r");
                InputStream inputStream6 = assetFd3.createInputStream();
                long length5 = assetFd3.getLength();
                return new OpenForReadResult(uri, inputStream6, mimeType4, length5, assetFd3);
            case 4:
                OpenForReadResult ret = readDataUri(uri);
                if (ret != null) {
                    return ret;
                }
                break;
            case 5:
            case 6:
                HttpURLConnection conn = (HttpURLConnection) new URL(uri.toString()).openConnection();
                conn.setRequestProperty("Accept-Encoding", "gzip");
                conn.setDoInput(true);
                String mimeType5 = conn.getHeaderField("Content-Type");
                if (mimeType5 == null) {
                    mimeType = mimeType5;
                } else {
                    mimeType = mimeType5.split(";")[0];
                }
                int length6 = conn.getContentLength();
                if ("gzip".equals(conn.getContentEncoding())) {
                    inputStream2 = new GZIPInputStream(conn.getInputStream());
                } else {
                    inputStream2 = conn.getInputStream();
                }
                return new OpenForReadResult(uri, inputStream2, mimeType, length6, null);
            case 7:
                String pluginId = uri.getHost();
                CordovaPlugin plugin = this.pluginManager.getPlugin(pluginId);
                if (plugin == null) {
                    throw new FileNotFoundException("Invalid plugin ID in URI: " + uri);
                }
                return plugin.handleOpenForRead(uri);
        }
        throw new FileNotFoundException("URI not supported by CordovaResourceApi: " + uri);
    }

    public OutputStream openOutputStream(Uri uri) throws IOException {
        return openOutputStream(uri, false);
    }

    public OutputStream openOutputStream(Uri uri, boolean append) throws IOException {
        assertBackgroundThread();
        switch (getUriType(uri)) {
            case 0:
                File localFile = new File(uri.getPath());
                File parent = localFile.getParentFile();
                if (parent != null) {
                    parent.mkdirs();
                }
                return new FileOutputStream(localFile, append);
            case 1:
            default:
                throw new FileNotFoundException("URI not supported by CordovaResourceApi: " + uri);
            case 2:
            case 3:
                AssetFileDescriptor assetFd = this.contentResolver.openAssetFileDescriptor(uri, append ? "wa" : "w");
                return assetFd.createOutputStream();
        }
    }

    public HttpURLConnection createHttpConnection(Uri uri) throws IOException {
        assertBackgroundThread();
        return (HttpURLConnection) new URL(uri.toString()).openConnection();
    }

    public void copyResource(OpenForReadResult input, OutputStream outputStream) throws IOException {
        long offset;
        assertBackgroundThread();
        try {
            InputStream inputStream = input.inputStream;
            if ((inputStream instanceof FileInputStream) && (outputStream instanceof FileOutputStream)) {
                FileChannel inChannel = ((FileInputStream) input.inputStream).getChannel();
                FileChannel outChannel = ((FileOutputStream) outputStream).getChannel();
                long length = input.length;
                if (input.assetFd == null) {
                    offset = 0;
                } else {
                    offset = input.assetFd.getStartOffset();
                }
                inChannel.position(offset);
                outChannel.transferFrom(inChannel, 0L, length);
            } else {
                byte[] buffer = new byte[8192];
                while (true) {
                    int bytesRead = inputStream.read(buffer, 0, 8192);
                    if (bytesRead <= 0) {
                        break;
                    } else {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
            }
        } finally {
            input.inputStream.close();
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    public void copyResource(Uri sourceUri, OutputStream outputStream) throws IOException {
        copyResource(openForRead(sourceUri), outputStream);
    }

    public void copyResource(Uri sourceUri, Uri dstUri) throws IOException {
        copyResource(openForRead(sourceUri), openOutputStream(dstUri));
    }

    private void assertBackgroundThread() {
        if (this.threadCheckingEnabled) {
            Thread curThread = Thread.currentThread();
            if (curThread == Looper.getMainLooper().getThread()) {
                throw new IllegalStateException("Do not perform IO operations on the UI thread. Use CordovaInterface.getThreadPool() instead.");
            }
            if (curThread == jsThread) {
                throw new IllegalStateException("Tried to perform an IO operation on the WebCore thread. Use CordovaInterface.getThreadPool() instead.");
            }
        }
    }

    private String getDataUriMimeType(Uri uri) {
        String uriAsString = uri.getSchemeSpecificPart();
        int commaPos = uriAsString.indexOf(44);
        if (commaPos == -1) {
            return null;
        }
        String[] mimeParts = uriAsString.substring(0, commaPos).split(";");
        if (mimeParts.length > 0) {
            return mimeParts[0];
        }
        return null;
    }

    private OpenForReadResult readDataUri(Uri uri) {
        String contentType;
        byte[] data;
        String uriAsString = uri.getSchemeSpecificPart();
        int commaPos = uriAsString.indexOf(44);
        if (commaPos == -1) {
            return null;
        }
        String[] mimeParts = uriAsString.substring(0, commaPos).split(";");
        boolean base64 = false;
        if (mimeParts.length <= 0) {
            contentType = null;
        } else {
            String contentType2 = mimeParts[0];
            contentType = contentType2;
        }
        for (int i = 1; i < mimeParts.length; i++) {
            if ("base64".equalsIgnoreCase(mimeParts[i])) {
                base64 = true;
            }
        }
        int i2 = commaPos + 1;
        String dataPartAsString = uriAsString.substring(i2);
        if (base64) {
            data = Base64.decode(dataPartAsString, 0);
        } else {
            try {
                data = dataPartAsString.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                data = dataPartAsString.getBytes();
            }
        }
        InputStream inputStream = new ByteArrayInputStream(data);
        return new OpenForReadResult(uri, inputStream, contentType, data.length, null);
    }

    private static void assertNonRelative(Uri uri) {
        if (!uri.isAbsolute()) {
            throw new IllegalArgumentException("Relative URIs are not supported.");
        }
    }

    public static final class OpenForReadResult {
        public final AssetFileDescriptor assetFd;
        public final InputStream inputStream;
        public final long length;
        public final String mimeType;
        public final Uri uri;

        public OpenForReadResult(Uri uri, InputStream inputStream, String mimeType, long length, AssetFileDescriptor assetFd) {
            this.uri = uri;
            this.inputStream = inputStream;
            this.mimeType = mimeType;
            this.length = length;
            this.assetFd = assetFd;
        }
    }
}
