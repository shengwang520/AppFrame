package com.common.base.common.holder;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.common.base.R;

/**
 * toolbar 顶部需要显示的自定义布局
 */
public class ToolbarView {
    private final TextView textView;

    public ToolbarView(Context context) {
        textView = new TextView(context);
    }

    /**
     * 设置文案
     */
    public ToolbarView setText(CharSequence charSequence) {
        textView.setText(charSequence);
        return this;
    }

    /**
     * 设置大小
     */
    public ToolbarView setTextSize(float size) {
        textView.setTextSize(size);
        return this;
    }

    /**
     * 设置文字颜色
     */
    public ToolbarView setTextColor(int color) {
        textView.setTextColor(color);
        return this;
    }

    /**
     * 是否加粗
     */
    public ToolbarView isBold() {
        textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        return this;
    }

    /**
     * 设置左边icon
     */
    public ToolbarView setLeftIcon(int left) {
        textView.setCompoundDrawablesWithIntrinsicBounds(left, 0, 0, 0);
        return this;
    }

    /**
     * 设置右边icon
     */
    public ToolbarView setRightIcon(int right) {
        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, right, 0);
        return this;
    }

    /**
     * 设置padding
     */
    public ToolbarView setPadding(int left, int top, int right, int bottom) {
        textView.setPadding(left, top, right, bottom);
        return this;
    }

    /**
     * 设置点击监听
     */
    public ToolbarView setOnClickListener(View.OnClickListener onClickListener) {
        textView.setOnClickListener(onClickListener);
        return this;
    }

    /**
     * 创建布局
     */
    public View create() {
        textView.setGravity(Gravity.CENTER);
        textView.setMaxLines(1);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        return textView;
    }

    /**
     * 创建统一的toolbar 标题样式
     *
     * @param title 标题
     */
    public static View createTitleView(Context context, String title) {
        return new ToolbarView(context)
                .setText(title)
                .setTextColor(R.color.white)
                .setTextSize(17)
                .isBold()
                .create();
    }
}
