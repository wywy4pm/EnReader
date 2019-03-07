package com.arun.ebook.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.arun.ebook.common.Constant;
import com.arun.ebook.listener.ClickWordListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018/4/16.
 */

public class StringUtils {

    public static void setEachWord(TextView textView, String text, ClickWordListener clickWordListener) {
        SpannableString spannable = new SpannableString(text);
        Integer[] indexs = getIndexs(text);
        int start = 0;
        int end = 0;
        for (int i = 0; i < indexs.length; i++) {
            end = indexs[i];
            if (end > start) {
                Log.d("TAG", "wordStart = " + start + "   wordEnd= " + end);
                spannable.setSpan(getClickableSpan(clickWordListener), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            start = end + 1;
        }
        textView.setText(spannable);
        textView.setHighlightColor(Color.RED);
    }

    public static ClickableSpan getClickableSpan(final ClickWordListener clickWordListener) {
        return new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                TextView tv = (TextView) widget;
                Log.d("TAG", "SelectionStart =" + tv.getSelectionStart() + "   SelectionEnd = " + tv.getSelectionEnd());
                if (tv.getSelectionStart() != -1 && tv.getSelectionEnd() != -1) {
                    String word = tv.getText().subSequence(tv.getSelectionStart(), tv.getSelectionEnd()).toString();
                    if (clickWordListener != null) {
                        clickWordListener.onClickWord(word);
                    }
                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                //super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#15140F"));
                ds.setUnderlineText(false);
            }
        };
    }

    public static Integer[] getIndexs(String string) {
        List<Integer> indexAry = new ArrayList<>();
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z') {//英文字符
            } else {
                indexAry.add(i);
            }
        }
        return indexAry.toArray(new Integer[0]);
    }

    public static boolean isEnNum(char c) {
        if (Character.isLetterOrDigit(c) || c == '-') {
            return true;
        }
        return false;
    }

    public static boolean isEnChar(char c) {
        if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '-') {//英文字符
            return true;
        }
        return false;
    }

    public static boolean isSpace(char c) {
        if (c == ' ') {//空格
            return true;
        }
        return false;
    }

    /*public static boolean isPunctuation(char c) {
        if (Pattern.matches("\\p{P}", String.valueOf(c)) && c != '\'') {
            return true;
        }
        return false;
    }*/

    public static boolean isAddSpacePun(String s) {
        switch (s) {
            case ".":
            case ",":
            case "?":
            case "!":
            case ":":
            case ";":
                return true;
        }
        return false;
    }

    //['.', ',', '"', '"', '?', ';', ':', '!', '(', ')', '\'', '...']
    public static boolean isPunctuation(String s) {
        switch (s) {
            case ".":
            case ",":
            case "\"":
            case "?":
            case ";":
            case ":":
            case "!":
            case "(":
            case ")":
            case "...":
                return true;
        }
        return false;
    }

    public static void setEnTextFont(Context context, TextView content) {
        File[] files = Utils.readFontsFile(context, "new_fonts", Constant.PATH_FONT_NEW);
        if (files != null && files.length > 0) {
            Typeface typeface = Typeface.createFromFile(files[0]);
            if (typeface != null) {
                content.setTypeface(typeface);
            }
        }
    }

    /**
     * 复制到剪切板
     */
    public static void copyWord(Context mContext, String text) {
        ClipboardManager clip = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clip != null) {
            clip.setPrimaryClip(ClipData.newPlainText("CopyWord", text));
        }
    }
}
