package com.example.ezbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    DatabaseHelper db;
    EditText etId,etPass,etConPass,etName,etEmail;
    Button btnLogin;
    ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db=new DatabaseHelper(this);
        etId=findViewById(R.id.etId);
        etPass=findViewById(R.id.etPass);
        etConPass=findViewById(R.id.etConPass);
        etEmail=findViewById(R.id.etEmail);
        etName=findViewById(R.id.etName);
        btnLogin=findViewById(R.id.btnReg);
        loadingBar=new ProgressDialog(this);

        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String name,id,pass,email,conPass;

                id=etId.getText().toString();
                pass=etPass.getText().toString();
                conPass=etConPass.getText().toString();
                name=etName.getText().toString();
                email=etEmail.getText().toString();
                Intent i = new Intent(RegisterActivity.this,LoginActivity.class);

                if(name.equals("")){
                    Toast.makeText(RegisterActivity.this,"Name Required",Toast.LENGTH_SHORT).show();
                }else if(id.equals("")) {
                    Toast.makeText(RegisterActivity.this, "ID Required", Toast.LENGTH_SHORT).show();
                }else if(email.equals("")) {
                    Toast.makeText(RegisterActivity.this,"email Required",Toast.LENGTH_SHORT).show();
                }else if(pass.equals("")){
                    Toast.makeText(RegisterActivity.this,"Password Required",Toast.LENGTH_SHORT).show();
                }else if(conPass.equals("")) {
                    Toast.makeText(RegisterActivity.this,"Confirm Password Required",Toast.LENGTH_SHORT).show();
                }else{
                    //Authentication
                    if(conPass.equals(pass)) {
                        //Boolean chkId = db.chkId(id);
 //                       if (chkId==true){
 //                           boolean insert = db.insert(id,pass,name,email);
 //                           if (insert==true){
                                //Toast.makeText(getApplicationContext(),"register successful",Toast.LENGTH_SHORT).show();
                                loadingBar.setTitle("Create Account");
                                loadingBar.setMessage("Please wait, while we are checking the credentials.");
                                loadingBar.setCanceledOnTouchOutside(false);
                                loadingBar.show();
                                ValidateName(name,id,pass,email);
                                startActivity(i);
 //                           }
 //                       }
 //                       else{
 //                           Toast.makeText(getApplicationContext(),"ID already exists",Toast.LENGTH_SHORT).show();
 //                       }
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Password do not match",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void ValidateName(final String name, final String id, final String pass, final String email) {
        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!(dataSnapshot.child("Users").child(id).exists())){
                    HashMap<String,Object> userdataMap=new HashMap<>();
                    userdataMap.put("name",name);
                    userdataMap.put("id",id);
                    userdataMap.put("password",pass);
                    userdataMap.put("email",email);

                    RootRef.child("Users").child(id).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(RegisterActivity.this,"Congratulation, your account create",Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                        Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(i);
                                    }else{
                                        loadingBar.dismiss();
                                        Toast.makeText(RegisterActivity.this,"Network Error : please try again",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }else{
                    Toast.makeText(RegisterActivity.this,id+"already exists",Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this,"Please try again using another ID",Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(RegisterActivity.this, RegisterActivity.class);
                    startActivity(i);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
