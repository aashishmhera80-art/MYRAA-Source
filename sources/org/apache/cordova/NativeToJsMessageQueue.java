package org.apache.cordova;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/* JADX INFO: loaded from: classes.dex */
public class NativeToJsMessageQueue {
    private static int COMBINED_RESPONSE_CUTOFF = 16777216;
    static final boolean DISABLE_EXEC_CHAINING = false;
    private static final boolean FORCE_ENCODE_USING_EVAL = false;
    private static final String LOG_TAG = "JsMessageQueue";
    private BridgeMode activeBridgeMode;
    private boolean paused;
    private final LinkedList<JsMessage> queue = new LinkedList<>();
    private ArrayList<BridgeMode> bridgeModes = new ArrayList<>();

    public void addBridgeMode(BridgeMode bridgeMode) {
        this.bridgeModes.add(bridgeMode);
    }

    public boolean isBridgeEnabled() {
        return this.activeBridgeMode != null;
    }

    public boolean isEmpty() {
        return this.queue.isEmpty();
    }

    public void setBridgeMode(int value) {
        if (value < -1 || value >= this.bridgeModes.size()) {
            LOG.d(LOG_TAG, "Invalid NativeToJsBridgeMode: " + value);
            return;
        }
        BridgeMode newMode = value < 0 ? null : this.bridgeModes.get(value);
        if (newMode != this.activeBridgeMode) {
            LOG.d(LOG_TAG, "Set native->JS mode to " + (newMode == null ? "null" : newMode.getClass().getSimpleName()));
            synchronized (this) {
                this.activeBridgeMode = newMode;
                if (newMode != null) {
                    newMode.reset();
                    if (!this.paused && !this.queue.isEmpty()) {
                        newMode.onNativeToJsMessageAvailable(this);
                    }
                }
            }
        }
    }

    public void reset() {
        synchronized (this) {
            this.queue.clear();
            setBridgeMode(-1);
        }
    }

    private int calculatePackedMessageLength(JsMessage message) {
        int messageLen = message.calculateEncodedLength();
        String messageLenStr = String.valueOf(messageLen);
        return messageLenStr.length() + messageLen + 1;
    }

    private void packMessage(JsMessage message, StringBuilder sb) {
        int len = message.calculateEncodedLength();
        sb.append(len).append(' ');
        message.encodeAsMessage(sb);
    }

    public String popAndEncode(boolean fromOnlineEvent) {
        synchronized (this) {
            if (this.activeBridgeMode == null) {
                return null;
            }
            this.activeBridgeMode.notifyOfFlush(this, fromOnlineEvent);
            if (this.queue.isEmpty()) {
                return null;
            }
            int totalPayloadLen = 0;
            int numMessagesToSend = 0;
            for (JsMessage message : this.queue) {
                int messageSize = calculatePackedMessageLength(message);
                if (numMessagesToSend > 0 && COMBINED_RESPONSE_CUTOFF > 0 && totalPayloadLen + messageSize > COMBINED_RESPONSE_CUTOFF) {
                    break;
                }
                totalPayloadLen += messageSize;
                numMessagesToSend++;
            }
            StringBuilder sb = new StringBuilder(totalPayloadLen);
            for (int i = 0; i < numMessagesToSend; i++) {
                JsMessage message2 = this.queue.removeFirst();
                packMessage(message2, sb);
            }
            if (!this.queue.isEmpty()) {
                sb.append('*');
            }
            String ret = sb.toString();
            return ret;
        }
    }

