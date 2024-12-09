package com.example.asm2.DonorRegister;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.asm2.AddDonationEditandDelete.DonationSite;
import com.example.asm2.DatabaseHelper;
import com.example.asm2.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.asm2.databinding.ActivityMapsBinding;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        dbHelper = new DatabaseHelper(this);
    }

    private BitmapDescriptor getBitmapDescriptorFromVector(int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(this, vectorResId);
        if (vectorDrawable == null) {
            throw new IllegalArgumentException("Resource not found: " + vectorResId);
        }
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng rmit = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(rmit).title("Marker in RMIT"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(rmit));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(rmit, 15));
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Retrieve donation sites from the database
        List<DonationSite> donationSites = dbHelper.getAllDonationSites();

        for (DonationSite site : donationSites) {
            LatLng location = new LatLng(site.getLatitude(), site.getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title(site.getAddress())
                    .snippet("Opening Hours: " + site.getHours() + "\nBlood Type Require: " + site.getBloodTypes())
                    .icon(getBitmapDescriptorFromVector(R.drawable.custom_marker)));
        }

        if (!donationSites.isEmpty()) {
            LatLng firstLocation = new LatLng(donationSites.get(0).getLatitude(), donationSites.get(0).getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 12));
        }

        // Handle marker clicks to show custom dialog
        mMap.setOnMarkerClickListener(marker -> {
            showDonationSiteDialog(marker);
            return true; // Consume the click event
        });
    }

    private void showDonationSiteDialog(Marker marker) {
        // Create a custom dialog
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_donation_site_info); // Ensure this layout exists

        // Set dialog views
        TextView title = dialog.findViewById(R.id.dialogTitle);
        TextView hours = dialog.findViewById(R.id.dialogHours);
        TextView bloodTypes = dialog.findViewById(R.id.dialogBloodTypes);
        Button btnRegister = dialog.findViewById(R.id.btnDialogRegister);
        Button btnClose = dialog.findViewById(R.id.btnDialogClose);

        // Populate dialog with marker info
        title.setText(marker.getTitle());
        String[] snippetParts = marker.getSnippet().split("\n");
        if (snippetParts.length > 0) {
            hours.setText(snippetParts[0]); // Opening hours
        }
        if (snippetParts.length > 1) {
            bloodTypes.setText(snippetParts[1]); // Blood types
        }

        // Handle register button click
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MapsActivity.this, RegisterDonorActivity.class);
            intent.putExtra("site_address", marker.getTitle());
            startActivity(intent);
            dialog.dismiss(); // Close the dialog
        });

        // Handle close button click
        btnClose.setOnClickListener(v -> dialog.dismiss());

        // Show the dialog
        dialog.show();
    }

    public void backToAdmin(View view) {
        finish(); // Close the MapsActivity and return to Admin activity
    }
}