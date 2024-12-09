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

import com.example.asm2.DatabaseHelper;
import com.example.asm2.R;

import java.util.List;

public class DonationSiteAdapter extends ArrayAdapter<DonationSite> {
    private DatabaseHelper dbHelper;

    public DonationSiteAdapter(Context context, List<DonationSite> donationSites) {
        super(context, 0, donationSites);
        dbHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_donation_site, parent, false);
        }

        DonationSite site = getItem(position);

        TextView txtAddress = convertView.findViewById(R.id.txtAddress);
        TextView txtHours = convertView.findViewById(R.id.txtHours);
        TextView txtBloodTypes = convertView.findViewById(R.id.txtBloodTypes);
        TextView txtLatitude = convertView.findViewById(R.id.txtLatitude);  // New TextView for latitude
        TextView txtLongitude = convertView.findViewById(R.id.txtLongitude); // New TextView for longitude
        Button btnEdit = convertView.findViewById(R.id.btnEdit);
        Button btnDelete = convertView.findViewById(R.id.btnDelete);

        txtAddress.setText(site.getAddress());
        txtHours.setText(site.getHours());
        txtBloodTypes.setText(site.getBloodTypes());
        txtLatitude.setText("Lat: " + site.getLatitude());  // Display latitude
        txtLongitude.setText("Lng: " + site.getLongitude()); // Display longitude

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