package androidx.activity;

import android.os.Build;
import android.window.BackEvent;
import androidx.core.app.NotificationCompat;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import kotlin.Metadata;
import kotlin.annotation.AnnotationRetention;
import kotlin.annotation.AnnotationTarget;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: BackEventCompat.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\u000e\n\u0002\b\u0003\u0018\u0000 \u001b2\u00020\u0001:\u0002\u001a\u001bB5\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t¢\u0006\u0004\b\n\u0010\u000bB\u0011\b\u0017\u0012\u0006\u0010\f\u001a\u00020\r¢\u0006\u0004\b\n\u0010\u000eJ\b\u0010\u0017\u001a\u00020\rH\u0007J\b\u0010\u0018\u001a\u00020\u0019H\u0016R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0010R\u0011\u0010\u0005\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0010R\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016¨\u0006\u001c"}, d2 = {"Landroidx/activity/BackEventCompat;", "", "touchX", "", "touchY", NotificationCompat.CATEGORY_PROGRESS, "swipeEdge", "", "frameTimeMillis", "", "<init>", "(FFFIJ)V", "backEvent", "Landroid/window/BackEvent;", "(Landroid/window/BackEvent;)V", "getTouchX", "()F", "getTouchY", "getProgress", "getSwipeEdge", "()I", "getFrameTimeMillis", "()J", "toBackEvent", "toString", "", "SwipeEdge", "Companion", "activity_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final class BackEventCompat {
    public static final int EDGE_LEFT = 0;
    public static final int EDGE_NONE = 2;
    public static final int EDGE_RIGHT = 1;
    private final long frameTimeMillis;
    private final float progress;
    private final int swipeEdge;
    private final float touchX;
    private final float touchY;

    /* JADX INFO: compiled from: BackEventCompat.kt */
    @Target({ElementType.TYPE_USE})
    @Metadata(d1 = {"\u0000\n\n\u0002\u0018\u0002\n\u0002\u0010\u001b\n\u0000\b\u0087\u0002\u0018\u00002\u00020\u0001B\u0000¨\u0006\u0002"}, d2 = {"Landroidx/activity/BackEventCompat$SwipeEdge;", "", "activity_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
    @kotlin.annotation.Target(allowedTargets = {AnnotationTarget.TYPE})
    @Retention(RetentionPolicy.SOURCE)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    public @interface SwipeEdge {
    }

    public BackEventCompat(float touchX, float touchY, float progress, int swipeEdge, long frameTimeMillis) {
        this.touchX = touchX;
        this.touchY = touchY;
        this.progress = progress;
        this.swipeEdge = swipeEdge;
        this.frameTimeMillis = frameTimeMillis;
    }

    /* JADX WARN: Illegal instructions before constructor call */
    public /* synthetic */ BackEventCompat(float f, float f2, float f3, int i, long j, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        long j2;
        if ((i2 & 16) == 0) {
            j2 = j;
        } else {
            j2 = 0;
        }
        this(f, f2, f3, i, j2);
    }

    public final float getTouchX() {
        return this.touchX;
    }

    public final float getTouchY() {
        return this.touchY;
    }

    public final float getProgress() {
        return this.progress;
    }

    public final int getSwipeEdge() {
        return this.swipeEdge;
    }

    public final long getFrameTimeMillis() {
        return this.frameTimeMillis;
    }

    public BackEventCompat(float touchX, float touchY, float progress, int swipeEdge) {
        this(touchX, touchY, progress, swipeEdge, 0L, 16, null);
    }

    /* JADX WARN: Illegal instructions before constructor call */
    public BackEventCompat(BackEvent backEvent) {
        long jFrameTimeMillis;
        Intrinsics.checkNotNullParameter(backEvent, "backEvent");
        float f = Api34Impl.INSTANCE.touchX(backEvent);
        float f2 = Api34Impl.INSTANCE.touchY(backEvent);
        float fProgress = Api34Impl.INSTANCE.progress(backEvent);
        int iSwipeEdge = Api34Impl.INSTANCE.swipeEdge(backEvent);
        if (Build.VERSION.SDK_INT >= 36) {
            jFrameTimeMillis = Api36Impl.INSTANCE.frameTimeMillis(backEvent);
        } else {
            jFrameTimeMillis = 0;
        }
        this(f, f2, fProgress, iSwipeEdge, jFrameTimeMillis);
    }

    public final BackEvent toBackEvent() {
        if (Build.VERSION.SDK_INT >= 36) {
            return Api36Impl.INSTANCE.createOnBackEvent(this.touchX, this.touchY, this.progress, this.swipeEdge, this.frameTimeMillis);
        }
        return Api34Impl.INSTANCE.createOnBackEvent(this.touchX, this.touchY, this.progress, this.swipeEdge);
    }

    public String toString() {
        return "BackEventCompat{touchX=" + this.touchX + ", touchY=" + this.touchY + ", progress=" + this.progress + ", swipeEdge=" + this.swipeEdge + ", frameTimeMillis=" + this.frameTimeMillis + '}';
    }
}
