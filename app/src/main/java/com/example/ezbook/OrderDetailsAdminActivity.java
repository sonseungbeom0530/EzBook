package com.example.ezbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ezbook.Adapters.AdapterOrderedItem;
import com.example.ezbook.Models.ModelOrderedItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class OrderDetailsAdminActivity extends AppCompatActivity {

    private ImageButton backBtn,editBtn,mapBtn;
    private TextView orderIdTv,dateTv,orderStatusTv,emailTv,phoneTv,totalItemsTv,amountTv,addressTv;
    private RecyclerView itemsRv;

    String orderId,orderBy;

    String sourceLatitude,sourceLongitude,destinationLatitude,destinationLongitude;

    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelOrderedItem> orderedItemArrayList;
    private AdapterOrderedItem adapterOrderedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details_admin);

        backBtn=findViewById(R.id.backBtn);
        editBtn=findViewById(R.id.editBtn);
        mapBtn=findViewById(R.id.mapBtn);
        orderIdTv=findViewById(R.id.orderIdTv);
        dateTv=findViewById(R.id.dateTv);
        emailTv=findViewById(R.id.emailTv);
        phoneTv=findViewById(R.id.phoneTv);
        totalItemsTv=findViewById(R.id.totalItemsTv);
        amountTv=findViewById(R.id.amountTv);
        addressTv=findViewById(R.id.addressTv);
        itemsRv=findViewById(R.id.itemsRv);
        orderStatusTv=findViewById(R.id.orderStatusTv);


        orderId=getIntent().getStringExtra("orderId");
        orderBy=getIntent().getStringExtra("orderBy");

        firebaseAuth=FirebaseAuth.getInstance();
        loadMyInfo();
        loadBuyerInfo();
        loadOrderDetails();
        loadOrderedItems();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        });
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editOrderStatusDialog();
            }
        });
    }

    private void editOrderStatusDialog() {
        final String[] options={"In Progress","Completed","Cancelled"};
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Edit Order Status")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedOption=options[which];
                        editOrderStatus(selectedOption);
                    }
                })
                .show();
    }

    private void editOrderStatus(final String selectedOption) {
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("orderStatus",""+selectedOption);

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Orders").child(orderId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(OrderDetailsAdminActivity.this,"Order is now"+selectedOption,Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OrderDetailsAdminActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadOrderDetails() {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Orders").child(orderId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String orderBy=""+dataSnapshot.child("orderBy").getValue();
                        String orderCost=""+dataSnapshot.child("orderCost").getValue();
                        String orderId=""+dataSnapshot.child("orderId").getValue();
                        String orderStatus=""+dataSnapshot.child("orderStatus").getValue();
                        String orderTime=""+dataSnapshot.child("orderTime").getValue();
                        String orderTo=""+dataSnapshot.child("orderTo").getValue();
                        String latitude=""+dataSnapshot.child("latitude").getValue();
                        String longitude=""+dataSnapshot.child("longitude").getValue();

                        Calendar calendar=Calendar.getInstance();
                        calendar.setTimeInMillis(Long.parseLong(orderTime));
                        String dateFormated= DateFormat.format("dd/MM/yyyy",calendar).toString();

                        if (orderStatus.equals("In Progress")){
                            orderStatusTv.setTextColor(getResources().getColor(R.color.purple));
                        }else
                        if (orderStatus.equals("Completed")){
                            orderStatusTv.setTextColor(getResources().getColor(R.color.green));
                        }else if (orderStatus.equals("Cancelled")){
                            orderStatusTv.setTextColor(getResources().getColor(R.color.red));
                        }

                        orderIdTv.setText(orderId);
                        orderStatusTv.setText(orderStatus);
                        amountTv.setText("$"+orderCost);
                        dateTv.setText(dateFormated);

                        findAddress(latitude,longitude);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void findAddress(String latitude, String longitude) {

        double lat=Double.parseDouble(latitude);
        double lon=Double.parseDouble(longitude);

        Geocoder geocoder;
        List<Address> addresses;
        geocoder=new Geocoder(this, Locale.getDefault());
        try {
            addresses=geocoder.getFromLocation(lat,lon,1);
            String address=addresses.get(0).getAddressLine(0);
            addressTv.setText(address);
        }catch (Exception e){
            Toast.makeText(this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
        }

    }

    private void loadOrderedItems(){

        orderedItemArrayList=new ArrayList<>();
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Orders").child(orderId).child("Items")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        orderedItemArrayList.clear();
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            ModelOrderedItem modelOrderedItem=ds.getValue(ModelOrderedItem.class);
                            orderedItemArrayList.add(modelOrderedItem);
                        }
                        adapterOrderedItem=new AdapterOrderedItem(OrderDetailsAdminActivity.this,orderedItemArrayList);
                        itemsRv.setAdapter(adapterOrderedItem);

                        totalItemsTv.setText(""+dataSnapshot.getChildrenCount());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void openMap() {
        // String address="https//:maps.google.com/maps?saddr="+myLatitude+","+myLongitude+"&daddr="+shopLatitude+","+shopLongitude;
        //Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse(address));
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr="+sourceLatitude+","+sourceLongitude+"&daddr="+destinationLatitude+","+destinationLongitude));
        startActivity(intent);

    }

    private void loadMyInfo() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        sourceLatitude=""+dataSnapshot.child("latitude").getValue();
                        sourceLongitude =""+dataSnapshot.child("longitude").getValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadBuyerInfo() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(orderBy)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        destinationLatitude=""+dataSnapshot.child("latitude").getValue();
                        destinationLongitude =""+dataSnapshot.child("longitude").getValue();
                        String email =""+dataSnapshot.child("email").getValue();
                        String phone =""+dataSnapshot.child("phone").getValue();

                        emailTv.setText(email);
                        phoneTv.setText(phone);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}