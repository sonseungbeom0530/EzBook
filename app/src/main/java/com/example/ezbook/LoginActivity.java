package com.example.ezbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ezbook.Model.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText etId,etPass;
    Button btnLogin;
    TextView tvReg,adminLink,notAdminLink;
//    DatabaseHelper db;
    ProgressDialog loadingBar;
    String parentDbName = "Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etId=findViewById(R.id.etId);
        etPass=findViewById(R.id.etPass);
        btnLogin=findViewById(R.id.btnLogin);
        tvReg=findViewById(R.id.tvReg);
        adminLink=findViewById(R.id.admin_panel_link);
        notAdminLink=findViewById(R.id.not_admin_panel_link);
//        db=new DatabaseHelper(this);
        loadingBar = new ProgressDialog(this);

        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String id,pass;

                id=etId.getText().toString();
                pass=etPass.getText().toString();
//                boolean chkLogin = db.chkLogin(id,pass);
//                Intent i = new Intent(LoginActivity.this,MainActivity.class);

                if(id.equals("")){
                    Toast.makeText(LoginActivity.this,"ID Required",Toast.LENGTH_SHORT).show();

                }else if(pass.equals("")) {
                    Toast.makeText(LoginActivity.this,"Password Required",Toast.LENGTH_SHORT).show();
                }else{
                    //Authentication
//                    if(chkLogin==true){
                        loadingBar.setTitle("Create Account");
                        loadingBar.setMessage("Please wait, while we are checking the credentials.");
                        loadingBar.setCanceledOnTouchOutside(false);
                        loadingBar.show();
                        
                        AllowAccessToAccount(id,pass);
                        
                        //Toast.makeText(getApplicationContext(),"Successfully Login",Toast.LENGTH_SHORT).show();
                        //startActivity(i);
//                    }else{
//                        Toast.makeText(getApplicationContext(),"Wrong ID or Password\nEnter correct ID and Password",Toast.LENGTH_SHORT).show();
//                    }
                }
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
        adminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLogin.setText("Login Admin");
                adminLink.setVisibility(View.INVISIBLE);
                notAdminLink.setVisibility((View.VISIBLE));
                parentDbName="Admins";
            }
        });
        notAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLogin.setText("Login");
                adminLink.setVisibility(View.VISIBLE);
                notAdminLink.setVisibility((View.INVISIBLE));
                parentDbName="Users";
            }
        });
    }

    private void AllowAccessToAccount(final String id, final String pass) {
        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(parentDbName).child(id).exists()){
                    Users usersData = dataSnapshot.child(parentDbName).child(id).getValue(Users.class);
                    if(usersData.getId().equals(id)){
                        if(usersData.getPassword().equals(pass)){
                            if(parentDbName.equals("Admins")){
                                Toast.makeText(getApplicationContext(),"Welcome admin, you are logged in successfully Login",Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                Intent i = new Intent(LoginActivity.this,AdminMainActivity.class);
                                startActivity(i);
                            }else if(parentDbName.equals("Users")){
                                Toast.makeText(getApplicationContext(),"Successfully Login",Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                Intent i = new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(i);
                            }
                        }else {
                            loadingBar.dismiss();
                            Toast.makeText(getApplicationContext(),"Password incorrect",Toast.LENGTH_SHORT).show();
                        }
                    }
                }else {
                    Toast.makeText(LoginActivity.this,"Account with this ID do not exists",Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
