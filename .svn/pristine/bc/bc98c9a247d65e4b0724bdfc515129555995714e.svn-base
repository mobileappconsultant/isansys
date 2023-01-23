package com.isansys.pse_isansysportal;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class ManualVitalSignsValidityTimeSelectionRecyclerViewAdapter extends RecyclerView.Adapter<ManualVitalSignsValidityTimeSelectionRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<VitalSignValidityTimeDescriptor> validity_times;

    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    private boolean enable_entry = false;

    private final Context context;

    // Data is passed into the constructor
    ManualVitalSignsValidityTimeSelectionRecyclerViewAdapter(Context context, ArrayList<VitalSignValidityTimeDescriptor> validity_times)
    {
        this.context = context;

        this.mInflater = LayoutInflater.from(context);

        this.validity_times = validity_times;
    }

    // inflates the cell layout from xml when needed
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType)
    {
        View view = mInflater.inflate(R.layout.vital_sign_validity_time_button, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        holder.buttonVitalSignValidityTime.setText(validity_times.get(position).display_value);

        if (enable_entry)
        {
            holder.buttonVitalSignValidityTime.setBackground(ContextCompat.getDrawable(context, R.drawable.button_blue));
        }
        else
        {
            holder.buttonVitalSignValidityTime.setBackground(ContextCompat.getDrawable(context, R.drawable.button_gray));
        }
    }


    @Override
    public int getItemCount()
    {
        return validity_times.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        final TextView buttonVitalSignValidityTime;

        ViewHolder(View itemView)
        {
            super(itemView);

            // Its a text view as this is how the example code worked as could not get the onClick to work for a Button but works for a TextView
            buttonVitalSignValidityTime = itemView.findViewById(R.id.buttonVitalSignValidityTime);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            if (mClickListener != null)
            {
                mClickListener.onItemClick(validity_times.get(getBindingAdapterPosition()));
            }
        }
    }


    void setClickListener(ItemClickListener itemClickListener)
    {
        this.mClickListener = itemClickListener;
    }


    void enableEntry(Boolean enable_entry)
    {
        this.enable_entry = enable_entry;
    }


    public interface ItemClickListener
    {
        void onItemClick(VitalSignValidityTimeDescriptor vital_sign);
    }
}