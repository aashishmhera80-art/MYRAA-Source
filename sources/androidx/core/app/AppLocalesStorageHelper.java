package androidx.core.app;

import android.content.Context;
import android.util.Log;
import android.util.Xml;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

/* JADX INFO: loaded from: classes.dex */
public class AppLocalesStorageHelper {
    static final String APPLICATION_LOCALES_RECORD_FILE = "androidx.appcompat.app.AppCompatDelegate.application_locales_record_file";
    static final boolean DEBUG = false;
    static final String LOCALE_RECORD_ATTRIBUTE_TAG = "application_locales";
    static final String LOCALE_RECORD_FILE_TAG = "locales";
    static final String TAG = "AppLocalesStorageHelper";
    private static final Object sAppLocaleStorageSync = new Object();

    private AppLocalesStorageHelper() {
    }

    /* JADX WARN: Code duplicated, block: B:57:0x0048 A[EXC_TOP_SPLITTER, PHI: r1
      0x0048: PHI (r1v2 'appLocales' java.lang.String) = (r1v0 'appLocales' java.lang.String), (r1v4 'appLocales' java.lang.String) binds: [B:33:0x005b, B:22:0x0046] A[DONT_GENERATE, DONT_INLINE], SYNTHETIC] */
    /* JADX WARN: Not initialized variable reg: 2, insn: 0x006c: IF  (r2 I:??[int, boolean, OBJECT, ARRAY, byte, short, char] A[D('fis' java.io.FileInputStream)]) == (0 ??[int, boolean, OBJECT, ARRAY, byte, short, char])  -> B:46:0x0074 (LINE:94), block:B:41:0x006c */
    public static String readLocales(Context context) {
        String appLocales;
        FileInputStream fis;
        synchronized (sAppLocaleStorageSync) {
            appLocales = "";
            try {
                try {
                    FileInputStream fis2 = context.openFileInput(APPLICATION_LOCALES_RECORD_FILE);
                    try {
                        XmlPullParser parser = Xml.newPullParser();
                        parser.setInput(fis2, "UTF-8");
                        int outerDepth = parser.getDepth();
                        while (true) {
                            int type = parser.next();
                            if (type == 1 || (type == 3 && parser.getDepth() <= outerDepth)) {
                                break;
                            }
                            if (type != 3 && type != 4) {
                                String tagName = parser.getName();
                                if (tagName.equals(LOCALE_RECORD_FILE_TAG)) {
                                    appLocales = parser.getAttributeValue(null, LOCALE_RECORD_ATTRIBUTE_TAG);
                                    break;
                                }
                            }
                        }
                        if (fis2 != null) {
                            try {
                                fis2.close();
                            } catch (IOException e) {
                            }
                        }
                    } catch (IOException | XmlPullParserException e2) {
                        Log.w(TAG, "Reading app Locales : Unable to parse through file :androidx.appcompat.app.AppCompatDelegate.application_locales_record_file");
                        if (fis2 != null) {
                            fis2.close();
                        }
                    }
                    if (appLocales.isEmpty()) {
                        context.deleteFile(APPLICATION_LOCALES_RECORD_FILE);
                    }
                } catch (FileNotFoundException e3) {
                    return "";
                }
            } catch (Throwable th) {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e4) {
                    }
                }
                throw th;
            }
        }
        return appLocales;
    }

    /* JADX WARN: Code duplicated, block: B:44:0x004e A[EXC_TOP_SPLITTER, SYNTHETIC] */
    public static void persistLocales(Context context, String locales) {
        synchronized (sAppLocaleStorageSync) {
            if (locales.equals("")) {
                context.deleteFile(APPLICATION_LOCALES_RECORD_FILE);
                return;
            }
            try {
                FileOutputStream fos = context.openFileOutput(APPLICATION_LOCALES_RECORD_FILE, 0);
                XmlSerializer serializer = Xml.newSerializer();
                try {
                    try {
                        serializer.setOutput(fos, null);
                        serializer.startDocument("UTF-8", true);
                        serializer.startTag(null, LOCALE_RECORD_FILE_TAG);
                        serializer.attribute(null, LOCALE_RECORD_ATTRIBUTE_TAG, locales);
                        serializer.endTag(null, LOCALE_RECORD_FILE_TAG);
                        serializer.endDocument();
                        if (fos != null) {
                            try {
                                fos.close();
                            } catch (IOException e) {
                            }
                        }
                    } catch (Throwable th) {
                        if (fos != null) {
                            try {
                                fos.close();
                            } catch (IOException e2) {
                            }
                        }
                        throw th;
                    }
                } catch (Exception e3) {
                    Log.w(TAG, "Storing App Locales : Failed to persist app-locales in storage ", e3);
                    if (fos != null) {
                        fos.close();
                    }
                }
            } catch (FileNotFoundException e4) {
                Log.w(TAG, String.format("Storing App Locales : FileNotFoundException: Cannot open file %s for writing ", APPLICATION_LOCALES_RECORD_FILE));
            }
        }
    }
}
