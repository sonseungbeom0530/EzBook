package com.example.ezbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.ColorSpace;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.telecom.PhoneAccountHandle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ezbook.Adapters.AdapterCartItem;
import com.example.ezbook.Adapters.AdapterProductAdmin;
import com.example.ezbook.Adapters.AdapterProductUser;
import com.example.ezbook.Adapters.AdapterReview;
import com.example.ezbook.Models.ModelCartItem;
import com.example.ezbook.Models.ModelProduct;
import com.example.ezbook.Models.ModelReview;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.HashMap;

public class ShopDetailsActivity extends AppCompatActivity {

    private ImageView shopIv;
    private TextView nameTv,phoneTv,emailTv,addressTv,filteredProductsTv,cartCountTv;
    private ImageButton callBtn,mapBtn,backBtn,filterProductBtn,cartBtn,reviewsBtn;
    private EditText searchProductEt;
    private RecyclerView productsRv;
    private RatingBar ratingBar;

    private ProgressDialog pd;

    private String shopUid;

    private FirebaseAuth firebaseAuth;
    private String myLatitude,myLongitude,myPhone;
    private String shopName,shopEmail,shopPhone,shopAddress,shopLatitude,shopLongitude;

    private ArrayList<ModelProduct> productsList;
    private AdapterProductUser adapterProductUser;

    private ArrayList<ModelCartItem> cartItemsList;
    private AdapterCartItem adapterCartItem;

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
        cartCountTv=findViewById(R.id.cartCountTv);
        reviewsBtn=findViewById(R.id.reviewsBtn);
        ratingBar=findViewById(R.id.ratingBar);

        pd=new ProgressDialog(this);
        pd.setTitle("Please wait");
        pd.setCanceledOnTouchOutside(false);

        //get uid of the shop from intent
        shopUid=getIntent().getStringExtra("shopUid");

        firebaseAuth=FirebaseAuth.getInstance();
        loadMyInfo();
        loadShopDetails();
        loadShopProducts();
        loadReviews();

        //each shop have its own products and orders so if user add itesm to cart and go back and open cart in differnet shop then cart should be different
        //so delte cart data whenever user open this activity
        deleteCartData();

