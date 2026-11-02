package com.getcapacitor.plugin.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.StrictMode;
import androidx.core.content.FileProvider;
import com.getcapacitor.Logger;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

/* JADX INFO: loaded from: classes4.dex */
public final class AssetUtil {
    public static final int RESOURCE_ID_ZERO_VALUE = 0;
    private static final String STORAGE_FOLDER = "/capacitorassets";
    private final Context context;

    private AssetUtil(Context context) {
        this.context = context;
    }

    public static AssetUtil getInstance(Context context) {
        return new AssetUtil(context);
    }

    public Uri parse(String path) {
        if (path == null || path.isEmpty()) {
            return Uri.EMPTY;
        }
        if (path.startsWith("res:")) {
            return getUriForResourcePath(path);
        }
        if (path.startsWith("file:///")) {
            return getUriFromPath(path);
        }
        if (path.startsWith("file://")) {
            return getUriFromAsset(path);
        }
        if (path.startsWith("http")) {
            return getUriFromRemote(path);
        }
        if (path.startsWith("content://")) {
            return Uri.parse(path);
        }
        return Uri.EMPTY;
    }

    private Uri getUriFromPath(String path) {
        String absPath = path.replaceFirst("file://", "").replaceFirst("\\?.*$", "");
        File file = new File(absPath);
        if (!file.exists()) {
            Logger.error("File not found: " + file.getAbsolutePath());
            return Uri.EMPTY;
        }
        return getUriFromFile(file);
    }

    private Uri getUriFromAsset(String path) {
        String resPath = path.replaceFirst("file:/", "www").replaceFirst("\\?.*$", "");
        String fileName = resPath.substring(resPath.lastIndexOf(47) + 1);
        File file = getTmpFile(fileName);
        if (file == null) {
            return Uri.EMPTY;
        }
        try {
            AssetManager assets = this.context.getAssets();
            InputStream in = assets.open(resPath);
            FileOutputStream out = new FileOutputStream(file);
            copyFile(in, out);
            return getUriFromFile(file);
        } catch (Exception e) {
            Logger.error("File not found: assets/" + resPath);
            return Uri.EMPTY;
        }
    }

    private Uri getUriForResourcePath(String path) {
        Resources res = this.context.getResources();
        String resPath = path.replaceFirst("res://", "");
        int resId = getResId(resPath);
        if (resId == 0) {
            Logger.error("File not found: " + resPath);
            return Uri.EMPTY;
        }
        return new Uri.Builder().scheme("android.resource").authority(res.getResourcePackageName(resId)).appendPath(res.getResourceTypeName(resId)).appendPath(res.getResourceEntryName(resId)).build();
    }

    private Uri getUriFromRemote(String path) {
        File file = getTmpFile();
        if (file == null) {
            return Uri.EMPTY;
        }
        try {
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            connection.setRequestProperty("Connection", "close");
            connection.setConnectTimeout(5000);
            connection.connect();
            InputStream in = connection.getInputStream();
            FileOutputStream out = new FileOutputStream(file);
            copyFile(in, out);
            return getUriFromFile(file);
        } catch (FileNotFoundException e) {
            Logger.error(Logger.tags("Asset"), "Failed to create new File from HTTP Content", e);
            return Uri.EMPTY;
        } catch (MalformedURLException e2) {
            Logger.error(Logger.tags("Asset"), "Incorrect URL", e2);
            return Uri.EMPTY;
        } catch (IOException e3) {
            Logger.error(Logger.tags("Asset"), "No Input can be created from http Stream", e3);
            return Uri.EMPTY;
        }
    }

    private void copyFile(InputStream in, FileOutputStream out) {
        byte[] buffer = new byte[1024];
        while (true) {
            try {
                int read = in.read(buffer);
                if (read != -1) {
                    out.write(buffer, 0, read);
                } else {
                    out.flush();
                    out.close();
                    return;
                }
            } catch (Exception e) {
                Logger.error("Error copying", e);
                return;
            }
        }
    }

    public int getResId(String resPath) {
        int resId = getResId(this.context.getResources(), resPath);
        if (resId == 0) {
            return getResId(Resources.getSystem(), resPath);
        }
        return resId;
    }

    private int getResId(Resources res, String resPath) {
        String pkgName = getPkgName(res);
        String resName = getBaseName(resPath);
        int resId = res.getIdentifier(resName, "mipmap", pkgName);
        if (resId == 0) {
            resId = res.getIdentifier(resName, "drawable", pkgName);
        }
        if (resId == 0) {
            return res.getIdentifier(resName, "raw", pkgName);
        }
        return resId;
    }

    public Bitmap getIconFromUri(Uri uri) throws IOException {
        InputStream input = this.context.getContentResolver().openInputStream(uri);
        return BitmapFactory.decodeStream(input);
    }

    private String getBaseName(String resPath) {
        String drawable = resPath;
        if (drawable.contains("/")) {
            drawable = drawable.substring(drawable.lastIndexOf(47) + 1);
        }
        if (resPath.contains(".")) {
            return drawable.substring(0, drawable.lastIndexOf(46));
        }
        return drawable;
    }

    private File getTmpFile() {
        return getTmpFile(UUID.randomUUID().toString());
    }

    private File getTmpFile(String name) {
        File dir = this.context.getExternalCacheDir();
        if (dir == null) {
            dir = this.context.getCacheDir();
        }
        if (dir == null) {
            Logger.error(Logger.tags("Asset"), "Missing cache dir", null);
            return null;
        }
        String storage = dir.toString() + STORAGE_FOLDER;
        new File(storage).mkdir();
        return new File(storage, name);
    }

    private Uri getUriFromFile(File file) {
        try {
            String authority = this.context.getPackageName() + ".provider";
            return FileProvider.getUriForFile(this.context, authority, file);
        } catch (IllegalArgumentException e) {
            Logger.error("File not supported by provider", e);
            return Uri.EMPTY;
        }
    }

    private String getPkgName(Resources res) {
        return res == Resources.getSystem() ? "android" : this.context.getPackageName();
    }

    public static int getResourceID(Context context, String resourceName, String dir) {
        return context.getResources().getIdentifier(resourceName, dir, context.getPackageName());
    }

    public static String getResourceBaseName(String resPath) {
        if (resPath == null) {
            return null;
        }
        if (resPath.contains("/")) {
            return resPath.substring(resPath.lastIndexOf(47) + 1);
        }
        if (resPath.contains(".")) {
            return resPath.substring(0, resPath.lastIndexOf(46));
        }
        return resPath;
    }
}
