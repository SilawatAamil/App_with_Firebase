package com.example.unitygames.app_with_firebase.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.unitygames.app_with_firebase.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import Fragments.HomeFragment;
import Fragments.ProfileFragment;
import Fragments.SettingFragment;
import Models.Post;

import static com.example.unitygames.app_with_firebase.Activities.RegisterActivity.PreqCode;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    Dialog popAddPost;
    ImageView mPopupImg,mPopUserImg,mPopupAdd;
    TextView mPopupTitle,mPopupDescription;
    ProgressBar mPopupPrgbar;
    public static final int PreqCode=2;
    public static final int REQCODE=2;
    private Uri picImageUri = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

            //Intialize Mathods

        iniPopup();
        setUpopAddImage();

        // Floating Button

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               popAddPost.show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        updateNavHeader();

        //Set Home Fragment As Default

        getSupportFragmentManager().beginTransaction().replace(R.id.container,new HomeFragment()).commit();

    }

        //  Add Pop Image

    private void setUpopAddImage() {

        mPopupImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkAndrequestforPermission();

            }
        });
    }
            // Checking Request Permissions

    private void checkAndrequestforPermission() {

        if (ContextCompat.checkSelfPermission(Home.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(Home.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(Home.this, "Please Accept for required Permission", Toast.LENGTH_SHORT).show();
            } else {

                ActivityCompat.requestPermissions(Home.this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PreqCode);

            }
        }
        else {
            openGallary();
        }
    }
            // User can add Post Image

    private void openGallary() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQCODE);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && resultCode == Activity.RESULT_OK){
            //User Can SuccessFully Uplaod an Image
            picImageUri = data.getData();
            mPopupImg.setImageURI(picImageUri);
        }

    }
            // Initialize PopUp Dialog

    private void iniPopup() {
        popAddPost= new Dialog(this);
        popAddPost.setContentView(R.layout.popup_add_post);
        popAddPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        popAddPost.getWindow().getAttributes().gravity= Gravity.TOP;

        mPopUserImg= popAddPost.findViewById(R.id.popup_user_image);
        mPopupImg= popAddPost.findViewById(R.id.popup_img);
        mPopupAdd=popAddPost.findViewById(R.id.popup_add);
        mPopupTitle=popAddPost.findViewById(R.id.popup_title);
        mPopupDescription=popAddPost.findViewById(R.id.popup_description);
        mPopupPrgbar=popAddPost.findViewById(R.id.popup_progressBar);


        Glide.with(Home.this).load(currentUser.getPhotoUrl()).into(mPopUserImg);

            // Add Post Using Title description And Image


        mPopupAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupAdd.setVisibility(View.INVISIBLE);
                mPopupPrgbar.setVisibility(View.VISIBLE);

                    if (!mPopupTitle.getText().toString().isEmpty()
                            &&  !mPopupDescription.getText().toString().isEmpty()
                            && picImageUri != null){

                        StorageReference mStorgaeRef = FirebaseStorage.getInstance().getReference().child("blog_images");
                        final StorageReference mFilePath= mStorgaeRef.child(picImageUri.getLastPathSegment());
                        mFilePath.putFile(picImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                mFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imgDwnldLnk = uri.toString();

                                        //  Create Post

                                         Post post = new Post(mPopupTitle.getText().toString()
                                                 ,mPopupDescription.getText().toString(),
                                                 imgDwnldLnk,currentUser.getUid(),currentUser.getPhotoUrl().toString());

                                         //Add Post To Firebase

                                         addPost(post);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        showMessage(e.getMessage());
                                        mPopupPrgbar.setVisibility(View.INVISIBLE);
                                        mPopupAdd.setVisibility(View.VISIBLE);

                                    }
                                });
                            }
                        });

                    }else {
                        showMessage("Please Verify All Field And Choose Image");
                        mPopupAdd.setVisibility(View.VISIBLE);
                        mPopupPrgbar.setVisibility(View.INVISIBLE);
                    }
            }
        });


    }

    private void addPost(Post post) {
        FirebaseDatabase mfiredatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mfiredatabase.getReference("Posts").push();

        //get Post Unique Id  and UpdatePost Key

        String Key = myRef.getKey();
        post.setPostKey(Key);

        //Add Post data to firebase database

        myRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showMessage("Post Added Successfully");
                Clear();
                mPopupPrgbar.setVisibility(View.INVISIBLE);
                mPopupAdd.setVisibility(View.VISIBLE);
                popAddPost.dismiss();
            }
        });
    }
    private void status(String status){
        DatabaseReference mRef;
        mRef= FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        HashMap<String , Object> hashMap= new HashMap<>();
        hashMap.put("status",status);
        mRef.updateChildren(hashMap);
        }

    @Override
    protected void onResume() {
        super.onResume();
        status("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("Offline");
    }

    // It Will Displays The Toast Message

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
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
        getMenuInflater().inflate(R.menu.home, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            getSupportActionBar().setTitle("Home");
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new  HomeFragment()).commit();
            // Handle the camera action
        }
        else if (id == R.id.nav_profile) {
            getSupportActionBar().setTitle("Profile");
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new ProfileFragment()).commit();


        } else if (id == R.id.nav_setting) {
            getSupportActionBar().setTitle("Settings");
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new SettingFragment()).commit();


        } else if (id == R.id.nav_lagout) {

            FirebaseAuth.getInstance().signOut();
            Intent loginActivity = new Intent(this,LoginActivity.class);
            startActivity(loginActivity);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void updateNavHeader() {

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.nav_username);
        TextView navEmail = headerView.findViewById(R.id.nav_usermail);
        ImageView navImgView = headerView.findViewById(R.id.nav_UserPic);

        navUsername.setText(currentUser.getEmail());
        navEmail.setText(currentUser.getDisplayName());

        Glide.with(this).load(currentUser.getPhotoUrl()).into(navImgView);

    }
    // It Will Clear Texts And Image Views When You Post Your Second Blog
    private void Clear(){
        mPopupTitle.setText("");
        mPopupDescription.setText("");
        mPopupImg.setImageResource(0);
    }

}
