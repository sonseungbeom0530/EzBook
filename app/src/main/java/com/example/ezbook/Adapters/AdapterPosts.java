package com.example.ezbook.Adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezbook.Models.ModelPost;
import com.example.ezbook.R;
import com.example.ezbook.ThereProfileActivity;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder> {

    Context context;
    List<ModelPost> postList;

    public AdapterPosts(Context context, List<ModelPost> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        //inflate layout row_post.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_posts,viewGroup,false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {

        final String uid =postList.get(i).getUid();
        String uEmail =postList.get(i).getuEmail();
        String uName =postList.get(i).getuName();
        String uDp =postList.get(i).getuDp();
        String pId =postList.get(i).getpId();
        String pTitle=postList.get(i).getpTitle();
        String pDescription =postList.get(i).getpDescr();
        String pImage =postList.get(i).getpImage();
        String pTimeStamp =postList.get(i).getpTime();

        //convert timestamp to dd/mm/YYYY hh:mm am/pm
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
        String pTime= DateFormat.format("dd/MM/yyyy hh:mm aa",calendar).toString();

        //set data
        myHolder.uNameTv.setText(uName);
        myHolder.pTimeTv.setText(pTime);
        myHolder.pTitleTv.setText(pTitle);
        myHolder.pDescriptionTv.setText(pDescription);

        //set user dp
        try{
            Picasso.get().load(uDp).placeholder(R.drawable.ic_default_img_purple).into(myHolder.uPictureIv);
        }catch (Exception e){

        }

        //set post image
        //if there is no image
        if (pImage.equals("noImage")){
            //hide imageview
            myHolder.pImageIv.setVisibility(View.GONE);
        }else {
            try{
                Picasso.get().load(pImage).into(myHolder.pImageIv);
            }catch (Exception e){

            }
        }


        //handle button clicks
        myHolder.moreBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(context,"More",Toast.LENGTH_SHORT).show();
            }
        });
        myHolder.likeBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(context,"Like",Toast.LENGTH_SHORT).show();
            }
        });
        myHolder.commentBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(context,"Comment",Toast.LENGTH_SHORT).show();
            }
        });
        myHolder.profileLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //click to go to ThereProfileActivity with uid, this uid is of clicked user
                //which will be used to show user specific data/post
                Intent intent=new Intent(context, ThereProfileActivity.class);
                intent.putExtra("uid",uid);
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder{

        //views from row_post.xml
        ImageView uPictureIv,pImageIv;
        TextView uNameTv,pTimeTv,pTitleTv,pDescriptionTv,pLikeTv;
        ImageButton moreBtn;
        Button likeBtn,commentBtn;
        LinearLayout profileLayout;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            uPictureIv=itemView.findViewById(R.id.uPictureIv);
            pImageIv=itemView.findViewById(R.id.pImageIv);
            uNameTv=itemView.findViewById(R.id.uNameTv);
            pTimeTv=itemView.findViewById(R.id.pTimeTv);
            pTitleTv=itemView.findViewById(R.id.pTitleTv);
            pDescriptionTv=itemView.findViewById(R.id.pDescriptionTv);
            pLikeTv=itemView.findViewById(R.id.pLikeTv);
            moreBtn=itemView.findViewById(R.id.moreBtn);
            likeBtn=itemView.findViewById(R.id.likeBtn);
            commentBtn=itemView.findViewById(R.id.commentBtn);
            profileLayout=itemView.findViewById(R.id.profileLayout);

        }
    }
}
