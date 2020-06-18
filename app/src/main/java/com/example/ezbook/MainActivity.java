package com.example.ezbook;

import android.content.Intent;



import android.os.Bundle;



import android.view.View;











import androidx.appcompat.app.ActionBarDrawerToggle;



import androidx.appcompat.app.AppCompatActivity;



import androidx.appcompat.widget.Toolbar;



import androidx.cardview.widget.CardView;



import androidx.core.view.GravityCompat;



import androidx.drawerlayout.widget.DrawerLayout;







import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {



    //DrawerLayout drawerLayout;
    NavigationView navigationView;
    //Toolbar toolbar;
    CardView bookstoreCard,chatCard,profileCard,logoutCard,findOfflineStoreCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Hooks
        //drawerLayout=findViewById(R.id.drawer_layout);
        //navigationView=findViewById(R.id.nav_view);
        //toolbar=findViewById(R.id.toolbar);
        //toolbar
        //setSupportActionBar(toolbar);
        //Navigation Drawer menu
        //ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        //drawerLayout.addDrawerListener(toggle);
        //toggle.syncState();


        //defining card
        bookstoreCard=(CardView)findViewById(R.id.bookstore_card);
        chatCard=(CardView)findViewById(R.id.chat_card);
        profileCard=(CardView)findViewById(R.id.profile_card);
        logoutCard=(CardView)findViewById(R.id.logout_card);
        //findOfflineStoreCard=(CardView)findViewById(R.id.findOfflineStore_card);


        //Add click listener to the cards
        bookstoreCard.setOnClickListener(this);
        chatCard.setOnClickListener(this);
        profileCard.setOnClickListener(this);
        logoutCard.setOnClickListener(this);
        //findOfflineStoreCard.setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        //if(drawerLayout.isDrawerOpen(GravityCompat.START)){
        //    drawerLayout.closeDrawer(GravityCompat.START);
        //}else{
        //    super.onBackPressed();
        //}
        onBackPressed();
    }


    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()){
            case R.id.bookstore_card : i = new Intent(this,UserBookstoreActivity.class);startActivity(i); break;
            case R.id.profile_card : i = new Intent(this,UserProfileActivity.class);startActivity(i); break;
            case R.id.logout_card : i = new Intent(this,LoginActivity.class);startActivity(i); break;
            case R.id.chat_card : i = new Intent(this,ChatDashboardActivity.class);startActivity(i); break;

            default:break;

        }
    }

}