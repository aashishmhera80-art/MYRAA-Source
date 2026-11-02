package androidx.core.provider;

import android.content.ContentProviderClient;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.fonts.Font;
import android.net.Uri;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.RemoteException;
import android.util.Log;
import androidx.collection.LruCache;
import androidx.core.content.res.FontResourcesParserCompat;
import androidx.core.graphics.TypefaceCompat;
import androidx.tracing.Trace;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
class FontProvider {
    private static final LruCache<ProviderCacheKey, ProviderInfo> sProviderCache = new LruCache<>(2);
    private static final Comparator<byte[]> sByteArrayComparator = new Comparator() { // from class: androidx.core.provider.FontProvider$$ExternalSyntheticLambda0
        @Override // java.util.Comparator
        public final int compare(Object obj, Object obj2) {
            return FontProvider.lambda$static$0((byte[]) obj, (byte[]) obj2);
        }
    };

    private FontProvider() {
    }

    /* JADX WARN: Code duplicated, block: B:13:0x0040 A[Catch: all -> 0x006f, TryCatch #0 {all -> 0x006f, blocks: (B:3:0x0005, B:4:0x000b, B:6:0x0012, B:8:0x001f, B:10:0x0029, B:12:0x002f, B:19:0x0064, B:13:0x0040, B:15:0x004f, B:18:0x005b, B:20:0x0067), top: B:26:0x0005 }] */
    /* JADX WARN: Code duplicated, block: B:18:0x005b A[Catch: all -> 0x006f, TRY_ENTER, TryCatch #0 {all -> 0x006f, blocks: (B:3:0x0005, B:4:0x000b, B:6:0x0012, B:8:0x001f, B:10:0x0029, B:12:0x002f, B:19:0x0064, B:13:0x0040, B:15:0x004f, B:18:0x005b, B:20:0x0067), top: B:26:0x0005 }] */
    /* JADX WARN: Code duplicated, block: B:28:0x004f A[SYNTHETIC] */
    static FontsContractCompat.FontFamilyResult getFontFamilyResult(Context context, List<FontRequest> requests, CancellationSignal cancellationSignal) throws PackageManager.NameNotFoundException {
        ProviderInfo providerInfo;
        String systemFont;
        Typeface typeface;
        Trace.beginSection("FontProvider.getFontFamilyResult");
        try {
            ArrayList<FontsContractCompat.FontInfo[]> queryResults = new ArrayList<>();
            for (int i = 0; i < requests.size(); i++) {
                FontRequest request = requests.get(i);
                if (Build.VERSION.SDK_INT >= 31 && (typeface = TypefaceCompat.getSystemFontFamily((systemFont = request.getSystemFont()))) != null) {
                    Font font = TypefaceCompat.guessPrimaryFont(typeface);
                    if (font != null) {
                        queryResults.add(new FontsContractCompat.FontInfo[]{new FontsContractCompat.FontInfo(systemFont, request.getVariationSettings())});
                    } else {
                        providerInfo = getProvider(context.getPackageManager(), request, context.getResources());
                        if (providerInfo == null) {
                            return FontsContractCompat.FontFamilyResult.create(1, (FontsContractCompat.FontInfo[]) null);
                        }
                        FontsContractCompat.FontInfo[] fonts = query(context, request, providerInfo.authority, cancellationSignal);
                        queryResults.add(fonts);
                    }
                } else {
                    providerInfo = getProvider(context.getPackageManager(), request, context.getResources());
                    if (providerInfo == null) {
                        return FontsContractCompat.FontFamilyResult.create(1, (FontsContractCompat.FontInfo[]) null);
                    }
                    FontsContractCompat.FontInfo[] fonts2 = query(context, request, providerInfo.authority, cancellationSignal);
                    queryResults.add(fonts2);
                }
            }
            return FontsContractCompat.FontFamilyResult.create(0, queryResults);
        } finally {
            Trace.endSection();
        }
    }

    private static class ProviderCacheKey {
        String mAuthority;
        List<List<byte[]>> mCertificates;
        String mPackageName;

