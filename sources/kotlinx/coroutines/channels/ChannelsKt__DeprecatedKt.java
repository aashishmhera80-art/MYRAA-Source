package kotlinx.coroutines.channels;

import androidx.core.view.MotionEventCompat;
import com.getcapacitor.PluginMethod;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.CancellationException;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.ExceptionsKt;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.IndexedValue;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.Boxing;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.GlobalScope;
import okhttp3.internal.http.StatusLine;

/* JADX INFO: compiled from: Deprecated.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000®\u0001\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u001f\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0011\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010$\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010%\n\u0002\b\u0002\n\u0002\u0010!\n\u0000\n\u0002\u0010#\n\u0000\n\u0002\u0010\"\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u001aJ\u0010\u0000\u001a#\u0012\u0015\u0012\u0013\u0018\u00010\u0002¢\u0006\f\b\u0003\u0012\b\b\u0004\u0012\u0004\b\b(\u0005\u0012\u0004\u0012\u00020\u00060\u0001j\u0002`\u00072\u001a\u0010\b\u001a\u000e\u0012\n\b\u0001\u0012\u0006\u0012\u0002\b\u00030\n0\t\"\u0006\u0012\u0002\b\u00030\nH\u0001¢\u0006\u0002\u0010\u000b\u001a\u001e\u0010\f\u001a\u00020\r\"\u0004\b\u0000\u0010\u000e*\b\u0012\u0004\u0012\u0002H\u000e0\nH\u0087@¢\u0006\u0002\u0010\u000f\u001aC\u0010\u0010\u001a\u0002H\u0011\"\u0004\b\u0000\u0010\u000e\"\u0004\b\u0001\u0010\u0011*\b\u0012\u0004\u0012\u0002H\u000e0\u00122\u001d\u0010\u0013\u001a\u0019\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u000e0\n\u0012\u0004\u0012\u0002H\u00110\u0001¢\u0006\u0002\b\u0014H\u0087\b¢\u0006\u0002\u0010\u0015\u001a2\u0010\u0016\u001a\u00020\u0006\"\u0004\b\u0000\u0010\u000e*\b\u0012\u0004\u0012\u0002H\u000e0\u00122\u0012\u0010\u0017\u001a\u000e\u0012\u0004\u0012\u0002H\u000e\u0012\u0004\u0012\u00020\u00060\u0001H\u0087H¢\u0006\u0002\u0010\u0018\u001a1\u0010\u0019\u001a#\u0012\u0015\u0012\u0013\u0018\u00010\u0002¢\u0006\f\b\u0003\u0012\b\b\u0004\u0012\u0004\b\b(\u0005\u0012\u0004\u0012\u00020\u00060\u0001j\u0002`\u0007*\u0006\u0012\u0002\b\u00030\nH\u0001\u001a\u001e\u0010\u001a\u001a\u00020\u001b\"\u0004\b\u0000\u0010\u000e*\b\u0012\u0004\u0012\u0002H\u000e0\nH\u0087@¢\u0006\u0002\u0010\u000f\u001a\u001e\u0010\u001c\u001a\b\u0012\u0004\u0012\u0002H\u000e0\n\"\u0004\b\u0000\u0010\u000e*\b\u0012\u0004\u0012\u0002H\u000e0\nH\u0007\u001aW\u0010\u001d\u001a\b\u0012\u0004\u0012\u0002H\u000e0\n\"\u0004\b\u0000\u0010\u000e\"\u0004\b\u0001\u0010\u001e*\b\u0012\u0004\u0012\u0002H\u000e0\n2\b\b\u0002\u0010\u001f\u001a\u00020 2\"\u0010!\u001a\u001e\b\u0001\u0012\u0004\u0012\u0002H\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u001e0#\u0012\u0006\u0012\u0004\u0018\u00010$0\"H\u0001¢\u0006\u0002\u0010%\u001a0\u0010&\u001a\b\u0012\u0004\u0012\u0002H\u000e0\n\"\u0004\b\u0000\u0010\u000e*\b\u0012\u0004\u0012\u0002H\u000e0\n2\u0006\u0010'\u001a\u00020\u001b2\b\b\u0002\u0010\u001f\u001a\u00020 H\u0007\u001aQ\u0010(\u001a\b\u0012\u0004\u0012\u0002H\u000e0\n\"\u0004\b\u0000\u0010\u000e*\b\u0012\u0004\u0012\u0002H\u000e0\n2\b\b\u0002\u0010\u001f\u001a\u00020 2\"\u0010)\u001a\u001e\b\u0001\u0012\u0004\u0012\u0002H\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\r0#\u0012\u0006\u0012\u0004\u0018\u00010$0\"H\u0007¢\u0006\u0002\u0010%\u001a&\u0010*\u001a\u0002H\u000e\"\u0004\b\u0000\u0010\u000e*\b\u0012\u0004\u0012\u0002H\u000e0\n2\u0006\u0010+\u001a\u00020\u001bH\u0087@¢\u0006\u0002\u0010,\u001a(\u0010-\u001a\u0004\u0018\u0001H\u000e\"\u0004\b\u0000\u0010\u000e*\b\u0012\u0004\u0012\u0002H\u000e0\n2\u0006\u0010+\u001a\u00020\u001bH\u0087@¢\u0006\u0002\u0010,\u001aQ\u0010.\u001a\b\u0012\u0004\u0012\u0002H\u000e0\n\"\u0004\b\u0000\u0010\u000e*\b\u0012\u0004\u0012\u0002H\u000e0\n2\b\b\u0002\u0010\u001f\u001a\u00020 2\"\u0010)\u001a\u001e\b\u0001\u0012\u0004\u0012\u0002H\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\r0#\u0012\u0006\u0012\u0004\u0018\u00010$0\"H\u0001¢\u0006\u0002\u0010%\u001af\u0010/\u001a\b\u0012\u0004\u0012\u0002H\u000e0\n\"\u0004\b\u0000\u0010\u000e*\b\u0012\u0004\u0012\u0002H\u000e0\n2\b\b\u0002\u0010\u001f\u001a\u00020 27\u0010)\u001a3\b\u0001\u0012\u0013\u0012\u00110\u001b¢\u0006\f\b\u0003\u0012\b\b\u0004\u0012\u0004\b\b(+\u0012\u0004\u0012\u0002H\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\r0#\u0012\u0006\u0012\u0004\u0018\u00010$00H\u0007¢\u0006\u0002\u00101\u001aQ\u00102\u001a\b\u0012\u0004\u0012\u0002H\u000e0\n\"\u0004\b\u0000\u0010\u000e*\b\u0012\u0004\u0012\u0002H\u000e0\n2\b\b\u0002\u0010\u001f\u001a\u00020 2\"\u0010)\u001a\u001e\b\u0001\u0012\u0004\u0012\u0002H\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\r0#\u0012\u0006\u0012\u0004\u0018\u00010$0\"H\u0007¢\u0006\u0002\u0010%\u001a$\u00103\u001a\b\u0012\u0004\u0012\u0002H\u000e0\n\"\b\b\u0000\u0010\u000e*\u00020$*\n\u0012\u0006\u0012\u0004\u0018\u0001H\u000e0\nH\u0001\u001a>\u00104\u001a\u0002H5\"\b\b\u0000\u0010\u000e*\u00020$\"\u0010\b\u0001\u00105*\n\u0012\u0006\b\u0000\u0012\u0002H\u000e06*\n\u0012\u0006\u0012\u0004\u0018\u0001H\u000e0\n2\u0006\u00107\u001a\u0002H5H\u0087@¢\u0006\u0002\u00108\u001a<\u00104\u001a\u0002H5\"\b\b\u0000\u0010\u000e*\u00020$\"\u000e\b\u0001\u00105*\b\u0012\u0004\u0012\u0002H\u000e09*\n\u0012\u0006\u0012\u0004\u0018\u0001H\u000e0\n2\u0006\u00107\u001a\u0002H5H\u0087@¢\u0006\u0002\u0010:\u001a\u001e\u0010;\u001a\u0002H\u000e\"\u0004\b\u0000\u0010\u000e*\b\u0012\u0004\u0012\u0002H\u000e0\nH\u0087@¢\u0006\u0002\u0010\u000f\u001a \u0010<\u001a\u0004\u0018\u0001H\u000e\"\u0004\b\u0000\u0010\u000e*\b\u0012\u0004\u0012\u0002H\u000e0\nH\u0087@¢\u0006\u0002\u0010\u000f\u001a]\u0010=\u001a\b\u0012\u0004\u0012\u0002H\u00110\n\"\u0004\b\u0000\u0010\u000e\"\u0004\b\u0001\u0010\u0011*\b\u0012\u0004\u0012\u0002H\u000e0\n2\b\b\u0002\u0010\u001f\u001a\u00020 2(\u0010>\u001a$\b\u0001\u0012\u0004\u0012\u0002H\u000e\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00110\n0#\u0012\u0006\u0012\u0004\u0018\u00010$0\"H\u0007¢\u0006\u0002\u0010%\u001a&\u0010?\u001a\u00020\u001b\"\u0004\b\u0000\u0010\u000e*\b\u0012\u0004\u0012\u0002H\u000e0\n2\u0006\u0010@\u001a\u0002H\u000eH\u0087@¢\u0006\u0002\u0010A\u001a\u001e\u0010B\u001a\u0002H\u000e\"\u0004\b\u0000\u0010\u000e*\b\u0012\u0004\u0012\u0002H\u000e0\nH\u0087@¢\u0006\u0002\u0010\u000f\u001a&\u0010C\u001a\u00020\u001b\"\u0004\b\u0000\u0010\u000e*\b\u0012\u0004\u0012\u0002H\u000e0\n2\u0006\u0010@\u001a\u0002H\u000eH\u0087@¢\u0006\u0002\u0010A\u001a \u0010D\u001a\u0004\u0018\u0001H\u000e\"\u0004\b\u0000\u0010\u000e*\b\u0012\u0004\u0012\u0002H\u000e0\nH\u0087@¢\u0006\u0002\u0010\u000f\u001aW\u0010E\u001a\b\u0012\u0004\u0012\u0002H\u00110\n\"\u0004\b\u0000\u0010\u000e\"\u0004\b\u0001\u0010\u0011*\b\u0012\u0004\u0012\u0002H\u000e0\n2\b\b\u0002\u0010\u001f\u001a\u00020 2\"\u0010>\u001a\u001e\b\u0001\u0012\u0004\u0012\u0002H\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00110#\u0012\u0006\u0012\u0004\u0018\u00010$0\"H\u0001¢\u0006\u0002\u0010%\u001al\u0010F\u001a\b\u0012\u0004\u0012\u0002H\u00110\n\"\u0004\b\u0000\u0010\u000e\"\u0004\b\u0001\u0010\u0011*\b\u0012\u0004\u0012\u0002H\u000e0\n2\b\b\u0002\u0010\u001f\u001a\u00020 27\u0010>\u001a3\b\u0001\u0012\u0013\u0012\u00110\u001b¢\u0006\f\b\u0003\u0012\b\b\u0004\u0012\u0004\b\b(+\u0012\u0004\u0012\u0002H\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00110#\u0012\u0006\u0012\u0004\u0018\u00010$00H\u0001¢\u0006\u0002\u00101\u001ar\u0010G\u001a\b\u0012\u0004\u0012\u0002H\u00110\n\"\u0004\b\u0000\u0010\u000e\"\b\b\u0001\u0010\u0011*\u00020$*\b\u0012\u0004\u0012\u0002H\u000e0\n2\b\b\u0002\u0010\u001f\u001a\u00020 29\u0010>\u001a5\b\u0001\u0012\u0013\u0012\u00110\u001b¢\u0006\f\b\u0003\u0012\b\b\u0004\u0012\u0004\b\b(+\u0012\u0004\u0012\u0002H\u000e\u0012\f\u0012\n\u0012\u0006\u0012\u0004\u0018\u0001H\u00110#\u0012\u0006\u0012\u0004\u0018\u00010$00H\u0007¢\u0006\u0002\u00101\u001a]\u0010H\u001a\b\u0012\u0004\u0012\u0002H\u00110\n\"\u0004\b\u0000\u0010\u000e\"\b\b\u0001\u0010\u0011*\u00020$*\b\u0012\u0004\u0012\u0002H\u000e0\n2\b\b\u0002\u0010\u001f\u001a\u00020 2$\u0010>\u001a \b\u0001\u0012\u0004\u0012\u0002H\u000e\u0012\f\u0012\n\u0012\u0006\u0012\u0004\u0018\u0001H\u00110#\u0012\u0006\u0012\u0004\u0018\u00010$0\"H\u0007¢\u0006\u0002\u0010%\u001a<\u0010I\u001a\u0004\u0018\u0001H\u000e\"\u0004\b\u0000\u0010\u000e*\b\u0012\u0004\u0012\u0002H\u000e0\n2\u001a\u0010J\u001a\u0016\u0012\u0006\b\u0000\u0012\u0002H\u000e0Kj\n\u0012\u0006\b\u0000\u0012\u0002H\u000e`LH\u0087@¢\u0006\u0002\u0010M\u001a<\u0010N\u001a\u0004\u0018\u0001H\u000e\"\u0004\b\u0000\u0010\u000e*\b\u0012\u0004\u0012\u0002H\u000e0\n2\u001a\u0010J\u001a\u0016\u0012\u0006\b\u0000\u0012\u0002H\u000e0Kj\n\u0012\u0006\b\u0000\u0012\u0002H\u000e`LH\u0087@¢\u0006\u0002\u0010M\u001a\u001e\u0010O\u001a\u00020\r\"\u0004\b\u0000\u0010\u000e*\b\u0012\u0004\u0012\u0002H\u000e0\nH\u0087@¢\u0006\u0002\u0010\u000f\u001a$\u0010P\u001a\b\u0012\u0004\u0012\u0002H\u000e0\n\"\b\b\u0000\u0010\u000e*\u00020$*\n\u0012\u0006\u0012\u0004\u0018\u0001H\u000e0\nH\u0007\u001a\u001e\u0010Q\u001a\u0002H\u000e\"\u0004\b\u0000\u0010\u000e*\b\u0012\u0004\u0012\u0002H\u000e0\nH\u0087@¢\u0006\u0002\u0010\u000f\u001a \u0010R\u001a\u0004\u0018\u0001H\u000e\"\u0004\b\u0000\u0010\u000e*\b\u0012\u0004\u0012\u0002H\u000e0\nH\u0087@¢\u0006\u0002\u0010\u000f\u001a0\u0010S\u001a\b\u0012\u0004\u0012\u0002H\u000e0\n\"\u0004\b\u0000\u0010\u000e*\b\u0012\u0004\u0012\u0002H\u000e0\n2\u0006\u0010'\u001a\u00020\u001b2\b\b\u0002\u0010\u001f\u001a\u00020 H\u0007\u001aQ\u0010T\u001a\b\u0012\u0004\u0012\u0002H\u000e0\n\"\u0004\b\u0000\u0010\u000e*\b\u0012\u0004\u0012\u0002H\u000e0\n2\b\b\u0002\u0010\u001f\u001a\u00020 2\"\u0010)\u001a\u001e\b\u0001\u0012\u0004\u0012\u0002H\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\r0#\u0012\u0006\u0012\u0004\u0018\u00010$0\"H\u0007¢\u0006\u0002\u0010%\u001a6\u0010U\u001a\u0002H5\"\u0004\b\u0000\u0010\u000e\"\u000e\b\u0001\u00105*\b\u0012\u0004\u0012\u0002H\u000e09*\b\u0012\u0004\u0012\u0002H\u000e0\n2\u0006\u00107\u001a\u0002H5H\u0081@¢\u0006\u0002\u0010:\u001a8\u0010V\u001a\u0002H5\"\u0004\b\u0000\u0010\u000e\"\u0010\b\u0001\u00105*\n\u0012\u0006\b\u0000\u0012\u0002H\u000e06*\b\u0012\u0004\u0012\u0002H\u000e0\n2\u0006\u00107\u001a\u0002H5H\u0081@¢\u0006\u0002\u00108\u001a<\u0010W\u001a\u000e\u0012\u0004\u0012\u0002H\u001e\u0012\u0004\u0012\u0002HY0X\"\u0004\b\u0000\u0010\u001e\"\u0004\b\u0001\u0010Y*\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u0002H\u001e\u0012\u0004\u0012\u0002HY0Z0\nH\u0087@¢\u0006\u0002\u0010\u000f\u001aR\u0010W\u001a\u0002H[\"\u0004\b\u0000\u0010\u001e\"\u0004\b\u0001\u0010Y\"\u0018\b\u0002\u0010[*\u0012\u0012\u0006\b\u0000\u0012\u0002H\u001e\u0012\u0006\b\u0000\u0012\u0002HY0\\*\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u0002H\u001e\u0012\u0004\u0012\u0002HY0Z0\n2\u0006\u00107\u001a\u0002H[H\u0081@¢\u0006\u0002\u0010]\u001a$\u0010^\u001a\b\u0012\u0004\u0012\u0002H\u000e0_\"\u0004\b\u0000\u0010\u000e*\b\u0012\u0004\u0012\u0002H\u000e0\nH\u0087@¢\u0006\u0002\u0010\u000f\u001a$\u0010`\u001a\b\u0012\u0004\u0012\u0002H\u000e0a\"\u0004\b\u0000\u0010\u000e*\b\u0012\u0004\u0012\u0002H\u000e0\nH\u0081@¢\u0006\u0002\u0010\u000f\u001a$\u0010b\u001a\b\u0012\u0004\u0012\u0002H\u000e0c\"\u0004\b\u0000\u0010\u000e*\b\u0012\u0004\u0012\u0002H\u000e0\nH\u0087@¢\u0006\u0002\u0010\u000f\u001a.\u0010d\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u000e0e0\n\"\u0004\b\u0000\u0010\u000e*\b\u0012\u0004\u0012\u0002H\u000e0\n2\b\b\u0002\u0010\u001f\u001a\u00020 H\u0007\u001a?\u0010f\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u0002H\u000e\u0012\u0004\u0012\u0002H\u00110Z0\n\"\u0004\b\u0000\u0010\u000e\"\u0004\b\u0001\u0010\u0011*\b\u0012\u0004\u0012\u0002H\u000e0\n2\f\u0010g\u001a\b\u0012\u0004\u0012\u0002H\u00110\nH\u0087\u0004\u001az\u0010f\u001a\b\u0012\u0004\u0012\u0002HY0\n\"\u0004\b\u0000\u0010\u000e\"\u0004\b\u0001\u0010\u0011\"\u0004\b\u0002\u0010Y*\b\u0012\u0004\u0012\u0002H\u000e0\n2\f\u0010g\u001a\b\u0012\u0004\u0012\u0002H\u00110\n2\b\b\u0002\u0010\u001f\u001a\u00020 26\u0010>\u001a2\u0012\u0013\u0012\u0011H\u000e¢\u0006\f\b\u0003\u0012\b\b\u0004\u0012\u0004\b\b(h\u0012\u0013\u0012\u0011H\u0011¢\u0006\f\b\u0003\u0012\b\b\u0004\u0012\u0004\b\b(i\u0012\u0004\u0012\u0002HY0\"H\u0001¨\u0006j"}, d2 = {"consumesAll", "Lkotlin/Function1;", "", "Lkotlin/ParameterName;", "name", "cause", "", "Lkotlinx/coroutines/CompletionHandler;", "channels", "", "Lkotlinx/coroutines/channels/ReceiveChannel;", "([Lkotlinx/coroutines/channels/ReceiveChannel;)Lkotlin/jvm/functions/Function1;", "any", "", "E", "(Lkotlinx/coroutines/channels/ReceiveChannel;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "consume", "R", "Lkotlinx/coroutines/channels/BroadcastChannel;", "block", "Lkotlin/ExtensionFunctionType;", "(Lkotlinx/coroutines/channels/BroadcastChannel;Lkotlin/jvm/functions/Function1;)Ljava/lang/Object;", "consumeEach", "action", "(Lkotlinx/coroutines/channels/BroadcastChannel;Lkotlin/jvm/functions/Function1;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "consumes", "count", "", "distinct", "distinctBy", "K", "context", "Lkotlin/coroutines/CoroutineContext;", "selector", "Lkotlin/Function2;", "Lkotlin/coroutines/Continuation;", "", "(Lkotlinx/coroutines/channels/ReceiveChannel;Lkotlin/coroutines/CoroutineContext;Lkotlin/jvm/functions/Function2;)Lkotlinx/coroutines/channels/ReceiveChannel;", "drop", "n", "dropWhile", "predicate", "elementAt", "index", "(Lkotlinx/coroutines/channels/ReceiveChannel;ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "elementAtOrNull", "filter", "filterIndexed", "Lkotlin/Function3;", "(Lkotlinx/coroutines/channels/ReceiveChannel;Lkotlin/coroutines/CoroutineContext;Lkotlin/jvm/functions/Function3;)Lkotlinx/coroutines/channels/ReceiveChannel;", "filterNot", "filterNotNull", "filterNotNullTo", "C", "", "destination", "(Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/util/Collection;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "Lkotlinx/coroutines/channels/SendChannel;", "(Lkotlinx/coroutines/channels/ReceiveChannel;Lkotlinx/coroutines/channels/SendChannel;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "first", "firstOrNull", "flatMap", "transform", "indexOf", "element", "(Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/lang/Object;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "last", "lastIndexOf", "lastOrNull", "map", "mapIndexed", "mapIndexedNotNull", "mapNotNull", "maxWith", "comparator", "Ljava/util/Comparator;", "Lkotlin/Comparator;", "(Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/util/Comparator;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "minWith", PluginMethod.RETURN_NONE, "requireNoNulls", "single", "singleOrNull", "take", "takeWhile", "toChannel", "toCollection", "toMap", "", "V", "Lkotlin/Pair;", "M", "", "(Lkotlinx/coroutines/channels/ReceiveChannel;Ljava/util/Map;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "toMutableList", "", "toMutableSet", "", "toSet", "", "withIndex", "Lkotlin/collections/IndexedValue;", "zip", "other", "a", "b", "kotlinx-coroutines-core"}, k = 5, mv = {1, 9, 0}, xi = 48, xs = "kotlinx/coroutines/channels/ChannelsKt")
final /* synthetic */ class ChannelsKt__DeprecatedKt {

    /* JADX INFO: renamed from: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$any$1, reason: invalid class name */
    /* JADX INFO: compiled from: Deprecated.kt */
    @Metadata(k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt", f = "Deprecated.kt", i = {0}, l = {434}, m = "any", n = {"$this$consume$iv"}, s = {"L$0"})
    static final class AnonymousClass1<E> extends ContinuationImpl {
        Object L$0;
        int label;
        /* synthetic */ Object result;

        AnonymousClass1(Continuation<? super AnonymousClass1> continuation) {
            super(continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return ChannelsKt__DeprecatedKt.any(null, this);
        }
    }

    /* JADX INFO: renamed from: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$consumeEach$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: Deprecated.kt */
    @Metadata(k = 3, mv = {1, 9, 0}, xi = 176)
    @DebugMetadata(c = "kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt", f = "Deprecated.kt", i = {0, 0}, l = {MotionEventCompat.AXIS_GENERIC_10}, m = "consumeEach", n = {"action", "channel$iv"}, s = {"L$0", "L$1"})
    static final class C00711<E> extends ContinuationImpl {
        Object L$0;
        Object L$1;
        Object L$2;
        int label;
        /* synthetic */ Object result;

        C00711(Continuation<? super C00711> continuation) {
            super(continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return ChannelsKt__DeprecatedKt.consumeEach(null, null, this);
        }
    }

    /* JADX INFO: renamed from: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$count$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: Deprecated.kt */
    @Metadata(k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt", f = "Deprecated.kt", i = {0, 0}, l = {517}, m = "count", n = {"count", "$this$consume$iv$iv"}, s = {"L$0", "L$1"})
    static final class C00741<E> extends ContinuationImpl {
        Object L$0;
        Object L$1;
        Object L$2;
        int label;
        /* synthetic */ Object result;

        C00741(Continuation<? super C00741> continuation) {
            super(continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return ChannelsKt__DeprecatedKt.count(null, this);
        }
    }

    /* JADX INFO: renamed from: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$elementAt$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: Deprecated.kt */
    @Metadata(k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt", f = "Deprecated.kt", i = {0, 0, 0}, l = {68}, m = "elementAt", n = {"$this$consume$iv", "index", "count"}, s = {"L$0", "I$0", "I$1"})
    static final class C00791<E> extends ContinuationImpl {
        int I$0;
        int I$1;
        Object L$0;
        Object L$1;
        int label;
        /* synthetic */ Object result;

        C00791(Continuation<? super C00791> continuation) {
            super(continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return ChannelsKt__DeprecatedKt.elementAt(null, 0, this);
        }
    }

    /* JADX INFO: renamed from: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$elementAtOrNull$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: Deprecated.kt */
    @Metadata(k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt", f = "Deprecated.kt", i = {0, 0, 0}, l = {83}, m = "elementAtOrNull", n = {"$this$consume$iv", "index", "count"}, s = {"L$0", "I$0", "I$1"})
    static final class C00801<E> extends ContinuationImpl {
        int I$0;
        int I$1;
        Object L$0;
        Object L$1;
        int label;
        /* synthetic */ Object result;

        C00801(Continuation<? super C00801> continuation) {
            super(continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return ChannelsKt__DeprecatedKt.elementAtOrNull(null, 0, this);
        }
    }

    /* JADX INFO: renamed from: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$filterNotNullTo$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: Deprecated.kt */
    @Metadata(k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt", f = "Deprecated.kt", i = {0, 0}, l = {517}, m = "filterNotNullTo", n = {"destination", "$this$consume$iv$iv"}, s = {"L$0", "L$1"})
    static final class C00851<E, C extends Collection<? super E>> extends ContinuationImpl {
        Object L$0;
        Object L$1;
        Object L$2;
        int label;
        /* synthetic */ Object result;

        C00851(Continuation<? super C00851> continuation) {
            super(continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return ChannelsKt__DeprecatedKt.filterNotNullTo((ReceiveChannel) null, (Collection) null, this);
        }
    }

    /* JADX INFO: renamed from: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$filterNotNullTo$3, reason: invalid class name */
    /* JADX INFO: compiled from: Deprecated.kt */
    @Metadata(k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt", f = "Deprecated.kt", i = {0, 0, 1, 1}, l = {517, 272}, m = "filterNotNullTo", n = {"destination", "$this$consume$iv$iv", "destination", "$this$consume$iv$iv"}, s = {"L$0", "L$1", "L$0", "L$1"})
    static final class AnonymousClass3<E, C extends SendChannel<? super E>> extends ContinuationImpl {
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
            return ChannelsKt__DeprecatedKt.filterNotNullTo((ReceiveChannel) null, (SendChannel) null, this);
        }
    }

    /* JADX INFO: renamed from: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$first$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: Deprecated.kt */
    @Metadata(k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt", f = "Deprecated.kt", i = {0, 0}, l = {95}, m = "first", n = {"$this$consume$iv", "iterator"}, s = {"L$0", "L$1"})
    static final class C00861<E> extends ContinuationImpl {
        Object L$0;
        Object L$1;
        int label;
        /* synthetic */ Object result;

        C00861(Continuation<? super C00861> continuation) {
            super(continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return ChannelsKt__DeprecatedKt.first(null, this);
        }
    }

    /* JADX INFO: renamed from: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$firstOrNull$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: Deprecated.kt */
    @Metadata(k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt", f = "Deprecated.kt", i = {0, 0}, l = {105}, m = "firstOrNull", n = {"$this$consume$iv", "iterator"}, s = {"L$0", "L$1"})
    static final class C00871<E> extends ContinuationImpl {
        Object L$0;
        Object L$1;
        int label;
        /* synthetic */ Object result;

        C00871(Continuation<? super C00871> continuation) {
            super(continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return ChannelsKt__DeprecatedKt.firstOrNull(null, this);
        }
    }

    /* JADX INFO: renamed from: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$indexOf$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: Deprecated.kt */
    @Metadata(k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt", f = "Deprecated.kt", i = {0, 0, 0}, l = {517}, m = "indexOf", n = {"element", "index", "$this$consume$iv$iv"}, s = {"L$0", "L$1", "L$2"})
    static final class C00891<E> extends ContinuationImpl {
        Object L$0;
        Object L$1;
        Object L$2;
        Object L$3;
        int label;
        /* synthetic */ Object result;

        C00891(Continuation<? super C00891> continuation) {
            super(continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return ChannelsKt__DeprecatedKt.indexOf(null, null, this);
        }
    }

    /* JADX INFO: renamed from: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$last$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: Deprecated.kt */
    @Metadata(k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt", f = "Deprecated.kt", i = {0, 0, 1, 1, 1}, l = {127, 130}, m = "last", n = {"$this$consume$iv", "iterator", "$this$consume$iv", "iterator", "last"}, s = {"L$0", "L$1", "L$0", "L$1", "L$2"})
    static final class C00901<E> extends ContinuationImpl {
        Object L$0;
        Object L$1;
        Object L$2;
        int label;
        /* synthetic */ Object result;

        C00901(Continuation<? super C00901> continuation) {
            super(continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return ChannelsKt__DeprecatedKt.last(null, this);
        }
    }

    /* JADX INFO: renamed from: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$lastIndexOf$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: Deprecated.kt */
    @Metadata(k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt", f = "Deprecated.kt", i = {0, 0, 0, 0}, l = {517}, m = "lastIndexOf", n = {"element", "lastIndex", "index", "$this$consume$iv$iv"}, s = {"L$0", "L$1", "L$2", "L$3"})
    static final class C00911<E> extends ContinuationImpl {
        Object L$0;
        Object L$1;
        Object L$2;
        Object L$3;
        Object L$4;
        int label;
        /* synthetic */ Object result;

        C00911(Continuation<? super C00911> continuation) {
            super(continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return ChannelsKt__DeprecatedKt.lastIndexOf(null, null, this);
        }
    }

    /* JADX INFO: renamed from: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$lastOrNull$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: Deprecated.kt */
    @Metadata(k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt", f = "Deprecated.kt", i = {0, 0, 1, 1, 1}, l = {153, 156}, m = "lastOrNull", n = {"$this$consume$iv", "iterator", "$this$consume$iv", "iterator", "last"}, s = {"L$0", "L$1", "L$0", "L$1", "L$2"})
    static final class C00921<E> extends ContinuationImpl {
        Object L$0;
        Object L$1;
        Object L$2;
        int label;
        /* synthetic */ Object result;

        C00921(Continuation<? super C00921> continuation) {
            super(continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return ChannelsKt__DeprecatedKt.lastOrNull(null, this);
        }
    }

    /* JADX INFO: renamed from: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$maxWith$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: Deprecated.kt */
    @Metadata(k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt", f = "Deprecated.kt", i = {0, 0, 0, 1, 1, 1, 1}, l = {450, 452}, m = "maxWith", n = {"comparator", "$this$consume$iv", "iterator", "comparator", "$this$consume$iv", "iterator", "max"}, s = {"L$0", "L$1", "L$2", "L$0", "L$1", "L$2", "L$3"})
    static final class C00951<E> extends ContinuationImpl {
        Object L$0;
        Object L$1;
        Object L$2;
        Object L$3;
        int label;
        /* synthetic */ Object result;

        C00951(Continuation<? super C00951> continuation) {
            super(continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return ChannelsKt__DeprecatedKt.maxWith(null, null, this);
        }
    }

    /* JADX INFO: renamed from: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$minWith$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: Deprecated.kt */
    @Metadata(k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt", f = "Deprecated.kt", i = {0, 0, 0, 1, 1, 1, 1}, l = {464, 466}, m = "minWith", n = {"comparator", "$this$consume$iv", "iterator", "comparator", "$this$consume$iv", "iterator", "min"}, s = {"L$0", "L$1", "L$2", "L$0", "L$1", "L$2", "L$3"})
    static final class C00961<E> extends ContinuationImpl {
        Object L$0;
        Object L$1;
        Object L$2;
        Object L$3;
        int label;
        /* synthetic */ Object result;

        C00961(Continuation<? super C00961> continuation) {
            super(continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return ChannelsKt__DeprecatedKt.minWith(null, null, this);
        }
    }

    /* JADX INFO: renamed from: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$none$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: Deprecated.kt */
    @Metadata(k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt", f = "Deprecated.kt", i = {0}, l = {477}, m = PluginMethod.RETURN_NONE, n = {"$this$consume$iv"}, s = {"L$0"})
    static final class C00971<E> extends ContinuationImpl {
        Object L$0;
        int label;
        /* synthetic */ Object result;

        C00971(Continuation<? super C00971> continuation) {
            super(continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return ChannelsKt__DeprecatedKt.none(null, this);
        }
    }

    /* JADX INFO: renamed from: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$single$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: Deprecated.kt */
    @Metadata(k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt", f = "Deprecated.kt", i = {0, 0, 1, 1}, l = {166, 169}, m = "single", n = {"$this$consume$iv", "iterator", "$this$consume$iv", "single"}, s = {"L$0", "L$1", "L$0", "L$1"})
    static final class C00991<E> extends ContinuationImpl {
        Object L$0;
        Object L$1;
        int label;
        /* synthetic */ Object result;

        C00991(Continuation<? super C00991> continuation) {
            super(continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return ChannelsKt__DeprecatedKt.single(null, this);
        }
    }

    /* JADX INFO: renamed from: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$singleOrNull$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: Deprecated.kt */
    @Metadata(k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt", f = "Deprecated.kt", i = {0, 0, 1, 1}, l = {179, 182}, m = "singleOrNull", n = {"$this$consume$iv", "iterator", "$this$consume$iv", "single"}, s = {"L$0", "L$1", "L$0", "L$1"})
    static final class C01001<E> extends ContinuationImpl {
        Object L$0;
        Object L$1;
        int label;
        /* synthetic */ Object result;

        C01001(Continuation<? super C01001> continuation) {
            super(continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return ChannelsKt__DeprecatedKt.singleOrNull(null, this);
        }
    }

    /* JADX INFO: renamed from: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$toChannel$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: Deprecated.kt */
    @Metadata(k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt", f = "Deprecated.kt", i = {0, 0, 1, 1}, l = {517, StatusLine.HTTP_PERM_REDIRECT}, m = "toChannel", n = {"destination", "$this$consume$iv$iv", "destination", "$this$consume$iv$iv"}, s = {"L$0", "L$1", "L$0", "L$1"})
    static final class C01031<E, C extends SendChannel<? super E>> extends ContinuationImpl {
        Object L$0;
        Object L$1;
        Object L$2;
        int label;
        /* synthetic */ Object result;

        C01031(Continuation<? super C01031> continuation) {
            super(continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return ChannelsKt.toChannel(null, null, this);
        }
    }

    /* JADX INFO: renamed from: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$toCollection$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: Deprecated.kt */
    @Metadata(k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt", f = "Deprecated.kt", i = {0, 0}, l = {517}, m = "toCollection", n = {"destination", "$this$consume$iv$iv"}, s = {"L$0", "L$1"})
    static final class C01041<E, C extends Collection<? super E>> extends ContinuationImpl {
        Object L$0;
        Object L$1;
        Object L$2;
        int label;
        /* synthetic */ Object result;

        C01041(Continuation<? super C01041> continuation) {
            super(continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return ChannelsKt.toCollection(null, null, this);
        }
    }

    /* JADX INFO: renamed from: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$toMap$2, reason: invalid class name */
    /* JADX INFO: compiled from: Deprecated.kt */
    @Metadata(k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt", f = "Deprecated.kt", i = {0, 0}, l = {517}, m = "toMap", n = {"destination", "$this$consume$iv$iv"}, s = {"L$0", "L$1"})
    static final class AnonymousClass2<K, V, M extends Map<? super K, ? super V>> extends ContinuationImpl {
        Object L$0;
        Object L$1;
        Object L$2;
        int label;
        /* synthetic */ Object result;

        AnonymousClass2(Continuation<? super AnonymousClass2> continuation) {
            super(continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return ChannelsKt.toMap(null, null, this);
        }
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "BroadcastChannel is deprecated in the favour of SharedFlow and is no longer supported")
    public static final <E, R> R consume(BroadcastChannel<E> broadcastChannel, Function1<? super ReceiveChannel<? extends E>, ? extends R> function1) {
        ReceiveChannel<E> receiveChannelOpenSubscription = broadcastChannel.openSubscription();
        try {
            return function1.invoke(receiveChannelOpenSubscription);
        } finally {
            ReceiveChannel.DefaultImpls.cancel$default((ReceiveChannel) receiveChannelOpenSubscription, (CancellationException) null, 1, (Object) null);
        }
    }

    /* JADX WARN: Code duplicated, block: B:22:0x006f A[RETURN] */
    /* JADX WARN: Code duplicated, block: B:23:0x0070  */
    /* JADX WARN: Code duplicated, block: B:26:0x0081 A[Catch: all -> 0x009b, TryCatch #2 {all -> 0x009b, blocks: (B:24:0x0079, B:26:0x0081, B:27:0x0090), top: B:40:0x0079 }] */
    /* JADX WARN: Code duplicated, block: B:7:0x0014  */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:23:0x0070 -> B:40:0x0079). Please report as a decompilation issue!!! */
    /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxOverflowException: Regions stack size limit reached
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
        	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
        	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
        */
    @kotlin.Deprecated(level = kotlin.DeprecationLevel.ERROR, message = "BroadcastChannel is deprecated in the favour of SharedFlow and is no longer supported")
    public static final <E> java.lang.Object consumeEach(kotlinx.coroutines.channels.BroadcastChannel<E> r10, kotlin.jvm.functions.Function1<? super E, kotlin.Unit> r11, kotlin.coroutines.Continuation<? super kotlin.Unit> r12) {
        /*
            boolean r0 = r12 instanceof kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt.C00711
            if (r0 == 0) goto L14
            r0 = r12
            kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$consumeEach$1 r0 = (kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt.C00711) r0
            int r1 = r0.label
            r2 = -2147483648(0xffffffff80000000, float:-0.0)
            r1 = r1 & r2
            if (r1 == 0) goto L14
            int r12 = r0.label
            int r12 = r12 - r2
            r0.label = r12
            goto L19
        L14:
            kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$consumeEach$1 r0 = new kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$consumeEach$1
            r0.<init>(r12)
        L19:
            java.lang.Object r12 = r0.result
            java.lang.Object r1 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
            int r2 = r0.label
            r3 = 1
            r4 = 0
            switch(r2) {
                case 0: goto L4b;
                case 1: goto L2e;
                default: goto L26;
            }
        L26:
            java.lang.IllegalStateException r10 = new java.lang.IllegalStateException
            java.lang.String r11 = "call to 'resume' before 'invoke' with coroutine"
            r10.<init>(r11)
            throw r10
        L2e:
            r10 = 0
            r11 = 0
            r2 = 0
            java.lang.Object r5 = r0.L$2
            kotlinx.coroutines.channels.ChannelIterator r5 = (kotlinx.coroutines.channels.ChannelIterator) r5
            java.lang.Object r6 = r0.L$1
            kotlinx.coroutines.channels.ReceiveChannel r6 = (kotlinx.coroutines.channels.ReceiveChannel) r6
            java.lang.Object r7 = r0.L$0
            kotlin.jvm.functions.Function1 r7 = (kotlin.jvm.functions.Function1) r7
            kotlin.ResultKt.throwOnFailure(r12)     // Catch: java.lang.Throwable -> L48
            r8 = r7
            r7 = r6
            r6 = r5
            r5 = r2
            r2 = r1
            r1 = r0
            r0 = r12
            goto L79
        L48:
            r1 = move-exception
            goto La5
        L4b:
            kotlin.ResultKt.throwOnFailure(r12)
            r2 = 0
            r5 = 0
            kotlinx.coroutines.channels.ReceiveChannel r6 = r10.openSubscription()
            r10 = r6
            r7 = 0
            kotlinx.coroutines.channels.ChannelIterator r8 = r10.iterator()     // Catch: java.lang.Throwable -> La2
            r10 = r2
            r2 = r7
            r7 = r11
            r11 = r5
            r5 = r8
        L61:
            r0.L$0 = r7     // Catch: java.lang.Throwable -> L48
            r0.L$1 = r6     // Catch: java.lang.Throwable -> L48
            r0.L$2 = r5     // Catch: java.lang.Throwable -> L48
            r0.label = r3     // Catch: java.lang.Throwable -> L48
            java.lang.Object r8 = r5.hasNext(r0)     // Catch: java.lang.Throwable -> L48
            if (r8 != r1) goto L70
            return r1
        L70:
            r9 = r0
            r0 = r12
            r12 = r8
            r8 = r7
            r7 = r6
            r6 = r5
            r5 = r2
            r2 = r1
            r1 = r9
        L79:
            java.lang.Boolean r12 = (java.lang.Boolean) r12     // Catch: java.lang.Throwable -> L9b
            boolean r12 = r12.booleanValue()     // Catch: java.lang.Throwable -> L9b
            if (r12 == 0) goto L90
            java.lang.Object r12 = r6.next()     // Catch: java.lang.Throwable -> L9b
            r8.invoke(r12)     // Catch: java.lang.Throwable -> L9b
            r12 = r0
            r0 = r1
            r1 = r2
            r2 = r5
            r5 = r6
            r6 = r7
            r7 = r8
            goto L61
        L90:
            kotlin.Unit r12 = kotlin.Unit.INSTANCE     // Catch: java.lang.Throwable -> L9b
            kotlinx.coroutines.channels.ReceiveChannel.DefaultImpls.cancel$default(r7, r4, r3, r4)
            kotlin.Unit r11 = kotlin.Unit.INSTANCE
            return r11
        L9b:
            r12 = move-exception
            r6 = r1
            r1 = r12
            r12 = r0
            r0 = r6
            r6 = r7
            goto La5
        La2:
            r1 = move-exception
            r10 = r2
            r11 = r5
        La5:
            kotlinx.coroutines.channels.ReceiveChannel.DefaultImpls.cancel$default(r6, r4, r3, r4)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt.consumeEach(kotlinx.coroutines.channels.BroadcastChannel, kotlin.jvm.functions.Function1, kotlin.coroutines.Continuation):java.lang.Object");
    }

    @Deprecated(level = DeprecationLevel.ERROR, message = "BroadcastChannel is deprecated in the favour of SharedFlow and is no longer supported")
    private static final <E> Object consumeEach$$forInline(BroadcastChannel<E> broadcastChannel, Function1<? super E, Unit> function1, Continuation<? super Unit> continuation) {
        ReceiveChannel<E> receiveChannelOpenSubscription = broadcastChannel.openSubscription();
        try {
            ChannelIterator<E> it = receiveChannelOpenSubscription.iterator();
            while (((Boolean) it.hasNext(null)).booleanValue()) {
                Object element = it.next();
                function1.invoke(element);
            }
            Unit unit = Unit.INSTANCE;
            return Unit.INSTANCE;
        } finally {
            ReceiveChannel.DefaultImpls.cancel$default((ReceiveChannel) receiveChannelOpenSubscription, (CancellationException) null, 1, (Object) null);
        }
    }

    public static final Function1<Throwable, Unit> consumesAll(final ReceiveChannel<?>... receiveChannelArr) {
        return new Function1<Throwable, Unit>() { // from class: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt.consumesAll.1
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(1);
            }

            @Override // kotlin.jvm.functions.Function1
            public /* bridge */ /* synthetic */ Unit invoke(Throwable th) throws Throwable {
                invoke2(th);
                return Unit.INSTANCE;
            }

            /* JADX INFO: renamed from: invoke, reason: avoid collision after fix types in other method */
            public final void invoke2(Throwable cause) throws Throwable {
                Throwable exception = null;
                for (ReceiveChannel<?> receiveChannel : receiveChannelArr) {
                    try {
                        ChannelsKt.cancelConsumed(receiveChannel, cause);
                    } catch (Throwable e) {
                        if (exception == null) {
                            exception = e;
                        } else {
                            ExceptionsKt.addSuppressed(exception, e);
                        }
                    }
                }
                if (exception != null) {
                    Throwable it = exception;
                    throw it;
                }
            }
        };
    }

    /* JADX WARN: Code duplicated, block: B:23:0x0075 A[RETURN] */
    /* JADX WARN: Code duplicated, block: B:24:0x0076  */
    /* JADX WARN: Code duplicated, block: B:27:0x0088 A[Catch: all -> 0x00bf, TRY_LEAVE, TryCatch #4 {all -> 0x00bf, blocks: (B:25:0x0080, B:27:0x0088), top: B:56:0x0080 }] */
    /* JADX WARN: Code duplicated, block: B:30:0x0091  */
    /* JADX WARN: Code duplicated, block: B:32:0x0095  */
    /* JADX WARN: Code duplicated, block: B:7:0x0014  */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:24:0x0076 -> B:56:0x0080). Please report as a decompilation issue!!! */
    /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxOverflowException: Regions stack size limit reached
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
        	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
        	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
        */
    @kotlin.Deprecated(level = kotlin.DeprecationLevel.HIDDEN, message = "Binary compatibility")
    public static final /* synthetic */ java.lang.Object elementAt(kotlinx.coroutines.channels.ReceiveChannel r12, int r13, kotlin.coroutines.Continuation r14) {
        /*
            Method dump skipped, instruction units count: 242
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt.elementAt(kotlinx.coroutines.channels.ReceiveChannel, int, kotlin.coroutines.Continuation):java.lang.Object");
    }

    /* JADX WARN: Code duplicated, block: B:26:0x0078 A[RETURN] */
    /* JADX WARN: Code duplicated, block: B:27:0x0079  */
    /* JADX WARN: Code duplicated, block: B:30:0x008a A[Catch: all -> 0x00a4, TRY_LEAVE, TryCatch #1 {all -> 0x00a4, blocks: (B:28:0x0082, B:30:0x008a), top: B:51:0x0082 }] */
    /* JADX WARN: Code duplicated, block: B:33:0x0093  */
    /* JADX WARN: Code duplicated, block: B:35:0x0097  */
    /* JADX WARN: Code duplicated, block: B:7:0x0014  */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:27:0x0079 -> B:51:0x0082). Please report as a decompilation issue!!! */
    /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxOverflowException: Regions stack size limit reached
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
        	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
        	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
        */
    @kotlin.Deprecated(level = kotlin.DeprecationLevel.HIDDEN, message = "Binary compatibility")
    public static final /* synthetic */ java.lang.Object elementAtOrNull(kotlinx.coroutines.channels.ReceiveChannel r11, int r12, kotlin.coroutines.Continuation r13) {
        /*
            boolean r0 = r13 instanceof kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt.C00801
            if (r0 == 0) goto L14
            r0 = r13
            kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$elementAtOrNull$1 r0 = (kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt.C00801) r0
            int r1 = r0.label
            r2 = -2147483648(0xffffffff80000000, float:-0.0)
            r1 = r1 & r2
            if (r1 == 0) goto L14
            int r13 = r0.label
            int r13 = r13 - r2
            r0.label = r13
            goto L19
        L14:
            kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$elementAtOrNull$1 r0 = new kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$elementAtOrNull$1
            r0.<init>(r13)
        L19:
            java.lang.Object r13 = r0.result
            java.lang.Object r1 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
            int r2 = r0.label
            r3 = 0
            switch(r2) {
                case 0: goto L4a;
                case 1: goto L2d;
                default: goto L25;
            }
        L25:
            java.lang.IllegalStateException r11 = new java.lang.IllegalStateException
            java.lang.String r12 = "call to 'resume' before 'invoke' with coroutine"
            r11.<init>(r12)
            throw r11
        L2d:
            r11 = 0
            r12 = 0
            int r2 = r0.I$1
            int r4 = r0.I$0
            java.lang.Object r5 = r0.L$1
            kotlinx.coroutines.channels.ChannelIterator r5 = (kotlinx.coroutines.channels.ChannelIterator) r5
            java.lang.Object r6 = r0.L$0
            kotlinx.coroutines.channels.ReceiveChannel r6 = (kotlinx.coroutines.channels.ReceiveChannel) r6
            kotlin.ResultKt.throwOnFailure(r13)     // Catch: java.lang.Throwable -> L47
            r8 = r5
            r7 = r6
            r5 = r3
            r6 = r4
            r4 = r2
            r2 = r1
            r1 = r0
            r0 = r13
            goto L82
        L47:
            r11 = move-exception
            goto Lb1
        L4a:
            kotlin.ResultKt.throwOnFailure(r13)
            r2 = 0
            r4 = r11
            r5 = 0
            if (r12 >= 0) goto L5b
        L57:
            kotlinx.coroutines.channels.ChannelsKt.cancelConsumed(r11, r3)
            return r3
        L5b:
            r6 = r11
            r11 = r3
            r7 = 0
            kotlinx.coroutines.channels.ChannelIterator r8 = r4.iterator()     // Catch: java.lang.Throwable -> Lad
            r4 = r11
            r11 = r5
            r5 = r12
            r12 = r2
            r2 = r7
        L67:
            r0.L$0 = r6     // Catch: java.lang.Throwable -> Laa
            r0.L$1 = r8     // Catch: java.lang.Throwable -> Laa
            r0.I$0 = r5     // Catch: java.lang.Throwable -> Laa
            r0.I$1 = r2     // Catch: java.lang.Throwable -> Laa
            r7 = 1
            r0.label = r7     // Catch: java.lang.Throwable -> Laa
            java.lang.Object r7 = r8.hasNext(r0)     // Catch: java.lang.Throwable -> Laa
            if (r7 != r1) goto L79
            return r1
        L79:
            r10 = r0
            r0 = r13
            r13 = r7
            r7 = r6
            r6 = r5
            r5 = r4
            r4 = r2
            r2 = r1
            r1 = r10
        L82:
            java.lang.Boolean r13 = (java.lang.Boolean) r13     // Catch: java.lang.Throwable -> La4
            boolean r13 = r13.booleanValue()     // Catch: java.lang.Throwable -> La4
            if (r13 == 0) goto L9f
            java.lang.Object r13 = r8.next()     // Catch: java.lang.Throwable -> La4
            int r9 = r4 + 1
            if (r6 != r4) goto L97
        L93:
            kotlinx.coroutines.channels.ChannelsKt.cancelConsumed(r7, r5)
            return r13
        L97:
            r4 = r5
            r13 = r0
            r0 = r1
            r1 = r2
            r5 = r6
            r6 = r7
            r2 = r9
            goto L67
        L9f:
            kotlinx.coroutines.channels.ChannelsKt.cancelConsumed(r7, r5)
            return r3
        La4:
            r11 = move-exception
            r6 = r7
            r3 = r5
            r13 = r0
            r0 = r1
            goto Lb1
        Laa:
            r11 = move-exception
            r3 = r4
            goto Lb1
        Lad:
            r12 = move-exception
            r3 = r11
            r11 = r12
            r12 = r2
        Lb1:
            r1 = r11
            throw r11     // Catch: java.lang.Throwable -> Lb4
        Lb4:
            r11 = move-exception
            kotlinx.coroutines.channels.ChannelsKt.cancelConsumed(r6, r1)
            throw r11
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt.elementAtOrNull(kotlinx.coroutines.channels.ReceiveChannel, int, kotlin.coroutines.Continuation):java.lang.Object");
    }

    /* JADX WARN: Code duplicated, block: B:24:0x0064 A[Catch: all -> 0x003c, TRY_LEAVE, TryCatch #2 {all -> 0x003c, blocks: (B:13:0x0037, B:22:0x005c, B:24:0x0064, B:27:0x006c, B:28:0x0073), top: B:39:0x0037 }] */
    /* JADX WARN: Code duplicated, block: B:27:0x006c A[Catch: all -> 0x003c, TRY_ENTER, TryCatch #2 {all -> 0x003c, blocks: (B:13:0x0037, B:22:0x005c, B:24:0x0064, B:27:0x006c, B:28:0x0073), top: B:39:0x0037 }] */
    /* JADX WARN: Code duplicated, block: B:7:0x0014  */
    @Deprecated(level = DeprecationLevel.HIDDEN, message = "Binary compatibility")
    public static final /* synthetic */ Object first(ReceiveChannel $this$first, Continuation continuation) throws Throwable {
        C00861 c00861;
        ReceiveChannel $this$consume$iv;
        Throwable cause$iv;
        ChannelIterator iterator;
        Object objHasNext;
        if (continuation instanceof C00861) {
            c00861 = (C00861) continuation;
            if ((c00861.label & Integer.MIN_VALUE) != 0) {
                c00861.label -= Integer.MIN_VALUE;
            } else {
                c00861 = new C00861(continuation);
            }
        } else {
            c00861 = new C00861(continuation);
        }
        Object $result = c00861.result;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        switch (c00861.label) {
            case 0:
                ResultKt.throwOnFailure($result);
                $this$consume$iv = $this$first;
                cause$iv = null;
                try {
                    iterator = $this$consume$iv.iterator();
                    c00861.L$0 = $this$consume$iv;
                    c00861.L$1 = iterator;
                    c00861.label = 1;
                    objHasNext = iterator.hasNext(c00861);
                    if (objHasNext == coroutine_suspended) {
                        return coroutine_suspended;
                    }
                    if (((Boolean) objHasNext).booleanValue()) {
                        throw new NoSuchElementException("ReceiveChannel is empty.");
                    }
                    Object next = iterator.next();
                    ChannelsKt.cancelConsumed($this$consume$iv, cause$iv);
                    return next;
                } catch (Throwable th) {
                    e$iv = th;
                    Throwable cause$iv2 = e$iv;
                    try {
                        throw e$iv;
                    } catch (Throwable e$iv) {
                        ChannelsKt.cancelConsumed($this$consume$iv, cause$iv2);
                        throw e$iv;
                    }
                }
            case 1:
                iterator = (ChannelIterator) c00861.L$1;
                $this$consume$iv = (ReceiveChannel) c00861.L$0;
                cause$iv = null;
                try {
                    ResultKt.throwOnFailure($result);
                    objHasNext = $result;
                    if (((Boolean) objHasNext).booleanValue()) {
                        throw new NoSuchElementException("ReceiveChannel is empty.");
                    }
                    Object next2 = iterator.next();
                    ChannelsKt.cancelConsumed($this$consume$iv, cause$iv);
                    return next2;
                } catch (Throwable th2) {
                    e$iv = th2;
                    Throwable cause$iv3 = e$iv;
                    throw e$iv;
                }
            default:
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
    }

    /* JADX WARN: Code duplicated, block: B:26:0x006a A[Catch: all -> 0x006f, TRY_ENTER, TRY_LEAVE, TryCatch #3 {all -> 0x006f, blocks: (B:22:0x005d, B:26:0x006a), top: B:43:0x005d }] */
    /* JADX WARN: Code duplicated, block: B:7:0x0014  */
    @Deprecated(level = DeprecationLevel.HIDDEN, message = "Binary compatibility")
    public static final /* synthetic */ Object firstOrNull(ReceiveChannel $this$firstOrNull, Continuation continuation) throws Throwable {
        C00871 c00871;
        ReceiveChannel $this$consume$iv;
        Throwable cause$iv;
        ChannelIterator iterator;
        Object objHasNext;
        if (continuation instanceof C00871) {
            c00871 = (C00871) continuation;
            if ((c00871.label & Integer.MIN_VALUE) != 0) {
                c00871.label -= Integer.MIN_VALUE;
            } else {
                c00871 = new C00871(continuation);
            }
        } else {
            c00871 = new C00871(continuation);
        }
        Object $result = c00871.result;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        Object next = null;
        switch (c00871.label) {
            case 0:
                ResultKt.throwOnFailure($result);
                $this$consume$iv = $this$firstOrNull;
                cause$iv = null;
                try {
                    iterator = $this$consume$iv.iterator();
                    c00871.L$0 = $this$consume$iv;
                    c00871.L$1 = iterator;
                    c00871.label = 1;
                    objHasNext = iterator.hasNext(c00871);
                    if (objHasNext == coroutine_suspended) {
                        return coroutine_suspended;
                    }
                    try {
                        if (((Boolean) objHasNext).booleanValue()) {
                            next = iterator.next();
                            break;
                        }
                        ChannelsKt.cancelConsumed($this$consume$iv, cause$iv);
                        return next;
                    } catch (Throwable th) {
                        e$iv = th;
                        Throwable cause$iv2 = e$iv;
                        try {
                            throw e$iv;
                        } catch (Throwable e$iv) {
                            ChannelsKt.cancelConsumed($this$consume$iv, cause$iv2);
                            throw e$iv;
                        }
                    }
                } catch (Throwable th2) {
                    e$iv = th2;
                    Throwable cause$iv3 = e$iv;
                    throw e$iv;
                }
            case 1:
                ChannelIterator iterator2 = (ChannelIterator) c00871.L$1;
                $this$consume$iv = (ReceiveChannel) c00871.L$0;
                try {
                    ResultKt.throwOnFailure($result);
                    objHasNext = $result;
                    iterator = iterator2;
                    cause$iv = null;
                    if (((Boolean) objHasNext).booleanValue()) {
                        next = iterator.next();
                        break;
                    }
                    ChannelsKt.cancelConsumed($this$consume$iv, cause$iv);
                    return next;
                } catch (Throwable th3) {
                    e$iv = th3;
                    Throwable cause$iv4 = e$iv;
                    throw e$iv;
                }
            default:
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
    }

    /* JADX WARN: Code duplicated, block: B:22:0x0080 A[RETURN] */
    /* JADX WARN: Code duplicated, block: B:23:0x0081  */
    /* JADX WARN: Code duplicated, block: B:26:0x0096 A[Catch: all -> 0x00d9, TryCatch #4 {all -> 0x00d9, blocks: (B:24:0x008e, B:26:0x0096, B:28:0x00a2), top: B:60:0x008e }] */
    /* JADX WARN: Code duplicated, block: B:28:0x00a2 A[Catch: all -> 0x00d9, TRY_LEAVE, TryCatch #4 {all -> 0x00d9, blocks: (B:24:0x008e, B:26:0x0096, B:28:0x00a2), top: B:60:0x008e }] */
    /* JADX WARN: Code duplicated, block: B:62:0x00ac A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:7:0x0016  */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:23:0x0081 -> B:60:0x008e). Please report as a decompilation issue!!! */
    /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxOverflowException: Regions stack size limit reached
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
        	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
        	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
        */
    @kotlin.Deprecated(level = kotlin.DeprecationLevel.HIDDEN, message = "Binary compatibility")
    public static final /* synthetic */ java.lang.Object indexOf(kotlinx.coroutines.channels.ReceiveChannel r17, java.lang.Object r18, kotlin.coroutines.Continuation r19) {
        /*
            Method dump skipped, instruction units count: 246
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt.indexOf(kotlinx.coroutines.channels.ReceiveChannel, java.lang.Object, kotlin.coroutines.Continuation):java.lang.Object");
    }

    /* JADX WARN: Code duplicated, block: B:29:0x0096 A[RETURN] */
    /* JADX WARN: Code duplicated, block: B:30:0x0097  */
    /* JADX WARN: Code duplicated, block: B:33:0x00a9 A[Catch: all -> 0x00bc, TRY_LEAVE, TryCatch #3 {all -> 0x00bc, blocks: (B:31:0x00a1, B:33:0x00a9), top: B:52:0x00a1 }] */
    /* JADX WARN: Code duplicated, block: B:7:0x0014  */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:30:0x0097 -> B:52:0x00a1). Please report as a decompilation issue!!! */
    /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxOverflowException: Regions stack size limit reached
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
        	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
        	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
        */
    @kotlin.Deprecated(level = kotlin.DeprecationLevel.HIDDEN, message = "Binary compatibility")
    public static final /* synthetic */ java.lang.Object last(kotlinx.coroutines.channels.ReceiveChannel r9, kotlin.coroutines.Continuation r10) {
        /*
            Method dump skipped, instruction units count: 224
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt.last(kotlinx.coroutines.channels.ReceiveChannel, kotlin.coroutines.Continuation):java.lang.Object");
    }

    /* JADX WARN: Code duplicated, block: B:22:0x0091 A[RETURN] */
    /* JADX WARN: Code duplicated, block: B:23:0x0092  */
    /* JADX WARN: Code duplicated, block: B:26:0x00a8 A[Catch: all -> 0x00d8, TryCatch #2 {all -> 0x00d8, blocks: (B:24:0x00a0, B:26:0x00a8, B:28:0x00b3, B:29:0x00b7, B:30:0x00c8), top: B:46:0x00a0 }] */
    /* JADX WARN: Code duplicated, block: B:28:0x00b3 A[Catch: all -> 0x00d8, TryCatch #2 {all -> 0x00d8, blocks: (B:24:0x00a0, B:26:0x00a8, B:28:0x00b3, B:29:0x00b7, B:30:0x00c8), top: B:46:0x00a0 }] */
    /* JADX WARN: Code duplicated, block: B:7:0x0016  */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:23:0x0092 -> B:46:0x00a0). Please report as a decompilation issue!!! */
    /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxOverflowException: Regions stack size limit reached
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
        	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
        	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
        */
    @kotlin.Deprecated(level = kotlin.DeprecationLevel.HIDDEN, message = "Binary compatibility")
    public static final /* synthetic */ java.lang.Object lastIndexOf(kotlinx.coroutines.channels.ReceiveChannel r17, java.lang.Object r18, kotlin.coroutines.Continuation r19) {
        /*
            Method dump skipped, instruction units count: 244
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt.lastIndexOf(kotlinx.coroutines.channels.ReceiveChannel, java.lang.Object, kotlin.coroutines.Continuation):java.lang.Object");
    }

    /* JADX WARN: Code duplicated, block: B:35:0x00a0 A[RETURN] */
    /* JADX WARN: Code duplicated, block: B:36:0x00a1  */
    /* JADX WARN: Code duplicated, block: B:39:0x00b3 A[Catch: all -> 0x00c6, TRY_LEAVE, TryCatch #1 {all -> 0x00c6, blocks: (B:37:0x00ab, B:39:0x00b3), top: B:58:0x00ab }] */
    /* JADX WARN: Code duplicated, block: B:7:0x0014  */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:36:0x00a1 -> B:58:0x00ab). Please report as a decompilation issue!!! */
    /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxOverflowException: Regions stack size limit reached
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
        	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
        	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
        */
    @kotlin.Deprecated(level = kotlin.DeprecationLevel.HIDDEN, message = "Binary compatibility")
    public static final /* synthetic */ java.lang.Object lastOrNull(kotlinx.coroutines.channels.ReceiveChannel r9, kotlin.coroutines.Continuation r10) {
        /*
            Method dump skipped, instruction units count: 232
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt.lastOrNull(kotlinx.coroutines.channels.ReceiveChannel, kotlin.coroutines.Continuation):java.lang.Object");
    }

    /* JADX WARN: Code duplicated, block: B:29:0x0079 A[Catch: all -> 0x004e, TRY_LEAVE, TryCatch #2 {all -> 0x004e, blocks: (B:18:0x0048, B:27:0x0071, B:29:0x0079, B:39:0x00a3, B:40:0x00aa), top: B:51:0x0048 }] */
    /* JADX WARN: Code duplicated, block: B:31:0x008a A[RETURN] */
    /* JADX WARN: Code duplicated, block: B:32:0x008b  */
    /* JADX WARN: Code duplicated, block: B:35:0x0097  */
    /* JADX WARN: Code duplicated, block: B:37:0x009b A[Catch: all -> 0x003a, TRY_ENTER, TryCatch #0 {all -> 0x003a, blocks: (B:13:0x0035, B:33:0x008e, B:37:0x009b, B:38:0x00a2), top: B:47:0x0035 }] */
    /* JADX WARN: Code duplicated, block: B:39:0x00a3 A[Catch: all -> 0x004e, TRY_ENTER, TryCatch #2 {all -> 0x004e, blocks: (B:18:0x0048, B:27:0x0071, B:29:0x0079, B:39:0x00a3, B:40:0x00aa), top: B:51:0x0048 }] */
    /* JADX WARN: Code duplicated, block: B:7:0x0014  */
    @Deprecated(level = DeprecationLevel.HIDDEN, message = "Binary compatibility")
    public static final /* synthetic */ Object single(ReceiveChannel $this$single, Continuation continuation) throws Throwable {
        C00991 c00991;
        ReceiveChannel $this$consume$iv;
        Throwable cause$iv;
        ChannelIterator iterator;
        Object obj;
        int i;
        ReceiveChannel $this$consume$iv2;
        Object single;
        Object objHasNext;
        Object obj2;
        if (continuation instanceof C00991) {
            c00991 = (C00991) continuation;
            if ((c00991.label & Integer.MIN_VALUE) != 0) {
                c00991.label -= Integer.MIN_VALUE;
            } else {
                c00991 = new C00991(continuation);
            }
        } else {
            c00991 = new C00991(continuation);
        }
        Object $result = c00991.result;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        switch (c00991.label) {
            case 0:
                ResultKt.throwOnFailure($result);
                $this$consume$iv = $this$single;
                cause$iv = null;
                try {
                    iterator = $this$consume$iv.iterator();
                    c00991.L$0 = $this$consume$iv;
                    c00991.L$1 = iterator;
                    c00991.label = 1;
                    Object objHasNext2 = iterator.hasNext(c00991);
                    if (objHasNext2 == coroutine_suspended) {
                        return coroutine_suspended;
                    }
                    obj = objHasNext2;
                    i = 0;
                    $this$consume$iv2 = $this$consume$iv;
                    if (((Boolean) obj).booleanValue()) {
                        throw new NoSuchElementException("ReceiveChannel is empty.");
                    }
                    single = iterator.next();
                    c00991.L$0 = $this$consume$iv2;
                    c00991.L$1 = single;
                    c00991.label = 2;
                    objHasNext = iterator.hasNext(c00991);
                    if (objHasNext == coroutine_suspended) {
                        return coroutine_suspended;
                    }
                    obj2 = single;
                    $this$consume$iv = $this$consume$iv2;
                    if (((Boolean) objHasNext).booleanValue()) {
                        throw new IllegalArgumentException("ReceiveChannel has more than one element.");
                    }
                    ChannelsKt.cancelConsumed($this$consume$iv, cause$iv);
                    return obj2;
                } catch (Throwable th) {
                    e$iv = th;
                    Throwable cause$iv2 = e$iv;
                    try {
                        throw e$iv;
                    } catch (Throwable e$iv) {
                        ChannelsKt.cancelConsumed($this$consume$iv, cause$iv2);
                        throw e$iv;
                    }
                }
            case 1:
                i = 0;
                ChannelIterator iterator2 = (ChannelIterator) c00991.L$1;
                cause$iv = null;
                $this$consume$iv2 = (ReceiveChannel) c00991.L$0;
                try {
                    ResultKt.throwOnFailure($result);
                    iterator = iterator2;
                    obj = $result;
                    if (((Boolean) obj).booleanValue()) {
                        throw new NoSuchElementException("ReceiveChannel is empty.");
                    }
                    single = iterator.next();
                    c00991.L$0 = $this$consume$iv2;
                    c00991.L$1 = single;
                    c00991.label = 2;
                    objHasNext = iterator.hasNext(c00991);
                    if (objHasNext == coroutine_suspended) {
                        return coroutine_suspended;
                    }
                    obj2 = single;
                    $this$consume$iv = $this$consume$iv2;
                    if (((Boolean) objHasNext).booleanValue()) {
                        throw new IllegalArgumentException("ReceiveChannel has more than one element.");
                    }
                    ChannelsKt.cancelConsumed($this$consume$iv, cause$iv);
                    return obj2;
                } catch (Throwable th2) {
                    e$iv = th2;
                    $this$consume$iv = $this$consume$iv2;
                    Throwable cause$iv3 = e$iv;
                    throw e$iv;
                }
            case 2:
                obj2 = c00991.L$1;
                $this$consume$iv = (ReceiveChannel) c00991.L$0;
                cause$iv = null;
                try {
                    ResultKt.throwOnFailure($result);
                    objHasNext = $result;
                    if (((Boolean) objHasNext).booleanValue()) {
                        throw new IllegalArgumentException("ReceiveChannel has more than one element.");
                    }
                    ChannelsKt.cancelConsumed($this$consume$iv, cause$iv);
                    return obj2;
                } catch (Throwable th3) {
                    e$iv = th3;
                    Throwable cause$iv4 = e$iv;
                    throw e$iv;
                }
            default:
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
    }

    /* JADX WARN: Code duplicated, block: B:29:0x0085  */
    /* JADX WARN: Code duplicated, block: B:33:0x009a A[RETURN] */
    /* JADX WARN: Code duplicated, block: B:34:0x009b  */
    /* JADX WARN: Code duplicated, block: B:37:0x00a8  */
    /* JADX WARN: Code duplicated, block: B:39:0x00ac  */
    /* JADX WARN: Code duplicated, block: B:56:0x0089 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:7:0x0014  */
    @Deprecated(level = DeprecationLevel.HIDDEN, message = "Binary compatibility")
    public static final /* synthetic */ Object singleOrNull(ReceiveChannel $this$singleOrNull, Continuation continuation) throws Throwable {
        C01001 c01001;
        ReceiveChannel $this$consume$iv;
        Throwable e$iv;
        ChannelIterator iterator;
        Object objHasNext;
        Throwable cause$iv;
        ReceiveChannel $this$consume$iv2;
        Throwable cause$iv2;
        Object single;
        Object objHasNext2;
        Object obj;
        Throwable th;
        if (continuation instanceof C01001) {
            c01001 = (C01001) continuation;
            if ((c01001.label & Integer.MIN_VALUE) != 0) {
                c01001.label -= Integer.MIN_VALUE;
            } else {
                c01001 = new C01001(continuation);
            }
        } else {
            c01001 = new C01001(continuation);
        }
        Object $result = c01001.result;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        switch (c01001.label) {
            case 0:
                ResultKt.throwOnFailure($result);
                $this$consume$iv = $this$singleOrNull;
                try {
                    iterator = $this$consume$iv.iterator();
                    c01001.L$0 = $this$consume$iv;
                    c01001.L$1 = iterator;
                    c01001.label = 1;
                    objHasNext = iterator.hasNext(c01001);
                    if (objHasNext == coroutine_suspended) {
                        return coroutine_suspended;
                    }
                    cause$iv = null;
                    $this$consume$iv2 = $this$consume$iv;
                    cause$iv2 = null;
                    try {
                        if (!((Boolean) objHasNext).booleanValue()) {
                            ChannelsKt.cancelConsumed($this$consume$iv2, cause$iv2);
                            return null;
                        }
                        try {
                            single = iterator.next();
                            c01001.L$0 = $this$consume$iv2;
                            c01001.L$1 = single;
                            c01001.label = 2;
                            objHasNext2 = iterator.hasNext(c01001);
                            if (objHasNext2 == coroutine_suspended) {
                                return coroutine_suspended;
                            }
                            obj = single;
                            th = cause$iv2;
                            $this$consume$iv = $this$consume$iv2;
                            try {
                                if (((Boolean) objHasNext2).booleanValue()) {
                                    ChannelsKt.cancelConsumed($this$consume$iv, th);
                                    return null;
                                }
                                ChannelsKt.cancelConsumed($this$consume$iv, th);
                                return obj;
                            } catch (Throwable th2) {
                                e$iv = th2;
                                Throwable cause$iv3 = e$iv;
                                try {
                                    throw e$iv;
                                } catch (Throwable e$iv2) {
                                    ChannelsKt.cancelConsumed($this$consume$iv, cause$iv3);
                                    throw e$iv2;
                                }
                            }
                        } catch (Throwable th3) {
                            e$iv = th3;
                            $this$consume$iv = $this$consume$iv2;
                            Throwable cause$iv4 = e$iv;
                            throw e$iv;
                        }
                    } catch (Throwable th4) {
                        e$iv = th4;
                        $this$consume$iv = $this$consume$iv2;
                    }
                } catch (Throwable th5) {
                    e$iv = th5;
                    Throwable cause$iv5 = e$iv;
                    throw e$iv;
                }
                break;
            case 1:
                ChannelIterator iterator2 = (ChannelIterator) c01001.L$1;
                ReceiveChannel receiveChannel = (ReceiveChannel) c01001.L$0;
                try {
                    ResultKt.throwOnFailure($result);
                    cause$iv = null;
                    iterator = iterator2;
                    $this$consume$iv2 = receiveChannel;
                    objHasNext = $result;
                    cause$iv2 = null;
                    if (!((Boolean) objHasNext).booleanValue()) {
                        ChannelsKt.cancelConsumed($this$consume$iv2, cause$iv2);
                        return null;
                    }
                    single = iterator.next();
                    c01001.L$0 = $this$consume$iv2;
                    c01001.L$1 = single;
                    c01001.label = 2;
                    objHasNext2 = iterator.hasNext(c01001);
                    if (objHasNext2 == coroutine_suspended) {
                        return coroutine_suspended;
                    }
                    obj = single;
                    th = cause$iv2;
                    $this$consume$iv = $this$consume$iv2;
                    if (((Boolean) objHasNext2).booleanValue()) {
                        ChannelsKt.cancelConsumed($this$consume$iv, th);
                        return null;
                    }
                    ChannelsKt.cancelConsumed($this$consume$iv, th);
                    return obj;
                } catch (Throwable th6) {
                    $this$consume$iv = receiveChannel;
                    e$iv = th6;
                    Throwable cause$iv6 = e$iv;
                    throw e$iv;
                }
            case 2:
                obj = c01001.L$1;
                $this$consume$iv = (ReceiveChannel) c01001.L$0;
                try {
                    ResultKt.throwOnFailure($result);
                    objHasNext2 = $result;
                    th = null;
                    if (((Boolean) objHasNext2).booleanValue()) {
                        ChannelsKt.cancelConsumed($this$consume$iv, th);
                        return null;
                    }
                    ChannelsKt.cancelConsumed($this$consume$iv, th);
                    return obj;
                } catch (Throwable th7) {
                    e$iv = th7;
                    Throwable cause$iv7 = e$iv;
                    throw e$iv;
                }
            default:
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
    }

    /* JADX INFO: Add missing generic type declarations: [E] */
    /* JADX INFO: renamed from: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$drop$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: Deprecated.kt */
    @Metadata(d1 = {"\u0000\f\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u0003H\u008a@"}, d2 = {"<anonymous>", "", "E", "Lkotlinx/coroutines/channels/ProducerScope;"}, k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$drop$1", f = "Deprecated.kt", i = {0, 0, 1, 2}, l = {194, 199, 200}, m = "invokeSuspend", n = {"$this$produce", "remaining", "$this$produce", "$this$produce"}, s = {"L$0", "I$0", "L$0", "L$0"})
    static final class C00771<E> extends SuspendLambda implements Function2<ProducerScope<? super E>, Continuation<? super Unit>, Object> {
        final /* synthetic */ int $n;
        final /* synthetic */ ReceiveChannel<E> $this_drop;
        int I$0;
        private /* synthetic */ Object L$0;
        Object L$1;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        /* JADX WARN: Multi-variable type inference failed */
        C00771(int i, ReceiveChannel<? extends E> receiveChannel, Continuation<? super C00771> continuation) {
            super(2, continuation);
            this.$n = i;
            this.$this_drop = receiveChannel;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            C00771 c00771 = new C00771(this.$n, this.$this_drop, continuation);
            c00771.L$0 = obj;
            return c00771;
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(ProducerScope<? super E> producerScope, Continuation<? super Unit> continuation) {
            return ((C00771) create(producerScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        /* JADX WARN: Code duplicated, block: B:20:0x007a A[RETURN] */
        /* JADX WARN: Code duplicated, block: B:21:0x007b  */
        /* JADX WARN: Code duplicated, block: B:24:0x008b  */
        /* JADX WARN: Code duplicated, block: B:27:0x0093  */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:21:0x007b -> B:22:0x0083). Please report as a decompilation issue!!! */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:39:0x00da -> B:30:0x00a5). Please report as a decompilation issue!!! */
        /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
            jadx.core.utils.exceptions.JadxOverflowException: Regions stack size limit reached
            	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
            	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
            	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
            */
        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final java.lang.Object invokeSuspend(java.lang.Object r9) {
            /*
                Method dump skipped, instruction units count: 276
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt.C00771.invokeSuspend(java.lang.Object):java.lang.Object");
        }
    }

    public static /* synthetic */ ReceiveChannel drop$default(ReceiveChannel receiveChannel, int i, CoroutineContext coroutineContext, int i2, Object obj) {
        if ((i2 & 2) != 0) {
            coroutineContext = Dispatchers.getUnconfined();
        }
        return drop(receiveChannel, i, coroutineContext);
    }

    @Deprecated(level = DeprecationLevel.HIDDEN, message = "Binary compatibility")
    public static final /* synthetic */ ReceiveChannel drop(ReceiveChannel $this$drop, int n, CoroutineContext context) {
        return ProduceKt.produce$default(GlobalScope.INSTANCE, context, 0, null, ChannelsKt.consumes($this$drop), new C00771(n, $this$drop, null), 6, null);
    }

    public static /* synthetic */ ReceiveChannel dropWhile$default(ReceiveChannel receiveChannel, CoroutineContext coroutineContext, Function2 function2, int i, Object obj) {
        if ((i & 1) != 0) {
            coroutineContext = Dispatchers.getUnconfined();
        }
        return dropWhile(receiveChannel, coroutineContext, function2);
    }

    /* JADX INFO: Add missing generic type declarations: [E] */
    /* JADX INFO: renamed from: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$dropWhile$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: Deprecated.kt */
    @Metadata(d1 = {"\u0000\f\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u0003H\u008a@"}, d2 = {"<anonymous>", "", "E", "Lkotlinx/coroutines/channels/ProducerScope;"}, k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$dropWhile$1", f = "Deprecated.kt", i = {0, 1, 1, 2, 3, 4}, l = {211, 212, 213, 217, 218}, m = "invokeSuspend", n = {"$this$produce", "$this$produce", "e", "$this$produce", "$this$produce", "$this$produce"}, s = {"L$0", "L$0", "L$2", "L$0", "L$0", "L$0"})
    static final class C00781<E> extends SuspendLambda implements Function2<ProducerScope<? super E>, Continuation<? super Unit>, Object> {
        final /* synthetic */ Function2<E, Continuation<? super Boolean>, Object> $predicate;
        final /* synthetic */ ReceiveChannel<E> $this_dropWhile;
        private /* synthetic */ Object L$0;
        Object L$1;
        Object L$2;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        /* JADX WARN: Multi-variable type inference failed */
        C00781(ReceiveChannel<? extends E> receiveChannel, Function2<? super E, ? super Continuation<? super Boolean>, ? extends Object> function2, Continuation<? super C00781> continuation) {
            super(2, continuation);
            this.$this_dropWhile = receiveChannel;
            this.$predicate = function2;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            C00781 c00781 = new C00781(this.$this_dropWhile, this.$predicate, continuation);
            c00781.L$0 = obj;
            return c00781;
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(ProducerScope<? super E> producerScope, Continuation<? super Unit> continuation) {
            return ((C00781) create(producerScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        /* JADX WARN: Code duplicated, block: B:14:0x0088 A[RETURN] */
        /* JADX WARN: Code duplicated, block: B:15:0x0089  */
        /* JADX WARN: Code duplicated, block: B:18:0x0097  */
        /* JADX WARN: Code duplicated, block: B:20:0x00ac A[RETURN] */
        /* JADX WARN: Code duplicated, block: B:21:0x00ad  */
        /* JADX WARN: Code duplicated, block: B:24:0x00b9  */
        /* JADX WARN: Code duplicated, block: B:26:0x00cb A[RETURN] */
        /* JADX WARN: Code duplicated, block: B:27:0x00cc  */
        /* JADX WARN: Code duplicated, block: B:29:0x00d5  */
        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r2v15 */
        /* JADX WARN: Type inference failed for: r2v2 */
        /* JADX WARN: Type inference failed for: r2v3 */
        /* JADX WARN: Type inference failed for: r3v22 */
        /* JADX WARN: Type inference failed for: r3v6, types: [java.lang.Object] */
        /* JADX WARN: Type inference failed for: r3v9 */
        /* JADX WARN: Type inference failed for: r5v0 */
        /* JADX WARN: Type inference failed for: r5v1, types: [java.lang.Object] */
        /* JADX WARN: Type inference failed for: r5v10 */
        /* JADX WARN: Type inference failed for: r5v11 */
        /* JADX WARN: Type inference failed for: r5v12, types: [java.lang.Object, kotlinx.coroutines.channels.ProducerScope] */
        /* JADX WARN: Type inference failed for: r5v13, types: [java.lang.Object] */
        /* JADX WARN: Type inference failed for: r5v14 */
        /* JADX WARN: Type inference failed for: r5v15 */
        /* JADX WARN: Type inference failed for: r5v16 */
        /* JADX WARN: Type inference failed for: r5v17 */
        /* JADX WARN: Type inference failed for: r5v18 */
        /* JADX WARN: Type inference failed for: r5v19 */
        /* JADX WARN: Type inference failed for: r5v20 */
        /* JADX WARN: Type inference failed for: r5v5 */
        /* JADX WARN: Type inference failed for: r5v6, types: [java.lang.Object, kotlinx.coroutines.channels.ProducerScope] */
        /* JADX WARN: Type inference failed for: r5v9 */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:21:0x00ad -> B:22:0x00b1). Please report as a decompilation issue!!! */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:40:0x0112 -> B:31:0x00e1). Please report as a decompilation issue!!! */
        /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
            jadx.core.utils.exceptions.JadxOverflowException: Regions stack size limit reached
            	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
            	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
            	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
            */
        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final java.lang.Object invokeSuspend(java.lang.Object r10) {
            /*
                Method dump skipped, instruction units count: 296
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt.C00781.invokeSuspend(java.lang.Object):java.lang.Object");
        }
    }

    @Deprecated(level = DeprecationLevel.HIDDEN, message = "Binary compatibility")
    public static final /* synthetic */ ReceiveChannel dropWhile(ReceiveChannel $this$dropWhile, CoroutineContext context, Function2 predicate) {
        return ProduceKt.produce$default(GlobalScope.INSTANCE, context, 0, null, ChannelsKt.consumes($this$dropWhile), new C00781($this$dropWhile, predicate, null), 6, null);
    }

    public static /* synthetic */ ReceiveChannel filter$default(ReceiveChannel receiveChannel, CoroutineContext coroutineContext, Function2 function2, int i, Object obj) {
        if ((i & 1) != 0) {
            coroutineContext = Dispatchers.getUnconfined();
        }
        return ChannelsKt.filter(receiveChannel, coroutineContext, function2);
    }

    /* JADX INFO: Add missing generic type declarations: [E] */
    /* JADX INFO: renamed from: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$filter$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: Deprecated.kt */
    @Metadata(d1 = {"\u0000\f\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u0003H\u008a@"}, d2 = {"<anonymous>", "", "E", "Lkotlinx/coroutines/channels/ProducerScope;"}, k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$filter$1", f = "Deprecated.kt", i = {0, 1, 1, 2}, l = {228, 229, 229}, m = "invokeSuspend", n = {"$this$produce", "$this$produce", "e", "$this$produce"}, s = {"L$0", "L$0", "L$2", "L$0"})
    static final class C00811<E> extends SuspendLambda implements Function2<ProducerScope<? super E>, Continuation<? super Unit>, Object> {
        final /* synthetic */ Function2<E, Continuation<? super Boolean>, Object> $predicate;
        final /* synthetic */ ReceiveChannel<E> $this_filter;
        private /* synthetic */ Object L$0;
        Object L$1;
        Object L$2;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        /* JADX WARN: Multi-variable type inference failed */
        C00811(ReceiveChannel<? extends E> receiveChannel, Function2<? super E, ? super Continuation<? super Boolean>, ? extends Object> function2, Continuation<? super C00811> continuation) {
            super(2, continuation);
            this.$this_filter = receiveChannel;
            this.$predicate = function2;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            C00811 c00811 = new C00811(this.$this_filter, this.$predicate, continuation);
            c00811.L$0 = obj;
            return c00811;
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(ProducerScope<? super E> producerScope, Continuation<? super Unit> continuation) {
            return ((C00811) create(producerScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        /* JADX WARN: Code duplicated, block: B:12:0x006b A[RETURN] */
        /* JADX WARN: Code duplicated, block: B:13:0x006c  */
        /* JADX WARN: Code duplicated, block: B:16:0x007b  */
        /* JADX WARN: Code duplicated, block: B:18:0x0090 A[RETURN] */
        /* JADX WARN: Code duplicated, block: B:19:0x0091  */
        /* JADX WARN: Code duplicated, block: B:22:0x009e  */
        /* JADX WARN: Code duplicated, block: B:24:0x00b0 A[RETURN] */
        /* JADX WARN: Code duplicated, block: B:25:0x00b1  */
        /* JADX WARN: Code duplicated, block: B:27:0x00b7  */
        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r4v10 */
        /* JADX WARN: Type inference failed for: r4v11 */
        /* JADX WARN: Type inference failed for: r4v14, types: [java.lang.Object] */
        /* JADX WARN: Type inference failed for: r4v18 */
        /* JADX WARN: Type inference failed for: r4v2 */
        /* JADX WARN: Type inference failed for: r5v0 */
        /* JADX WARN: Type inference failed for: r5v10 */
        /* JADX WARN: Type inference failed for: r5v5, types: [java.lang.Object] */
        /* JADX WARN: Type inference failed for: r6v0 */
        /* JADX WARN: Type inference failed for: r6v1, types: [java.lang.Object, kotlinx.coroutines.channels.ProducerScope] */
        /* JADX WARN: Type inference failed for: r6v4 */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:25:0x00b1 -> B:10:0x0059). Please report as a decompilation issue!!! */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:27:0x00b7 -> B:10:0x0059). Please report as a decompilation issue!!! */
        /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
            jadx.core.utils.exceptions.JadxOverflowException: Regions stack size limit reached
            	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
            	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
            	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
            */
        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final java.lang.Object invokeSuspend(java.lang.Object r10) {
            /*
                Method dump skipped, instruction units count: 204
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt.C00811.invokeSuspend(java.lang.Object):java.lang.Object");
        }
    }

    public static final <E> ReceiveChannel<E> filter(ReceiveChannel<? extends E> receiveChannel, CoroutineContext context, Function2<? super E, ? super Continuation<? super Boolean>, ? extends Object> function2) {
        return ProduceKt.produce$default(GlobalScope.INSTANCE, context, 0, null, ChannelsKt.consumes(receiveChannel), new C00811(receiveChannel, function2, null), 6, null);
    }

    public static /* synthetic */ ReceiveChannel filterIndexed$default(ReceiveChannel receiveChannel, CoroutineContext coroutineContext, Function3 function3, int i, Object obj) {
        if ((i & 1) != 0) {
            coroutineContext = Dispatchers.getUnconfined();
        }
        return filterIndexed(receiveChannel, coroutineContext, function3);
    }

    /* JADX INFO: Add missing generic type declarations: [E] */
    /* JADX INFO: renamed from: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$filterIndexed$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: Deprecated.kt */
    @Metadata(d1 = {"\u0000\f\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u0003H\u008a@"}, d2 = {"<anonymous>", "", "E", "Lkotlinx/coroutines/channels/ProducerScope;"}, k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$filterIndexed$1", f = "Deprecated.kt", i = {0, 0, 1, 1, 1, 2, 2}, l = {241, 242, 242}, m = "invokeSuspend", n = {"$this$produce", "index", "$this$produce", "e", "index", "$this$produce", "index"}, s = {"L$0", "I$0", "L$0", "L$2", "I$0", "L$0", "I$0"})
    static final class C00821<E> extends SuspendLambda implements Function2<ProducerScope<? super E>, Continuation<? super Unit>, Object> {
        final /* synthetic */ Function3<Integer, E, Continuation<? super Boolean>, Object> $predicate;
        final /* synthetic */ ReceiveChannel<E> $this_filterIndexed;
        int I$0;
        private /* synthetic */ Object L$0;
        Object L$1;
        Object L$2;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        /* JADX WARN: Multi-variable type inference failed */
        C00821(ReceiveChannel<? extends E> receiveChannel, Function3<? super Integer, ? super E, ? super Continuation<? super Boolean>, ? extends Object> function3, Continuation<? super C00821> continuation) {
            super(2, continuation);
            this.$this_filterIndexed = receiveChannel;
            this.$predicate = function3;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            C00821 c00821 = new C00821(this.$this_filterIndexed, this.$predicate, continuation);
            c00821.L$0 = obj;
            return c00821;
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(ProducerScope<? super E> producerScope, Continuation<? super Unit> continuation) {
            return ((C00821) create(producerScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        /* JADX WARN: Code duplicated, block: B:12:0x0074 A[RETURN] */
        /* JADX WARN: Code duplicated, block: B:13:0x0075  */
        /* JADX WARN: Code duplicated, block: B:16:0x0085  */
        /* JADX WARN: Code duplicated, block: B:18:0x00a2 A[RETURN] */
        /* JADX WARN: Code duplicated, block: B:19:0x00a3  */
        /* JADX WARN: Code duplicated, block: B:22:0x00ae  */
        /* JADX WARN: Code duplicated, block: B:24:0x00c2 A[RETURN] */
        /* JADX WARN: Code duplicated, block: B:25:0x00c3  */
        /* JADX WARN: Code duplicated, block: B:27:0x00ca  */
        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r5v10 */
        /* JADX WARN: Type inference failed for: r5v12, types: [java.lang.Object] */
        /* JADX WARN: Type inference failed for: r5v16 */
        /* JADX WARN: Type inference failed for: r5v2 */
        /* JADX WARN: Type inference failed for: r5v9 */
        /* JADX WARN: Type inference failed for: r6v0 */
        /* JADX WARN: Type inference failed for: r6v10 */
        /* JADX WARN: Type inference failed for: r6v3, types: [java.lang.Object, kotlinx.coroutines.channels.ProducerScope] */
        /* JADX WARN: Type inference failed for: r6v4, types: [java.lang.Object] */
        /* JADX WARN: Type inference failed for: r6v8 */
        /* JADX WARN: Type inference failed for: r6v9 */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:25:0x00c3 -> B:10:0x0060). Please report as a decompilation issue!!! */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:27:0x00ca -> B:10:0x0060). Please report as a decompilation issue!!! */
        /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
            jadx.core.utils.exceptions.JadxOverflowException: Regions stack size limit reached
            	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
            	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
            	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
            */
        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final java.lang.Object invokeSuspend(java.lang.Object r12) {
            /*
                Method dump skipped, instruction units count: 224
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt.C00821.invokeSuspend(java.lang.Object):java.lang.Object");
        }
    }

    @Deprecated(level = DeprecationLevel.HIDDEN, message = "Binary compatibility")
    public static final /* synthetic */ ReceiveChannel filterIndexed(ReceiveChannel $this$filterIndexed, CoroutineContext context, Function3 predicate) {
        return ProduceKt.produce$default(GlobalScope.INSTANCE, context, 0, null, ChannelsKt.consumes($this$filterIndexed), new C00821($this$filterIndexed, predicate, null), 6, null);
    }

    public static /* synthetic */ ReceiveChannel filterNot$default(ReceiveChannel receiveChannel, CoroutineContext coroutineContext, Function2 function2, int i, Object obj) {
        if ((i & 1) != 0) {
            coroutineContext = Dispatchers.getUnconfined();
        }
        return filterNot(receiveChannel, coroutineContext, function2);
    }

    /* JADX INFO: Add missing generic type declarations: [E] */
    /* JADX INFO: renamed from: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$filterNot$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: Deprecated.kt */
    @Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u00022\u0006\u0010\u0003\u001a\u0002H\u0002H\u008a@"}, d2 = {"<anonymous>", "", "E", "it"}, k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$filterNot$1", f = "Deprecated.kt", i = {}, l = {252}, m = "invokeSuspend", n = {}, s = {})
    static final class C00831<E> extends SuspendLambda implements Function2<E, Continuation<? super Boolean>, Object> {
        final /* synthetic */ Function2<E, Continuation<? super Boolean>, Object> $predicate;
        /* synthetic */ Object L$0;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        /* JADX WARN: Multi-variable type inference failed */
        C00831(Function2<? super E, ? super Continuation<? super Boolean>, ? extends Object> function2, Continuation<? super C00831> continuation) {
            super(2, continuation);
            this.$predicate = function2;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            C00831 c00831 = new C00831(this.$predicate, continuation);
            c00831.L$0 = obj;
            return c00831;
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(E e, Continuation<? super Boolean> continuation) {
            return ((C00831) create(e, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        /*  JADX ERROR: JadxRuntimeException in pass: ModVisitor
            jadx.core.utils.exceptions.JadxRuntimeException: Can't change immutable type java.lang.Object to kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$filterNot$1<E> for r5v1 'this'  java.lang.Object
            	at jadx.core.dex.instructions.args.SSAVar.setType(SSAVar.java:114)
            	at jadx.core.dex.instructions.args.RegisterArg.setType(RegisterArg.java:52)
            	at jadx.core.dex.visitors.ModVisitor.removeCheckCast(ModVisitor.java:417)
            	at jadx.core.dex.visitors.ModVisitor.replaceStep(ModVisitor.java:152)
            	at jadx.core.dex.visitors.ModVisitor.visit(ModVisitor.java:96)
            */
        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final java.lang.Object invokeSuspend(java.lang.Object r6) {
            /*
                r5 = this;
                java.lang.Object r0 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
                int r1 = r5.label
                r2 = 1
                switch(r1) {
                    case 0: goto L19;
                    case 1: goto L12;
                    default: goto La;
                }
            La:
                java.lang.IllegalStateException r6 = new java.lang.IllegalStateException
                java.lang.String r0 = "call to 'resume' before 'invoke' with coroutine"
                r6.<init>(r0)
                throw r6
            L12:
                r0 = r5
                kotlin.ResultKt.throwOnFailure(r6)
                r1 = r0
                r0 = r6
                goto L2c
            L19:
                kotlin.ResultKt.throwOnFailure(r6)
                r1 = r5
                java.lang.Object r3 = r1.L$0
                kotlin.jvm.functions.Function2<E, kotlin.coroutines.Continuation<? super java.lang.Boolean>, java.lang.Object> r4 = r1.$predicate
                r1.label = r2
                java.lang.Object r3 = r4.invoke(r3, r1)
                if (r3 != r0) goto L2a
                return r0
            L2a:
                r0 = r6
                r6 = r3
            L2c:
                java.lang.Boolean r6 = (java.lang.Boolean) r6
                boolean r6 = r6.booleanValue()
                r6 = r6 ^ r2
                java.lang.Boolean r6 = kotlin.coroutines.jvm.internal.Boxing.boxBoolean(r6)
                return r6
            */
            throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt.C00831.invokeSuspend(java.lang.Object):java.lang.Object");
        }
    }

    @Deprecated(level = DeprecationLevel.HIDDEN, message = "Binary compatibility")
    public static final /* synthetic */ ReceiveChannel filterNot(ReceiveChannel $this$filterNot, CoroutineContext context, Function2 predicate) {
        return ChannelsKt.filter($this$filterNot, context, new C00831(predicate, null));
    }

    /* JADX INFO: Add missing generic type declarations: [E] */
    /* JADX INFO: renamed from: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$filterNotNull$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: Deprecated.kt */
    @Metadata(d1 = {"\u0000\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\u0010\u0000\u001a\u00020\u0001\"\b\b\u0000\u0010\u0002*\u00020\u00032\b\u0010\u0004\u001a\u0004\u0018\u0001H\u0002H\u008a@"}, d2 = {"<anonymous>", "", "E", "", "it"}, k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$filterNotNull$1", f = "Deprecated.kt", i = {}, l = {}, m = "invokeSuspend", n = {}, s = {})
    static final class C00841<E> extends SuspendLambda implements Function2<E, Continuation<? super Boolean>, Object> {
        /* synthetic */ Object L$0;
        int label;

        C00841(Continuation<? super C00841> continuation) {
            super(2, continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            C00841 c00841 = new C00841(continuation);
            c00841.L$0 = obj;
            return c00841;
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(E e, Continuation<? super Boolean> continuation) {
            return ((C00841) create(e, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) throws Throwable {
            IntrinsicsKt.getCOROUTINE_SUSPENDED();
            switch (this.label) {
                case 0:
                    ResultKt.throwOnFailure(obj);
                    Object it = this.L$0;
                    return Boxing.boxBoolean(it != null);
                default:
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }
        }
    }

    public static final <E> ReceiveChannel<E> filterNotNull(ReceiveChannel<? extends E> receiveChannel) {
        ReceiveChannel<E> receiveChannelFilter$default = filter$default(receiveChannel, null, new C00841(null), 1, null);
        Intrinsics.checkNotNull(receiveChannelFilter$default, "null cannot be cast to non-null type kotlinx.coroutines.channels.ReceiveChannel<E of kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt.filterNotNull>");
        return receiveChannelFilter$default;
    }

    /* JADX WARN: Code duplicated, block: B:22:0x0070 A[RETURN] */
    /* JADX WARN: Code duplicated, block: B:23:0x0071  */
    /* JADX WARN: Code duplicated, block: B:26:0x0083 A[Catch: all -> 0x00a1, TryCatch #0 {all -> 0x00a1, blocks: (B:24:0x007b, B:26:0x0083, B:28:0x008a, B:30:0x0097), top: B:42:0x007b }] */
    /* JADX WARN: Code duplicated, block: B:28:0x008a A[Catch: all -> 0x00a1, TryCatch #0 {all -> 0x00a1, blocks: (B:24:0x007b, B:26:0x0083, B:28:0x008a, B:30:0x0097), top: B:42:0x007b }] */
    /* JADX WARN: Code duplicated, block: B:30:0x0097 A[Catch: all -> 0x00a1, TRY_LEAVE, TryCatch #0 {all -> 0x00a1, blocks: (B:24:0x007b, B:26:0x0083, B:28:0x008a, B:30:0x0097), top: B:42:0x007b }] */
    /* JADX WARN: Code duplicated, block: B:7:0x0014  */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:23:0x0071 -> B:42:0x007b). Please report as a decompilation issue!!! */
    /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxOverflowException: Regions stack size limit reached
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
        	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
        	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
        */
    @kotlin.Deprecated(level = kotlin.DeprecationLevel.HIDDEN, message = "Binary compatibility")
    public static final /* synthetic */ java.lang.Object filterNotNullTo(kotlinx.coroutines.channels.ReceiveChannel r10, java.util.Collection r11, kotlin.coroutines.Continuation r12) {
        /*
            boolean r0 = r12 instanceof kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt.C00851
            if (r0 == 0) goto L14
            r0 = r12
            kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$filterNotNullTo$1 r0 = (kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt.C00851) r0
            int r1 = r0.label
            r2 = -2147483648(0xffffffff80000000, float:-0.0)
            r1 = r1 & r2
            if (r1 == 0) goto L14
            int r12 = r0.label
            int r12 = r12 - r2
            r0.label = r12
            goto L19
        L14:
            kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$filterNotNullTo$1 r0 = new kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$filterNotNullTo$1
            r0.<init>(r12)
        L19:
            java.lang.Object r12 = r0.result
            java.lang.Object r1 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
            int r2 = r0.label
            switch(r2) {
                case 0: goto L4b;
                case 1: goto L2c;
                default: goto L24;
            }
        L24:
            java.lang.IllegalStateException r10 = new java.lang.IllegalStateException
            java.lang.String r11 = "call to 'resume' before 'invoke' with coroutine"
            r10.<init>(r11)
            throw r10
        L2c:
            r10 = 0
            r11 = 0
            r2 = 0
            java.lang.Object r3 = r0.L$2
            kotlinx.coroutines.channels.ChannelIterator r3 = (kotlinx.coroutines.channels.ChannelIterator) r3
            r4 = 0
            java.lang.Object r5 = r0.L$1
            kotlinx.coroutines.channels.ReceiveChannel r5 = (kotlinx.coroutines.channels.ReceiveChannel) r5
            java.lang.Object r6 = r0.L$0
            java.util.Collection r6 = (java.util.Collection) r6
            kotlin.ResultKt.throwOnFailure(r12)     // Catch: java.lang.Throwable -> L48
            r7 = r6
            r6 = r5
            r5 = r4
            r4 = r3
            r3 = r2
            r2 = r1
            r1 = r0
            r0 = r12
            goto L7b
        L48:
            r1 = move-exception
            goto Lac
        L4b:
            kotlin.ResultKt.throwOnFailure(r12)
            r2 = 0
            r5 = r10
            r10 = 0
            r4 = 0
            r3 = r5
            r6 = 0
            kotlinx.coroutines.channels.ChannelIterator r7 = r3.iterator()     // Catch: java.lang.Throwable -> La9
            r3 = r11
            r11 = r10
            r10 = r2
            r2 = r6
            r6 = r3
            r3 = r7
        L61:
            r0.L$0 = r6     // Catch: java.lang.Throwable -> L48
            r0.L$1 = r5     // Catch: java.lang.Throwable -> L48
            r0.L$2 = r3     // Catch: java.lang.Throwable -> L48
            r7 = 1
            r0.label = r7     // Catch: java.lang.Throwable -> L48
            java.lang.Object r7 = r3.hasNext(r0)     // Catch: java.lang.Throwable -> L48
            if (r7 != r1) goto L71
            return r1
        L71:
            r9 = r0
            r0 = r12
            r12 = r7
            r7 = r6
            r6 = r5
            r5 = r4
            r4 = r3
            r3 = r2
            r2 = r1
            r1 = r9
        L7b:
            java.lang.Boolean r12 = (java.lang.Boolean) r12     // Catch: java.lang.Throwable -> La1
            boolean r12 = r12.booleanValue()     // Catch: java.lang.Throwable -> La1
            if (r12 == 0) goto L97
            java.lang.Object r12 = r4.next()     // Catch: java.lang.Throwable -> La1
            r8 = 0
            if (r12 == 0) goto L8d
            r7.add(r12)     // Catch: java.lang.Throwable -> La1
        L8d:
            r12 = r0
            r0 = r1
            r1 = r2
            r2 = r3
            r3 = r4
            r4 = r5
            r5 = r6
            r6 = r7
            goto L61
        L97:
            kotlin.Unit r12 = kotlin.Unit.INSTANCE     // Catch: java.lang.Throwable -> La1
            kotlinx.coroutines.channels.ChannelsKt.cancelConsumed(r6, r5)
            return r7
        La1:
            r12 = move-exception
            r4 = r1
            r1 = r12
            r12 = r0
            r0 = r4
            r4 = r5
            r5 = r6
            goto Lac
        La9:
            r1 = move-exception
            r11 = r10
            r10 = r2
        Lac:
            r2 = r1
            throw r1     // Catch: java.lang.Throwable -> Laf
        Laf:
            r1 = move-exception
            kotlinx.coroutines.channels.ChannelsKt.cancelConsumed(r5, r2)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt.filterNotNullTo(kotlinx.coroutines.channels.ReceiveChannel, java.util.Collection, kotlin.coroutines.Continuation):java.lang.Object");
    }

    /* JADX WARN: Code duplicated, block: B:25:0x0084 A[RETURN] */
    /* JADX WARN: Code duplicated, block: B:26:0x0085  */
    /* JADX WARN: Code duplicated, block: B:29:0x0092 A[Catch: all -> 0x00c6, TryCatch #1 {all -> 0x00c6, blocks: (B:27:0x008a, B:29:0x0092, B:31:0x0099, B:38:0x00bc), top: B:54:0x008a }] */
    /* JADX WARN: Code duplicated, block: B:31:0x0099 A[Catch: all -> 0x00c6, TryCatch #1 {all -> 0x00c6, blocks: (B:27:0x008a, B:29:0x0092, B:31:0x0099, B:38:0x00bc), top: B:54:0x008a }] */
    /* JADX WARN: Code duplicated, block: B:33:0x00a8 A[RETURN] */
    /* JADX WARN: Code duplicated, block: B:34:0x00a9  */
    /* JADX WARN: Code duplicated, block: B:36:0x00b8  */
    /* JADX WARN: Code duplicated, block: B:38:0x00bc A[Catch: all -> 0x00c6, TRY_LEAVE, TryCatch #1 {all -> 0x00c6, blocks: (B:27:0x008a, B:29:0x0092, B:31:0x0099, B:38:0x00bc), top: B:54:0x008a }] */
    /* JADX WARN: Code duplicated, block: B:7:0x0014  */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:34:0x00a9 -> B:35:0x00af). Please report as a decompilation issue!!! */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:36:0x00b8 -> B:37:0x00bb). Please report as a decompilation issue!!! */
    /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxOverflowException: Regions stack size limit reached
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
        	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
        	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
        */
    @kotlin.Deprecated(level = kotlin.DeprecationLevel.HIDDEN, message = "Binary compatibility")
    public static final /* synthetic */ java.lang.Object filterNotNullTo(kotlinx.coroutines.channels.ReceiveChannel r11, kotlinx.coroutines.channels.SendChannel r12, kotlin.coroutines.Continuation r13) {
        /*
            Method dump skipped, instruction units count: 232
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt.filterNotNullTo(kotlinx.coroutines.channels.ReceiveChannel, kotlinx.coroutines.channels.SendChannel, kotlin.coroutines.Continuation):java.lang.Object");
    }

    /* JADX INFO: Add missing generic type declarations: [E] */
    /* JADX INFO: renamed from: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$take$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: Deprecated.kt */
    @Metadata(d1 = {"\u0000\f\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u0003H\u008a@"}, d2 = {"<anonymous>", "", "E", "Lkotlinx/coroutines/channels/ProducerScope;"}, k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$take$1", f = "Deprecated.kt", i = {0, 0, 1, 1}, l = {284, 285}, m = "invokeSuspend", n = {"$this$produce", "remaining", "$this$produce", "remaining"}, s = {"L$0", "I$0", "L$0", "I$0"})
    static final class C01011<E> extends SuspendLambda implements Function2<ProducerScope<? super E>, Continuation<? super Unit>, Object> {
        final /* synthetic */ int $n;
        final /* synthetic */ ReceiveChannel<E> $this_take;
        int I$0;
        private /* synthetic */ Object L$0;
        Object L$1;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        /* JADX WARN: Multi-variable type inference failed */
        C01011(int i, ReceiveChannel<? extends E> receiveChannel, Continuation<? super C01011> continuation) {
            super(2, continuation);
            this.$n = i;
            this.$this_take = receiveChannel;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            C01011 c01011 = new C01011(this.$n, this.$this_take, continuation);
            c01011.L$0 = obj;
            return c01011;
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(ProducerScope<? super E> producerScope, Continuation<? super Unit> continuation) {
            return ((C01011) create(producerScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        /* JADX WARN: Code duplicated, block: B:21:0x006a A[RETURN] */
        /* JADX WARN: Code duplicated, block: B:22:0x006b  */
        /* JADX WARN: Code duplicated, block: B:25:0x0079  */
        /* JADX WARN: Code duplicated, block: B:27:0x008f A[RETURN] */
        /* JADX WARN: Code duplicated, block: B:28:0x0090  */
        /* JADX WARN: Code duplicated, block: B:31:0x009a  */
        /* JADX WARN: Code duplicated, block: B:33:0x009d  */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:28:0x0090 -> B:29:0x0096). Please report as a decompilation issue!!! */
        /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
            jadx.core.utils.exceptions.JadxOverflowException: Regions stack size limit reached
            	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
            	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
            	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
            */
        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final java.lang.Object invokeSuspend(java.lang.Object r11) {
            /*
                Method dump skipped, instruction units count: 212
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt.C01011.invokeSuspend(java.lang.Object):java.lang.Object");
        }
    }

    public static /* synthetic */ ReceiveChannel take$default(ReceiveChannel receiveChannel, int i, CoroutineContext coroutineContext, int i2, Object obj) {
        if ((i2 & 2) != 0) {
            coroutineContext = Dispatchers.getUnconfined();
        }
        return take(receiveChannel, i, coroutineContext);
    }

    @Deprecated(level = DeprecationLevel.HIDDEN, message = "Binary compatibility")
    public static final /* synthetic */ ReceiveChannel take(ReceiveChannel $this$take, int n, CoroutineContext context) {
        return ProduceKt.produce$default(GlobalScope.INSTANCE, context, 0, null, ChannelsKt.consumes($this$take), new C01011(n, $this$take, null), 6, null);
    }

    public static /* synthetic */ ReceiveChannel takeWhile$default(ReceiveChannel receiveChannel, CoroutineContext coroutineContext, Function2 function2, int i, Object obj) {
        if ((i & 1) != 0) {
            coroutineContext = Dispatchers.getUnconfined();
        }
        return takeWhile(receiveChannel, coroutineContext, function2);
    }

    /* JADX INFO: Add missing generic type declarations: [E] */
    /* JADX INFO: renamed from: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$takeWhile$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: Deprecated.kt */
    @Metadata(d1 = {"\u0000\f\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u0003H\u008a@"}, d2 = {"<anonymous>", "", "E", "Lkotlinx/coroutines/channels/ProducerScope;"}, k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$takeWhile$1", f = "Deprecated.kt", i = {0, 1, 1, 2}, l = {299, 300, 301}, m = "invokeSuspend", n = {"$this$produce", "$this$produce", "e", "$this$produce"}, s = {"L$0", "L$0", "L$2", "L$0"})
    static final class C01021<E> extends SuspendLambda implements Function2<ProducerScope<? super E>, Continuation<? super Unit>, Object> {
        final /* synthetic */ Function2<E, Continuation<? super Boolean>, Object> $predicate;
        final /* synthetic */ ReceiveChannel<E> $this_takeWhile;
        private /* synthetic */ Object L$0;
        Object L$1;
        Object L$2;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        /* JADX WARN: Multi-variable type inference failed */
        C01021(ReceiveChannel<? extends E> receiveChannel, Function2<? super E, ? super Continuation<? super Boolean>, ? extends Object> function2, Continuation<? super C01021> continuation) {
            super(2, continuation);
            this.$this_takeWhile = receiveChannel;
            this.$predicate = function2;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            C01021 c01021 = new C01021(this.$this_takeWhile, this.$predicate, continuation);
            c01021.L$0 = obj;
            return c01021;
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(ProducerScope<? super E> producerScope, Continuation<? super Unit> continuation) {
            return ((C01021) create(producerScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        /* JADX WARN: Code duplicated, block: B:12:0x0068 A[RETURN] */
        /* JADX WARN: Code duplicated, block: B:13:0x0069  */
        /* JADX WARN: Code duplicated, block: B:16:0x0078  */
        /* JADX WARN: Code duplicated, block: B:18:0x008d A[RETURN] */
        /* JADX WARN: Code duplicated, block: B:19:0x008e  */
        /* JADX WARN: Code duplicated, block: B:22:0x009b  */
        /* JADX WARN: Code duplicated, block: B:24:0x009e  */
        /* JADX WARN: Code duplicated, block: B:26:0x00b1 A[RETURN] */
        /* JADX WARN: Code duplicated, block: B:27:0x00b2  */
        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r3v10 */
        /* JADX WARN: Type inference failed for: r3v13, types: [java.lang.Object] */
        /* JADX WARN: Type inference failed for: r3v17 */
        /* JADX WARN: Type inference failed for: r3v2 */
        /* JADX WARN: Type inference failed for: r4v0 */
        /* JADX WARN: Type inference failed for: r4v10 */
        /* JADX WARN: Type inference failed for: r4v5, types: [java.lang.Object] */
        /* JADX WARN: Type inference failed for: r5v0 */
        /* JADX WARN: Type inference failed for: r5v1, types: [java.lang.Object, kotlinx.coroutines.channels.ProducerScope] */
        /* JADX WARN: Type inference failed for: r5v4 */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:27:0x00b2 -> B:10:0x0058). Please report as a decompilation issue!!! */
        /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
            jadx.core.utils.exceptions.JadxOverflowException: Regions stack size limit reached
            	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
            	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
            	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
            */
        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final java.lang.Object invokeSuspend(java.lang.Object r9) {
            /*
                r8 = this;
                java.lang.Object r0 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
                int r1 = r8.label
                switch(r1) {
                    case 0: goto L47;
                    case 1: goto L35;
                    case 2: goto L1f;
                    case 3: goto L11;
                    default: goto L9;
                }
            L9:
                java.lang.IllegalStateException r9 = new java.lang.IllegalStateException
                java.lang.String r0 = "call to 'resume' before 'invoke' with coroutine"
                r9.<init>(r0)
                throw r9
            L11:
                r1 = r8
                java.lang.Object r2 = r1.L$1
                kotlinx.coroutines.channels.ChannelIterator r2 = (kotlinx.coroutines.channels.ChannelIterator) r2
                java.lang.Object r3 = r1.L$0
                kotlinx.coroutines.channels.ProducerScope r3 = (kotlinx.coroutines.channels.ProducerScope) r3
                kotlin.ResultKt.throwOnFailure(r9)
                goto Lb7
            L1f:
                r1 = r8
                java.lang.Object r2 = r1.L$2
                java.lang.Object r3 = r1.L$1
                kotlinx.coroutines.channels.ChannelIterator r3 = (kotlinx.coroutines.channels.ChannelIterator) r3
                java.lang.Object r4 = r1.L$0
                kotlinx.coroutines.channels.ProducerScope r4 = (kotlinx.coroutines.channels.ProducerScope) r4
                kotlin.ResultKt.throwOnFailure(r9)
                r5 = r4
                r4 = r3
                r3 = r2
                r2 = r1
                r1 = r0
                r0 = r9
                goto L93
            L35:
                r1 = r8
                java.lang.Object r2 = r1.L$1
                kotlinx.coroutines.channels.ChannelIterator r2 = (kotlinx.coroutines.channels.ChannelIterator) r2
                java.lang.Object r3 = r1.L$0
                kotlinx.coroutines.channels.ProducerScope r3 = (kotlinx.coroutines.channels.ProducerScope) r3
                kotlin.ResultKt.throwOnFailure(r9)
                r4 = r3
                r3 = r2
                r2 = r1
                r1 = r0
                r0 = r9
                goto L70
            L47:
                kotlin.ResultKt.throwOnFailure(r9)
                r1 = r8
                java.lang.Object r2 = r1.L$0
                kotlinx.coroutines.channels.ProducerScope r2 = (kotlinx.coroutines.channels.ProducerScope) r2
                kotlinx.coroutines.channels.ReceiveChannel<E> r3 = r1.$this_takeWhile
                kotlinx.coroutines.channels.ChannelIterator r3 = r3.iterator()
                r7 = r3
                r3 = r2
                r2 = r7
            L58:
                r4 = r1
                kotlin.coroutines.Continuation r4 = (kotlin.coroutines.Continuation) r4
                r1.L$0 = r3
                r1.L$1 = r2
                r5 = 1
                r1.label = r5
                java.lang.Object r4 = r2.hasNext(r4)
                if (r4 != r0) goto L69
                return r0
            L69:
                r7 = r0
                r0 = r9
                r9 = r4
                r4 = r3
                r3 = r2
                r2 = r1
                r1 = r7
            L70:
                java.lang.Boolean r9 = (java.lang.Boolean) r9
                boolean r9 = r9.booleanValue()
                if (r9 == 0) goto Lb8
                java.lang.Object r9 = r3.next()
                kotlin.jvm.functions.Function2<E, kotlin.coroutines.Continuation<? super java.lang.Boolean>, java.lang.Object> r5 = r2.$predicate
                r2.L$0 = r4
                r2.L$1 = r3
                r2.L$2 = r9
                r6 = 2
                r2.label = r6
                java.lang.Object r5 = r5.invoke(r9, r2)
                if (r5 != r1) goto L8e
                return r1
            L8e:
                r7 = r3
                r3 = r9
                r9 = r5
                r5 = r4
                r4 = r7
            L93:
                java.lang.Boolean r9 = (java.lang.Boolean) r9
                boolean r9 = r9.booleanValue()
                if (r9 != 0) goto L9e
                kotlin.Unit r9 = kotlin.Unit.INSTANCE
                return r9
            L9e:
                r9 = r2
                kotlin.coroutines.Continuation r9 = (kotlin.coroutines.Continuation) r9
                r2.L$0 = r5
                r2.L$1 = r4
                r6 = 0
                r2.L$2 = r6
                r6 = 3
                r2.label = r6
                java.lang.Object r9 = r5.send(r3, r9)
                if (r9 != r1) goto Lb2
                return r1
            Lb2:
                r9 = r0
                r0 = r1
                r1 = r2
                r2 = r4
                r3 = r5
            Lb7:
                goto L58
            Lb8:
                kotlin.Unit r9 = kotlin.Unit.INSTANCE
                return r9
            */
            throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt.C01021.invokeSuspend(java.lang.Object):java.lang.Object");
        }
    }

    @Deprecated(level = DeprecationLevel.HIDDEN, message = "Binary compatibility")
    public static final /* synthetic */ ReceiveChannel takeWhile(ReceiveChannel $this$takeWhile, CoroutineContext context, Function2 predicate) {
        return ProduceKt.produce$default(GlobalScope.INSTANCE, context, 0, null, ChannelsKt.consumes($this$takeWhile), new C01021($this$takeWhile, predicate, null), 6, null);
    }

    /* JADX WARN: Code duplicated, block: B:25:0x008c A[RETURN] */
    /* JADX WARN: Code duplicated, block: B:26:0x008d  */
    /* JADX WARN: Code duplicated, block: B:29:0x009a A[Catch: all -> 0x00bf, TryCatch #4 {all -> 0x00bf, blocks: (B:27:0x0092, B:29:0x009a, B:34:0x00b5), top: B:55:0x0092 }] */
    /* JADX WARN: Code duplicated, block: B:31:0x00ae A[RETURN] */
    /* JADX WARN: Code duplicated, block: B:32:0x00af  */
    /* JADX WARN: Code duplicated, block: B:7:0x0014  */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:32:0x00af -> B:33:0x00b3). Please report as a decompilation issue!!! */
    /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxOverflowException: Regions stack size limit reached
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
        	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
        	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
        */
    public static final <E, C extends kotlinx.coroutines.channels.SendChannel<? super E>> java.lang.Object toChannel(kotlinx.coroutines.channels.ReceiveChannel<? extends E> r11, C r12, kotlin.coroutines.Continuation<? super C> r13) {
        /*
            Method dump skipped, instruction units count: 226
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt.toChannel(kotlinx.coroutines.channels.ReceiveChannel, kotlinx.coroutines.channels.SendChannel, kotlin.coroutines.Continuation):java.lang.Object");
    }

    /* JADX WARN: Code duplicated, block: B:22:0x0070 A[RETURN] */
    /* JADX WARN: Code duplicated, block: B:23:0x0071  */
    /* JADX WARN: Code duplicated, block: B:26:0x0083 A[Catch: all -> 0x00a0, TryCatch #0 {all -> 0x00a0, blocks: (B:24:0x007b, B:26:0x0083, B:27:0x0096), top: B:39:0x007b }] */
    /* JADX WARN: Code duplicated, block: B:7:0x0014  */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r6v1 */
    /* JADX WARN: Type inference failed for: r6v2, types: [java.lang.Object] */
    /* JADX WARN: Type inference failed for: r6v5 */
    /* JADX WARN: Type inference failed for: r7v3 */
    /* JADX WARN: Type inference failed for: r7v4, types: [java.lang.Object, java.util.Collection] */
    /* JADX WARN: Type inference failed for: r7v5 */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:23:0x0071 -> B:39:0x007b). Please report as a decompilation issue!!! */
    /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxOverflowException: Regions stack size limit reached
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
        	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
        	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
        */
    public static final <E, C extends java.util.Collection<? super E>> java.lang.Object toCollection(kotlinx.coroutines.channels.ReceiveChannel<? extends E> r11, C r12, kotlin.coroutines.Continuation<? super C> r13) {
        /*
            boolean r0 = r13 instanceof kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt.C01041
            if (r0 == 0) goto L14
            r0 = r13
            kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$toCollection$1 r0 = (kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt.C01041) r0
            int r1 = r0.label
            r2 = -2147483648(0xffffffff80000000, float:-0.0)
            r1 = r1 & r2
            if (r1 == 0) goto L14
            int r13 = r0.label
            int r13 = r13 - r2
            r0.label = r13
            goto L19
        L14:
            kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$toCollection$1 r0 = new kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$toCollection$1
            r0.<init>(r13)
        L19:
            java.lang.Object r13 = r0.result
            java.lang.Object r1 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
            int r2 = r0.label
            switch(r2) {
                case 0: goto L4b;
                case 1: goto L2c;
                default: goto L24;
            }
        L24:
            java.lang.IllegalStateException r11 = new java.lang.IllegalStateException
            java.lang.String r12 = "call to 'resume' before 'invoke' with coroutine"
            r11.<init>(r12)
            throw r11
        L2c:
            r11 = 0
            r12 = 0
            r2 = 0
            java.lang.Object r3 = r0.L$2
            kotlinx.coroutines.channels.ChannelIterator r3 = (kotlinx.coroutines.channels.ChannelIterator) r3
            r4 = 0
            java.lang.Object r5 = r0.L$1
            kotlinx.coroutines.channels.ReceiveChannel r5 = (kotlinx.coroutines.channels.ReceiveChannel) r5
            java.lang.Object r6 = r0.L$0
            java.util.Collection r6 = (java.util.Collection) r6
            kotlin.ResultKt.throwOnFailure(r13)     // Catch: java.lang.Throwable -> L48
            r7 = r6
            r6 = r5
            r5 = r4
            r4 = r3
            r3 = r2
            r2 = r1
            r1 = r0
            r0 = r13
            goto L7b
        L48:
            r1 = move-exception
            goto Lab
        L4b:
            kotlin.ResultKt.throwOnFailure(r13)
            r2 = 0
            r5 = r11
            r11 = 0
            r4 = 0
            r3 = r5
            r6 = 0
            kotlinx.coroutines.channels.ChannelIterator r7 = r3.iterator()     // Catch: java.lang.Throwable -> La8
            r3 = r12
            r12 = r11
            r11 = r2
            r2 = r6
            r6 = r3
            r3 = r7
        L61:
            r0.L$0 = r6     // Catch: java.lang.Throwable -> L48
            r0.L$1 = r5     // Catch: java.lang.Throwable -> L48
            r0.L$2 = r3     // Catch: java.lang.Throwable -> L48
            r7 = 1
            r0.label = r7     // Catch: java.lang.Throwable -> L48
            java.lang.Object r7 = r3.hasNext(r0)     // Catch: java.lang.Throwable -> L48
            if (r7 != r1) goto L71
            return r1
        L71:
            r10 = r0
            r0 = r13
            r13 = r7
            r7 = r6
            r6 = r5
            r5 = r4
            r4 = r3
            r3 = r2
            r2 = r1
            r1 = r10
        L7b:
            java.lang.Boolean r13 = (java.lang.Boolean) r13     // Catch: java.lang.Throwable -> La0
            boolean r13 = r13.booleanValue()     // Catch: java.lang.Throwable -> La0
            if (r13 == 0) goto L96
            java.lang.Object r13 = r4.next()     // Catch: java.lang.Throwable -> La0
            r8 = r13
            r9 = 0
            r7.add(r8)     // Catch: java.lang.Throwable -> La0
            r13 = r0
            r0 = r1
            r1 = r2
            r2 = r3
            r3 = r4
            r4 = r5
            r5 = r6
            r6 = r7
            goto L61
        L96:
            kotlin.Unit r13 = kotlin.Unit.INSTANCE     // Catch: java.lang.Throwable -> La0
            kotlinx.coroutines.channels.ChannelsKt.cancelConsumed(r6, r5)
            return r7
        La0:
            r13 = move-exception
            r4 = r1
            r1 = r13
            r13 = r0
            r0 = r4
            r4 = r5
            r5 = r6
            goto Lab
        La8:
            r1 = move-exception
            r12 = r11
            r11 = r2
        Lab:
            r2 = r1
            throw r1     // Catch: java.lang.Throwable -> Lae
        Lae:
            r1 = move-exception
            kotlinx.coroutines.channels.ChannelsKt.cancelConsumed(r5, r2)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt.toCollection(kotlinx.coroutines.channels.ReceiveChannel, java.util.Collection, kotlin.coroutines.Continuation):java.lang.Object");
    }

    /* JADX WARN: Code duplicated, block: B:22:0x0070 A[RETURN] */
    /* JADX WARN: Code duplicated, block: B:23:0x0071  */
    /* JADX WARN: Code duplicated, block: B:26:0x0083 A[Catch: all -> 0x00aa, TryCatch #3 {all -> 0x00aa, blocks: (B:24:0x007b, B:26:0x0083, B:27:0x00a0), top: B:45:0x007b }] */
    /* JADX WARN: Code duplicated, block: B:7:0x0014  */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r6v1 */
    /* JADX WARN: Type inference failed for: r6v2, types: [java.lang.Object] */
    /* JADX WARN: Type inference failed for: r6v5 */
    /* JADX WARN: Type inference failed for: r7v3 */
    /* JADX WARN: Type inference failed for: r7v4, types: [java.lang.Object, java.util.Map] */
    /* JADX WARN: Type inference failed for: r7v5 */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:23:0x0071 -> B:45:0x007b). Please report as a decompilation issue!!! */
    /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxOverflowException: Regions stack size limit reached
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
        	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
        	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
        */
    public static final <K, V, M extends java.util.Map<? super K, ? super V>> java.lang.Object toMap(kotlinx.coroutines.channels.ReceiveChannel<? extends kotlin.Pair<? extends K, ? extends V>> r13, M r14, kotlin.coroutines.Continuation<? super M> r15) {
        /*
            boolean r0 = r15 instanceof kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt.AnonymousClass2
            if (r0 == 0) goto L14
            r0 = r15
            kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$toMap$2 r0 = (kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt.AnonymousClass2) r0
            int r1 = r0.label
            r2 = -2147483648(0xffffffff80000000, float:-0.0)
            r1 = r1 & r2
            if (r1 == 0) goto L14
            int r15 = r0.label
            int r15 = r15 - r2
            r0.label = r15
            goto L19
        L14:
            kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$toMap$2 r0 = new kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$toMap$2
            r0.<init>(r15)
        L19:
            java.lang.Object r15 = r0.result
            java.lang.Object r1 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
            int r2 = r0.label
            switch(r2) {
                case 0: goto L4b;
                case 1: goto L2c;
                default: goto L24;
            }
        L24:
            java.lang.IllegalStateException r13 = new java.lang.IllegalStateException
            java.lang.String r14 = "call to 'resume' before 'invoke' with coroutine"
            r13.<init>(r14)
            throw r13
        L2c:
            r13 = 0
            r14 = 0
            r2 = 0
            java.lang.Object r3 = r0.L$2
            kotlinx.coroutines.channels.ChannelIterator r3 = (kotlinx.coroutines.channels.ChannelIterator) r3
            r4 = 0
            java.lang.Object r5 = r0.L$1
            kotlinx.coroutines.channels.ReceiveChannel r5 = (kotlinx.coroutines.channels.ReceiveChannel) r5
            java.lang.Object r6 = r0.L$0
            java.util.Map r6 = (java.util.Map) r6
            kotlin.ResultKt.throwOnFailure(r15)     // Catch: java.lang.Throwable -> L48
            r7 = r6
            r6 = r5
            r5 = r4
            r4 = r3
            r3 = r2
            r2 = r1
            r1 = r0
            r0 = r15
            goto L7b
        L48:
            r1 = move-exception
            goto Lb5
        L4b:
            kotlin.ResultKt.throwOnFailure(r15)
            r2 = 0
            r5 = r13
            r13 = 0
            r4 = 0
            r3 = r5
            r6 = 0
            kotlinx.coroutines.channels.ChannelIterator r7 = r3.iterator()     // Catch: java.lang.Throwable -> Lb2
            r3 = r14
            r14 = r13
            r13 = r2
            r2 = r6
            r6 = r3
            r3 = r7
        L61:
            r0.L$0 = r6     // Catch: java.lang.Throwable -> L48
            r0.L$1 = r5     // Catch: java.lang.Throwable -> L48
            r0.L$2 = r3     // Catch: java.lang.Throwable -> L48
            r7 = 1
            r0.label = r7     // Catch: java.lang.Throwable -> L48
            java.lang.Object r7 = r3.hasNext(r0)     // Catch: java.lang.Throwable -> L48
            if (r7 != r1) goto L71
            return r1
        L71:
            r12 = r0
            r0 = r15
            r15 = r7
            r7 = r6
            r6 = r5
            r5 = r4
            r4 = r3
            r3 = r2
            r2 = r1
            r1 = r12
        L7b:
            java.lang.Boolean r15 = (java.lang.Boolean) r15     // Catch: java.lang.Throwable -> Laa
            boolean r15 = r15.booleanValue()     // Catch: java.lang.Throwable -> Laa
            if (r15 == 0) goto La0
            java.lang.Object r15 = r4.next()     // Catch: java.lang.Throwable -> Laa
            r8 = r15
            kotlin.Pair r8 = (kotlin.Pair) r8     // Catch: java.lang.Throwable -> Laa
            r9 = 0
            java.lang.Object r10 = r8.getFirst()     // Catch: java.lang.Throwable -> Laa
            java.lang.Object r11 = r8.getSecond()     // Catch: java.lang.Throwable -> Laa
            r7.put(r10, r11)     // Catch: java.lang.Throwable -> Laa
            r15 = r0
            r0 = r1
            r1 = r2
            r2 = r3
            r3 = r4
            r4 = r5
            r5 = r6
            r6 = r7
            goto L61
        La0:
            kotlin.Unit r15 = kotlin.Unit.INSTANCE     // Catch: java.lang.Throwable -> Laa
            kotlinx.coroutines.channels.ChannelsKt.cancelConsumed(r6, r5)
            return r7
        Laa:
            r15 = move-exception
            r4 = r1
            r1 = r15
            r15 = r0
            r0 = r4
            r4 = r5
            r5 = r6
            goto Lb5
        Lb2:
            r1 = move-exception
            r14 = r13
            r13 = r2
        Lb5:
            r2 = r1
            throw r1     // Catch: java.lang.Throwable -> Lb8
        Lb8:
            r1 = move-exception
            kotlinx.coroutines.channels.ChannelsKt.cancelConsumed(r5, r2)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt.toMap(kotlinx.coroutines.channels.ReceiveChannel, java.util.Map, kotlin.coroutines.Continuation):java.lang.Object");
    }

    public static /* synthetic */ ReceiveChannel flatMap$default(ReceiveChannel receiveChannel, CoroutineContext coroutineContext, Function2 function2, int i, Object obj) {
        if ((i & 1) != 0) {
            coroutineContext = Dispatchers.getUnconfined();
        }
        return flatMap(receiveChannel, coroutineContext, function2);
    }

    /* JADX INFO: Add missing generic type declarations: [R] */
    /* JADX INFO: renamed from: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$flatMap$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: Deprecated.kt */
    @Metadata(d1 = {"\u0000\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\u0003*\b\u0012\u0004\u0012\u0002H\u00030\u0004H\u008a@"}, d2 = {"<anonymous>", "", "E", "R", "Lkotlinx/coroutines/channels/ProducerScope;"}, k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$flatMap$1", f = "Deprecated.kt", i = {0, 1, 2}, l = {351, 352, 352}, m = "invokeSuspend", n = {"$this$produce", "$this$produce", "$this$produce"}, s = {"L$0", "L$0", "L$0"})
    static final class C00881<R> extends SuspendLambda implements Function2<ProducerScope<? super R>, Continuation<? super Unit>, Object> {
        final /* synthetic */ ReceiveChannel<E> $this_flatMap;
        final /* synthetic */ Function2<E, Continuation<? super ReceiveChannel<? extends R>>, Object> $transform;
        private /* synthetic */ Object L$0;
        Object L$1;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        /* JADX WARN: Multi-variable type inference failed */
        C00881(ReceiveChannel<? extends E> receiveChannel, Function2<? super E, ? super Continuation<? super ReceiveChannel<? extends R>>, ? extends Object> function2, Continuation<? super C00881> continuation) {
            super(2, continuation);
            this.$this_flatMap = receiveChannel;
            this.$transform = function2;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            C00881 c00881 = new C00881(this.$this_flatMap, this.$transform, continuation);
            c00881.L$0 = obj;
            return c00881;
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(ProducerScope<? super R> producerScope, Continuation<? super Unit> continuation) {
            return ((C00881) create(producerScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        /* JADX WARN: Code duplicated, block: B:12:0x0065 A[RETURN] */
        /* JADX WARN: Code duplicated, block: B:13:0x0066  */
        /* JADX WARN: Code duplicated, block: B:16:0x0075  */
        /* JADX WARN: Code duplicated, block: B:18:0x0088 A[RETURN] */
        /* JADX WARN: Code duplicated, block: B:21:0x009e A[RETURN] */
        /* JADX WARN: Code duplicated, block: B:22:0x009f  */
        /* JADX WARN: Type inference incomplete: some casts might be missing */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:22:0x009f -> B:10:0x0055). Please report as a decompilation issue!!! */
        /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
            jadx.core.utils.exceptions.JadxOverflowException: Regions stack size limit reached
            	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
            	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
            	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
            */
        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final java.lang.Object invokeSuspend(java.lang.Object r10) {
            /*
                r9 = this;
                java.lang.Object r0 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
                int r1 = r9.label
                switch(r1) {
                    case 0: goto L44;
                    case 1: goto L32;
                    case 2: goto L1f;
                    case 3: goto L11;
                    default: goto L9;
                }
            L9:
                java.lang.IllegalStateException r10 = new java.lang.IllegalStateException
                java.lang.String r0 = "call to 'resume' before 'invoke' with coroutine"
                r10.<init>(r0)
                throw r10
            L11:
                r1 = r9
                java.lang.Object r2 = r1.L$1
                kotlinx.coroutines.channels.ChannelIterator r2 = (kotlinx.coroutines.channels.ChannelIterator) r2
                java.lang.Object r3 = r1.L$0
                kotlinx.coroutines.channels.ProducerScope r3 = (kotlinx.coroutines.channels.ProducerScope) r3
                kotlin.ResultKt.throwOnFailure(r10)
                goto La4
            L1f:
                r1 = r9
                java.lang.Object r2 = r1.L$1
                kotlinx.coroutines.channels.ChannelIterator r2 = (kotlinx.coroutines.channels.ChannelIterator) r2
                java.lang.Object r3 = r1.L$0
                kotlinx.coroutines.channels.ProducerScope r3 = (kotlinx.coroutines.channels.ProducerScope) r3
                kotlin.ResultKt.throwOnFailure(r10)
                r4 = r3
                r3 = r2
                r2 = r1
                r1 = r0
                r0 = r10
                goto L89
            L32:
                r1 = r9
                java.lang.Object r2 = r1.L$1
                kotlinx.coroutines.channels.ChannelIterator r2 = (kotlinx.coroutines.channels.ChannelIterator) r2
                java.lang.Object r3 = r1.L$0
                kotlinx.coroutines.channels.ProducerScope r3 = (kotlinx.coroutines.channels.ProducerScope) r3
                kotlin.ResultKt.throwOnFailure(r10)
                r4 = r3
                r3 = r2
                r2 = r1
                r1 = r0
                r0 = r10
                goto L6d
            L44:
                kotlin.ResultKt.throwOnFailure(r10)
                r1 = r9
                java.lang.Object r2 = r1.L$0
                kotlinx.coroutines.channels.ProducerScope r2 = (kotlinx.coroutines.channels.ProducerScope) r2
                kotlinx.coroutines.channels.ReceiveChannel<E> r3 = r1.$this_flatMap
                kotlinx.coroutines.channels.ChannelIterator r3 = r3.iterator()
                r8 = r3
                r3 = r2
                r2 = r8
            L55:
                r4 = r1
                kotlin.coroutines.Continuation r4 = (kotlin.coroutines.Continuation) r4
                r1.L$0 = r3
                r1.L$1 = r2
                r5 = 1
                r1.label = r5
                java.lang.Object r4 = r2.hasNext(r4)
                if (r4 != r0) goto L66
                return r0
            L66:
                r8 = r0
                r0 = r10
                r10 = r4
                r4 = r3
                r3 = r2
                r2 = r1
                r1 = r8
            L6d:
                java.lang.Boolean r10 = (java.lang.Boolean) r10
                boolean r10 = r10.booleanValue()
                if (r10 == 0) goto La5
                java.lang.Object r10 = r3.next()
                kotlin.jvm.functions.Function2<E, kotlin.coroutines.Continuation<? super kotlinx.coroutines.channels.ReceiveChannel<? extends R>>, java.lang.Object> r5 = r2.$transform
                r2.L$0 = r4
                r2.L$1 = r3
                r6 = 2
                r2.label = r6
                java.lang.Object r10 = r5.invoke(r10, r2)
                if (r10 != r1) goto L89
                return r1
            L89:
                kotlinx.coroutines.channels.ReceiveChannel r10 = (kotlinx.coroutines.channels.ReceiveChannel) r10
                r5 = r4
                kotlinx.coroutines.channels.SendChannel r5 = (kotlinx.coroutines.channels.SendChannel) r5
                r6 = r2
                kotlin.coroutines.Continuation r6 = (kotlin.coroutines.Continuation) r6
                r2.L$0 = r4
                r2.L$1 = r3
                r7 = 3
                r2.label = r7
                java.lang.Object r10 = kotlinx.coroutines.channels.ChannelsKt.toChannel(r10, r5, r6)
                if (r10 != r1) goto L9f
                return r1
            L9f:
                r10 = r0
                r0 = r1
                r1 = r2
                r2 = r3
                r3 = r4
            La4:
                goto L55
            La5:
                kotlin.Unit r10 = kotlin.Unit.INSTANCE
                return r10
            */
            throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt.C00881.invokeSuspend(java.lang.Object):java.lang.Object");
        }
    }

    @Deprecated(level = DeprecationLevel.HIDDEN, message = "Binary compatibility")
    public static final /* synthetic */ ReceiveChannel flatMap(ReceiveChannel $this$flatMap, CoroutineContext context, Function2 transform) {
        return ProduceKt.produce$default(GlobalScope.INSTANCE, context, 0, null, ChannelsKt.consumes($this$flatMap), new C00881($this$flatMap, transform, null), 6, null);
    }

    public static /* synthetic */ ReceiveChannel map$default(ReceiveChannel receiveChannel, CoroutineContext coroutineContext, Function2 function2, int i, Object obj) {
        if ((i & 1) != 0) {
            coroutineContext = Dispatchers.getUnconfined();
        }
        return ChannelsKt.map(receiveChannel, coroutineContext, function2);
    }

    /* JADX INFO: Add missing generic type declarations: [R] */
    /* JADX INFO: renamed from: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$map$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: Deprecated.kt */
    @Metadata(d1 = {"\u0000\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\u0003*\b\u0012\u0004\u0012\u0002H\u00030\u0004H\u008a@"}, d2 = {"<anonymous>", "", "E", "R", "Lkotlinx/coroutines/channels/ProducerScope;"}, k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$map$1", f = "Deprecated.kt", i = {0, 0, 1, 1, 2, 2}, l = {517, 363, 363}, m = "invokeSuspend", n = {"$this$produce", "$this$consume$iv$iv", "$this$produce", "$this$consume$iv$iv", "$this$produce", "$this$consume$iv$iv"}, s = {"L$0", "L$2", "L$0", "L$2", "L$0", "L$2"})
    static final class C00931<R> extends SuspendLambda implements Function2<ProducerScope<? super R>, Continuation<? super Unit>, Object> {
        final /* synthetic */ ReceiveChannel<E> $this_map;
        final /* synthetic */ Function2<E, Continuation<? super R>, Object> $transform;
        private /* synthetic */ Object L$0;
        Object L$1;
        Object L$2;
        Object L$3;
        Object L$4;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        /* JADX WARN: Multi-variable type inference failed */
        C00931(ReceiveChannel<? extends E> receiveChannel, Function2<? super E, ? super Continuation<? super R>, ? extends Object> function2, Continuation<? super C00931> continuation) {
            super(2, continuation);
            this.$this_map = receiveChannel;
            this.$transform = function2;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            C00931 c00931 = new C00931(this.$this_map, this.$transform, continuation);
            c00931.L$0 = obj;
            return c00931;
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(ProducerScope<? super R> producerScope, Continuation<? super Unit> continuation) {
            return ((C00931) create(producerScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        /* JADX WARN: Code duplicated, block: B:23:0x00b8 A[RETURN] */
        /* JADX WARN: Code duplicated, block: B:24:0x00b9  */
        /* JADX WARN: Code duplicated, block: B:27:0x00ca A[Catch: all -> 0x0116, TryCatch #1 {all -> 0x0116, blocks: (B:25:0x00c2, B:27:0x00ca, B:31:0x00e8, B:36:0x010a), top: B:50:0x00c2 }] */
        /* JADX WARN: Code duplicated, block: B:29:0x00e2 A[RETURN] */
        /* JADX WARN: Code duplicated, block: B:30:0x00e3  */
        /* JADX WARN: Code duplicated, block: B:33:0x00fc A[RETURN] */
        /* JADX WARN: Code duplicated, block: B:34:0x00fd  */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:34:0x00fd -> B:35:0x0106). Please report as a decompilation issue!!! */
        /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
            jadx.core.utils.exceptions.JadxOverflowException: Regions stack size limit reached
            	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
            	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
            	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
            */
        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final java.lang.Object invokeSuspend(java.lang.Object r18) {
            /*
                Method dump skipped, instruction units count: 306
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt.C00931.invokeSuspend(java.lang.Object):java.lang.Object");
        }
    }

    public static final <E, R> ReceiveChannel<R> map(ReceiveChannel<? extends E> receiveChannel, CoroutineContext context, Function2<? super E, ? super Continuation<? super R>, ? extends Object> function2) {
        return ProduceKt.produce$default(GlobalScope.INSTANCE, context, 0, null, ChannelsKt.consumes(receiveChannel), new C00931(receiveChannel, function2, null), 6, null);
    }

    public static /* synthetic */ ReceiveChannel mapIndexed$default(ReceiveChannel receiveChannel, CoroutineContext coroutineContext, Function3 function3, int i, Object obj) {
        if ((i & 1) != 0) {
            coroutineContext = Dispatchers.getUnconfined();
        }
        return ChannelsKt.mapIndexed(receiveChannel, coroutineContext, function3);
    }

    /* JADX INFO: Add missing generic type declarations: [R] */
    /* JADX INFO: renamed from: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$mapIndexed$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: Deprecated.kt */
    @Metadata(d1 = {"\u0000\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\u0003*\b\u0012\u0004\u0012\u0002H\u00030\u0004H\u008a@"}, d2 = {"<anonymous>", "", "E", "R", "Lkotlinx/coroutines/channels/ProducerScope;"}, k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$mapIndexed$1", f = "Deprecated.kt", i = {0, 0, 1, 1, 2, 2}, l = {374, 375, 375}, m = "invokeSuspend", n = {"$this$produce", "index", "$this$produce", "index", "$this$produce", "index"}, s = {"L$0", "I$0", "L$0", "I$0", "L$0", "I$0"})
    static final class C00941<R> extends SuspendLambda implements Function2<ProducerScope<? super R>, Continuation<? super Unit>, Object> {
        final /* synthetic */ ReceiveChannel<E> $this_mapIndexed;
        final /* synthetic */ Function3<Integer, E, Continuation<? super R>, Object> $transform;
        int I$0;
        private /* synthetic */ Object L$0;
        Object L$1;
        Object L$2;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        /* JADX WARN: Multi-variable type inference failed */
        C00941(ReceiveChannel<? extends E> receiveChannel, Function3<? super Integer, ? super E, ? super Continuation<? super R>, ? extends Object> function3, Continuation<? super C00941> continuation) {
            super(2, continuation);
            this.$this_mapIndexed = receiveChannel;
            this.$transform = function3;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            C00941 c00941 = new C00941(this.$this_mapIndexed, this.$transform, continuation);
            c00941.L$0 = obj;
            return c00941;
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(ProducerScope<? super R> producerScope, Continuation<? super Unit> continuation) {
            return ((C00941) create(producerScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        /* JADX WARN: Code duplicated, block: B:12:0x0072 A[RETURN] */
        /* JADX WARN: Code duplicated, block: B:13:0x0073  */
        /* JADX WARN: Code duplicated, block: B:16:0x0082  */
        /* JADX WARN: Code duplicated, block: B:18:0x009f A[RETURN] */
        /* JADX WARN: Code duplicated, block: B:19:0x00a0  */
        /* JADX WARN: Code duplicated, block: B:22:0x00b8 A[RETURN] */
        /* JADX WARN: Code duplicated, block: B:23:0x00b9  */
        /* JADX WARN: Type inference incomplete: some casts might be missing */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:23:0x00b9 -> B:10:0x0060). Please report as a decompilation issue!!! */
        /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
            jadx.core.utils.exceptions.JadxOverflowException: Regions stack size limit reached
            	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
            	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
            	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
            */
        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final java.lang.Object invokeSuspend(java.lang.Object r11) {
            /*
                Method dump skipped, instruction units count: 206
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt.C00941.invokeSuspend(java.lang.Object):java.lang.Object");
        }
    }

    public static final <E, R> ReceiveChannel<R> mapIndexed(ReceiveChannel<? extends E> receiveChannel, CoroutineContext context, Function3<? super Integer, ? super E, ? super Continuation<? super R>, ? extends Object> function3) {
        return ProduceKt.produce$default(GlobalScope.INSTANCE, context, 0, null, ChannelsKt.consumes(receiveChannel), new C00941(receiveChannel, function3, null), 6, null);
    }

    public static /* synthetic */ ReceiveChannel mapIndexedNotNull$default(ReceiveChannel receiveChannel, CoroutineContext coroutineContext, Function3 function3, int i, Object obj) {
        if ((i & 1) != 0) {
            coroutineContext = Dispatchers.getUnconfined();
        }
        return mapIndexedNotNull(receiveChannel, coroutineContext, function3);
    }

    @Deprecated(level = DeprecationLevel.HIDDEN, message = "Binary compatibility")
    public static final /* synthetic */ ReceiveChannel mapIndexedNotNull(ReceiveChannel $this$mapIndexedNotNull, CoroutineContext context, Function3 transform) {
        return ChannelsKt.filterNotNull(ChannelsKt.mapIndexed($this$mapIndexedNotNull, context, transform));
    }

    public static /* synthetic */ ReceiveChannel mapNotNull$default(ReceiveChannel receiveChannel, CoroutineContext coroutineContext, Function2 function2, int i, Object obj) {
        if ((i & 1) != 0) {
            coroutineContext = Dispatchers.getUnconfined();
        }
        return mapNotNull(receiveChannel, coroutineContext, function2);
    }

    @Deprecated(level = DeprecationLevel.HIDDEN, message = "Binary compatibility")
    public static final /* synthetic */ ReceiveChannel mapNotNull(ReceiveChannel $this$mapNotNull, CoroutineContext context, Function2 transform) {
        return ChannelsKt.filterNotNull(ChannelsKt.map($this$mapNotNull, context, transform));
    }

    /* JADX INFO: Add missing generic type declarations: [E] */
    /* JADX INFO: renamed from: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$withIndex$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: Deprecated.kt */
    @Metadata(d1 = {"\u0000\u0010\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00020\u00040\u0003H\u008a@"}, d2 = {"<anonymous>", "", "E", "Lkotlinx/coroutines/channels/ProducerScope;", "Lkotlin/collections/IndexedValue;"}, k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$withIndex$1", f = "Deprecated.kt", i = {0, 0, 1, 1}, l = {400, 401}, m = "invokeSuspend", n = {"$this$produce", "index", "$this$produce", "index"}, s = {"L$0", "I$0", "L$0", "I$0"})
    static final class C01051<E> extends SuspendLambda implements Function2<ProducerScope<? super IndexedValue<? extends E>>, Continuation<? super Unit>, Object> {
        final /* synthetic */ ReceiveChannel<E> $this_withIndex;
        int I$0;
        private /* synthetic */ Object L$0;
        Object L$1;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        /* JADX WARN: Multi-variable type inference failed */
        C01051(ReceiveChannel<? extends E> receiveChannel, Continuation<? super C01051> continuation) {
            super(2, continuation);
            this.$this_withIndex = receiveChannel;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            C01051 c01051 = new C01051(this.$this_withIndex, continuation);
            c01051.L$0 = obj;
            return c01051;
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(ProducerScope<? super IndexedValue<? extends E>> producerScope, Continuation<? super Unit> continuation) {
            return ((C01051) create(producerScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        /* JADX WARN: Code duplicated, block: B:11:0x005b A[RETURN] */
        /* JADX WARN: Code duplicated, block: B:12:0x005c  */
        /* JADX WARN: Code duplicated, block: B:15:0x006a  */
        /* JADX WARN: Code duplicated, block: B:17:0x0087 A[RETURN] */
        /* JADX WARN: Code duplicated, block: B:18:0x0088  */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:18:0x0088 -> B:9:0x0049). Please report as a decompilation issue!!! */
        /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
            jadx.core.utils.exceptions.JadxOverflowException: Regions stack size limit reached
            	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
            	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
            	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
            */
        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final java.lang.Object invokeSuspend(java.lang.Object r11) {
            /*
                r10 = this;
                java.lang.Object r0 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
                int r1 = r10.label
                switch(r1) {
                    case 0: goto L3a;
                    case 1: goto L25;
                    case 2: goto L11;
                    default: goto L9;
                }
            L9:
                java.lang.IllegalStateException r11 = new java.lang.IllegalStateException
                java.lang.String r0 = "call to 'resume' before 'invoke' with coroutine"
                r11.<init>(r0)
                throw r11
            L11:
                r1 = r10
                int r2 = r1.I$0
                java.lang.Object r3 = r1.L$1
                kotlinx.coroutines.channels.ChannelIterator r3 = (kotlinx.coroutines.channels.ChannelIterator) r3
                java.lang.Object r4 = r1.L$0
                kotlinx.coroutines.channels.ProducerScope r4 = (kotlinx.coroutines.channels.ProducerScope) r4
                kotlin.ResultKt.throwOnFailure(r11)
                r9 = r3
                r3 = r2
                r2 = r4
                r4 = r9
                goto L8d
            L25:
                r1 = r10
                int r2 = r1.I$0
                java.lang.Object r3 = r1.L$1
                kotlinx.coroutines.channels.ChannelIterator r3 = (kotlinx.coroutines.channels.ChannelIterator) r3
                java.lang.Object r4 = r1.L$0
                kotlinx.coroutines.channels.ProducerScope r4 = (kotlinx.coroutines.channels.ProducerScope) r4
                kotlin.ResultKt.throwOnFailure(r11)
                r5 = r4
                r4 = r3
                r3 = r2
                r2 = r1
                r1 = r0
                r0 = r11
                goto L62
            L3a:
                kotlin.ResultKt.throwOnFailure(r11)
                r1 = r10
                java.lang.Object r2 = r1.L$0
                kotlinx.coroutines.channels.ProducerScope r2 = (kotlinx.coroutines.channels.ProducerScope) r2
                r3 = 0
                kotlinx.coroutines.channels.ReceiveChannel<E> r4 = r1.$this_withIndex
                kotlinx.coroutines.channels.ChannelIterator r4 = r4.iterator()
            L49:
                r5 = r1
                kotlin.coroutines.Continuation r5 = (kotlin.coroutines.Continuation) r5
                r1.L$0 = r2
                r1.L$1 = r4
                r1.I$0 = r3
                r6 = 1
                r1.label = r6
                java.lang.Object r5 = r4.hasNext(r5)
                if (r5 != r0) goto L5c
                return r0
            L5c:
                r9 = r0
                r0 = r11
                r11 = r5
                r5 = r2
                r2 = r1
                r1 = r9
            L62:
                java.lang.Boolean r11 = (java.lang.Boolean) r11
                boolean r11 = r11.booleanValue()
                if (r11 == 0) goto L8e
                java.lang.Object r11 = r4.next()
                kotlin.collections.IndexedValue r6 = new kotlin.collections.IndexedValue
                int r7 = r3 + 1
                r6.<init>(r3, r11)
                r3 = r2
                kotlin.coroutines.Continuation r3 = (kotlin.coroutines.Continuation) r3
                r2.L$0 = r5
                r2.L$1 = r4
                r2.I$0 = r7
                r8 = 2
                r2.label = r8
                java.lang.Object r11 = r5.send(r6, r3)
                if (r11 != r1) goto L88
                return r1
            L88:
                r11 = r0
                r0 = r1
                r1 = r2
                r2 = r5
                r3 = r7
            L8d:
                goto L49
            L8e:
                kotlin.Unit r11 = kotlin.Unit.INSTANCE
                return r11
            */
            throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt.C01051.invokeSuspend(java.lang.Object):java.lang.Object");
        }
    }

    public static /* synthetic */ ReceiveChannel withIndex$default(ReceiveChannel receiveChannel, CoroutineContext coroutineContext, int i, Object obj) {
        if ((i & 1) != 0) {
            coroutineContext = Dispatchers.getUnconfined();
        }
        return withIndex(receiveChannel, coroutineContext);
    }

    @Deprecated(level = DeprecationLevel.HIDDEN, message = "Binary compatibility")
    public static final /* synthetic */ ReceiveChannel withIndex(ReceiveChannel $this$withIndex, CoroutineContext context) {
        return ProduceKt.produce$default(GlobalScope.INSTANCE, context, 0, null, ChannelsKt.consumes($this$withIndex), new C01051($this$withIndex, null), 6, null);
    }

    /* JADX INFO: Add missing generic type declarations: [E] */
    /* JADX INFO: renamed from: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$distinct$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: Deprecated.kt */
    @Metadata(d1 = {"\u0000\u0004\n\u0002\b\u0003\u0010\u0000\u001a\u0002H\u0001\"\u0004\b\u0000\u0010\u00012\u0006\u0010\u0002\u001a\u0002H\u0001H\u008a@"}, d2 = {"<anonymous>", "E", "it"}, k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$distinct$1", f = "Deprecated.kt", i = {}, l = {}, m = "invokeSuspend", n = {}, s = {})
    static final class C00751<E> extends SuspendLambda implements Function2<E, Continuation<? super E>, Object> {
        /* synthetic */ Object L$0;
        int label;

        C00751(Continuation<? super C00751> continuation) {
            super(2, continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            C00751 c00751 = new C00751(continuation);
            c00751.L$0 = obj;
            return c00751;
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(E e, Continuation<? super E> continuation) {
            return ((C00751) create(e, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) throws Throwable {
            IntrinsicsKt.getCOROUTINE_SUSPENDED();
            switch (this.label) {
                case 0:
                    ResultKt.throwOnFailure(obj);
                    Object it = this.L$0;
                    return it;
                default:
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }
        }
    }

    public static /* synthetic */ ReceiveChannel distinctBy$default(ReceiveChannel receiveChannel, CoroutineContext coroutineContext, Function2 function2, int i, Object obj) {
        if ((i & 1) != 0) {
            coroutineContext = Dispatchers.getUnconfined();
        }
        return ChannelsKt.distinctBy(receiveChannel, coroutineContext, function2);
    }

    /* JADX INFO: Add missing generic type declarations: [E] */
    /* JADX INFO: renamed from: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$distinctBy$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: Deprecated.kt */
    @Metadata(d1 = {"\u0000\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\u0003*\b\u0012\u0004\u0012\u0002H\u00020\u0004H\u008a@"}, d2 = {"<anonymous>", "", "E", "K", "Lkotlinx/coroutines/channels/ProducerScope;"}, k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$distinctBy$1", f = "Deprecated.kt", i = {0, 0, 1, 1, 1, 2, 2, 2}, l = {417, 418, 420}, m = "invokeSuspend", n = {"$this$produce", "keys", "$this$produce", "keys", "e", "$this$produce", "keys", "k"}, s = {"L$0", "L$1", "L$0", "L$1", "L$3", "L$0", "L$1", "L$3"})
    static final class C00761<E> extends SuspendLambda implements Function2<ProducerScope<? super E>, Continuation<? super Unit>, Object> {
        final /* synthetic */ Function2<E, Continuation<? super K>, Object> $selector;
        final /* synthetic */ ReceiveChannel<E> $this_distinctBy;
        private /* synthetic */ Object L$0;
        Object L$1;
        Object L$2;
        Object L$3;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        /* JADX WARN: Multi-variable type inference failed */
        C00761(ReceiveChannel<? extends E> receiveChannel, Function2<? super E, ? super Continuation<? super K>, ? extends Object> function2, Continuation<? super C00761> continuation) {
            super(2, continuation);
            this.$this_distinctBy = receiveChannel;
            this.$selector = function2;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            C00761 c00761 = new C00761(this.$this_distinctBy, this.$selector, continuation);
            c00761.L$0 = obj;
            return c00761;
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(ProducerScope<? super E> producerScope, Continuation<? super Unit> continuation) {
            return ((C00761) create(producerScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        /* JADX WARN: Code duplicated, block: B:12:0x0082 A[RETURN] */
        /* JADX WARN: Code duplicated, block: B:13:0x0083  */
        /* JADX WARN: Code duplicated, block: B:16:0x0093  */
        /* JADX WARN: Code duplicated, block: B:18:0x00aa A[RETURN] */
        /* JADX WARN: Code duplicated, block: B:19:0x00ab  */
        /* JADX WARN: Code duplicated, block: B:22:0x00b8  */
        /* JADX WARN: Code duplicated, block: B:24:0x00cc A[RETURN] */
        /* JADX WARN: Code duplicated, block: B:25:0x00cd  */
        /* JADX WARN: Code duplicated, block: B:27:0x00df  */
        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r4v10 */
        /* JADX WARN: Type inference failed for: r4v14, types: [java.lang.Object] */
        /* JADX WARN: Type inference failed for: r4v17 */
        /* JADX WARN: Type inference failed for: r4v2 */
        /* JADX WARN: Type inference failed for: r5v0 */
        /* JADX WARN: Type inference failed for: r5v11 */
        /* JADX WARN: Type inference failed for: r5v12 */
        /* JADX WARN: Type inference failed for: r5v15 */
        /* JADX WARN: Type inference failed for: r5v5 */
        /* JADX WARN: Type inference failed for: r5v6, types: [java.lang.Object] */
        /* JADX WARN: Type inference failed for: r6v0 */
        /* JADX WARN: Type inference failed for: r6v1, types: [java.lang.Object, kotlinx.coroutines.channels.ProducerScope] */
        /* JADX WARN: Type inference failed for: r6v4 */
        /* JADX WARN: Type inference failed for: r6v8, types: [java.util.Collection] */
        /* JADX WARN: Type inference incomplete: some casts might be missing */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:25:0x00cd -> B:26:0x00d5). Please report as a decompilation issue!!! */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:27:0x00df -> B:10:0x006d). Please report as a decompilation issue!!! */
        /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
            jadx.core.utils.exceptions.JadxOverflowException: Regions stack size limit reached
            	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
            	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
            	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
            */
        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final java.lang.Object invokeSuspend(java.lang.Object r11) {
            /*
                Method dump skipped, instruction units count: 246
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt.C00761.invokeSuspend(java.lang.Object):java.lang.Object");
        }
    }

    public static final <E, K> ReceiveChannel<E> distinctBy(ReceiveChannel<? extends E> receiveChannel, CoroutineContext context, Function2<? super E, ? super Continuation<? super K>, ? extends Object> function2) {
        return ProduceKt.produce$default(GlobalScope.INSTANCE, context, 0, null, ChannelsKt.consumes(receiveChannel), new C00761(receiveChannel, function2, null), 6, null);
    }

    public static final <E> Object toMutableSet(ReceiveChannel<? extends E> receiveChannel, Continuation<? super Set<E>> continuation) {
        return ChannelsKt.toCollection(receiveChannel, new LinkedHashSet(), continuation);
    }

    /* JADX WARN: Code duplicated, block: B:7:0x0014  */
    @Deprecated(level = DeprecationLevel.HIDDEN, message = "Binary compatibility")
    public static final /* synthetic */ Object any(ReceiveChannel $this$any, Continuation continuation) throws Throwable {
        AnonymousClass1 anonymousClass1;
        ReceiveChannel $this$consume$iv;
        Throwable cause$iv;
        Object objHasNext;
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
        Object $result = anonymousClass1.result;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        switch (anonymousClass1.label) {
            case 0:
                ResultKt.throwOnFailure($result);
                $this$consume$iv = $this$any;
                cause$iv = null;
                try {
                    ChannelIterator it = $this$consume$iv.iterator();
                    anonymousClass1.L$0 = $this$consume$iv;
                    anonymousClass1.label = 1;
                    objHasNext = it.hasNext(anonymousClass1);
                    if (objHasNext == coroutine_suspended) {
                        return coroutine_suspended;
                    }
                    ChannelsKt.cancelConsumed($this$consume$iv, cause$iv);
                    return objHasNext;
                } catch (Throwable th) {
                    e$iv = th;
                    Throwable cause$iv2 = e$iv;
                    try {
                        throw e$iv;
                    } catch (Throwable e$iv) {
                        ChannelsKt.cancelConsumed($this$consume$iv, cause$iv2);
                        throw e$iv;
                    }
                }
            case 1:
                $this$consume$iv = (ReceiveChannel) anonymousClass1.L$0;
                cause$iv = null;
                try {
                    ResultKt.throwOnFailure($result);
                    objHasNext = $result;
                    ChannelsKt.cancelConsumed($this$consume$iv, cause$iv);
                    return objHasNext;
                } catch (Throwable th2) {
                    e$iv = th2;
                    Throwable cause$iv3 = e$iv;
                    throw e$iv;
                }
            default:
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
    }

    /* JADX WARN: Code duplicated, block: B:22:0x0076 A[RETURN] */
    /* JADX WARN: Code duplicated, block: B:23:0x0077  */
    /* JADX WARN: Code duplicated, block: B:26:0x008a A[Catch: all -> 0x00ae, TryCatch #3 {all -> 0x00ae, blocks: (B:24:0x0082, B:26:0x008a, B:27:0x009e), top: B:45:0x0082 }] */
    /* JADX WARN: Code duplicated, block: B:7:0x0014  */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:23:0x0077 -> B:45:0x0082). Please report as a decompilation issue!!! */
    /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxOverflowException: Regions stack size limit reached
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
        	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
        	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
        */
    @kotlin.Deprecated(level = kotlin.DeprecationLevel.HIDDEN, message = "Binary compatibility")
    public static final /* synthetic */ java.lang.Object count(kotlinx.coroutines.channels.ReceiveChannel r13, kotlin.coroutines.Continuation r14) {
        /*
            Method dump skipped, instruction units count: 202
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt.count(kotlinx.coroutines.channels.ReceiveChannel, kotlin.coroutines.Continuation):java.lang.Object");
    }

    /* JADX WARN: Code duplicated, block: B:35:0x00ae A[RETURN] */
    /* JADX WARN: Code duplicated, block: B:36:0x00af  */
    /* JADX WARN: Code duplicated, block: B:39:0x00c1 A[Catch: all -> 0x00e3, TRY_LEAVE, TryCatch #0 {all -> 0x00e3, blocks: (B:37:0x00b9, B:39:0x00c1), top: B:58:0x00b9 }] */
    /* JADX WARN: Code duplicated, block: B:41:0x00cb  */
    /* JADX WARN: Code duplicated, block: B:42:0x00d5  */
    /* JADX WARN: Code duplicated, block: B:7:0x0014  */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:36:0x00af -> B:58:0x00b9). Please report as a decompilation issue!!! */
    /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxOverflowException: Regions stack size limit reached
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
        	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
        	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
        */
    @kotlin.Deprecated(level = kotlin.DeprecationLevel.HIDDEN, message = "Binary compatibility")
    public static final /* synthetic */ java.lang.Object maxWith(kotlinx.coroutines.channels.ReceiveChannel r10, java.util.Comparator r11, kotlin.coroutines.Continuation r12) {
        /*
            Method dump skipped, instruction units count: 260
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt.maxWith(kotlinx.coroutines.channels.ReceiveChannel, java.util.Comparator, kotlin.coroutines.Continuation):java.lang.Object");
    }

    /* JADX WARN: Code duplicated, block: B:35:0x00ae A[RETURN] */
    /* JADX WARN: Code duplicated, block: B:36:0x00af  */
    /* JADX WARN: Code duplicated, block: B:39:0x00c1 A[Catch: all -> 0x00e3, TRY_LEAVE, TryCatch #0 {all -> 0x00e3, blocks: (B:37:0x00b9, B:39:0x00c1), top: B:58:0x00b9 }] */
    /* JADX WARN: Code duplicated, block: B:41:0x00cb  */
    /* JADX WARN: Code duplicated, block: B:42:0x00d5  */
    /* JADX WARN: Code duplicated, block: B:7:0x0014  */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:36:0x00af -> B:58:0x00b9). Please report as a decompilation issue!!! */
    /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxOverflowException: Regions stack size limit reached
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
        	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
        	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
        */
    @kotlin.Deprecated(level = kotlin.DeprecationLevel.HIDDEN, message = "Binary compatibility")
    public static final /* synthetic */ java.lang.Object minWith(kotlinx.coroutines.channels.ReceiveChannel r10, java.util.Comparator r11, kotlin.coroutines.Continuation r12) {
        /*
            Method dump skipped, instruction units count: 260
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt.minWith(kotlinx.coroutines.channels.ReceiveChannel, java.util.Comparator, kotlin.coroutines.Continuation):java.lang.Object");
    }

    /* JADX WARN: Code duplicated, block: B:24:0x005d  */
    /* JADX WARN: Code duplicated, block: B:25:0x005e  */
    /* JADX WARN: Code duplicated, block: B:7:0x0014  */
    @Deprecated(level = DeprecationLevel.HIDDEN, message = "Binary compatibility")
    public static final /* synthetic */ Object none(ReceiveChannel $this$none, Continuation continuation) throws Throwable {
        C00971 c00971;
        ReceiveChannel $this$consume$iv;
        Throwable cause$iv;
        Object objHasNext;
        if (continuation instanceof C00971) {
            c00971 = (C00971) continuation;
            if ((c00971.label & Integer.MIN_VALUE) != 0) {
                c00971.label -= Integer.MIN_VALUE;
            } else {
                c00971 = new C00971(continuation);
            }
        } else {
            c00971 = new C00971(continuation);
        }
        Object $result = c00971.result;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        boolean z = true;
        switch (c00971.label) {
            case 0:
                ResultKt.throwOnFailure($result);
                $this$consume$iv = $this$none;
                cause$iv = null;
                try {
                    ChannelIterator it = $this$consume$iv.iterator();
                    c00971.L$0 = $this$consume$iv;
                    c00971.label = 1;
                    objHasNext = it.hasNext(c00971);
                    if (objHasNext == coroutine_suspended) {
                        return coroutine_suspended;
                    }
                    if (!((Boolean) objHasNext).booleanValue()) {
                        z = false;
                    }
                    Boolean boolBoxBoolean = Boxing.boxBoolean(z);
                    ChannelsKt.cancelConsumed($this$consume$iv, cause$iv);
                    return boolBoxBoolean;
                } catch (Throwable th) {
                    e$iv = th;
                    Throwable cause$iv2 = e$iv;
                    try {
                        throw e$iv;
                    } catch (Throwable e$iv) {
                        ChannelsKt.cancelConsumed($this$consume$iv, cause$iv2);
                        throw e$iv;
                    }
                }
            case 1:
                $this$consume$iv = (ReceiveChannel) c00971.L$0;
                cause$iv = null;
                try {
                    ResultKt.throwOnFailure($result);
                    objHasNext = $result;
                    if (!((Boolean) objHasNext).booleanValue()) {
                        z = false;
                    }
                    Boolean boolBoxBoolean2 = Boxing.boxBoolean(z);
                    ChannelsKt.cancelConsumed($this$consume$iv, cause$iv);
                    return boolBoxBoolean2;
                } catch (Throwable th2) {
                    e$iv = th2;
                    Throwable cause$iv3 = e$iv;
                    throw e$iv;
                }
            default:
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
    }

    /* JADX INFO: Add missing generic type declarations: [E] */
    /* JADX INFO: renamed from: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$requireNoNulls$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: Deprecated.kt */
    @Metadata(d1 = {"\u0000\n\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0000\u0010\u0000\u001a\u0002H\u0001\"\b\b\u0000\u0010\u0001*\u00020\u00022\b\u0010\u0003\u001a\u0004\u0018\u0001H\u0001H\u008a@"}, d2 = {"<anonymous>", "E", "", "it"}, k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$requireNoNulls$1", f = "Deprecated.kt", i = {}, l = {}, m = "invokeSuspend", n = {}, s = {})
    static final class C00981<E> extends SuspendLambda implements Function2<E, Continuation<? super E>, Object> {
        final /* synthetic */ ReceiveChannel<E> $this_requireNoNulls;
        /* synthetic */ Object L$0;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        /* JADX WARN: Multi-variable type inference failed */
        C00981(ReceiveChannel<? extends E> receiveChannel, Continuation<? super C00981> continuation) {
            super(2, continuation);
            this.$this_requireNoNulls = receiveChannel;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            C00981 c00981 = new C00981(this.$this_requireNoNulls, continuation);
            c00981.L$0 = obj;
            return c00981;
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(E e, Continuation<? super E> continuation) {
            return ((C00981) create(e, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) throws Throwable {
            IntrinsicsKt.getCOROUTINE_SUSPENDED();
            switch (this.label) {
                case 0:
                    ResultKt.throwOnFailure(obj);
                    Object it = this.L$0;
                    if (it != null) {
                        return it;
                    }
                    throw new IllegalArgumentException("null element found in " + this.$this_requireNoNulls + '.');
                default:
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }
        }
    }

    public static /* synthetic */ ReceiveChannel zip$default(ReceiveChannel receiveChannel, ReceiveChannel receiveChannel2, CoroutineContext coroutineContext, Function2 function2, int i, Object obj) {
        if ((i & 2) != 0) {
            coroutineContext = Dispatchers.getUnconfined();
        }
        return ChannelsKt.zip(receiveChannel, receiveChannel2, coroutineContext, function2);
    }

    /* JADX INFO: Add missing generic type declarations: [V] */
    /* JADX INFO: renamed from: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$zip$2, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: Deprecated.kt */
    @Metadata(d1 = {"\u0000\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\u0003\"\u0004\b\u0002\u0010\u0004*\b\u0012\u0004\u0012\u0002H\u00040\u0005H\u008a@"}, d2 = {"<anonymous>", "", "E", "R", "V", "Lkotlinx/coroutines/channels/ProducerScope;"}, k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt$zip$2", f = "Deprecated.kt", i = {0, 0, 0, 1, 1, 1, 1, 2, 2, 2}, l = {517, 499, 501}, m = "invokeSuspend", n = {"$this$produce", "otherIterator", "$this$consume$iv$iv", "$this$produce", "otherIterator", "$this$consume$iv$iv", "element1", "$this$produce", "otherIterator", "$this$consume$iv$iv"}, s = {"L$0", "L$1", "L$3", "L$0", "L$1", "L$3", "L$5", "L$0", "L$1", "L$3"})
    static final class C01072<V> extends SuspendLambda implements Function2<ProducerScope<? super V>, Continuation<? super Unit>, Object> {
        final /* synthetic */ ReceiveChannel<R> $other;
        final /* synthetic */ ReceiveChannel<E> $this_zip;
        final /* synthetic */ Function2<E, R, V> $transform;
        private /* synthetic */ Object L$0;
        Object L$1;
        Object L$2;
        Object L$3;
        Object L$4;
        Object L$5;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        /* JADX WARN: Multi-variable type inference failed */
        C01072(ReceiveChannel<? extends R> receiveChannel, ReceiveChannel<? extends E> receiveChannel2, Function2<? super E, ? super R, ? extends V> function2, Continuation<? super C01072> continuation) {
            super(2, continuation);
            this.$other = receiveChannel;
            this.$this_zip = receiveChannel2;
            this.$transform = function2;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            C01072 c01072 = new C01072(this.$other, this.$this_zip, this.$transform, continuation);
            c01072.L$0 = obj;
            return c01072;
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(ProducerScope<? super V> producerScope, Continuation<? super Unit> continuation) {
            return ((C01072) create(producerScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        /* JADX WARN: Code duplicated, block: B:24:0x00d1 A[RETURN] */
        /* JADX WARN: Code duplicated, block: B:25:0x00d2  */
        /* JADX WARN: Code duplicated, block: B:28:0x00e8 A[Catch: all -> 0x0167, TRY_LEAVE, TryCatch #4 {all -> 0x0167, blocks: (B:26:0x00e0, B:28:0x00e8, B:34:0x0112, B:43:0x015b), top: B:63:0x00e0 }] */
        /* JADX WARN: Code duplicated, block: B:30:0x0102 A[RETURN] */
        /* JADX WARN: Code duplicated, block: B:31:0x0103  */
        /* JADX WARN: Code duplicated, block: B:34:0x0112 A[Catch: all -> 0x0167, TRY_ENTER, TryCatch #4 {all -> 0x0167, blocks: (B:26:0x00e0, B:28:0x00e8, B:34:0x0112, B:43:0x015b), top: B:63:0x00e0 }] */
        /* JADX WARN: Code duplicated, block: B:36:0x0132 A[RETURN] */
        /* JADX WARN: Code duplicated, block: B:37:0x0133  */
        /* JADX WARN: Code duplicated, block: B:39:0x0142  */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:37:0x0133 -> B:38:0x013d). Please report as a decompilation issue!!! */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:39:0x0142 -> B:40:0x014f). Please report as a decompilation issue!!! */
        /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
            jadx.core.utils.exceptions.JadxOverflowException: Regions stack size limit reached
            	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
            	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
            	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
            */
        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final java.lang.Object invokeSuspend(java.lang.Object r18) {
            /*
                Method dump skipped, instruction units count: 390
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt.C01072.invokeSuspend(java.lang.Object):java.lang.Object");
        }
    }

    public static final <E, R, V> ReceiveChannel<V> zip(ReceiveChannel<? extends E> receiveChannel, ReceiveChannel<? extends R> receiveChannel2, CoroutineContext context, Function2<? super E, ? super R, ? extends V> function2) {
        return ProduceKt.produce$default(GlobalScope.INSTANCE, context, 0, null, ChannelsKt.consumesAll(receiveChannel, receiveChannel2), new C01072(receiveChannel2, receiveChannel, function2, null), 6, null);
    }

    public static final Function1<Throwable, Unit> consumes(final ReceiveChannel<?> receiveChannel) {
        return new Function1<Throwable, Unit>() { // from class: kotlinx.coroutines.channels.ChannelsKt__DeprecatedKt.consumes.1
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(1);
            }

            @Override // kotlin.jvm.functions.Function1
            public /* bridge */ /* synthetic */ Unit invoke(Throwable th) {
                invoke2(th);
                return Unit.INSTANCE;
            }

            /* JADX INFO: renamed from: invoke, reason: avoid collision after fix types in other method */
            public final void invoke2(Throwable cause) {
                ChannelsKt.cancelConsumed(receiveChannel, cause);
            }
        };
    }
}
