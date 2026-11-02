package androidx.webkit.internal;

import androidx.core.view.MotionEventCompat;
import java.net.URLConnection;

/* JADX INFO: loaded from: classes.dex */
class MimeUtil {
    MimeUtil() {
    }

    public static String getMimeFromFileName(String fileName) {
        if (fileName == null) {
            return null;
        }
        String mimeType = URLConnection.guessContentTypeFromName(fileName);
        if (mimeType != null) {
            return mimeType;
        }
        return guessHardcodedMime(fileName);
    }

    private static String guessHardcodedMime(String fileName) {
        byte b = 46;
        int finalFullStop = fileName.lastIndexOf(46);
        if (finalFullStop == -1) {
            return null;
        }
        String extension = fileName.substring(finalFullStop + 1).toLowerCase();
        switch (extension.hashCode()) {
            case 3315:
                b = !extension.equals("gz") ? (byte) -1 : (byte) 42;
                break;
            case 3401:
                b = !extension.equals("js") ? (byte) -1 : (byte) 33;
                break;
            case 97669:
                b = !extension.equals("bmp") ? (byte) -1 : (byte) 47;
                break;
            case 98819:
                b = !extension.equals("css") ? (byte) -1 : (byte) 27;
                break;
            case 102340:
                b = !extension.equals("gif") ? (byte) -1 : (byte) 14;
                break;
            case 103649:
                b = !extension.equals("htm") ? (byte) -1 : (byte) 29;
                break;
            case 104085:
                b = !extension.equals("ico") ? (byte) -1 : (byte) 40;
                break;
            case 105441:
                b = !extension.equals("jpg") ? (byte) -1 : (byte) 16;
                break;
            case 106458:
                b = !extension.equals("m4a") ? (byte) -1 : (byte) 13;
                break;
            case 106479:
                b = !extension.equals("m4v") ? (byte) -1 : (byte) 37;
                break;
            case 108089:
                b = !extension.equals("mht") ? (byte) -1 : (byte) 25;
                break;
            case 108150:
                b = !extension.equals("mjs") ? (byte) -1 : (byte) 34;
                break;
            case 108272:
                b = !extension.equals("mp3") ? (byte) -1 : (byte) 3;
                break;
            case 108273:
                b = !extension.equals("mp4") ? (byte) -1 : (byte) 36;
                break;
            case 108324:
                b = !extension.equals("mpg") ? (byte) -1 : (byte) 2;
                break;
            case 109961:
                b = !extension.equals("oga") ? (byte) -1 : (byte) 10;
                break;
            case 109967:
                b = !extension.equals("ogg") ? (byte) -1 : (byte) 9;
                break;
            case 109973:
                b = !extension.equals("ogm") ? (byte) -1 : (byte) 39;
                break;
            case 109982:
                b = !extension.equals("ogv") ? (byte) -1 : (byte) 38;
                break;
            case 110834:
                b = !extension.equals("pdf") ? (byte) -1 : (byte) 45;
                break;
            case 111030:
                b = !extension.equals("pjp") ? (byte) -1 : (byte) 19;
                break;
            case 111145:
                b = !extension.equals("png") ? (byte) -1 : (byte) 20;
                break;
            case 114276:
                b = !extension.equals("svg") ? (byte) -1 : (byte) 22;
                break;
            case 114791:
                b = !extension.equals("tgz") ? (byte) -1 : (byte) 43;
                break;
            case 114833:
                b = !extension.equals("tif") ? (byte) -1 : (byte) 49;
                break;
            case 117484:
                b = !extension.equals("wav") ? (byte) -1 : (byte) 12;
                break;
            case 118660:
                b = !extension.equals("xht") ? (byte) -1 : (byte) 6;
                break;
            case 118807:
                b = !extension.equals("xml") ? (byte) -1 : (byte) 35;
                break;
            case 120609:
                if (!extension.equals("zip")) {
                    b = -1;
                }
                break;
            case 3000872:
                b = !extension.equals("apng") ? (byte) -1 : (byte) 21;
                break;
            case 3145576:
                b = !extension.equals("flac") ? (byte) -1 : (byte) 8;
                break;
            case 3213227:
                b = !extension.equals("html") ? (byte) -1 : (byte) 28;
                break;
            case 3259225:
                b = !extension.equals("jfif") ? (byte) -1 : (byte) 17;
                break;
            case 3268712:
                b = !extension.equals("jpeg") ? (byte) -1 : (byte) 15;
                break;
            case 3271912:
                b = !extension.equals("json") ? (byte) -1 : (byte) 44;
                break;
            case 3358085:
                b = !extension.equals("mpeg") ? (byte) -1 : (byte) 1;
                break;
            case 3418175:
                b = !extension.equals("opus") ? (byte) -1 : (byte) 11;
                break;
            case 3529614:
                b = !extension.equals("shtm") ? (byte) -1 : (byte) 31;
                break;
            case 3542678:
                b = !extension.equals("svgz") ? (byte) -1 : (byte) 23;
                break;
            case 3559925:
                b = !extension.equals("tiff") ? (byte) -1 : (byte) 48;
                break;
            case 3642020:
                b = !extension.equals("wasm") ? (byte) -1 : (byte) 4;
                break;
            case 3645337:
                b = !extension.equals("webm") ? (byte) -1 : (byte) 0;
                break;
            case 3645340:
                b = !extension.equals("webp") ? (byte) -1 : (byte) 24;
                break;
            case 3655064:
                b = !extension.equals("woff") ? (byte) -1 : (byte) 41;
                break;
            case 3678569:
                b = !extension.equals("xhtm") ? (byte) -1 : (byte) 7;
                break;
            case 96488848:
                b = !extension.equals("ehtml") ? (byte) -1 : (byte) 32;
                break;
            case 103877016:
                b = !extension.equals("mhtml") ? (byte) -1 : (byte) 26;
                break;
            case 106703064:
                b = !extension.equals("pjpeg") ? (byte) -1 : (byte) 18;
                break;
            case 109418142:
                b = !extension.equals("shtml") ? (byte) -1 : (byte) 30;
                break;
            case 114035747:
                b = !extension.equals("xhtml") ? (byte) -1 : (byte) 5;
                break;
            default:
                b = -1;
                break;
        }
        switch (b) {
            case 0:
                return "video/webm";
            case 1:
            case 2:
                return "video/mpeg";
            case 3:
                return "audio/mpeg";
            case 4:
                return "application/wasm";
            case 5:
            case 6:
            case 7:
                return "application/xhtml+xml";
            case 8:
                return "audio/flac";
            case 9:
            case 10:
            case 11:
                return "audio/ogg";
            case 12:
                return "audio/wav";
            case 13:
                return "audio/x-m4a";
            case 14:
                return "image/gif";
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
                return "image/jpeg";
            case 20:
                return "image/png";
            case 21:
                return "image/apng";
            case 22:
            case 23:
                return "image/svg+xml";
            case 24:
                return "image/webp";
            case 25:
            case 26:
                return "multipart/related";
            case 27:
                return "text/css";
            case MotionEventCompat.AXIS_RELATIVE_Y /* 28 */:
            case 29:
            case 30:
            case 31:
            case 32:
                return "text/html";
            case MotionEventCompat.AXIS_GENERIC_2 /* 33 */:
            case MotionEventCompat.AXIS_GENERIC_3 /* 34 */:
                return "text/javascript";
            case MotionEventCompat.AXIS_GENERIC_4 /* 35 */:
                return "text/xml";
            case 36:
            case MotionEventCompat.AXIS_GENERIC_6 /* 37 */:
                return "video/mp4";
            case MotionEventCompat.AXIS_GENERIC_7 /* 38 */:
            case MotionEventCompat.AXIS_GENERIC_8 /* 39 */:
                return "video/ogg";
            case MotionEventCompat.AXIS_GENERIC_9 /* 40 */:
                return "image/x-icon";
            case MotionEventCompat.AXIS_GENERIC_10 /* 41 */:
                return "application/font-woff";
            case MotionEventCompat.AXIS_GENERIC_11 /* 42 */:
            case MotionEventCompat.AXIS_GENERIC_12 /* 43 */:
                return "application/gzip";
            case MotionEventCompat.AXIS_GENERIC_13 /* 44 */:
                return "application/json";
            case MotionEventCompat.AXIS_GENERIC_14 /* 45 */:
                return "application/pdf";
            case MotionEventCompat.AXIS_GENERIC_15 /* 46 */:
                return "application/zip";
            case MotionEventCompat.AXIS_GENERIC_16 /* 47 */:
                return "image/bmp";
            case 48:
            case 49:
                return "image/tiff";
            default:
                return null;
        }
    }
}
