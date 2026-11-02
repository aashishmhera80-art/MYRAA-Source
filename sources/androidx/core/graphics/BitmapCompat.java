package androidx.core.graphics;

import android.graphics.Bitmap;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.ColorSpace;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build;
import androidx.annotation.ReplaceWith;

/* JADX INFO: loaded from: classes.dex */
public final class BitmapCompat {
    @ReplaceWith(expression = "bitmap.hasMipMap()")
    @Deprecated
    public static boolean hasMipMap(Bitmap bitmap) {
        return bitmap.hasMipMap();
    }

    @ReplaceWith(expression = "bitmap.setHasMipMap(hasMipMap)")
    @Deprecated
    public static void setHasMipMap(Bitmap bitmap, boolean hasMipMap) {
        bitmap.setHasMipMap(hasMipMap);
    }

    @ReplaceWith(expression = "bitmap.getAllocationByteCount()")
    @Deprecated
    public static int getAllocationByteCount(Bitmap bitmap) {
        return bitmap.getAllocationByteCount();
    }

    /* JADX WARN: Type inference failed for: r3v4 */
    /* JADX WARN: Type inference failed for: r3v5, types: [boolean, int] */
    /* JADX WARN: Type inference failed for: r3v7 */
    public static Bitmap createScaledBitmap(Bitmap srcBm, int dstW, int dstH, Rect srcRect, boolean scaleInLinearSpace) {
        int totalStepsY;
        int srcW;
        ?? r3;
        int iSizeAtStep;
        int totalStepsY2;
        int i = dstW;
        int i2 = dstH;
        if (i <= 0 || i2 <= 0) {
            throw new IllegalArgumentException("dstW and dstH must be > 0!");
        }
        if (srcRect != null && (srcRect.isEmpty() || srcRect.left < 0 || srcRect.right > srcBm.getWidth() || srcRect.top < 0 || srcRect.bottom > srcBm.getHeight())) {
            throw new IllegalArgumentException("srcRect must be contained by srcBm!");
        }
        Bitmap src = srcBm;
        if (Build.VERSION.SDK_INT >= 27) {
            src = Api27Impl.copyBitmapIfHardware(srcBm);
        }
        int srcW2 = srcRect != null ? srcRect.width() : srcBm.getWidth();
        int srcH = srcRect != null ? srcRect.height() : srcBm.getHeight();
        float sx = i / srcW2;
        float sy = i2 / srcH;
        int srcX = srcRect != null ? srcRect.left : 0;
        int srcY = srcRect != null ? srcRect.top : 0;
        if (srcX == 0 && srcY == 0 && i == srcBm.getWidth() && i2 == srcBm.getHeight()) {
            if (srcBm.isMutable() && srcBm == src) {
                return srcBm.copy(srcBm.getConfig(), true);
            }
            return src;
        }
        Paint paint = new Paint(1);
        paint.setFilterBitmap(true);
        if (Build.VERSION.SDK_INT >= 29) {
            Api29Impl.setPaintBlendMode(paint);
        } else {
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        }
        if (srcW2 == i && srcH == i2) {
            Bitmap out = Bitmap.createBitmap(i, i2, src.getConfig());
            Canvas canvasForCopy = new Canvas(out);
            canvasForCopy.drawBitmap(src, -srcX, -srcY, paint);
            return out;
        }
        double log2 = Math.log(2.0d);
        int stepsX = sx > 1.0f ? (int) Math.ceil(Math.log(sx) / log2) : (int) Math.floor(Math.log(sx) / log2);
        int stepsY = sy > 1065353216 ? (int) Math.ceil(Math.log(sy) / log2) : (int) Math.floor(Math.log(sy) / log2);
        int totalStepsX = stepsX;
        int totalStepsY3 = stepsY;
        Bitmap dst = null;
        int i3 = 0;
        if (!scaleInLinearSpace || Build.VERSION.SDK_INT < 27 || Api27Impl.isAlreadyF16AndLinear(srcBm)) {
            totalStepsY = totalStepsY3;
        } else {
            if (stepsX > 0) {
                r3 = 1;
                iSizeAtStep = sizeAtStep(srcW2, i, 1, totalStepsX);
            } else {
                r3 = 1;
                iSizeAtStep = srcW2;
            }
            int allocW = iSizeAtStep;
            if (stepsY > 0) {
                totalStepsY = totalStepsY3;
                totalStepsY2 = sizeAtStep(srcH, i2, r3, totalStepsY);
            } else {
                totalStepsY = totalStepsY3;
                totalStepsY2 = srcH;
            }
            Bitmap dst2 = Api27Impl.createBitmapWithSourceColorspace(allocW, totalStepsY2, srcBm, r3);
            Canvas canvasForCopy2 = new Canvas(dst2);
            canvasForCopy2.drawBitmap(src, -srcX, -srcY, paint);
            srcY = 0;
            dst = src;
            src = dst2;
            i3 = 1;
            srcX = 0;
        }
        Rect currRect = new Rect(srcX, srcY, srcW2, srcH);
        Rect nextRect = new Rect();
        Bitmap dst3 = dst;
        int stepsX2 = stepsX;
        while (true) {
            if (stepsX2 == 0 && stepsY == 0) {
                break;
            }
            if (stepsX2 < 0) {
                stepsX2++;
            } else if (stepsX2 > 0) {
                stepsX2--;
            }
            if (stepsY < 0) {
                stepsY++;
            } else if (stepsY > 0) {
                stepsY--;
            }
            int srcY2 = srcY;
            int nextW = sizeAtStep(srcW2, i, stepsX2, totalStepsX);
            int stepsX3 = stepsX2;
            int nextH = sizeAtStep(srcH, i2, stepsY, totalStepsY);
            int stepsY2 = stepsY;
            nextRect.set(0, 0, nextW, nextH);
            boolean lastStep = stepsX3 == 0 && stepsY2 == 0;
            boolean dstSizeIsFinal = dst3 != null && dst3.getWidth() == i && dst3.getHeight() == i2;
            if (dst3 != null && dst3 != srcBm) {
                if (scaleInLinearSpace) {
                    int nextH2 = Build.VERSION.SDK_INT;
                    if (nextH2 < 27 || Api27Impl.isAlreadyF16AndLinear(dst3)) {
                    }
                    Canvas canvas = new Canvas(dst3);
                    canvas.drawBitmap(src, currRect, nextRect, paint);
                    Bitmap swap = src;
                    src = dst3;
                    dst3 = swap;
                    currRect.set(nextRect);
                    i = dstW;
                    i2 = dstH;
                    srcY = srcY2;
                    stepsY = stepsY2;
                    srcW2 = srcW;
                    stepsX2 = stepsX3;
                }
                if (!lastStep || (dstSizeIsFinal && i3 == 0)) {
                    srcW = srcW2;
                }
                Canvas canvas2 = new Canvas(dst3);
                canvas2.drawBitmap(src, currRect, nextRect, paint);
                Bitmap swap2 = src;
                src = dst3;
                dst3 = swap2;
                currRect.set(nextRect);
                i = dstW;
                i2 = dstH;
                srcY = srcY2;
                stepsY = stepsY2;
                srcW2 = srcW;
                stepsX2 = stepsX3;
            }
            if (dst3 != srcBm && dst3 != null) {
                dst3.recycle();
            }
            int lastScratchStep = i3;
            int allocW2 = sizeAtStep(srcW2, i, stepsX3 > 0 ? lastScratchStep : stepsX3, totalStepsX);
            int allocH = sizeAtStep(srcH, i2, stepsY2 > 0 ? lastScratchStep : stepsY2, totalStepsY);
            srcW = srcW2;
            if (Build.VERSION.SDK_INT >= 27) {
                boolean linear = scaleInLinearSpace && !lastStep;
                dst3 = Api27Impl.createBitmapWithSourceColorspace(allocW2, allocH, srcBm, linear);
            } else {
                dst3 = Bitmap.createBitmap(allocW2, allocH, src.getConfig());
            }
            Canvas canvas3 = new Canvas(dst3);
            canvas3.drawBitmap(src, currRect, nextRect, paint);
            Bitmap swap3 = src;
            src = dst3;
            dst3 = swap3;
            currRect.set(nextRect);
            i = dstW;
            i2 = dstH;
            srcY = srcY2;
            stepsY = stepsY2;
            srcW2 = srcW;
            stepsX2 = stepsX3;
        }
        if (dst3 != srcBm && dst3 != null) {
            dst3.recycle();
        }
        return src;
    }

