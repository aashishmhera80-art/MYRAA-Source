package kotlinx.coroutines.sync;

import androidx.concurrent.futures.AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugProbesKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.KFunction;
import kotlinx.coroutines.CancellableContinuation;
import kotlinx.coroutines.CancellableContinuationImpl;
import kotlinx.coroutines.CancellableContinuationKt;
import kotlinx.coroutines.DebugKt;
import kotlinx.coroutines.Waiter;
import kotlinx.coroutines.channels.ChannelSegment$$ExternalSyntheticBackportWithForwarding0;
import kotlinx.coroutines.internal.ConcurrentLinkedListKt;
import kotlinx.coroutines.internal.Segment;
import kotlinx.coroutines.internal.SegmentOrClosed;
import kotlinx.coroutines.selects.SelectInstance;

/* JADX INFO: compiled from: Semaphore.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000h\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0003\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0005\b\u0010\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003¢\u0006\u0002\u0010\u0005J\u000e\u0010\u0016\u001a\u00020\u0014H\u0096@¢\u0006\u0002\u0010\u0017Jb\u0010\u0016\u001a\u00020\u0014\"\u0004\b\u0000\u0010\u00182\u0006\u0010\u0019\u001a\u0002H\u00182!\u0010\u001a\u001a\u001d\u0012\u0013\u0012\u0011H\u0018¢\u0006\f\b\u001b\u0012\b\b\u001c\u0012\u0004\b\b(\u0019\u0012\u0004\u0012\u00020\u001d0\u00122!\u0010\u001e\u001a\u001d\u0012\u0013\u0012\u0011H\u0018¢\u0006\f\b\u001b\u0012\b\b\u001c\u0012\u0004\b\b(\u0019\u0012\u0004\u0012\u00020\u00140\u0012H\u0083\b¢\u0006\u0002\u0010\u001fJ\u0016\u0010\u0016\u001a\u00020\u00142\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00140 H\u0005J\u000e\u0010!\u001a\u00020\u0014H\u0082@¢\u0006\u0002\u0010\u0017J\u0010\u0010\"\u001a\u00020\u001d2\u0006\u0010\u0019\u001a\u00020#H\u0002J\b\u0010$\u001a\u00020\u0014H\u0002J\b\u0010%\u001a\u00020\u0003H\u0002J\u001e\u0010&\u001a\u00020\u00142\n\u0010'\u001a\u0006\u0012\u0002\b\u00030(2\b\u0010)\u001a\u0004\u0018\u00010*H\u0004J\b\u0010+\u001a\u00020\u0014H\u0016J\b\u0010,\u001a\u00020\u001dH\u0016J\b\u0010-\u001a\u00020\u001dH\u0002J\f\u0010.\u001a\u00020\u001d*\u00020*H\u0002R\t\u0010\u0006\u001a\u00020\u0007X\u0082\u0004R\u0014\u0010\b\u001a\u00020\u00038VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\t\u0010\nR\t\u0010\u000b\u001a\u00020\fX\u0082\u0004R\t\u0010\r\u001a\u00020\fX\u0082\u0004R\u000f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000fX\u0082\u0004R\u001a\u0010\u0011\u001a\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u00020\u00140\u0012X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00100\u000fX\u0082\u0004¨\u0006/"}, d2 = {"Lkotlinx/coroutines/sync/SemaphoreImpl;", "Lkotlinx/coroutines/sync/Semaphore;", "permits", "", "acquiredPermits", "(II)V", "_availablePermits", "Lkotlinx/atomicfu/AtomicInt;", "availablePermits", "getAvailablePermits", "()I", "deqIdx", "Lkotlinx/atomicfu/AtomicLong;", "enqIdx", "head", "Lkotlinx/atomicfu/AtomicRef;", "Lkotlinx/coroutines/sync/SemaphoreSegment;", "onCancellationRelease", "Lkotlin/Function1;", "", "", "tail", "acquire", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "W", "waiter", "suspend", "Lkotlin/ParameterName;", "name", "", "onAcquired", "(Ljava/lang/Object;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;)V", "Lkotlinx/coroutines/CancellableContinuation;", "acquireSlowPath", "addAcquireToQueue", "Lkotlinx/coroutines/Waiter;", "coerceAvailablePermitsAtMaximum", "decPermits", "onAcquireRegFunction", "select", "Lkotlinx/coroutines/selects/SelectInstance;", "ignoredParam", "", "release", "tryAcquire", "tryResumeNextFromQueue", "tryResumeAcquire", "kotlinx-coroutines-core"}, k = 1, mv = {1, 9, 0}, xi = 48)
public class SemaphoreImpl implements Semaphore {
    private volatile /* synthetic */ int _availablePermits$volatile;
    private volatile /* synthetic */ long deqIdx$volatile;
    private volatile /* synthetic */ long enqIdx$volatile;
    private volatile /* synthetic */ Object head$volatile;
    private final Function1<Throwable, Unit> onCancellationRelease;
    private final int permits;
    private volatile /* synthetic */ Object tail$volatile;
    private static final /* synthetic */ AtomicReferenceFieldUpdater head$volatile$FU = AtomicReferenceFieldUpdater.newUpdater(SemaphoreImpl.class, Object.class, "head$volatile");
    private static final /* synthetic */ AtomicLongFieldUpdater deqIdx$volatile$FU = AtomicLongFieldUpdater.newUpdater(SemaphoreImpl.class, "deqIdx$volatile");
    private static final /* synthetic */ AtomicReferenceFieldUpdater tail$volatile$FU = AtomicReferenceFieldUpdater.newUpdater(SemaphoreImpl.class, Object.class, "tail$volatile");
    private static final /* synthetic */ AtomicLongFieldUpdater enqIdx$volatile$FU = AtomicLongFieldUpdater.newUpdater(SemaphoreImpl.class, "enqIdx$volatile");
    private static final /* synthetic */ AtomicIntegerFieldUpdater _availablePermits$volatile$FU = AtomicIntegerFieldUpdater.newUpdater(SemaphoreImpl.class, "_availablePermits$volatile");

    private final /* synthetic */ long getDeqIdx$volatile() {
        return this.deqIdx$volatile;
    }

    private final /* synthetic */ long getEnqIdx$volatile() {
        return this.enqIdx$volatile;
    }

    private final /* synthetic */ Object getHead$volatile() {
        return this.head$volatile;
    }

    private final /* synthetic */ Object getTail$volatile() {
        return this.tail$volatile;
    }

    private final /* synthetic */ int get_availablePermits$volatile() {
        return this._availablePermits$volatile;
    }

    private final /* synthetic */ void setDeqIdx$volatile(long j) {
        this.deqIdx$volatile = j;
    }

    private final /* synthetic */ void setEnqIdx$volatile(long j) {
        this.enqIdx$volatile = j;
    }

    private final /* synthetic */ void setHead$volatile(Object obj) {
        this.head$volatile = obj;
    }

    private final /* synthetic */ void setTail$volatile(Object obj) {
        this.tail$volatile = obj;
    }

    private final /* synthetic */ void set_availablePermits$volatile(int i) {
        this._availablePermits$volatile = i;
    }

    @Override // kotlinx.coroutines.sync.Semaphore
    public Object acquire(Continuation<? super Unit> continuation) {
        return acquire$suspendImpl(this, continuation);
    }

    public SemaphoreImpl(int permits, int acquiredPermits) {
        this.permits = permits;
        if (!(this.permits > 0)) {
            throw new IllegalArgumentException(("Semaphore should have at least 1 permit, but had " + this.permits).toString());
        }
        if (!(acquiredPermits >= 0 && acquiredPermits <= this.permits)) {
            throw new IllegalArgumentException(("The number of acquired permits should be in 0.." + this.permits).toString());
        }
        SemaphoreSegment s = new SemaphoreSegment(0L, null, 2);
        this.head$volatile = s;
        this.tail$volatile = s;
        this._availablePermits$volatile = this.permits - acquiredPermits;
        this.onCancellationRelease = new Function1<Throwable, Unit>() { // from class: kotlinx.coroutines.sync.SemaphoreImpl$onCancellationRelease$1
            {
                super(1);
            }

            @Override // kotlin.jvm.functions.Function1
            public /* bridge */ /* synthetic */ Unit invoke(Throwable th) {
                invoke2(th);
                return Unit.INSTANCE;
            }

            /* JADX INFO: renamed from: invoke, reason: avoid collision after fix types in other method */
            public final void invoke2(Throwable th) {
                this.this$0.release();
            }
        };
    }

    @Override // kotlinx.coroutines.sync.Semaphore
    public int getAvailablePermits() {
        return Math.max(_availablePermits$volatile$FU.get(this), 0);
    }

    @Override // kotlinx.coroutines.sync.Semaphore
    public boolean tryAcquire() {
        while (true) {
            int p = _availablePermits$volatile$FU.get(this);
            if (p > this.permits) {
                coerceAvailablePermitsAtMaximum();
            } else {
                if (p <= 0) {
                    return false;
                }
                if (_availablePermits$volatile$FU.compareAndSet(this, p, p - 1)) {
                    return true;
                }
            }
        }
    }

    static /* synthetic */ Object acquire$suspendImpl(SemaphoreImpl $this, Continuation<? super Unit> continuation) {
        Object objAcquireSlowPath;
        int p = $this.decPermits();
        return (p <= 0 && (objAcquireSlowPath = $this.acquireSlowPath(continuation)) == IntrinsicsKt.getCOROUTINE_SUSPENDED()) ? objAcquireSlowPath : Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final Object acquireSlowPath(Continuation<? super Unit> continuation) {
        CancellableContinuationImpl cancellable$iv = CancellableContinuationKt.getOrCreateCancellableContinuation(IntrinsicsKt.intercepted(continuation));
        try {
            if (!addAcquireToQueue(cancellable$iv)) {
                acquire((CancellableContinuation<? super Unit>) cancellable$iv);
            }
            Object result = cancellable$iv.getResult();
            if (result == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
                DebugProbesKt.probeCoroutineSuspended(continuation);
            }
            return result == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? result : Unit.INSTANCE;
        } catch (Throwable e$iv) {
            cancellable$iv.releaseClaimedReusableContinuation$kotlinx_coroutines_core();
            throw e$iv;
        }
    }

    protected final void acquire(CancellableContinuation<? super Unit> waiter) {
        while (p$iv <= 0) {
            Intrinsics.checkNotNull(waiter, "null cannot be cast to non-null type kotlinx.coroutines.Waiter");
            if (addAcquireToQueue((Waiter) waiter)) {
                return;
            }
        }
        waiter.resume(Unit.INSTANCE, this.onCancellationRelease);
    }

    private final <W> void acquire(W waiter, Function1<? super W, Boolean> suspend, Function1<? super W, Unit> onAcquired) {
        while (p <= 0) {
            if (suspend.invoke(waiter).booleanValue()) {
                return;
            }
        }
        onAcquired.invoke(waiter);
    }

    protected final void onAcquireRegFunction(SelectInstance<?> select, Object ignoredParam) {
        while (p$iv <= 0) {
            Intrinsics.checkNotNull(select, "null cannot be cast to non-null type kotlinx.coroutines.Waiter");
            if (addAcquireToQueue((Waiter) select)) {
                return;
            }
        }
        select.selectInRegistrationPhase(Unit.INSTANCE);
    }

    private final int decPermits() {
        int p;
        do {
            p = _availablePermits$volatile$FU.getAndDecrement(this);
        } while (p > this.permits);
        return p;
    }

    @Override // kotlinx.coroutines.sync.Semaphore
    public void release() {
        do {
            int p = _availablePermits$volatile$FU.getAndIncrement(this);
            if (p >= this.permits) {
                coerceAvailablePermitsAtMaximum();
                throw new IllegalStateException(("The number of released permits cannot be greater than " + this.permits).toString());
            }
            if (p >= 0) {
                return;
            }
        } while (!tryResumeNextFromQueue());
    }

    private final void coerceAvailablePermitsAtMaximum() {
        int cur;
        do {
            cur = _availablePermits$volatile$FU.get(this);
            if (cur <= this.permits) {
                return;
            }
        } while (!_availablePermits$volatile$FU.compareAndSet(this, cur, this.permits));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final boolean addAcquireToQueue(Waiter waiter) {
        Object s$iv;
        long enqIdx;
        boolean z;
        boolean z2;
        SemaphoreSegment curTail;
        boolean z3;
        SemaphoreSegment curTail2 = (SemaphoreSegment) tail$volatile$FU.get(this);
        long enqIdx2 = enqIdx$volatile$FU.getAndIncrement(this);
        KFunction createNewSegment = SemaphoreImpl$addAcquireToQueue$createNewSegment$1.INSTANCE;
        AtomicReferenceFieldUpdater handler$atomicfu$iv = tail$volatile$FU;
        long id$iv = enqIdx2 / ((long) SemaphoreKt.SEGMENT_SIZE);
        while (true) {
            s$iv = ConcurrentLinkedListKt.findSegmentInternal(curTail2, id$iv, (Function2) createNewSegment);
            if (SegmentOrClosed.m1722isClosedimpl(s$iv)) {
                enqIdx = enqIdx2;
                z = false;
                z2 = true;
                break;
            }
            Segment to$iv$iv = SegmentOrClosed.m1720getSegmentimpl(s$iv);
            while (true) {
                Segment cur$iv$iv = (Segment) handler$atomicfu$iv.get(this);
                z = false;
                z2 = true;
                curTail = curTail2;
                enqIdx = enqIdx2;
                if (cur$iv$iv.id >= to$iv$iv.id) {
                    z3 = true;
                    break;
                }
                if (!to$iv$iv.tryIncPointers$kotlinx_coroutines_core()) {
                    z3 = false;
                    break;
                }
                if (AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0.m(handler$atomicfu$iv, this, cur$iv$iv, to$iv$iv)) {
                    if (cur$iv$iv.decPointers$kotlinx_coroutines_core()) {
                        cur$iv$iv.remove();
                    }
                    z3 = true;
                    break;
                }
                if (to$iv$iv.decPointers$kotlinx_coroutines_core()) {
                    to$iv$iv.remove();
                }
                curTail2 = curTail;
                enqIdx2 = enqIdx;
            }
            if (z3) {
                break;
            }
            curTail2 = curTail;
            enqIdx2 = enqIdx;
        }
        SemaphoreSegment segment = (SemaphoreSegment) SegmentOrClosed.m1720getSegmentimpl(s$iv);
        int i = (int) (enqIdx % ((long) SemaphoreKt.SEGMENT_SIZE));
        if (!ChannelSegment$$ExternalSyntheticBackportWithForwarding0.m(segment.getAcquirers(), i, null, waiter)) {
            Object expected$iv = SemaphoreKt.PERMIT;
            Object value$iv = SemaphoreKt.TAKEN;
            if (ChannelSegment$$ExternalSyntheticBackportWithForwarding0.m(segment.getAcquirers(), i, expected$iv, value$iv)) {
                if (waiter instanceof CancellableContinuation) {
                    Intrinsics.checkNotNull(waiter, "null cannot be cast to non-null type kotlinx.coroutines.CancellableContinuation<kotlin.Unit>");
                    ((CancellableContinuation) waiter).resume(Unit.INSTANCE, this.onCancellationRelease);
                } else if (waiter instanceof SelectInstance) {
                    ((SelectInstance) waiter).selectInRegistrationPhase(Unit.INSTANCE);
                } else {
                    throw new IllegalStateException(("unexpected: " + waiter).toString());
                }
                return z2;
            }
            if (DebugKt.getASSERTIONS_ENABLED()) {
                if (!(segment.getAcquirers().get(i) == SemaphoreKt.BROKEN ? z2 : z)) {
                    throw new AssertionError();
                }
            }
            return z;
        }
        waiter.invokeOnCancellation(segment, i);
        return z2;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r11v1 */
    /* JADX WARN: Type inference failed for: r11v2, types: [int] */
    /* JADX WARN: Type inference failed for: r11v5 */
    /* JADX WARN: Type inference failed for: r12v2 */
    private final boolean tryResumeNextFromQueue() {
        Object objFindSegmentInternal;
        boolean z;
        boolean z2;
        boolean z3;
        SemaphoreSegment semaphoreSegment = (SemaphoreSegment) head$volatile$FU.get(this);
        long andIncrement = deqIdx$volatile$FU.getAndIncrement(this);
        long j = andIncrement / ((long) SemaphoreKt.SEGMENT_SIZE);
        SemaphoreImpl$tryResumeNextFromQueue$createNewSegment$1 semaphoreImpl$tryResumeNextFromQueue$createNewSegment$1 = SemaphoreImpl$tryResumeNextFromQueue$createNewSegment$1.INSTANCE;
        AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = head$volatile$FU;
        do {
            objFindSegmentInternal = ConcurrentLinkedListKt.findSegmentInternal(semaphoreSegment, j, semaphoreImpl$tryResumeNextFromQueue$createNewSegment$1);
            if (SegmentOrClosed.m1722isClosedimpl(objFindSegmentInternal)) {
                z = false;
                z2 = true;
                break;
            }
            Segment segmentM1720getSegmentimpl = SegmentOrClosed.m1720getSegmentimpl(objFindSegmentInternal);
            while (true) {
                Segment segment = (Segment) atomicReferenceFieldUpdater.get(this);
                z = false;
                z2 = true;
                if (segment.id >= segmentM1720getSegmentimpl.id) {
                    z3 = true;
                    break;
                }
                if (!segmentM1720getSegmentimpl.tryIncPointers$kotlinx_coroutines_core()) {
                    z3 = false;
                    break;
                }
                if (AbstractResolvableFuture$SafeAtomicHelper$$ExternalSyntheticBackportWithForwarding0.m(atomicReferenceFieldUpdater, this, segment, segmentM1720getSegmentimpl)) {
                    if (segment.decPointers$kotlinx_coroutines_core()) {
                        segment.remove();
                    }
                    z3 = true;
                    break;
                }
                if (segmentM1720getSegmentimpl.decPointers$kotlinx_coroutines_core()) {
                    segmentM1720getSegmentimpl.remove();
                }
            }
        } while (!z3);
        SemaphoreSegment semaphoreSegment2 = (SemaphoreSegment) SegmentOrClosed.m1720getSegmentimpl(objFindSegmentInternal);
        semaphoreSegment2.cleanPrev();
        if (semaphoreSegment2.id > j) {
            return z;
        }
        int i = (int) (andIncrement % ((long) SemaphoreKt.SEGMENT_SIZE));
        Object andSet = semaphoreSegment2.getAcquirers().getAndSet(i, SemaphoreKt.PERMIT);
        if (andSet != null) {
            return andSet == SemaphoreKt.CANCELLED ? z : tryResumeAcquire(andSet);
        }
        int i2 = SemaphoreKt.MAX_SPIN_CYCLES;
        for (?? r11 = z; r11 < i2; r11++) {
            SemaphoreSegment semaphoreSegment3 = semaphoreSegment;
            if (semaphoreSegment2.getAcquirers().get(i) == SemaphoreKt.TAKEN) {
                return z2;
            }
            semaphoreSegment = semaphoreSegment3;
        }
        return !ChannelSegment$$ExternalSyntheticBackportWithForwarding0.m(semaphoreSegment2.getAcquirers(), i, SemaphoreKt.PERMIT, SemaphoreKt.BROKEN);
    }

    private final boolean tryResumeAcquire(Object $this$tryResumeAcquire) {
        if ($this$tryResumeAcquire instanceof CancellableContinuation) {
            Intrinsics.checkNotNull($this$tryResumeAcquire, "null cannot be cast to non-null type kotlinx.coroutines.CancellableContinuation<kotlin.Unit>");
            Object token = ((CancellableContinuation) $this$tryResumeAcquire).tryResume(Unit.INSTANCE, null, this.onCancellationRelease);
            if (token == null) {
                return false;
            }
            ((CancellableContinuation) $this$tryResumeAcquire).completeResume(token);
            return true;
        }
        if ($this$tryResumeAcquire instanceof SelectInstance) {
            return ((SelectInstance) $this$tryResumeAcquire).trySelect(this, Unit.INSTANCE);
        }
        throw new IllegalStateException(("unexpected: " + $this$tryResumeAcquire).toString());
    }
}
