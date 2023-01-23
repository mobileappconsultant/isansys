package com.isansys.pse_isansysportal;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

public class PopupRecyclingReminder extends IsansysPopupDialogFragment
{
    public interface Callback
    {
        void touchEventFromPopupWindow();

        void dismissButtonPressed();
    }

    private Callback callback;

    public PopupRecyclingReminder(MainActivityInterface main_activity_interface)
    {
        super(main_activity_interface);
    }

    public void setArguments(Callback callback)
    {
        this.callback = callback;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final Dialog dialog = new Dialog(getActivity())
        {
            @Override
            public boolean dispatchTouchEvent(@NonNull MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    callback.touchEventFromPopupWindow();
                }

                return super.dispatchTouchEvent(event);
            }
        };

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.pop_up_recycling_reminder);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        Button btnDismiss = dialog.findViewById(R.id.dismiss);
        btnDismiss.setOnClickListener(v -> {
            callback.dismissButtonPressed();
            dismissPopupIfVisible();
        });

        return dialog;
    }


    public void dismissPopupIfVisible()
    {
        if (getDialog() != null)
        {
            if (getDialog().isShowing())
            {
                getDialog().dismiss();
            }
        }
    }

    @Override
    public void onDismiss(@NotNull DialogInterface dialog)
    {
        setShowNotInProgress();

        callback.dismissButtonPressed();

        super.onDismiss(dialog);
    }
}