        ProviderCacheKey(String authority, String packageName, List<List<byte[]>> certificates) {
            this.mAuthority = authority;
            this.mPackageName = packageName;
            this.mCertificates = certificates;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof ProviderCacheKey)) {
                return false;
            }
            ProviderCacheKey that = (ProviderCacheKey) o;
            return Objects.equals(this.mAuthority, that.mAuthority) && Objects.equals(this.mPackageName, that.mPackageName) && Objects.equals(this.mCertificates, that.mCertificates);
        }

        public int hashCode() {
            return Objects.hash(this.mAuthority, this.mPackageName, this.mCertificates);
        }
    }

    static void clearProviderCache() {
        sProviderCache.evictAll();
    }

    static ProviderInfo getProvider(PackageManager packageManager, FontRequest request, Resources resources) throws PackageManager.NameNotFoundException {
        Trace.beginSection("FontProvider.getProvider");
        try {
            List<List<byte[]>> requestCertificatesList = getCertificates(request, resources);
            ProviderCacheKey cacheKey = new ProviderCacheKey(request.getProviderAuthority(), request.getProviderPackage(), requestCertificatesList);
            ProviderInfo cachedPackageInfo = sProviderCache.get(cacheKey);
            if (cachedPackageInfo != null) {
                Trace.endSection();
                return cachedPackageInfo;
            }
            String providerAuthority = request.getProviderAuthority();
            ProviderInfo info = packageManager.resolveContentProvider(providerAuthority, 0);
            if (info == null) {
                throw new PackageManager.NameNotFoundException("No package found for authority: " + providerAuthority);
            }
            if (!info.packageName.equals(request.getProviderPackage())) {
                throw new PackageManager.NameNotFoundException("Found content provider " + providerAuthority + ", but package was not " + request.getProviderPackage());
            }
            PackageInfo packageInfo = packageManager.getPackageInfo(info.packageName, 64);
            List<byte[]> signatures = convertToByteArrayList(packageInfo.signatures);
            Collections.sort(signatures, sByteArrayComparator);
            for (int i = 0; i < requestCertificatesList.size(); i++) {
                List<byte[]> requestSignatures = new ArrayList<>(requestCertificatesList.get(i));
                Collections.sort(requestSignatures, sByteArrayComparator);
                if (equalsByteArrayList(signatures, requestSignatures)) {
                    sProviderCache.put(cacheKey, info);
                    Trace.endSection();
                    return info;
                }
            }
            Trace.endSection();
            return null;
        } catch (Throwable th) {
            Trace.endSection();
            throw th;
        }
    }

    static FontsContractCompat.FontInfo[] query(Context context, FontRequest request, String authority, CancellationSignal cancellationSignal) {
        ContentQueryWrapper queryWrapper;
        int resultCode;
        Uri fileUri;
        int italicColumnIndex;
        Trace.beginSection("FontProvider.query");
        try {
            ArrayList<FontsContractCompat.FontInfo> result = new ArrayList<>();
            Uri uri = new Uri.Builder().scheme("content").authority(authority).build();
            Uri fileBaseUri = new Uri.Builder().scheme("content").authority(authority).appendPath("file").build();
            Cursor cursor = null;
            ContentQueryWrapper queryWrapper2 = ContentQueryWrapper.make(context, uri);
            try {
                String[] projection = {"_id", FontsContractCompat.Columns.FILE_ID, FontsContractCompat.Columns.TTC_INDEX, FontsContractCompat.Columns.VARIATION_SETTINGS, FontsContractCompat.Columns.WEIGHT, FontsContractCompat.Columns.ITALIC, FontsContractCompat.Columns.RESULT_CODE};
                Trace.beginSection("ContentQueryWrapper.query");
                queryWrapper = queryWrapper2;
                try {
                    try {
                        Cursor cursor2 = queryWrapper.query(uri, projection, "query = ?", new String[]{request.getQuery()}, null, cancellationSignal);
                        Trace.endSection();
                        if (cursor2 != null && cursor2.getCount() > 0) {
                            int resultCodeColumnIndex = cursor2.getColumnIndex(FontsContractCompat.Columns.RESULT_CODE);
                            result = new ArrayList<>();
                            int idColumnIndex = cursor2.getColumnIndex("_id");
                            int fileIdColumnIndex = cursor2.getColumnIndex(FontsContractCompat.Columns.FILE_ID);
                            int ttcIndexColumnIndex = cursor2.getColumnIndex(FontsContractCompat.Columns.TTC_INDEX);
                            int weightColumnIndex = cursor2.getColumnIndex(FontsContractCompat.Columns.WEIGHT);
                            int weight = cursor2.getColumnIndex(FontsContractCompat.Columns.ITALIC);
                            while (cursor2.moveToNext()) {
                                if (resultCodeColumnIndex != -1) {
                                    resultCode = cursor2.getInt(resultCodeColumnIndex);
                                } else {
                                    resultCode = 0;
                                }
                                int ttcIndex = ttcIndexColumnIndex != -1 ? cursor2.getInt(ttcIndexColumnIndex) : 0;
                                if (fileIdColumnIndex == -1) {
                                    long id = cursor2.getLong(idColumnIndex);
                                    Uri fileUri2 = ContentUris.withAppendedId(uri, id);
                                    fileUri = fileUri2;
                                } else {
                                    long id2 = cursor2.getLong(fileIdColumnIndex);
                                    fileUri = ContentUris.withAppendedId(fileBaseUri, id2);
                                }
                                int weight2 = weightColumnIndex != -1 ? cursor2.getInt(weightColumnIndex) : 400;
                                int resultCodeColumnIndex2 = resultCodeColumnIndex;
                                int resultCodeColumnIndex3 = weight;
                                if (resultCodeColumnIndex3 != -1) {
                                    italicColumnIndex = resultCodeColumnIndex3;
                                    boolean italic = cursor2.getInt(resultCodeColumnIndex3) == 1;
                                    result.add(FontsContractCompat.FontInfo.create(fileUri, ttcIndex, weight2, italic, resultCode));
                                    weight = italicColumnIndex;
                                    resultCodeColumnIndex = resultCodeColumnIndex2;
                                } else {
                                    italicColumnIndex = resultCodeColumnIndex3;
                                }
                                result.add(FontsContractCompat.FontInfo.create(fileUri, ttcIndex, weight2, italic, resultCode));
                                weight = italicColumnIndex;
                                resultCodeColumnIndex = resultCodeColumnIndex2;
                            }
                        }
                        if (cursor2 != null) {
                            cursor2.close();
                        }
                        queryWrapper.close();
                        return (FontsContractCompat.FontInfo[]) result.toArray(new FontsContractCompat.FontInfo[0]);
                    } catch (Throwable th) {
                        th = th;
                        if (0 != 0) {
                            cursor.close();
                        }
                        queryWrapper.close();
                        throw th;
                    }
                } finally {
                    Trace.endSection();
                }
            } catch (Throwable th2) {
                th = th2;
                queryWrapper = queryWrapper2;
            }
        } catch (Throwable th3) {
            Trace.endSection();
            throw th3;
        }
    }

    private static List<List<byte[]>> getCertificates(FontRequest request, Resources resources) {
        if (request.getCertificates() != null) {
            return request.getCertificates();
        }
        int resourceId = request.getCertificatesArrayResId();
        return FontResourcesParserCompat.readCerts(resources, resourceId);
    }

    static /* synthetic */ int lambda$static$0(byte[] l, byte[] r) {
        if (l.length != r.length) {
            return l.length - r.length;
        }
        for (int i = 0; i < l.length; i++) {
            if (l[i] != r[i]) {
                return l[i] - r[i];
            }
        }
        return 0;
    }

    private static boolean equalsByteArrayList(List<byte[]> signatures, List<byte[]> requestSignatures) {
        if (signatures.size() != requestSignatures.size()) {
            return false;
        }
        for (int i = 0; i < signatures.size(); i++) {
            if (!Arrays.equals(signatures.get(i), requestSignatures.get(i))) {
                return false;
            }
        }
        return true;
    }

    private static List<byte[]> convertToByteArrayList(Signature[] signatures) {
        List<byte[]> shaList = new ArrayList<>();
        for (Signature signature : signatures) {
            shaList.add(signature.toByteArray());
        }
        return shaList;
    }

    private interface ContentQueryWrapper {
        void close();

        Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2, CancellationSignal cancellationSignal);

        static ContentQueryWrapper make(Context context, Uri uri) {
            return new ContentQueryWrapperApi24Impl(context, uri);
        }
    }

    private static class ContentQueryWrapperApi16Impl implements ContentQueryWrapper {
        private final ContentProviderClient mClient;

        ContentQueryWrapperApi16Impl(Context context, Uri uri) {
            this.mClient = context.getContentResolver().acquireUnstableContentProviderClient(uri);
        }

        @Override // androidx.core.provider.FontProvider.ContentQueryWrapper
        public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, CancellationSignal cancellationSignal) {
            RemoteException e;
            if (this.mClient == null) {
                return null;
            }
            try {
                try {
                    return this.mClient.query(uri, projection, selection, selectionArgs, sortOrder, cancellationSignal);
                } catch (RemoteException e2) {
                    e = e2;
                    Log.w("FontsProvider", "Unable to query the content provider", e);
                    return null;
                }
            } catch (RemoteException e3) {
                e = e3;
            }
        }

        @Override // androidx.core.provider.FontProvider.ContentQueryWrapper
        public void close() {
            if (this.mClient != null) {
                this.mClient.release();
            }
        }
    }

    private static class ContentQueryWrapperApi24Impl implements ContentQueryWrapper {
        private final ContentProviderClient mClient;

        ContentQueryWrapperApi24Impl(Context context, Uri uri) {
            this.mClient = context.getContentResolver().acquireUnstableContentProviderClient(uri);
        }

        @Override // androidx.core.provider.FontProvider.ContentQueryWrapper
        public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, CancellationSignal cancellationSignal) {
            RemoteException e;
            if (this.mClient == null) {
                return null;
            }
            try {
                try {
                    return this.mClient.query(uri, projection, selection, selectionArgs, sortOrder, cancellationSignal);
                } catch (RemoteException e2) {
                    e = e2;
                    Log.w("FontsProvider", "Unable to query the content provider", e);
                    return null;
                }
            } catch (RemoteException e3) {
                e = e3;
            }
        }

        @Override // androidx.core.provider.FontProvider.ContentQueryWrapper
        public void close() {
            if (this.mClient != null) {
                this.mClient.close();
            }
        }
    }
}