    public String popAndEncodeAsJs() {
        synchronized (this) {
            int length = this.queue.size();
            if (length == 0) {
                return null;
            }
            int totalPayloadLen = 0;
            int numMessagesToSend = 0;
            Iterator<JsMessage> it = this.queue.iterator();
            while (it.hasNext()) {
                int messageSize = it.next().calculateEncodedLength() + 50;
                if (numMessagesToSend > 0 && COMBINED_RESPONSE_CUTOFF > 0 && totalPayloadLen + messageSize > COMBINED_RESPONSE_CUTOFF) {
                    break;
                }
                totalPayloadLen += messageSize;
                numMessagesToSend++;
            }
            boolean willSendAllMessages = numMessagesToSend == this.queue.size();
            StringBuilder sb = new StringBuilder((willSendAllMessages ? 0 : 100) + totalPayloadLen);
            for (int i = 0; i < numMessagesToSend; i++) {
                JsMessage message = this.queue.removeFirst();
                if (willSendAllMessages && i + 1 == numMessagesToSend) {
                    message.encodeAsJsMessage(sb);
                } else {
                    sb.append("try{");
                    message.encodeAsJsMessage(sb);
                    sb.append("}finally{");
                }
            }
            if (!willSendAllMessages) {
                sb.append("window.setTimeout(function(){cordova.require('cordova/plugin/android/polling').pollOnce();},0);");
            }
            for (int i2 = willSendAllMessages ? 1 : 0; i2 < numMessagesToSend; i2++) {
                sb.append('}');
            }
            String ret = sb.toString();
            return ret;
        }
    }

    public void addJavaScript(String statement) {
        enqueueMessage(new JsMessage(statement));
    }

    public void addPluginResult(PluginResult result, String callbackId) {
        if (callbackId == null) {
            LOG.e(LOG_TAG, "Got plugin result with no callbackId", new Throwable());
            return;
        }
        boolean noResult = result.getStatus() == PluginResult.Status.NO_RESULT.ordinal();
        boolean keepCallback = result.getKeepCallback();
        if (noResult && keepCallback) {
            return;
        }
        JsMessage message = new JsMessage(result, callbackId);
        enqueueMessage(message);
    }

    private void enqueueMessage(JsMessage message) {
        synchronized (this) {
            if (this.activeBridgeMode == null) {
                LOG.d(LOG_TAG, "Dropping Native->JS message due to disabled bridge");
                return;
            }
            this.queue.add(message);
            if (!this.paused) {
                this.activeBridgeMode.onNativeToJsMessageAvailable(this);
            }
        }
    }

    public void setPaused(boolean value) {
        if (this.paused && value) {
            LOG.e(LOG_TAG, "nested call to setPaused detected.", new Throwable());
        }
        this.paused = value;
        if (!value) {
            synchronized (this) {
                if (!this.queue.isEmpty() && this.activeBridgeMode != null) {
                    this.activeBridgeMode.onNativeToJsMessageAvailable(this);
                }
            }
        }
    }

    public static abstract class BridgeMode {
        public abstract void onNativeToJsMessageAvailable(NativeToJsMessageQueue nativeToJsMessageQueue);

        public void notifyOfFlush(NativeToJsMessageQueue queue, boolean fromOnlineEvent) {
        }

        public void reset() {
        }
    }

    public static class NoOpBridgeMode extends BridgeMode {
        @Override // org.apache.cordova.NativeToJsMessageQueue.BridgeMode
        public void onNativeToJsMessageAvailable(NativeToJsMessageQueue queue) {
        }
    }

    public static class LoadUrlBridgeMode extends BridgeMode {
        private final CordovaInterface cordova;
        private final CordovaWebViewEngine engine;

        public LoadUrlBridgeMode(CordovaWebViewEngine engine, CordovaInterface cordova) {
            this.engine = engine;
            this.cordova = cordova;
        }

        @Override // org.apache.cordova.NativeToJsMessageQueue.BridgeMode
        public void onNativeToJsMessageAvailable(final NativeToJsMessageQueue queue) {
            this.cordova.getActivity().runOnUiThread(new Runnable() { // from class: org.apache.cordova.NativeToJsMessageQueue.LoadUrlBridgeMode.1
                @Override // java.lang.Runnable
                public void run() {
                    String js = queue.popAndEncodeAsJs();
                    if (js != null) {
                        LoadUrlBridgeMode.this.engine.loadUrl("javascript:" + js, false);
                    }
                }
            });
        }
    }

    public static class OnlineEventsBridgeMode extends BridgeMode {
        private final OnlineEventsBridgeModeDelegate delegate;
        private boolean ignoreNextFlush;
        private boolean online;

