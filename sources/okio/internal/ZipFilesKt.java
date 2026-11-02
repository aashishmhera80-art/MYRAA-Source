package okio.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.TuplesKt;
import kotlin.UShort;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.io.CloseableKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;
import kotlin.text.CharsKt;
import kotlin.text.StringsKt;
import okhttp3.internal.ws.WebSocketProtocol;
import okio.BufferedSource;
import okio.FileHandle;
import okio.FileMetadata;
import okio.FileSystem;
import okio.Okio;
import okio.Path;
import okio.ZipFileSystem;

/* JADX INFO: compiled from: ZipFiles.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000j\n\u0000\n\u0002\u0010\b\n\u0002\b\t\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\u001a\"\u0010\u0012\u001a\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00150\u00132\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00150\u0017H\u0002\u001a\u001f\u0010\u0018\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\u0019\u001a\u00020\u00012\u0006\u0010\u001a\u001a\u00020\u0001H\u0002¢\u0006\u0002\u0010\u001b\u001a.\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u00142\u0006\u0010\u001f\u001a\u00020 2\u0014\b\u0002\u0010!\u001a\u000e\u0012\u0004\u0012\u00020\u0015\u0012\u0004\u0012\u00020#0\"H\u0000\u001a\f\u0010$\u001a\u00020\u0015*\u00020%H\u0000\u001a\f\u0010&\u001a\u00020'*\u00020%H\u0002\u001a.\u0010(\u001a\u00020)*\u00020%2\u0006\u0010*\u001a\u00020\u00012\u0018\u0010+\u001a\u0014\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020)0,H\u0002\u001a\u0014\u0010-\u001a\u00020.*\u00020%2\u0006\u0010/\u001a\u00020.H\u0000\u001a\u0018\u00100\u001a\u0004\u0018\u00010.*\u00020%2\b\u0010/\u001a\u0004\u0018\u00010.H\u0002\u001a\u0014\u00101\u001a\u00020'*\u00020%2\u0006\u00102\u001a\u00020'H\u0002\u001a\f\u00103\u001a\u00020)*\u00020%H\u0000\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0003\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0004\u001a\u00020\u0001X\u0080T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0005\u001a\u00020\u0001X\u0080T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0006\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0007\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\b\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\t\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\n\u001a\u00020\u000bX\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\f\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\r\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u0018\u0010\u000e\u001a\u00020\u000f*\u00020\u00018BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\u0010\u0010\u0011¨\u00064"}, d2 = {"BIT_FLAG_ENCRYPTED", "", "BIT_FLAG_UNSUPPORTED_MASK", "CENTRAL_FILE_HEADER_SIGNATURE", "COMPRESSION_METHOD_DEFLATED", "COMPRESSION_METHOD_STORED", "END_OF_CENTRAL_DIRECTORY_SIGNATURE", "HEADER_ID_EXTENDED_TIMESTAMP", "HEADER_ID_ZIP64_EXTENDED_INFO", "LOCAL_FILE_HEADER_SIGNATURE", "MAX_ZIP_ENTRY_AND_ARCHIVE_SIZE", "", "ZIP64_EOCD_RECORD_SIGNATURE", "ZIP64_LOCATOR_SIGNATURE", "hex", "", "getHex", "(I)Ljava/lang/String;", "buildIndex", "", "Lokio/Path;", "Lokio/internal/ZipEntry;", "entries", "", "dosDateTimeToEpochMillis", "date", "time", "(II)Ljava/lang/Long;", "openZip", "Lokio/ZipFileSystem;", "zipPath", "fileSystem", "Lokio/FileSystem;", "predicate", "Lkotlin/Function1;", "", "readEntry", "Lokio/BufferedSource;", "readEocdRecord", "Lokio/internal/EocdRecord;", "readExtra", "", "extraSize", "block", "Lkotlin/Function2;", "readLocalHeader", "Lokio/FileMetadata;", "basicMetadata", "readOrSkipLocalHeader", "readZip64EocdRecord", "regularRecord", "skipLocalHeader", "okio"}, k = 2, mv = {1, 9, 0}, xi = 48)
public final class ZipFilesKt {
    private static final int BIT_FLAG_ENCRYPTED = 1;
    private static final int BIT_FLAG_UNSUPPORTED_MASK = 1;
    private static final int CENTRAL_FILE_HEADER_SIGNATURE = 33639248;
    public static final int COMPRESSION_METHOD_DEFLATED = 8;
    public static final int COMPRESSION_METHOD_STORED = 0;
    private static final int END_OF_CENTRAL_DIRECTORY_SIGNATURE = 101010256;
    private static final int HEADER_ID_EXTENDED_TIMESTAMP = 21589;
    private static final int HEADER_ID_ZIP64_EXTENDED_INFO = 1;
    private static final int LOCAL_FILE_HEADER_SIGNATURE = 67324752;
    private static final long MAX_ZIP_ENTRY_AND_ARCHIVE_SIZE = 4294967295L;
    private static final int ZIP64_EOCD_RECORD_SIGNATURE = 101075792;
    private static final int ZIP64_LOCATOR_SIGNATURE = 117853008;

    public static /* synthetic */ ZipFileSystem openZip$default(Path path, FileSystem fileSystem, Function1 function1, int i, Object obj) throws IOException {
        if ((i & 4) != 0) {
            function1 = new Function1<ZipEntry, Boolean>() { // from class: okio.internal.ZipFilesKt.openZip.1
                @Override // kotlin.jvm.functions.Function1
                public final Boolean invoke(ZipEntry it) {
                    Intrinsics.checkNotNullParameter(it, "it");
                    return true;
                }
            };
        }
        return openZip(path, fileSystem, function1);
    }

    public static final ZipFileSystem openZip(Path zipPath, FileSystem fileSystem, Function1<? super ZipEntry, Boolean> function1) throws IOException {
        BufferedSource source;
        Throwable th;
        Throwable th2;
        Function1<? super ZipEntry, Boolean> predicate = function1;
        Intrinsics.checkNotNullParameter(zipPath, "zipPath");
        Intrinsics.checkNotNullParameter(fileSystem, "fileSystem");
        Intrinsics.checkNotNullParameter(predicate, "predicate");
        FileHandle fileHandleOpenReadOnly = fileSystem.openReadOnly(zipPath);
        try {
            FileHandle fileHandle = fileHandleOpenReadOnly;
            int i = 0;
            long scanOffset = fileHandle.size() - ((long) 22);
            long j = 0;
            if (scanOffset < 0) {
                throw new IOException("not a zip: size=" + fileHandle.size());
            }
            long stopOffset = Math.max(scanOffset - 65536, 0L);
            while (true) {
                BufferedSource source2 = Okio.buffer(fileHandle.source(scanOffset));
                try {
                    long j2 = j;
                    if (source2.readIntLe() == END_OF_CENTRAL_DIRECTORY_SIGNATURE) {
                        long eocdOffset = scanOffset;
                        EocdRecord eocdRecord = readEocdRecord(source2);
                        source = source2;
                        try {
                            String comment = source.readUtf8(eocdRecord.getCommentByteCount());
                            source.close();
                            long zip64LocatorOffset = eocdOffset - ((long) 20);
                            if (zip64LocatorOffset > j2) {
                                BufferedSource bufferedSourceBuffer = Okio.buffer(fileHandle.source(zip64LocatorOffset));
                                try {
                                    BufferedSource zip64LocatorSource = bufferedSourceBuffer;
                                    try {
                                        if (zip64LocatorSource.readIntLe() == ZIP64_LOCATOR_SIGNATURE) {
                                            int diskWithCentralDir = zip64LocatorSource.readIntLe();
                                            long zip64EocdRecordOffset = zip64LocatorSource.readLongLe();
                                            int numDisks = zip64LocatorSource.readIntLe();
                                            if (numDisks != 1 || diskWithCentralDir != 0) {
                                                throw new IOException("unsupported zip: spanned");
                                            }
                                            BufferedSource bufferedSourceBuffer2 = Okio.buffer(fileHandle.source(zip64EocdRecordOffset));
                                            try {
                                                BufferedSource zip64EocdSource = bufferedSourceBuffer2;
                                                int zip64EocdSignature = zip64EocdSource.readIntLe();
                                                try {
                                                    if (zip64EocdSignature != ZIP64_EOCD_RECORD_SIGNATURE) {
                                                        throw new IOException("bad zip: expected " + getHex(ZIP64_EOCD_RECORD_SIGNATURE) + " but was " + getHex(zip64EocdSignature));
                                                    }
                                                    eocdRecord = readZip64EocdRecord(zip64EocdSource, eocdRecord);
                                                    Unit unit = Unit.INSTANCE;
                                                    CloseableKt.closeFinally(bufferedSourceBuffer2, null);
                                                } catch (Throwable th3) {
                                                    th2 = th3;
                                                    try {
                                                        throw th2;
                                                    } catch (Throwable th4) {
                                                        CloseableKt.closeFinally(bufferedSourceBuffer2, th2);
                                                        throw th4;
                                                    }
                                                }
                                            } catch (Throwable th5) {
                                                th2 = th5;
                                            }
                                            try {
                                                throw th;
                                            } catch (Throwable th6) {
                                                CloseableKt.closeFinally(fileHandleOpenReadOnly, th);
                                                throw th6;
                                            }
                                        }
                                        Unit unit2 = Unit.INSTANCE;
                                        CloseableKt.closeFinally(bufferedSourceBuffer, null);
                                    } catch (Throwable th7) {
                                        th = th7;
                                        try {
                                            throw th;
                                        } catch (Throwable th8) {
                                            CloseableKt.closeFinally(bufferedSourceBuffer, th);
                                            throw th8;
                                        }
                                    }
                                } catch (Throwable th9) {
                                    th = th9;
                                }
                            }
                            List entries = new ArrayList();
                            BufferedSource bufferedSourceBuffer3 = Okio.buffer(fileHandle.source(eocdRecord.getCentralDirectoryOffset()));
                            try {
                                BufferedSource source3 = bufferedSourceBuffer3;
                                long i2 = 0;
                                long entryCount = eocdRecord.getEntryCount();
                                while (i2 < entryCount) {
                                    ZipEntry entry = readEntry(source3);
                                    if (entry.getOffset() >= eocdRecord.getCentralDirectoryOffset()) {
                                        throw new IOException("bad zip: local file header offset >= central directory offset");
                                    }
                                    if (predicate.invoke(entry).booleanValue()) {
                                        entries.add(entry);
                                    }
                                    i2++;
                                    source3 = source3;
                                }
                                Unit unit3 = Unit.INSTANCE;
                                CloseableKt.closeFinally(bufferedSourceBuffer3, null);
                                ZipFileSystem zipFileSystem = new ZipFileSystem(zipPath, fileSystem, buildIndex(entries), comment);
                                CloseableKt.closeFinally(fileHandleOpenReadOnly, null);
                                return zipFileSystem;
                            } catch (Throwable th10) {
                                try {
                                    throw th10;
                                } catch (Throwable th11) {
                                    CloseableKt.closeFinally(bufferedSourceBuffer3, th10);
                                    throw th11;
                                }
                            }
                        } catch (Throwable th12) {
                            th = th12;
                        }
                    } else {
                        int i3 = i;
                        source2.close();
                        scanOffset--;
                        if (scanOffset < stopOffset) {
                            throw new IOException("not a zip: end of central directory signature not found");
                        }
                        predicate = function1;
                        j = j2;
                        i = i3;
                    }
                } catch (Throwable th13) {
                    th = th13;
                    source = source2;
                }
                source.close();
                throw th;
            }
        } catch (Throwable th14) {
            throw th14;
        }
    }

    private static final Map<Path, ZipEntry> buildIndex(List<ZipEntry> list) {
        Path root = Path.Companion.get$default(Path.INSTANCE, "/", false, 1, (Object) null);
        Map<Path, ZipEntry> mapMutableMapOf = MapsKt.mutableMapOf(TuplesKt.to(root, new ZipEntry(root, true, null, 0L, 0L, 0L, 0, null, 0L, 508, null)));
        List<ZipEntry> $this$sortedBy$iv = list;
        for (ZipEntry entry : CollectionsKt.sortedWith($this$sortedBy$iv, new Comparator() { // from class: okio.internal.ZipFilesKt$buildIndex$$inlined$sortedBy$1
            /* JADX WARN: Multi-variable type inference failed */
            @Override // java.util.Comparator
            public final int compare(T t, T t2) {
                ZipEntry it = (ZipEntry) t;
                ZipEntry it2 = (ZipEntry) t2;
                return ComparisonsKt.compareValues(it.getCanonicalPath(), it2.getCanonicalPath());
            }
        })) {
            ZipEntry replaced = mapMutableMapOf.put(entry.getCanonicalPath(), entry);
            if (replaced == null) {
                ZipEntry child = entry;
                while (true) {
                    Path parentPath = child.getCanonicalPath().parent();
                    if (parentPath == null) {
                        break;
                    }
                    ZipEntry parentEntry = mapMutableMapOf.get(parentPath);
                    if (parentEntry != null) {
                        parentEntry.getChildren().add(child.getCanonicalPath());
                        break;
                    }
                    ZipEntry parentEntry2 = new ZipEntry(parentPath, true, null, 0L, 0L, 0L, 0, null, 0L, 508, null);
                    mapMutableMapOf.put(parentPath, parentEntry2);
                    parentEntry2.getChildren().add(child.getCanonicalPath());
                    child = parentEntry2;
                }
            }
        }
        return mapMutableMapOf;
    }

    public static final ZipEntry readEntry(final BufferedSource $this$readEntry) throws IOException {
        Intrinsics.checkNotNullParameter($this$readEntry, "<this>");
        int signature = $this$readEntry.readIntLe();
        if (signature != CENTRAL_FILE_HEADER_SIGNATURE) {
            throw new IOException("bad zip: expected " + getHex(CENTRAL_FILE_HEADER_SIGNATURE) + " but was " + getHex(signature));
        }
        $this$readEntry.skip(4L);
        int bitFlag = $this$readEntry.readShortLe() & UShort.MAX_VALUE;
        if ((bitFlag & 1) != 0) {
            throw new IOException("unsupported zip: general purpose bit flag=" + getHex(bitFlag));
        }
        int compressionMethod = $this$readEntry.readShortLe() & UShort.MAX_VALUE;
        int time = $this$readEntry.readShortLe() & UShort.MAX_VALUE;
        int date = $this$readEntry.readShortLe() & UShort.MAX_VALUE;
        Long lastModifiedAtMillis = dosDateTimeToEpochMillis(date, time);
        long crc = ((long) $this$readEntry.readIntLe()) & MAX_ZIP_ENTRY_AND_ARCHIVE_SIZE;
        final Ref.LongRef compressedSize = new Ref.LongRef();
        compressedSize.element = ((long) $this$readEntry.readIntLe()) & MAX_ZIP_ENTRY_AND_ARCHIVE_SIZE;
        final Ref.LongRef size = new Ref.LongRef();
        size.element = ((long) $this$readEntry.readIntLe()) & MAX_ZIP_ENTRY_AND_ARCHIVE_SIZE;
        int nameSize = $this$readEntry.readShortLe() & UShort.MAX_VALUE;
        int extraSize = $this$readEntry.readShortLe() & UShort.MAX_VALUE;
        int commentByteCount = 65535 & $this$readEntry.readShortLe();
        $this$readEntry.skip(8L);
        final Ref.LongRef offset = new Ref.LongRef();
        offset.element = ((long) $this$readEntry.readIntLe()) & MAX_ZIP_ENTRY_AND_ARCHIVE_SIZE;
        String name = $this$readEntry.readUtf8(nameSize);
        if (StringsKt.contains$default((CharSequence) name, (char) 0, false, 2, (Object) null)) {
            throw new IOException("bad zip: filename contains 0x00");
        }
        long result = 0;
        if (size.element == MAX_ZIP_ENTRY_AND_ARCHIVE_SIZE) {
            result = 0 + ((long) 8);
        }
        if (compressedSize.element == MAX_ZIP_ENTRY_AND_ARCHIVE_SIZE) {
            result += (long) 8;
        }
        if (offset.element == MAX_ZIP_ENTRY_AND_ARCHIVE_SIZE) {
            result += (long) 8;
        }
        final Ref.BooleanRef hasZip64Extra = new Ref.BooleanRef();
        final long requiredZip64ExtraSize = result;
        readExtra($this$readEntry, extraSize, new Function2<Integer, Long, Unit>() { // from class: okio.internal.ZipFilesKt.readEntry.1
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(2);
            }

            @Override // kotlin.jvm.functions.Function2
            public /* bridge */ /* synthetic */ Unit invoke(Integer num, Long l) throws IOException {
                invoke(num.intValue(), l.longValue());
                return Unit.INSTANCE;
            }

            public final void invoke(int headerId, long dataSize) throws IOException {
                if (headerId == 1) {
                    if (!hasZip64Extra.element) {
                        hasZip64Extra.element = true;
                        if (dataSize < requiredZip64ExtraSize) {
                            throw new IOException("bad zip: zip64 extra too short");
                        }
                        size.element = size.element == ZipFilesKt.MAX_ZIP_ENTRY_AND_ARCHIVE_SIZE ? $this$readEntry.readLongLe() : size.element;
                        compressedSize.element = compressedSize.element == ZipFilesKt.MAX_ZIP_ENTRY_AND_ARCHIVE_SIZE ? $this$readEntry.readLongLe() : 0L;
                        offset.element = offset.element == ZipFilesKt.MAX_ZIP_ENTRY_AND_ARCHIVE_SIZE ? $this$readEntry.readLongLe() : 0L;
                        return;
                    }
                    throw new IOException("bad zip: zip64 extra repeated");
                }
            }
        });
        if (requiredZip64ExtraSize <= 0 || hasZip64Extra.element) {
            String comment = $this$readEntry.readUtf8(commentByteCount);
            Path canonicalPath = Path.Companion.get$default(Path.INSTANCE, "/", false, 1, (Object) null).resolve(name);
            boolean isDirectory = StringsKt.endsWith$default(name, "/", false, 2, (Object) null);
            return new ZipEntry(canonicalPath, isDirectory, comment, crc, compressedSize.element, size.element, compressionMethod, lastModifiedAtMillis, offset.element);
        }
        throw new IOException("bad zip: zip64 extra required but absent");
    }

    private static final EocdRecord readEocdRecord(BufferedSource $this$readEocdRecord) throws IOException {
        int diskNumber = $this$readEocdRecord.readShortLe() & 65535;
        int diskWithCentralDir = $this$readEocdRecord.readShortLe() & 65535;
        long entryCount = $this$readEocdRecord.readShortLe() & UShort.MAX_VALUE;
        long totalEntryCount = $this$readEocdRecord.readShortLe() & UShort.MAX_VALUE;
        if (entryCount != totalEntryCount || diskNumber != 0 || diskWithCentralDir != 0) {
            throw new IOException("unsupported zip: spanned");
        }
        $this$readEocdRecord.skip(4L);
        long centralDirectoryOffset = MAX_ZIP_ENTRY_AND_ARCHIVE_SIZE & ((long) $this$readEocdRecord.readIntLe());
        int commentByteCount = $this$readEocdRecord.readShortLe() & UShort.MAX_VALUE;
        return new EocdRecord(entryCount, centralDirectoryOffset, commentByteCount);
    }

    private static final EocdRecord readZip64EocdRecord(BufferedSource $this$readZip64EocdRecord, EocdRecord regularRecord) throws IOException {
        $this$readZip64EocdRecord.skip(12L);
        int diskNumber = $this$readZip64EocdRecord.readIntLe();
        int diskWithCentralDirStart = $this$readZip64EocdRecord.readIntLe();
        long entryCount = $this$readZip64EocdRecord.readLongLe();
        long totalEntryCount = $this$readZip64EocdRecord.readLongLe();
        if (entryCount != totalEntryCount || diskNumber != 0 || diskWithCentralDirStart != 0) {
            throw new IOException("unsupported zip: spanned");
        }
        $this$readZip64EocdRecord.skip(8L);
        long centralDirectoryOffset = $this$readZip64EocdRecord.readLongLe();
        return new EocdRecord(entryCount, centralDirectoryOffset, regularRecord.getCommentByteCount());
    }

    private static final void readExtra(BufferedSource $this$readExtra, int extraSize, Function2<? super Integer, ? super Long, Unit> function2) throws IOException {
        long remaining = extraSize;
        while (remaining != 0) {
            if (remaining < 4) {
                throw new IOException("bad zip: truncated header in extra field");
            }
            int headerId = $this$readExtra.readShortLe() & UShort.MAX_VALUE;
            long dataSize = ((long) $this$readExtra.readShortLe()) & WebSocketProtocol.PAYLOAD_SHORT_MAX;
            long remaining2 = remaining - ((long) 4);
            if (remaining2 < dataSize) {
                throw new IOException("bad zip: truncated value in extra field");
            }
            $this$readExtra.require(dataSize);
            long sizeBefore = $this$readExtra.getBuffer().size();
            function2.invoke(Integer.valueOf(headerId), Long.valueOf(dataSize));
            long fieldRemaining = ($this$readExtra.getBuffer().size() + dataSize) - sizeBefore;
            if (fieldRemaining < 0) {
                throw new IOException("unsupported zip: too many bytes processed for " + headerId);
            }
            if (fieldRemaining > 0) {
                $this$readExtra.getBuffer().skip(fieldRemaining);
            }
            remaining = remaining2 - dataSize;
        }
    }

    public static final void skipLocalHeader(BufferedSource $this$skipLocalHeader) throws IOException {
        Intrinsics.checkNotNullParameter($this$skipLocalHeader, "<this>");
        readOrSkipLocalHeader($this$skipLocalHeader, null);
    }

    public static final FileMetadata readLocalHeader(BufferedSource $this$readLocalHeader, FileMetadata basicMetadata) throws IOException {
        Intrinsics.checkNotNullParameter($this$readLocalHeader, "<this>");
        Intrinsics.checkNotNullParameter(basicMetadata, "basicMetadata");
        FileMetadata orSkipLocalHeader = readOrSkipLocalHeader($this$readLocalHeader, basicMetadata);
        Intrinsics.checkNotNull(orSkipLocalHeader);
        return orSkipLocalHeader;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static final FileMetadata readOrSkipLocalHeader(final BufferedSource bufferedSource, FileMetadata fileMetadata) throws IOException {
        final Ref.ObjectRef objectRef = new Ref.ObjectRef();
        objectRef.element = fileMetadata != null ? fileMetadata.getLastModifiedAtMillis() : 0;
        final Ref.ObjectRef objectRef2 = new Ref.ObjectRef();
        final Ref.ObjectRef objectRef3 = new Ref.ObjectRef();
        int intLe = bufferedSource.readIntLe();
        if (intLe == LOCAL_FILE_HEADER_SIGNATURE) {
            bufferedSource.skip(2L);
            int shortLe = bufferedSource.readShortLe() & UShort.MAX_VALUE;
            if ((shortLe & 1) == 0) {
                bufferedSource.skip(18L);
                long shortLe2 = ((long) bufferedSource.readShortLe()) & WebSocketProtocol.PAYLOAD_SHORT_MAX;
                int shortLe3 = 65535 & bufferedSource.readShortLe();
                bufferedSource.skip(shortLe2);
                if (fileMetadata == null) {
                    bufferedSource.skip(shortLe3);
                    return null;
                }
                readExtra(bufferedSource, shortLe3, new Function2<Integer, Long, Unit>() { // from class: okio.internal.ZipFilesKt.readOrSkipLocalHeader.1
                    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                    {
                        super(2);
                    }

                    @Override // kotlin.jvm.functions.Function2
                    public /* bridge */ /* synthetic */ Unit invoke(Integer num, Long l) throws IOException {
                        invoke(num.intValue(), l.longValue());
                        return Unit.INSTANCE;
                    }

                    /* JADX WARN: Type inference failed for: r4v12, types: [T, java.lang.Long] */
                    /* JADX WARN: Type inference failed for: r4v6, types: [T, java.lang.Long] */
                    /* JADX WARN: Type inference failed for: r4v9, types: [T, java.lang.Long] */
                    public final void invoke(int headerId, long dataSize) throws IOException {
                        if (headerId == ZipFilesKt.HEADER_ID_EXTENDED_TIMESTAMP) {
                            if (dataSize < 1) {
                                throw new IOException("bad zip: extended timestamp extra too short");
                            }
                            int flags = bufferedSource.readByte() & 255;
                            boolean hasLastModifiedAtMillis = (flags & 1) == 1;
                            boolean hasLastAccessedAtMillis = (flags & 2) == 2;
                            boolean hasCreatedAtMillis = (flags & 4) == 4;
                            BufferedSource bufferedSource2 = bufferedSource;
                            long result = hasLastModifiedAtMillis ? 1 + 4 : 1L;
                            if (hasLastAccessedAtMillis) {
                                result += 4;
                            }
                            if (hasCreatedAtMillis) {
                                result += 4;
                            }
                            if (dataSize < result) {
                                throw new IOException("bad zip: extended timestamp extra too short");
                            }
                            if (hasLastModifiedAtMillis) {
                                objectRef.element = Long.valueOf(((long) bufferedSource.readIntLe()) * 1000);
                            }
                            if (hasLastAccessedAtMillis) {
                                objectRef2.element = Long.valueOf(((long) bufferedSource.readIntLe()) * 1000);
                            }
                            if (hasCreatedAtMillis) {
                                objectRef3.element = Long.valueOf(((long) bufferedSource.readIntLe()) * 1000);
                            }
                        }
                    }
                });
                return new FileMetadata(fileMetadata.getIsRegularFile(), fileMetadata.getIsDirectory(), null, fileMetadata.getSize(), (Long) objectRef3.element, (Long) objectRef.element, (Long) objectRef2.element, null, 128, null);
            }
            throw new IOException("unsupported zip: general purpose bit flag=" + getHex(shortLe));
        }
        throw new IOException("bad zip: expected " + getHex(LOCAL_FILE_HEADER_SIGNATURE) + " but was " + getHex(intLe));
    }

    private static final Long dosDateTimeToEpochMillis(int date, int time) {
        if (time == -1) {
            return null;
        }
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(14, 0);
        int year = ((date >> 9) & 127) + 1980;
        int month = (date >> 5) & 15;
        int day = date & 31;
        int hour = (time >> 11) & 31;
        int minute = (time >> 5) & 63;
        int second = (time & 31) << 1;
        cal.set(year, month - 1, day, hour, minute, second);
        return Long.valueOf(cal.getTime().getTime());
    }

    private static final String getHex(int $this$hex) {
        StringBuilder sbAppend = new StringBuilder().append("0x");
        String string = Integer.toString($this$hex, CharsKt.checkRadix(16));
        Intrinsics.checkNotNullExpressionValue(string, "toString(this, checkRadix(radix))");
        return sbAppend.append(string).toString();
    }
}
