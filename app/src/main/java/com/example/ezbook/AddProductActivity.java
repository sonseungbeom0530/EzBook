package com.example.ezbook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;
import java.util.HashMap;

public class AddProductActivity extends AppCompatActivity {

    //ui views
    private ImageButton backBtn;
    private ImageView productIconIv;
    private EditText titleEt,descriptionEt,authorEt;
    private TextView categoryTv,quantityEt,priceEt,discountedPriceEt,discountedNoteEt;
    private SwitchCompat discountSwitch;
    private Button addProductBtn;

    //permission constants
    private static final int CAMERA_REQUEST_CODE=200;
    private static final int STORAGE_REQUEST_CODE=300;
    //image pick constats
    private static final int IMAGE_PICK_GALLERY_CODE=400;
    private static final int IMAGE_PICK_CAMERA_CODE=500;
    //permission arrays
    private String[] cameraPermissions;
    private String[] storagePermissions;

    //image picked uri
    private Uri image_uri;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        //init ui views
        backBtn=findViewById(R.id.backBtn);
        productIconIv=findViewById(R.id.productIconIv);
        titleEt=findViewById(R.id.titleEt);
        descriptionEt=findViewById(R.id.descriptionEt);
        categoryTv=findViewById(R.id.categoryTv);
        quantityEt=findViewById(R.id.quantityEt);
        priceEt=findViewById(R.id.priceEt);
        authorEt=findViewById(R.id.authorEt);
        discountSwitch=findViewById(R.id.discountSwitch);
        discountedPriceEt=findViewById(R.id.discountedPriceEt);
        discountedNoteEt=findViewById(R.id.discountedNoteEt);
        addProductBtn=findViewById(R.id.addProductBtn);

        discountedPriceEt.setVisibility(View.GONE);
        discountedNoteEt.setVisibility(View.GONE);

