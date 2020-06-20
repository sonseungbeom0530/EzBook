package com.example.ezbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ezbook.Adapters.AdapterOrderShop;
import com.example.ezbook.Adapters.AdapterProductAdmin;
import com.example.ezbook.Models.ModelOrderShop;
import com.example.ezbook.Models.ModelProduct;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AdminBookstoreActivity extends AppCompatActivity {

    private TextView nameTv,emailTv,tabProductsTv,tabOrdersTv,filteredProductsTv,filterOrdersTv;
    private EditText searchProductEt;
    private ImageButton addProductBtn,filterProductBtn,filterOrderBtn,reviewsBtn;
    private ImageView profileIv;

    private RelativeLayout productsRl,ordersRl;
    private RecyclerView productsRv,ordersRv;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private ArrayList<ModelProduct> productList;
    private AdapterProductAdmin adapterProductAdmin;

    private ArrayList<ModelOrderShop> orderShopArrayList;
    private AdapterOrderShop adapterOrderShop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_bookstore);

        nameTv=findViewById(R.id.nameTv);
        emailTv=findViewById(R.id.emailTv);
        filteredProductsTv=findViewById(R.id.filteredProductsTv);

        addProductBtn=findViewById(R.id.addProductBtn);
        profileIv=findViewById(R.id.profileIv);
        searchProductEt=findViewById(R.id.searchProductEt);
        tabProductsTv=findViewById(R.id.tabProductTv);
        filterProductBtn=findViewById(R.id.filterProductBtn);
        tabOrdersTv=findViewById(R.id.tabOrdersTv);
        productsRl=findViewById(R.id.productsRl);
        ordersRl=findViewById(R.id.ordersRl);
        productsRv=findViewById(R.id.productsRv);
        filterOrdersTv=findViewById(R.id.filterOrdersTv);
        filterOrderBtn=findViewById(R.id.filterOrderBtn);
        ordersRv=findViewById(R.id.ordersRv);
        reviewsBtn=findViewById(R.id.reviewsBtn);

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth=FirebaseAuth.getInstance();

        checkUser();

        loadAllProducts();

        loadAllOrders();
        showOrdersUI();

        searchProductEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    adapterProductAdmin.getFilter().filter(s);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminBookstoreActivity.this,AddProductActivity.class));
            }
        });
        tabProductsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //load products
                showProductsUI();
            }
        });
        tabOrdersTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //load orders
                showOrdersUI();

            }
        });
        filterProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(AdminBookstoreActivity.this);
                builder.setTitle("Choose Category : ")
                        .setItems(Constants.productCategory1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //get selected item
                                String selected = Constants.productCategory1[which];
                                filteredProductsTv.setText(selected);
                                if(selected.equals("All")){
                                    loadAllProducts();
                                }else {
                                    loadFilteredProducts(selected);
                                }

                            }
                        }).show();

            }
        });
        filterOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] options={"All","In Progress","Completed","Cancelled"};
                AlertDialog.Builder builder=new AlertDialog.Builder(AdminBookstoreActivity.this);
                builder.setTitle("Filter Orders")
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which==0){
                                    filterOrdersTv.setText("Showing All Orders");
                                    adapterOrderShop.getFilter().filter("");
                                }else {
                                    String optionClicked=options[which];
                                    filterOrdersTv.setText("Showing "+optionClicked+" Orders");
                                    adapterOrderShop.getFilter().filter(optionClicked);
                                }
                            }
                        })
                        .show();

            }
        });
        reviewsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminBookstoreActivity.this,ShopReviewsActivity.class);
                intent.putExtra("shopUid",""+firebaseAuth.getUid());
                startActivity(intent);
            }
        });

    }

    private void loadAllOrders() {
        orderShopArrayList=new ArrayList<>();

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Orders")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        orderShopArrayList.clear();
                        for(DataSnapshot ds:dataSnapshot.getChildren()){
                            ModelOrderShop modelOrderShop=ds.getValue(ModelOrderShop.class);
                            orderShopArrayList.add(modelOrderShop);
                        }
                        adapterOrderShop=new AdapterOrderShop(AdminBookstoreActivity.this,orderShopArrayList);
                        ordersRv.setAdapter(adapterOrderShop);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadFilteredProducts(final String selected) {
        productList=new ArrayList<>();
        //get all products
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //before getting reset list
                        for(DataSnapshot ds : dataSnapshot.getChildren()){

                            String productCategory=""+ds.child("productCategory").getValue();
                            if(selected.equals(productCategory)){
                                ModelProduct modelProduct=ds.getValue(ModelProduct.class);
                                productList.add(modelProduct);
                            }

                        }
                        adapterProductAdmin= new AdapterProductAdmin(AdminBookstoreActivity.this,productList);
                        //set adapter
                        productsRv.setAdapter(adapterProductAdmin);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadAllProducts() {
        productList=new ArrayList<>();
        //get all products
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //before getting reset list
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            ModelProduct modelProduct=ds.getValue(ModelProduct.class);
                            productList.add(modelProduct);
                        }
                        adapterProductAdmin= new AdapterProductAdmin(AdminBookstoreActivity.this,productList);
                        //set adapter
                        productsRv.setAdapter(adapterProductAdmin);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void showProductsUI() {
        //show products ui and hide orders ui
        productsRl.setVisibility(View.VISIBLE);
        ordersRl.setVisibility(View.GONE);

        tabProductsTv.setTextColor(getResources().getColor(R.color.black));
        tabProductsTv.setBackgroundResource(R.drawable.shape_rect04);

        tabOrdersTv.setTextColor(getResources().getColor(R.color.white));
        tabOrdersTv.setBackgroundColor(getResources().getColor(R.color.purple));
    }

    private void showOrdersUI() {
        //show order ui and hide products ui
        productsRl.setVisibility(View.GONE);
        ordersRl.setVisibility(View.VISIBLE);

        tabOrdersTv.setTextColor(getResources().getColor(R.color.black));
        tabOrdersTv.setBackgroundResource(R.drawable.shape_rect04);

        tabProductsTv.setTextColor(getResources().getColor(R.color.white));
        tabProductsTv.setBackgroundColor(getResources().getColor(R.color.purple));
    }


    private void checkUser() {
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user==null){
            startActivity(new Intent(AdminBookstoreActivity.this,LoginActivity.class));
            finish();
        }else {
            loadMyInfo();
        }
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

                            nameTv.setText(name);
                            emailTv.setText(email);
                            try{
                                Picasso.get().load(image).placeholder(R.drawable.ic_store).into(profileIv);
                            }catch (Exception e){
                                profileIv.setImageResource(R.drawable.ic_store);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}