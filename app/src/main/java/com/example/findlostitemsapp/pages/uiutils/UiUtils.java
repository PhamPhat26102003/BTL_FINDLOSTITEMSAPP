package com.example.findlostitemsapp.pages.uiutils;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;
public class UiUtils {
    public static void setColoredSpan(TextView textView, String fullText, String textToColor, String colorHex) {
        SpannableString spannable = new SpannableString(fullText);

        int start = fullText.indexOf(textToColor);
        if (start >= 0) {
            int end = start + textToColor.length();
            spannable.setSpan(
                    new ForegroundColorSpan(Color.parseColor(colorHex)),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }

        textView.setText(spannable);
    }
}