        cartCount();

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
                showCartDialog();
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
        reviewsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ShopDetailsActivity.this,ShopReviewsActivity.class);
                intent.putExtra("shopUid",shopUid);
                startActivity(intent);
            }
        });
    }

    private float ratingSum=0;
    private void loadReviews() {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid).child("Ratings")
                .addValueEventListener(new ValueEventListener(){
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot){

                        ratingSum=0;
                        for (DataSnapshot ds:dataSnapshot.getChildren()){
                            float rating=Float.parseFloat(""+ds.child("ratings").getValue());
                            ratingSum=ratingSum+rating;
                        }

                        long numberOfReview=dataSnapshot.getChildrenCount();
                        float avgRating=ratingSum/numberOfReview;
                        ratingBar.setRating(avgRating);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError){

                    }
                });
    }

    private void deleteCartData() {
        DataBaseHandler dataBaseHandler=new DataBaseHandler(this);
        dataBaseHandler.deleteAll();
    }

    public void cartCount(){
        DataBaseHandler dataBaseHandler=new DataBaseHandler(this);
        int count=dataBaseHandler.getAllData().getCount();
        if (count<=0){
            cartCountTv.setVisibility(View.GONE);
        }else {
            cartCountTv.setVisibility(View.VISIBLE);
            cartCountTv.setText(""+count);//concatenate with string, because i cannt se integer in textview
        }
    }

    public double allTotalPrice=0.00;
    public TextView allTotalPriceTv;
    private void showCartDialog() {
        cartItemsList=new ArrayList<>();
        //inflate cart layout
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_cart,null);
        //init views
        TextView shopNameTv=view.findViewById(R.id.shopNameTv);
        RecyclerView cartItemRv=view.findViewById(R.id.cartItemRv);
        allTotalPriceTv=view.findViewById(R.id.TotalTv);
        Button checkoutBtn=view.findViewById(R.id.checkoutBtn);

        //dialog
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setView(view);

        shopNameTv.setText(shopName);
        DataBaseHandler dataBaseHandler=new DataBaseHandler(this);
        Cursor res=dataBaseHandler.getAllData();
        while (res.moveToNext()){
            String id=res.getString(0);
            String pId=res.getString(1);
            String name=res.getString(2);
            String price=res.getString(3);
            String cost=res.getString(4);
            String quantity=res.getString(5);

            allTotalPrice=allTotalPrice+Double.parseDouble(cost);
            ModelCartItem modelCartItem=new ModelCartItem(
                    ""+id,
                    ""+pId,
                    ""+name,
                    ""+price,
                    ""+cost,
                    ""+quantity
            );
            cartItemsList.add(modelCartItem);

        }
        adapterCartItem=new AdapterCartItem(this,cartItemsList);
        cartItemRv.setAdapter(adapterCartItem);
        allTotalPriceTv.setText("$"+allTotalPrice);
        AlertDialog dialog=builder.create();
        dialog.show();

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                allTotalPrice=0.00;
            }
        });

        //place order
        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myLatitude.equals("")||myLatitude.equals("null")||myLongitude.equals("")||myLongitude.equals("null")){
                    //user didnt enter address in profile
                    Toast.makeText(ShopDetailsActivity.this,"Please enter your address in you profile before placing order...",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (myPhone.equals("")||myPhone.equals("null")){
                    Toast.makeText(ShopDetailsActivity.this,"Please enter your phone number in profile before placing order",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (cartItemsList.size()==0){
                    Toast.makeText(ShopDetailsActivity.this,"No Item in cart",Toast.LENGTH_SHORT).show();
                    return;
                }
                submitOrder();
            }
        });
        
    }

    private void submitOrder() {
        pd.setMessage("Placing order...");
        pd.show();

        final String timeStamp=""+System.currentTimeMillis();
        //for order is and order item
        String cost=allTotalPriceTv.getText().toString().trim().replace("$",""); //remove $ if contains



        //set order data
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("orderId",""+timeStamp);
        hashMap.put("orderTime",""+timeStamp);
        hashMap.put("orderStatus","In Progress");
        hashMap.put("orderCost",""+cost);
        hashMap.put("orderBy",""+firebaseAuth.getUid());
        hashMap.put("orderTo",""+shopUid);
        hashMap.put("latitude",""+myLatitude);
        hashMap.put("longitude",""+myLongitude);

        //add to db
        final DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users").child(shopUid).child("Orders");
        ref.child(timeStamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        for(int i=0;i<cartItemsList.size();i++){
                            String pId=cartItemsList.get(i).getpId();
                            String id=cartItemsList.get(i).getId();
                            String cost=cartItemsList.get(i).getCost();
                            String name=cartItemsList.get(i).getName();
                            String price=cartItemsList.get(i).getPrice();
                            String quantity=cartItemsList.get(i).getQuantity();

                            HashMap<String,String> hashMap1=new HashMap<>();
                            hashMap1.put("pId",pId);
                            hashMap1.put("name",name);
                            hashMap1.put("cost",cost);
                            hashMap1.put("price",price);
                            hashMap1.put("quantity",quantity);

                            ref.child(timeStamp).child("Items").child(pId).setValue(hashMap1);
                        }
                        pd.dismiss();
                        Toast.makeText(ShopDetailsActivity.this,"Order Place Successfully",Toast.LENGTH_SHORT).show();

                        //after placing order open order details page
                        Intent intent=new Intent(ShopDetailsActivity.this, OrderDetailsUserActivity.class);
                        intent.putExtra("orderTo",shopUid);
                        intent.putExtra("orderId",timeStamp);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(ShopDetailsActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void openMap() {
       // String address="https//:maps.google.com/maps?saddr="+myLatitude+","+myLongitude+"&daddr="+shopLatitude+","+shopLongitude;
        //Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse(address));
        //Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                //Uri.parse("http://maps.google.com/maps?saddr="+myLatitude+","+myLongitude+"&daddr="+shopLatitude+","+shopLongitude));
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
                            myPhone=""+ds.child("phone").getValue();
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