package androidx.activity;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelLazy;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.viewmodel.CreationExtras;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Lazy;
import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;

/* JADX INFO: compiled from: ActivityViewModelLazy.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000\"\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a4\u0010\u0000\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\n\b\u0000\u0010\u0002\u0018\u0001*\u00020\u0003*\u00020\u00042\u0010\b\n\u0010\u0005\u001a\n\u0012\u0004\u0012\u00020\u0007\u0018\u00010\u0006H\u0087\bø\u0001\u0000\u001aF\u0010\u0000\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\n\b\u0000\u0010\u0002\u0018\u0001*\u00020\u0003*\u00020\u00042\u0010\b\n\u0010\b\u001a\n\u0012\u0004\u0012\u00020\t\u0018\u00010\u00062\u0010\b\n\u0010\u0005\u001a\n\u0012\u0004\u0012\u00020\u0007\u0018\u00010\u0006H\u0087\bø\u0001\u0000\u0082\u0002\u0007\n\u0005\b\u009920\u0001¨\u0006\n"}, d2 = {"viewModels", "Lkotlin/Lazy;", "VM", "Landroidx/lifecycle/ViewModel;", "Landroidx/activity/ComponentActivity;", "factoryProducer", "Lkotlin/Function0;", "Landroidx/lifecycle/ViewModelProvider$Factory;", "extrasProducer", "Landroidx/lifecycle/viewmodel/CreationExtras;", "activity_release"}, k = 2, mv = {2, 0, 0}, xi = 48)
public final class ActivityViewModelLazyKt {
    public static /* synthetic */ Lazy viewModels$default(ComponentActivity $this$viewModels_u24default, Function0 factoryProducer, int i, Object obj) {
        if ((i & 1) != 0) {
            factoryProducer = null;
        }
        Intrinsics.checkNotNullParameter($this$viewModels_u24default, "<this>");
        ActivityViewModelLazyKt$viewModels$factoryPromise$1 factoryPromise = factoryProducer == null ? new ActivityViewModelLazyKt$viewModels$factoryPromise$1($this$viewModels_u24default) : factoryProducer;
        Intrinsics.reifiedOperationMarker(4, "VM");
        return new ViewModelLazy(Reflection.getOrCreateKotlinClass(ViewModel.class), new AnonymousClass1($this$viewModels_u24default), factoryPromise, new AnonymousClass2($this$viewModels_u24default));
    }

    @Deprecated(level = DeprecationLevel.HIDDEN, message = "Superseded by viewModels that takes a CreationExtras")
    public static final /* synthetic */ <VM extends ViewModel> Lazy<VM> viewModels(ComponentActivity $this$viewModels, Function0<? extends ViewModelProvider.Factory> function0) {
        Intrinsics.checkNotNullParameter($this$viewModels, "<this>");
        ActivityViewModelLazyKt$viewModels$factoryPromise$1 activityViewModelLazyKt$viewModels$factoryPromise$1 = function0 == null ? new ActivityViewModelLazyKt$viewModels$factoryPromise$1($this$viewModels) : function0;
        Intrinsics.reifiedOperationMarker(4, "VM");
        return new ViewModelLazy(Reflection.getOrCreateKotlinClass(ViewModel.class), new AnonymousClass1($this$viewModels), activityViewModelLazyKt$viewModels$factoryPromise$1, new AnonymousClass2($this$viewModels));
    }

    /* JADX INFO: renamed from: androidx.activity.ActivityViewModelLazyKt$viewModels$1, reason: invalid class name */
    /* JADX INFO: compiled from: ActivityViewModelLazy.kt */
    @Metadata(k = 3, mv = {2, 0, 0}, xi = 176)
    public static final class AnonymousClass1 implements Function0<ViewModelStore> {
        final /* synthetic */ ComponentActivity $this_viewModels;

        public AnonymousClass1(ComponentActivity componentActivity) {
            this.$this_viewModels = componentActivity;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // kotlin.jvm.functions.Function0
        public final ViewModelStore invoke() {
            return this.$this_viewModels.getViewModelStore();
        }
    }

    /* JADX INFO: renamed from: androidx.activity.ActivityViewModelLazyKt$viewModels$2, reason: invalid class name */
    /* JADX INFO: compiled from: ActivityViewModelLazy.kt */
    @Metadata(k = 3, mv = {2, 0, 0}, xi = 176)
    public static final class AnonymousClass2 implements Function0<CreationExtras> {
        final /* synthetic */ ComponentActivity $this_viewModels;

        public AnonymousClass2(ComponentActivity componentActivity) {
            this.$this_viewModels = componentActivity;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // kotlin.jvm.functions.Function0
        public final CreationExtras invoke() {
            return this.$this_viewModels.getDefaultViewModelCreationExtras();
        }
    }

    public static /* synthetic */ Lazy viewModels$default(ComponentActivity $this$viewModels_u24default, Function0 extrasProducer, Function0 factoryProducer, int i, Object obj) {
        if ((i & 1) != 0) {
            extrasProducer = null;
        }
        if ((i & 2) != 0) {
            factoryProducer = null;
        }
        Intrinsics.checkNotNullParameter($this$viewModels_u24default, "<this>");
        ActivityViewModelLazyKt$viewModels$factoryPromise$2 factoryPromise = factoryProducer == null ? new ActivityViewModelLazyKt$viewModels$factoryPromise$2($this$viewModels_u24default) : factoryProducer;
        Intrinsics.reifiedOperationMarker(4, "VM");
        return new ViewModelLazy(Reflection.getOrCreateKotlinClass(ViewModel.class), new AnonymousClass3($this$viewModels_u24default), factoryPromise, new AnonymousClass4(extrasProducer, $this$viewModels_u24default));
    }

    public static final /* synthetic */ <VM extends ViewModel> Lazy<VM> viewModels(ComponentActivity $this$viewModels, Function0<? extends CreationExtras> function0, Function0<? extends ViewModelProvider.Factory> function1) {
        Intrinsics.checkNotNullParameter($this$viewModels, "<this>");
        ActivityViewModelLazyKt$viewModels$factoryPromise$2 activityViewModelLazyKt$viewModels$factoryPromise$2 = function1 == null ? new ActivityViewModelLazyKt$viewModels$factoryPromise$2($this$viewModels) : function1;
        Intrinsics.reifiedOperationMarker(4, "VM");
        return new ViewModelLazy(Reflection.getOrCreateKotlinClass(ViewModel.class), new AnonymousClass3($this$viewModels), activityViewModelLazyKt$viewModels$factoryPromise$2, new AnonymousClass4(function0, $this$viewModels));
    }

    /* JADX INFO: renamed from: androidx.activity.ActivityViewModelLazyKt$viewModels$3, reason: invalid class name */
    /* JADX INFO: compiled from: ActivityViewModelLazy.kt */
    @Metadata(k = 3, mv = {2, 0, 0}, xi = 176)
    public static final class AnonymousClass3 implements Function0<ViewModelStore> {
        final /* synthetic */ ComponentActivity $this_viewModels;

        public AnonymousClass3(ComponentActivity componentActivity) {
            this.$this_viewModels = componentActivity;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // kotlin.jvm.functions.Function0
        public final ViewModelStore invoke() {
            return this.$this_viewModels.getViewModelStore();
        }
    }

    /* JADX INFO: renamed from: androidx.activity.ActivityViewModelLazyKt$viewModels$4, reason: invalid class name */
    /* JADX INFO: compiled from: ActivityViewModelLazy.kt */
    @Metadata(k = 3, mv = {2, 0, 0}, xi = 176)
    public static final class AnonymousClass4 implements Function0<CreationExtras> {
        final /* synthetic */ Function0<CreationExtras> $extrasProducer;
        final /* synthetic */ ComponentActivity $this_viewModels;

        /* JADX WARN: Multi-variable type inference failed */
        public AnonymousClass4(Function0<? extends CreationExtras> function0, ComponentActivity componentActivity) {
            this.$extrasProducer = function0;
            this.$this_viewModels = componentActivity;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // kotlin.jvm.functions.Function0
        public final CreationExtras invoke() {
            CreationExtras creationExtrasInvoke;
            Function0<CreationExtras> function0 = this.$extrasProducer;
            return (function0 == null || (creationExtrasInvoke = function0.invoke()) == null) ? this.$this_viewModels.getDefaultViewModelCreationExtras() : creationExtrasInvoke;
        }
    }
}
