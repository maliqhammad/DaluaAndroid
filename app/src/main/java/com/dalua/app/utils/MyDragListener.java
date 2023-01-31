package com.dalua.app.utils;

import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;

import static android.content.ContentValues.TAG;

public class
MyDragListener implements View.OnDragListener {

    @Override
    public boolean onDrag(View v, DragEvent event) {
        int action = event.getAction();
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                // do nothing
                break;
            case DragEvent.ACTION_DRAG_ENTERED:

                break;
            case DragEvent.ACTION_DRAG_EXITED:

                break;
            case DragEvent.ACTION_DROP:
                // Dropped, reassign View to ViewGroup
                View view = (View) event.getLocalState();
                ViewGroup owner = (ViewGroup) view.getParent();
                owner.removeView(view);
//                Toast.makeText(MainActivity.this, ((Button)view).getText(), 1000).show();
                Log.d(TAG, "onDrag: your here ");
                view.setVisibility(View.VISIBLE);
                break;
            case DragEvent.ACTION_DRAG_ENDED:

            default:
                break;
        }
        return true;
    }
}