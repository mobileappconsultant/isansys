package com.isansys.pse_isansysportal;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;

import com.isansys.common.enums.SensorType;
import com.isansys.common.measurements.VitalSignType;
import com.isansys.patientgateway.deviceInfo.SetupModeLog;
import com.isansys.common.measurements.MeasurementVitalSign;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FragmentGraphLifetouchNormalMode extends FragmentIsansys
{
    private final String TAG = this.getClass().getSimpleName();

    private FragmentGraphLifetouchNormalModeHeartRate fragment_heart_rate = null;
    private FragmentGraphLifetouchNormalModeRespirationRate fragment_respiration_rate = null;

    @Override
    public void onCreate(Bundle saved)
    {
        Log.d(TAG, "onCreate");

        fragment_heart_rate = new FragmentGraphLifetouchNormalModeHeartRate();
        fragment_respiration_rate = new FragmentGraphLifetouchNormalModeRespirationRate();

        super.onCreate(saved);
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        Log.d(TAG, "onCreateView");

        return inflater.inflate(R.layout.lifetouch_normal_mode, container, false);
    }


    @Override
    public void onResume()
    {
        Log.d(TAG, "onResume");

        replaceFragmentIfSafe(R.id.fragment_graph_lifetouch_heart_rate, fragment_heart_rate);
        replaceFragmentIfSafe(R.id.fragment_graph_lifetouch_respiration_rate, fragment_respiration_rate);

        super.onResume();
    }


    @Override
    public void onDestroy()
    {
        Log.d(TAG, "onDestroy");

        Fragment fragment;

        fragment = getChildFragmentManager().findFragmentById(R.id.fragment_graph_lifetouch_heart_rate);

        if(isFragmentAddedAndSafeToRemove(fragment, getActivity()))
        {
            getChildFragmentManager().beginTransaction().remove(fragment).commit();
        }


        fragment = getChildFragmentManager().findFragmentById(R.id.fragment_graph_lifetouch_respiration_rate);

        if(isFragmentAddedAndSafeToRemove(fragment, getActivity()))
        {
            getChildFragmentManager().beginTransaction().remove(fragment).commit();
        }

        super.onDestroy();
    }


    public <T extends MeasurementVitalSign> void redrawNormalMeasurementGraph(VitalSignType vital_sign_type, ArrayList<T> cached_measurements)
    {
        switch (vital_sign_type)
        {
            case HEART_RATE:
            {
                if (UtilityFunctions.isFragmentAddedAndResumed(fragment_heart_rate))
                {
                    fragment_heart_rate.redrawNormalMeasurementGraph(cached_measurements);
                }
            }
            break;

            case RESPIRATION_RATE:
            {
                if (UtilityFunctions.isFragmentAddedAndResumed(fragment_respiration_rate))
                {
                    fragment_respiration_rate.redrawNormalMeasurementGraph(cached_measurements);
                }
            }
            break;
        }
    }


    void scrollOrScaleToTimeWindow(long start_timestamp, long end_timestamp)
    {
        if(UtilityFunctions.isFragmentAddedAndResumed(fragment_respiration_rate))
        {
            fragment_respiration_rate.scrollOrScaleToTimeWindow(start_timestamp, end_timestamp);
        }
        else
        {
            Log.d(TAG, "scrollOrScaleToTimeWindow : Respiration rate graph isn't added");
        }

        if(UtilityFunctions.isFragmentAddedAndResumed(fragment_heart_rate))
        {
            fragment_heart_rate.scrollOrScaleToTimeWindow(start_timestamp, end_timestamp);
        }
        else
        {
            Log.d(TAG, "scrollOrScaleToTimeWindow : Heart Rate rate graph isn't added");
        }
    }


    void scaleGraph(ScaleGestureDetector detector)
    {
        if(UtilityFunctions.isFragmentAddedAndResumed(fragment_respiration_rate))
        {
            fragment_respiration_rate.scaleGraph(detector);
        }
        else
        {
            Log.d(TAG, "scaleGraph : Respiration rate graph isn't added");
        }

        if(UtilityFunctions.isFragmentAddedAndResumed(fragment_heart_rate))
        {
            fragment_heart_rate.scaleGraph(detector);
        }
        else
        {
            Log.d(TAG, "scaleGraph : Heart Rate rate graph isn't added");
        }
    }


    public void redrawSetupModeLogGraph(ArrayList<SetupModeLog> setup_mode_log)
    {
        if (UtilityFunctions.isFragmentAddedAndResumed(fragment_heart_rate))
        {
            fragment_heart_rate.redrawSetupModeLogGraph(setup_mode_log);
        }

        if (UtilityFunctions.isFragmentAddedAndResumed(fragment_respiration_rate))
        {
            fragment_respiration_rate.redrawSetupModeLogGraph(setup_mode_log);
        }
    }


    private void hideSetupModeBlobs()
    {
        if (UtilityFunctions.isFragmentAddedAndResumed(fragment_heart_rate))
        {
            fragment_heart_rate.hideSetupModeBlobs();
        }

        if (UtilityFunctions.isFragmentAddedAndResumed(fragment_respiration_rate))
        {
            fragment_respiration_rate.hideSetupModeBlobs();
        }
    }


    public <T extends MeasurementVitalSign> void redrawAnnotationsOnGraph(ArrayList<T> cached_measurements_list)
    {
        if (UtilityFunctions.isFragmentAddedAndResumed(fragment_heart_rate))
        {
            fragment_heart_rate.redrawAnnotationsOnGraph(cached_measurements_list);
        }

        if (UtilityFunctions.isFragmentAddedAndResumed(fragment_respiration_rate))
        {
            fragment_respiration_rate.redrawAnnotationsOnGraph(cached_measurements_list);
        }
    }


    public <T extends MeasurementVitalSign> void redrawManuallyEnteredMeasurementGraph(ArrayList<T> cached_measurements)
    {
        if (UtilityFunctions.isFragmentAddedAndResumed(fragment_heart_rate))
        {
            fragment_heart_rate.redrawMeasurementGraph(cached_measurements, true);
        }

        if (UtilityFunctions.isFragmentAddedAndResumed(fragment_respiration_rate))
        {
            fragment_respiration_rate.redrawMeasurementGraph(cached_measurements, true);
        }
    }


    public void showSetupModeBlobs(boolean show)
    {
        if (show == true)
        {
            redrawSetupModeLogGraph(main_activity_interface.getSetupModeLog(SensorType.SENSOR_TYPE__LIFETOUCH));
        }
        else
        {
            hideSetupModeBlobs();
        }
    }

}
