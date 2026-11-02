package com.getcapacitor.plugin;

import android.R;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.webkit.WebViewCompat;
import com.getcapacitor.Logger;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.WebViewListener;
import com.getcapacitor.annotation.CapacitorPlugin;
import java.util.Locale;

/* JADX INFO: loaded from: classes4.dex */
@CapacitorPlugin
public class SystemBars extends Plugin {
    static final String BAR_GESTURE_BAR = "NavigationBar";
    static final String BAR_STATUS_BAR = "StatusBar";
    static final String INSETS_HANDLING_CSS = "css";
    static final String INSETS_HANDLING_DISABLE = "disable";
    static final String STYLE_DARK = "DARK";
    static final String STYLE_DEFAULT = "DEFAULT";
    static final String STYLE_LIGHT = "LIGHT";
    private static final int WEBVIEW_VERSION_WITH_SAFE_AREA_FIX = 140;
    private static final int WEBVIEW_VERSION_WITH_SAFE_AREA_KEYBOARD_FIX = 144;
    static final String viewportMetaJSFunction = "function capacitorSystemBarsCheckMetaViewport() {\n    const meta = document.querySelectorAll(\"meta[name=viewport]\");\n    if (meta.length == 0) {\n        return false;\n    }\n    // get the last found meta viewport tag\n    const metaContent = meta[meta.length - 1].content;\n    return metaContent.includes(\"viewport-fit=cover\");\n}\ncapacitorSystemBarsCheckMetaViewport();\n";
    private String insetsHandling = INSETS_HANDLING_CSS;
    private boolean hasViewportCover = false;
    private String currentStatusBarStyle = STYLE_DEFAULT;
    private String currentGestureBarStyle = STYLE_DEFAULT;

    @Override // com.getcapacitor.Plugin
    public void load() {
        getBridge().getWebView().addJavascriptInterface(this, "CapacitorSystemBarsAndroidInterface");
        super.load();
        initSystemBars();
    }

    @Override // com.getcapacitor.Plugin
    protected void handleOnStart() {
        super.handleOnStart();
        getBridge().addWebViewListener(new WebViewListener() { // from class: com.getcapacitor.plugin.SystemBars.1
            @Override // com.getcapacitor.WebViewListener
            public void onPageCommitVisible(android.webkit.WebView view, String url) {
                super.onPageCommitVisible(view, url);
                SystemBars.this.getBridge().getWebView().requestApplyInsets();
            }
        });
    }

    @Override // com.getcapacitor.Plugin
    protected void handleOnConfigurationChanged(Configuration newConfig) {
        super.handleOnConfigurationChanged(newConfig);
        setStyle(this.currentGestureBarStyle, BAR_GESTURE_BAR);
        setStyle(this.currentStatusBarStyle, BAR_STATUS_BAR);
    }

