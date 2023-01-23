package com.isansys.pse_isansysportal;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.isansys.remotelogging.RemoteLogging;

import org.jetbrains.annotations.NotNull;

public class IsansysPopupDialogFragment extends DialogFragment
{
    private boolean show_in_progress = false;

    public final MainActivityInterface main_activity_interface;
    public final RemoteLogging Log;

    public IsansysPopupDialogFragment(MainActivityInterface main_activity_interface)
    {
        this.main_activity_interface = main_activity_interface;

        this.Log = main_activity_interface.getMainActivityLogger();
    }

    @Override
    public void show(@NotNull FragmentManager manager, String tag)
    {
        if(!isShowInProgress())
        {
            setShowInProgress();

            super.show(manager, tag);
        }
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        dialog.setCanceledOnTouchOutside(true);

        dialog.setOnCancelListener(DialogInterface::dismiss);

        dialog.setOnDismissListener(this);

        return dialog;
    }


    @Override
    public void onDismiss(@NotNull DialogInterface dialog)
    {
        setShowNotInProgress();

        super.onDismiss(dialog);
    }


    public boolean isShowInProgress()
    {
        return show_in_progress;
    }


    public void setShowInProgress()
    {
        show_in_progress = true;
    }


    public void setShowNotInProgress()
    {
        show_in_progress = false;
    }
}
