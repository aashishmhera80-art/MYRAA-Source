package androidx.core.os;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;

/* JADX INFO: compiled from: Profiling.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b7\u0018\u0000 \b2\u00020\u0001:\u0003\b\t\nB\u0011\b\u0004\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005R\u0014\u0010\u0002\u001a\u00020\u0003X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u0082\u0001\u0002\u000b\f¨\u0006\r"}, d2 = {"Landroidx/core/os/BufferFillPolicy;", "", "value", "", "<init>", "(I)V", "getValue$core_release", "()I", "Companion", "Discard", "RingBuffer", "Landroidx/core/os/BufferFillPolicy$Discard;", "Landroidx/core/os/BufferFillPolicy$RingBuffer;", "core_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
public abstract class BufferFillPolicy {
    public static final BufferFillPolicy DISCARD = new Discard();
    public static final BufferFillPolicy RING_BUFFER = new RingBuffer();
    private final int value;

    public /* synthetic */ BufferFillPolicy(int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(i);
    }

    private BufferFillPolicy(int value) {
        this.value = value;
    }

    /* JADX INFO: renamed from: getValue$core_release, reason: from getter */
    public final int getValue() {
        return this.value;
    }

    /* JADX INFO: compiled from: Profiling.kt */
    @Metadata(d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0002\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003¨\u0006\u0004"}, d2 = {"Landroidx/core/os/BufferFillPolicy$Discard;", "Landroidx/core/os/BufferFillPolicy;", "<init>", "()V", "core_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
    private static final class Discard extends BufferFillPolicy {
        public Discard() {
            super(1, null);
        }
    }

    /* JADX INFO: compiled from: Profiling.kt */
    @Metadata(d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0002\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003¨\u0006\u0004"}, d2 = {"Landroidx/core/os/BufferFillPolicy$RingBuffer;", "Landroidx/core/os/BufferFillPolicy;", "<init>", "()V", "core_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
    private static final class RingBuffer extends BufferFillPolicy {
        public RingBuffer() {
            super(2, null);
        }
    }
}
