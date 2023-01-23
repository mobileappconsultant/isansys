package com.isansys.pse_isansysportal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FragmentWebpageSelection extends FragmentIsansys
{
    private final String TAG = this.getClass().getSimpleName();

    private RecyclerView recyclerView;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.webpage_selection, container, false);

        recyclerView = v.findViewById(R.id.recyclerView);

        ArrayList<WebPageButtonDescriptor> webPageButtonDescriptors = main_activity_interface.getWebPageButtons();

        showWebpagesOnScreen(webPageButtonDescriptors);

        return v;
    }

    private void showWebpagesOnScreen(ArrayList<WebPageButtonDescriptor> webPageButtonDescriptors)
    {
        int numberOfColumns = 1;
        if (webPageButtonDescriptors.size() > 5)
        {
            numberOfColumns = 2;
        }

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));
        WebPageButtonRecyclerViewAdapter adapter = new WebPageButtonRecyclerViewAdapter(getContext(), webPageButtonDescriptors);
        adapter.setClickListener(buttonInformation -> {
            Log.d(TAG, "onItemClick : " + buttonInformation.url + " : " + buttonInformation.description);
            main_activity_interface.showWebpagePopup(buttonInformation.url);
        });
        recyclerView.setAdapter(adapter);
    }
}
