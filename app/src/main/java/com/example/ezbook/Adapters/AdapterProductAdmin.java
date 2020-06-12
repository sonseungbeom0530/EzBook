package com.example.ezbook.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezbook.EditProductActivity;
import com.example.ezbook.FilterProduct;
import com.example.ezbook.FilterProductUser;
import com.example.ezbook.Models.ModelProduct;
import com.example.ezbook.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterProductAdmin extends RecyclerView.Adapter<AdapterProductAdmin.HolderProductAdmin> implements Filterable {

    private Context context;
    public ArrayList<ModelProduct> productList,filterList;
    private FilterProduct filter;

    public AdapterProductAdmin(Context context, ArrayList<ModelProduct> productList) {
        this.context = context;
        this.productList = productList;
        this.filterList=productList;
    }

    @NonNull
    @Override
    public HolderProductAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view=LayoutInflater.from(context).inflate(R.layout.row_product_admin,parent,false);
        return new HolderProductAdmin(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductAdmin holder, int position) {
        //get data
        final ModelProduct modelProduct=productList.get(position);
        String id=modelProduct.getProductId();
        String uid=modelProduct.getUid();
        String discountAvailable=modelProduct.getDiscountAvailable();
        String discountNote=modelProduct.getDiscountNote();
        String discountPrice=modelProduct.getDiscountPrice();
        String productCategory=modelProduct.getProductCategory();
        String productDescription=modelProduct.getProductDescription();
        String icon=modelProduct.getProductIcon();
        String title=modelProduct.getProductTitle();
        String timeStamp=modelProduct.getTimeStamp();
        String quantity=modelProduct.getProductQuantity();
        String originalPrice=modelProduct.getOriginalPrice();

        //set data
        holder.titleTv.setText(title);
        holder.quantityTv.setText(quantity);
        holder.discountedNoteTv.setText(discountNote);
        holder.discountedPriceTv.setText("$"+discountPrice);
        holder.originalPriceTv.setText("$"+originalPrice);
        if(discountAvailable.equals("true")){
            //product is on discount
            holder.discountedPriceTv.setVisibility(View.VISIBLE);
            holder.discountedNoteTv.setVisibility(View.VISIBLE);
            holder.originalPriceTv.setPaintFlags(holder.originalPriceTv.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        }else {
            holder.discountedPriceTv.setVisibility(View.GONE);
            holder.discountedNoteTv.setVisibility(View.GONE);
            holder.originalPriceTv.setPaintFlags(0);

        }
        try{
            Picasso.get().load(icon).placeholder(R.drawable.ic_shoppingcart_purple).into(holder.productIconIv);
        }catch (Exception e){
            holder.productIconIv.setImageResource(R.drawable.ic_shoppingcart_purple);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //handle item clicks, show item details
                detailsBottomSheet(modelProduct);
            }
        });

    }

    private void detailsBottomSheet(ModelProduct modelProduct) {
        //bottom sheet
        final BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(context);
        View view=LayoutInflater.from(context).inflate(R.layout.bs_product_details_admin,null);
        bottomSheetDialog.setContentView(view);


        //init view of bottomsheet
        ImageButton backBtn=view.findViewById(R.id.backBtn);
        ImageButton deleteBtn=view.findViewById(R.id.deleteBtn);
        ImageButton editBtn=view.findViewById(R.id.editBtn);
        ImageView productIconIv=view.findViewById(R.id.productIconIv);
        TextView discountNoteTv=view.findViewById(R.id.discountNoteTv);
        TextView titleTv=view.findViewById(R.id.titleTv);
        TextView descriptionTv=view.findViewById(R.id.descriptionTv);
        TextView categoryTv=view.findViewById(R.id.categoryTv);
        TextView quantityTv=view.findViewById(R.id.quantityTv);
        TextView discountedPriceTv=view.findViewById(R.id.discountPriceTv);
        TextView originalPriceTv=view.findViewById(R.id.originalPriceTv);

        //get data
        final String id=modelProduct.getProductId();
        String uid=modelProduct.getUid();
        String discountAvailable=modelProduct.getDiscountAvailable();
        String discountNote=modelProduct.getDiscountNote();
        String discountPrice=modelProduct.getDiscountPrice();
        String productCategory=modelProduct.getProductCategory();
        String productDescription=modelProduct.getProductDescription();
        String icon=modelProduct.getProductIcon();
        final String title=modelProduct.getProductTitle();
        String timeStamp=modelProduct.getTimeStamp();
        String quantity=modelProduct.getProductQuantity();
        String originalPrice=modelProduct.getOriginalPrice();

        //set data
        titleTv.setText(title);
        descriptionTv.setText(productDescription);
        categoryTv.setText(productCategory);
        quantityTv.setText(quantity);
        discountNoteTv.setText(discountNote);
        discountedPriceTv.setText("$"+discountPrice);
        originalPriceTv.setText("$"+originalPrice);
        if(discountAvailable.equals("true")){
            //product is on discount
            discountedPriceTv.setVisibility(View.VISIBLE);
            discountNoteTv.setVisibility(View.VISIBLE);
            originalPriceTv.setPaintFlags(originalPriceTv.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        }else {
            discountedPriceTv.setVisibility(View.GONE);
            discountNoteTv.setVisibility(View.GONE);
        }
        try{
            Picasso.get().load(icon).placeholder(R.drawable.ic_shoppingcart_purple).into(productIconIv);
        }catch (Exception e){
            productIconIv.setImageResource(R.drawable.ic_shoppingcart_purple);
        }

        //show dialog
        bottomSheetDialog.show();

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                Intent intent=new Intent(context, EditProductActivity.class);
                intent.putExtra("productId",id);
                context.startActivity(intent);
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Art you sure you wan to delete product"+title+"?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteProduct(id);
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();

            }
        });

    }
    private void deleteProduct(String id){
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products").child(id).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context,"Product deleted",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter==null){
            filter=new FilterProduct(this,filterList);
        }
        return filter;
    }


    class HolderProductAdmin extends RecyclerView.ViewHolder{

        private ImageView productIconIv;
        private TextView discountedNoteTv,titleTv,quantityTv,discountedPriceTv,originalPriceTv;

        public HolderProductAdmin(@NonNull View itemView) {
            super(itemView);

            productIconIv=itemView.findViewById(R.id.productIconIv);
            discountedNoteTv=itemView.findViewById(R.id.discountedNoteTv);
            titleTv=itemView.findViewById(R.id.titleTv);
            quantityTv=itemView.findViewById(R.id.quantityTv);
            discountedPriceTv=itemView.findViewById(R.id.discountedPriceTv);
            originalPriceTv=itemView.findViewById(R.id.originalPrice);


        }
    }
}
