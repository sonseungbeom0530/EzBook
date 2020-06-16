package com.example.ezbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransitionImpl;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.ezbook.Adapters.AdapterReview;
import com.example.ezbook.Models.ModelReview;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ShopReviewsActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private ImageView profileIv;
    private TextView shopNameTv,ratingsTv;
    private RatingBar ratingBar;
    private RecyclerView reviewsRv;

    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelReview> reviewArrayList;
    private AdapterReview adapterReview;

    private String shopUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_reviews);

        backBtn=findViewById(R.id.backBtn);
        profileIv=findViewById(R.id.profileIv);
        shopNameTv=findViewById(R.id.shopNameTv);
        ratingsTv=findViewById(R.id.ratingsTv);
        ratingBar=findViewById(R.id.ratingBar);
        reviewsRv=findViewById(R.id.reviewsRv);

        shopUid=getIntent().getStringExtra("shopUid");

        firebaseAuth= FirebaseAuth.getInstance();
        loadShopDetails();
        loadReviews();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private float ratingSum=0;
    private void loadReviews() {

        reviewArrayList=new ArrayList<>();

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid).child("Ratings")
                .addValueEventListener(new ValueEventListener(){
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot){

                        reviewArrayList.clear();
                        ratingSum=0;
                        for (DataSnapshot ds:dataSnapshot.getChildren()){
                            float rating=Float.parseFloat(""+ds.child("ratings").getValue());
                            ratingSum=ratingSum+rating;

                            ModelReview modelReview=ds.getValue(ModelReview.class);
                            reviewArrayList.add(modelReview);
                        }
                        adapterReview=new AdapterReview(ShopReviewsActivity.this,reviewArrayList);
                        reviewsRv.setAdapter(adapterReview);

                        long numberOfReview=dataSnapshot.getChildrenCount();
                        float avgRating=ratingSum/numberOfReview;
                        ratingsTv.setText(String.format("%.2f",avgRating)+" ["+numberOfReview+"]");
                        ratingBar.setRating(avgRating);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError){

                    }
                });
    }

    private void loadShopDetails() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String shopName=""+dataSnapshot.child("name").getValue();
                        String profileImage=""+dataSnapshot.child("image").getValue();
                        shopNameTv.setText(shopName);
                        try {
                            Picasso.get().load(profileImage).placeholder(R.drawable.ic_store).into(profileIv);
                        }catch (Exception e){
                            profileIv.setImageResource(R.drawable.ic_store);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}