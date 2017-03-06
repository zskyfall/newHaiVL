package com.nimkid.haivl;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baoyz.widget.PullRefreshLayout;
import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.http.body.FilePart;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.like.IconType;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.sackcentury.shinebuttonlib.ShineButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener {


     ImageView imgAvatar;
     TextView txtName,txtEmail;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    AccessToken accessToken;
    private int PICK_IMAGE_REQUEST = 1;
    private String pictureSrc;
    private AlertDialog.Builder builder;
    private AlertDialog dia;
    private String postTit;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_main);

        final RecyclerView rvPosts;
        final PostAdapter[] postAdapters = new PostAdapter[1];
        final List<Post> posts;
        LikeButton btnlove;

        mAuth = FirebaseAuth.getInstance();

        accessToken = AccessToken.getCurrentAccessToken();


        rvPosts = (RecyclerView) findViewById(R.id.recyclerView);

        posts = new ArrayList<>();
        posts.add(new Post("1","Bai viet 1","24/01/2017","http://68.media.tumblr.com/50af8ed4e4afcc4530c83057db147a91/tumblr_nltc6y6pau1rx5rfmo1_1280.gif","http://navakashop.com/wp-content/uploads/2016/09/harga-mainan-minions.jpg","Trạch Văn Đoành","11",10,69,210));
        posts.add(new Post("2","Bai viet 2","24/01/2017","http://tophinhanhdep.net/wp-content/uploads/2015/12/anh-dep-mua-xuan-5.jpg","http://navakashop.com/wp-content/uploads/2016/09/harga-mainan-minions.jpg","Lều Thị Mông Mơ","12",10,69,210));
        posts.add(new Post("3","Bai viet 3","24/01/2017","http://tophinhanhdep.net/wp-content/uploads/2015/12/anh-dep-mua-xuan-5.jpg","http://navakashop.com/wp-content/uploads/2016/09/harga-mainan-minions.jpg","Trương Văn Bá Đạo","13",10,69,210));
        posts.add(new Post("4","Bai viet 4","24/01/2017","http://tophinhanhdep.net/wp-content/uploads/2015/12/anh-dep-mua-xuan-5.jpg","http://navakashop.com/wp-content/uploads/2016/09/harga-mainan-minions.jpg","Lê Thị Nghịch Lý","14",10,69,210));

        postAdapters[0]= new PostAdapter(posts,getApplicationContext());
        rvPosts.setAdapter(postAdapters[0]);

        //RecyclerView scroll vertical
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvPosts.setLayoutManager(linearLayoutManager);


        //TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                if(accessToken != null){
                    builder = new AlertDialog.Builder(MainActivity.this);
                    final View dialog = getLayoutInflater().inflate(R.layout.dialog_post,null);
                    Button btnSubmit = (Button) dialog.findViewById(R.id.btnPostSubmit);
                    final EditText edtPostTitle = (EditText) dialog.findViewById(R.id.edtPostTitle);
                    ImageButton imgbtnUpload = (ImageButton) dialog.findViewById(R.id.imgbtnUpload);
                    ImageView imgpic = (ImageView) dialog.findViewById(R.id.imgPicture);
                    imgpic.setImageResource(R.drawable.ic_photo_new);
                    imgbtnUpload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            postTit = edtPostTitle.getText().toString();
                            Intent intent = new Intent();
                            // Show only images, no videos or anything else
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            // Always show the chooser (if there are multiple options available)
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

                        }
                    });

                    btnSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(pictureSrc != null){
                                Toast.makeText(MainActivity.this,pictureSrc, Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(MainActivity.this, edtPostTitle.getText(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    builder.setView(dialog);
                    dia = builder.create();
                    dia.show();


                }
                else{
                    Toast.makeText(MainActivity.this, "Xin mời Đăng Nhập để Viết bài", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent);
                }

            }
        });


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in


                } else {
                    accessToken = null;
                    Toast.makeText(MainActivity.this, "Đã Đăng Xuất", Toast.LENGTH_SHORT).show();
                }
                // ...
            }
        };




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = LayoutInflater.from(this).inflate(R.layout.nav_header_main, null);
        navigationView.addHeaderView(header);
        txtName = (TextView) header.findViewById(R.id.txtName);
        txtEmail = (TextView) header.findViewById(R.id.txtEmail);
        imgAvatar = (ImageView) header.findViewById(R.id.imgAvatar);


        //PULL to Refresh Layout

        final PullRefreshLayout layout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        // listen refresh event
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(MainActivity.this, "đang tải...", Toast.LENGTH_SHORT).show();

                Ion.with(getApplicationContext())
                        .load("https://android-haivl-zskyfall.c9users.io")
                        .asJsonArray()
                        .setCallback(new FutureCallback<JsonArray>() {
                            @Override
                            public void onCompleted(Exception e, JsonArray result) {

                                if(e == null){
                                    for(int i =0 ;i<result.size();i++){
                                        JsonObject obj = result.get(i).getAsJsonObject();
                                        String id = obj.get("id").getAsString();
                                        String title = obj.get("title").getAsString();
                                        String time = obj.get("time").getAsString();
                                        String url = obj.get("image_url").getAsString();
                                        String author_avatar = obj.get("author_avatar").getAsString();
                                        String author_name = obj.get("author_name").getAsString();
                                        String author_id = obj.get("author_id").getAsString();

                                        int like = obj.get("like").getAsInt();
                                        int love = obj.get("love").getAsInt();
                                        int dislike = obj.get("dislike").getAsInt();

                                        posts.add(0,new Post(id,title,time,url,author_avatar,author_name,author_id,like,love,dislike));
                                        postAdapters[0]= new PostAdapter(posts,getApplicationContext());
                                        rvPosts.setAdapter(postAdapters[0]);
                                        layout.setRefreshing(false);
                                    }
                                }
                                else{
                                    Toast.makeText(MainActivity.this, "Xảy ra lỗi! Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                                    layout.setRefreshing(false);
                                }

                            }
                        });

              //  posts.add(0,new Post("Bai viet 5","5","Tac gia 5","26/01/2017","http://tophinhanhdep.net/wp-content/uploads/2015/12/anh-dep-mua-xuan-5.jpg",113,6349,70));


            }
        });

        // refresh complete
        layout.setRefreshing(false);

        //FACEBOOK LOGIN TOKEN
        if(accessToken != null){
            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {

                            String name,id,gender,cover,picture,location,email;
                            int age_max,age_min;
                            JSONObject objLocation,objCover,objPicture,objAge_range;

                            try {
                                name = object.getString("name").toString();
                                email = object.getString("email").toString();
                                id = object.getString("id").toString();
                                gender = object.getString("gender").toString();
                                objCover = object.getJSONObject("cover");
                                cover = objCover.getString("source");
                                objPicture = object.getJSONObject("picture");
                                objPicture = objPicture.getJSONObject("data");
                                picture = objPicture.getString("url");
                             /*



                                objLocation = object.getJSONObject("location");

                                objAge_range = object.getJSONObject("age_range");


                                location = objLocation.getString("name");
                               */
                                Glide.with(getApplicationContext()).load(picture).into(imgAvatar);

                                txtName.setText(name);
                                txtEmail.setText(email);


                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender,birthday,location,locale,link,picture,cover");
            request.setParameters(parameters);
            request.executeAsync();
        }






    }

    public static String getRealPathFromDocumentUri(Context context, Uri uri){
        String filePath = "";

        Pattern p = Pattern.compile("(\\d+)$");
        Matcher m = p.matcher(uri.toString());
        if (!m.find()) {
            return filePath;
        }
        String imgId = m.group();

        String[] column = { MediaStore.Images.Media.DATA };
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ imgId }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();

        return filePath;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            final String picturePath =  getRealPathFromDocumentUri(getApplicationContext(),uri);
            pictureSrc = picturePath;
            /*
            String[] projection = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
            cursor.moveToFirst();


            int columnIndex = cursor.getColumnIndex(projection[0]);
            String picturePath = cursor.getString(columnIndex); // returns null

            if(picturePath == null){
                Toast.makeText(this, cursor.toString(), Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, picturePath, Toast.LENGTH_SHORT).show();
            }
            cursor.close();
             */


            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                View dialog = getLayoutInflater().inflate(R.layout.dialog_post,null);
                final ImageView imgPicture = (ImageView) dialog.findViewById(R.id.imgPicture);
                imgPicture.setImageBitmap(bitmap);
                Button btnSubmit = (Button) dialog.findViewById(R.id.btnPostSubmit);
                final EditText edtTit = (EditText) dialog.findViewById(R.id.edtPostTitle);
                edtTit.setText(postTit);
                ImageButton imgbtnUpload = (ImageButton) dialog.findViewById(R.id.imgbtnUpload);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                imgbtnUpload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        // Show only images, no videos or anything else
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        // Always show the chooser (if there are multiple options available)
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

                    }
                });

                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(edtTit.getText().toString().equalsIgnoreCase("")){
                            Toast.makeText(MainActivity.this, "Vui lòng nhập Tiêu Đề", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            Bitmap image = ((BitmapDrawable) imgPicture.getDrawable()).getBitmap();
                            image.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
                            String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(),Base64.DEFAULT);

                            String url = "https://android-haivl-zskyfall.c9users.io/user/upload";
                            url = "http://nimkid.com/user/upload";
                            /*
                            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                            // POST parameters
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("picture",encodedImage);
                            params.put("title",edtTit.getText().toString());

                            JSONObject jsonObj = new JSONObject(params);
                            */

                            Ion.with(getApplicationContext())
                                    .load("POST","http://nimkid.com/user/upload")
                                    .setTimeout(60*60*1000)
                                    .setBodyParameter("picture",encodedImage)
                                    .setBodyParameter("title",edtTit.getText().toString())
                                    .asString()
                                    .setCallback(new FutureCallback<String>() {
                                        @Override
                                        public void onCompleted(Exception e, String result) {
                                            if(e == null){
                                                if(result.equalsIgnoreCase("ok")){
                                                    Toast.makeText(MainActivity.this, "Thành công!~", Toast.LENGTH_SHORT).show();
                                                }
                                                else{
                                                    Toast.makeText(MainActivity.this, result.toString(), Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                            else{
                                                Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });





                        }
                    }
                });
                builder.setView(dialog);
                dia.hide();
                AlertDialog dial = builder.create();
                dial.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Intent intent = new Intent(MainActivity.this,AuthorActivity.class);
            startActivity(intent);
            //return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {
            Toast.makeText(this, "shared", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            txtName.setText("");
            txtEmail.setText("");
            imgAvatar.setImageResource(R.mipmap.ic_launcher);


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {

        Toast.makeText(this, v.getId()+"", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


}
