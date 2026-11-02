package com.getcapacitor;

/* JADX INFO: loaded from: classes3.dex */
public class ServerPath {
    private final String path;
    private final PathType type;

    public enum PathType {
        BASE_PATH,
        ASSET_PATH
    }

    public ServerPath(PathType type, String path) {
        this.type = type;
        this.path = path;
    }

    public PathType getType() {
        return this.type;
    }

    public String getPath() {
        return this.path;
    }
}
