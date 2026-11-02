package org.apache.cordova;

import android.content.Context;
import androidx.core.app.NotificationCompat;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public class ConfigXmlParser {
    private static final String DEFAULT_CONTENT_SRC = "index.html";
    private String contentSrc;
    private String launchUrl;
    private static String TAG = "ConfigXmlParser";
    private static String SCHEME_HTTP = "http";
    private static String SCHEME_HTTPS = "https";
    private static String DEFAULT_HOSTNAME = "localhost";
    private CordovaPreferences prefs = new CordovaPreferences();
    private ArrayList<PluginEntry> pluginEntries = new ArrayList<>(20);
    boolean insideFeature = false;
    String service = "";
    String pluginClass = "";
    String paramType = "";
    boolean onload = false;

    public CordovaPreferences getPreferences() {
        return this.prefs;
    }

    public ArrayList<PluginEntry> getPluginEntries() {
        return this.pluginEntries;
    }

    public String getLaunchUrl() {
        if (this.launchUrl == null) {
            setStartUrl(this.contentSrc);
        }
        return this.launchUrl;
    }

    public void parse(Context action) throws XmlPullParserException, IOException {
        int id = action.getResources().getIdentifier("config", "xml", action.getClass().getPackage().getName());
        if (id == 0 && (id = action.getResources().getIdentifier("config", "xml", action.getPackageName())) == 0) {
            LOG.e(TAG, "res/xml/config.xml is missing!");
            return;
        }
        this.pluginEntries.add(new PluginEntry(AllowListPlugin.PLUGIN_NAME, "org.apache.cordova.AllowListPlugin", true));
        this.pluginEntries.add(new PluginEntry("CordovaSplashScreenPlugin", "org.apache.cordova.SplashScreenPlugin", true));
        parse(action.getResources().getXml(id));
    }

    public void parse(XmlPullParser xml) throws XmlPullParserException, IOException {
        int eventType = -1;
        while (eventType != 1) {
            if (eventType == 2) {
                handleStartTag(xml);
            } else if (eventType == 3) {
                handleEndTag(xml);
            }
            try {
                eventType = xml.next();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e2) {
                e2.printStackTrace();
            }
        }
        onPostParse();
    }

    private void onPostParse() {
        if (this.contentSrc == null) {
            this.contentSrc = DEFAULT_CONTENT_SRC;
        }
    }

    public void handleStartTag(XmlPullParser xml) {
        String strNode = xml.getName();
        if (strNode.equals("feature")) {
            this.insideFeature = true;
            this.service = xml.getAttributeValue(null, "name");
            return;
        }
        if (this.insideFeature && strNode.equals("param")) {
            this.paramType = xml.getAttributeValue(null, "name");
            if (this.paramType.equals(NotificationCompat.CATEGORY_SERVICE)) {
                this.service = xml.getAttributeValue(null, "value");
                return;
            }
            if (this.paramType.equals("package") || this.paramType.equals("android-package")) {
                this.pluginClass = xml.getAttributeValue(null, "value");
                return;
            } else {
                if (this.paramType.equals("onload")) {
                    this.onload = "true".equals(xml.getAttributeValue(null, "value"));
                    return;
                }
                return;
            }
        }
        if (strNode.equals("preference")) {
            String name = xml.getAttributeValue(null, "name").toLowerCase(Locale.ENGLISH);
            String value = xml.getAttributeValue(null, "value");
            this.prefs.set(name, value);
        } else if (strNode.equals("content")) {
            String src = xml.getAttributeValue(null, "src");
            if (src != null) {
                this.contentSrc = src;
            } else {
                this.contentSrc = DEFAULT_CONTENT_SRC;
            }
        }
    }

    public void handleEndTag(XmlPullParser xml) {
        String strNode = xml.getName();
        if (strNode.equals("feature")) {
            this.pluginEntries.add(new PluginEntry(this.service, this.pluginClass, this.onload));
            this.service = "";
            this.pluginClass = "";
            this.insideFeature = false;
            this.onload = false;
        }
    }

    private String getLaunchUrlPrefix() {
        if (this.prefs.getBoolean("AndroidInsecureFileModeEnabled", false)) {
            return "file:///android_asset/www/";
        }
        String scheme = this.prefs.getString("scheme", SCHEME_HTTPS).toLowerCase();
        String hostname = this.prefs.getString("hostname", DEFAULT_HOSTNAME).toLowerCase();
        if (!scheme.contentEquals(SCHEME_HTTP) && !scheme.contentEquals(SCHEME_HTTPS)) {
            LOG.d(TAG, "The provided scheme \"" + scheme + "\" is not valid. Defaulting to \"" + SCHEME_HTTPS + "\". (Valid Options=" + SCHEME_HTTP + "," + SCHEME_HTTPS + ")");
            scheme = SCHEME_HTTPS;
        }
        return scheme + "://" + hostname + '/';
    }

    private void setStartUrl(String src) {
        Pattern schemeRegex = Pattern.compile("^[a-z-]+://");
        Matcher matcher = schemeRegex.matcher(src);
        if (matcher.find()) {
            this.launchUrl = src;
            return;
        }
        String launchUrlPrefix = getLaunchUrlPrefix();
        if (src.charAt(0) == '/') {
            src = src.substring(1);
        }
        this.launchUrl = launchUrlPrefix + src;
    }
}
