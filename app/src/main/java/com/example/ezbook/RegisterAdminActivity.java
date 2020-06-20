package com.example.ezbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class RegisterAdminActivity extends AppCompatActivity implements LocationListener {

    private EditText etPhone,etPass,etConPass,etShopName,etEmail,etAddress,etCountry,etState,etCity;
    private Button btnReg;
    private ImageButton gpsBtn;

    //permissions
    private static final int LOCATION_REQUEST_CODE=100;

    private String[] locationPermissions;

    private double latitude=0.0,longitude=0.0;

    private LocationManager locationManager;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog pd;

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

        firebaseAuth=FirebaseAuth.getInstance();
        pd=new ProgressDialog(this);
        pd.setTitle("Please wait");
        pd.setCanceledOnTouchOutside(false);


        btnReg.setOnClickListener(new View.OnClickListener(){

            @Override

            public void onClick(View v){
                //register user
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

    private String shopName,phone,country,state,city,address,email,password,confirmPassword;

    private  void inputData(){
        //input data
        shopName=etShopName.getText().toString().trim();
        phone=etPhone.getText().toString().trim();
        password=etPass.getText().toString().trim();
        confirmPassword=etConPass.getText().toString().trim();
        email=etEmail.getText().toString().trim();
        country=etCountry.getText().toString().trim();
        state=etState.getText().toString().trim();
        city=etCity.getText().toString().trim();
        address=etAddress.getText().toString().trim();
        //validate data
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"Invalid email pattern",Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(shopName)){
            Toast.makeText(this,"Enter name",Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length()<6){
            Toast.makeText(this,"password must be at least 6 characters long",Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)){
            Toast.makeText(this,"password doesn't match",Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(phone)){
            Toast.makeText(this,"Enter phone number",Toast.LENGTH_SHORT).show();
            return;
        }
        if (latitude==0.0||longitude==0.0){
            Toast.makeText(this,"Please click GPS button to detect location",Toast.LENGTH_SHORT).show();
            return;
        }
        createAccount();
    }

    private void createAccount() {
        pd.setMessage("Creating Account");
        pd.show();

        //create account
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {

                    @Override

                    public void onSuccess(AuthResult authResult) {
                        //account created
                        saverFirebaseData();
                    }

                })
                .addOnFailureListener(new OnFailureListener() {

                    @Override

                    public void onFailure(@NonNull Exception e) {
                        //failed creating account
                        pd.dismiss();
                        Toast.makeText(RegisterAdminActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }

                });

    }

    private void saverFirebaseData() {
        pd.setMessage("Saving Account Info...");
        String timestamp=""+System.currentTimeMillis();

        //setup data to save
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("uid",""+firebaseAuth.getUid());
        hashMap.put("email",""+email);
        hashMap.put("name",""+shopName);
        hashMap.put("password",""+password);
        hashMap.put("phone",""+phone);
        hashMap.put("timestamp",""+timestamp);
        hashMap.put("accountType","Admin");
        hashMap.put("online","true");
        hashMap.put("image","");
        hashMap.put("cover","");
        hashMap.put("city",city);
        hashMap.put("state",state);
        hashMap.put("country",country);
        hashMap.put("address",address);
        hashMap.put("longitude",longitude);
        hashMap.put("latitude",latitude);


        //save to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {

                    @Override

                    public void onSuccess(Void aVoid) {
                        //db updated
                        pd.dismiss();
                        startActivity(new Intent(RegisterAdminActivity.this,LoginActivity.class));
                        finish();
                    }

                })

                .addOnFailureListener(new OnFailureListener() {

                    @Override

                    public void onFailure(@NonNull Exception e) {
                        //failed updating db
                        pd.dismiss();
                        startActivity(new Intent(RegisterAdminActivity.this,LoginActivity.class));
                        finish();
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