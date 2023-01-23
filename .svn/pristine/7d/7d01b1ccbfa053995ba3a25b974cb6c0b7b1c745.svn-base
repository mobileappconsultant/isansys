package com.isansys.pse_isansysportal;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class WebPageButtonRecyclerViewAdapter extends RecyclerView.Adapter<WebPageButtonRecyclerViewAdapter.ViewHolder>
{
    private final ArrayList<WebPageButtonDescriptor> button_information_list;

    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // Data is passed into the constructor
    WebPageButtonRecyclerViewAdapter(Context context, ArrayList<WebPageButtonDescriptor> button_information_list)
    {
        this.mInflater = LayoutInflater.from(context);

        this.button_information_list = button_information_list;
    }

    // inflates the cell layout from xml when needed
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType)
    {
        View view = mInflater.inflate(R.layout.webpage_button_for_selection, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position)
    {
        WebPageButtonDescriptor button_information = button_information_list.get(position);

        holder.buttonText.setText(button_information.description);
        holder.buttonText.setBackground(button_information.button_drawable);
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

        ViewHolder(View itemView)
        {
            super(itemView);

            // Its a text view as this is how the example code worked as could not get the onClick to work for a Button but works for a TextView
            buttonText = itemView.findViewById(R.id.buttonWebpageName);
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
        void onItemClick(WebPageButtonDescriptor button_information);
    }
}