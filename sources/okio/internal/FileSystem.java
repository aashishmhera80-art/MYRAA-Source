package okio.internal;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import kotlin.ExceptionsKt;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.ArrayDeque;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.RestrictedSuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequenceScope;
import kotlin.sequences.SequencesKt;
import okio.BufferedSink;
import okio.FileMetadata;
import okio.Okio;
import okio.Path;
import okio.Source;

/* JADX INFO: renamed from: okio.internal.-FileSystem, reason: invalid class name */
/* JADX INFO: compiled from: FileSystem.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u00004\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\r\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u001aI\u0010\u0000\u001a\u00020\u0001*\b\u0012\u0004\u0012\u00020\u00030\u00022\u0006\u0010\u0004\u001a\u00020\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00030\u00072\u0006\u0010\b\u001a\u00020\u00032\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\nH\u0080@ø\u0001\u0000¢\u0006\u0002\u0010\f\u001a\u001c\u0010\r\u001a\u00020\u0001*\u00020\u00052\u0006\u0010\u000e\u001a\u00020\u00032\u0006\u0010\u000f\u001a\u00020\u0003H\u0000\u001a\u001c\u0010\u0010\u001a\u00020\u0001*\u00020\u00052\u0006\u0010\u0011\u001a\u00020\u00032\u0006\u0010\u0012\u001a\u00020\nH\u0000\u001a\u001c\u0010\u0013\u001a\u00020\u0001*\u00020\u00052\u0006\u0010\u0014\u001a\u00020\u00032\u0006\u0010\u0015\u001a\u00020\nH\u0000\u001a\u0014\u0010\u0016\u001a\u00020\n*\u00020\u00052\u0006\u0010\b\u001a\u00020\u0003H\u0000\u001a\"\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00030\u0018*\u00020\u00052\u0006\u0010\u0011\u001a\u00020\u00032\u0006\u0010\t\u001a\u00020\nH\u0000\u001a\u0014\u0010\u0019\u001a\u00020\u001a*\u00020\u00052\u0006\u0010\b\u001a\u00020\u0003H\u0000\u001a\u0016\u0010\u001b\u001a\u0004\u0018\u00010\u0003*\u00020\u00052\u0006\u0010\b\u001a\u00020\u0003H\u0000\u0082\u0002\u0004\n\u0002\b\u0019¨\u0006\u001c"}, d2 = {"collectRecursively", "", "Lkotlin/sequences/SequenceScope;", "Lokio/Path;", "fileSystem", "Lokio/FileSystem;", "stack", "Lkotlin/collections/ArrayDeque;", "path", "followSymlinks", "", "postorder", "(Lkotlin/sequences/SequenceScope;Lokio/FileSystem;Lkotlin/collections/ArrayDeque;Lokio/Path;ZZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "commonCopy", "source", "target", "commonCreateDirectories", "dir", "mustCreate", "commonDeleteRecursively", "fileOrDirectory", "mustExist", "commonExists", "commonListRecursively", "Lkotlin/sequences/Sequence;", "commonMetadata", "Lokio/FileMetadata;", "symlinkTarget", "okio"}, k = 2, mv = {1, 9, 0}, xi = 48)
public final class FileSystem {

    /* JADX INFO: renamed from: okio.internal.-FileSystem$collectRecursively$1, reason: invalid class name */
    /* JADX INFO: compiled from: FileSystem.kt */
    @Metadata(k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "okio.internal.-FileSystem", f = "FileSystem.kt", i = {0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1}, l = {116, 135, 145}, m = "collectRecursively", n = {"$this$collectRecursively", "fileSystem", "stack", "path", "followSymlinks", "postorder", "$this$collectRecursively", "fileSystem", "stack", "path", "followSymlinks", "postorder"}, s = {"L$0", "L$1", "L$2", "L$3", "Z$0", "Z$1", "L$0", "L$1", "L$2", "L$3", "Z$0", "Z$1"})
    static final class AnonymousClass1 extends ContinuationImpl {
        Object L$0;
        Object L$1;
        Object L$2;
        Object L$3;
        Object L$4;
        boolean Z$0;
        boolean Z$1;
        int label;
        /* synthetic */ Object result;

        AnonymousClass1(Continuation<? super AnonymousClass1> continuation) {
            super(continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return FileSystem.collectRecursively(null, null, null, null, false, false, this);
        }
    }

    public static final FileMetadata commonMetadata(okio.FileSystem $this$commonMetadata, Path path) throws IOException {
        Intrinsics.checkNotNullParameter($this$commonMetadata, "<this>");
        Intrinsics.checkNotNullParameter(path, "path");
        FileMetadata fileMetadataMetadataOrNull = $this$commonMetadata.metadataOrNull(path);
        if (fileMetadataMetadataOrNull != null) {
            return fileMetadataMetadataOrNull;
        }
        throw new FileNotFoundException("no such file: " + path);
    }

    public static final boolean commonExists(okio.FileSystem $this$commonExists, Path path) throws IOException {
        Intrinsics.checkNotNullParameter($this$commonExists, "<this>");
        Intrinsics.checkNotNullParameter(path, "path");
        return $this$commonExists.metadataOrNull(path) != null;
    }

    public static final void commonCreateDirectories(okio.FileSystem $this$commonCreateDirectories, Path dir, boolean mustCreate) throws IOException {
        Intrinsics.checkNotNullParameter($this$commonCreateDirectories, "<this>");
        Intrinsics.checkNotNullParameter(dir, "dir");
        ArrayDeque<Path> directories = new ArrayDeque();
        for (Path path = dir; path != null && !$this$commonCreateDirectories.exists(path); path = path.parent()) {
            directories.addFirst(path);
        }
        if (mustCreate && directories.isEmpty()) {
            throw new IOException(dir + " already exists.");
        }
        for (Path toCreate : directories) {
            $this$commonCreateDirectories.createDirectory(toCreate);
        }
    }

    public static final void commonDeleteRecursively(okio.FileSystem $this$commonDeleteRecursively, Path fileOrDirectory, boolean mustExist) throws IOException {
        Intrinsics.checkNotNullParameter($this$commonDeleteRecursively, "<this>");
        Intrinsics.checkNotNullParameter(fileOrDirectory, "fileOrDirectory");
        Sequence sequence = SequencesKt.sequence(new FileSystem$commonDeleteRecursively$sequence$1($this$commonDeleteRecursively, fileOrDirectory, null));
        Iterator iterator = sequence.iterator();
        while (iterator.hasNext()) {
            Path toDelete = (Path) iterator.next();
            $this$commonDeleteRecursively.delete(toDelete, mustExist && !iterator.hasNext());
        }
    }

    /* JADX INFO: renamed from: okio.internal.-FileSystem$commonListRecursively$1, reason: invalid class name and case insensitive filesystem */
    /* JADX INFO: compiled from: FileSystem.kt */
    @Metadata(d1 = {"\u0000\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\b\u0012\u0004\u0012\u00020\u00030\u0002H\u008a@"}, d2 = {"<anonymous>", "", "Lkotlin/sequences/SequenceScope;", "Lokio/Path;"}, k = 3, mv = {1, 9, 0}, xi = 48)
    @DebugMetadata(c = "okio.internal.-FileSystem$commonListRecursively$1", f = "FileSystem.kt", i = {0, 0}, l = {96}, m = "invokeSuspend", n = {"$this$sequence", "stack"}, s = {"L$0", "L$1"})
    static final class C01361 extends RestrictedSuspendLambda implements Function2<SequenceScope<? super Path>, Continuation<? super Unit>, Object> {
        final /* synthetic */ Path $dir;
        final /* synthetic */ boolean $followSymlinks;
        final /* synthetic */ okio.FileSystem $this_commonListRecursively;
        private /* synthetic */ Object L$0;
        Object L$1;
        Object L$2;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        C01361(Path path, okio.FileSystem fileSystem, boolean z, Continuation<? super C01361> continuation) {
            super(2, continuation);
            this.$dir = path;
            this.$this_commonListRecursively = fileSystem;
            this.$followSymlinks = z;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            C01361 c01361 = new C01361(this.$dir, this.$this_commonListRecursively, this.$followSymlinks, continuation);
            c01361.L$0 = obj;
            return c01361;
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(SequenceScope<? super Path> sequenceScope, Continuation<? super Unit> continuation) {
            return ((C01361) create(sequenceScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object $result) throws Throwable {
            C01361 c01361;
            ArrayDeque stack;
            SequenceScope $this$sequence;
            Iterator<Path> it;
            Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            switch (this.label) {
                case 0:
                    ResultKt.throwOnFailure($result);
                    c01361 = this;
                    SequenceScope $this$sequence2 = (SequenceScope) c01361.L$0;
                    ArrayDeque stack2 = new ArrayDeque();
                    stack2.addLast(c01361.$dir);
                    stack = stack2;
                    $this$sequence = $this$sequence2;
                    it = c01361.$this_commonListRecursively.list(c01361.$dir).iterator();
                    break;
                case 1:
                    c01361 = this;
                    it = (Iterator) c01361.L$2;
                    ArrayDeque stack3 = (ArrayDeque) c01361.L$1;
                    SequenceScope $this$sequence3 = (SequenceScope) c01361.L$0;
                    ResultKt.throwOnFailure($result);
                    stack = stack3;
                    $this$sequence = $this$sequence3;
                    break;
                default:
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }
            while (it.hasNext()) {
                Path child = it.next();
                c01361.L$0 = $this$sequence;
                c01361.L$1 = stack;
                c01361.L$2 = it;
                c01361.label = 1;
                if (FileSystem.collectRecursively($this$sequence, c01361.$this_commonListRecursively, stack, child, c01361.$followSymlinks, false, c01361) == coroutine_suspended) {
                    return coroutine_suspended;
                }
            }
            return Unit.INSTANCE;
        }
    }

    public static final Sequence<Path> commonListRecursively(okio.FileSystem $this$commonListRecursively, Path dir, boolean followSymlinks) throws IOException {
        Intrinsics.checkNotNullParameter($this$commonListRecursively, "<this>");
        Intrinsics.checkNotNullParameter(dir, "dir");
        return SequencesKt.sequence(new C01361(dir, $this$commonListRecursively, followSymlinks, null));
    }

    /* JADX WARN: Code duplicated, block: B:27:0x009c  */
    /* JADX WARN: Code duplicated, block: B:30:0x00a9  */
    /* JADX WARN: Code duplicated, block: B:47:0x00ea A[Catch: all -> 0x013e, TryCatch #3 {all -> 0x013e, blocks: (B:45:0x00e4, B:47:0x00ea, B:53:0x00f9), top: B:84:0x00e4 }] */
    /* JADX WARN: Code duplicated, block: B:49:0x00f3  */
    /* JADX WARN: Code duplicated, block: B:50:0x00f5  */
    /* JADX WARN: Code duplicated, block: B:52:0x00f8  */
    /* JADX WARN: Code duplicated, block: B:59:0x0127 A[LOOP:0: B:84:0x00e4->B:59:0x0127, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:70:0x0149 A[LOOP:1: B:32:0x00ac->B:70:0x0149, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:72:0x0150  */
    /* JADX WARN: Code duplicated, block: B:74:0x0164 A[RETURN] */
    /* JADX WARN: Code duplicated, block: B:7:0x0016  */
    /* JADX WARN: Code duplicated, block: B:87:0x0126 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:90:0x00d4 A[SYNTHETIC] */
    /* JADX WARN: Instruction removed from duplicated block: B:47:0x00ea, please report this as an issue */
    public static final Object collectRecursively(SequenceScope<? super Path> sequenceScope, okio.FileSystem fileSystem, ArrayDeque<Path> arrayDeque, Path path, boolean z, boolean z2, Continuation<? super Unit> continuation) throws Throwable {
        AnonymousClass1 anonymousClass1;
        SequenceScope $this$collectRecursively;
        ArrayDeque<Path> arrayDeque2;
        boolean followSymlinks;
        okio.FileSystem fileSystem2;
        Path path2;
        boolean postorder;
        List<Path> listListOrNull;
        Path symlinkPath;
        int symlinkCount;
        Path pathSymlinkTarget;
        ArrayDeque<Path> arrayDeque3;
        SequenceScope $this$collectRecursively2;
        okio.FileSystem fileSystem3;
        Path path3;
        Iterator<Path> it;
        Path child;
        boolean z3;
        boolean z4;
        AnonymousClass1 anonymousClass2;
        ArrayDeque<Path> arrayDeque4;
        okio.FileSystem fileSystem4;
        SequenceScope $this$collectRecursively3;
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
                $this$collectRecursively = sequenceScope;
                arrayDeque2 = arrayDeque;
                followSymlinks = z;
                fileSystem2 = fileSystem;
                path2 = path;
                postorder = z2;
                if (!postorder) {
                    anonymousClass1.L$0 = $this$collectRecursively;
                    anonymousClass1.L$1 = fileSystem2;
                    anonymousClass1.L$2 = arrayDeque2;
                    anonymousClass1.L$3 = path2;
                    anonymousClass1.Z$0 = followSymlinks;
                    anonymousClass1.Z$1 = postorder;
                    anonymousClass1.label = 1;
                    if ($this$collectRecursively.yield(path2, anonymousClass1) == coroutine_suspended) {
                        return coroutine_suspended;
                    }
                }
                listListOrNull = fileSystem2.listOrNull(path2);
                if (listListOrNull == null) {
                    listListOrNull = CollectionsKt.emptyList();
                }
                if (!listListOrNull.isEmpty()) {
                    symlinkPath = path2;
                    symlinkCount = 0;
                    while (true) {
                        if (!followSymlinks && arrayDeque2.contains(symlinkPath)) {
                            throw new IOException("symlink cycle at " + path2);
                        }
                        pathSymlinkTarget = symlinkTarget(fileSystem2, symlinkPath);
                        if (pathSymlinkTarget == null) {
                            if (!followSymlinks || symlinkCount == 0) {
                                arrayDeque2.addLast(symlinkPath);
                                try {
                                    Iterator<Path> it2 = listListOrNull.iterator();
                                    $this$collectRecursively2 = $this$collectRecursively;
                                    fileSystem3 = fileSystem2;
                                    path3 = path2;
                                    it = it2;
                                    while (it.hasNext()) {
                                        try {
                                            child = it.next();
                                            if (followSymlinks) {
                                                z3 = true;
                                            } else {
                                                z3 = false;
                                            }
                                            z4 = postorder;
                                            anonymousClass1.L$0 = $this$collectRecursively2;
                                            anonymousClass1.L$1 = fileSystem3;
                                            anonymousClass1.L$2 = arrayDeque2;
                                            anonymousClass1.L$3 = path3;
                                            anonymousClass1.L$4 = it;
                                            anonymousClass1.Z$0 = followSymlinks;
                                            anonymousClass1.Z$1 = postorder;
                                            anonymousClass1.label = 2;
                                            anonymousClass2 = anonymousClass1;
                                            arrayDeque4 = arrayDeque2;
                                            fileSystem4 = fileSystem3;
                                            $this$collectRecursively3 = $this$collectRecursively2;
                                            try {
                                                if (collectRecursively($this$collectRecursively3, fileSystem4, arrayDeque4, child, z3, z4, anonymousClass2) == coroutine_suspended) {
                                                    return coroutine_suspended;
                                                }
                                                anonymousClass1 = anonymousClass2;
                                                arrayDeque2 = arrayDeque4;
                                                fileSystem3 = fileSystem4;
                                                $this$collectRecursively2 = $this$collectRecursively3;
                                            } catch (Throwable th) {
                                                th = th;
                                                arrayDeque3 = arrayDeque4;
                                                arrayDeque3.removeLast();
                                                throw th;
                                            }
                                        } catch (Throwable th2) {
                                            th = th2;
                                            arrayDeque3 = arrayDeque2;
                                        }
                                    }
                                    arrayDeque2.removeLast();
                                    path2 = path3;
                                    $this$collectRecursively = $this$collectRecursively2;
                                } catch (Throwable th3) {
                                    th = th3;
                                    arrayDeque3 = arrayDeque2;
                                    arrayDeque3.removeLast();
                                    throw th;
                                }
                            }
                            return Unit.INSTANCE;
                        }
                        symlinkPath = pathSymlinkTarget;
                        symlinkCount++;
                    }
                }
                if (postorder) {
                    anonymousClass1.L$0 = null;
                    anonymousClass1.L$1 = null;
                    anonymousClass1.L$2 = null;
                    anonymousClass1.L$3 = null;
                    anonymousClass1.L$4 = null;
                    anonymousClass1.label = 3;
                    if ($this$collectRecursively.yield(path2, anonymousClass1) == coroutine_suspended) {
                        return coroutine_suspended;
                    }
                }
                return Unit.INSTANCE;
            case 1:
                postorder = anonymousClass1.Z$1;
                followSymlinks = anonymousClass1.Z$0;
                path2 = (Path) anonymousClass1.L$3;
                arrayDeque2 = (ArrayDeque) anonymousClass1.L$2;
                fileSystem2 = (okio.FileSystem) anonymousClass1.L$1;
                $this$collectRecursively = (SequenceScope) anonymousClass1.L$0;
                ResultKt.throwOnFailure($result);
                listListOrNull = fileSystem2.listOrNull(path2);
                if (listListOrNull == null) {
                    listListOrNull = CollectionsKt.emptyList();
                }
                if (!listListOrNull.isEmpty()) {
                    symlinkPath = path2;
                    symlinkCount = 0;
                    while (true) {
                        if (!followSymlinks) {
                        }
                        pathSymlinkTarget = symlinkTarget(fileSystem2, symlinkPath);
                        if (pathSymlinkTarget == null) {
                            if (!followSymlinks) {
                            }
                            arrayDeque2.addLast(symlinkPath);
                            Iterator<Path> it3 = listListOrNull.iterator();
                            $this$collectRecursively2 = $this$collectRecursively;
                            fileSystem3 = fileSystem2;
                            path3 = path2;
                            it = it3;
                            while (it.hasNext()) {
                                child = it.next();
                                if (followSymlinks) {
                                    z3 = true;
                                } else {
                                    z3 = false;
                                }
                                if (postorder) {
                                }
                                anonymousClass1.L$0 = $this$collectRecursively2;
                                anonymousClass1.L$1 = fileSystem3;
                                anonymousClass1.L$2 = arrayDeque2;
                                anonymousClass1.L$3 = path3;
                                anonymousClass1.L$4 = it;
                                anonymousClass1.Z$0 = followSymlinks;
                                anonymousClass1.Z$1 = postorder;
                                anonymousClass1.label = 2;
                                anonymousClass2 = anonymousClass1;
                                arrayDeque4 = arrayDeque2;
                                fileSystem4 = fileSystem3;
                                $this$collectRecursively3 = $this$collectRecursively2;
                                if (collectRecursively($this$collectRecursively3, fileSystem4, arrayDeque4, child, z3, z4, anonymousClass2) == coroutine_suspended) {
                                    return coroutine_suspended;
                                }
                                anonymousClass1 = anonymousClass2;
                                arrayDeque2 = arrayDeque4;
                                fileSystem3 = fileSystem4;
                                $this$collectRecursively2 = $this$collectRecursively3;
                            }
                            arrayDeque2.removeLast();
                            path2 = path3;
                            $this$collectRecursively = $this$collectRecursively2;
                        } else {
                            symlinkPath = pathSymlinkTarget;
                            symlinkCount++;
                        }
                        break;
                    }
                }
                if (postorder) {
                    anonymousClass1.L$0 = null;
                    anonymousClass1.L$1 = null;
                    anonymousClass1.L$2 = null;
                    anonymousClass1.L$3 = null;
                    anonymousClass1.L$4 = null;
                    anonymousClass1.label = 3;
                    if ($this$collectRecursively.yield(path2, anonymousClass1) == coroutine_suspended) {
                        return coroutine_suspended;
                    }
                }
                return Unit.INSTANCE;
            case 2:
                postorder = anonymousClass1.Z$1;
                followSymlinks = anonymousClass1.Z$0;
                it = (Iterator) anonymousClass1.L$4;
                Path path4 = (Path) anonymousClass1.L$3;
                arrayDeque3 = (ArrayDeque) anonymousClass1.L$2;
                fileSystem3 = (okio.FileSystem) anonymousClass1.L$1;
                $this$collectRecursively2 = (SequenceScope) anonymousClass1.L$0;
                try {
                    ResultKt.throwOnFailure($result);
                    path3 = path4;
                    arrayDeque2 = arrayDeque3;
                    while (it.hasNext()) {
                        child = it.next();
                        if (followSymlinks) {
                            z3 = true;
                        } else {
                            z3 = false;
                        }
                        if (postorder) {
                        }
                        anonymousClass1.L$0 = $this$collectRecursively2;
                        anonymousClass1.L$1 = fileSystem3;
                        anonymousClass1.L$2 = arrayDeque2;
                        anonymousClass1.L$3 = path3;
                        anonymousClass1.L$4 = it;
                        anonymousClass1.Z$0 = followSymlinks;
                        anonymousClass1.Z$1 = postorder;
                        anonymousClass1.label = 2;
                        anonymousClass2 = anonymousClass1;
                        arrayDeque4 = arrayDeque2;
                        fileSystem4 = fileSystem3;
                        $this$collectRecursively3 = $this$collectRecursively2;
                        if (collectRecursively($this$collectRecursively3, fileSystem4, arrayDeque4, child, z3, z4, anonymousClass2) == coroutine_suspended) {
                            return coroutine_suspended;
                        }
                        anonymousClass1 = anonymousClass2;
                        arrayDeque2 = arrayDeque4;
                        fileSystem3 = fileSystem4;
                        $this$collectRecursively2 = $this$collectRecursively3;
                    }
                    arrayDeque2.removeLast();
                    path2 = path3;
                    $this$collectRecursively = $this$collectRecursively2;
                    if (postorder) {
                        anonymousClass1.L$0 = null;
                        anonymousClass1.L$1 = null;
                        anonymousClass1.L$2 = null;
                        anonymousClass1.L$3 = null;
                        anonymousClass1.L$4 = null;
                        anonymousClass1.label = 3;
                        if ($this$collectRecursively.yield(path2, anonymousClass1) == coroutine_suspended) {
                            return coroutine_suspended;
                        }
                    }
                    return Unit.INSTANCE;
                } catch (Throwable th4) {
                    th = th4;
                    arrayDeque3.removeLast();
                    throw th;
                }
            case 3:
                ResultKt.throwOnFailure($result);
                return Unit.INSTANCE;
            default:
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
    }

    public static final Path symlinkTarget(okio.FileSystem $this$symlinkTarget, Path path) throws IOException {
        Intrinsics.checkNotNullParameter($this$symlinkTarget, "<this>");
        Intrinsics.checkNotNullParameter(path, "path");
        Path target = $this$symlinkTarget.metadata(path).getSymlinkTarget();
        if (target == null) {
            return null;
        }
        Path pathParent = path.parent();
        Intrinsics.checkNotNull(pathParent);
        return pathParent.resolve(target);
    }

    public static final void commonCopy(okio.FileSystem $this$commonCopy, Path source, Path target) throws IOException {
        Intrinsics.checkNotNullParameter($this$commonCopy, "<this>");
        Intrinsics.checkNotNullParameter(source, "source");
        Intrinsics.checkNotNullParameter(target, "target");
        Closeable $this$use$iv = $this$commonCopy.source(source);
        Object result$iv = null;
        Throwable thrown$iv = null;
        try {
            Source bytesIn = (Source) $this$use$iv;
            Closeable $this$use$iv2 = Okio.buffer($this$commonCopy.sink(target));
            Object result$iv2 = null;
            Throwable thrown$iv2 = null;
            try {
                BufferedSink bytesOut = (BufferedSink) $this$use$iv2;
                result$iv2 = Long.valueOf(bytesOut.writeAll(bytesIn));
                if ($this$use$iv2 != null) {
                    try {
                        $this$use$iv2.close();
                    } catch (Throwable t$iv) {
                        thrown$iv2 = t$iv;
                    }
                }
            } catch (Throwable t$iv2) {
                thrown$iv2 = t$iv2;
                if ($this$use$iv2 != null) {
                    try {
                        $this$use$iv2.close();
                    } catch (Throwable t$iv3) {
                        ExceptionsKt.addSuppressed(thrown$iv2, t$iv3);
                    }
                }
            }
            if (thrown$iv2 != null) {
                throw thrown$iv2;
            }
            Intrinsics.checkNotNull(result$iv2);
            result$iv = Long.valueOf(((Number) result$iv2).longValue());
            if ($this$use$iv != null) {
                try {
                    $this$use$iv.close();
                } catch (Throwable t$iv4) {
                    thrown$iv = t$iv4;
                }
            }
            if (thrown$iv != null) {
                throw thrown$iv;
            }
            Intrinsics.checkNotNull(result$iv);
        } catch (Throwable t$iv5) {
            thrown$iv = t$iv5;
            if ($this$use$iv != null) {
                try {
                    $this$use$iv.close();
                } catch (Throwable t$iv6) {
                    ExceptionsKt.addSuppressed(thrown$iv, t$iv6);
                }
            }
        }
    }
}
