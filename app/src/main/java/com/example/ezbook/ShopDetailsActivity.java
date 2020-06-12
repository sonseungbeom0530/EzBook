package com.example.ezbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ezbook.Adapters.AdapterProductAdmin;
import com.example.ezbook.Adapters.AdapterProductUser;
import com.example.ezbook.Models.ModelProduct;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ThrowOnExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.util.ArrayList;

public class ShopDetailsActivity extends AppCompatActivity {

    private ImageView shopIv;
    private TextView nameTv,phoneTv,emailTv,addressTv,filteredProductsTv;
    private ImageButton callBtn,mapBtn,backBtn,filterProductBtn,cartBtn;
    private EditText searchProductEt;
    private RecyclerView productsRv;

    private String shopUid;

    private FirebaseAuth firebaseAuth;
    private String myLatitude,myLongitude;
    private String shopName,shopEmail,shopPhone,shopAddress,shopLatitude,shopLongitude;

    private ArrayList<ModelProduct> productsList;
    private AdapterProductUser adapterProductUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_details);

        shopIv=findViewById(R.id.shopIv);
        nameTv=findViewById(R.id.nameTv);
        phoneTv=findViewById(R.id.phoneTv);
        emailTv=findViewById(R.id.emailTv);
        addressTv=findViewById(R.id.addressTv);
        callBtn=findViewById(R.id.callBtn);
        mapBtn=findViewById(R.id.mapBtn);
        backBtn=findViewById(R.id.backBtn);
        searchProductEt=findViewById(R.id.searchProductEt);
        filterProductBtn=findViewById(R.id.filterProductBtn);
        filteredProductsTv=findViewById(R.id.filteredProductsTv);
        productsRv=findViewById(R.id.productsRv);
        cartBtn=findViewById(R.id.cartBtn);

        //get uid of the shop from intent
        shopUid=getIntent().getStringExtra("shopUid");

        firebaseAuth=FirebaseAuth.getInstance();
        loadMyInfo();
        loadShopDetails();
        loadShopProducts();
        searchProductEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    adapterProductUser.getFilter().filter(s);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialPhone();
            }
        });
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        });
        filterProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(ShopDetailsActivity.this);
                builder.setTitle("Choose Category : ")
                        .setItems(Constants.productCategory1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //get selected item
                                String selected = Constants.productCategory1[which];
                                filteredProductsTv.setText(selected);
                                if(selected.equals("All")){
                                    loadShopProducts();
                                }else {
                                    adapterProductUser.getFilter().filter(selected);
                                }

                            }
                        }).show();
            }
        });
    }

    private void openMap() {
       // String address="https//:maps.google.com/maps?saddr="+myLatitude+","+myLongitude+"&daddr="+shopLatitude+","+shopLongitude;
        //Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse(address));
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr="+myLatitude+","+myLongitude+"&daddr="+shopLatitude+","+shopLongitude));
        startActivity(intent);

    }

    private void dialPhone() {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+Uri.encode(shopPhone))));
        Toast.makeText(this,""+shopPhone,Toast.LENGTH_SHORT).show();

    }

    private void loadMyInfo() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds :dataSnapshot.getChildren()){
                            String name=""+ds.child("name").getValue();
                            String accountType=""+ds.child("accountType").getValue();
                            String email=""+ds.child("email").getValue();
                            String image=""+ds.child("image").getValue();
                            String phone=""+ds.child("phone").getValue();
                            String city=""+ds.child("city").getValue();
                            myLatitude=""+ds.child("latitude").getValue();

                            myLongitude=""+ds.child("longitude").getValue();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
    private void loadShopDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                shopName=""+dataSnapshot.child("name").getValue();
                shopEmail=""+dataSnapshot.child("email").getValue();
                shopPhone=""+dataSnapshot.child("phone").getValue();
                shopLatitude=""+dataSnapshot.child("latitude").getValue();
                shopLongitude=""+dataSnapshot.child("longitude").getValue();
                shopAddress=""+dataSnapshot.child("address").getValue();
                String image=""+dataSnapshot.child("image").getValue();

                //set data
                nameTv.setText(shopName);
                emailTv.setText(shopEmail);
                addressTv.setText(shopAddress);
                phoneTv.setText(shopPhone);
                try {
                    Picasso.get().load(image).into(shopIv);
                }catch (Exception e){

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void loadShopProducts() {
        productsList=new ArrayList<>();
        //get all products
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopUid).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //before getting reset list
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            ModelProduct modelProduct=ds.getValue(ModelProduct.class);
                            productsList.add(modelProduct);
                        }
                        adapterProductUser= new AdapterProductUser(ShopDetailsActivity.this,productsList);
                        //set adapter
                        productsRv.setAdapter(adapterProductUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}