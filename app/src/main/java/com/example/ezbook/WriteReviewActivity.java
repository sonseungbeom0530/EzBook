package com.example.ezbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class WriteReviewActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private ImageView profileIv;
    private TextView shopNameTv,labelTv;
    private RatingBar ratingBar;
    private EditText reviewEt;
    private FloatingActionButton submitBtn;

    private String shopUid;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        backBtn=findViewById(R.id.backBtn);
        profileIv=findViewById(R.id.profileIv);
        shopNameTv=findViewById(R.id.shopNameTv);
        labelTv=findViewById(R.id.labelTv);
        ratingBar=findViewById(R.id.ratingBar);
        reviewEt=findViewById(R.id.reviewEt);
        submitBtn=findViewById(R.id.submitBtn);

        shopUid=getIntent().getStringExtra("shopUid");

        firebaseAuth=FirebaseAuth.getInstance();

        //load shop info
        loadShopInfo();
        
        //if user has written review to this shop, load it
        loadMyReview();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //input data
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();

            }
        });

    }

    private void loadShopInfo() {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String shopName=""+dataSnapshot.child("name").getValue();
                String shopImage=""+dataSnapshot.child("image").getValue();

                //set shop info to ui
                shopNameTv.setText(shopName);
                try {
                    Picasso.get().load(shopImage).placeholder(R.drawable.ic_store).into(profileIv);

                }catch (Exception e){
                    profileIv.setImageResource(R.drawable.ic_store);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadMyReview() {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid).child("Ratings").child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            String uid=""+dataSnapshot.child("uid").getValue();
                            String ratings=""+dataSnapshot.child("ratings").getValue();
                            String review=""+dataSnapshot.child("review").getValue();
                            String timestamp=""+dataSnapshot.child("timestamp").getValue();

                            float myRating=Float.parseFloat(ratings);
                            ratingBar.setRating(myRating);
                            reviewEt.setText(review);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void inputData() {
        String rating=""+ratingBar.getRating();
        String review=reviewEt.getText().toString().trim();

        String timeStamp=""+System.currentTimeMillis();

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("uid",""+firebaseAuth.getUid());
        hashMap.put("ratings",""+rating);
        hashMap.put("review",""+review);
        hashMap.put("timestamp",""+timeStamp);

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid).child("Ratings").child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(WriteReviewActivity.this, "Review published succeddfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(WriteReviewActivity.this,UserBookstoreActivity.class));

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(WriteReviewActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

    }
}