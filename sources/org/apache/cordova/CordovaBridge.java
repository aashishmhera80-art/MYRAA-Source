package org.apache.cordova;

import java.security.SecureRandom;
import org.json.JSONArray;
import org.json.JSONException;

/* JADX INFO: loaded from: classes.dex */
public class CordovaBridge {
    private static final String LOG_TAG = "CordovaBridge";
    private volatile int expectedBridgeSecret = -1;
    private NativeToJsMessageQueue jsMessageQueue;
    private PluginManager pluginManager;

    public CordovaBridge(PluginManager pluginManager, NativeToJsMessageQueue jsMessageQueue) {
        this.pluginManager = pluginManager;
        this.jsMessageQueue = jsMessageQueue;
    }

    public String jsExec(int bridgeSecret, String service, String action, String callbackId, String arguments) throws JSONException, IllegalAccessException {
        if (!verifySecret("exec()", bridgeSecret)) {
            return null;
        }
        if (arguments == null) {
            return "@Null arguments.";
        }
        this.jsMessageQueue.setPaused(true);
        try {
            CordovaResourceApi.jsThread = Thread.currentThread();
            this.pluginManager.exec(service, action, callbackId, arguments);
            String ret = this.jsMessageQueue.popAndEncode(false);
            this.jsMessageQueue.setPaused(false);
            return ret;
        } catch (Throwable e) {
            try {
                e.printStackTrace();
                return "";
            } finally {
                this.jsMessageQueue.setPaused(false);
            }
        }
    }

    public void jsSetNativeToJsBridgeMode(int bridgeSecret, int value) throws IllegalAccessException {
        if (!verifySecret("setNativeToJsBridgeMode()", bridgeSecret)) {
            return;
        }
        this.jsMessageQueue.setBridgeMode(value);
    }

    public String jsRetrieveJsMessages(int bridgeSecret, boolean fromOnlineEvent) throws IllegalAccessException {
        if (!verifySecret("retrieveJsMessages()", bridgeSecret)) {
            return null;
        }
        return this.jsMessageQueue.popAndEncode(fromOnlineEvent);
    }

    private boolean verifySecret(String action, int bridgeSecret) throws IllegalAccessException {
        if (!this.jsMessageQueue.isBridgeEnabled()) {
            if (bridgeSecret == -1) {
                LOG.d(LOG_TAG, action + " call made before bridge was enabled.");
                return false;
            }
            LOG.d(LOG_TAG, "Ignoring " + action + " from previous page load.");
            return false;
        }
        if (this.expectedBridgeSecret < 0 || bridgeSecret != this.expectedBridgeSecret) {
            LOG.e(LOG_TAG, "Bridge access attempt with wrong secret token, possibly from malicious code. Disabling exec() bridge!");
            clearBridgeSecret();
            throw new IllegalAccessException();
        }
        return true;
    }

    void clearBridgeSecret() {
        this.expectedBridgeSecret = -1;
    }

    public boolean isSecretEstablished() {
        return this.expectedBridgeSecret != -1;
    }

    int generateBridgeSecret() {
        SecureRandom randGen = new SecureRandom();
        this.expectedBridgeSecret = randGen.nextInt(Integer.MAX_VALUE);
        return this.expectedBridgeSecret;
    }

    public void reset() {
        this.jsMessageQueue.reset();
        clearBridgeSecret();
    }

    public String promptOnJsPrompt(String origin, String message, String defaultValue) {
        JSONException e;
        IllegalAccessException e2;
        if (defaultValue != null && defaultValue.startsWith("gap:")) {
            try {
                JSONArray array = new JSONArray(defaultValue.substring(4));
                int bridgeSecret = array.getInt(0);
                String service = array.getString(1);
                String action = array.getString(2);
                String callbackId = array.getString(3);
                try {
                    String r = jsExec(bridgeSecret, service, action, callbackId, message);
                    return r == null ? "" : r;
                } catch (IllegalAccessException e3) {
                    e2 = e3;
                    e2.printStackTrace();
                    return "";
                } catch (JSONException e4) {
                    e = e4;
                    e.printStackTrace();
                    return "";
                }
            } catch (IllegalAccessException e5) {
                e2 = e5;
            } catch (JSONException e6) {
                e = e6;
            }
        } else {
            if (defaultValue != null && defaultValue.startsWith("gap_bridge_mode:")) {
                try {
                    int bridgeSecret2 = Integer.parseInt(defaultValue.substring(16));
                    jsSetNativeToJsBridgeMode(bridgeSecret2, Integer.parseInt(message));
                } catch (IllegalAccessException e7) {
                    e7.printStackTrace();
                } catch (NumberFormatException e8) {
                    e8.printStackTrace();
                }
                return "";
            }
            if (defaultValue != null && defaultValue.startsWith("gap_poll:")) {
                int bridgeSecret3 = Integer.parseInt(defaultValue.substring(9));
                try {
                    String r2 = jsRetrieveJsMessages(bridgeSecret3, "1".equals(message));
                    return r2 == null ? "" : r2;
                } catch (IllegalAccessException e9) {
                    e9.printStackTrace();
                    return "";
                }
            }
            if (defaultValue != null && defaultValue.startsWith("gap_init:")) {
                if (this.pluginManager.shouldAllowBridgeAccess(origin)) {
                    int bridgeMode = Integer.parseInt(defaultValue.substring(9));
                    this.jsMessageQueue.setBridgeMode(bridgeMode);
                    int secret = generateBridgeSecret();
                    return "" + secret;
                }
                LOG.e(LOG_TAG, "gap_init called from restricted origin: " + origin);
                return "";
            }
            return null;
        }
    }
}
