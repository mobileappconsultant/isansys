package com.isansys.pse_isansysportal;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class ManualVitalSignsButtonRecyclerViewAdapter extends RecyclerView.Adapter<ManualVitalSignsButtonRecyclerViewAdapter.ViewHolder>
{
    private final ArrayList<ManualVitalSignButtonDescriptor> button_information_list;

    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    private final boolean show_rhs_description;

    // Data is passed into the constructor
    ManualVitalSignsButtonRecyclerViewAdapter(Context context, ArrayList<ManualVitalSignButtonDescriptor> button_information_list, boolean show_rhs_description)
    {
        this.mInflater = LayoutInflater.from(context);

        this.button_information_list = button_information_list;

        this.show_rhs_description = show_rhs_description;
    }

    // inflates the cell layout from xml when needed
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType)
    {
        View view = mInflater.inflate(R.layout.observation_manual_vital_sign_button_for_selection, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        ManualVitalSignButtonDescriptor button_information = button_information_list.get(position);

        holder.buttonText.setText(button_information.button_text);
        holder.buttonText.setTextColor(button_information.button_text_colour);
        holder.buttonText.setBackground(button_information.button_drawable);

        if (show_rhs_description)
        {
            holder.textDescription.setText(button_information.button_rhs_description);
        }
        else
        {
            holder.textDescription.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount()
    {
        return button_information_list.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        final TextView buttonText;

        final TextView textDescription;
        //final TextView textValue;
        //final TextView textValidityTime;

        ViewHolder(View itemView)
        {
            super(itemView);

            // Its a text view as this is how the example code worked as could not get the onClick to work for a Button but works for a TextView
            buttonText = itemView.findViewById(R.id.buttonVitalSignName);
            textDescription = itemView.findViewById(R.id.textDescription);
            //textValue = itemView.findViewById(R.id.textValue);
            //textValidityTime = itemView.findViewById(R.id.textValidityTime);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            if (mClickListener != null)
            {
                mClickListener.onItemClick(button_information_list.get(getBindingAdapterPosition()));
            }
        }
    }


    void setClickListener(ItemClickListener itemClickListener)
    {
        this.mClickListener = itemClickListener;
    }


    public interface ItemClickListener
    {
        void onItemClick(ManualVitalSignButtonDescriptor button_information);
    }
}