package kotlinx.coroutines;

import java.util.concurrent.CancellationException;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.jvm.internal.CoroutineStackFrame;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.internal.DispatchedContinuation;
import kotlinx.coroutines.internal.StackTraceRecoveryKt;
import kotlinx.coroutines.internal.ThreadContextKt;
import kotlinx.coroutines.scheduling.Task;
import kotlinx.coroutines.scheduling.TaskContext;

/* JADX INFO: compiled from: DispatchedTask.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u00004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0003\n\u0002\b\u000f\b!\u0018\u0000*\u0006\b\u0000\u0010\u0001 \u00002\u00060\u0002j\u0002`\u0003B\u000f\b\u0000\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\u001f\u0010\u000b\u001a\u00020\f2\b\u0010\r\u001a\u0004\u0018\u00010\u000e2\u0006\u0010\u000f\u001a\u00020\u0010H\u0010¢\u0006\u0002\b\u0011J\u0019\u0010\u0012\u001a\u0004\u0018\u00010\u00102\b\u0010\u0013\u001a\u0004\u0018\u00010\u000eH\u0010¢\u0006\u0002\b\u0014J\u001f\u0010\u0015\u001a\u0002H\u0001\"\u0004\b\u0001\u0010\u00012\b\u0010\u0013\u001a\u0004\u0018\u00010\u000eH\u0010¢\u0006\u0004\b\u0016\u0010\u0017J!\u0010\u0018\u001a\u00020\f2\b\u0010\u0019\u001a\u0004\u0018\u00010\u00102\b\u0010\u001a\u001a\u0004\u0018\u00010\u0010H\u0000¢\u0006\u0002\b\u001bJ\u0006\u0010\u001c\u001a\u00020\fJ\u000f\u0010\u001d\u001a\u0004\u0018\u00010\u000eH ¢\u0006\u0002\b\u001eR\u0018\u0010\u0007\u001a\b\u0012\u0004\u0012\u00028\u00000\bX \u0004¢\u0006\u0006\u001a\u0004\b\t\u0010\nR\u0012\u0010\u0004\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000¨\u0006\u001f"}, d2 = {"Lkotlinx/coroutines/DispatchedTask;", "T", "Lkotlinx/coroutines/scheduling/Task;", "Lkotlinx/coroutines/SchedulerTask;", "resumeMode", "", "(I)V", "delegate", "Lkotlin/coroutines/Continuation;", "getDelegate$kotlinx_coroutines_core", "()Lkotlin/coroutines/Continuation;", "cancelCompletedResult", "", "takenState", "", "cause", "", "cancelCompletedResult$kotlinx_coroutines_core", "getExceptionalResult", "state", "getExceptionalResult$kotlinx_coroutines_core", "getSuccessfulResult", "getSuccessfulResult$kotlinx_coroutines_core", "(Ljava/lang/Object;)Ljava/lang/Object;", "handleFatalException", "exception", "finallyException", "handleFatalException$kotlinx_coroutines_core", "run", "takeState", "takeState$kotlinx_coroutines_core", "kotlinx-coroutines-core"}, k = 1, mv = {1, 9, 0}, xi = 48)
public abstract class DispatchedTask<T> extends Task {
    public int resumeMode;

    public abstract Continuation<T> getDelegate$kotlinx_coroutines_core();

    public abstract Object takeState$kotlinx_coroutines_core();

    public DispatchedTask(int resumeMode) {
        this.resumeMode = resumeMode;
    }

    public void cancelCompletedResult$kotlinx_coroutines_core(Object takenState, Throwable cause) {
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <T> T getSuccessfulResult$kotlinx_coroutines_core(Object state) {
        return state;
    }

    public Throwable getExceptionalResult$kotlinx_coroutines_core(Object state) {
        CompletedExceptionally completedExceptionally = state instanceof CompletedExceptionally ? (CompletedExceptionally) state : null;
        if (completedExceptionally != null) {
            return completedExceptionally.cause;
        }
        return null;
    }

    /* JADX WARN: Code duplicated, block: B:47:0x00c5  */
    /* JADX WARN: Code duplicated, block: B:49:0x00cd A[Catch: all -> 0x010a, TryCatch #4 {all -> 0x010a, blocks: (B:45:0x00b5, B:46:0x00b8, B:51:0x00e8, B:41:0x00a5, B:49:0x00cd, B:50:0x00db), top: B:90:0x0076 }] */
    /* JADX WARN: Code duplicated, block: B:50:0x00db A[Catch: all -> 0x010a, TryCatch #4 {all -> 0x010a, blocks: (B:45:0x00b5, B:46:0x00b8, B:51:0x00e8, B:41:0x00a5, B:49:0x00cd, B:50:0x00db), top: B:90:0x0076 }] */
    /* JADX WARN: Code duplicated, block: B:55:0x00f4 A[Catch: all -> 0x011e, TRY_LEAVE, TryCatch #7 {all -> 0x011e, blocks: (B:53:0x00ee, B:55:0x00f4, B:66:0x0113, B:70:0x011d, B:68:0x0119), top: B:96:0x004e }] */
    /* JADX WARN: Code duplicated, block: B:68:0x0119 A[Catch: all -> 0x011e, TryCatch #7 {all -> 0x011e, blocks: (B:53:0x00ee, B:55:0x00f4, B:66:0x0113, B:70:0x011d, B:68:0x0119), top: B:96:0x004e }] */
    @Override // java.lang.Runnable
    public final void run() {
        Object result;
        UndispatchedCoroutine<?> undispatchedCoroutineUpdateUndispatchedCompletion;
        TaskContext taskContext;
        CancellationException cancellationExceptionRecoverFromStackFrame;
        if (DebugKt.getASSERTIONS_ENABLED()) {
            if (!(this.resumeMode != -1)) {
                throw new AssertionError();
            }
        }
        TaskContext taskContext2 = this.taskContext;
        Throwable fatalException = null;
        try {
            Continuation<T> delegate$kotlinx_coroutines_core = getDelegate$kotlinx_coroutines_core();
            Intrinsics.checkNotNull(delegate$kotlinx_coroutines_core, "null cannot be cast to non-null type kotlinx.coroutines.internal.DispatchedContinuation<T of kotlinx.coroutines.DispatchedTask>");
            DispatchedContinuation delegate = (DispatchedContinuation) delegate$kotlinx_coroutines_core;
            Continuation<T> continuation = delegate.continuation;
            Object countOrElement$iv = delegate.countOrElement;
            CoroutineContext context$iv = continuation.get$context();
            Object oldValue$iv = ThreadContextKt.updateThreadContext(context$iv, countOrElement$iv);
            Job job = null;
            if (oldValue$iv != ThreadContextKt.NO_THREAD_ELEMENTS) {
                try {
                    undispatchedCoroutineUpdateUndispatchedCompletion = CoroutineContextKt.updateUndispatchedCompletion(continuation, context$iv, oldValue$iv);
                } catch (Throwable th) {
                    e = th;
                    fatalException = e;
                    try {
                        Result.Companion companion = Result.INSTANCE;
                        taskContext2.afterTask();
                        result = Result.m155constructorimpl(Unit.INSTANCE);
                    } catch (Throwable th2) {
                        th = th2;
                        Result.Companion companion2 = Result.INSTANCE;
                        result = Result.m155constructorimpl(ResultKt.createFailure(th));
                    }
                    handleFatalException$kotlinx_coroutines_core(fatalException, Result.m158exceptionOrNullimpl(result));
                }
            } else {
                undispatchedCoroutineUpdateUndispatchedCompletion = null;
            }
            UndispatchedCoroutine<?> undispatchedCoroutine = undispatchedCoroutineUpdateUndispatchedCompletion;
            try {
                try {
                    CoroutineContext context = continuation.get$context();
                    Object state = takeState$kotlinx_coroutines_core();
                    Throwable exception = getExceptionalResult$kotlinx_coroutines_core(state);
                    if (exception == null) {
                        try {
                            if (DispatchedTaskKt.isCancellableMode(this.resumeMode)) {
                                job = (Job) context.get(Job.INSTANCE);
                            }
                            try {
                                if (job != null || job.isActive()) {
                                    taskContext = taskContext2;
                                    if (exception != null) {
                                        Result.Companion companion3 = Result.INSTANCE;
                                        continuation.resumeWith(Result.m155constructorimpl(ResultKt.createFailure(exception)));
                                    } else {
                                        Result.Companion companion4 = Result.INSTANCE;
                                        continuation.resumeWith(Result.m155constructorimpl(getSuccessfulResult$kotlinx_coroutines_core(state)));
                                    }
                                } else {
                                    CancellationException cause = job.getCancellationException();
                                    cancelCompletedResult$kotlinx_coroutines_core(state, cause);
                                    Result.Companion companion5 = Result.INSTANCE;
                                    if (DebugKt.getRECOVER_STACK_TRACES()) {
                                        taskContext = taskContext2;
                                        try {
                                            if (continuation instanceof CoroutineStackFrame) {
                                                cancellationExceptionRecoverFromStackFrame = StackTraceRecoveryKt.recoverFromStackFrame(cause, (CoroutineStackFrame) continuation);
                                            }
                                            continuation.resumeWith(Result.m155constructorimpl(ResultKt.createFailure(cancellationExceptionRecoverFromStackFrame)));
                                        } catch (Throwable th3) {
                                            th = th3;
                                            if (undispatchedCoroutine != null || undispatchedCoroutine.clearThreadContext()) {
                                                ThreadContextKt.restoreThreadContext(context$iv, oldValue$iv);
                                            }
                                            throw th;
                                        }
                                    } else {
                                        taskContext = taskContext2;
                                    }
                                    cancellationExceptionRecoverFromStackFrame = cause;
                                    continuation.resumeWith(Result.m155constructorimpl(ResultKt.createFailure(cancellationExceptionRecoverFromStackFrame)));
                                }
                                Unit unit = Unit.INSTANCE;
                                if (undispatchedCoroutine != null || undispatchedCoroutine.clearThreadContext()) {
                                    ThreadContextKt.restoreThreadContext(context$iv, oldValue$iv);
                                }
                                try {
                                    Result.Companion companion6 = Result.INSTANCE;
                                    DispatchedTask<T> dispatchedTask = this;
                                    taskContext.afterTask();
                                    result = Result.m155constructorimpl(Unit.INSTANCE);
                                } catch (Throwable th4) {
                                    th = th4;
                                    Result.Companion companion7 = Result.INSTANCE;
                                    result = Result.m155constructorimpl(ResultKt.createFailure(th));
                                }
                            } catch (Throwable th5) {
                                th = th5;
                            }
                        } catch (Throwable th6) {
                            th = th6;
                            if (undispatchedCoroutine != null) {
                                ThreadContextKt.restoreThreadContext(context$iv, oldValue$iv);
                            } else {
                                ThreadContextKt.restoreThreadContext(context$iv, oldValue$iv);
                            }
                            throw th;
                        }
                    } else {
                        if (job != null) {
                            taskContext = taskContext2;
                            if (exception != null) {
                                Result.Companion companion8 = Result.INSTANCE;
                                continuation.resumeWith(Result.m155constructorimpl(ResultKt.createFailure(exception)));
                            } else {
                                Result.Companion companion9 = Result.INSTANCE;
                                continuation.resumeWith(Result.m155constructorimpl(getSuccessfulResult$kotlinx_coroutines_core(state)));
                            }
                        } else {
                            taskContext = taskContext2;
                            if (exception != null) {
                                Result.Companion companion10 = Result.INSTANCE;
                                continuation.resumeWith(Result.m155constructorimpl(ResultKt.createFailure(exception)));
                            } else {
                                Result.Companion companion11 = Result.INSTANCE;
                                continuation.resumeWith(Result.m155constructorimpl(getSuccessfulResult$kotlinx_coroutines_core(state)));
                            }
                        }
                        Unit unit2 = Unit.INSTANCE;
                        if (undispatchedCoroutine != null) {
                            ThreadContextKt.restoreThreadContext(context$iv, oldValue$iv);
                        } else {
                            ThreadContextKt.restoreThreadContext(context$iv, oldValue$iv);
                        }
                        Result.Companion companion12 = Result.INSTANCE;
                        DispatchedTask<T> dispatchedTask2 = this;
                        taskContext.afterTask();
                        result = Result.m155constructorimpl(Unit.INSTANCE);
                    }
                } catch (Throwable th7) {
                    th = th7;
                }
            } catch (Throwable th8) {
                e = th8;
                fatalException = e;
                Result.Companion companion13 = Result.INSTANCE;
                taskContext2.afterTask();
                result = Result.m155constructorimpl(Unit.INSTANCE);
            }
        } catch (Throwable th9) {
            e = th9;
        }
        handleFatalException$kotlinx_coroutines_core(fatalException, Result.m158exceptionOrNullimpl(result));
    }

    public final void handleFatalException$kotlinx_coroutines_core(Throwable exception, Throwable finallyException) {
        if (exception == null && finallyException == null) {
            return;
        }
        if (exception != null && finallyException != null) {
            kotlin.ExceptionsKt.addSuppressed(exception, finallyException);
        }
        Throwable cause = exception == null ? finallyException : exception;
        Intrinsics.checkNotNull(cause);
        CoroutinesInternalError reason = new CoroutinesInternalError("Fatal exception in coroutines machinery for " + this + ". Please read KDoc to 'handleFatalException' method and report this incident to maintainers", cause);
        CoroutineExceptionHandlerKt.handleCoroutineException(getDelegate$kotlinx_coroutines_core().get$context(), reason);
    }
}
