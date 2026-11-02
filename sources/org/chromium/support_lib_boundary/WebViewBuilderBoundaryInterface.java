package org.chromium.support_lib_boundary;

import android.content.Context;
import android.webkit.WebView;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.jspecify.annotations.NullMarked;

/* JADX INFO: loaded from: classes.dex */
@NullMarked
public interface WebViewBuilderBoundaryInterface {

    @Retention(RetentionPolicy.SOURCE)
    public @interface Baseline {
        public static final int DEFAULT = 0;
    }

    @Target({ElementType.TYPE_USE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ConfigField {
        public static final int BASELINE = 0;
        public static final int JAVASCRIPT_INTERFACE = 1;
    }

    WebView build(Context context, Consumer<BiConsumer<Integer, Object>> consumer);

    public static class Config implements Consumer<BiConsumer<Integer, Object>> {
        public int baseline = 0;
        List<Object> mJavascriptInterfaceObjects = new ArrayList();
        List<String> mJavascriptInterfaceNames = new ArrayList();
        List<List<String>> mJavascriptInterfaceSitePatterns = new ArrayList();

        public void addJavascriptInterface(Object object, String name, List<String> sitePatterns) {
            this.mJavascriptInterfaceObjects.add(object);
            this.mJavascriptInterfaceNames.add(name);
            this.mJavascriptInterfaceSitePatterns.add(sitePatterns);
        }

        @Override // java.util.function.Consumer
        public void accept(BiConsumer<Integer, Object> chromiumConfig) {
            chromiumConfig.accept(0, Integer.valueOf(this.baseline));
            chromiumConfig.accept(1, new Object[]{this.mJavascriptInterfaceObjects, this.mJavascriptInterfaceNames, this.mJavascriptInterfaceSitePatterns});
        }
    }
}
