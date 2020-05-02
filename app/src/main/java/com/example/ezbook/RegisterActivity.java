package com.example.ezbook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    EditText etId,etPass,etConPass,etName,etEmail;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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
                }else if(pass.equals("")){
                    Toast.makeText(RegisterActivity.this,"Password Required",Toast.LENGTH_SHORT).show();

                }else if(conPass.equals("")) {
                    Toast.makeText(RegisterActivity.this,"Password Required",Toast.LENGTH_SHORT).show();
                }else if(conPass.equals("pass")) {
                    Toast.makeText(RegisterActivity.this,"Password missmatch",Toast.LENGTH_SHORT).show();
                }else if(email.equals("")) {
                    Toast.makeText(RegisterActivity.this,"email Required",Toast.LENGTH_SHORT).show();
                }else{
                    //Authentication
                }
            }
        });
    }
}
