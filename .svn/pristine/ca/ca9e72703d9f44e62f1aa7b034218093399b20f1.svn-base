package com.isansys.pse_isansysportal;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class ObservationSetSelectionRecyclerViewAdapter extends RecyclerView.Adapter<ObservationSetSelectionRecyclerViewAdapter.ViewHolder>
{
    private final ArrayList<ManuallyEnteredVitalSignDescriptor> vital_signs;

    private final LayoutInflater mInflater;
    private AdapterInterface adapter_interface;

    // Data is passed into the constructor
    ObservationSetSelectionRecyclerViewAdapter(Context context, ArrayList<ManuallyEnteredVitalSignDescriptor> vital_signs)
    {
        this.mInflater = LayoutInflater.from(context);

        this.vital_signs = vital_signs;
    }

    // inflates the cell layout from xml when needed
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType)
    {
        View view = mInflater.inflate(R.layout.observation_display_button_for_selection, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        ManuallyEnteredVitalSignDescriptor vital_sign_descriptor = vital_signs.get(position);

        holder.buttonVitalSignName.setText(vital_sign_descriptor.display_name);

        ManualVitalSignBeingEntered vital_sign_being_entered = this.adapter_interface.getManualVitalSignValueForThisVitalSignDescriptor(vital_sign_descriptor);

        if (vital_sign_being_entered != null)
        {
            holder.textValue.setText(vital_sign_being_entered.getValue());
            holder.imageCancel.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.textValue.setText("");
            holder.imageCancel.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount()
    {
        return vital_signs.size();
    }


    // Stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        final TextView buttonVitalSignName;
        final ImageView imageCancel;
        final TextView textValue;

        ViewHolder(View itemView)
        {
            super(itemView);

            // Its a text view as this is how the example code worked as could not get the onClick to work for a Button but works for a TextView
            buttonVitalSignName = itemView.findViewById(R.id.buttonVitalSignName);
            buttonVitalSignName.setOnClickListener(v -> {
                if (adapter_interface != null)
                {
                    adapter_interface.onButtonItemClick(vital_signs.get(getBindingAdapterPosition()));
                }
            });

            imageCancel = itemView.findViewById(R.id.imageCancel);
            imageCancel.setOnClickListener(v -> {
                if (adapter_interface != null)
                {
                    adapter_interface.onCancelItemClick(vital_signs.get(getBindingAdapterPosition()));
                }
            });

            textValue = itemView.findViewById(R.id.textValue);
        }
    }


    void setAdapterInterface(AdapterInterface adapter_interface)
    {
        this.adapter_interface = adapter_interface;
    }


    public interface AdapterInterface
    {
        void onButtonItemClick(ManuallyEnteredVitalSignDescriptor vital_sign_selected);
        void onCancelItemClick(ManuallyEnteredVitalSignDescriptor vital_sign_selected);

        ManualVitalSignBeingEntered getManualVitalSignValueForThisVitalSignDescriptor(ManuallyEnteredVitalSignDescriptor vital_sign_selected);
    }
}