        public interface OnlineEventsBridgeModeDelegate {
            void runOnUiThread(Runnable runnable);

            void setNetworkAvailable(boolean z);
        }

        public OnlineEventsBridgeMode(OnlineEventsBridgeModeDelegate delegate) {
            this.delegate = delegate;
        }

        @Override // org.apache.cordova.NativeToJsMessageQueue.BridgeMode
        public void reset() {
            this.delegate.runOnUiThread(new Runnable() { // from class: org.apache.cordova.NativeToJsMessageQueue.OnlineEventsBridgeMode.1
                @Override // java.lang.Runnable
                public void run() {
                    OnlineEventsBridgeMode.this.online = false;
                    OnlineEventsBridgeMode.this.ignoreNextFlush = true;
                    OnlineEventsBridgeMode.this.delegate.setNetworkAvailable(true);
                }
            });
        }

        @Override // org.apache.cordova.NativeToJsMessageQueue.BridgeMode
        public void onNativeToJsMessageAvailable(final NativeToJsMessageQueue queue) {
            this.delegate.runOnUiThread(new Runnable() { // from class: org.apache.cordova.NativeToJsMessageQueue.OnlineEventsBridgeMode.2
                @Override // java.lang.Runnable
                public void run() {
                    if (!queue.isEmpty()) {
                        OnlineEventsBridgeMode.this.ignoreNextFlush = false;
                        OnlineEventsBridgeMode.this.delegate.setNetworkAvailable(OnlineEventsBridgeMode.this.online);
                    }
                }
            });
        }

        @Override // org.apache.cordova.NativeToJsMessageQueue.BridgeMode
        public void notifyOfFlush(NativeToJsMessageQueue queue, boolean fromOnlineEvent) {
            if (fromOnlineEvent && !this.ignoreNextFlush) {
                this.online = !this.online;
            }
        }
    }

    public static class EvalBridgeMode extends BridgeMode {
        private final CordovaInterface cordova;
        private final CordovaWebViewEngine engine;

        public EvalBridgeMode(CordovaWebViewEngine engine, CordovaInterface cordova) {
            this.engine = engine;
            this.cordova = cordova;
        }

        @Override // org.apache.cordova.NativeToJsMessageQueue.BridgeMode
        public void onNativeToJsMessageAvailable(final NativeToJsMessageQueue queue) {
            this.cordova.getActivity().runOnUiThread(new Runnable() { // from class: org.apache.cordova.NativeToJsMessageQueue.EvalBridgeMode.1
                @Override // java.lang.Runnable
                public void run() {
                    String js = queue.popAndEncodeAsJs();
                    if (js != null) {
                        EvalBridgeMode.this.engine.evaluateJavascript(js, null);
                    }
                }
            });
        }
    }

    private static class JsMessage {
        final String jsPayloadOrCallbackId;
        final PluginResult pluginResult;

        JsMessage(String js) {
            if (js == null) {
                throw new NullPointerException();
            }
            this.jsPayloadOrCallbackId = js;
            this.pluginResult = null;
        }

        JsMessage(PluginResult pluginResult, String callbackId) {
            if (callbackId == null || pluginResult == null) {
                throw new NullPointerException();
            }
            this.jsPayloadOrCallbackId = callbackId;
            this.pluginResult = pluginResult;
        }

        static int calculateEncodedLengthHelper(PluginResult pluginResult) {
            switch (pluginResult.getMessageType()) {
                case 1:
                    return pluginResult.getStrMessage().length() + 1;
                case 2:
                default:
                    return pluginResult.getMessage().length();
                case 3:
                    return pluginResult.getMessage().length() + 1;
                case 4:
                case 5:
                    return 1;
                case 6:
                    return pluginResult.getMessage().length() + 1;
                case 7:
                    return pluginResult.getMessage().length() + 1;
                case 8:
                    int ret = 1;
                    for (int i = 0; i < pluginResult.getMultipartMessagesSize(); i++) {
                        int length = calculateEncodedLengthHelper(pluginResult.getMultipartMessage(i));
                        int argLength = String.valueOf(length).length();
                        ret += argLength + 1 + length;
                    }
                    return ret;
            }
        }

