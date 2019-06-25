package com.example.unitygames.app_with_firebase.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.unitygames.app_with_firebase.Activities.PostDetailActivity;
import com.example.unitygames.app_with_firebase.R;

import java.util.List;

import Models.Post;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    Context mContext;
    List<Post> mData;

    public PostAdapter(Context mContext, List<Post> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.row_post_item,parent,false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.tvTitle.setText(mData.get(position).getTitle());
        Glide.with(mContext).load(mData.get(position).getPicture()).into(holder.imgPost);
        Glide.with(mContext).load(mData.get(position).getUserPhoto()).into(holder.imgPostProfile);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvTitle;
        ImageView imgPost;
        ImageView imgPostProfile;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle =itemView.findViewById(R.id.row_post_title);
            imgPost = itemView.findViewById(R.id.row_post_img);
            imgPostProfile = itemView.findViewById(R.id.row_post_profile_img);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext,PostDetailActivity.class);

                    int pos = getAdapterPosition();
                    intent.putExtra("title",mData.get(pos).getTitle());
                    intent.putExtra("postImage",mData.get(pos).getPicture());
                    intent.putExtra("description",mData.get(pos).getDescription());
                    intent.putExtra("postKey",mData.get(pos).getPostKey());
                    intent.putExtra("userPhoto",mData.get(pos).getUserPhoto());
                    // will fix this later i forgot to add user name to post object
                    //postDetailActivity.putExtra("userName",mData.get(position).getUsername);
                    long timeStamp = (long)mData.get(pos).getTimeStamp();
                    intent.putExtra("postDate",timeStamp);
                    mContext.startActivity(intent);



                }
            });
        }
    }
}
