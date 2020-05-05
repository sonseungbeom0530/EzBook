package com.example.ezbook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    EditText etId,etPass;
    Button btnLogin;
    TextView tvReg;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etId=findViewById(R.id.etId);
        etPass=findViewById(R.id.etPass);

        btnLogin=findViewById(R.id.btnLogin);
        tvReg=findViewById(R.id.tvReg);
        db=new DatabaseHelper(this);

        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String id,pass;

                id=etId.getText().toString();
                pass=etPass.getText().toString();
                boolean chkLogin = db.chkLogin(id,pass);
                Intent i = new Intent(LoginActivity.this,MainActivity.class);

                if(id.equals("")){
                    Toast.makeText(LoginActivity.this,"ID Required",Toast.LENGTH_SHORT).show();

                }else if(pass.equals("")) {
                    Toast.makeText(LoginActivity.this,"Password Required",Toast.LENGTH_SHORT).show();
                }else{
                    //Authentication
                    if(chkLogin==true){
                        Toast.makeText(getApplicationContext(),"Successfully Login",Toast.LENGTH_SHORT).show();
                        startActivity(i);
                    }else{
                        Toast.makeText(getApplicationContext(),"Wrong ID or Password\nEnter correct ID and Password",Toast.LENGTH_SHORT).show();
                    }
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
}
}
