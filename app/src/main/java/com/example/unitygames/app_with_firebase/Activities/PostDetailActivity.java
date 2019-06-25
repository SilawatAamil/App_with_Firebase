package com.example.unitygames.app_with_firebase.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.unitygames.app_with_firebase.Adapters.CommentAdapter;
import com.example.unitygames.app_with_firebase.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import Models.Comment;


public class PostDetailActivity extends AppCompatActivity {

    ImageView mimgPost,mimgUserpost,mimgCurrentUser;
    TextView mtxtPostDesc,mtxtPostDate,mtxtPostTitle;
    EditText meditTextComment;
    Button mBtnAddComment;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseDatabase mDatabase;
    String postKey;
    RecyclerView RvComment;
    CommentAdapter commentAdapter;
    List<Comment> listComment;
    static String COMMENT_KEY = "Comment" ;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getSupportActionBar().hide();

        RvComment = findViewById(R.id.rv_comment);
        mimgPost = findViewById(R.id.post_detail_img);
        mimgUserpost= findViewById(R.id.post_detail_user_img);
        mimgCurrentUser=findViewById(R.id.post_detail_currentuser_img);

        mtxtPostTitle = findViewById(R.id.post_detail_title);
        mtxtPostDate = findViewById(R.id.post_detail_date_name);
        mtxtPostDesc=findViewById(R.id.post_detail_desc);

        meditTextComment=findViewById(R.id.post_detail_comment);
        mBtnAddComment=findViewById(R.id.post_detail_add_comment_btn);

        mAuth = FirebaseAuth.getInstance();
        mUser= mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();

        // Add Comment Listener

        mBtnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    DatabaseReference mDatabaseRef = mDatabase.getReference(COMMENT_KEY).child(postKey).push();
                    String Comment_content = meditTextComment.getText().toString();
                    String uid = mUser.getUid();
                    String uname = mUser.getDisplayName();
                    String uimg = mUser.getPhotoUrl().toString();
                    Comment comment = new Comment(Comment_content, uid, uimg, uname);

                    mDatabaseRef.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            showMessage("comment added");
                            meditTextComment.setText("");
                            mBtnAddComment.setVisibility(View.VISIBLE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showMessage("fail to add comment : " + e.getMessage());
                        }
                    });
                }

        });


        // Now we need to Bind  all data into those Views

        // First We need to get Post data
        String postImage = getIntent().getExtras().getString("postImage");
        Glide.with(this).load(postImage).into(mimgPost);

        String postTitle = getIntent().getExtras().getString("title");
        mtxtPostTitle.setText(postTitle);

        String userPostImage= getIntent().getExtras().getString("userPhoto");
        Glide.with(this).load(userPostImage).into(mimgUserpost);

        String postDecs = getIntent().getExtras().getString("description");
        mtxtPostDesc.setText(postDecs);

        Glide.with(this).load(mUser.getPhotoUrl()).into(mimgCurrentUser);

        postKey = getIntent().getExtras().getString("postKey");

        String date = timeToStamp(getIntent().getExtras().getLong("postDate"));
        mtxtPostDate.setText(date);

        //intialize RV comment
        iniRvComment();

    }

    private void iniRvComment() {
        RvComment.setLayoutManager(new LinearLayoutManager(this));


        DatabaseReference commentRef = mDatabase.getReference(COMMENT_KEY).child(postKey);
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listComment = new ArrayList<>();
                for (DataSnapshot snap:dataSnapshot.getChildren()) {
                    Comment comment = snap.getValue(Comment.class);
                    listComment.add(comment) ;
                }

                commentAdapter = new CommentAdapter(getApplicationContext(),listComment);
                RvComment.setAdapter(commentAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private String timeToStamp(long time){
            Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
            calendar.setTimeInMillis(time);
            String date = android.text.format.DateFormat.format("dd-MM-yyyy",calendar).toString();
            return date;
        }
    private void showMessage(String message) {

        Toast.makeText(this,message,Toast.LENGTH_LONG).show();

    }

}
