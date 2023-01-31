package com.ehsanmashhadi.library.view;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

class MyDialog extends android.app.Dialog implements BaseView {

    MyDialog(@NonNull Context context) {

        super(context);
    }

    @Override
    public void setView(View view) {
        this.setContentView(view);
    }


    @Override
    public void dismissView() {

        this.dismiss();
    }

    @Override
    public void showView() {
        this.show();
    }
}