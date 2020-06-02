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





public class EditUserProfileActivity extends AppCompatActivity {



    //TextView tvName, tvPhone, tvPassword, tvEmail, tvAccountType;

    TextView tvAccountType;

    EditText etPhone,etName,etPassword,etEmail;

    ImageButton backBtn,editBtn;

    FirebaseAuth firebaseAuth;

    ProgressDialog progressDialog;





    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_profile);



        firebaseAuth=FirebaseAuth.getInstance();



        etPhone = findViewById(R.id.etPhone);

        etName = findViewById(R.id.etName);

        etPassword = findViewById(R.id.etPassword);

        etEmail = findViewById(R.id.etEmail);

        tvAccountType = findViewById(R.id.tvAccountType);

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

                //open edit profile activity

                //startActivity(new Intent(UserProfileActivity.this,EditUserProfileActivity.class ));

                inputData();

            }

        });







    }

    private String aName,aPassword,aEmail,aPhone;

    private void inputData() {

        aName=etName.getText().toString().trim();

        aPassword=etPassword.getText().toString().trim();

        aEmail=etEmail.getText().toString().trim();

        aPhone=etPhone.getText().toString().trim();

        updateProfile();



    }

    private void updateProfile() {

        progressDialog.setMessage("updating profile");

        progressDialog.show();



        HashMap<String,Object> hashMap=new HashMap<>();

        hashMap.put("email",""+aEmail);

        hashMap.put("name",""+aName);

        hashMap.put("password",""+aPassword);

        hashMap.put("phone",""+aPhone);



        //update to db

        DatabaseReference ref =FirebaseDatabase.getInstance().getReference("Users");

        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)

                .addOnSuccessListener(new OnSuccessListener<Void>() {

                    @Override

                    public void onSuccess(Void aVoid) {

                        //updated

                        progressDialog.dismiss();

                        Toast.makeText(EditUserProfileActivity.this,"Profile updated.",Toast.LENGTH_SHORT).show();

                    }

                })

                .addOnFailureListener(new OnFailureListener() {

                    @Override

                    public void onFailure(@NonNull Exception e) {

                        //failed to update

                        progressDialog.dismiss();

                        Toast.makeText(EditUserProfileActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

                    }

                });



    }

    private void checkUser() {

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(user==null){

            startActivity(new Intent (EditUserProfileActivity.this,LoginActivity.class));

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

                            String name=""+ds.child("name").getValue();

                            String password=""+ds.child("password").getValue();

                            String email=""+ds.child("email").getValue();

                            String phone=""+ds.child("phone").getValue();



                            etName.setText(name);

                            etEmail.setText(email);

                            etPassword.setText(password);

                            etPhone.setText(phone);

                            tvAccountType.setText(accountType);



                        }

                    }



                    @Override

                    public void onCancelled(@NonNull DatabaseError databaseError) {



                    }

                });

    }



}