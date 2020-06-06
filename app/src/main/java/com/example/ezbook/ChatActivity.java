package com.example.ezbook;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class ChatActivity extends AppCompatActivity {

    //views from xml
    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView profileIv;
    TextView nameTv,userStatusTv;
    EditText messageEt;
    ImageButton sendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //init views
        Toolbar toolbar =findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");

        recyclerView=findViewById(R.id.chat_recyclerView);
        profileIv=findViewById(R.id.profileIv);
        nameTv=findViewById(R.id.nameTv);
        userStatusTv=findViewById(R.id.userStatusTv);
        messageEt=findViewById(R.id.messageEt);
        sendBtn=findViewById(R.id.sendBtn);


    }


}