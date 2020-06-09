package com.example.ezbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
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

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class RegisterAdminActivity extends AppCompatActivity implements LocationListener {

    private EditText etPhone,etPass,etConPass,etShopName,etEmail,etAddress,etCountry,etState,etCity;
    private Button btnReg;
    private ImageButton gpsBtn;

    //permissions
    private static final int LOCATION_REQUEST_CODE=100;

    private String[] locationPermissions;

    private double latitude,longitude;

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_admin);

        etPhone=findViewById(R.id.etPhone);
        etPass=findViewById(R.id.etPass);
        etConPass=findViewById(R.id.etConPass);
        etEmail=findViewById(R.id.etEmail);
        etShopName=findViewById(R.id.etShopName);
        etCity=findViewById(R.id.etCity);
        etAddress=findViewById(R.id.etAddress);
        etCountry=findViewById(R.id.etCountry);
        etState=findViewById(R.id.etState);

        gpsBtn=findViewById(R.id.gpsBtn);
        btnReg=findViewById(R.id.btnReg);

        //init permissions
        locationPermissions=new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

        btnReg.setOnClickListener(new View.OnClickListener(){

            @Override

            public void onClick(View v){
                //register user

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
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

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


}