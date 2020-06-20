package com.example.ezbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class EditAdminProfileActivity extends AppCompatActivity implements LocationListener {
    //TextView tvName, tvPhone, tvPassword, tvEmail, tvAccountType;
    private EditText etPhone, etName, etPass, etEmail,etAddress,etCountry,etState,etCity;
    private ImageButton backBtn,gpsBtn;
    private Button updateBtn;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private LocationManager locationManager;
    private static final int LOCATION_REQUEST_CODE =100;

    private double latitude=0.0;
    private double longitude=0.0;

    private String[] locationPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_admin_profile);

        etPhone = findViewById(R.id.etPhone);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        backBtn = findViewById(R.id.backBtn);
        etPass = findViewById(R.id.etPass);
        etAddress = findViewById(R.id.etAddress);
        etCountry = findViewById(R.id.etCountry);
        etState = findViewById(R.id.etState);
        etCity = findViewById(R.id.etCity);
        updateBtn = findViewById(R.id.updateBtn);
        gpsBtn = findViewById(R.id.gpsBtn);

        locationPermissions=new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();

        checkUser();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        });
        gpsBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //detect current location
                if(checkLocationPermissions()){
                    //already allowed
                    detectLocation();
                }else {
                    //not allowed, request
                    requestLocationPermission();
                }
            }

        });


    }


    private void detectLocation() {
        Toast.makeText(this,"Please wait..",Toast.LENGTH_SHORT).show();

        locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
    }

    private boolean checkLocationPermissions(){
        boolean result= ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                ==(PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestLocationPermission(){
        ActivityCompat.requestPermissions(this,locationPermissions,LOCATION_REQUEST_CODE);
    }

    @Override
    public void onLocationChanged(Location location) {
        //location detected
        latitude=location.getLatitude();
        longitude=location.getLongitude();

        findAddress();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    private void findAddress() {
        //find address,country,state,city
        Geocoder geocoder;
        List<Address> addresses;
        geocoder=new Geocoder(this, Locale.getDefault());

        try{
            addresses=geocoder.getFromLocation(latitude, longitude, 1);

            String address =addresses.get(0).getAddressLine(0); //compelte address
            String city=addresses.get(0).getLocality();
            String state=addresses.get(0).getAdminArea();
            String country=addresses.get(0).getCountryName();

            //set address
            etCountry.setText(country);
            etState.setText(state);
            etCity.setText(city);
            etAddress.setText(address);
        } catch (Exception e) {
            Toast.makeText(this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case LOCATION_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean locationAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if(locationAccepted){
                        //permission allowed
                        detectLocation();
                    }else {
                        //permission denied
                        Toast.makeText(this,"Location permissions",Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }



    private String name,phone,country,state,city,address;

    private void inputData() {
        name = etName.getText().toString().trim();
        phone = etPhone.getText().toString().trim();
        country = etCountry.getText().toString().trim();
        state = etState.getText().toString().trim();
        city = etCity.getText().toString().trim();
        address = etAddress.getText().toString().trim();

        updateProfile();

    }

    private void updateProfile() {
        progressDialog.setMessage("updating profile");
        progressDialog.show();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", "" + name);
        hashMap.put("phone", "" + phone);
        hashMap.put("country", "" + country);
        hashMap.put("city", "" + city);
        hashMap.put("state", "" + state);
        hashMap.put("address", "" + address);
        hashMap.put("latitude", "" + latitude);
        hashMap.put("longitude", "" + longitude);

        //update to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //updated
                        progressDialog.dismiss();
                        Toast.makeText(EditAdminProfileActivity.this, "Profile updated.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed to update
                        progressDialog.dismiss();
                        Toast.makeText(EditAdminProfileActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(EditAdminProfileActivity .this, LoginActivity.class));
        } else {
            loadMyInfo();
        }
    }

    private void loadMyInfo() {
        //load user info, and set to views
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String accountType = "" + ds.child("accountType").getValue();
                            String name = "" + ds.child("name").getValue();
                            String password = "" + ds.child("password").getValue();
                            String email = "" + ds.child("email").getValue();
                            String phone = "" + ds.child("phone").getValue();
                            String address = "" + ds.child("address").getValue();
                            String city = "" + ds.child("city").getValue();
                            String country = "" + ds.child("country").getValue();
                            String state = "" + ds.child("state").getValue();
                            latitude = Double.parseDouble("" + ds.child("latitude").getValue());
                            longitude = Double.parseDouble("" + ds.child("longitude").getValue());
                            String uid = "" + ds.child("uid").getValue();

                            etName.setText(name);
                            etEmail.setText(email);
                            etPass.setText(password);
                            etPhone.setText(phone);
                            etAddress.setText(address);
                            etCity.setText(city);
                            etCountry.setText(country);
                            etState.setText(state);


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


}