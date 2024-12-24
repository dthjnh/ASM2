package com.example.asm2.AddDonationEditandDelete;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.asm2.Database.DonationSitesDatabaseHelper;
import com.example.asm2.R;

import java.util.List;

public class DonationSiteAdapter extends ArrayAdapter<DonationSite> {
    private DonationSitesDatabaseHelper dbHelper;

    public DonationSiteAdapter(Context context, List<DonationSite> donationSites) {
        super(context, 0, donationSites);
        dbHelper = new DonationSitesDatabaseHelper(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_donation_site, parent, false);
        }

        DonationSite site = getItem(position);

        TextView txtName = convertView.findViewById(R.id.txtName);
        TextView txtAddress = convertView.findViewById(R.id.txtAddress);
        TextView txtHours = convertView.findViewById(R.id.txtHours);
        TextView txtBloodTypes = convertView.findViewById(R.id.txtBloodTypes);
        TextView txtLatitude = convertView.findViewById(R.id.txtLatitude);
        TextView txtLongitude = convertView.findViewById(R.id.txtLongitude);
        TextView txtCreatorType = convertView.findViewById(R.id.txtCreatorType);
        Button btnEdit = convertView.findViewById(R.id.btnEdit);
        Button btnDelete = convertView.findViewById(R.id.btnDelete);

        txtName.setText(site.getName());
        txtAddress.setText("Address: " + site.getAddress());
        txtHours.setText("Opening Hours: " + site.getHours());
        txtBloodTypes.setText("Blood Type Require: " + site.getBloodTypes());
        txtLatitude.setText("Lat: " + site.getLatitude());
        txtLongitude.setText("Lng: " + site.getLongitude());
        txtCreatorType.setText("Created by: " + site.getCreatorType());

        // Handle Edit button
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EditDonationSiteActivity.class);
            intent.putExtra("site_id", site.getId());
            getContext().startActivity(intent);
        });

        // Handle Delete button
        btnDelete.setOnClickListener(v -> {
            dbHelper.deleteDonationSite(site.getId());
            remove(site); // Remove the item from the list
            notifyDataSetChanged(); // Refresh the ListView
        });

        return convertView;
    }
}