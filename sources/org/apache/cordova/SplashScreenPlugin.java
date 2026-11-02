package org.apache.cordova;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.splashscreen.SplashScreenViewProvider;
import org.json.JSONArray;
import org.json.JSONException;

/* JADX INFO: loaded from: classes.dex */
public class SplashScreenPlugin extends CordovaPlugin {
    private static final boolean DEFAULT_AUTO_HIDE = true;
    private static final int DEFAULT_DELAY_TIME = -1;
    private static final boolean DEFAULT_FADE = true;
    private static final int DEFAULT_FADE_TIME = 500;
    static final String PLUGIN_NAME = "CordovaSplashScreenPlugin";
    private boolean autoHide;
    private int delayTime;
    private int fadeDuration;
    private boolean isFadeEnabled;
    private boolean keepOnScreen = true;

    @Override // org.apache.cordova.CordovaPlugin
    protected void pluginInitialize() {
        this.autoHide = this.preferences.getBoolean("AutoHideSplashScreen", true);
        this.delayTime = this.preferences.getInteger("SplashScreenDelay", -1);
        LOG.d(PLUGIN_NAME, "Auto Hide: " + this.autoHide);
        if (this.delayTime != -1) {
            LOG.d(PLUGIN_NAME, "Delay: " + this.delayTime + "ms");
        }
        this.isFadeEnabled = this.preferences.getBoolean("FadeSplashScreen", true);
        this.fadeDuration = this.preferences.getInteger("FadeSplashScreenDuration", DEFAULT_FADE_TIME);
        LOG.d(PLUGIN_NAME, "Fade: " + this.isFadeEnabled);
        if (this.isFadeEnabled) {
            LOG.d(PLUGIN_NAME, "Fade Duration: " + this.fadeDuration + "ms");
        }
    }

    @Override // org.apache.cordova.CordovaPlugin
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (!action.equals("hide") || this.autoHide) {
            return false;
        }
        this.keepOnScreen = false;
        callbackContext.success();
        return true;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:11:0x001c  */
    @Override // org.apache.cordova.CordovaPlugin
    public Object onMessage(String id, Object data) {
        switch (id) {
            case "setupSplashScreen":
                setupSplashScreen((SplashScreen) data);
                break;
            case "onPageFinished":
                attemptCloseOnPageFinished();
                break;
        }
        return null;
    }

    private void setupSplashScreen(SplashScreen splashScreen) {
        splashScreen.setKeepOnScreenCondition(new SplashScreen.KeepOnScreenCondition() { // from class: org.apache.cordova.SplashScreenPlugin$$ExternalSyntheticLambda0
            @Override // androidx.core.splashscreen.SplashScreen.KeepOnScreenCondition
            public final boolean shouldKeepOnScreen() {
                return this.f$0.m1904lambda$setupSplashScreen$0$orgapachecordovaSplashScreenPlugin();
            }
        });
        if (this.autoHide && this.delayTime != -1) {
            Handler splashScreenDelayHandler = new Handler(this.cordova.getContext().getMainLooper());
            splashScreenDelayHandler.postDelayed(new Runnable() { // from class: org.apache.cordova.SplashScreenPlugin$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1905lambda$setupSplashScreen$1$orgapachecordovaSplashScreenPlugin();
                }
            }, this.delayTime);
        }
        if (this.isFadeEnabled) {
            splashScreen.setOnExitAnimationListener(new SplashScreen.OnExitAnimationListener() { // from class: org.apache.cordova.SplashScreenPlugin.1
                @Override // androidx.core.splashscreen.SplashScreen.OnExitAnimationListener
                public void onSplashScreenExit(final SplashScreenViewProvider splashScreenViewProvider) {
                    View splashScreenView = splashScreenViewProvider.getView();
                    splashScreenView.animate().alpha(0.0f).setDuration(SplashScreenPlugin.this.fadeDuration).setStartDelay(0L).setInterpolator(new AccelerateInterpolator()).setListener(new AnimatorListenerAdapter() { // from class: org.apache.cordova.SplashScreenPlugin.1.1
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            splashScreenViewProvider.remove();
                        }
                    }).start();
                }
            });
        }
    }

    /* JADX INFO: renamed from: lambda$setupSplashScreen$0$org-apache-cordova-SplashScreenPlugin, reason: not valid java name */
    /* synthetic */ boolean m1904lambda$setupSplashScreen$0$orgapachecordovaSplashScreenPlugin() {
        return this.keepOnScreen;
    }

    /* JADX INFO: renamed from: lambda$setupSplashScreen$1$org-apache-cordova-SplashScreenPlugin, reason: not valid java name */
    /* synthetic */ void m1905lambda$setupSplashScreen$1$orgapachecordovaSplashScreenPlugin() {
        this.keepOnScreen = false;
    }

    private void attemptCloseOnPageFinished() {
        if (this.autoHide && this.delayTime == -1) {
            this.keepOnScreen = false;
        }
    }
}
