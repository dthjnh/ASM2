package com.example.asm2.MapView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asm2.AddDonationEditandDelete.DonationSite;
import com.example.asm2.Database.DonationSitesDatabaseHelper;
import com.example.asm2.DonorRegister.RegisterDonorActivity;
import com.example.asm2.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DonationSitesDatabaseHelper dbHelper;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private SearchView mapSearch;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        dbHelper = new DonationSitesDatabaseHelper(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mapSearch = findViewById(R.id.mapSearch);

        mapSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterDonationSites(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterDonationSites(newText);
                return false;
            }
        });

        // Obtain the SupportMapFragment and set up the map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void filterDonationSites(String query) {
        if (mMap == null) return;

        // Clear existing markers
        mMap.clear();

        if (query == null || query.trim().isEmpty()) {
            // Show all donation sites when the query is empty
            List<DonationSite> allSites = dbHelper.getAllDonationSites();

            for (DonationSite site : allSites) {
                LatLng location = new LatLng(site.getLatitude(), site.getLongitude());
                mMap.addMarker(new MarkerOptions()
                        .position(location)
                        .title(site.getAddress())
                        .snippet("Opening Hours: " + site.getHours() + "\nBlood Type Required: " + site.getBloodTypes())
                        .icon(getBitmapDescriptorFromVector(R.drawable.custom_marker)));
            }

            if (!allSites.isEmpty()) {
                LatLng firstLocation = new LatLng(allSites.get(0).getLatitude(), allSites.get(0).getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 15));
                Toast.makeText(this, "Showing all donation sites.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No donation sites available.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Filter donation sites based on the query
            List<DonationSite> filteredSites = dbHelper.getDonationSitesByBloodType(query.trim());

            if (!filteredSites.isEmpty()) {
                for (DonationSite site : filteredSites) {
                    LatLng location = new LatLng(site.getLatitude(), site.getLongitude());
                    mMap.addMarker(new MarkerOptions()
                            .position(location)
                            .title(site.getAddress())
                            .snippet("Opening Hours: " + site.getHours() + "\nBlood Type Required: " + site.getBloodTypes())
                            .icon(getBitmapDescriptorFromVector(R.drawable.custom_marker)));
                }

                // Move the camera to the first matching location and zoom in
                LatLng firstLocation = new LatLng(filteredSites.get(0).getLatitude(), filteredSites.get(0).getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 15));
                Toast.makeText(this, "Found " + filteredSites.size() + " site(s) matching: " + query, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No results found for: " + query, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Enable UI controls
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        enableMyLocation();

        // Retrieve and display donation sites from the database
        displayDonationSites();

        // Set marker click listener
        mMap.setOnMarkerClickListener(marker -> {
            showDonationSiteDialog(marker);
            return true;
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15)); // Zoom level 15
                        } else {
                            Toast.makeText(this, "Unable to fetch current location", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }


//        mMap.setOnMarkerClickListener(marker -> {
//            showCurrentLocationOnMarkerClick();
//            return true;
//        });
    }

//    private void showCurrentLocationOnMarkerClick() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            fusedLocationProviderClient.getLastLocation()
//                    .addOnSuccessListener(this, location -> {
//                        if (location != null) {
//                            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
//
//                            // Animate the camera to your current location
//                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
//
//                            // Add or update a marker at the current location
//                            mMap.addMarker(new MarkerOptions()
//                                    .position(currentLatLng)
//                                    .title("Your Current Location")
//                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
//
//                            Toast.makeText(this, "Latitude: " + location.getLatitude() +
//                                    "\nLongitude: " + location.getLongitude(), Toast.LENGTH_LONG).show();
//                        } else {
//                            Toast.makeText(this, "Unable to fetch current location", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        } else {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    LOCATION_PERMISSION_REQUEST_CODE);
//        }
//    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    public void getCurrentPosition(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));

                            // Add a marker at the current location
                            mMap.addMarker(new MarkerOptions()
                                    .position(currentLatLng)
                                    .title("Your Position")
                                    .icon(BitmapDescriptorFactory.defaultMarker()));

                        } else {
                            Toast.makeText(this, "Unable to fetch current location", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void displayDonationSites() {
        List<DonationSite> donationSites = dbHelper.getAllDonationSites();

        for (DonationSite site : donationSites) {
            LatLng location = new LatLng(site.getLatitude(), site.getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title(site.getAddress())
                    .snippet("Opening Hours: " + site.getHours() + "\nBlood Type Required: " + site.getBloodTypes())
                    .icon(getBitmapDescriptorFromVector(R.drawable.custom_marker)));
        }

        // Move camera to the first location if available
        if (!donationSites.isEmpty()) {
            LatLng firstLocation = new LatLng(donationSites.get(0).getLatitude(), donationSites.get(0).getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 12));
        }
    }

    private void showDonationSiteDialog(Marker marker) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_donation_site_info);

        TextView title = dialog.findViewById(R.id.dialogTitle);
        TextView hours = dialog.findViewById(R.id.dialogHours);
        TextView bloodTypes = dialog.findViewById(R.id.dialogBloodTypes);
        Button btnRegister = dialog.findViewById(R.id.btnDialogRegister);
        Button btnClose = dialog.findViewById(R.id.btnDialogClose);
        Button btnFindRoute = dialog.findViewById(R.id.btnDialogFindRoute); // New button for finding route

        // Populate dialog with marker info
        title.setText(marker.getTitle());
        String[] snippetParts = marker.getSnippet().split("\n");
        if (snippetParts.length > 0) {
            hours.setText(snippetParts[0]);
        }
        if (snippetParts.length > 1) {
            bloodTypes.setText(snippetParts[1]);
        }

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MapsActivity.this, RegisterDonorActivity.class);
            intent.putExtra("site_address", marker.getTitle());
            startActivity(intent);
            dialog.dismiss();
        });

        btnFindRoute.setOnClickListener(v -> {
            LatLng destination = marker.getPosition();
            findRoute(destination); // Pass the destination to the findRoute method
            dialog.dismiss();
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void findRoute(LatLng destination) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                            // Use Directions API to find a route
                            String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                                    "origin=" + currentLatLng.latitude + "," + currentLatLng.longitude +
                                    "&destination=" + destination.latitude + "," + destination.longitude +
                                    "&key=AIzaSyAmYG0ewlmb4zaJAkC6pBsFjqi0NBQu-Po";

                            fetchRoute(url);
                        } else {
                            Toast.makeText(this, "Unable to fetch current location", Toast.LENGTH_SHORT).show();
                        }
                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds.Builder()
                                .include(destination)
                                .include(new LatLng(location.getLatitude(), location.getLongitude()))
                                .build(), 450));
                    });
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void fetchRoute(String url) {
        new Thread(() -> {
            try {
                URL apiUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder jsonResponse = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    jsonResponse.append(line);
                }

                JSONObject jsonObject = new JSONObject(jsonResponse.toString());
                JSONArray routes = jsonObject.getJSONArray("routes");

                if (routes.length() > 0) {
                    JSONObject route = routes.getJSONObject(0);
                    JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
                    String encodedPolyline = overviewPolyline.getString("points");

                    runOnUiThread(() -> drawPolyline(encodedPolyline));
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "No route found", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Error fetching route", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void drawPolyline(String encodedPolyline) {
        List<LatLng> polylinePoints = PolyUtil.decode(encodedPolyline);

        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(polylinePoints)
                .width(10)
                .color(ContextCompat.getColor(this, R.color.teal_700)) // Replace with your desired color
                .geodesic(true);

        mMap.addPolyline(polylineOptions);
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

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            } else {
                Toast.makeText(this, "Location permission is required to use this feature", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getPosition(View view) {
        getCurrentPosition(view);
    }
}