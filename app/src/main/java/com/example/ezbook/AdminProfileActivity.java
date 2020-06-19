package com.example.ezbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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


public class AdminProfileActivity extends AppCompatActivity {

    TextView tvShopName, tvPhone, tvPassword, tvEmail, tvAccountType,tvAddress,tvCountry,tvState,tvCity;

    ImageButton backBtn,editBtn;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        firebaseAuth=FirebaseAuth.getInstance();

        tvPhone = findViewById(R.id.tvPhone);
        tvShopName = findViewById(R.id.tvShopName);
        tvPassword = findViewById(R.id.tvPassword);
        tvEmail = findViewById(R.id.tvEmail);
        tvAccountType = findViewById(R.id.tvAccountType);
        tvAddress = findViewById(R.id.tvAddress);
        tvCountry = findViewById(R.id.tvCountry);
        tvCity=findViewById(R.id.tvCity);
        tvState=findViewById(R.id.tvState);


        backBtn=findViewById(R.id.backBtn);
        editBtn=findViewById(R.id.editBtn);

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);


        checkUser();

        backBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onBackPressed();
            }
        });
        editBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(AdminProfileActivity.this,EditAdminProfileActivity.class);
                startActivity(i);
            }
        });




    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user==null){
            startActivity(new Intent (AdminProfileActivity.this,LoginActivity.class));
        }else{
            loadMyInfo();
        }
    }

    private void loadMyInfo() {
        //load user info, and set to views
        DatabaseReference ref =FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            String accountType=""+ds.child("accountType").getValue();
                            String shopName=""+ds.child("name").getValue();
                            String password=""+ds.child("password").getValue();
                            String email=""+ds.child("email").getValue();
                            String phone=""+ds.child("phone").getValue();
                            String address=""+ds.child("address").getValue();
                            String city=""+ds.child("city").getValue();
                            String state=""+ds.child("state").getValue();
                            String country=""+ds.child("country").getValue();



                            tvShopName.setText(shopName);
                            tvEmail.setText(email);
                            tvPassword.setText(password);
                            tvPhone.setText(phone);
                            tvAccountType.setText(accountType);
                            tvAddress.setText(address);
                            tvCountry.setText(country);
                            tvCity.setText(city);
                            tvState.setText(state);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

}
