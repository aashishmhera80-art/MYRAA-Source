package com.getcapacitor.util;

import androidx.webkit.ProxyConfig;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public interface HostMask {
    boolean matches(String str);

    public static class Parser {
        private static HostMask NOTHING = new Nothing();

        public static HostMask parse(String[] masks) {
            return masks == null ? NOTHING : Any.parse(masks);
        }

        public static HostMask parse(String mask) {
            return mask == null ? NOTHING : Simple.parse(mask);
        }
    }

    public static class Simple implements HostMask {
        private final List<String> maskParts;

        private Simple(List<String> maskParts) {
            if (maskParts == null) {
                throw new IllegalArgumentException("Mask parts can not be null");
            }
            this.maskParts = maskParts;
        }

        static Simple parse(String mask) {
            List<String> parts = Util.splitAndReverse(mask);
            return new Simple(parts);
        }

        @Override // com.getcapacitor.util.HostMask
        public boolean matches(String host) {
            if (host == null) {
                return false;
            }
            List<String> hostParts = Util.splitAndReverse(host);
            int hostSize = hostParts.size();
            int maskSize = this.maskParts.size();
            if (maskSize > 1 && hostSize != maskSize) {
                return false;
            }
            int minSize = Math.min(hostSize, maskSize);
            for (int i = 0; i < minSize; i++) {
                String maskPart = this.maskParts.get(i);
                String hostPart = hostParts.get(i);
                if (!Util.matches(maskPart, hostPart)) {
                    return false;
                }
            }
            return true;
        }
    }

    public static class Any implements HostMask {
        private final List<? extends HostMask> masks;

        Any(List<? extends HostMask> masks) {
            this.masks = masks;
        }

        @Override // com.getcapacitor.util.HostMask
        public boolean matches(String host) {
            for (HostMask mask : this.masks) {
                if (mask.matches(host)) {
                    return true;
                }
            }
            return false;
        }

        static Any parse(String... rawMasks) {
            List<Simple> masks = new ArrayList<>();
            for (String raw : rawMasks) {
                masks.add(Simple.parse(raw));
            }
            return new Any(masks);
        }
    }

    public static class Nothing implements HostMask {
        @Override // com.getcapacitor.util.HostMask
        public boolean matches(String host) {
            return false;
        }
    }

    public static class Util {
        static boolean matches(String mask, String string) {
            if (mask == null) {
                return false;
            }
            if (ProxyConfig.MATCH_ALL_SCHEMES.equals(mask)) {
                return true;
            }
            if (string == null) {
                return false;
            }
            return mask.toUpperCase().equals(string.toUpperCase());
        }

        static List<String> splitAndReverse(String string) {
            if (string == null) {
                throw new IllegalArgumentException("Can not split null argument");
            }
            List<String> parts = Arrays.asList(string.split("\\."));
            Collections.reverse(parts);
            return parts;
        }
    }
}
