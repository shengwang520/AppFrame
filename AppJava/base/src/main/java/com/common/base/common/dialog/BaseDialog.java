package com.common.base.common.dialog;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;

import com.common.base.R;

/**
 * dialog 上层基类，确定样式
 */
public class BaseDialog extends Dialog {

    public BaseDialog(@NonNull Context context) {
        super(context, R.style.Dialog);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
