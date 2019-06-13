package com.dokiwa.dokidoki.ui.span;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import androidx.core.content.res.ResourcesCompat;
import org.xml.sax.XMLReader;

public class HtmlSpan {

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String source) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(source);
        }
    }

    public static Spanned fromHtmlWithMoreTag(String source) {
        return Html.fromHtml(source, null, new Html.TagHandler() {
            int startTag;
            int endTag;

            @Override
            public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
                if (tag.equalsIgnoreCase("del") || tag.equalsIgnoreCase("s")) {
                    if (opening) {
                        startTag = output.length();
                    } else {
                        endTag = output.length();
                        output.setSpan(new StrikethroughSpan(), startTag, endTag, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }
        });
    }

    public static Spanned fromHtmlWithRresourceDarwableString(final Context context, String source) {
        Html.ImageGetter imgGetter = new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {
                int id = context.getResources()
                        .getIdentifier(source, "drawable", context.getPackageName());
                if (id != 0) {
                    Drawable d = ResourcesCompat.getDrawable(context.getResources(), id, null);
                    if (d != null) {
                        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                    }
                    return d;
                }
                return null;
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY, imgGetter, null);
        } else {
            return Html.fromHtml(source, imgGetter, null);
        }
    }

    public static Spanned fromHtmlWithRresourceDarwableId(final Context context, String source) {
        Html.ImageGetter imgGetter = new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {
                try {
                    int id = Integer.valueOf(source);
                    if (id != 0) {
                        return ResourcesCompat.getDrawable(context.getResources(), id, null);
                    }
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                }
                return null;
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY, imgGetter, null);
        } else {
            return Html.fromHtml(source, imgGetter, null);
        }
    }

    public static Spanned fromHtmlWithSolidUnderline(final Context context, String source,
                                                     final int color, final int lineHeight, final int offsetY) {
        return Html.fromHtml(source, null, new Html.TagHandler() {
            int startTag;
            int endTag;

            @Override
            public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
                if (tag.equalsIgnoreCase("underline")) {
                    if (opening) {
                        startTag = output.length();
                    } else {
                        endTag = output.length();
                        UnderlineSpan span = new UnderlineSpan(context, UnderlineSpan.Style.SOLID,
                                color, lineHeight, offsetY);
                        output.setSpan(span, startTag, endTag, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }
        });
    }
}
