package com.ehsanmashhadi.library.view;

import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;

class ViewFactory {

    static BaseView create(CountryPicker.ViewType viewType, Activity activity) {

        switch (viewType) {

            case DIALOG:
                return new MyDialog(activity);

            case BOTTOMSHEET:
                return new MyBottomSheetDialog(activity);

            default:
                return null;
        }
    }
}