package com.getcapacitor;

/* JADX INFO: loaded from: classes3.dex */
public class ProcessedRoute {
    private boolean ignoreAssetPath;
    private boolean isAsset;
    private String path;

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isAsset() {
        return this.isAsset;
    }

    public void setAsset(boolean asset) {
        this.isAsset = asset;
    }

    public boolean isIgnoreAssetPath() {
        return this.ignoreAssetPath;
    }

    public void setIgnoreAssetPath(boolean ignoreAssetPath) {
        this.ignoreAssetPath = ignoreAssetPath;
    }
}
