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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etId=findViewById(R.id.etId);
        etPass=findViewById(R.id.etPass);

        btnLogin=findViewById(R.id.btnLogin);
        tvReg=findViewById(R.id.tvReg);

        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String id,pass;

                id=etId.getText().toString();
                pass=etPass.getText().toString();

                if(id.equals("")){
                    Toast.makeText(LoginActivity.this,"ID Required",Toast.LENGTH_SHORT).show();

                }else if(pass.equals("")) {
                    Toast.makeText(LoginActivity.this,"Password Required",Toast.LENGTH_SHORT).show();
                }else{
                    //Authentication
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
