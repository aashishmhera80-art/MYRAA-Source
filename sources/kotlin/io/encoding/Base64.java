package kotlin.io.encoding;

import androidx.core.provider.FontsContractCompat;
import androidx.webkit.Profile;
import java.io.IOException;
import java.nio.charset.Charset;
import kotlin.Metadata;
import kotlin.UByte;
import kotlin.collections.AbstractList;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.CharsKt;
import kotlin.text.Charsets;
import okio.Utf8;

/* JADX INFO: compiled from: Base64.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u0002\n\u0002\u0010\r\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0016\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\r\b\u0017\u0018\u0000 <2\u00020\u0001:\u0002<=B\u001f\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0002\u0010\u0007J\u0015\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010H\u0000¢\u0006\u0002\b\u0011J%\u0010\u0012\u001a\u00020\u00102\u0006\u0010\u000f\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0015H\u0000¢\u0006\u0002\b\u0017J \u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u00152\u0006\u0010\u001b\u001a\u00020\u00152\u0006\u0010\u001c\u001a\u00020\u0015H\u0002J\u0010\u0010\u001d\u001a\u00020\u00192\u0006\u0010\u001e\u001a\u00020\u0015H\u0002J%\u0010\u001f\u001a\u00020\u00192\u0006\u0010 \u001a\u00020\u00152\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0015H\u0000¢\u0006\u0002\b!J\"\u0010\"\u001a\u00020\u00102\u0006\u0010\u000f\u001a\u00020\u00102\b\b\u0002\u0010\u0014\u001a\u00020\u00152\b\b\u0002\u0010\u0016\u001a\u00020\u0015J\"\u0010\"\u001a\u00020\u00102\u0006\u0010\u000f\u001a\u00020\u00132\b\b\u0002\u0010\u0014\u001a\u00020\u00152\b\b\u0002\u0010\u0016\u001a\u00020\u0015J0\u0010#\u001a\u00020\u00152\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010$\u001a\u00020\u00102\u0006\u0010\u001b\u001a\u00020\u00152\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0015H\u0002J4\u0010%\u001a\u00020\u00152\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010$\u001a\u00020\u00102\b\b\u0002\u0010\u001b\u001a\u00020\u00152\b\b\u0002\u0010\u0014\u001a\u00020\u00152\b\b\u0002\u0010\u0016\u001a\u00020\u0015J4\u0010%\u001a\u00020\u00152\u0006\u0010\u000f\u001a\u00020\u00132\u0006\u0010$\u001a\u00020\u00102\b\b\u0002\u0010\u001b\u001a\u00020\u00152\b\b\u0002\u0010\u0014\u001a\u00020\u00152\b\b\u0002\u0010\u0016\u001a\u00020\u0015J%\u0010&\u001a\u00020\u00152\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0015H\u0000¢\u0006\u0002\b'J\"\u0010(\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\b\b\u0002\u0010\u0014\u001a\u00020\u00152\b\b\u0002\u0010\u0016\u001a\u00020\u0015J4\u0010)\u001a\u00020\u00152\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010$\u001a\u00020\u00102\b\b\u0002\u0010\u001b\u001a\u00020\u00152\b\b\u0002\u0010\u0014\u001a\u00020\u00152\b\b\u0002\u0010\u0016\u001a\u00020\u0015J5\u0010*\u001a\u00020\u00152\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010$\u001a\u00020\u00102\u0006\u0010\u001b\u001a\u00020\u00152\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0015H\u0000¢\u0006\u0002\b+J\u0015\u0010,\u001a\u00020\u00152\u0006\u0010 \u001a\u00020\u0015H\u0000¢\u0006\u0002\b-J=\u0010.\u001a\u0002H/\"\f\b\u0000\u0010/*\u000600j\u0002`12\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010$\u001a\u0002H/2\b\b\u0002\u0010\u0014\u001a\u00020\u00152\b\b\u0002\u0010\u0016\u001a\u00020\u0015¢\u0006\u0002\u00102J\"\u00103\u001a\u00020\u00102\u0006\u0010\u000f\u001a\u00020\u00102\b\b\u0002\u0010\u0014\u001a\u00020\u00152\b\b\u0002\u0010\u0016\u001a\u00020\u0015J%\u00104\u001a\u00020\u00102\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0015H\u0000¢\u0006\u0002\b5J(\u00106\u001a\u00020\u00152\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u001e\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00152\u0006\u00107\u001a\u00020\u0015H\u0002J\b\u00108\u001a\u00020\u0003H\u0002J \u00109\u001a\u00020\u00152\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0015H\u0002J\u0010\u0010:\u001a\u00020\u00002\u0006\u0010;\u001a\u00020\u0006H\u0007R\u0014\u0010\u0004\u001a\u00020\u0003X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0014\u0010\u0002\u001a\u00020\u0003X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\tR\u0014\u0010\u0005\u001a\u00020\u0006X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f¨\u0006>"}, d2 = {"Lkotlin/io/encoding/Base64;", "", "isUrlSafe", "", "isMimeScheme", "paddingOption", "Lkotlin/io/encoding/Base64$PaddingOption;", "(ZZLkotlin/io/encoding/Base64$PaddingOption;)V", "isMimeScheme$kotlin_stdlib", "()Z", "isUrlSafe$kotlin_stdlib", "getPaddingOption$kotlin_stdlib", "()Lkotlin/io/encoding/Base64$PaddingOption;", "bytesToStringImpl", "", "source", "", "bytesToStringImpl$kotlin_stdlib", "charsToBytesImpl", "", "startIndex", "", "endIndex", "charsToBytesImpl$kotlin_stdlib", "checkDestinationBounds", "", "destinationSize", "destinationOffset", "capacityNeeded", "checkPaddingIsAllowed", "padIndex", "checkSourceBounds", "sourceSize", "checkSourceBounds$kotlin_stdlib", "decode", "decodeImpl", "destination", "decodeIntoByteArray", "decodeSize", "decodeSize$kotlin_stdlib", "encode", "encodeIntoByteArray", "encodeIntoByteArrayImpl", "encodeIntoByteArrayImpl$kotlin_stdlib", "encodeSize", "encodeSize$kotlin_stdlib", "encodeToAppendable", "A", "Ljava/lang/Appendable;", "Lkotlin/text/Appendable;", "([BLjava/lang/Appendable;II)Ljava/lang/Appendable;", "encodeToByteArray", "encodeToByteArrayImpl", "encodeToByteArrayImpl$kotlin_stdlib", "handlePaddingSymbol", "byteStart", "shouldPadOnEncode", "skipIllegalSymbolsIfMime", "withPadding", "option", Profile.DEFAULT_PROFILE_NAME, "PaddingOption", "kotlin-stdlib"}, k = 1, mv = {1, 9, 0}, xi = 48)
public class Base64 {
    private static final int bitsPerByte = 8;
    private static final int bitsPerSymbol = 6;
    public static final int bytesPerGroup = 3;
    private static final int mimeGroupsPerLine = 19;
    public static final int mimeLineLength = 76;
    public static final byte padSymbol = 61;
    public static final int symbolsPerGroup = 4;
    private final boolean isMimeScheme;
    private final boolean isUrlSafe;
    private final PaddingOption paddingOption;

    /* JADX INFO: renamed from: Default, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private static final byte[] mimeLineSeparatorSymbols = {13, 10};
    private static final Base64 UrlSafe = new Base64(true, false, PaddingOption.PRESENT);
    private static final Base64 Mime = new Base64(false, true, PaddingOption.PRESENT);

    /* JADX INFO: compiled from: Base64.kt */
    @Metadata(d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0006\b\u0087\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006¨\u0006\u0007"}, d2 = {"Lkotlin/io/encoding/Base64$PaddingOption;", "", "(Ljava/lang/String;I)V", "PRESENT", "ABSENT", "PRESENT_OPTIONAL", "ABSENT_OPTIONAL", "kotlin-stdlib"}, k = 1, mv = {1, 9, 0}, xi = 48)
    public enum PaddingOption {
        PRESENT,
        ABSENT,
        PRESENT_OPTIONAL,
        ABSENT_OPTIONAL;

        private static final /* synthetic */ EnumEntries $ENTRIES = EnumEntriesKt.enumEntries($VALUES);

        public static EnumEntries<PaddingOption> getEntries() {
            return $ENTRIES;
        }
    }

    public /* synthetic */ Base64(boolean z, boolean z2, PaddingOption paddingOption, DefaultConstructorMarker defaultConstructorMarker) {
        this(z, z2, paddingOption);
    }

    private Base64(boolean isUrlSafe, boolean isMimeScheme, PaddingOption paddingOption) {
        this.isUrlSafe = isUrlSafe;
        this.isMimeScheme = isMimeScheme;
        this.paddingOption = paddingOption;
        if ((this.isUrlSafe && this.isMimeScheme) ? false : true) {
        } else {
            throw new IllegalArgumentException("Failed requirement.".toString());
        }
    }

    /* JADX INFO: renamed from: isUrlSafe$kotlin_stdlib, reason: from getter */
    public final boolean getIsUrlSafe() {
        return this.isUrlSafe;
    }

    /* JADX INFO: renamed from: isMimeScheme$kotlin_stdlib, reason: from getter */
    public final boolean getIsMimeScheme() {
        return this.isMimeScheme;
    }

    /* JADX INFO: renamed from: getPaddingOption$kotlin_stdlib, reason: from getter */
    public final PaddingOption getPaddingOption() {
        return this.paddingOption;
    }

    public final Base64 withPadding(PaddingOption option) {
        Intrinsics.checkNotNullParameter(option, "option");
        return this.paddingOption == option ? this : new Base64(this.isUrlSafe, this.isMimeScheme, option);
    }

    public static /* synthetic */ byte[] encodeToByteArray$default(Base64 base64, byte[] bArr, int i, int i2, int i3, Object obj) {
        if (obj != null) {
            throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: encodeToByteArray");
        }
        if ((i3 & 2) != 0) {
            i = 0;
        }
        if ((i3 & 4) != 0) {
            i2 = bArr.length;
        }
        return base64.encodeToByteArray(bArr, i, i2);
    }

    public final byte[] encodeToByteArray(byte[] source, int startIndex, int endIndex) {
        Intrinsics.checkNotNullParameter(source, "source");
        return encodeToByteArrayImpl$kotlin_stdlib(source, startIndex, endIndex);
    }

    public static /* synthetic */ int encodeIntoByteArray$default(Base64 base64, byte[] bArr, byte[] bArr2, int i, int i2, int i3, int i4, Object obj) {
        if (obj != null) {
            throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: encodeIntoByteArray");
        }
        if ((i4 & 4) != 0) {
            i = 0;
        }
        if ((i4 & 8) != 0) {
            i2 = 0;
        }
        if ((i4 & 16) != 0) {
            i3 = bArr.length;
        }
        return base64.encodeIntoByteArray(bArr, bArr2, i, i2, i3);
    }

    public final int encodeIntoByteArray(byte[] source, byte[] destination, int destinationOffset, int startIndex, int endIndex) {
        Intrinsics.checkNotNullParameter(source, "source");
        Intrinsics.checkNotNullParameter(destination, "destination");
        return encodeIntoByteArrayImpl$kotlin_stdlib(source, destination, destinationOffset, startIndex, endIndex);
    }

    public static /* synthetic */ String encode$default(Base64 base64, byte[] bArr, int i, int i2, int i3, Object obj) {
        if (obj != null) {
            throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: encode");
        }
        if ((i3 & 2) != 0) {
            i = 0;
        }
        if ((i3 & 4) != 0) {
            i2 = bArr.length;
        }
        return base64.encode(bArr, i, i2);
    }

    public final String encode(byte[] source, int startIndex, int endIndex) {
        Intrinsics.checkNotNullParameter(source, "source");
        return new String(encodeToByteArrayImpl$kotlin_stdlib(source, startIndex, endIndex), Charsets.ISO_8859_1);
    }

    public static /* synthetic */ Appendable encodeToAppendable$default(Base64 base64, byte[] bArr, Appendable appendable, int i, int i2, int i3, Object obj) {
        if (obj != null) {
            throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: encodeToAppendable");
        }
        if ((i3 & 4) != 0) {
            i = 0;
        }
        if ((i3 & 8) != 0) {
            i2 = bArr.length;
        }
        return base64.encodeToAppendable(bArr, appendable, i, i2);
    }

    public final <A extends Appendable> A encodeToAppendable(byte[] source, A destination, int startIndex, int endIndex) throws IOException {
        Intrinsics.checkNotNullParameter(source, "source");
        Intrinsics.checkNotNullParameter(destination, "destination");
        String stringResult = new String(encodeToByteArrayImpl$kotlin_stdlib(source, startIndex, endIndex), Charsets.ISO_8859_1);
        destination.append(stringResult);
        return destination;
    }

    public static /* synthetic */ byte[] decode$default(Base64 base64, byte[] bArr, int i, int i2, int i3, Object obj) {
        if (obj != null) {
            throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: decode");
        }
        if ((i3 & 2) != 0) {
            i = 0;
        }
        if ((i3 & 4) != 0) {
            i2 = bArr.length;
        }
        return base64.decode(bArr, i, i2);
    }

    public final byte[] decode(byte[] source, int startIndex, int endIndex) {
        Intrinsics.checkNotNullParameter(source, "source");
        checkSourceBounds$kotlin_stdlib(source.length, startIndex, endIndex);
        int decodeSize = decodeSize$kotlin_stdlib(source, startIndex, endIndex);
        byte[] destination = new byte[decodeSize];
        int bytesWritten = decodeImpl(source, destination, 0, startIndex, endIndex);
        if (!(bytesWritten == destination.length)) {
            throw new IllegalStateException("Check failed.".toString());
        }
        return destination;
    }

    public static /* synthetic */ int decodeIntoByteArray$default(Base64 base64, byte[] bArr, byte[] bArr2, int i, int i2, int i3, int i4, Object obj) {
        if (obj != null) {
            throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: decodeIntoByteArray");
        }
        if ((i4 & 4) != 0) {
            i = 0;
        }
        if ((i4 & 8) != 0) {
            i2 = 0;
        }
        if ((i4 & 16) != 0) {
            i3 = bArr.length;
        }
        return base64.decodeIntoByteArray(bArr, bArr2, i, i2, i3);
    }

    public final int decodeIntoByteArray(byte[] source, byte[] destination, int destinationOffset, int startIndex, int endIndex) {
        Intrinsics.checkNotNullParameter(source, "source");
        Intrinsics.checkNotNullParameter(destination, "destination");
        checkSourceBounds$kotlin_stdlib(source.length, startIndex, endIndex);
        checkDestinationBounds(destination.length, destinationOffset, decodeSize$kotlin_stdlib(source, startIndex, endIndex));
        return decodeImpl(source, destination, destinationOffset, startIndex, endIndex);
    }

    public static /* synthetic */ byte[] decode$default(Base64 base64, CharSequence charSequence, int i, int i2, int i3, Object obj) {
        if (obj != null) {
            throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: decode");
        }
        if ((i3 & 2) != 0) {
            i = 0;
        }
        if ((i3 & 4) != 0) {
            i2 = charSequence.length();
        }
        return base64.decode(charSequence, i, i2);
    }

    public final byte[] decode(CharSequence source, int startIndex, int endIndex) {
        byte[] bArrCharsToBytesImpl$kotlin_stdlib;
        Intrinsics.checkNotNullParameter(source, "source");
        if (source instanceof String) {
            checkSourceBounds$kotlin_stdlib(source.length(), startIndex, endIndex);
            String strSubstring = ((String) source).substring(startIndex, endIndex);
            Intrinsics.checkNotNullExpressionValue(strSubstring, "substring(...)");
            Charset charset = Charsets.ISO_8859_1;
            Intrinsics.checkNotNull(strSubstring, "null cannot be cast to non-null type java.lang.String");
            bArrCharsToBytesImpl$kotlin_stdlib = strSubstring.getBytes(charset);
            Intrinsics.checkNotNullExpressionValue(bArrCharsToBytesImpl$kotlin_stdlib, "getBytes(...)");
        } else {
            bArrCharsToBytesImpl$kotlin_stdlib = charsToBytesImpl$kotlin_stdlib(source, startIndex, endIndex);
        }
        byte[] byteSource = bArrCharsToBytesImpl$kotlin_stdlib;
        return decode$default(this, byteSource, 0, 0, 6, (Object) null);
    }

    public static /* synthetic */ int decodeIntoByteArray$default(Base64 base64, CharSequence charSequence, byte[] bArr, int i, int i2, int i3, int i4, Object obj) {
        if (obj != null) {
            throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: decodeIntoByteArray");
        }
        if ((i4 & 4) != 0) {
            i = 0;
        }
        if ((i4 & 8) != 0) {
            i2 = 0;
        }
        if ((i4 & 16) != 0) {
            i3 = charSequence.length();
        }
        return base64.decodeIntoByteArray(charSequence, bArr, i, i2, i3);
    }

    public final int decodeIntoByteArray(CharSequence source, byte[] destination, int destinationOffset, int startIndex, int endIndex) {
        byte[] bArrCharsToBytesImpl$kotlin_stdlib;
        Intrinsics.checkNotNullParameter(source, "source");
        Intrinsics.checkNotNullParameter(destination, "destination");
        if (source instanceof String) {
            checkSourceBounds$kotlin_stdlib(source.length(), startIndex, endIndex);
            String strSubstring = ((String) source).substring(startIndex, endIndex);
            Intrinsics.checkNotNullExpressionValue(strSubstring, "substring(...)");
            Charset charset = Charsets.ISO_8859_1;
            Intrinsics.checkNotNull(strSubstring, "null cannot be cast to non-null type java.lang.String");
            bArrCharsToBytesImpl$kotlin_stdlib = strSubstring.getBytes(charset);
            Intrinsics.checkNotNullExpressionValue(bArrCharsToBytesImpl$kotlin_stdlib, "getBytes(...)");
        } else {
            bArrCharsToBytesImpl$kotlin_stdlib = charsToBytesImpl$kotlin_stdlib(source, startIndex, endIndex);
        }
        byte[] byteSource = bArrCharsToBytesImpl$kotlin_stdlib;
        return decodeIntoByteArray$default(this, byteSource, destination, destinationOffset, 0, 0, 24, (Object) null);
    }

    public final byte[] encodeToByteArrayImpl$kotlin_stdlib(byte[] source, int startIndex, int endIndex) {
        Intrinsics.checkNotNullParameter(source, "source");
        checkSourceBounds$kotlin_stdlib(source.length, startIndex, endIndex);
        int encodeSize = encodeSize$kotlin_stdlib(endIndex - startIndex);
        byte[] destination = new byte[encodeSize];
        encodeIntoByteArrayImpl$kotlin_stdlib(source, destination, 0, startIndex, endIndex);
        return destination;
    }

    public final int encodeIntoByteArrayImpl$kotlin_stdlib(byte[] source, byte[] destination, int destinationOffset, int startIndex, int endIndex) {
        Intrinsics.checkNotNullParameter(source, "source");
        Intrinsics.checkNotNullParameter(destination, "destination");
        checkSourceBounds$kotlin_stdlib(source.length, startIndex, endIndex);
        checkDestinationBounds(destination.length, destinationOffset, encodeSize$kotlin_stdlib(endIndex - startIndex));
        byte[] encodeMap = this.isUrlSafe ? Base64Kt.base64UrlEncodeMap : Base64Kt.base64EncodeMap;
        int sourceIndex = startIndex;
        int destinationIndex = destinationOffset;
        int groupsPerLine = this.isMimeScheme ? 19 : Integer.MAX_VALUE;
        while (true) {
            if (sourceIndex + 2 >= endIndex) {
                break;
            }
            int groups = Math.min((endIndex - sourceIndex) / 3, groupsPerLine);
            int i = 0;
            while (i < groups) {
                int sourceIndex2 = sourceIndex + 1;
                int byte1 = source[sourceIndex] & 255;
                int sourceIndex3 = sourceIndex2 + 1;
                int byte2 = source[sourceIndex2] & 255;
                int sourceIndex4 = sourceIndex3 + 1;
                int byte3 = source[sourceIndex3] & 255;
                int bits = (byte1 << 16) | (byte2 << 8) | byte3;
                int destinationIndex2 = destinationIndex + 1;
                destination[destinationIndex] = encodeMap[bits >>> 18];
                int destinationIndex3 = destinationIndex2 + 1;
                destination[destinationIndex2] = encodeMap[(bits >>> 12) & 63];
                int destinationIndex4 = destinationIndex3 + 1;
                destination[destinationIndex3] = encodeMap[(bits >>> 6) & 63];
                destinationIndex = destinationIndex4 + 1;
                destination[destinationIndex4] = encodeMap[bits & 63];
                i++;
                sourceIndex = sourceIndex4;
            }
            if (groups == groupsPerLine && sourceIndex != endIndex) {
                int destinationIndex5 = destinationIndex + 1;
                destination[destinationIndex] = mimeLineSeparatorSymbols[0];
                destinationIndex = destinationIndex5 + 1;
                destination[destinationIndex5] = mimeLineSeparatorSymbols[1];
            }
        }
        switch (endIndex - sourceIndex) {
            case 1:
                int byte4 = sourceIndex + 1;
                int byte5 = source[sourceIndex] & 255;
                int bits2 = byte5 << 4;
                int destinationIndex6 = destinationIndex + 1;
                destination[destinationIndex] = encodeMap[bits2 >>> 6];
                destinationIndex = destinationIndex6 + 1;
                destination[destinationIndex6] = encodeMap[bits2 & 63];
                if (!shouldPadOnEncode()) {
                    sourceIndex = byte4;
                } else {
                    int destinationIndex7 = destinationIndex + 1;
                    destination[destinationIndex] = padSymbol;
                    destinationIndex = destinationIndex7 + 1;
                    destination[destinationIndex7] = padSymbol;
                    sourceIndex = byte4;
                }
                break;
            case 2:
                int sourceIndex5 = sourceIndex + 1;
                int byte6 = source[sourceIndex] & 255;
                int sourceIndex6 = sourceIndex5 + 1;
                int bits3 = (byte6 << 10) | ((source[sourceIndex5] & 255) << 2);
                int destinationIndex8 = destinationIndex + 1;
                destination[destinationIndex] = encodeMap[bits3 >>> 12];
                int destinationIndex9 = destinationIndex8 + 1;
                destination[destinationIndex8] = encodeMap[(bits3 >>> 6) & 63];
                int destinationIndex10 = destinationIndex9 + 1;
                destination[destinationIndex9] = encodeMap[bits3 & 63];
                if (!shouldPadOnEncode()) {
                    sourceIndex = sourceIndex6;
                    destinationIndex = destinationIndex10;
                } else {
                    destinationIndex = destinationIndex10 + 1;
                    destination[destinationIndex10] = padSymbol;
                    sourceIndex = sourceIndex6;
                }
                break;
        }
        if (!(sourceIndex == endIndex)) {
            throw new IllegalStateException("Check failed.".toString());
        }
        return destinationIndex - destinationOffset;
    }

    public final int encodeSize$kotlin_stdlib(int sourceSize) {
        int groups = sourceSize / 3;
        int trailingBytes = sourceSize % 3;
        int size = groups * 4;
        if (trailingBytes != 0) {
            size += shouldPadOnEncode() ? 4 : trailingBytes + 1;
        }
        if (this.isMimeScheme) {
            size += ((size - 1) / 76) * 2;
        }
        if (size < 0) {
            throw new IllegalArgumentException("Input is too big");
        }
        return size;
    }

    private final boolean shouldPadOnEncode() {
        return this.paddingOption == PaddingOption.PRESENT || this.paddingOption == PaddingOption.PRESENT_OPTIONAL;
    }

    /* JADX WARN: Code duplicated, block: B:22:0x008a  */
    /* JADX WARN: Code duplicated, block: B:24:0x008e  */
    /* JADX WARN: Code duplicated, block: B:58:0x00c8 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:59:0x0082 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:60:0x0091 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:61:0x007f A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:62:0x00e8 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:63:0x00d4 A[SYNTHETIC] */
    private final int decodeImpl(byte[] source, byte[] destination, int destinationOffset, int startIndex, int endIndex) {
        int i;
        int symbol;
        int symbolBits;
        int[] decodeMap;
        int[] decodeMap2 = this.isUrlSafe ? Base64Kt.base64UrlDecodeMap : Base64Kt.base64DecodeMap;
        int payload = 0;
        int byteStart = -8;
        int symbol1 = startIndex;
        int destinationIndex = destinationOffset;
        boolean hasPadding = false;
        while (true) {
            if (symbol1 >= endIndex) {
                i = 8;
                break;
            }
            if (byteStart != -8 || symbol1 + 3 >= endIndex) {
                i = 8;
                symbol = source[symbol1] & UByte.MAX_VALUE;
                symbolBits = decodeMap2[symbol];
                if (symbolBits < 0) {
                    if (symbolBits == -2) {
                        hasPadding = true;
                        symbol1 = handlePaddingSymbol(source, symbol1, endIndex, byteStart);
                        break;
                    }
                    if (this.isMimeScheme) {
                        symbol1++;
                    } else {
                        StringBuilder sbAppend = new StringBuilder().append("Invalid symbol '").append((char) symbol).append("'(");
                        String string = Integer.toString(symbol, CharsKt.checkRadix(i));
                        Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
                        throw new IllegalArgumentException(sbAppend.append(string).append(") at index ").append(symbol1).toString());
                    }
                } else {
                    decodeMap = decodeMap2;
                    symbol1++;
                    payload = (payload << 6) | symbolBits;
                    byteStart += 6;
                    if (byteStart >= 0) {
                        decodeMap2 = decodeMap;
                    } else {
                        destination[destinationIndex] = (byte) (payload >>> byteStart);
                        payload &= (1 << byteStart) - 1;
                        byteStart -= 8;
                        destinationIndex++;
                        decodeMap2 = decodeMap;
                    }
                }
            } else {
                int sourceIndex = symbol1 + 1;
                int symbol2 = decodeMap2[source[symbol1] & 255];
                int sourceIndex2 = sourceIndex + 1;
                int symbol3 = decodeMap2[source[sourceIndex] & 255];
                int sourceIndex3 = sourceIndex2 + 1;
                i = 8;
                int symbol4 = decodeMap2[source[sourceIndex2] & UByte.MAX_VALUE];
                int sourceIndex4 = sourceIndex3 + 1;
                int symbol5 = decodeMap2[source[sourceIndex3] & UByte.MAX_VALUE];
                int bits = (symbol2 << 18) | (symbol3 << 12) | (symbol4 << 6) | symbol5;
                if (bits >= 0) {
                    int destinationIndex2 = destinationIndex + 1;
                    destination[destinationIndex] = (byte) (bits >> 16);
                    int destinationIndex3 = destinationIndex2 + 1;
                    destination[destinationIndex2] = (byte) (bits >> 8);
                    destination[destinationIndex3] = (byte) bits;
                    destinationIndex = destinationIndex3 + 1;
                    symbol1 = sourceIndex4;
                } else {
                    symbol1 = sourceIndex4 - 4;
                    symbol = source[symbol1] & UByte.MAX_VALUE;
                    symbolBits = decodeMap2[symbol];
                    if (symbolBits < 0) {
                        if (symbolBits == -2) {
                            hasPadding = true;
                            symbol1 = handlePaddingSymbol(source, symbol1, endIndex, byteStart);
                            break;
                        }
                        if (this.isMimeScheme) {
                            symbol1++;
                        } else {
                            StringBuilder sbAppend2 = new StringBuilder().append("Invalid symbol '").append((char) symbol).append("'(");
                            String string2 = Integer.toString(symbol, CharsKt.checkRadix(i));
                            Intrinsics.checkNotNullExpressionValue(string2, "toString(...)");
                            throw new IllegalArgumentException(sbAppend2.append(string2).append(") at index ").append(symbol1).toString());
                        }
                    } else {
                        decodeMap = decodeMap2;
                        symbol1++;
                        payload = (payload << 6) | symbolBits;
                        byteStart += 6;
                        if (byteStart >= 0) {
                            decodeMap2 = decodeMap;
                        } else {
                            destination[destinationIndex] = (byte) (payload >>> byteStart);
                            payload &= (1 << byteStart) - 1;
                            byteStart -= 8;
                            destinationIndex++;
                            decodeMap2 = decodeMap;
                        }
                    }
                }
            }
        }
        if (byteStart == -2) {
            throw new IllegalArgumentException("The last unit of input does not have enough bits");
        }
        if (byteStart != -8 && !hasPadding && this.paddingOption == PaddingOption.PRESENT) {
            throw new IllegalArgumentException("The padding option is set to PRESENT, but the input is not properly padded");
        }
        if (payload != 0) {
            throw new IllegalArgumentException("The pad bits must be zeros");
        }
        int sourceIndex5 = skipIllegalSymbolsIfMime(source, symbol1, endIndex);
        if (sourceIndex5 < endIndex) {
            int symbol6 = source[sourceIndex5] & UByte.MAX_VALUE;
            StringBuilder sbAppend3 = new StringBuilder().append("Symbol '").append((char) symbol6).append("'(");
            String string3 = Integer.toString(symbol6, CharsKt.checkRadix(i));
            Intrinsics.checkNotNullExpressionValue(string3, "toString(...)");
            throw new IllegalArgumentException(sbAppend3.append(string3).append(") at index ").append(sourceIndex5 - 1).append(" is prohibited after the pad character").toString());
        }
        return destinationIndex - destinationOffset;
    }

    public final int decodeSize$kotlin_stdlib(byte[] source, int startIndex, int endIndex) {
        Intrinsics.checkNotNullParameter(source, "source");
        int symbols = endIndex - startIndex;
        if (symbols == 0) {
            return 0;
        }
        if (symbols == 1) {
            throw new IllegalArgumentException("Input should have at least 2 symbols for Base64 decoding, startIndex: " + startIndex + ", endIndex: " + endIndex);
        }
        if (this.isMimeScheme) {
            for (int index = startIndex; index < endIndex; index++) {
                int symbol = source[index] & 255;
                int symbolBits = Base64Kt.base64DecodeMap[symbol];
                if (symbolBits < 0) {
                    if (symbolBits == -2) {
                        symbols -= endIndex - index;
                        break;
                    }
                    symbols--;
                }
            }
        } else {
            int index2 = endIndex - 1;
            if (source[index2] == 61) {
                symbols--;
                if (source[endIndex - 2] == 61) {
                    symbols--;
                }
            }
        }
        return (int) ((((long) symbols) * ((long) 6)) / ((long) 8));
    }

    public final byte[] charsToBytesImpl$kotlin_stdlib(CharSequence source, int startIndex, int endIndex) {
        int length;
        Intrinsics.checkNotNullParameter(source, "source");
        checkSourceBounds$kotlin_stdlib(source.length(), startIndex, endIndex);
        byte[] byteArray = new byte[endIndex - startIndex];
        int length2 = 0;
        for (int index = startIndex; index < endIndex; index++) {
            int symbol = source.charAt(index);
            if (symbol <= 255) {
                length = length2 + 1;
                byteArray[length2] = (byte) symbol;
            } else {
                length = length2 + 1;
                byteArray[length2] = Utf8.REPLACEMENT_BYTE;
            }
            length2 = length;
        }
        return byteArray;
    }

    public final String bytesToStringImpl$kotlin_stdlib(byte[] source) {
        Intrinsics.checkNotNullParameter(source, "source");
        StringBuilder stringBuilder = new StringBuilder(source.length);
        for (byte b : source) {
            stringBuilder.append((char) b);
        }
        String string = stringBuilder.toString();
        Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        return string;
    }

    private final int handlePaddingSymbol(byte[] source, int padIndex, int endIndex, int byteStart) {
        switch (byteStart) {
            case -8:
                throw new IllegalArgumentException("Redundant pad character at index " + padIndex);
            case -7:
            case -5:
            case -3:
            default:
                throw new IllegalStateException("Unreachable".toString());
            case -6:
                checkPaddingIsAllowed(padIndex);
                return padIndex + 1;
            case FontsContractCompat.FontRequestCallback.FAIL_REASON_SECURITY_VIOLATION /* -4 */:
                checkPaddingIsAllowed(padIndex);
                int secondPadIndex = skipIllegalSymbolsIfMime(source, padIndex + 1, endIndex);
                if (secondPadIndex == endIndex || source[secondPadIndex] != 61) {
                    throw new IllegalArgumentException("Missing one pad character at index " + secondPadIndex);
                }
                return secondPadIndex + 1;
            case -2:
                return padIndex + 1;
        }
    }

    private final void checkPaddingIsAllowed(int padIndex) {
        if (this.paddingOption == PaddingOption.ABSENT) {
            throw new IllegalArgumentException("The padding option is set to ABSENT, but the input has a pad character at index " + padIndex);
        }
    }

    private final int skipIllegalSymbolsIfMime(byte[] source, int startIndex, int endIndex) {
        if (!this.isMimeScheme) {
            return startIndex;
        }
        int sourceIndex = startIndex;
        while (sourceIndex < endIndex) {
            int symbol = source[sourceIndex] & 255;
            if (Base64Kt.base64DecodeMap[symbol] != -1) {
                return sourceIndex;
            }
            sourceIndex++;
        }
        return sourceIndex;
    }

    public final void checkSourceBounds$kotlin_stdlib(int sourceSize, int startIndex, int endIndex) {
        AbstractList.INSTANCE.checkBoundsIndexes$kotlin_stdlib(startIndex, endIndex, sourceSize);
    }

    private final void checkDestinationBounds(int destinationSize, int destinationOffset, int capacityNeeded) {
        if (destinationOffset < 0 || destinationOffset > destinationSize) {
            throw new IndexOutOfBoundsException("destination offset: " + destinationOffset + ", destination size: " + destinationSize);
        }
        int destinationEndIndex = destinationOffset + capacityNeeded;
        if (destinationEndIndex < 0 || destinationEndIndex > destinationSize) {
            throw new IndexOutOfBoundsException("The destination array does not have enough capacity, destination offset: " + destinationOffset + ", destination size: " + destinationSize + ", capacity needed: " + capacityNeeded);
        }
    }

    /* JADX INFO: renamed from: kotlin.io.encoding.Base64$Default, reason: from kotlin metadata */
    /* JADX INFO: compiled from: Base64.kt */
    @Metadata(d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u0012\n\u0002\b\u0003\n\u0002\u0010\u0005\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u0011\u0010\u0003\u001a\u00020\u0001¢\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\u0005R\u0011\u0010\u0006\u001a\u00020\u0001¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\u0005R\u000e\u0010\b\u001a\u00020\tX\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\tX\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\tX\u0080T¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\tX\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\tX\u0080T¢\u0006\u0002\n\u0000R\u0014\u0010\u000e\u001a\u00020\u000fX\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u000e\u0010\u0012\u001a\u00020\u0013X\u0080T¢\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\tX\u0080T¢\u0006\u0002\n\u0000¨\u0006\u0015"}, d2 = {"Lkotlin/io/encoding/Base64$Default;", "Lkotlin/io/encoding/Base64;", "()V", "Mime", "getMime", "()Lkotlin/io/encoding/Base64;", "UrlSafe", "getUrlSafe", "bitsPerByte", "", "bitsPerSymbol", "bytesPerGroup", "mimeGroupsPerLine", "mimeLineLength", "mimeLineSeparatorSymbols", "", "getMimeLineSeparatorSymbols$kotlin_stdlib", "()[B", "padSymbol", "", "symbolsPerGroup", "kotlin-stdlib"}, k = 1, mv = {1, 9, 0}, xi = 48)
    public static final class Companion extends Base64 {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        /* JADX WARN: Illegal instructions before constructor call */
        private Companion() {
            boolean z = false;
            super(z, z, PaddingOption.PRESENT, null);
        }

        public final byte[] getMimeLineSeparatorSymbols$kotlin_stdlib() {
            return Base64.mimeLineSeparatorSymbols;
        }

        public final Base64 getUrlSafe() {
            return Base64.UrlSafe;
        }

        public final Base64 getMime() {
            return Base64.Mime;
        }
    }
}
