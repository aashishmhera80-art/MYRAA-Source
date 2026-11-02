package com.getcapacitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;

/* JADX INFO: loaded from: classes3.dex */
public class JSArray extends JSONArray {
    public JSArray() {
    }

    public JSArray(String json) throws JSONException {
        super(json);
    }

    public JSArray(Collection copyFrom) {
        super(copyFrom);
    }

    public JSArray(Object array) throws JSONException {
        super(array);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <E> List<E> toList() throws JSONException {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < length(); i++) {
            get(i);
            try {
                arrayList.add(get(i));
            } catch (Exception e) {
                throw new JSONException("Not all items are instances of the given type");
            }
        }
        return arrayList;
    }

    public static JSArray from(Object array) {
        try {
            return new JSArray(array);
        } catch (JSONException e) {
            return null;
        }
    }
}
