package androidx.activity;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.channels.ProduceKt;
import kotlinx.coroutines.channels.ProducerScope;

/* JADX INFO: compiled from: PipHintTracker.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\b\u0012\u0004\u0012\u00020\u00030\u0002H\n"}, d2 = {"<anonymous>", "", "Lkotlinx/coroutines/channels/ProducerScope;", "Landroid/graphics/Rect;"}, k = 3, mv = {2, 0, 0}, xi = 48)
@DebugMetadata(c = "androidx.activity.PipHintTrackerKt$trackPipAnimationHintView$flow$1", f = "PipHintTracker.kt", i = {}, l = {86}, m = "invokeSuspend", n = {}, s = {})
final class PipHintTrackerKt$trackPipAnimationHintView$flow$1 extends SuspendLambda implements Function2<ProducerScope<? super Rect>, Continuation<? super Unit>, Object> {
    final /* synthetic */ View $view;
    private /* synthetic */ Object L$0;
    int label;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    PipHintTrackerKt$trackPipAnimationHintView$flow$1(View view, Continuation<? super PipHintTrackerKt$trackPipAnimationHintView$flow$1> continuation) {
        super(2, continuation);
        this.$view = view;
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
        PipHintTrackerKt$trackPipAnimationHintView$flow$1 pipHintTrackerKt$trackPipAnimationHintView$flow$1 = new PipHintTrackerKt$trackPipAnimationHintView$flow$1(this.$view, continuation);
        pipHintTrackerKt$trackPipAnimationHintView$flow$1.L$0 = obj;
        return pipHintTrackerKt$trackPipAnimationHintView$flow$1;
    }

    @Override // kotlin.jvm.functions.Function2
    public final Object invoke(ProducerScope<? super Rect> producerScope, Continuation<? super Unit> continuation) {
        return ((PipHintTrackerKt$trackPipAnimationHintView$flow$1) create(producerScope, continuation)).invokeSuspend(Unit.INSTANCE);
    }

    /* JADX WARN: Type inference failed for: r3v1, types: [androidx.activity.PipHintTrackerKt$trackPipAnimationHintView$flow$1$attachStateChangeListener$1] */
    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Object invokeSuspend(Object $result) throws Throwable {
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        switch (this.label) {
            case 0:
                ResultKt.throwOnFailure($result);
                final ProducerScope $this$callbackFlow = (ProducerScope) this.L$0;
                final View.OnLayoutChangeListener layoutChangeListener = new View.OnLayoutChangeListener() { // from class: androidx.activity.PipHintTrackerKt$trackPipAnimationHintView$flow$1$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnLayoutChangeListener
                    public final void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                        PipHintTrackerKt$trackPipAnimationHintView$flow$1.invokeSuspend$lambda$0($this$callbackFlow, view, i, i2, i3, i4, i5, i6, i7, i8);
                    }
                };
                final View view = this.$view;
                final ViewTreeObserver.OnScrollChangedListener scrollChangeListener = new ViewTreeObserver.OnScrollChangedListener() { // from class: androidx.activity.PipHintTrackerKt$trackPipAnimationHintView$flow$1$$ExternalSyntheticLambda1
                    @Override // android.view.ViewTreeObserver.OnScrollChangedListener
                    public final void onScrollChanged() {
                        PipHintTrackerKt$trackPipAnimationHintView$flow$1.invokeSuspend$lambda$1($this$callbackFlow, view);
                    }
                };
                final View view2 = this.$view;
                final ?? r3 = new View.OnAttachStateChangeListener() { // from class: androidx.activity.PipHintTrackerKt$trackPipAnimationHintView$flow$1$attachStateChangeListener$1
                    @Override // android.view.View.OnAttachStateChangeListener
                    public void onViewAttachedToWindow(View v) {
                        Intrinsics.checkNotNullParameter(v, "v");
                        $this$callbackFlow.mo1658trySendJP2dKIU(PipHintTrackerKt.trackPipAnimationHintView$positionInWindow(view2));
                        view2.getViewTreeObserver().addOnScrollChangedListener(scrollChangeListener);
                        view2.addOnLayoutChangeListener(layoutChangeListener);
                    }

                    @Override // android.view.View.OnAttachStateChangeListener
                    public void onViewDetachedFromWindow(View v) {
                        Intrinsics.checkNotNullParameter(v, "v");
                        v.getViewTreeObserver().removeOnScrollChangedListener(scrollChangeListener);
                        v.removeOnLayoutChangeListener(layoutChangeListener);
                    }
                };
                if (this.$view.isAttachedToWindow()) {
                    $this$callbackFlow.mo1658trySendJP2dKIU(PipHintTrackerKt.trackPipAnimationHintView$positionInWindow(this.$view));
                    this.$view.getViewTreeObserver().addOnScrollChangedListener(scrollChangeListener);
                    this.$view.addOnLayoutChangeListener(layoutChangeListener);
                }
                this.$view.addOnAttachStateChangeListener((View.OnAttachStateChangeListener) r3);
                final View view3 = this.$view;
                this.label = 1;
                if (ProduceKt.awaitClose($this$callbackFlow, new Function0() { // from class: androidx.activity.PipHintTrackerKt$trackPipAnimationHintView$flow$1$$ExternalSyntheticLambda2
                    @Override // kotlin.jvm.functions.Function0
                    public final Object invoke() {
                        return PipHintTrackerKt$trackPipAnimationHintView$flow$1.invokeSuspend$lambda$2(view3, scrollChangeListener, layoutChangeListener, r3);
                    }
                }, this) == coroutine_suspended) {
                    return coroutine_suspended;
                }
                break;
            case 1:
                ResultKt.throwOnFailure($result);
                break;
            default:
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void invokeSuspend$lambda$0(ProducerScope $$this$callbackFlow, View v, int l, int t, int r, int b, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if (l != oldLeft || r != oldRight || t != oldTop || b != oldBottom) {
            Intrinsics.checkNotNull(v);
            $$this$callbackFlow.mo1658trySendJP2dKIU(PipHintTrackerKt.trackPipAnimationHintView$positionInWindow(v));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void invokeSuspend$lambda$1(ProducerScope $$this$callbackFlow, View $view) {
        $$this$callbackFlow.mo1658trySendJP2dKIU(PipHintTrackerKt.trackPipAnimationHintView$positionInWindow($view));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit invokeSuspend$lambda$2(View $view, ViewTreeObserver.OnScrollChangedListener $scrollChangeListener, View.OnLayoutChangeListener $layoutChangeListener, PipHintTrackerKt$trackPipAnimationHintView$flow$1$attachStateChangeListener$1 $attachStateChangeListener) {
        $view.getViewTreeObserver().removeOnScrollChangedListener($scrollChangeListener);
        $view.removeOnLayoutChangeListener($layoutChangeListener);
        $view.removeOnAttachStateChangeListener($attachStateChangeListener);
        return Unit.INSTANCE;
    }
}
