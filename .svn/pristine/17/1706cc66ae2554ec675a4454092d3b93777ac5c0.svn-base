package com.isansys.pse_isansysportal;

import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class FragmentAnnotationActionList extends FragmentIsansys implements AnnotationSelectionRecyclerViewAdapter.OnItemCheckListener
{
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.annotation_actions, container, false);

        recyclerView = v.findViewById(R.id.recycler_view_annotations);

        return v;
    }


    @Override
    public void onResume()
    {
        super.onResume();

        int numberOfColumns;

        if (main_activity_interface.isScreenLandscape())
        {
            numberOfColumns = 3;
        }
        else
        {
            numberOfColumns = 2;
        }

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));

        AnnotationSelectionRecyclerViewAdapter adapter = new AnnotationSelectionRecyclerViewAdapter(getContext(), main_activity_interface.getAnnotationActions(), this);

        recyclerView.setAdapter(adapter);
    }


    private final List<AnnotationDescriptor> currentSelectedItems = new ArrayList<>();

    @Override
    public void onItemCheck(AnnotationDescriptor item)
    {
        currentSelectedItems.add(item);

        processSelectedItems();
    }


    @Override
    public void onItemUncheck(AnnotationDescriptor item)
    {
        currentSelectedItems.remove(item);

        processSelectedItems();
    }


    private void processSelectedItems()
    {
        String selected = "";
        String comma_string = ", ";

        for (AnnotationDescriptor this_time : currentSelectedItems)
        {
            selected = selected.concat(this_time.name).concat(comma_string);
        }

        if (!selected.isEmpty())
        {
            selected = selected.substring(0, selected.length() - comma_string.length());
        }

        main_activity_interface.annotationActionsSelected(selected);
    }
}