        int calculateEncodedLength() {
            if (this.pluginResult == null) {
                return this.jsPayloadOrCallbackId.length() + 1;
            }
            int statusLen = String.valueOf(this.pluginResult.getStatus()).length();
            int ret = statusLen + 2 + 1 + this.jsPayloadOrCallbackId.length() + 1;
            return calculateEncodedLengthHelper(this.pluginResult) + ret;
        }

        static void encodeAsMessageHelper(StringBuilder sb, PluginResult pluginResult) {
            switch (pluginResult.getMessageType()) {
                case 1:
                    sb.append('s');
                    sb.append(pluginResult.getStrMessage());
                    break;
                case 2:
                default:
                    sb.append(pluginResult.getMessage());
                    break;
                case 3:
                    sb.append('n').append(pluginResult.getMessage());
                    break;
                case 4:
                    sb.append(pluginResult.getMessage().charAt(0));
                    break;
                case 5:
                    sb.append('N');
                    break;
                case 6:
                    sb.append('A');
                    sb.append(pluginResult.getMessage());
                    break;
                case 7:
                    sb.append('S');
                    sb.append(pluginResult.getMessage());
                    break;
                case 8:
                    sb.append('M');
                    for (int i = 0; i < pluginResult.getMultipartMessagesSize(); i++) {
                        PluginResult multipartMessage = pluginResult.getMultipartMessage(i);
                        sb.append(String.valueOf(calculateEncodedLengthHelper(multipartMessage)));
                        sb.append(' ');
                        encodeAsMessageHelper(sb, multipartMessage);
                    }
                    break;
            }
        }

        void encodeAsMessage(StringBuilder sb) {
            if (this.pluginResult == null) {
                sb.append('J').append(this.jsPayloadOrCallbackId);
                return;
            }
            int status = this.pluginResult.getStatus();
            boolean noResult = status == PluginResult.Status.NO_RESULT.ordinal();
            boolean resultOk = status == PluginResult.Status.OK.ordinal();
            boolean keepCallback = this.pluginResult.getKeepCallback();
            sb.append((noResult || resultOk) ? 'S' : 'F').append(keepCallback ? '1' : '0').append(status).append(' ').append(this.jsPayloadOrCallbackId).append(' ');
            encodeAsMessageHelper(sb, this.pluginResult);
        }

        void buildJsMessage(StringBuilder sb) {
            switch (this.pluginResult.getMessageType()) {
                case 5:
                    sb.append("null");
                    break;
                case 6:
                    sb.append("cordova.require('cordova/base64').toArrayBuffer('").append(this.pluginResult.getMessage()).append("')");
                    break;
                case 7:
                    sb.append("atob('").append(this.pluginResult.getMessage()).append("')");
                    break;
                case 8:
                    int size = this.pluginResult.getMultipartMessagesSize();
                    for (int i = 0; i < size; i++) {
                        PluginResult subresult = this.pluginResult.getMultipartMessage(i);
                        JsMessage submessage = new JsMessage(subresult, this.jsPayloadOrCallbackId);
                        submessage.buildJsMessage(sb);
                        if (i < size - 1) {
                            sb.append(",");
                        }
                    }
                    break;
                default:
                    sb.append(this.pluginResult.getMessage());
                    break;
            }
        }

        void encodeAsJsMessage(StringBuilder sb) {
            if (this.pluginResult == null) {
                sb.append(this.jsPayloadOrCallbackId);
                return;
            }
            int status = this.pluginResult.getStatus();
            boolean success = status == PluginResult.Status.OK.ordinal() || status == PluginResult.Status.NO_RESULT.ordinal();
            sb.append("cordova.callbackFromNative('").append(this.jsPayloadOrCallbackId).append("',").append(success).append(",").append(status).append(",[");
            buildJsMessage(sb);
            sb.append("],").append(this.pluginResult.getKeepCallback()).append(");");
        }
    }
}
