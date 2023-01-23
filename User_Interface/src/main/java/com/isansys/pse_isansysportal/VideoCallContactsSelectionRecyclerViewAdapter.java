package com.isansys.pse_isansysportal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.isansys.common.VideoCallContact;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class VideoCallContactsSelectionRecyclerViewAdapter extends RecyclerView.Adapter<VideoCallContactsSelectionRecyclerViewAdapter.ViewHolder>
{
    private final ArrayList<VideoCallContactModel> contacts;
    private final LayoutInflater mInflater;
    private final Context context;

    public interface VideoCallContactsSelectionRecyclerViewAdapterInterface
    {
        void contactButtonClicked(int numberOfContactsSelected);
    }

    private final VideoCallContactsSelectionRecyclerViewAdapterInterface videoCallContactsSelectionRecyclerViewAdapterInterface;

    // Data is passed into the constructor
    VideoCallContactsSelectionRecyclerViewAdapter(Context context, VideoCallContactsSelectionRecyclerViewAdapterInterface videoCallContactsSelectionRecyclerViewAdapterInterface, ArrayList<VideoCallContact> contacts)
    {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.videoCallContactsSelectionRecyclerViewAdapterInterface = videoCallContactsSelectionRecyclerViewAdapterInterface;
        this.contacts = new ArrayList<>();

        contacts.forEach(c -> this.contacts.add(new VideoCallContactModel(c)));
    }

    // inflates the cell layout from xml when needed
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType)
    {
        View view = mInflater.inflate(R.layout.video_call_contact_button_for_selection, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        VideoCallContactModel model = contacts.get(position);
        holder.bind(model);
    }

    public ArrayList<VideoCallContact> getSelectedContacts()
    {
        ArrayList<VideoCallContact> selected = new ArrayList<>();

        for (VideoCallContactModel contact : contacts)
        {
            if(contact.isSelected())
            {
                selected.add(contact.getVideoCallContact());
            }
        }

        return selected;
    }

    @Override
    public int getItemCount()
    {
        return contacts.size();
    }

    public static class VideoCallContactModel
    {
        final VideoCallContact videoCallContact;
        private boolean isSelected;

        VideoCallContactModel(VideoCallContact videoCallContact)
        {
            this.videoCallContact = videoCallContact;
            this.isSelected = false;
        }

        public boolean isSelected()
        {
            return this.isSelected;
        }

        public void setIsSelected(final boolean isSelected)
        {
            this.isSelected = isSelected;
        }

        public VideoCallContact getVideoCallContact()
        {
            return this.videoCallContact;
        }
    }

    // Stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        final TextView buttonContact;

        ViewHolder(View itemView)
        {
            super(itemView);

            // Its a text view as this is how the example code worked as could not get the onClick to work for a Button but works for a TextView
            buttonContact = itemView.findViewById(R.id.buttonContact);
        }

        void bind(final VideoCallContactModel videoCallContactModel)
        {
            VideoCallContact contact = videoCallContactModel.getVideoCallContact();

            buttonContact.setText(contact.name);

            // Disable the button if the Contact is unavailable
            if (!contact.available)
            {
                buttonContact.setOnClickListener(null);
                buttonContact.setBackground(ContextCompat.getDrawable(context, R.drawable.button_gray));
            }
            else
            {
                this.setContactColor(videoCallContactModel.isSelected());

                buttonContact.setOnClickListener(v -> {
                    videoCallContactModel.setIsSelected(!videoCallContactModel.isSelected());
                    this.setContactColor(videoCallContactModel.isSelected());

                    int numberOfContactsSelected = getSelectedContacts().size();
                    videoCallContactsSelectionRecyclerViewAdapterInterface.contactButtonClicked(numberOfContactsSelected);
                });
            }
        }

        void setContactColor(boolean isSelected)
        {
            if (isSelected)
            {
                buttonContact.setBackground(ContextCompat.getDrawable(context, R.drawable.button_green));
            }
            else
            {
                buttonContact.setBackground(ContextCompat.getDrawable(context, R.drawable.button_blue));
            }
        }
    }
}