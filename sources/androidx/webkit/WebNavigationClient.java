package androidx.webkit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/* JADX INFO: loaded from: classes.dex */
public interface WebNavigationClient {

    @Target({ElementType.METHOD, ElementType.TYPE, ElementType.FIELD})
    @Retention(RetentionPolicy.CLASS)
    public @interface ExperimentalNavigationCallback {
    }

    void onFirstContentfulPaint(Page page);

    void onNavigationCompleted(Navigation navigation);

    void onNavigationRedirected(Navigation navigation);

    void onNavigationStarted(Navigation navigation);

    void onPageDeleted(Page page);

    void onPageDomContentLoadedEventFired(Page page);

    void onPageLoadEventFired(Page page);
}
