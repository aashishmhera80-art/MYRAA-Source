package com.getcapacitor;

import android.net.Uri;
import androidx.webkit.ProxyConfig;
import com.getcapacitor.util.HostMask;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes3.dex */
public class UriMatcher {
    private static final int EXACT = 0;
    private static final int MASK = 3;
    static final Pattern PATH_SPLIT_PATTERN = Pattern.compile("/");
    private static final int REST = 2;
    private static final int TEXT = 1;
    private ArrayList<UriMatcher> mChildren;
    private Object mCode;
    private String mText;
    private int mWhich;

    public UriMatcher(Object code) {
        this.mCode = code;
        this.mWhich = -1;
        this.mChildren = new ArrayList<>();
        this.mText = null;
    }

    private UriMatcher() {
        this.mCode = null;
        this.mWhich = -1;
        this.mChildren = new ArrayList<>();
        this.mText = null;
    }

    public void addURI(String scheme, String authority, String path, Object code) {
        String token;
        if (code == null) {
            throw new IllegalArgumentException("Code can't be null");
        }
        String[] tokens = null;
        if (path != null) {
            String newPath = path;
            if (!path.isEmpty() && path.charAt(0) == '/') {
                newPath = path.substring(1);
            }
            tokens = PATH_SPLIT_PATTERN.split(newPath);
        }
        int numTokens = tokens != null ? tokens.length : 0;
        UriMatcher node = this;
        int i = -2;
        while (i < numTokens) {
            if (i == -2) {
                token = scheme;
            } else {
                token = i == -1 ? authority : tokens[i];
            }
            ArrayList<UriMatcher> children = node.mChildren;
            int numChildren = children.size();
            int j = 0;
            while (j < numChildren) {
                UriMatcher child = children.get(j);
                if (!token.equals(child.mText)) {
                    j++;
                } else {
                    node = child;
                    break;
                }
            }
            if (j == numChildren) {
                UriMatcher child2 = new UriMatcher();
                if (i == -1 && token.contains(ProxyConfig.MATCH_ALL_SCHEMES)) {
                    child2.mWhich = 3;
                } else if (token.equals("**")) {
                    child2.mWhich = 2;
                } else if (token.equals(ProxyConfig.MATCH_ALL_SCHEMES)) {
                    child2.mWhich = 1;
                } else {
                    child2.mWhich = 0;
                }
                child2.mText = token;
                node.mChildren.add(child2);
                node = child2;
            }
            i++;
        }
        node.mCode = code;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:36:0x0065 A[LOOP:1: B:21:0x0038->B:36:0x0065, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:40:0x006c A[LOOP:0: B:9:0x0015->B:40:0x006c, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:46:0x006a A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:47:0x0068 A[SYNTHETIC] */
    public Object match(Uri uri) {
        String u;
        List<String> pathSegments = uri.getPathSegments();
        int li = pathSegments.size();
        UriMatcher node = this;
        if (li == 0 && uri.getAuthority() == null) {
            return this.mCode;
        }
        for (int i = -2; i < li; i++) {
            if (i == -2) {
                u = uri.getScheme();
            } else if (i == -1) {
                u = uri.getAuthority();
            } else {
                String u2 = pathSegments.get(i);
                u = u2;
            }
            ArrayList<UriMatcher> list = node.mChildren;
            if (list != null) {
                node = null;
                int lj = list.size();
                for (int j = 0; j < lj; j++) {
                    UriMatcher n = list.get(j);
                    switch (n.mWhich) {
                        case 0:
                            if (n.mText.equals(u)) {
                                node = n;
                            }
                            if (node != null) {
                                if (node == null) {
                                    return null;
                                }
                            }
                            break;
                        case 1:
                            node = n;
                            if (node != null) {
                                if (node == null) {
                                    return null;
                                }
                            }
                            break;
                        case 2:
                            return n.mCode;
                        case 3:
                            if (HostMask.Parser.parse(n.mText).matches(u)) {
                                node = n;
                            }
                            if (node != null) {
                                if (node == null) {
                                    return null;
                                }
                            }
                            break;
                        default:
                            if (node != null) {
                                if (node == null) {
                                    return null;
                                }
                            }
                            break;
                    }
                }
                if (node == null) {
                    return null;
                }
            } else {
                return node.mCode;
            }
        }
        return node.mCode;
    }
}