    private void initSystemBars() {
        final String style = getConfig().getString("style", STYLE_DEFAULT).toUpperCase(Locale.US);
        final boolean hidden = getConfig().getBoolean("hidden", false);
        String configuredInsetsHandling = getConfig().getString("insetsHandling", INSETS_HANDLING_CSS);
        if (INSETS_HANDLING_CSS.equals(configuredInsetsHandling) || INSETS_HANDLING_DISABLE.equals(configuredInsetsHandling)) {
            this.insetsHandling = configuredInsetsHandling;
        } else {
            Logger.warn("SystemBars", "Unknown insetsHandling value '" + configuredInsetsHandling + "'. Falling back to '" + INSETS_HANDLING_CSS + "'.");
            this.insetsHandling = INSETS_HANDLING_CSS;
        }
        initWindowInsetsListener();
        initSafeAreaCSSVariables();
        getBridge().executeOnMainThread(new Runnable() { // from class: com.getcapacitor.plugin.SystemBars$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$initSystemBars$0(style, hidden);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initSystemBars$0(String style, boolean hidden) {
        setStyle(style, "");
        setHidden(hidden, "");
    }

    @PluginMethod
    public void setStyle(final PluginCall call) {
        final String bar = call.getString("bar", "");
        final String style = call.getString("style", STYLE_DEFAULT);
        getBridge().executeOnMainThread(new Runnable() { // from class: com.getcapacitor.plugin.SystemBars$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$setStyle$1(style, bar, call);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setStyle$1(String style, String bar, PluginCall call) {
        setStyle(style, bar);
        call.resolve();
    }

    @PluginMethod
    public void show(final PluginCall call) {
        final String bar = call.getString("bar", "");
        getBridge().executeOnMainThread(new Runnable() { // from class: com.getcapacitor.plugin.SystemBars$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$show$2(bar, call);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$show$2(String bar, PluginCall call) {
        setHidden(false, bar);
        call.resolve();
    }

    @PluginMethod
    public void hide(final PluginCall call) {
        final String bar = call.getString("bar", "");
        getBridge().executeOnMainThread(new Runnable() { // from class: com.getcapacitor.plugin.SystemBars$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$hide$3(bar, call);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$hide$3(String bar, PluginCall call) {
        setHidden(true, bar);
        call.resolve();
    }

    @PluginMethod
    public void setAnimation(PluginCall call) {
        call.resolve();
    }

    @JavascriptInterface
    public void onDOMReady() {
        if (INSETS_HANDLING_CSS.equals(this.insetsHandling)) {
            getActivity().runOnUiThread(new Runnable() { // from class: com.getcapacitor.plugin.SystemBars$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onDOMReady$5();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onDOMReady$5() {
        this.bridge.getWebView().evaluateJavascript(viewportMetaJSFunction, new ValueCallback() { // from class: com.getcapacitor.plugin.SystemBars$$ExternalSyntheticLambda7
            @Override // android.webkit.ValueCallback
            public final void onReceiveValue(Object obj) {
                this.f$0.lambda$onDOMReady$4((String) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onDOMReady$4(String res) {
        this.hasViewportCover = res.equals("true");
        getBridge().getWebView().requestApplyInsets();
    }

    private Insets calcSafeAreaInsets(WindowInsetsCompat insets) {
        Insets safeArea = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
        if (insets.isVisible(WindowInsetsCompat.Type.ime())) {
            return Insets.of(safeArea.left, safeArea.top, safeArea.right, 0);
        }
        return Insets.of(safeArea.left, safeArea.top, safeArea.right, safeArea.bottom);
    }

    private void initSafeAreaCSSVariables() {
        WindowInsetsCompat insets;
        if (INSETS_HANDLING_CSS.equals(this.insetsHandling)) {
            if (Build.VERSION.SDK_INT >= 35) {
                View v = (View) getBridge().getWebView().getParent();
                insets = ViewCompat.getRootWindowInsets(v);
            } else {
                insets = WindowInsetsCompat.CONSUMED;
            }
            if (insets != null) {
                Insets safeAreaInsets = calcSafeAreaInsets(insets);
                injectSafeAreaCSS(safeAreaInsets.top, safeAreaInsets.right, safeAreaInsets.bottom, safeAreaInsets.left);
            }
        }
    }

    private void initWindowInsetsListener() {
        if (INSETS_HANDLING_DISABLE.equals(this.insetsHandling)) {
            return;
        }
        ViewCompat.setOnApplyWindowInsetsListener((View) getBridge().getWebView().getParent(), new OnApplyWindowInsetsListener() { // from class: com.getcapacitor.plugin.SystemBars$$ExternalSyntheticLambda1
            @Override // androidx.core.view.OnApplyWindowInsetsListener
            public final WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat) {
                return this.f$0.lambda$initWindowInsetsListener$6(view, windowInsetsCompat);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ WindowInsetsCompat lambda$initWindowInsetsListener$6(View v, WindowInsetsCompat insets) {
        boolean shouldPassthroughInsets = getWebViewMajorVersion().intValue() >= WEBVIEW_VERSION_WITH_SAFE_AREA_FIX && this.hasViewportCover;
        Insets systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
        Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());
        boolean keyboardVisible = insets.isVisible(WindowInsetsCompat.Type.ime());
        if (shouldPassthroughInsets) {
            v.setPadding(0, 0, 0, keyboardVisible ? imeInsets.bottom : 0);
            Insets safeAreaInsets = calcSafeAreaInsets(insets);
            injectSafeAreaCSS(safeAreaInsets.top, safeAreaInsets.right, safeAreaInsets.bottom, safeAreaInsets.left);
            return new WindowInsetsCompat.Builder(insets).setInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout(), Insets.of(systemBarsInsets.left, systemBarsInsets.top, systemBarsInsets.right, getBottomInset(systemBarsInsets, keyboardVisible))).build();
        }
        if (Build.VERSION.SDK_INT >= 35) {
            v.setPadding(systemBarsInsets.left, systemBarsInsets.top, systemBarsInsets.right, keyboardVisible ? imeInsets.bottom : systemBarsInsets.bottom);
        }
        WindowInsetsCompat newInsets = new WindowInsetsCompat.Builder(insets).setInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout(), Insets.of(0, 0, 0, 0)).build();
        Insets safeAreaInsets2 = calcSafeAreaInsets(newInsets);
        injectSafeAreaCSS(safeAreaInsets2.top, safeAreaInsets2.right, safeAreaInsets2.bottom, safeAreaInsets2.left);
        return newInsets;
    }

    private void injectSafeAreaCSS(int top, int right, int bottom, int left) {
        float density = getActivity().getResources().getDisplayMetrics().density;
        final float topPx = top / density;
        final float rightPx = right / density;
        final float bottomPx = bottom / density;
        final float leftPx = left / density;
        getBridge().executeOnMainThread(new Runnable() { // from class: com.getcapacitor.plugin.SystemBars$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$injectSafeAreaCSS$7(topPx, rightPx, bottomPx, leftPx);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$injectSafeAreaCSS$7(float topPx, float rightPx, float bottomPx, float leftPx) {
        if (this.bridge != null && this.bridge.getWebView() != null) {
            String script = String.format(Locale.US, "try {\n  document.documentElement.style.setProperty(\"--safe-area-inset-top\", \"%dpx\");\n  document.documentElement.style.setProperty(\"--safe-area-inset-right\", \"%dpx\");\n  document.documentElement.style.setProperty(\"--safe-area-inset-bottom\", \"%dpx\");\n  document.documentElement.style.setProperty(\"--safe-area-inset-left\", \"%dpx\");\n} catch(e) { console.error('Error injecting safe area CSS:', e); }\n", Integer.valueOf((int) topPx), Integer.valueOf((int) rightPx), Integer.valueOf((int) bottomPx), Integer.valueOf((int) leftPx));
            this.bridge.getWebView().evaluateJavascript(script, null);
        }
    }

    private void setStyle(String style, String bar) {
        if (style.equals(STYLE_DEFAULT)) {
            style = getStyleForTheme();
        }
        Window window = getActivity().getWindow();
        WindowInsetsControllerCompat windowInsetsControllerCompat = WindowCompat.getInsetsController(window, window.getDecorView());
        if (bar.isEmpty() || bar.equals(BAR_STATUS_BAR)) {
            this.currentStatusBarStyle = style;
            windowInsetsControllerCompat.setAppearanceLightStatusBars(!style.equals(STYLE_DARK));
        }
        if (bar.isEmpty() || bar.equals(BAR_GESTURE_BAR)) {
            this.currentGestureBarStyle = style;
            windowInsetsControllerCompat.setAppearanceLightNavigationBars(!style.equals(STYLE_DARK));
        }
        getActivity().getWindow().getDecorView().setBackgroundColor(getThemeColor(getContext(), R.attr.windowBackground));
    }

    private void setHidden(boolean hide, String bar) {
        Window window = getActivity().getWindow();
        WindowInsetsControllerCompat windowInsetsControllerCompat = WindowCompat.getInsetsController(window, window.getDecorView());
        if (hide) {
            if (bar.isEmpty()) {
                windowInsetsControllerCompat.hide(WindowInsetsCompat.Type.systemBars());
                return;
            } else if (bar.equals(BAR_STATUS_BAR)) {
                windowInsetsControllerCompat.hide(WindowInsetsCompat.Type.statusBars());
                return;
            } else {
                if (bar.equals(BAR_GESTURE_BAR)) {
                    windowInsetsControllerCompat.hide(WindowInsetsCompat.Type.navigationBars());
                    return;
                }
                return;
            }
        }
        if (bar.isEmpty()) {
            windowInsetsControllerCompat.show(WindowInsetsCompat.Type.systemBars());
        } else if (bar.equals(BAR_STATUS_BAR)) {
            windowInsetsControllerCompat.show(WindowInsetsCompat.Type.statusBars());
        } else if (bar.equals(BAR_GESTURE_BAR)) {
            windowInsetsControllerCompat.show(WindowInsetsCompat.Type.navigationBars());
        }
    }

    private String getStyleForTheme() {
        int currentNightMode = getActivity().getResources().getConfiguration().uiMode & 48;
        if (currentNightMode != 32) {
            return STYLE_LIGHT;
        }
        return STYLE_DARK;
    }

    public int getThemeColor(Context context, int attrRes) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(attrRes, typedValue, true);
        return typedValue.data;
    }

    private Integer getWebViewMajorVersion() {
        PackageInfo info = WebViewCompat.getCurrentWebViewPackage(getContext());
        if (info != null && info.versionName != null) {
            String[] versionSegments = info.versionName.split("\\.");
            return Integer.valueOf(versionSegments[0]);
        }
        return 0;
    }

    private int getBottomInset(Insets systemBarsInsets, boolean keyboardVisible) {
        if (getWebViewMajorVersion().intValue() < WEBVIEW_VERSION_WITH_SAFE_AREA_KEYBOARD_FIX && keyboardVisible) {
            return 0;
        }
        return systemBarsInsets.bottom;
    }
}
