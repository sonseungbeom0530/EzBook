package com.example.ezbook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {
    DatabaseHelper db;
    EditText etId,etPass,etConPass,etName,etEmail;
    Button btnLogin;

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
        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String name,id,pass,email,conPass;

                id=etId.getText().toString();
                pass=etPass.getText().toString();
                conPass=etConPass.getText().toString();
                name=etName.getText().toString();
                email=etEmail.getText().toString();

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
                        Boolean chkid = db.chkid(id);
                        if (chkid==true){
                            boolean insert = db.insert(id,pass,name,email);
                            if (insert==true){
                                Toast.makeText(getApplicationContext(),"register successful",Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"ID already exists",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{Toast.makeText(getApplicationContext(),"Password do not match",Toast.LENGTH_SHORT).show();}
                }
            }
        });
    }
}
