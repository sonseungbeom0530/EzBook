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


public class UserProfileActivity extends AppCompatActivity {

    TextView tvName, tvPhone, tvPassword, tvEmail, tvAccountType;

    ImageButton backBtn,editBtn;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        firebaseAuth=FirebaseAuth.getInstance();

        tvPhone = findViewById(R.id.tvPhone);
        tvName = findViewById(R.id.tvName);
        tvPassword = findViewById(R.id.tvPassword);
        tvEmail = findViewById(R.id.tvEmail);
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
                startActivity(new Intent(UserProfileActivity.this,EditProfileActivity.class ));

            }
        });



    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user==null){
            startActivity(new Intent (UserProfileActivity.this,LoginActivity.class));
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

                            tvName.setText(name);
                            tvEmail.setText(email);
                            tvPassword.setText(password);
                            tvPhone.setText(phone);
                            tvAccountType.setText(accountType);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

}
