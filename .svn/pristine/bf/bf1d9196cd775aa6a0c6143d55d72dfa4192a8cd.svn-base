package com.isansys.pse_isansysportal;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

// Code from https://stackoverflow.com/questions/33434626/get-list-of-checked-checkboxes-from-recyclerview-android
public class AnnotationSelectionRecyclerViewAdapter extends RecyclerView.Adapter<AnnotationSelectionRecyclerViewAdapter.MyViewHolder>
{
    interface OnItemCheckListener
    {
        void onItemCheck(AnnotationDescriptor item);
        void onItemUncheck(AnnotationDescriptor item);
    }

    private final ArrayList<AnnotationDescriptor> items;

    private final LayoutInflater mInflater;

    private final OnItemCheckListener onItemClick;

    // Data is passed into the constructor
    AnnotationSelectionRecyclerViewAdapter(Context context, ArrayList<AnnotationDescriptor> items, OnItemCheckListener onItemCheckListener)
    {
        this.mInflater = LayoutInflater.from(context);

        this.items = items;
        this.onItemClick = onItemCheckListener;
    }

    // Inflates the cell layout from xml when needed
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType)
    {
        View view = mInflater.inflate(R.layout.annotation_button_for_selection, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position)
    {
        AnnotationDescriptor vital_sign = items.get(position);

        holder.checkbox.setText(vital_sign.name);

        final AnnotationDescriptor currentItem = items.get(position);

        holder.setOnClickListener(v -> {
            holder.checkbox.setChecked(!holder.checkbox.isChecked());

            if (holder.checkbox.isChecked())
            {
                onItemClick.onItemCheck(currentItem);
            }
            else
            {
                onItemClick.onItemUncheck(currentItem);
            }
        });
    }


    @Override
    public int getItemCount()
    {
        return items.size();
    }


    // stores and recycles views as they are scrolled off screen
    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        final CheckBox checkbox;
        final View itemView;

        MyViewHolder(View itemView)
        {
            super(itemView);
            this.itemView = itemView;

            checkbox = itemView.findViewById(R.id.checkBoxAnnotationName);
            checkbox.setClickable(false);
        }

        void setOnClickListener(View.OnClickListener onClickListener)
        {
            itemView.setOnClickListener(onClickListener);
        }
    }
}