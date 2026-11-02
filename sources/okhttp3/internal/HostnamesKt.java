package okhttp3.internal;

import java.net.IDN;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import okio.Buffer;

/* JADX INFO: compiled from: hostnames.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000&\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0012\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a0\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u0005H\u0002\u001a\"\u0010\n\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0005H\u0002\u001a\u0010\u0010\f\u001a\u00020\u00032\u0006\u0010\u0007\u001a\u00020\bH\u0002\u001a\f\u0010\r\u001a\u00020\u0001*\u00020\u0003H\u0002\u001a\f\u0010\u000e\u001a\u0004\u0018\u00010\u0003*\u00020\u0003¨\u0006\u000f"}, d2 = {"decodeIpv4Suffix", "", "input", "", "pos", "", "limit", "address", "", "addressOffset", "decodeIpv6", "Ljava/net/InetAddress;", "inet6AddressToAscii", "containsInvalidHostnameAsciiCodes", "toCanonicalHost", "okhttp"}, k = 2, mv = {1, 8, 0}, xi = 48)
public final class HostnamesKt {
    public static final String toCanonicalHost(String $this$toCanonicalHost) {
        Intrinsics.checkNotNullParameter($this$toCanonicalHost, "<this>");
        if (StringsKt.contains$default((CharSequence) $this$toCanonicalHost, (CharSequence) ":", false, 2, (Object) null)) {
            InetAddress inetAddress = (StringsKt.startsWith$default($this$toCanonicalHost, "[", false, 2, (Object) null) && StringsKt.endsWith$default($this$toCanonicalHost, "]", false, 2, (Object) null)) ? decodeIpv6($this$toCanonicalHost, 1, $this$toCanonicalHost.length() - 1) : decodeIpv6($this$toCanonicalHost, 0, $this$toCanonicalHost.length());
            if (inetAddress == null) {
                return null;
            }
            byte[] address = inetAddress.getAddress();
            if (address.length == 16) {
                Intrinsics.checkNotNullExpressionValue(address, "address");
                return inet6AddressToAscii(address);
            }
            if (address.length == 4) {
                return inetAddress.getHostAddress();
            }
            throw new AssertionError("Invalid IPv6 address: '" + $this$toCanonicalHost + '\'');
        }
        try {
            String ascii = IDN.toASCII($this$toCanonicalHost);
            Intrinsics.checkNotNullExpressionValue(ascii, "toASCII(host)");
            Locale US = Locale.US;
            Intrinsics.checkNotNullExpressionValue(US, "US");
            String result = ascii.toLowerCase(US);
            Intrinsics.checkNotNullExpressionValue(result, "this as java.lang.String).toLowerCase(locale)");
            if ((result.length() == 0) || containsInvalidHostnameAsciiCodes(result)) {
                return null;
            }
            return result;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static final boolean containsInvalidHostnameAsciiCodes(String $this$containsInvalidHostnameAsciiCodes) {
        int length = $this$containsInvalidHostnameAsciiCodes.length();
        for (int i = 0; i < length; i++) {
            char c = $this$containsInvalidHostnameAsciiCodes.charAt(i);
            if (Intrinsics.compare((int) c, 31) <= 0 || Intrinsics.compare((int) c, 127) >= 0 || StringsKt.indexOf$default((CharSequence) " #%/:?@[\\]", c, 0, false, 6, (Object) null) != -1) {
                return true;
            }
        }
        return false;
    }

    /* JADX WARN: Code duplicated, block: B:31:0x005b  */
    /* JADX WARN: Code duplicated, block: B:33:0x0065 A[LOOP:1: B:30:0x0059->B:33:0x0065, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:56:0x006d A[EDGE_INSN: B:56:0x006d->B:34:0x006d BREAK  A[LOOP:1: B:30:0x0059->B:33:0x0065], SYNTHETIC] */
    private static final InetAddress decodeIpv6(String input, int pos, int limit) {
        String input2;
        int value;
        int groupLength;
        int hexDigit;
        byte[] address = new byte[16];
        int b = 0;
        int compress = -1;
        int groupOffset = -1;
        int i = pos;
        while (i < limit) {
            if (b == address.length) {
                return null;
            }
            if (i + 2 <= limit) {
                input2 = input;
                if (StringsKt.startsWith$default(input2, "::", i, false, 4, (Object) null)) {
                    if (compress != -1) {
                        return null;
                    }
                    i += 2;
                    b += 2;
                    compress = b;
                    if (i == limit) {
                        break;
                    }
                    value = 0;
                    groupOffset = i;
                    while (i < limit) {
                        hexDigit = Util.parseHexDigit(input2.charAt(i));
                        if (hexDigit != -1) {
                            break;
                        }
                        value = (value << 4) + hexDigit;
                        i++;
                    }
                    groupLength = i - groupOffset;
                    if (groupLength != 0 || groupLength > 4) {
                        return null;
                    }
                    int b2 = b + 1;
                    address[b] = (byte) ((value >>> 8) & 255);
                    b = b2 + 1;
                    address[b2] = (byte) (value & 255);
                    input = input2;
                }
            } else {
                input2 = input;
            }
            if (b != 0) {
                if (StringsKt.startsWith$default(input2, ":", i, false, 4, (Object) null)) {
                    i++;
                } else {
                    if (!StringsKt.startsWith$default(input2, ".", i, false, 4, (Object) null) || !decodeIpv4Suffix(input2, groupOffset, limit, address, b - 2)) {
                        return null;
                    }
                    b += 2;
                    break;
                }
            }
            value = 0;
            groupOffset = i;
            while (i < limit) {
                hexDigit = Util.parseHexDigit(input2.charAt(i));
                if (hexDigit != -1) {
                    break;
                    break;
                }
                value = (value << 4) + hexDigit;
                i++;
            }
            groupLength = i - groupOffset;
            if (groupLength != 0) {
            }
            return null;
        }
        if (b != address.length) {
            if (compress == -1) {
                return null;
            }
            System.arraycopy(address, compress, address, address.length - (b - compress), b - compress);
            Arrays.fill(address, compress, (address.length - b) + compress, (byte) 0);
        }
        return InetAddress.getByAddress(address);
    }

    private static final boolean decodeIpv4Suffix(String input, int pos, int limit, byte[] address, int addressOffset) {
        int b = addressOffset;
        int i = pos;
        while (i < limit) {
            if (b == address.length) {
                return false;
            }
            if (b != addressOffset) {
                if (input.charAt(i) != '.') {
                    return false;
                }
                i++;
            }
            int value = 0;
            int groupOffset = i;
            while (i < limit) {
                char c = input.charAt(i);
                if (Intrinsics.compare((int) c, 48) < 0 || Intrinsics.compare((int) c, 57) > 0) {
                    break;
                }
                if ((value == 0 && groupOffset != i) || (value = ((value * 10) + c) - 48) > 255) {
                    return false;
                }
                i++;
            }
            int groupLength = i - groupOffset;
            if (groupLength == 0) {
                return false;
            }
            address[b] = (byte) value;
            b++;
        }
        return b == addressOffset + 4;
    }

    private static final String inet6AddressToAscii(byte[] address) {
        int longestRunOffset = -1;
        int longestRunLength = 0;
        int i = 0;
        while (i < address.length) {
            int currentRunOffset = i;
            while (i < 16 && address[i] == 0 && address[i + 1] == 0) {
                i += 2;
            }
            int currentRunLength = i - currentRunOffset;
            if (currentRunLength > longestRunLength && currentRunLength >= 4) {
                longestRunOffset = currentRunOffset;
                longestRunLength = currentRunLength;
            }
            i += 2;
        }
        Buffer result = new Buffer();
        int i2 = 0;
        while (i2 < address.length) {
            if (i2 == longestRunOffset) {
                result.writeByte(58);
                i2 += longestRunLength;
                if (i2 == 16) {
                    result.writeByte(58);
                }
            } else {
                if (i2 > 0) {
                    result.writeByte(58);
                }
                int group = (Util.and(address[i2], 255) << 8) | Util.and(address[i2 + 1], 255);
                result.writeHexadecimalUnsignedLong(group);
                i2 += 2;
            }
        }
        return result.readUtf8();
    }
}
