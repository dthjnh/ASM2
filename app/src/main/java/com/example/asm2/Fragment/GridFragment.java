package com.example.asm2.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.asm2.AddDonationSiteUser.AddDonationSiteUserActivity;
import com.example.asm2.AdminandUserViewVolunteer.UserViewVolunteerActivity;
import com.example.asm2.DonorRegister.ViewDonorsActivity;
import com.example.asm2.EmergencyContact.EmergencyContactsActivity;
import com.example.asm2.R;

public class GridFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grid, container, false);

        LinearLayout addSiteLayout = view.findViewById(R.id.add_site_layout);
        LinearLayout viewDonorsLayout = view.findViewById(R.id.view_donors_layout);
        LinearLayout viewVolunteersLayout = view.findViewById(R.id.view_volunteers_layout);
        LinearLayout viewEmergencyLayout = view.findViewById(R.id.view_emergency_layout);

        // Set onClickListeners for each layout
        addSiteLayout.setOnClickListener(v -> {
            // Navigate to Add Donation Site Activity
            startActivity(new Intent(getActivity(), AddDonationSiteUserActivity.class));
        });

        viewDonorsLayout.setOnClickListener(v -> {
            // Navigate to View Donors Activity
            startActivity(new Intent(getActivity(), ViewDonorsActivity.class));
        });

        viewVolunteersLayout.setOnClickListener(v -> {
            // Navigate to View Volunteers Activity
            startActivity(new Intent(getActivity(), UserViewVolunteerActivity.class));
        });

        viewEmergencyLayout.setOnClickListener(v -> {
            // Navigate to View Emergency Contacts Activity
            startActivity(new Intent(getActivity(), EmergencyContactsActivity.class));
        });


        return view;
    }
}