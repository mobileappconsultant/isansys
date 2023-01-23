package com.isansys.pse_isansysportal;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

public class ObservationSetConfirmationRecyclerViewAdapter extends RecyclerView.Adapter<ObservationSetConfirmationRecyclerViewAdapter.ViewHolder>
{
    private final MeasurementSetBeingEntered observation_set_being_entered;

    private final LayoutInflater mInflater;
    private AdapterInterface adapter_interface;

    // Data is passed into the constructor
    ObservationSetConfirmationRecyclerViewAdapter(Context context, MeasurementSetBeingEntered observation_set_being_entered)
    {
        this.mInflater = LayoutInflater.from(context);

        this.observation_set_being_entered = observation_set_being_entered;
    }

    // inflates the cell layout from xml when needed
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType)
    {
        View view = mInflater.inflate(R.layout.observation_display_row_for_confirmation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        ManualVitalSignBeingEntered vital_sign = observation_set_being_entered.getMeasurements().get(position);

        String name = vital_sign.getHumanReadableName();
        String value = vital_sign.getValue();
        int ews_score = vital_sign.getEwsScore();

        holder.textVitalSignName.setText(name);
        holder.textVitalSignValue.setText(value);
        holder.viewEarlyWarningScore.setBackground(adapter_interface.getDrawable(ews_score));
    }


    @Override
    public int getItemCount()
    {
        return observation_set_being_entered.getSize();
    }


    // Stores and recycles views as they are scrolled off screen
    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        final TextView textVitalSignName;
        final TextView textVitalSignValue;
        final View viewEarlyWarningScore;

        ViewHolder(View itemView)
        {
            super(itemView);

            textVitalSignName = itemView.findViewById(R.id.textVitalSignName);
            textVitalSignValue = itemView.findViewById(R.id.textVitalSignValue);
            viewEarlyWarningScore = itemView.findViewById(R.id.viewEarlyWarningScore);
        }
    }


    void setAdapterInterface(AdapterInterface adapter_interface)
    {
        this.adapter_interface = adapter_interface;
    }


    public interface AdapterInterface
    {
        Drawable getDrawable(int ews_score);
    }
}