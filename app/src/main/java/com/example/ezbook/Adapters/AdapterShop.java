package com.example.ezbook.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezbook.Models.ModelShop;
import com.example.ezbook.R;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AdapterShop extends RecyclerView.Adapter<AdapterShop.HolderShop>{

    private Context context;
    public ArrayList<ModelShop> shopsList;

    public AdapterShop(Context context, ArrayList<ModelShop> shopList) {
        this.context = context;
        this.shopsList = shopList;
    }

    @NonNull
    @Override
    public HolderShop onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout row_shop.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_shop,parent,false);
        return new HolderShop(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderShop holder, int position) {
        //get data
        ModelShop modelShop=shopsList.get(position);
        String accountType=modelShop.getAccountType();
        String address=modelShop.getAddress();
        String city=modelShop.getCity();
        String country=modelShop.getCountry();
        String email=modelShop.getEmail();
        double latitude=modelShop.getLatitude();
        double longitude=modelShop.getLongitude();
        String name=modelShop.getName();
        String password=modelShop.getPassword();
        String phone=modelShop.getPhone();
        String online=modelShop.getOnline();
        String cover=modelShop.getCover();
        String uid=modelShop.getUid();
        String timeStamp=modelShop.getTimestamp();
        String state=modelShop.getState();
        String image=modelShop.getImage();

        holder.nameTv.setText(name);
        holder.phoneTv.setText(phone);
        holder.addressTv.setText(address);
        try {
            Picasso.get().load(image).placeholder(R.drawable.ic_store).into(holder.shopIv);
        }catch (Exception e){
            holder.shopIv.setImageResource(R.drawable.ic_store);
        }
    }

    @Override
    public int getItemCount() {
        return shopsList.size();
    }

    //view holder
    class HolderShop extends RecyclerView.ViewHolder{

        //ui views of row_shop.xml
        private ImageView shopIv;
        private TextView nameTv,phoneTv,addressTv;
        private RatingBar ratingBar;

        public HolderShop(@NonNull View itemView) {
            super(itemView);
            shopIv=itemView.findViewById(R.id.shopIv);
            nameTv=itemView.findViewById(R.id.nameTv);
            phoneTv=itemView.findViewById(R.id.phoneTv);
            addressTv=itemView.findViewById(R.id.addressTv);
            ratingBar=itemView.findViewById(R.id.ratingBar);

        }
    }
}
