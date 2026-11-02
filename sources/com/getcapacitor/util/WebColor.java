package com.getcapacitor.util;

import android.graphics.Color;

/* JADX INFO: loaded from: classes2.dex */
public class WebColor {
    public static int parseColor(String colorString) {
        String formattedColor = colorString;
        if (colorString.charAt(0) != '#') {
            formattedColor = "#" + formattedColor;
        }
        if (formattedColor.length() != 7 && formattedColor.length() != 9) {
            throw new IllegalArgumentException("The encoded color space is invalid or unknown");
        }
        if (formattedColor.length() == 7) {
            return Color.parseColor(formattedColor);
        }
        return Color.parseColor("#" + formattedColor.substring(7) + formattedColor.substring(1, 7));
    }
}
