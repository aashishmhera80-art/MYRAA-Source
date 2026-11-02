package okhttp3.internal.publicsuffix;

import androidx.webkit.ProxyConfig;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.IDN;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.io.CloseableKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;
import kotlin.sequences.SequencesKt;
import kotlin.text.StringsKt;
import okhttp3.internal.Util;
import okhttp3.internal.platform.Platform;
import okio.BufferedSource;
import okio.GzipSource;
import okio.Okio;

/* JADX INFO: compiled from: PublicSuffixDatabase.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0005\u0018\u0000 \u00152\u00020\u0001:\u0001\u0015B\u0005¢\u0006\u0002\u0010\u0002J\u001c\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000b2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\f0\u000bH\u0002J\u0010\u0010\u000e\u001a\u0004\u0018\u00010\f2\u0006\u0010\u000f\u001a\u00020\fJ\b\u0010\u0010\u001a\u00020\u0011H\u0002J\b\u0010\u0012\u001a\u00020\u0011H\u0002J\u0016\u0010\u0013\u001a\u00020\u00112\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0006J\u0016\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\f0\u000b2\u0006\u0010\u000f\u001a\u00020\fH\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0016"}, d2 = {"Lokhttp3/internal/publicsuffix/PublicSuffixDatabase;", "", "()V", "listRead", "Ljava/util/concurrent/atomic/AtomicBoolean;", "publicSuffixExceptionListBytes", "", "publicSuffixListBytes", "readCompleteLatch", "Ljava/util/concurrent/CountDownLatch;", "findMatchingRule", "", "", "domainLabels", "getEffectiveTldPlusOne", "domain", "readTheList", "", "readTheListUninterruptibly", "setListBytes", "splitDomain", "Companion", "okhttp"}, k = 1, mv = {1, 8, 0}, xi = 48)
public final class PublicSuffixDatabase {
    private static final char EXCEPTION_MARKER = '!';
    public static final String PUBLIC_SUFFIX_RESOURCE = "publicsuffixes.gz";
    private byte[] publicSuffixExceptionListBytes;
    private byte[] publicSuffixListBytes;

    /* JADX INFO: renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private static final byte[] WILDCARD_LABEL = {42};
    private static final List<String> PREVAILING_RULE = CollectionsKt.listOf(ProxyConfig.MATCH_ALL_SCHEMES);
    private static final PublicSuffixDatabase instance = new PublicSuffixDatabase();
    private final AtomicBoolean listRead = new AtomicBoolean(false);
    private final CountDownLatch readCompleteLatch = new CountDownLatch(1);

    public final String getEffectiveTldPlusOne(String domain) {
        int firstLabelOffset;
        Intrinsics.checkNotNullParameter(domain, "domain");
        String unicodeDomain = IDN.toUnicode(domain);
        Intrinsics.checkNotNullExpressionValue(unicodeDomain, "unicodeDomain");
        List<String> listSplitDomain = splitDomain(unicodeDomain);
        List<String> listFindMatchingRule = findMatchingRule(listSplitDomain);
        if (listSplitDomain.size() == listFindMatchingRule.size() && listFindMatchingRule.get(0).charAt(0) != '!') {
            return null;
        }
        if (listFindMatchingRule.get(0).charAt(0) == '!') {
            firstLabelOffset = listSplitDomain.size() - listFindMatchingRule.size();
        } else {
            firstLabelOffset = listSplitDomain.size() - (listFindMatchingRule.size() + 1);
        }
        return SequencesKt.joinToString$default(SequencesKt.drop(CollectionsKt.asSequence(splitDomain(domain)), firstLabelOffset), ".", null, null, 0, null, null, 62, null);
    }

    private final List<String> splitDomain(String domain) {
        List<String> listSplit$default = StringsKt.split$default((CharSequence) domain, new char[]{'.'}, false, 0, 6, (Object) null);
        if (Intrinsics.areEqual(CollectionsKt.last((List) listSplit$default), "")) {
            return CollectionsKt.dropLast(listSplit$default, 1);
        }
        return listSplit$default;
    }

    private final List<String> findMatchingRule(List<String> domainLabels) {
        List<String> listEmptyList;
        List<String> listEmptyList2;
        if (!this.listRead.get() && this.listRead.compareAndSet(false, true)) {
            readTheListUninterruptibly();
        } else {
            try {
                this.readCompleteLatch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        if (!(this.publicSuffixListBytes != null)) {
            throw new IllegalStateException("Unable to load publicsuffixes.gz resource from the classpath.".toString());
        }
        int size = domainLabels.size();
        byte[][] domainLabelsUtf8Bytes = new byte[size][];
        for (int i = 0; i < size; i++) {
            String str = domainLabels.get(i);
            Charset UTF_8 = StandardCharsets.UTF_8;
            Intrinsics.checkNotNullExpressionValue(UTF_8, "UTF_8");
            byte[] bytes = str.getBytes(UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes, "this as java.lang.String).getBytes(charset)");
            domainLabelsUtf8Bytes[i] = bytes;
        }
        String exactMatch = null;
        int length = domainLabelsUtf8Bytes.length;
        for (int i2 = 0; i2 < length; i2++) {
            Companion companion = INSTANCE;
            byte[] bArr = this.publicSuffixListBytes;
            if (bArr == null) {
                Intrinsics.throwUninitializedPropertyAccessException("publicSuffixListBytes");
                bArr = null;
            }
            String rule = companion.binarySearch(bArr, domainLabelsUtf8Bytes, i2);
            if (rule != null) {
                exactMatch = rule;
                break;
            }
        }
        String wildcardMatch = null;
        if (domainLabelsUtf8Bytes.length > 1) {
            byte[][] labelsWithWildcard = (byte[][]) domainLabelsUtf8Bytes.clone();
            int length2 = labelsWithWildcard.length - 1;
            for (int labelIndex = 0; labelIndex < length2; labelIndex++) {
                labelsWithWildcard[labelIndex] = WILDCARD_LABEL;
                Companion companion2 = INSTANCE;
                byte[] bArr2 = this.publicSuffixListBytes;
                if (bArr2 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("publicSuffixListBytes");
                    bArr2 = null;
                }
                String rule2 = companion2.binarySearch(bArr2, labelsWithWildcard, labelIndex);
                if (rule2 != null) {
                    wildcardMatch = rule2;
                    break;
                }
            }
        }
        String exception = null;
        if (wildcardMatch != null) {
            int length3 = domainLabelsUtf8Bytes.length - 1;
            for (int labelIndex2 = 0; labelIndex2 < length3; labelIndex2++) {
                Companion companion3 = INSTANCE;
                byte[] bArr3 = this.publicSuffixExceptionListBytes;
                if (bArr3 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("publicSuffixExceptionListBytes");
                    bArr3 = null;
                }
                String rule3 = companion3.binarySearch(bArr3, domainLabelsUtf8Bytes, labelIndex2);
                if (rule3 != null) {
                    exception = rule3;
                    break;
                }
            }
        }
        if (exception != null) {
            return StringsKt.split$default((CharSequence) (EXCEPTION_MARKER + exception), new char[]{'.'}, false, 0, 6, (Object) null);
        }
        if (exactMatch == null && wildcardMatch == null) {
            return PREVAILING_RULE;
        }
        if (exactMatch == null || (listEmptyList = StringsKt.split$default((CharSequence) exactMatch, new char[]{'.'}, false, 0, 6, (Object) null)) == null) {
            listEmptyList = CollectionsKt.emptyList();
        }
        if (wildcardMatch == null || (listEmptyList2 = StringsKt.split$default((CharSequence) wildcardMatch, new char[]{'.'}, false, 0, 6, (Object) null)) == null) {
            listEmptyList2 = CollectionsKt.emptyList();
        }
        if (listEmptyList.size() > listEmptyList2.size()) {
            return listEmptyList;
        }
        return listEmptyList2;
    }

    private final void readTheListUninterruptibly() {
        boolean interrupted = false;
        while (true) {
            try {
                try {
                    try {
                        readTheList();
                        break;
                    } catch (IOException e) {
                        Platform.INSTANCE.get().log("Failed to read public suffix list", 5, e);
                        if (interrupted) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                        return;
                    }
                } catch (InterruptedIOException e2) {
                    Thread.interrupted();
                    interrupted = true;
                }
            } catch (Throwable th) {
                if (interrupted) {
                    Thread.currentThread().interrupt();
                }
                throw th;
            }
        }
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r7v1, types: [T, byte[]] */
    /* JADX WARN: Type inference failed for: r8v1, types: [T, byte[]] */
    private final void readTheList() throws IOException {
        try {
            Ref.ObjectRef publicSuffixListBytes = new Ref.ObjectRef();
            Ref.ObjectRef publicSuffixExceptionListBytes = new Ref.ObjectRef();
            InputStream resource = PublicSuffixDatabase.class.getResourceAsStream(PUBLIC_SUFFIX_RESOURCE);
            if (resource == null) {
                this.readCompleteLatch.countDown();
                return;
            }
            BufferedSource bufferedSourceBuffer = Okio.buffer(new GzipSource(Okio.source(resource)));
            try {
                BufferedSource bufferedSource = bufferedSourceBuffer;
                int totalBytes = bufferedSource.readInt();
                publicSuffixListBytes.element = bufferedSource.readByteArray(totalBytes);
                int totalExceptionBytes = bufferedSource.readInt();
                publicSuffixExceptionListBytes.element = bufferedSource.readByteArray(totalExceptionBytes);
                Unit unit = Unit.INSTANCE;
                CloseableKt.closeFinally(bufferedSourceBuffer, null);
                synchronized (this) {
                    T t = publicSuffixListBytes.element;
                    Intrinsics.checkNotNull(t);
                    this.publicSuffixListBytes = (byte[]) t;
                    T t2 = publicSuffixExceptionListBytes.element;
                    Intrinsics.checkNotNull(t2);
                    this.publicSuffixExceptionListBytes = (byte[]) t2;
                    Unit unit2 = Unit.INSTANCE;
                }
                this.readCompleteLatch.countDown();
            } catch (Throwable th) {
                try {
                    throw th;
                } catch (Throwable th2) {
                    CloseableKt.closeFinally(bufferedSourceBuffer, th);
                    throw th2;
                }
            }
        } catch (Throwable th3) {
            this.readCompleteLatch.countDown();
            throw th3;
        }
    }

    public final void setListBytes(byte[] publicSuffixListBytes, byte[] publicSuffixExceptionListBytes) {
        Intrinsics.checkNotNullParameter(publicSuffixListBytes, "publicSuffixListBytes");
        Intrinsics.checkNotNullParameter(publicSuffixExceptionListBytes, "publicSuffixExceptionListBytes");
        this.publicSuffixListBytes = publicSuffixListBytes;
        this.publicSuffixExceptionListBytes = publicSuffixExceptionListBytes;
        this.listRead.set(true);
        this.readCompleteLatch.countDown();
    }

    /* JADX INFO: compiled from: PublicSuffixDatabase.kt */
    @Metadata(d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\f\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0012\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0006\u0010\r\u001a\u00020\fJ)\u0010\u000e\u001a\u0004\u0018\u00010\u0007*\u00020\n2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\n0\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0002¢\u0006\u0002\u0010\u0013R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0007X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0014"}, d2 = {"Lokhttp3/internal/publicsuffix/PublicSuffixDatabase$Companion;", "", "()V", "EXCEPTION_MARKER", "", "PREVAILING_RULE", "", "", "PUBLIC_SUFFIX_RESOURCE", "WILDCARD_LABEL", "", "instance", "Lokhttp3/internal/publicsuffix/PublicSuffixDatabase;", "get", "binarySearch", "labels", "", "labelIndex", "", "([B[[BI)Ljava/lang/String;", "okhttp"}, k = 1, mv = {1, 8, 0}, xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final PublicSuffixDatabase get() {
            return PublicSuffixDatabase.instance;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final String binarySearch(byte[] $this$binarySearch, byte[][] labels, int labelIndex) {
            int byte0;
            int compareResult;
            int low = 0;
            int high = $this$binarySearch.length;
            while (low < high) {
                int mid = (low + high) / 2;
                while (mid > -1 && $this$binarySearch[mid] != 10) {
                    mid--;
                }
                int mid2 = mid + 1;
                int end = 1;
                while ($this$binarySearch[mid2 + end] != 10) {
                    end++;
                }
                int publicSuffixLength = (mid2 + end) - mid2;
                int currentLabelIndex = labelIndex;
                int currentLabelByteIndex = 0;
                int publicSuffixByteIndex = 0;
                boolean expectDot = false;
                while (true) {
                    if (!expectDot) {
                        byte0 = Util.and(labels[currentLabelIndex][currentLabelByteIndex], 255);
                    } else {
                        byte0 = 46;
                        expectDot = false;
                    }
                    int byte1 = Util.and($this$binarySearch[mid2 + publicSuffixByteIndex], 255);
                    compareResult = byte0 - byte1;
                    if (compareResult != 0) {
                        break;
                    }
                    publicSuffixByteIndex++;
                    currentLabelByteIndex++;
                    if (publicSuffixByteIndex == publicSuffixLength) {
                        break;
                    }
                    if (labels[currentLabelIndex].length == currentLabelByteIndex) {
                        if (currentLabelIndex == labels.length - 1) {
                            break;
                        }
                        currentLabelIndex++;
                        currentLabelByteIndex = -1;
                        expectDot = true;
                    }
                }
                if (compareResult < 0) {
                    high = mid2 - 1;
                } else if (compareResult > 0) {
                    low = mid2 + end + 1;
                } else {
                    int publicSuffixBytesLeft = publicSuffixLength - publicSuffixByteIndex;
                    int labelBytesLeft = labels[currentLabelIndex].length - currentLabelByteIndex;
                    int i = currentLabelIndex + 1;
                    int length = labels.length;
                    while (i < length) {
                        labelBytesLeft += labels[i].length;
                        i++;
                        low = low;
                    }
                    int low2 = low;
                    if (labelBytesLeft < publicSuffixBytesLeft) {
                        high = mid2 - 1;
                        low = low2;
                    } else if (labelBytesLeft > publicSuffixBytesLeft) {
                        low = mid2 + end + 1;
                    } else {
                        Charset UTF_8 = StandardCharsets.UTF_8;
                        Intrinsics.checkNotNullExpressionValue(UTF_8, "UTF_8");
                        String match = new String($this$binarySearch, mid2, publicSuffixLength, UTF_8);
                        return match;
                    }
                }
            }
            return null;
        }
    }
}