    static int sizeAtStep(int srcSize, int dstSize, int step, int totalSteps) {
        if (step == 0) {
            return dstSize;
        }
        return step > 0 ? (1 << (totalSteps - step)) * srcSize : dstSize << ((-step) - 1);
    }

    private BitmapCompat() {
    }

    static class Api27Impl {
        private Api27Impl() {
        }

        static Bitmap createBitmapWithSourceColorspace(int w, int h, Bitmap src, boolean linear) {
            Bitmap.Config config = src.getConfig();
            ColorSpace colorSpace = src.getColorSpace();
            ColorSpace linearCs = ColorSpace.get(ColorSpace.Named.LINEAR_EXTENDED_SRGB);
            if (linear && !src.getColorSpace().equals(linearCs)) {
                config = Bitmap.Config.RGBA_F16;
                colorSpace = linearCs;
            } else if (src.getConfig() == Bitmap.Config.HARDWARE) {
                config = Bitmap.Config.ARGB_8888;
                if (Build.VERSION.SDK_INT >= 31) {
                    config = Api31Impl.getHardwareBitmapConfig(src);
                }
            }
            return Bitmap.createBitmap(w, h, config, src.hasAlpha(), colorSpace);
        }

        static boolean isAlreadyF16AndLinear(Bitmap b) {
            ColorSpace linearCs = ColorSpace.get(ColorSpace.Named.LINEAR_EXTENDED_SRGB);
            return b.getConfig() == Bitmap.Config.RGBA_F16 && b.getColorSpace().equals(linearCs);
        }

        static Bitmap copyBitmapIfHardware(Bitmap bm) {
            if (bm.getConfig() == Bitmap.Config.HARDWARE) {
                Bitmap.Config newConfig = Bitmap.Config.ARGB_8888;
                if (Build.VERSION.SDK_INT >= 31) {
                    newConfig = Api31Impl.getHardwareBitmapConfig(bm);
                }
                return bm.copy(newConfig, true);
            }
            return bm;
        }
    }

    static class Api29Impl {
        private Api29Impl() {
        }

        static void setPaintBlendMode(Paint paint) {
            paint.setBlendMode(BlendMode.SRC);
        }
    }

    static class Api31Impl {
        private Api31Impl() {
        }

        static Bitmap.Config getHardwareBitmapConfig(Bitmap bm) {
            if (bm.getHardwareBuffer().getFormat() == 22) {
                return Bitmap.Config.RGBA_F16;
            }
            return Bitmap.Config.ARGB_8888;
        }
    }
}
