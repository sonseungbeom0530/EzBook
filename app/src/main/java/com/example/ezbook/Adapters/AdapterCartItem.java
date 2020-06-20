package com.example.ezbook.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezbook.Contact;
import com.example.ezbook.DataBaseHandler;
import com.example.ezbook.Models.ModelCartItem;
import com.example.ezbook.R;
import com.example.ezbook.ShopDetailsActivity;

import java.util.ArrayList;

public class AdapterCartItem extends RecyclerView.Adapter<AdapterCartItem.HolderCartItem>{

    private Context context;
    private ArrayList<ModelCartItem> cartItems;

    public AdapterCartItem(Context context, ArrayList<ModelCartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }



    @NonNull
    @Override
    public HolderCartItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout row_cartItem.xml
        View view= LayoutInflater.from(context).inflate(R.layout.row_cartitem,parent,false);
        return new HolderCartItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCartItem holder, final int position) {
        //get data
        ModelCartItem modelCartItem=cartItems.get(position);

        final String id=modelCartItem.getId();
        final String getpId=modelCartItem.getpId();
        final String title=modelCartItem.getName();
        final String cost=modelCartItem.getCost();
        final String price=modelCartItem.getPrice();
        final String quantity=modelCartItem.getQuantity();

        //set data
        holder.itemTitleTv.setText(""+title);
        holder.itemPriceTv.setText(""+cost);
        holder.itemQuantityTv.setText("["+quantity+"]");
        holder.itemPriceEachTv.setText(""+price);


        holder.itemRemoveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DataBaseHandler dataBaseHandler=new DataBaseHandler(context);
                dataBaseHandler.deleteItem(new Contact(1,getpId,title,price,cost,quantity));

                Toast.makeText(context,"Removed from cart",Toast.LENGTH_SHORT).show();
                cartItems.remove(position);
                notifyItemChanged(position);
                notifyDataSetChanged();

                double tx=Double.parseDouble((((ShopDetailsActivity)context).allTotalPriceTv.getText().toString().trim().replace("$","")));
                double totalPrice=tx-Double.parseDouble(cost.replace("$",""));
                ((ShopDetailsActivity)context).allTotalPrice=0.00;
                ((ShopDetailsActivity)context).allTotalPriceTv.setText("$"+String.format("%.2f",Double.parseDouble(String.format("%.2f",totalPrice))));

                //after removing item from cart, update cart count
                ((ShopDetailsActivity)context).cartCount();
            }
        });

    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    class HolderCartItem extends RecyclerView.ViewHolder{

        private TextView itemTitleTv,itemPriceTv,itemPriceEachTv,itemQuantityTv,itemRemoveTv;

        public HolderCartItem(@NonNull View itemView) {
            super(itemView);

            itemTitleTv=itemView.findViewById(R.id.itemTitleTv);
            itemPriceTv=itemView.findViewById(R.id.itemPriceTv);
            itemPriceEachTv=itemView.findViewById(R.id.itemPriceEachTv);
            itemQuantityTv=itemView.findViewById(R.id.itemQuantityTv);
            itemRemoveTv=itemView.findViewById(R.id.itemRemoveTv);


        }
    }
}