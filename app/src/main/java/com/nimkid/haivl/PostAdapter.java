package com.nimkid.haivl;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.like.LikeButton;
import com.like.OnLikeListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by thang on 1/24/17.
 */

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<Post> posts;
    private Context context;
    private LayoutInflater layoutInflater;
    AccessToken accessToken = AccessToken.getCurrentAccessToken();
    SharedPreferences sharedPreferences;

    public PostAdapter(List<Post> posts, Context context) {
        this.posts = posts;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }


    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = layoutInflater.inflate(R.layout.row_post2,parent,false);
        return new PostViewHolder(item);
    }

    @Override
    public void onBindViewHolder(final PostViewHolder holder, int position) {
        Post post = posts.get(position);


        holder.txtAuthor_name.setText(post.getAuthor_name());
        holder.txtTitle.setText(post.getTitle());
        holder.txtTime.setText(post.getTime());
        holder.txtLine.setBackgroundColor(Color.GRAY);
        holder.txtID.setText(post.getId()+"");
/*

        holder.txtDislike.setText(post.getDislike() + "");
        holder.txtLike.setText(post.getLike() + "");
        holder.txtLove.setText(post.getLove() + "");

 */


        if(sharedPreferences.getString("email","") != null){
            String url = "https://android-haivl-zskyfall.c9users.io/islike/"+sharedPreferences.getString("email","")+"/"+post.getId();
                    url = url.trim();
            Ion.with(getApplicationContext())
                    .load(url)
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            if(e != null){
                                Toast.makeText(context,"Loi ION", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                if(result.toString().trim().equalsIgnoreCase("true")){

                                    holder.btnlove.setLiked(true);
                                }
                                else{
                          
                                    holder.btnlove.setLiked(false);
                                }
                            }
                        }
                    });
        }
        else{
            Toast.makeText(context, "Loi SharedPreferences", Toast.LENGTH_SHORT).show();
        }

        Ion.with(context)
                .load(post.getImage_url())
                .withBitmap()
                .placeholder(R.drawable.ic_loading)
                .error(R.drawable.ic_collections_black_24dp)
                .intoImageView(holder.imgView);
        Ion.with(context)
                .load(post.getAuthor_avatar())
                .withBitmap()
                .placeholder(R.drawable.ic_loading)
                .error(R.drawable.ic_collections_black_24dp)
                .intoImageView(holder.imgAuthor_avatar);




     //   Glide.with(context).load(post.getImage_url()).into(holder.imgView);
      //  holder.txtUrl.setText(post.getImage_url());
      //  holder.txtAuthor.setText(post.getAuthor());
    }

    @Override
    public int getItemCount() {
       return posts.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        private TextView txtTitle;
        private TextView txtAuthor_name;
        private TextView txtUrl;
        private TextView txtLike;
        private TextView txtLove;
        private TextView txtDislike;
        private TextView txtID;
        private TextView txtLine;
        private TextView txtTime;
        private ImageView imgView;
        private ImageView imgAuthor_avatar;
        private LikeButton btnlove,btnlike;

        String name,id,gender,cover,picture,location,email;
        int age_max,age_min;
        JSONObject objLocation,objCover,objPicture,objAge_range;

        public PostViewHolder(View itemView) {
            super(itemView);


            txtID = (TextView) itemView.findViewById(R.id.txtID);
         //   txtUrl = (TextView) itemView.findViewById(R.id.txtURL);
            txtTitle = (TextView) itemView.findViewById(R.id.edtPostTitle);
            txtAuthor_name = (TextView) itemView.findViewById(R.id.txtAuthor_name);
         //   txtTime = (TextView) itemView.findViewById(R.id.txtTime);
            txtLike = (TextView) itemView.findViewById(R.id.txtLike);
            txtLove = (TextView) itemView.findViewById(R.id.txtLove);
            txtDislike = (TextView) itemView.findViewById(R.id.txtDislike);
            txtTime = (TextView) itemView.findViewById(R.id.txtTime);
            txtLine = (TextView) itemView.findViewById(R.id.txtLine);

            btnlove = (LikeButton) itemView.findViewById(R.id.btnlove);

            if(accessToken != null){
                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {

                                try {
                                    name = object.getString("name").toString();
                                    email = object.getString("email").toString();
                                    id = object.getString("id").toString();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday,location,locale,link,picture,cover");
                request.setParameters(parameters);
                request.executeAsync();


            }
            sharedPreferences = context.getSharedPreferences("email",MODE_PRIVATE);




            btnlove.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    if(id != null){
                        Toast.makeText(context, "Da Like", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(context, "Xin dang nhap truoc", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void unLiked(LikeButton likeButton) {

                    if(sharedPreferences.getString("email","") != null){
                        String url = "https://android-haivl-zskyfall.c9users.io/islike/"+sharedPreferences.getString("email","")+"/"+txtID.getText();
                        url = url.trim();

                        Ion.with(getApplicationContext())
                                .load(url)
                                .asString()
                                .setCallback(new FutureCallback<String>() {
                                    @Override
                                    public void onCompleted(Exception e, String result) {
                                        if(e != null){
                                            Toast.makeText(context,"Loi ION", Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            if(result.toString().trim().equalsIgnoreCase("true")){
                                                Toast.makeText(context, "Da Unlike", Toast.LENGTH_SHORT).show();
                                            }
                                            else{
                                                //
                                            }
                                        }
                                    }
                                });
                    }
                    else{
                        Toast.makeText(context, "Loi SharedPreferences", Toast.LENGTH_SHORT).show();
                    }

                }
            });

                imgView = (ImageView) itemView.findViewById(R.id.imgView);
                imgAuthor_avatar = (ImageView) itemView.findViewById(R.id.imgAuthor_avatar);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            Post post = posts.get(getAdapterPosition());
                        Toast.makeText(context, post.getTitle(), Toast.LENGTH_SHORT).show();

                    }
                });
        }
    }
}
