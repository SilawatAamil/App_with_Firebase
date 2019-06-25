package com.example.unitygames.app_with_firebase.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.unitygames.app_with_firebase.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText mLogEmail,mLogPassword;
    private Button mbtn_login,mbtn_reg;
    private ProgressBar mLogProgressbar;
    private FirebaseAuth mAuth;
    private ImageView mloginImg;
    private Intent HomeActivity;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLogEmail = (EditText)findViewById(R.id.loginEmail);
        mLogPassword=(EditText)findViewById(R.id.loginPassword);
        mLogProgressbar=(ProgressBar)findViewById(R.id.loginProgressBar);
        mbtn_login=(Button)findViewById(R.id.login_btn);
        mbtn_reg=(Button)findViewById(R.id.reg);
        mloginImg=(ImageView)findViewById(R.id.loginPhoto);

        mbtn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLogProgressbar.setVisibility(View.VISIBLE);
                mbtn_reg.setVisibility(View.INVISIBLE);
                Intent regActivity = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(regActivity);
                finish();
            }
        });

        mloginImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerActivity = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(registerActivity);
                finish();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        HomeActivity = new Intent(this, Home.class);


        mLogProgressbar.setVisibility(View.INVISIBLE);

        mbtn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLogProgressbar.setVisibility(View.VISIBLE);
                mbtn_login.setVisibility(View.INVISIBLE);
                mAuth = FirebaseAuth.getInstance();
                final String mail     =  mLogEmail.getText().toString();
                final String password =  mLogPassword.getText().toString();

                if (mail.isEmpty() || password.isEmpty()){

                    showMessage("Please Verify All Details");
                    mbtn_login.setVisibility(View.VISIBLE);
                    mLogProgressbar.setVisibility(View.INVISIBLE);
                }
                else{

                    signIn(mail,password);

                    mbtn_login.setVisibility(View.INVISIBLE);
                    mLogProgressbar.setVisibility(View.VISIBLE);
                }
            }
        });


    }

    private void signIn(String mail, String password) {
        mAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()){

                        mLogProgressbar.setVisibility(View.INVISIBLE);
                        mbtn_login.setVisibility(View.VISIBLE);
                        updateUI();

                    }
                    else {
                        showMessage(task.getException().getMessage());
                        mbtn_login.setVisibility(View.VISIBLE);
                    }
            }
        });

    }

    private void updateUI() {

        startActivity(HomeActivity);
        finish();

    }

    private void showMessage(String text) {

        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            updateUI();
        }
    }
}
