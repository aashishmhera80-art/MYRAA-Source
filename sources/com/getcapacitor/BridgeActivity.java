package com.getcapacitor;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.getcapacitor.android.R;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public class BridgeActivity extends AppCompatActivity {
    protected Bridge bridge;
    protected CapConfig config;
    protected boolean keepRunning = true;
    protected int activityDepth = 0;
    protected List<Class<? extends Plugin>> initialPlugins = new ArrayList();
    protected final Bridge.Builder bridgeBuilder = new Bridge.Builder(this);

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.bridgeBuilder.setInstanceState(savedInstanceState);
        getApplication().setTheme(R.style.AppTheme_NoActionBar);
        setTheme(R.style.AppTheme_NoActionBar);
        try {
            setContentView(R.layout.capacitor_bridge_layout_main);
            PluginManager loader = new PluginManager(getAssets());
            try {
                this.bridgeBuilder.addPlugins(loader.loadPluginClasses());
            } catch (PluginLoadException ex) {
                Logger.error("Error loading plugins.", ex);
            }
            load();
        } catch (Exception e) {
            setContentView(R.layout.no_webview);
        }
    }

    protected void load() {
        Logger.debug("Starting BridgeActivity");
        this.bridge = this.bridgeBuilder.addPlugins(this.initialPlugins).setConfig(this.config).create();
        this.keepRunning = this.bridge.shouldKeepRunning();
        onNewIntent(getIntent());
    }

    public void registerPlugin(Class<? extends Plugin> plugin) {
        this.bridgeBuilder.addPlugin(plugin);
    }

    public void registerPlugins(List<Class<? extends Plugin>> plugins) {
        this.bridgeBuilder.addPlugins(plugins);
    }

    public Bridge getBridge() {
        return this.bridge;
    }

    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (this.bridge != null) {
            this.bridge.saveInstanceState(outState);
        }
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStart() {
        super.onStart();
        this.activityDepth++;
        if (this.bridge != null) {
            this.bridge.onStart();
            Logger.debug("App started");
        }
    }

    @Override // android.app.Activity
    public void onRestart() {
        super.onRestart();
        if (this.bridge != null) {
            this.bridge.onRestart();
            Logger.debug("App restarted");
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        if (this.bridge != null) {
            this.bridge.getApp().fireStatusChange(true);
            this.bridge.onResume();
            Logger.debug("App resumed");
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        super.onPause();
        if (this.bridge != null) {
            this.bridge.onPause();
            Logger.debug("App paused");
        }
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStop() {
        super.onStop();
        if (this.bridge != null) {
            this.activityDepth = Math.max(0, this.activityDepth - 1);
            if (this.activityDepth == 0) {
                this.bridge.getApp().fireStatusChange(false);
            }
            this.bridge.onStop();
            Logger.debug("App stopped");
        }
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        if (this.bridge != null) {
            this.bridge.onDestroy();
            Logger.debug("App destroyed");
        }
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.bridge != null) {
            this.bridge.onDetachedFromWindow();
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (this.bridge != null && !this.bridge.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (this.bridge != null && !this.bridge.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (this.bridge == null || intent == null) {
            return;
        }
        this.bridge.onNewIntent(intent);
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.activity.ComponentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.bridge == null) {
            return;
        }
        this.bridge.onConfigurationChanged(newConfig);
    }
}
