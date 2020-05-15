package com.example.ezbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ezbook.Model.Users;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail,etPassword;
    Button btnLogin;
    TextView tvReg;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail=findViewById(R.id.etEmail);
        etPassword=findViewById(R.id.etPass);
        btnLogin=findViewById(R.id.btnLogin);
        tvReg=findViewById(R.id.tvReg);

        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);


        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                loginUser();
            }
        });

        tvReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });



    }
    private String email,password;

    private void loginUser() {
        email=etEmail.getText().toString().trim();
        password=etPassword.getText().toString().trim();

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"Invalid email pattern...",Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)){
            Toast.makeText(this,"Enter password...",Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Logging In...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //logged in successfully
                        makeMeOnline();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed Logging in
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void makeMeOnline() {
        //after logging in, make user online
        progressDialog.setMessage("Checking user...");
        HashMap<String,Object>hashMap = new HashMap<>();
        hashMap.put("online","true");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //update successfully
                        checkUserType();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed updating
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                        
                    }
                });
    }

    private void checkUserType() {
        //if user is seller, start seller main screen
        //if user is buyer, start user main screen

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            String accountType = ""+ds.child("accountType").getValue();
                            if(accountType.equals("Admin")){
                                progressDialog.dismiss();
                                //user is admin
                                startActivity(new Intent (LoginActivity.this,AdminMainActivity.class));
                                finish();

                            }else{
                                progressDialog.dismiss();
                                //users is customer
                                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


}

