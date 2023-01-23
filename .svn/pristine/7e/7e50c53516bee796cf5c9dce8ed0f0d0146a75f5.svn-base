package com.isansys.pse_isansysportal;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.isansys.common.measurements.MeasurementAnnotation;

import java.util.ArrayList;
import java.util.HashMap;

public class PopupAnnotationViewer extends IsansysPopupDialogFragment
{
    private ArrayList<MeasurementAnnotation> annotations;


    public PopupAnnotationViewer(MainActivityInterface main_activity_interface)
    {
        super(main_activity_interface);
    }


    public void setArguments(ArrayList<MeasurementAnnotation> annotations)
    {
        this.annotations = annotations;
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
                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    main_activity_interface.touchEventSoResetTimers();
                }

                return super.dispatchTouchEvent(event);
            }
        };

        dialog.setOnDismissListener(this);

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.pop_up_annotation_viewer);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        ListView listview = dialog.findViewById(R.id.listview);

        ArrayList<HashMap<String,String>> list = new ArrayList<>();
        HashMap<String,String> item;

        for(MeasurementAnnotation annotation : annotations)
        {
            item = new HashMap<>();
            item.put( "time", TimestampConversion.convertDateToHumanReadableStringDayHoursMinutes(annotation.timestamp_in_ms));
            item.put( "annotation", annotation.annotation);
            list.add(item);
        }

        AnnotationAdapter sa = new AnnotationAdapter(main_activity_interface.getAppContext(), list, R.layout.list_time_and_text, new String[] { "time", "annotation" }, new int[] {R.id.textViewTimestamp, R.id.textViewAnnotation});
        listview.setAdapter(sa);

        Button buttonPopupDismiss = dialog.findViewById(R.id.buttonHistoricalSetupModeViewerDismiss);
        buttonPopupDismiss.setOnClickListener(v -> dismiss());

        return dialog;
    }


    @Override
    public void onResume()
    {
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            Window window = dialog.getWindow();
            if (window != null)
            {
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = (int)(getResources().getDisplayMetrics().widthPixels * 0.95);
                params.height = (int)(getResources().getDisplayMetrics().heightPixels * 0.95);
                window.setAttributes(params);
            }
        }

        super.onResume();
    }
}
