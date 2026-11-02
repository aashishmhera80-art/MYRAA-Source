package org.apache.cordova;

import android.net.Uri;
import androidx.webkit.ProxyConfig;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes.dex */
public class AllowList {
    public static final String TAG = "CordovaAllowList";
    private ArrayList<URLPattern> allowList = new ArrayList<>();

    private static class URLPattern {
        public Pattern host;
        public Pattern path;
        public Integer port;
        public Pattern scheme;

        private String regexFromPattern(String pattern, boolean allowWildcards) {
            StringBuilder regex = new StringBuilder();
            for (int i = 0; i < pattern.length(); i++) {
                char c = pattern.charAt(i);
                if (c == '*' && allowWildcards) {
                    regex.append(".");
                } else if ("\\.[]{}()^$?+|".indexOf(c) > -1) {
                    regex.append('\\');
                }
                regex.append(c);
            }
            return regex.toString();
        }

        public URLPattern(String scheme, String host, String port, String path) throws MalformedURLException {
            if (scheme == null) {
                this.scheme = null;
            } else {
                try {
                    if (ProxyConfig.MATCH_ALL_SCHEMES.equals(scheme)) {
                        this.scheme = null;
                    } else {
                        this.scheme = Pattern.compile(regexFromPattern(scheme, false), 2);
                    }
                } catch (NumberFormatException e) {
                    throw new MalformedURLException("Port must be a number");
                }
            }
            if (ProxyConfig.MATCH_ALL_SCHEMES.equals(host)) {
                this.host = null;
            } else if (host.startsWith("*.")) {
                this.host = Pattern.compile("([a-z0-9.-]*\\.)?" + regexFromPattern(host.substring(2), false), 2);
            } else {
                this.host = Pattern.compile(regexFromPattern(host, false), 2);
            }
            if (port == null || ProxyConfig.MATCH_ALL_SCHEMES.equals(port)) {
                this.port = null;
            } else {
                this.port = Integer.valueOf(Integer.parseInt(port, 10));
            }
            if (path != null && !"/*".equals(path)) {
                this.path = Pattern.compile(regexFromPattern(path, true));
                return;
            }
            this.path = null;
        }

        public boolean matches(Uri uri) {
            try {
                if (this.scheme != null && !this.scheme.matcher(uri.getScheme()).matches()) {
                    return false;
                }
                if (this.host != null && !this.host.matcher(uri.getHost()).matches()) {
                    return false;
                }
                if (this.port == null || this.port.equals(Integer.valueOf(uri.getPort()))) {
                    return this.path == null || this.path.matcher(uri.getPath()).matches();
                }
                return false;
            } catch (Exception e) {
                LOG.d(AllowList.TAG, e.toString());
                return false;
            }
        }
    }

    public void addAllowListEntry(String origin, boolean subdomains) {
        if (this.allowList != null) {
            try {
                if (origin.compareTo(ProxyConfig.MATCH_ALL_SCHEMES) == 0) {
                    LOG.d(TAG, "Unlimited access to network resources");
                    this.allowList = null;
                    return;
                }
                Pattern parts = Pattern.compile("^((\\*|[A-Za-z-]+):(//)?)?(\\*|((\\*\\.)?[^*/:]+))?(:(\\d+))?(/.*)?");
                Matcher m = parts.matcher(origin);
                if (m.matches()) {
                    String scheme = m.group(2);
                    String host = m.group(4);
                    if (("file".equals(scheme) || "content".equals(scheme)) && host == null) {
                        host = ProxyConfig.MATCH_ALL_SCHEMES;
                    }
                    String port = m.group(8);
                    String path = m.group(9);
                    if (scheme == null) {
                        this.allowList.add(new URLPattern("http", host, port, path));
                        this.allowList.add(new URLPattern("https", host, port, path));
                    } else {
                        this.allowList.add(new URLPattern(scheme, host, port, path));
                    }
                }
            } catch (Exception e) {
                LOG.d(TAG, "Failed to add origin %s", origin);
            }
        }
    }

    public boolean isUrlAllowListed(String uri) {
        if (this.allowList == null) {
            return true;
        }
        Uri parsedUri = Uri.parse(uri);
        for (URLPattern p : this.allowList) {
            if (p.matches(parsedUri)) {
                return true;
            }
        }
        return false;
    }
}
