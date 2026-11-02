package org.apache.cordova;

import android.content.Context;
import androidx.webkit.ProxyConfig;
import org.xmlpull.v1.XmlPullParser;

/* JADX INFO: loaded from: classes.dex */
public class AllowListPlugin extends CordovaPlugin {
    protected static final String LOG_TAG = "CordovaAllowListPlugin";
    public static final String PLUGIN_NAME = "CordovaAllowListPlugin";
    private AllowList allowedIntents;
    private AllowList allowedNavigations;
    private AllowList allowedRequests;

    public AllowListPlugin() {
    }

    public AllowListPlugin(Context context) {
        this(new AllowList(), new AllowList(), null);
        new CustomConfigXmlParser().parse(context);
    }

    public AllowListPlugin(XmlPullParser xmlParser) {
        this(new AllowList(), new AllowList(), null);
        new CustomConfigXmlParser().parse(xmlParser);
    }

    public AllowListPlugin(AllowList allowedNavigations, AllowList allowedIntents, AllowList allowedRequests) {
        if (allowedRequests == null) {
            allowedRequests = new AllowList();
            allowedRequests.addAllowListEntry("file:///*", false);
            allowedRequests.addAllowListEntry("data:*", false);
        }
        this.allowedNavigations = allowedNavigations;
        this.allowedIntents = allowedIntents;
        this.allowedRequests = allowedRequests;
    }

    @Override // org.apache.cordova.CordovaPlugin
    public void pluginInitialize() {
        if (this.allowedNavigations == null) {
            this.allowedNavigations = new AllowList();
            this.allowedIntents = new AllowList();
            this.allowedRequests = new AllowList();
            new CustomConfigXmlParser().parse(this.webView.getContext());
        }
    }

    private class CustomConfigXmlParser extends ConfigXmlParser {
        private CordovaPreferences prefs;

        private CustomConfigXmlParser() {
            this.prefs = new CordovaPreferences();
        }

        @Override // org.apache.cordova.ConfigXmlParser
        public void handleStartTag(XmlPullParser xml) {
            String origin;
            String strNode = xml.getName();
            boolean z = false;
            if (strNode.equals("content")) {
                String startPage = xml.getAttributeValue(null, "src");
                AllowListPlugin.this.allowedNavigations.addAllowListEntry(startPage, false);
                return;
            }
            if (strNode.equals("allow-navigation")) {
                String origin2 = xml.getAttributeValue(null, "href");
                if (ProxyConfig.MATCH_ALL_SCHEMES.equals(origin2)) {
                    AllowListPlugin.this.allowedNavigations.addAllowListEntry("http://*/*", false);
                    AllowListPlugin.this.allowedNavigations.addAllowListEntry("https://*/*", false);
                    AllowListPlugin.this.allowedNavigations.addAllowListEntry("data:*", false);
                    return;
                }
                AllowListPlugin.this.allowedNavigations.addAllowListEntry(origin2, false);
                return;
            }
            if (strNode.equals("allow-intent")) {
                AllowListPlugin.this.allowedIntents.addAllowListEntry(xml.getAttributeValue(null, "href"), false);
                return;
            }
            if (strNode.equals("access") && (origin = xml.getAttributeValue(null, "origin")) != null) {
                if (ProxyConfig.MATCH_ALL_SCHEMES.equals(origin)) {
                    AllowListPlugin.this.allowedRequests.addAllowListEntry("http://*/*", false);
                    AllowListPlugin.this.allowedRequests.addAllowListEntry("https://*/*", false);
                    return;
                }
                String subdomains = xml.getAttributeValue(null, "subdomains");
                AllowList allowList = AllowListPlugin.this.allowedRequests;
                if (subdomains != null && subdomains.compareToIgnoreCase("true") == 0) {
                    z = true;
                }
                allowList.addAllowListEntry(origin, z);
            }
        }

        @Override // org.apache.cordova.ConfigXmlParser
        public void handleEndTag(XmlPullParser xml) {
        }
    }

    @Override // org.apache.cordova.CordovaPlugin
    public Boolean shouldAllowNavigation(String url) {
        if (this.allowedNavigations.isUrlAllowListed(url)) {
            return true;
        }
        return null;
    }

    @Override // org.apache.cordova.CordovaPlugin
    public Boolean shouldAllowRequest(String url) {
        if (Boolean.TRUE.equals(shouldAllowNavigation(url)) || this.allowedRequests.isUrlAllowListed(url)) {
            return true;
        }
        return null;
    }

    @Override // org.apache.cordova.CordovaPlugin
    public Boolean shouldOpenExternalUrl(String url) {
        if (this.allowedIntents.isUrlAllowListed(url)) {
            return true;
        }
        return null;
    }

    public AllowList getAllowedNavigations() {
        return this.allowedNavigations;
    }

    public void setAllowedNavigations(AllowList allowedNavigations) {
        this.allowedNavigations = allowedNavigations;
    }

    public AllowList getAllowedIntents() {
        return this.allowedIntents;
    }

    public void setAllowedIntents(AllowList allowedIntents) {
        this.allowedIntents = allowedIntents;
    }

    public AllowList getAllowedRequests() {
        return this.allowedRequests;
    }

    public void setAllowedRequests(AllowList allowedRequests) {
        this.allowedRequests = allowedRequests;
    }
}
