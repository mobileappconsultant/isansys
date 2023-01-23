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

import com.isansys.common.enums.SensorType;

import org.jetbrains.annotations.NotNull;

public class PopupRemoveFromEws extends IsansysPopupDialogFragment
{
    public interface Callback
    {
        void touchEventFromPopupWindow();
        void keepButtonPressed(SensorType sensor_type);
        void removeButtonPressed(SensorType sensor_type);
    }

    private PopupRemoveFromEws.Callback callback;

    private final SensorType sensor_type;

    public PopupRemoveFromEws(MainActivityInterface main_activity_interface, SensorType sensor_type)
    {
        super(main_activity_interface);

        this.sensor_type = sensor_type;
    }

    public void setArguments(PopupRemoveFromEws.Callback callback)
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
        dialog.setContentView(R.layout.pop_up_remove_from_ews);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Force the user to pick an option
        setCancelable(false);

        dialog.show();

        main_activity_interface.highlightFirstWordInTextView(dialog.findViewById(R.id.textViewLineOne));
        main_activity_interface.highlightFirstWordInTextView(dialog.findViewById(R.id.textViewLineTwo));

        Button buttonRemove = dialog.findViewById(R.id.buttonRemove);
        buttonRemove.setOnClickListener(v -> {
            callback.removeButtonPressed(sensor_type);
            dismissPopupIfVisible();
        });

        Button buttonKeep = dialog.findViewById(R.id.buttonKeep);
        buttonKeep.setOnClickListener(v -> {
            callback.keepButtonPressed(sensor_type);
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

        super.onDismiss(dialog);
    }
}
