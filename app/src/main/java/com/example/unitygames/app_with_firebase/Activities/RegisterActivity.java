package com.example.unitygames.app_with_firebase.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.unitygames.app_with_firebase.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    private ImageView ImgUserPhoto;
    private EditText mUserName,mEmail,mPassword,mCpassword;
    private ProgressBar mprogressbar;
    private Button mRegbtn,mLogin;
    private FirebaseAuth mAuth;
    static int PreqCode = 1;
    int REQCODE = 1;
    private Uri picImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //ini Views
        ImgUserPhoto = (ImageView) findViewById(R.id.regUserphoto);
        mUserName =(EditText)findViewById(R.id.regName);
        mEmail =(EditText)findViewById(R.id.regMail);
        mPassword=(EditText)findViewById(R.id.regPassword);
        mCpassword=(EditText)findViewById(R.id.regPassword2);
        mprogressbar =(ProgressBar)findViewById(R.id.progressBar);
        mRegbtn =(Button)findViewById(R.id.regBtn);
        mLogin=(Button)findViewById(R.id.regto_login);

       mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLogin.setVisibility(View.INVISIBLE);
                mprogressbar.setVisibility(View.VISIBLE);
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mprogressbar.setVisibility(View.INVISIBLE);


        mRegbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRegbtn.setVisibility(View.INVISIBLE);
                mprogressbar.setVisibility(View.VISIBLE);
                final String name      =  mUserName.getText().toString();
                final String email     =  mEmail.getText().toString();
                final String password  =  mPassword.getText().toString();
                final String password2 =  mCpassword.getText().toString();

                //validations

                if (email.isEmpty() || name.isEmpty() ||  password.isEmpty() || password2.isEmpty() || !password.equals(password2)){

                    showMessage("Please Verify All Fields");
                    mRegbtn.setVisibility(View.VISIBLE);
                    mprogressbar.setVisibility(View.INVISIBLE);

                } else if (picImageUri == null){
                    showMessage("PLease Select Image");
                    mRegbtn.setVisibility(View.VISIBLE);

                }
                else if (mEmail.getText() != null && mUserName.getText() != null && mPassword != null && mCpassword != null){

                    //Create Accounts
                    CreateUserAccount(name,email,password);

                } else {
                        //CreateUserAccount(name,email,password);
                        showMessage("Verify All Details");
                }
            }
        });

        ImgUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= 22) {
                    checkAndrequestforPermission();
                }
                else {
                    openGallary();
                }
            }
        });
    }
        //It Will Create Firebase Account For User

    private void CreateUserAccount(final String name, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            showMessage("Account Created");

                            UpdateUserInfo(name,picImageUri,mAuth.getCurrentUser());
                        }
                        else {

                            showMessage("Failed Try Again !!!!" +task.getException().getMessage());
                            mRegbtn.setVisibility(View.VISIBLE);
                            mprogressbar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    private void UpdateUserInfo(final String name, final Uri picImageUri, final FirebaseUser currentUser) {

        final StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
        final StorageReference imageFilePath = mStorage.child(picImageUri.getLastPathSegment());
        imageFilePath.putFile(picImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        UserProfileChangeRequest profileupdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();

                        currentUser.updateProfile(profileupdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            showMessage("Register Complete");
                                            updateUI();
                                        }
                                    }
                                });
                    }
                });
            }
        });

    }

    private void updateUI() {
        Intent homeIntent = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(homeIntent);
        finish();
    }

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    private void openGallary() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQCODE);
        }
    }


    private void checkAndrequestforPermission() {

        if (ContextCompat.checkSelfPermission(RegisterActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(RegisterActivity.this, "Please Accept for required Permission", Toast.LENGTH_SHORT).show();
            } else {

                ActivityCompat.requestPermissions(RegisterActivity.this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PreqCode);

            }
        }
            else {
                    openGallary();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && resultCode == Activity.RESULT_OK){
            //User Can SuccessFully Uplaod an Image
            picImageUri = data.getData();
            ImgUserPhoto.setImageURI(picImageUri);
        }

    }
}