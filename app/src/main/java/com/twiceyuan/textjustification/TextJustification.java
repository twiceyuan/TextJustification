package com.twiceyuan.textjustification;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ImageSpan;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by twiceYuan on 2017/5/6.
 * <p>
 * 使用 Spannable 让 TextView 文字两端对齐。
 *
 * 原理是测量原始行中空格占用的宽度总和，让行尾的空白平均到所有空格中去。使用 Span 来代替空格保证空格的宽度不受字体影响。
 */
public class TextJustification {

    public static void justify(final TextView textView) {

        // 标记是否已经进行过处理，因为 post 回调会在处理后继续触发
        final AtomicBoolean isJustify = new AtomicBoolean(false);

        // 处理前原始字符串
        final String textString = textView.getText().toString();

        // 用于测量文字宽度，计算分散对齐后的空格宽度
        final TextPaint textPaint = textView.getPaint();

        // 分散对齐后的文字
        final SpannableStringBuilder builder = new SpannableStringBuilder();

        // 在 TextView 完成测量绘制之后执行
        textView.post(new Runnable() {
            @Override
            public void run() {

                // 判断是否已经处理过
                if (!isJustify.get()) {

                    // 获取原始布局总行数
                    final int lineCount = textView.getLineCount();
                    // 获取 textView 的宽度
                    final int textViewWidth = textView.getWidth();

                    for (int i = 0; i < lineCount; i++) {

                        // 获取行首字符位置和行尾字符位置来截取每行的文字
                        int lineStart = textView.getLayout().getLineStart(i);
                        int lineEnd = textView.getLayout().getLineEnd(i);

                        String lineString = textString.substring(lineStart, lineEnd);

                        // 最后一行不做处理
                        if (i == lineCount - 1) {
                            builder.append(new SpannableString(lineString));
                            break;
                        }

                        // 行首行尾去掉空格以保证处理后的对齐效果
                        String trimSpaceText = lineString.trim();
                        String removeSpaceText = lineString.replaceAll(" ", "");

                        float removeSpaceWidth = textPaint.measureText(removeSpaceText);
                        float spaceCount = trimSpaceText.length() - removeSpaceText.length();

                        // 两端对齐时每个空格的重新计算的宽度
                        float eachSpaceWidth = (textViewWidth - removeSpaceWidth) / spaceCount;

                        SpannableString spannableString = new SpannableString(lineString);
                        for (int j = 0; j < trimSpaceText.length(); j++) {
                            char c = trimSpaceText.charAt(j);
                            if (c == ' ') {
                                // 之前使用了 AbsoluteSizeSpan 来设置空格的 span，不过会行高所以替换为了 ImageSpan
                                // AbsoluteSizeSpan spaceSpan = new AbsoluteSizeSpan(dp2px(context, eachSpaceWidth));
                                // 使用透明的 drawable 来填充空格部分
                                Drawable drawable = new ColorDrawable(0x00ffffff);
                                drawable.setBounds(0, 0, (int) eachSpaceWidth, 0);
                                ImageSpan span = new ImageSpan(drawable);
                                spannableString.setSpan(span, j, j + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        }

                        builder.append(spannableString);
                    }

                    textView.setText(builder);
                    // 标记处理完毕
                    isJustify.set(true);
                }
            }
        });
    }
}
