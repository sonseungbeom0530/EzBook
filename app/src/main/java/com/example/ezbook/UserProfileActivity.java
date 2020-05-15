package com.example.ezbook;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class UserProfileActivity extends AppCompatActivity {

    TextInputEditText nameProfile,idProfile,passwordProfile,emailProfile;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        firebaseAuth=FirebaseAuth.getInstance();

        nameProfile = findViewById(R.id.full_name_profile);
        idProfile = findViewById(R.id.id_profile);
        passwordProfile = findViewById(R.id.password_profile);
        emailProfile = findViewById(R.id.email_profile);

        checkUser();
    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user==null){
            startActivity(new Intent (UserProfileActivity.this,LoginActivity.class));
        }else{
            loadMyInfo();
        }
    }

    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                            String name = ""+ds.child("name").getValue();
                            String accountType = ""+ds.child("accountType").getValue();

                            nameProfile.setText(name);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

}
