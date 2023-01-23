package com.isansys.pse_isansysportal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.isansys.common.VideoCallContact;

import java.util.ArrayList;

public class FragmentVideoCallContactSelectionList extends FragmentIsansys implements VideoCallContactsSelectionRecyclerViewAdapter.VideoCallContactsSelectionRecyclerViewAdapterInterface
{
    private final String TAG = this.getClass().getSimpleName();

    private RecyclerView recyclerView;
    private VideoCallContactsSelectionRecyclerViewAdapter adapter;
    private Button buttonVideoCall;
    private TextView textViewNoContactsHaveBeenSetup;

    private int numberOfContactsSelected;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
    	View v = inflater.inflate(R.layout.video_call_contacts_selection, container, false);

        recyclerView = v.findViewById(R.id.recyclerView);

        buttonVideoCall = v.findViewById(R.id.buttonVideoCall);
        buttonVideoCall.setOnClickListener(x -> {
            ArrayList<VideoCallContact> selectedContacts = adapter.getSelectedContacts();
            if(selectedContacts.size() > 0) 
            {
                buttonVideoCall.setVisibility(View.GONE);

                main_activity_interface.patientRequestedVideoCall(selectedContacts);
            }
        });

        textViewNoContactsHaveBeenSetup = v.findViewById(R.id.textViewNoContactsHaveBeenSetup);

        // Wait for the getContactsFromServer to return before showing the controls to stop it flashing
        textViewNoContactsHaveBeenSetup.setVisibility(View.GONE);
        buttonVideoCall.setVisibility(View.GONE);

        return v;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        getContactsFromServer();
    }

    @Override
    public void contactButtonClicked(int numberOfContactsSelected)
    {
        this.numberOfContactsSelected = numberOfContactsSelected;

        Log.d(TAG, "contactButtonClicked : Number selected = " + numberOfContactsSelected);

        if (numberOfContactsSelected > 0)
        {
            buttonVideoCall.setVisibility(View.VISIBLE);
        }
        else
        {
            buttonVideoCall.setVisibility(View.INVISIBLE);
        }
    }

    public void enableVideoCallButtonIfContactsSelected()
    {
        if (numberOfContactsSelected > 0)
        {
            buttonVideoCall.setVisibility(View.VISIBLE);
        }
    }

    // Request the latest contacts for this Patient from the Server. Requested via realtime call to IsansysVideoCallContactsLookup service which in turn talks to the Hospital.
    // Up to Hospital to give current contacts list.
    private void getContactsFromServer()
    {
        main_activity_interface.requestPatientsVideoCallContactsFromServer();
    }

    private void autoAdjustNumberOfColumns(int numberOfThings)
    {
        int numberOfColumns = numberOfThings;
        if (numberOfColumns > 4)
        {
            numberOfColumns = 4;
        }

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));
    }

    // Show response from Hospital via IsansysVideoCallContactsLookup service on Lifeguard
    public void showContacts(ArrayList<VideoCallContact> contacts)
    {
        Log.d(TAG, "showContacts : size = " + contacts.size());

        if (contacts.size() > 0)
        {
            autoAdjustNumberOfColumns(contacts.size());

            textViewNoContactsHaveBeenSetup.setVisibility(View.GONE);

            for(VideoCallContact contact : contacts)
            {
                Log.d(TAG, "Contact : " + contact.name + " : " + contact.available);
            }

            adapter = new VideoCallContactsSelectionRecyclerViewAdapter(getContext(), this, contacts);
            recyclerView.setAdapter(adapter);
        }
        else
        {
            Log.e(TAG, "No contacts for this Gateway");

            textViewNoContactsHaveBeenSetup.setVisibility(View.VISIBLE);
        }
    }
}