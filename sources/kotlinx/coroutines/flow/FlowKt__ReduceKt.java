package kotlinx.coroutines.flow;

import androidx.core.view.MotionEventCompat;
import java.util.NoSuchElementException;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Ref;
import kotlinx.coroutines.flow.internal.AbortFlowException;
import kotlinx.coroutines.flow.internal.FlowExceptions_commonKt;
import kotlinx.coroutines.flow.internal.NullSurrogateKt;

/* JADX INFO: compiled from: Reduce.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000,\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\u0010\u0000\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\r\u001a\u001e\u0010\u0000\u001a\u0002H\u0001\"\u0004\b\u0000\u0010\u0001*\b\u0012\u0004\u0012\u0002H\u00010\u0002H\u0086@¢\u0006\u0002\u0010\u0003\u001aB\u0010\u0000\u001a\u0002H\u0001\"\u0004\b\u0000\u0010\u0001*\b\u0012\u0004\u0012\u0002H\u00010\u00022\"\u0010\u0004\u001a\u001e\b\u0001\u0012\u0004\u0012\u0002H\u0001\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00070\u0006\u0012\u0006\u0012\u0004\u0018\u00010\b0\u0005H\u0086@¢\u0006\u0002\u0010\t\u001a \u0010\n\u001a\u0004\u0018\u0001H\u0001\"\u0004\b\u0000\u0010\u0001*\b\u0012\u0004\u0012\u0002H\u00010\u0002H\u0086@¢\u0006\u0002\u0010\u0003\u001aD\u0010\n\u001a\u0004\u0018\u0001H\u0001\"\u0004\b\u0000\u0010\u0001*\b\u0012\u0004\u0012\u0002H\u00010\u00022\"\u0010\u0004\u001a\u001e\b\u0001\u0012\u0004\u0012\u0002H\u0001\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00070\u0006\u0012\u0006\u0012\u0004\u0018\u00010\b0\u0005H\u0086@¢\u0006\u0002\u0010\t\u001av\u0010\u000b\u001a\u0002H\f\"\u0004\b\u0000\u0010\u0001\"\u0004\b\u0001\u0010\f*\b\u0012\u0004\u0012\u0002H\u00010\u00022\u0006\u0010\r\u001a\u0002H\f2H\b\u0004\u0010\u000e\u001aB\b\u0001\u0012\u0013\u0012\u0011H\f¢\u0006\f\b\u0010\u0012\b\b\u0011\u0012\u0004\b\b(\u0012\u0012\u0013\u0012\u0011H\u0001¢\u0006\f\b\u0010\u0012\b\b\u0011\u0012\u0004\b\b(\u0013\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\f0\u0006\u0012\u0006\u0012\u0004\u0018\u00010\b0\u000fH\u0086H¢\u0006\u0002\u0010\u0014\u001a\u001e\u0010\u0015\u001a\u0002H\u0001\"\u0004\b\u0000\u0010\u0001*\b\u0012\u0004\u0012\u0002H\u00010\u0002H\u0086@¢\u0006\u0002\u0010\u0003\u001a \u0010\u0016\u001a\u0004\u0018\u0001H\u0001\"\u0004\b\u0000\u0010\u0001*\b\u0012\u0004\u0012\u0002H\u00010\u0002H\u0086@¢\u0006\u0002\u0010\u0003\u001ap\u0010\u0017\u001a\u0002H\u0018\"\u0004\b\u0000\u0010\u0018\"\b\b\u0001\u0010\u0001*\u0002H\u0018*\b\u0012\u0004\u0012\u0002H\u00010\u00022F\u0010\u000e\u001aB\b\u0001\u0012\u0013\u0012\u0011H\u0018¢\u0006\f\b\u0010\u0012\b\b\u0011\u0012\u0004\b\b(\u0019\u0012\u0013\u0012\u0011H\u0001¢\u0006\f\b\u0010\u0012\b\b\u0011\u0012\u0004\b\b(\u0013\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00180\u0006\u0012\u0006\u0012\u0004\u0018\u00010\b0\u000fH\u0086@¢\u0006\u0002\u0010\u001a\u001a\u001e\u0010\u001b\u001a\u0002H\u0001\"\u0004\b\u0000\u0010\u0001*\b\u0012\u0004\u0012\u0002H\u00010\u0002H\u0086@¢\u0006\u0002\u0010\u0003\u001a \u0010\u001c\u001a\u0004\u0018\u0001H\u0001\"\u0004\b\u0000\u0010\u0001*\b\u0012\u0004\u0012\u0002H\u00010\u0002H\u0086@¢\u0006\u0002\u0010\u0003¨\u0006\u001d"}, d2 = {"first", "T", "Lkotlinx/coroutines/flow/Flow;", "(Lkotlinx/coroutines/flow/Flow;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "predicate", "Lkotlin/Function2;", "Lkotlin/coroutines/Continuation;", "", "", "(Lkotlinx/coroutines/flow/Flow;Lkotlin/jvm/functions/Function2;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "firstOrNull", "fold", "R", "initial", "operation", "Lkotlin/Function3;", "Lkotlin/ParameterName;", "name", "acc", "value", "(Lkotlinx/coroutines/flow/Flow;Ljava/lang/Object;Lkotlin/jvm/functions/Function3;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "last", "lastOrNull", "reduce", "S", "accumulator", "(Lkotlinx/coroutines/flow/Flow;Lkotlin/jvm/functions/Function3;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "single", "singleOrNull", "kotlinx-coroutines-core"}, k = 5, mv = {1, 9, 0}, xi = 48, xs = "kotlinx/coroutines/flow/FlowKt")
final /* synthetic */ class FlowKt__ReduceKt {

    /* JADX INFO: renamed from: kotlinx.coroutines.flow.FlowKt__ReduceKt$first$1, reason: invalid class name */
    /* JADX INFO: compiled from: Reduce.kt */
    @Metadata(k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.flow.FlowKt__ReduceKt", f = "Reduce.kt", i = {0, 0}, l = {179}, m = "first", n = {"result", "collector$iv"}, s = {"L$0", "L$1"})
    static final class AnonymousClass1<T> extends ContinuationImpl {
        Object L$0;
        Object L$1;
        int label;
        /* synthetic */ Object result;

        AnonymousClass1(Continuation<? super AnonymousClass1> continuation) {
            super(continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return FlowKt.first(null, this);
        }
    }

    /* JADX INFO: renamed from: kotlinx.coroutines.flow.FlowKt__ReduceKt$first$3, reason: invalid class name */
    /* JADX INFO: compiled from: Reduce.kt */
    @Metadata(k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.flow.FlowKt__ReduceKt", f = "Reduce.kt", i = {0, 0, 0}, l = {179}, m = "first", n = {"predicate", "result", "collector$iv"}, s = {"L$0", "L$1", "L$2"})
    static final class AnonymousClass3<T> extends ContinuationImpl {
        Object L$0;
        Object L$1;
        Object L$2;
        int label;
        /* synthetic */ Object result;

        AnonymousClass3(Continuation<? super AnonymousClass3> continuation) {
            super(continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return FlowKt.first(null, null, this);
        }
    }

    /* JADX INFO: renamed from: kotlinx.coroutines.flow.FlowKt__ReduceKt$firstOrNull$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: Reduce.kt */
    @Metadata(k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.flow.FlowKt__ReduceKt", f = "Reduce.kt", i = {0, 0}, l = {179}, m = "firstOrNull", n = {"result", "collector$iv"}, s = {"L$0", "L$1"})
    static final class C01161<T> extends ContinuationImpl {
        Object L$0;
        Object L$1;
        int label;
        /* synthetic */ Object result;

        C01161(Continuation<? super C01161> continuation) {
            super(continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return FlowKt.firstOrNull(null, this);
        }
    }

    /* JADX INFO: renamed from: kotlinx.coroutines.flow.FlowKt__ReduceKt$firstOrNull$3, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: Reduce.kt */
    @Metadata(k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.flow.FlowKt__ReduceKt", f = "Reduce.kt", i = {0, 0}, l = {179}, m = "firstOrNull", n = {"result", "collector$iv"}, s = {"L$0", "L$1"})
    static final class C01173<T> extends ContinuationImpl {
        Object L$0;
        Object L$1;
        int label;
        /* synthetic */ Object result;

        C01173(Continuation<? super C01173> continuation) {
            super(continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return FlowKt.firstOrNull(null, null, this);
        }
    }

    /* JADX INFO: renamed from: kotlinx.coroutines.flow.FlowKt__ReduceKt$fold$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: Reduce.kt */
    @Metadata(k = 3, mv = {1, 9, 0}, xi = 176)
    @DebugMetadata(c = "kotlinx.coroutines.flow.FlowKt__ReduceKt", f = "Reduce.kt", i = {0}, l = {MotionEventCompat.AXIS_GENERIC_9}, m = "fold", n = {"accumulator"}, s = {"L$0"})
    static final class C01181<T, R> extends ContinuationImpl {
        Object L$0;
        int label;
        /* synthetic */ Object result;

        C01181(Continuation<? super C01181> continuation) {
            super(continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return FlowKt__ReduceKt.fold(null, null, null, this);
        }
    }

    /* JADX INFO: renamed from: kotlinx.coroutines.flow.FlowKt__ReduceKt$last$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: Reduce.kt */
    @Metadata(k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.flow.FlowKt__ReduceKt", f = "Reduce.kt", i = {0}, l = {151}, m = "last", n = {"result"}, s = {"L$0"})
    static final class C01191<T> extends ContinuationImpl {
        Object L$0;
        int label;
        /* synthetic */ Object result;

        C01191(Continuation<? super C01191> continuation) {
            super(continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return FlowKt.last(null, this);
        }
    }

    /* JADX INFO: renamed from: kotlinx.coroutines.flow.FlowKt__ReduceKt$lastOrNull$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: Reduce.kt */
    @Metadata(k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.flow.FlowKt__ReduceKt", f = "Reduce.kt", i = {0}, l = {163}, m = "lastOrNull", n = {"result"}, s = {"L$0"})
    static final class C01211<T> extends ContinuationImpl {
        Object L$0;
        int label;
        /* synthetic */ Object result;

        C01211(Continuation<? super C01211> continuation) {
            super(continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return FlowKt.lastOrNull(null, this);
        }
    }

    /* JADX INFO: renamed from: kotlinx.coroutines.flow.FlowKt__ReduceKt$reduce$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: Reduce.kt */
    @Metadata(k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.flow.FlowKt__ReduceKt", f = "Reduce.kt", i = {0}, l = {18}, m = "reduce", n = {"accumulator"}, s = {"L$0"})
    static final class C01231<S, T extends S> extends ContinuationImpl {
        Object L$0;
        int label;
        /* synthetic */ Object result;

        C01231(Continuation<? super C01231> continuation) {
            super(continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return FlowKt.reduce(null, null, this);
        }
    }

    /* JADX INFO: renamed from: kotlinx.coroutines.flow.FlowKt__ReduceKt$single$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: Reduce.kt */
    @Metadata(k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.flow.FlowKt__ReduceKt", f = "Reduce.kt", i = {0}, l = {53}, m = "single", n = {"result"}, s = {"L$0"})
    static final class C01251<T> extends ContinuationImpl {
        Object L$0;
        int label;
        /* synthetic */ Object result;

        C01251(Continuation<? super C01251> continuation) {
            super(continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return FlowKt.single(null, this);
        }
    }

    /* JADX INFO: renamed from: kotlinx.coroutines.flow.FlowKt__ReduceKt$singleOrNull$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: Reduce.kt */
    @Metadata(k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.flow.FlowKt__ReduceKt", f = "Reduce.kt", i = {0, 0}, l = {179}, m = "singleOrNull", n = {"result", "collector$iv"}, s = {"L$0", "L$1"})
    static final class C01271<T> extends ContinuationImpl {
        Object L$0;
        Object L$1;
        int label;
        /* synthetic */ Object result;

        C01271(Continuation<? super C01271> continuation) {
            super(continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return FlowKt.singleOrNull(null, this);
        }
    }

    /* JADX WARN: Code duplicated, block: B:7:0x0014  */
    /* JADX WARN: Type inference failed for: r3v0, types: [T, kotlinx.coroutines.internal.Symbol] */
    public static final <S, T extends S> Object reduce(Flow<? extends T> flow, Function3<? super S, ? super T, ? super Continuation<? super S>, ? extends Object> function3, Continuation<? super S> continuation) throws Throwable {
        C01231 c01231;
        Ref.ObjectRef accumulator;
        if (continuation instanceof C01231) {
            c01231 = (C01231) continuation;
            if ((c01231.label & Integer.MIN_VALUE) != 0) {
                c01231.label -= Integer.MIN_VALUE;
            } else {
                c01231 = new C01231(continuation);
            }
        } else {
            c01231 = new C01231(continuation);
        }
        Object $result = c01231.result;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        switch (c01231.label) {
            case 0:
                ResultKt.throwOnFailure($result);
                Ref.ObjectRef accumulator2 = new Ref.ObjectRef();
                accumulator2.element = NullSurrogateKt.NULL;
                FlowCollector<? super Object> c01242 = new C01242<>(accumulator2, function3);
                c01231.L$0 = accumulator2;
                c01231.label = 1;
                if (flow.collect(c01242, c01231) == coroutine_suspended) {
                    return coroutine_suspended;
                }
                accumulator = accumulator2;
                break;
                break;
            case 1:
                accumulator = (Ref.ObjectRef) c01231.L$0;
                ResultKt.throwOnFailure($result);
                break;
            default:
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
        if (accumulator.element == NullSurrogateKt.NULL) {
            throw new NoSuchElementException("Empty flow can't be reduced");
        }
        return accumulator.element;
    }

    /* JADX INFO: renamed from: kotlinx.coroutines.flow.FlowKt__ReduceKt$reduce$2, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: Reduce.kt */
    @Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002\"\b\b\u0001\u0010\u0003*\u0002H\u00022\u0006\u0010\u0004\u001a\u0002H\u0003H\u008a@¢\u0006\u0004\b\u0005\u0010\u0006"}, d2 = {"<anonymous>", "", "S", "T", "value", "emit", "(Ljava/lang/Object;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;"}, k = 3, mv = {1, 9, 0}, xi = 48)
    static final class C01242<T> implements FlowCollector {
        final /* synthetic */ Ref.ObjectRef<Object> $accumulator;
        final /* synthetic */ Function3<S, T, Continuation<? super S>, Object> $operation;

        /* JADX WARN: Multi-variable type inference failed */
        C01242(Ref.ObjectRef<Object> objectRef, Function3<? super S, ? super T, ? super Continuation<? super S>, ? extends Object> function3) {
            this.$accumulator = objectRef;
            this.$operation = function3;
        }

        /* JADX WARN: Code duplicated, block: B:7:0x0014  */
        /* JADX WARN: Type inference fix 'apply assigned field type' failed
        java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
        	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
        	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
        	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
         */
        @Override // kotlinx.coroutines.flow.FlowCollector
        public final Object emit(T t, Continuation<? super Unit> continuation) throws Throwable {
            FlowKt__ReduceKt$reduce$2$emit$1 flowKt__ReduceKt$reduce$2$emit$1;
            Ref.ObjectRef<Object> objectRef;
            Object obj;
            Ref.ObjectRef<Object> objectRef2;
            if (continuation instanceof FlowKt__ReduceKt$reduce$2$emit$1) {
                flowKt__ReduceKt$reduce$2$emit$1 = (FlowKt__ReduceKt$reduce$2$emit$1) continuation;
                if ((flowKt__ReduceKt$reduce$2$emit$1.label & Integer.MIN_VALUE) != 0) {
                    flowKt__ReduceKt$reduce$2$emit$1.label -= Integer.MIN_VALUE;
                } else {
                    flowKt__ReduceKt$reduce$2$emit$1 = new FlowKt__ReduceKt$reduce$2$emit$1(this, continuation);
                }
            } else {
                flowKt__ReduceKt$reduce$2$emit$1 = new FlowKt__ReduceKt$reduce$2$emit$1(this, continuation);
            }
            Object obj2 = flowKt__ReduceKt$reduce$2$emit$1.result;
            Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            switch (flowKt__ReduceKt$reduce$2$emit$1.label) {
                case 0:
                    ResultKt.throwOnFailure(obj2);
                    objectRef = this.$accumulator;
                    if (this.$accumulator.element != NullSurrogateKt.NULL) {
                        Function3<S, T, Continuation<? super S>, Object> function3 = this.$operation;
                        Object obj3 = this.$accumulator.element;
                        flowKt__ReduceKt$reduce$2$emit$1.L$0 = objectRef;
                        flowKt__ReduceKt$reduce$2$emit$1.label = 1;
                        Object objInvoke = function3.invoke((S) obj3, t, flowKt__ReduceKt$reduce$2$emit$1);
                        if (objInvoke == coroutine_suspended) {
                            return coroutine_suspended;
                        }
                        obj = objInvoke;
                        objectRef2 = objectRef;
                        objectRef = objectRef2;
                        t = (T) obj;
                    }
                    objectRef.element = t;
                    return Unit.INSTANCE;
                case 1:
                    objectRef2 = (Ref.ObjectRef) flowKt__ReduceKt$reduce$2$emit$1.L$0;
                    ResultKt.throwOnFailure(obj2);
                    obj = obj2;
                    objectRef = objectRef2;
                    t = (T) obj;
                    objectRef.element = t;
                    return Unit.INSTANCE;
                default:
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:7:0x0014  */
    /* JADX WARN: Multi-variable type inference failed */
    public static final <T, R> Object fold(Flow<? extends T> flow, R r, Function3<? super R, ? super T, ? super Continuation<? super R>, ? extends Object> function3, Continuation<? super R> continuation) throws Throwable {
        C01181 c01181;
        Ref.ObjectRef accumulator;
        if (continuation instanceof C01181) {
            c01181 = (C01181) continuation;
            if ((c01181.label & Integer.MIN_VALUE) != 0) {
                c01181.label -= Integer.MIN_VALUE;
            } else {
                c01181 = new C01181(continuation);
            }
        } else {
            c01181 = new C01181(continuation);
        }
        Object $result = c01181.result;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        switch (c01181.label) {
            case 0:
                ResultKt.throwOnFailure($result);
                Ref.ObjectRef accumulator2 = new Ref.ObjectRef();
                accumulator2.element = r;
                FlowCollector<? super Object> anonymousClass2 = new AnonymousClass2<>(accumulator2, function3);
                c01181.L$0 = accumulator2;
                c01181.label = 1;
                if (flow.collect(anonymousClass2, c01181) == coroutine_suspended) {
                    return coroutine_suspended;
                }
                accumulator = accumulator2;
                break;
            case 1:
                accumulator = (Ref.ObjectRef) c01181.L$0;
                ResultKt.throwOnFailure($result);
                break;
            default:
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
        return accumulator.element;
    }

    /* JADX INFO: renamed from: kotlinx.coroutines.flow.FlowKt__ReduceKt$fold$2, reason: invalid class name */
    /* JADX INFO: compiled from: Reduce.kt */
    @Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\u00032\u0006\u0010\u0004\u001a\u0002H\u0002H\u008a@¢\u0006\u0004\b\u0005\u0010\u0006"}, d2 = {"<anonymous>", "", "T", "R", "value", "emit", "(Ljava/lang/Object;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;"}, k = 3, mv = {1, 9, 0}, xi = 176)
    public static final class AnonymousClass2<T> implements FlowCollector {
        final /* synthetic */ Ref.ObjectRef<R> $accumulator;
        final /* synthetic */ Function3<R, T, Continuation<? super R>, Object> $operation;

        /* JADX WARN: Multi-variable type inference failed */
        public AnonymousClass2(Ref.ObjectRef<R> objectRef, Function3<? super R, ? super T, ? super Continuation<? super R>, ? extends Object> function3) {
            this.$accumulator = objectRef;
            this.$operation = function3;
        }

        /* JADX WARN: Code duplicated, block: B:7:0x0014  */
        /* JADX WARN: Type inference incomplete: some casts might be missing */
        @Override // kotlinx.coroutines.flow.FlowCollector
        public final Object emit(T t, Continuation<? super Unit> continuation) throws Throwable {
            FlowKt__ReduceKt$fold$2$emit$1 flowKt__ReduceKt$fold$2$emit$1;
            T t2;
            Ref.ObjectRef objectRef;
            if (continuation instanceof FlowKt__ReduceKt$fold$2$emit$1) {
                flowKt__ReduceKt$fold$2$emit$1 = (FlowKt__ReduceKt$fold$2$emit$1) continuation;
                if ((flowKt__ReduceKt$fold$2$emit$1.label & Integer.MIN_VALUE) != 0) {
                    flowKt__ReduceKt$fold$2$emit$1.label -= Integer.MIN_VALUE;
                } else {
                    flowKt__ReduceKt$fold$2$emit$1 = new FlowKt__ReduceKt$fold$2$emit$1(this, continuation);
                }
            } else {
                flowKt__ReduceKt$fold$2$emit$1 = new FlowKt__ReduceKt$fold$2$emit$1(this, continuation);
            }
            Object obj = flowKt__ReduceKt$fold$2$emit$1.result;
            Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            switch (flowKt__ReduceKt$fold$2$emit$1.label) {
                case 0:
                    ResultKt.throwOnFailure(obj);
                    Ref.ObjectRef objectRef2 = this.$accumulator;
                    Function3<R, T, Continuation<? super R>, Object> function3 = this.$operation;
                    T t3 = this.$accumulator.element;
                    flowKt__ReduceKt$fold$2$emit$1.L$0 = objectRef2;
                    flowKt__ReduceKt$fold$2$emit$1.label = 1;
                    Object objInvoke = function3.invoke((R) t3, t, flowKt__ReduceKt$fold$2$emit$1);
                    if (objInvoke == coroutine_suspended) {
                        return coroutine_suspended;
                    }
                    t2 = (T) objInvoke;
                    objectRef = objectRef2;
                    break;
                    break;
                case 1:
                    objectRef = (Ref.ObjectRef) flowKt__ReduceKt$fold$2$emit$1.L$0;
                    ResultKt.throwOnFailure(obj);
                    t2 = (T) obj;
                    break;
                default:
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }
            objectRef.element = t2;
            return Unit.INSTANCE;
        }

        /* JADX WARN: Type inference incomplete: some casts might be missing */
        public final Object emit$$forInline(T t, Continuation<? super Unit> continuation) {
            new FlowKt__ReduceKt$fold$2$emit$1(this, continuation);
            this.$accumulator.element = (T) this.$operation.invoke((R) this.$accumulator.element, t, (Continuation<? super R>) continuation);
            return Unit.INSTANCE;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static final <T, R> Object fold$$forInline(Flow<? extends T> flow, R r, Function3<? super R, ? super T, ? super Continuation<? super R>, ? extends Object> function3, Continuation<? super R> continuation) {
        Ref.ObjectRef accumulator = new Ref.ObjectRef();
        accumulator.element = r;
        flow.collect(new AnonymousClass2(accumulator, function3), continuation);
        return accumulator.element;
    }

    /* JADX WARN: Code duplicated, block: B:7:0x0014  */
    public static final <T> Object single(Flow<? extends T> flow, Continuation<? super T> continuation) throws Throwable {
        C01251 c01251;
        Ref.ObjectRef objectRef;
        if (continuation instanceof C01251) {
            c01251 = (C01251) continuation;
            if ((c01251.label & Integer.MIN_VALUE) != 0) {
                c01251.label -= Integer.MIN_VALUE;
            } else {
                c01251 = new C01251(continuation);
            }
        } else {
            c01251 = new C01251(continuation);
        }
        Object obj = c01251.result;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        switch (c01251.label) {
            case 0:
                ResultKt.throwOnFailure(obj);
                final Ref.ObjectRef objectRef2 = new Ref.ObjectRef();
                objectRef2.element = (T) NullSurrogateKt.NULL;
                FlowCollector<? super Object> flowCollector = new FlowCollector() { // from class: kotlinx.coroutines.flow.FlowKt__ReduceKt.single.2
                    @Override // kotlinx.coroutines.flow.FlowCollector
                    public final Object emit(T t, Continuation<? super Unit> continuation2) {
                        if (!(objectRef2.element == NullSurrogateKt.NULL)) {
                            throw new IllegalArgumentException("Flow has more than one element".toString());
                        }
                        objectRef2.element = t;
                        return Unit.INSTANCE;
                    }
                };
                c01251.L$0 = objectRef2;
                c01251.label = 1;
                if (flow.collect(flowCollector, c01251) == coroutine_suspended) {
                    return coroutine_suspended;
                }
                objectRef = objectRef2;
                break;
                break;
            case 1:
                objectRef = (Ref.ObjectRef) c01251.L$0;
                ResultKt.throwOnFailure(obj);
                break;
            default:
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
        if (objectRef.element == NullSurrogateKt.NULL) {
            throw new NoSuchElementException("Flow is empty");
        }
        return objectRef.element;
    }

    /* JADX WARN: Code duplicated, block: B:28:0x0072 A[RETURN, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:29:0x0074  */
    /* JADX WARN: Code duplicated, block: B:7:0x0014  */
    public static final <T> Object singleOrNull(Flow<? extends T> flow, Continuation<? super T> continuation) throws Throwable {
        C01271 c01271;
        final Ref.ObjectRef objectRef;
        AbortFlowException e;
        FlowCollector<T> flowCollector;
        if (continuation instanceof C01271) {
            c01271 = (C01271) continuation;
            if ((c01271.label & Integer.MIN_VALUE) != 0) {
                c01271.label -= Integer.MIN_VALUE;
            } else {
                c01271 = new C01271(continuation);
            }
        } else {
            c01271 = new C01271(continuation);
        }
        Object obj = c01271.result;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        switch (c01271.label) {
            case 0:
                ResultKt.throwOnFailure(obj);
                objectRef = new Ref.ObjectRef();
                objectRef.element = (T) NullSurrogateKt.NULL;
                FlowCollector<T> flowCollector2 = new FlowCollector<T>() { // from class: kotlinx.coroutines.flow.FlowKt__ReduceKt$singleOrNull$$inlined$collectWhile$1
                    @Override // kotlinx.coroutines.flow.FlowCollector
                    public Object emit(T t, Continuation<? super Unit> continuation2) {
                        boolean z;
                        if (objectRef.element == NullSurrogateKt.NULL) {
                            objectRef.element = t;
                            z = true;
                        } else {
                            objectRef.element = (T) NullSurrogateKt.NULL;
                            z = false;
                        }
                        if (!z) {
                            throw new AbortFlowException(this);
                        }
                        return Unit.INSTANCE;
                    }
                };
                try {
                    c01271.L$0 = objectRef;
                    c01271.L$1 = flowCollector2;
                    c01271.label = 1;
                    if (flow.collect(flowCollector2, c01271) == coroutine_suspended) {
                        return coroutine_suspended;
                    }
                    if (objectRef.element == NullSurrogateKt.NULL) {
                        return null;
                    }
                    return objectRef.element;
                } catch (AbortFlowException e2) {
                    e = e2;
                    flowCollector = flowCollector2;
                    FlowExceptions_commonKt.checkOwnership(e, flowCollector);
                }
                break;
            case 1:
                flowCollector = (FlowKt__ReduceKt$singleOrNull$$inlined$collectWhile$1) c01271.L$1;
                objectRef = (Ref.ObjectRef) c01271.L$0;
                try {
                    ResultKt.throwOnFailure(obj);
                    break;
                } catch (AbortFlowException e3) {
                    e = e3;
                    FlowExceptions_commonKt.checkOwnership(e, flowCollector);
                }
                if (objectRef.element == NullSurrogateKt.NULL) {
                    return null;
                }
                return objectRef.element;
            default:
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
    }

    /* JADX WARN: Code duplicated, block: B:28:0x0072  */
    /* JADX WARN: Code duplicated, block: B:30:0x0075  */
    /* JADX WARN: Code duplicated, block: B:7:0x0014  */
    public static final <T> Object first(Flow<? extends T> flow, Continuation<? super T> continuation) throws Throwable {
        AnonymousClass1 anonymousClass1;
        final Ref.ObjectRef objectRef;
        AbortFlowException e;
        FlowCollector<T> flowCollector;
        if (continuation instanceof AnonymousClass1) {
            anonymousClass1 = (AnonymousClass1) continuation;
            if ((anonymousClass1.label & Integer.MIN_VALUE) != 0) {
                anonymousClass1.label -= Integer.MIN_VALUE;
            } else {
                anonymousClass1 = new AnonymousClass1(continuation);
            }
        } else {
            anonymousClass1 = new AnonymousClass1(continuation);
        }
        Object obj = anonymousClass1.result;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        switch (anonymousClass1.label) {
            case 0:
                ResultKt.throwOnFailure(obj);
                objectRef = new Ref.ObjectRef();
                objectRef.element = (T) NullSurrogateKt.NULL;
                FlowCollector<T> flowCollector2 = new FlowCollector<T>() { // from class: kotlinx.coroutines.flow.FlowKt__ReduceKt$first$$inlined$collectWhile$1
                    @Override // kotlinx.coroutines.flow.FlowCollector
                    public Object emit(T t, Continuation<? super Unit> continuation2) {
                        objectRef.element = t;
                        throw new AbortFlowException(this);
                    }
                };
                try {
                    anonymousClass1.L$0 = objectRef;
                    anonymousClass1.L$1 = flowCollector2;
                    anonymousClass1.label = 1;
                    if (flow.collect(flowCollector2, anonymousClass1) == coroutine_suspended) {
                        return coroutine_suspended;
                    }
                    if (objectRef.element != NullSurrogateKt.NULL) {
                        throw new NoSuchElementException("Expected at least one element");
                    }
                    return objectRef.element;
                } catch (AbortFlowException e2) {
                    e = e2;
                    flowCollector = flowCollector2;
                    FlowExceptions_commonKt.checkOwnership(e, flowCollector);
                }
                break;
            case 1:
                flowCollector = (FlowKt__ReduceKt$first$$inlined$collectWhile$1) anonymousClass1.L$1;
                objectRef = (Ref.ObjectRef) anonymousClass1.L$0;
                try {
                    ResultKt.throwOnFailure(obj);
                    break;
                } catch (AbortFlowException e3) {
                    e = e3;
                    FlowExceptions_commonKt.checkOwnership(e, flowCollector);
                }
                if (objectRef.element != NullSurrogateKt.NULL) {
                    throw new NoSuchElementException("Expected at least one element");
                }
                return objectRef.element;
            default:
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
    }

    /* JADX WARN: Code duplicated, block: B:28:0x007b  */
    /* JADX WARN: Code duplicated, block: B:30:0x007e  */
    /* JADX WARN: Code duplicated, block: B:7:0x0014  */
    public static final <T> Object first(Flow<? extends T> flow, Function2<? super T, ? super Continuation<? super Boolean>, ? extends Object> function2, Continuation<? super T> continuation) throws Throwable {
        AnonymousClass3 anonymousClass3;
        Function2<? super T, ? super Continuation<? super Boolean>, ? extends Object> function3;
        AbortFlowException e;
        Ref.ObjectRef objectRef;
        FlowCollector<? super Object> flowCollector;
        if (continuation instanceof AnonymousClass3) {
            anonymousClass3 = (AnonymousClass3) continuation;
            if ((anonymousClass3.label & Integer.MIN_VALUE) != 0) {
                anonymousClass3.label -= Integer.MIN_VALUE;
            } else {
                anonymousClass3 = new AnonymousClass3(continuation);
            }
        } else {
            anonymousClass3 = new AnonymousClass3(continuation);
        }
        Object obj = anonymousClass3.result;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        switch (anonymousClass3.label) {
            case 0:
                ResultKt.throwOnFailure(obj);
                function3 = function2;
                Ref.ObjectRef objectRef2 = new Ref.ObjectRef();
                objectRef2.element = (T) NullSurrogateKt.NULL;
                FlowCollector<? super Object> flowKt__ReduceKt$first$$inlined$collectWhile$2 = new FlowKt__ReduceKt$first$$inlined$collectWhile$2<>(function3, objectRef2);
                try {
                    anonymousClass3.L$0 = function3;
                    anonymousClass3.L$1 = objectRef2;
                    anonymousClass3.L$2 = flowKt__ReduceKt$first$$inlined$collectWhile$2;
                    anonymousClass3.label = 1;
                    if (flow.collect(flowKt__ReduceKt$first$$inlined$collectWhile$2, anonymousClass3) == coroutine_suspended) {
                        return coroutine_suspended;
                    }
                    objectRef = objectRef2;
                    if (objectRef.element != NullSurrogateKt.NULL) {
                        throw new NoSuchElementException("Expected at least one element matching the predicate " + function3);
                    }
                    return objectRef.element;
                } catch (AbortFlowException e2) {
                    e = e2;
                    objectRef = objectRef2;
                    flowCollector = flowKt__ReduceKt$first$$inlined$collectWhile$2;
                    FlowExceptions_commonKt.checkOwnership(e, flowCollector);
                }
                break;
            case 1:
                flowCollector = (FlowKt__ReduceKt$first$$inlined$collectWhile$2) anonymousClass3.L$2;
                objectRef = (Ref.ObjectRef) anonymousClass3.L$1;
                function3 = (Function2) anonymousClass3.L$0;
                try {
                    ResultKt.throwOnFailure(obj);
                    break;
                } catch (AbortFlowException e3) {
                    e = e3;
                    FlowExceptions_commonKt.checkOwnership(e, flowCollector);
                }
                if (objectRef.element != NullSurrogateKt.NULL) {
                    throw new NoSuchElementException("Expected at least one element matching the predicate " + function3);
                }
                return objectRef.element;
            default:
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
    }

    /* JADX WARN: Code duplicated, block: B:7:0x0014  */
    public static final <T> Object firstOrNull(Flow<? extends T> flow, Continuation<? super T> continuation) throws Throwable {
        C01161 c01161;
        final Ref.ObjectRef result;
        AbortFlowException e$iv;
        FlowCollector<T> flowCollector;
        if (continuation instanceof C01161) {
            c01161 = (C01161) continuation;
            if ((c01161.label & Integer.MIN_VALUE) != 0) {
                c01161.label -= Integer.MIN_VALUE;
            } else {
                c01161 = new C01161(continuation);
            }
        } else {
            c01161 = new C01161(continuation);
        }
        Object $result = c01161.result;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        switch (c01161.label) {
            case 0:
                ResultKt.throwOnFailure($result);
                result = new Ref.ObjectRef();
                FlowCollector<T> flowCollector2 = new FlowCollector<T>() { // from class: kotlinx.coroutines.flow.FlowKt__ReduceKt$firstOrNull$$inlined$collectWhile$1
                    @Override // kotlinx.coroutines.flow.FlowCollector
                    public Object emit(T t, Continuation<? super Unit> continuation2) {
                        result.element = t;
                        throw new AbortFlowException(this);
                    }
                };
                try {
                    c01161.L$0 = result;
                    c01161.L$1 = flowCollector2;
                    c01161.label = 1;
                    if (flow.collect(flowCollector2, c01161) == coroutine_suspended) {
                        return coroutine_suspended;
                    }
                    return result.element;
                } catch (AbortFlowException e) {
                    e$iv = e;
                    flowCollector = flowCollector2;
                    FlowExceptions_commonKt.checkOwnership(e$iv, flowCollector);
                }
                break;
            case 1:
                flowCollector = (FlowKt__ReduceKt$firstOrNull$$inlined$collectWhile$1) c01161.L$1;
                result = (Ref.ObjectRef) c01161.L$0;
                try {
                    ResultKt.throwOnFailure($result);
                    break;
                } catch (AbortFlowException e2) {
                    e$iv = e2;
                    FlowExceptions_commonKt.checkOwnership(e$iv, flowCollector);
                }
                return result.element;
            default:
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
    }

    /* JADX WARN: Code duplicated, block: B:7:0x0014  */
    public static final <T> Object firstOrNull(Flow<? extends T> flow, Function2<? super T, ? super Continuation<? super Boolean>, ? extends Object> function2, Continuation<? super T> continuation) throws Throwable {
        C01173 c01173;
        Ref.ObjectRef result;
        FlowCollector<? super Object> collector$iv;
        AbortFlowException e$iv;
        if (continuation instanceof C01173) {
            c01173 = (C01173) continuation;
            if ((c01173.label & Integer.MIN_VALUE) != 0) {
                c01173.label -= Integer.MIN_VALUE;
            } else {
                c01173 = new C01173(continuation);
            }
        } else {
            c01173 = new C01173(continuation);
        }
        Object $result = c01173.result;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        switch (c01173.label) {
            case 0:
                ResultKt.throwOnFailure($result);
                Ref.ObjectRef result2 = new Ref.ObjectRef();
                FlowCollector<? super Object> collector$iv2 = new FlowKt__ReduceKt$firstOrNull$$inlined$collectWhile$2<>(function2, result2);
                try {
                    c01173.L$0 = result2;
                    c01173.L$1 = collector$iv2;
                    c01173.label = 1;
                    if (flow.collect(collector$iv2, c01173) == coroutine_suspended) {
                        return coroutine_suspended;
                    }
                    result = result2;
                    return result.element;
                } catch (AbortFlowException e) {
                    result = result2;
                    collector$iv = collector$iv2;
                    e$iv = e;
                    FlowExceptions_commonKt.checkOwnership(e$iv, collector$iv);
                }
                break;
            case 1:
                collector$iv = (FlowKt__ReduceKt$firstOrNull$$inlined$collectWhile$2) c01173.L$1;
                result = (Ref.ObjectRef) c01173.L$0;
                try {
                    ResultKt.throwOnFailure($result);
                    break;
                } catch (AbortFlowException e2) {
                    e$iv = e2;
                    FlowExceptions_commonKt.checkOwnership(e$iv, collector$iv);
                }
                return result.element;
            default:
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
    }

    /* JADX WARN: Code duplicated, block: B:7:0x0014  */
    public static final <T> Object last(Flow<? extends T> flow, Continuation<? super T> continuation) throws Throwable {
        C01191 c01191;
        Ref.ObjectRef objectRef;
        if (continuation instanceof C01191) {
            c01191 = (C01191) continuation;
            if ((c01191.label & Integer.MIN_VALUE) != 0) {
                c01191.label -= Integer.MIN_VALUE;
            } else {
                c01191 = new C01191(continuation);
            }
        } else {
            c01191 = new C01191(continuation);
        }
        Object obj = c01191.result;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        switch (c01191.label) {
            case 0:
                ResultKt.throwOnFailure(obj);
                final Ref.ObjectRef objectRef2 = new Ref.ObjectRef();
                objectRef2.element = (T) NullSurrogateKt.NULL;
                FlowCollector<? super Object> flowCollector = new FlowCollector() { // from class: kotlinx.coroutines.flow.FlowKt__ReduceKt.last.2
                    @Override // kotlinx.coroutines.flow.FlowCollector
                    public final Object emit(T t, Continuation<? super Unit> continuation2) {
                        objectRef2.element = t;
                        return Unit.INSTANCE;
                    }
                };
                c01191.L$0 = objectRef2;
                c01191.label = 1;
                if (flow.collect(flowCollector, c01191) == coroutine_suspended) {
                    return coroutine_suspended;
                }
                objectRef = objectRef2;
                break;
                break;
            case 1:
                objectRef = (Ref.ObjectRef) c01191.L$0;
                ResultKt.throwOnFailure(obj);
                break;
            default:
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
        if (objectRef.element == NullSurrogateKt.NULL) {
            throw new NoSuchElementException("Expected at least one element");
        }
        return objectRef.element;
    }

    /* JADX WARN: Code duplicated, block: B:7:0x0014  */
    public static final <T> Object lastOrNull(Flow<? extends T> flow, Continuation<? super T> continuation) throws Throwable {
        C01211 c01211;
        Ref.ObjectRef result;
        if (continuation instanceof C01211) {
            c01211 = (C01211) continuation;
            if ((c01211.label & Integer.MIN_VALUE) != 0) {
                c01211.label -= Integer.MIN_VALUE;
            } else {
                c01211 = new C01211(continuation);
            }
        } else {
            c01211 = new C01211(continuation);
        }
        Object $result = c01211.result;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        switch (c01211.label) {
            case 0:
                ResultKt.throwOnFailure($result);
                final Ref.ObjectRef result2 = new Ref.ObjectRef();
                FlowCollector<? super Object> flowCollector = new FlowCollector() { // from class: kotlinx.coroutines.flow.FlowKt__ReduceKt.lastOrNull.2
                    @Override // kotlinx.coroutines.flow.FlowCollector
                    public final Object emit(T t, Continuation<? super Unit> continuation2) {
                        result2.element = t;
                        return Unit.INSTANCE;
                    }
                };
                c01211.L$0 = result2;
                c01211.label = 1;
                if (flow.collect(flowCollector, c01211) == coroutine_suspended) {
                    return coroutine_suspended;
                }
                result = result2;
                break;
                break;
            case 1:
                result = (Ref.ObjectRef) c01211.L$0;
                ResultKt.throwOnFailure($result);
                break;
            default:
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
        return result.element;
    }
}
