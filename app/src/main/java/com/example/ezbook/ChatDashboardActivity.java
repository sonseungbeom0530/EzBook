package com.example.ezbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class ChatDashboardActivity extends AppCompatActivity {

    //firebase auth
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_dashboard);

        //init
        firebaseAuth=FirebaseAuth.getInstance();

        //bottom navigation
        BottomNavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);

        //home fragment transaction(default, on star)
        HomeFragment fragment1 = new HomeFragment();
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.content,fragment1,"");
        ft1.commit();


    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    //handle item clicks
                    switch ((menuItem.getItemId())){
                        case R.id.nav_home:
                            //home fragment transaction
                            HomeFragment fragment1 = new HomeFragment();
                            FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                            ft1.replace(R.id.content,fragment1,"");
                            ft1.commit();
                            return true;
                        case R.id.nav_profile:
                            //profile fragment transaction
                            ProfileFragment fragment2 = new ProfileFragment();
                            FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                            ft2.replace(R.id.content,fragment2,"");
                            ft2.commit();
                            return true;
                        case R.id.nav_users:
                            //users fragment transaction
                            UsersFragment fragment3 = new UsersFragment();
                            FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
                            ft3.replace(R.id.content,fragment3,"");
                            ft3.commit();
                            return true;
                        case R.id.nav_chat:
                            //users fragment transaction
                            ChatListFragment fragment4 = new ChatListFragment();
                            FragmentTransaction ft4 = getSupportFragmentManager().beginTransaction();
                            ft4.replace(R.id.content,fragment4,"");
                            ft4.commit();
                            return true;
                    }
                    return false;
                }
            };
}