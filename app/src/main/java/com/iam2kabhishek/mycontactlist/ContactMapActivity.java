package com.iam2kabhishek.mycontactlist;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ContactMapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_map);

        initListButton();
        initMapButton();
        initSettingsButton();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        initLocationButton(googleMap);
        initMapTypeButton(googleMap);

        ArrayList<Contact> contacts = new ArrayList<>();
        Contact currentContact = null;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ContactDataSource ds = new ContactDataSource(ContactMapActivity.this);
            try {
                ds.open();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            currentContact = ds.getSpecificContact(extras.getInt("contactid"));
            ds.close();
        } else {
            ContactDataSource ds = new ContactDataSource(ContactMapActivity.this);
            try {
                ds.open();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            contacts = ds.getContacts("contactname", "ASC");
            ds.close();
        }

        int measuredWidth = 0;
        int measuredHeight = 0;
        Point size = new Point();
        WindowManager w = getWindowManager();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            w.getDefaultDisplay().getSize(size);
            measuredWidth = size.x;
            measuredHeight = size.y;
        } else {
            Display d = w.getDefaultDisplay();
            measuredWidth = d.getWidth();
            measuredHeight = d.getHeight() - 180;
        }
        if (contacts.size() > 0) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder(); //2
            for (int i = 0; i < contacts.size(); i++) { //3

                currentContact = contacts.get(i);

                Geocoder geo = new Geocoder(this);

                List<Address> addresses = null;

                String address = currentContact.getStreetAddress() + ", " +
                        currentContact.getCity() + ", " +
                        currentContact.getState() + " " +
                        currentContact.getZipcode();
                try {

                    addresses = geo.getFromLocationName(address, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                LatLng point = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude()); //4
                builder.include(point);

                googleMap.addMarker(new MarkerOptions().position(point).title(currentContact.getContactName()).snippet(address)); //5
            }
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), measuredWidth, measuredHeight, 100)); //6
        } else {
            if (currentContact != null) { //7
                Geocoder geo = new Geocoder(this);
                List<Address> addresses = null;

                String address = currentContact.getStreetAddress() + ", " +
                        currentContact.getCity() + ", " +
                        currentContact.getState() + " " +
                        currentContact.getZipcode();
                try {
                    addresses = geo.getFromLocationName(address, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                LatLng point = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                googleMap.addMarker(new MarkerOptions().position(point).title(currentContact.getContactName()).snippet(address));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 16)); //8
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(ContactMapActivity.this).create(); //9
                alertDialog.setTitle("No Data");
                alertDialog.setMessage("No data is available for the mapping function.");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                alertDialog.show();
            }
        }
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current Location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    private void initLocationButton(final GoogleMap googleMap) {
        final Button locationbtn = findViewById(R.id.buttonShowMe);
        locationbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentSetting = locationbtn.getText().toString();
                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        &&
                        ContextCompat.checkSelfPermission(getApplicationContext(),
                                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        &&
                        currentSetting == "Location On") {
                    googleMap.setMyLocationEnabled(true);
                    locationbtn.setText("Location Off");
                } else {
                    ActivityCompat.requestPermissions(ContactMapActivity.this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, 200);
                    locationbtn.setText("Location On");
                    googleMap.setMyLocationEnabled(false);
                }
            }
        });
    }

    private void initMapTypeButton(final GoogleMap googleMap) {
        final Button satelitebtn = findViewById(R.id.buttonMapType);
        satelitebtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String currentSetting = satelitebtn.getText().toString();
                if (currentSetting.equalsIgnoreCase("Satellite View")) {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    satelitebtn.setText("Normal View");
                } else {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    satelitebtn.setText("Satellite View");
                }
            }
        });
    }

    private void initListButton() {
        ImageButton list = findViewById(R.id.imageButtonList);
        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactMapActivity.this, ContactListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initMapButton() {
        ImageButton list = findViewById(R.id.imageButtonMap);
        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactMapActivity.this, ContactMapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initSettingsButton() {
        ImageButton list = findViewById(R.id.imageButtonSettings);
        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactMapActivity.this, ContactSettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}