        firebaseAuth=FirebaseAuth.getInstance();

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        //init permission arrays
        cameraPermissions=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        discountSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked){
                if (isChecked){
                    //checked, show discountPriceEt, discountNoteEt
                    discountedPriceEt.setVisibility(View.VISIBLE);
                    discountedNoteEt.setVisibility(View.VISIBLE);
                }else {
                    discountedPriceEt.setVisibility(View.GONE);
                    discountedNoteEt.setVisibility(View.GONE);
                }
            }
        });
        productIconIv.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //show idalog to pick image
                showImagePickDialog();
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        categoryTv.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //pick category
                categoryDialog();

            }
        });

        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        });

    }

    private String productTitle,productDescription,productCategory,productQuantity,originalPrice,discountPrice,discountNote,author;
    private boolean discountAvailable=false;
    private void inputData() {
        //input data
        productTitle=titleEt.getText().toString().trim();
        productDescription=descriptionEt.getText().toString().trim();
        productCategory=categoryTv.getText().toString().trim();
        productQuantity=quantityEt.getText().toString().trim();
        originalPrice=priceEt.getText().toString().trim();
        author=authorEt.getText().toString().trim();

        discountAvailable=discountSwitch.isChecked();

        if(TextUtils.isEmpty(productTitle)){
            Toast.makeText(this,"Title is required",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(productCategory)){
            Toast.makeText(this,"product category is required",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(originalPrice)){
            Toast.makeText(this,"Price is required",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(author)){
            Toast.makeText(this,"author is required",Toast.LENGTH_SHORT).show();
            return;
        }
        if(discountAvailable){
            //product is with discount
            discountPrice=discountedPriceEt.getText().toString().trim();
            discountNote=discountedNoteEt.getText().toString().trim();
            if(TextUtils.isEmpty(discountPrice)){
                Toast.makeText(this,"Discount price is required",Toast.LENGTH_SHORT).show();
                return;
            }
        }else {
            discountPrice="0";
            discountNote="";
        }

        addProduct();
    }

    private void addProduct() {
        progressDialog.setMessage("Adding product...");
        progressDialog.show();
        final String timeStamp=""+System.currentTimeMillis();
        if(image_uri==null){
            HashMap<String,Object> hashMap=new HashMap<>();
            hashMap.put("productId",""+timeStamp);
            hashMap.put("productTitle",""+productTitle);
            hashMap.put("productDescription",""+productDescription);
            hashMap.put("productCategory",""+productCategory);
            hashMap.put("productQuantity",""+productQuantity);
            hashMap.put("productIcon","");
            hashMap.put("originalPrice",""+originalPrice);
            hashMap.put("discountPrice",""+discountPrice);
            hashMap.put("discountNote",""+discountNote);
            hashMap.put("author",""+author);
            hashMap.put("discountAvailable",""+discountAvailable);
            hashMap.put("timeStamp",""+timeStamp);
            hashMap.put("uid",""+firebaseAuth.getUid());

            //add to db
            DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid()).child("Products").child(timeStamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //added to db
                            progressDialog.dismiss();
                            Toast.makeText(AddProductActivity.this,"Product added",Toast.LENGTH_SHORT).show();
                            clearData();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddProductActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });

        }else {
            //upload with image
            String filePathAndName="product_images/"+""+timeStamp;

            StorageReference storageReference= FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //image uploaded
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful()) ;
                            Uri downloadImageUri = uriTask.getResult();
                            if (uriTask.isSuccessful()) {
                                //uri of image received, upload to db
                                HashMap<String,Object> hashMap=new HashMap<>();
                                hashMap.put("productId",""+timeStamp);
                                hashMap.put("productTitle",""+productTitle);
                                hashMap.put("productDescription",""+productDescription);
                                hashMap.put("productCategory",""+productCategory);
                                hashMap.put("productQuantity",""+productQuantity);
                                hashMap.put("productIcon",""+downloadImageUri);
                                hashMap.put("originalPrice",""+originalPrice);
                                hashMap.put("discountPrice",""+discountPrice);
                                hashMap.put("discountNote",""+discountNote);
                                hashMap.put("author",""+author);
                                hashMap.put("discountAvailable",""+discountAvailable);
                                hashMap.put("timeStamp",""+timeStamp);
                                hashMap.put("uid",""+firebaseAuth.getUid());

                                //add to db
                                DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
                                reference.child(firebaseAuth.getUid()).child("Products").child(timeStamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //added to db
                                                progressDialog.dismiss();
                                                Toast.makeText(AddProductActivity.this,"Product added",Toast.LENGTH_SHORT).show();
                                                clearData();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(AddProductActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddProductActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });

        }

    }

    private void clearData() {
        //clear data after uploading product
        titleEt.setText("");
        descriptionEt.setText("");
        categoryTv.setText("");
        quantityEt.setText("");
        priceEt.setText("");
        authorEt.setText("");
        discountedPriceEt.setText("");
        discountedNoteEt.setText("");
        productIconIv.setImageResource(R.drawable.ic_baseline_add_shopping_cart_24);
        image_uri=null;

    }

    private void categoryDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Product Category")
                .setItems(Constants.productCategory, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String category=Constants.productCategory[which];
                        categoryTv.setText(category);
                    }
                })
                .show();
    }

    private void showImagePickDialog() {
        //options to display in dialog
        String[] options={"Camera","Gallery"};
        //dialog
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Pick image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //handle item clicks
                        if(which==0){
                            //camera clicked
                            if(checkCameraPermission()){
                                //permission granted
                                pickFromCamera();
                            }else {
                                //permission not granted, request
                                requestCameraPermission();
                            }
                        }else {
                            //gallery clicked
                            if(checkStoragePermission()){
                                pickFromGallery();
                            }else {
                                requestStoragePermission();
                            }
                        }

                    }
                })
                .show();
    }

    private void pickFromGallery(){
        //intent to pick image from gallery
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera(){
        //intent to pick from camera

        //using media store to pick high/original quality image
        ContentValues contentValues=new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"Temp_Image_Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"Temp_Image_Description");

        image_uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);

        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(intent,IMAGE_PICK_CAMERA_CODE);
    }

    private boolean checkStoragePermission(){
        boolean result= ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==(PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storagePermissions,STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean result= ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                ==(PackageManager.PERMISSION_GRANTED);
        boolean result1= ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==(PackageManager.PERMISSION_GRANTED);
        return result&&result1;
    }
    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermissions,CAMERA_REQUEST_CODE);
    }

    //handle permission results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean cameraAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted=grantResults[1]==PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted&&storageAccepted){
                        //both permission granted
                        pickFromCamera();
                    }else {
                        //both or one of permission denied
                        Toast.makeText(this,"Camera & Storage permission are required",Toast.LENGTH_SHORT).show();
                    }

                }
            }
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean storageAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if(storageAccepted){
                        //permission granted
                        pickFromGallery();
                    }else {
                        Toast.makeText(this,"Storage permission is required",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //handle image pick results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode==RESULT_OK){
            if(requestCode==IMAGE_PICK_GALLERY_CODE){
                //image picked from gallery
                //save picked image uri
                image_uri=data.getData();
                //set image
                productIconIv.setImageURI(image_uri);
            }else if(requestCode==IMAGE_PICK_CAMERA_CODE){
                //image picked from camera
                productIconIv.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}