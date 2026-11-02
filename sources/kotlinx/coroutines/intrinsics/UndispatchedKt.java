package kotlinx.coroutines.intrinsics;

import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.BaseContinuationImpl;
import kotlin.coroutines.jvm.internal.CoroutineStackFrame;
import kotlin.coroutines.jvm.internal.DebugProbesKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.TypeIntrinsics;
import kotlinx.coroutines.CompletedExceptionally;
import kotlinx.coroutines.DebugKt;
import kotlinx.coroutines.JobSupportKt;
import kotlinx.coroutines.TimeoutCancellationException;
import kotlinx.coroutines.internal.ScopeCoroutine;
import kotlinx.coroutines.internal.StackTraceRecoveryKt;
import kotlinx.coroutines.internal.ThreadContextKt;

/* JADX INFO: compiled from: Undispatched.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000@\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a9\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u00022\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u0002H\u00020\u00042\u001a\u0010\u0005\u001a\u0016\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00020\u0004\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u0006H\u0082\b\u001aO\u0010\b\u001a\u00020\u0001\"\u0004\b\u0000\u0010\t\"\u0004\b\u0001\u0010\u0002*\u001e\b\u0001\u0012\u0004\u0012\u0002H\t\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00020\u0004\u0012\u0006\u0012\u0004\u0018\u00010\u00070\n2\u0006\u0010\u000b\u001a\u0002H\t2\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0004H\u0000¢\u0006\u0002\u0010\f\u001a;\u0010\r\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\u0018\b\u0001\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00020\u0004\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u00062\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0004H\u0000¢\u0006\u0002\u0010\u000e\u001aV\u0010\u000f\u001a\u0004\u0018\u00010\u0007\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\t*\b\u0012\u0004\u0012\u0002H\u00020\u00102\u0006\u0010\u000b\u001a\u0002H\t2'\u0010\u0005\u001a#\b\u0001\u0012\u0004\u0012\u0002H\t\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00020\u0004\u0012\u0006\u0012\u0004\u0018\u00010\u00070\n¢\u0006\u0002\b\u0011H\u0000¢\u0006\u0002\u0010\u0012\u001aV\u0010\u0013\u001a\u0004\u0018\u00010\u0007\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\t*\b\u0012\u0004\u0012\u0002H\u00020\u00102\u0006\u0010\u000b\u001a\u0002H\t2'\u0010\u0005\u001a#\b\u0001\u0012\u0004\u0012\u0002H\t\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00020\u0004\u0012\u0006\u0012\u0004\u0018\u00010\u00070\n¢\u0006\u0002\b\u0011H\u0000¢\u0006\u0002\u0010\u0012\u001a?\u0010\u0014\u001a\u0004\u0018\u00010\u0007\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u00102\u0012\u0010\u0015\u001a\u000e\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\u00170\u00062\u000e\u0010\u0018\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u0019H\u0082\b¨\u0006\u001a"}, d2 = {"startDirect", "", "T", "completion", "Lkotlin/coroutines/Continuation;", "block", "Lkotlin/Function1;", "", "startCoroutineUndispatched", "R", "Lkotlin/Function2;", "receiver", "(Lkotlin/jvm/functions/Function2;Ljava/lang/Object;Lkotlin/coroutines/Continuation;)V", "startCoroutineUnintercepted", "(Lkotlin/jvm/functions/Function1;Lkotlin/coroutines/Continuation;)V", "startUndispatchedOrReturn", "Lkotlinx/coroutines/internal/ScopeCoroutine;", "Lkotlin/ExtensionFunctionType;", "(Lkotlinx/coroutines/internal/ScopeCoroutine;Ljava/lang/Object;Lkotlin/jvm/functions/Function2;)Ljava/lang/Object;", "startUndispatchedOrReturnIgnoreTimeout", "undispatchedResult", "shouldThrow", "", "", "startBlock", "Lkotlin/Function0;", "kotlinx-coroutines-core"}, k = 2, mv = {1, 9, 0}, xi = 48)
public final class UndispatchedKt {
    public static final <T> void startCoroutineUnintercepted(Function1<? super Continuation<? super T>, ? extends Object> function1, Continuation<? super T> continuation) {
        Continuation actualCompletion$iv = DebugProbesKt.probeCoroutineCreated(continuation);
        try {
            Object value$iv = !(function1 instanceof BaseContinuationImpl) ? IntrinsicsKt.wrapWithContinuationImpl(function1, actualCompletion$iv) : ((Function1) TypeIntrinsics.beforeCheckcastToFunctionOfArity(function1, 1)).invoke(actualCompletion$iv);
            if (value$iv == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
                return;
            }
            Result.Companion companion = Result.INSTANCE;
            actualCompletion$iv.resumeWith(Result.m155constructorimpl(value$iv));
        } catch (Throwable e$iv) {
            Result.Companion companion2 = Result.INSTANCE;
            actualCompletion$iv.resumeWith(Result.m155constructorimpl(ResultKt.createFailure(e$iv)));
        }
    }

    public static final <R, T> void startCoroutineUndispatched(Function2<? super R, ? super Continuation<? super T>, ? extends Object> function2, R r, Continuation<? super T> continuation) {
        Continuation actualCompletion$iv = DebugProbesKt.probeCoroutineCreated(continuation);
        try {
            CoroutineContext context$iv = continuation.getContext();
            Object oldValue$iv = ThreadContextKt.updateThreadContext(context$iv, null);
            try {
                Object value$iv = !(function2 instanceof BaseContinuationImpl) ? IntrinsicsKt.wrapWithContinuationImpl(function2, r, actualCompletion$iv) : ((Function2) TypeIntrinsics.beforeCheckcastToFunctionOfArity(function2, 2)).invoke(r, actualCompletion$iv);
                ThreadContextKt.restoreThreadContext(context$iv, oldValue$iv);
                if (value$iv == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
                    return;
                }
                Result.Companion companion = Result.INSTANCE;
                actualCompletion$iv.resumeWith(Result.m155constructorimpl(value$iv));
            } catch (Throwable th) {
                ThreadContextKt.restoreThreadContext(context$iv, oldValue$iv);
                throw th;
            }
        } catch (Throwable e$iv) {
            Result.Companion companion2 = Result.INSTANCE;
            actualCompletion$iv.resumeWith(Result.m155constructorimpl(ResultKt.createFailure(e$iv)));
        }
    }

    private static final <T> void startDirect(Continuation<? super T> continuation, Function1<? super Continuation<? super T>, ? extends Object> function1) {
        Continuation actualCompletion = DebugProbesKt.probeCoroutineCreated(continuation);
        try {
            Object value = function1.invoke(actualCompletion);
            if (value != IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
                Result.Companion companion = Result.INSTANCE;
                actualCompletion.resumeWith(Result.m155constructorimpl(value));
            }
        } catch (Throwable e) {
            Result.Companion companion2 = Result.INSTANCE;
            actualCompletion.resumeWith(Result.m155constructorimpl(ResultKt.createFailure(e)));
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r3v0 */
    /* JADX WARN: Type inference failed for: r3v1, types: [int] */
    /* JADX WARN: Type inference failed for: r3v2 */
    /* JADX WARN: Type inference failed for: r3v3, types: [java.lang.Object] */
    /* JADX WARN: Type inference failed for: r3v5 */
    /* JADX WARN: Type inference failed for: r3v7 */
    /* JADX WARN: Type inference failed for: r3v8 */
    public static final <T, R> Object startUndispatchedOrReturn(ScopeCoroutine<? super T> scopeCoroutine, R r, Function2<? super R, ? super Continuation<? super T>, ? extends Object> function2) {
        Object objMakeCompletingOnce$kotlinx_coroutines_core;
        ?? completedExceptionally = 2;
        try {
            completedExceptionally = !(function2 instanceof BaseContinuationImpl) ? IntrinsicsKt.wrapWithContinuationImpl(function2, r, scopeCoroutine) : ((Function2) TypeIntrinsics.beforeCheckcastToFunctionOfArity(function2, 2)).invoke(r, scopeCoroutine);
        } catch (Throwable th) {
            completedExceptionally = new CompletedExceptionally(th, false, completedExceptionally, null);
        }
        if (completedExceptionally != IntrinsicsKt.getCOROUTINE_SUSPENDED() && (objMakeCompletingOnce$kotlinx_coroutines_core = scopeCoroutine.makeCompletingOnce$kotlinx_coroutines_core(completedExceptionally)) != JobSupportKt.COMPLETING_WAITING_CHILDREN) {
            if (objMakeCompletingOnce$kotlinx_coroutines_core instanceof CompletedExceptionally) {
                Throwable th2 = ((CompletedExceptionally) objMakeCompletingOnce$kotlinx_coroutines_core).cause;
                Throwable th3 = ((CompletedExceptionally) objMakeCompletingOnce$kotlinx_coroutines_core).cause;
                Continuation<? super T> continuation = scopeCoroutine.uCont;
                if (DebugKt.getRECOVER_STACK_TRACES() && (continuation instanceof CoroutineStackFrame)) {
                    throw StackTraceRecoveryKt.recoverFromStackFrame(th3, (CoroutineStackFrame) continuation);
                }
                throw th3;
            }
            return JobSupportKt.unboxState(objMakeCompletingOnce$kotlinx_coroutines_core);
        }
        return IntrinsicsKt.getCOROUTINE_SUSPENDED();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v4, types: [java.lang.Object] */
    /* JADX WARN: Type inference failed for: r3v0 */
    /* JADX WARN: Type inference failed for: r3v1, types: [int] */
    /* JADX WARN: Type inference failed for: r3v2 */
    /* JADX WARN: Type inference failed for: r3v3, types: [java.lang.Object] */
    /* JADX WARN: Type inference failed for: r3v5 */
    /* JADX WARN: Type inference failed for: r3v7 */
    /* JADX WARN: Type inference failed for: r3v8 */
    /* JADX WARN: Type inference failed for: r4v16 */
    /* JADX WARN: Type inference failed for: r4v2 */
    /* JADX WARN: Type inference failed for: r4v6 */
    /* JADX WARN: Type inference failed for: r4v7 */
    public static final <T, R> Object startUndispatchedOrReturnIgnoreTimeout(ScopeCoroutine<? super T> scopeCoroutine, R r, Function2<? super R, ? super Continuation<? super T>, ? extends Object> function2) throws Throwable {
        Object objMakeCompletingOnce$kotlinx_coroutines_core;
        ?? UnboxState;
        ?? completedExceptionally = 2;
        try {
            completedExceptionally = !(function2 instanceof BaseContinuationImpl) ? IntrinsicsKt.wrapWithContinuationImpl(function2, r, scopeCoroutine) : ((Function2) TypeIntrinsics.beforeCheckcastToFunctionOfArity(function2, 2)).invoke(r, scopeCoroutine);
        } catch (Throwable th) {
            completedExceptionally = new CompletedExceptionally(th, false, completedExceptionally, null);
        }
        if (completedExceptionally != IntrinsicsKt.getCOROUTINE_SUSPENDED() && (objMakeCompletingOnce$kotlinx_coroutines_core = scopeCoroutine.makeCompletingOnce$kotlinx_coroutines_core(completedExceptionally)) != JobSupportKt.COMPLETING_WAITING_CHILDREN) {
            if (objMakeCompletingOnce$kotlinx_coroutines_core instanceof CompletedExceptionally) {
                Throwable th2 = ((CompletedExceptionally) objMakeCompletingOnce$kotlinx_coroutines_core).cause;
                if (((th2 instanceof TimeoutCancellationException) && ((TimeoutCancellationException) th2).coroutine == scopeCoroutine) ? false : true) {
                    Throwable th3 = ((CompletedExceptionally) objMakeCompletingOnce$kotlinx_coroutines_core).cause;
                    Continuation<? super T> continuation = scopeCoroutine.uCont;
                    if (DebugKt.getRECOVER_STACK_TRACES() && (continuation instanceof CoroutineStackFrame)) {
                        throw StackTraceRecoveryKt.recoverFromStackFrame(th3, (CoroutineStackFrame) continuation);
                    }
                    throw th3;
                }
                if (completedExceptionally instanceof CompletedExceptionally) {
                    Throwable th4 = ((CompletedExceptionally) completedExceptionally).cause;
                    Continuation<? super T> continuation2 = scopeCoroutine.uCont;
                    if (DebugKt.getRECOVER_STACK_TRACES() && (continuation2 instanceof CoroutineStackFrame)) {
                        throw StackTraceRecoveryKt.recoverFromStackFrame(th4, (CoroutineStackFrame) continuation2);
                    }
                    throw th4;
                }
                UnboxState = completedExceptionally;
            } else {
                UnboxState = JobSupportKt.unboxState(objMakeCompletingOnce$kotlinx_coroutines_core);
            }
            return UnboxState;
        }
        return IntrinsicsKt.getCOROUTINE_SUSPENDED();
    }

    private static final <T> Object undispatchedResult(ScopeCoroutine<? super T> scopeCoroutine, Function1<? super Throwable, Boolean> function1, Function0<? extends Object> function0) throws Throwable {
        Object result;
        Object state;
        try {
            result = function0.invoke();
        } catch (Throwable e) {
            result = new CompletedExceptionally(e, false, 2, null);
        }
        if (result != IntrinsicsKt.getCOROUTINE_SUSPENDED() && (state = scopeCoroutine.makeCompletingOnce$kotlinx_coroutines_core(result)) != JobSupportKt.COMPLETING_WAITING_CHILDREN) {
            if (state instanceof CompletedExceptionally) {
                if (!function1.invoke(((CompletedExceptionally) state).cause).booleanValue()) {
                    if (!(result instanceof CompletedExceptionally)) {
                        return result;
                    }
                    Throwable exception$iv = ((CompletedExceptionally) result).cause;
                    Continuation<? super T> continuation = scopeCoroutine.uCont;
                    if (DebugKt.getRECOVER_STACK_TRACES() && (continuation instanceof CoroutineStackFrame)) {
                        throw StackTraceRecoveryKt.recoverFromStackFrame(exception$iv, (CoroutineStackFrame) continuation);
                    }
                    throw exception$iv;
                }
                Throwable exception$iv2 = ((CompletedExceptionally) state).cause;
                Continuation<? super T> continuation2 = scopeCoroutine.uCont;
                if (DebugKt.getRECOVER_STACK_TRACES() && (continuation2 instanceof CoroutineStackFrame)) {
                    throw StackTraceRecoveryKt.recoverFromStackFrame(exception$iv2, (CoroutineStackFrame) continuation2);
                }
                throw exception$iv2;
            }
            return JobSupportKt.unboxState(state);
        }
        return IntrinsicsKt.getCOROUTINE_SUSPENDED();
    }
}
