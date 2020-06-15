package com.example.ezbook.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Paint;
import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezbook.Contact;
import com.example.ezbook.DataBaseHandler;
import com.example.ezbook.FilterProductUser;
import com.example.ezbook.Models.ModelProduct;
import com.example.ezbook.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterProductUser extends RecyclerView.Adapter<AdapterProductUser.HolderProductUser> implements Filterable {

    private Context context;
    public ArrayList<ModelProduct> productsList,filterList;
    private FilterProductUser filter;

    public AdapterProductUser(Context context, ArrayList<ModelProduct> productsList) {
        this.context = context;
        this.productsList = productsList;
        this.filterList = productsList;

    }

    @NonNull
    @Override
    public HolderProductUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.row_product_user,parent,false);
        return new HolderProductUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductUser holder, int position) {
            //get data
        final ModelProduct modelProduct=productsList.get(position);
        String discountAvailable=modelProduct.getDiscountAvailable();
        String discountNote=modelProduct.getDiscountNote();
        String discountPrice=modelProduct.getDiscountPrice();
        String productCategory=modelProduct.getProductCategory();
        String originalPrice=modelProduct.getOriginalPrice();
        String productDescription=modelProduct.getProductDescription();
        String productTitle=modelProduct.getProductTitle();
        String productQuantity=modelProduct.getProductQuantity();
        String productId=modelProduct.getProductId();
        String timeStamp=modelProduct.getTimeStamp();
        String productIcon=modelProduct.getProductIcon();

        //set data
        holder.titleTv.setText(productTitle);
        holder.descriptionTv.setText(productDescription);
        holder.originalPriceTv.setText("$"+originalPrice);
        holder.discountedPriceTv.setText("$"+discountPrice);
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
            Picasso.get().load(productIcon).placeholder(R.drawable.ic_shoppingcart_purple).into(holder.productIconIv);
        }catch (Exception e){
            holder.productIconIv.setImageResource(R.drawable.ic_shoppingcart_purple);
        }
        holder.addToCartTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQuantityDialog(modelProduct);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private double cost=0;
    private double finalCost=0;
    private int quantity=0;

    private void showQuantityDialog(ModelProduct modelProduct) {
        //inflate layout for dialog
        View view =LayoutInflater.from(context).inflate(R.layout.dialog_quantity,null);
        ImageView productIv=view.findViewById(R.id.productIv);
        final TextView titleTv=view.findViewById(R.id.titleTv);
        TextView pQuantityIv=view.findViewById(R.id.pQuantityIv);
        TextView descriptionTv=view.findViewById(R.id.descriptionTv);
        TextView discountNoteTv=view.findViewById(R.id.discountNoteTv);
        final TextView originalPriceTv=view.findViewById(R.id.originalPriceTv);
        TextView priceDiscountedTv=view.findViewById(R.id.priceDiscountedTv);
        final TextView finalPriceTv=view.findViewById(R.id.finalPriceTv);
        ImageButton decrementBtn=view.findViewById(R.id.decrementBtn);
        final TextView quantityTv=view.findViewById(R.id.quantityTv);
        ImageButton incrementBtn=view.findViewById(R.id.incrementBtn);
        Button continueBtn=view.findViewById(R.id.continueBtn);

        //get data from model
        final String productId=modelProduct.getProductId();
        String title=modelProduct.getProductTitle();
        String productQuantity=modelProduct.getProductQuantity();
        String description=modelProduct.getProductDescription();
        String discountNote=modelProduct.getDiscountNote();
        String image=modelProduct.getProductIcon();

        final String price;
        if(modelProduct.getDiscountAvailable().equals("true")){
            price=modelProduct.getDiscountPrice();
            discountNoteTv.setVisibility(View.VISIBLE);
            originalPriceTv.setPaintFlags(originalPriceTv.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);

        }else {
            //product dont have discount
            discountNoteTv.setVisibility(View.GONE);
            priceDiscountedTv.setVisibility(View.GONE);
            price=modelProduct.getOriginalPrice();

        }
        cost=Double.parseDouble(price.replaceAll("$",""));
        finalCost=Double.parseDouble(price.replaceAll("$",""));
        quantity=1;
        //dialog
        AlertDialog.Builder builder=new AlertDialog.Builder(context);;
        builder.setView(view);

        try {
            Picasso.get().load(image).placeholder(R.drawable.ic_cart_gray).into(productIv);

        }catch (Exception e){
            productIv.setImageResource(R.drawable.ic_cart_gray);

        }
        titleTv.setText(""+title);
        pQuantityIv.setText(""+productQuantity);
        descriptionTv.setText(""+description);
        discountNoteTv.setText(""+discountNote);
        quantityTv.setText(""+quantity);
        originalPriceTv.setText("$"+modelProduct.getOriginalPrice());
        priceDiscountedTv.setText("$"+modelProduct.getDiscountNote());
        finalPriceTv.setText("$"+finalCost);

        final AlertDialog dialog=builder.create();
        dialog.show();

        incrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalCost=finalCost+cost;
                quantity++;

                finalPriceTv.setText("$"+finalCost);
                quantityTv.setText(""+quantity);
            }
        });
        decrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity>1){
                    finalCost=finalCost=cost;
                    quantity--;

                    finalPriceTv.setText("$"+finalCost);
                    quantityTv.setText(""+quantity);
                }
            }
        });
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title=titleTv.getText().toString().trim();
                String priceEach=price;
                String totalPrice=finalPriceTv.getText().toString().trim().replace("$","");
                String quantity=quantityTv.getText().toString().trim();
                //add to db(SQLite)
                addToCart(productId,title,priceEach,totalPrice,quantity);
                dialog.dismiss();
            }
        });
    }
    private int itemId=1;
    private void addToCart(String productId, String title, String priceEach, String price, String quantity) {
        itemId++;
        DataBaseHandler dataBaseHandler=new DataBaseHandler(context);
        dataBaseHandler.addItem(new Contact(itemId,productId,title,priceEach,price,quantity));

        Toast.makeText(context,"added to cart...",Toast.LENGTH_SHORT).show();

    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter==null){
            filter=new FilterProductUser(this,filterList);
        }
        return filter;
    }

    class HolderProductUser extends RecyclerView.ViewHolder{

        private ImageView productIconIv;
        private TextView discountedNoteTv,titleTv,descriptionTv,addToCartTv,discountedPriceTv,originalPriceTv;

        public HolderProductUser(@NonNull View itemView) {
            super(itemView);
            productIconIv=itemView.findViewById(R.id.productIconIv);
            discountedNoteTv=itemView.findViewById(R.id.discountedNoteTv);
            titleTv=itemView.findViewById(R.id.titleTv);
            descriptionTv=itemView.findViewById(R.id.descriptionTv);
            addToCartTv=itemView.findViewById(R.id.addToCartTv);
            discountedPriceTv=itemView.findViewById(R.id.discountedPriceTv);
            originalPriceTv=itemView.findViewById(R.id.originalPriceTv);


        }
    }
}
