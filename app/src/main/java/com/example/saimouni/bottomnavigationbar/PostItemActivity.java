package com.example.saimouni.bottomnavigationbar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class PostItemActivity extends AppCompatActivity {

    String titleIntent,descIntent,imageIntent,dateIntent;

    private Button enrollBtn,enrolledStudents;
    private ImageView postImage;
    public TextView postTitle,postDesc,postDate;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_item);

        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            titleIntent=extras.getString("titleIntent");
            descIntent=extras.getString("descIntent");
            imageIntent=extras.getString("imageIntent");
            dateIntent=extras.getString("dateIntent");
        }



        enrollBtn=(Button)findViewById(R.id.enrollBtn);
        postDesc=(TextView)findViewById(R.id.postDesc);
        postImage=(ImageView)findViewById(R.id.postImage);
        postTitle=(TextView)findViewById(R.id.postTitle);
        postDate=findViewById(R.id.postDate);
        mAuth = FirebaseAuth.getInstance();

        enrolledStudents=(Button)findViewById(R.id.enrolledStudents);


        Picasso.get().load(imageIntent+"").into(postImage);
        postTitle.setText(titleIntent+"");
        postDesc.setText(descIntent+"\n\n\n");
        postDate.setText("on: "+dateIntent);

        enrollBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PostItemActivity.this,DetailsActivity.class);
                intent.putExtra("title", titleIntent+"");
                intent.putExtra("desc",descIntent);
                intent.putExtra("image",imageIntent);
                intent.putExtra("date",dateIntent);
                startActivity(intent);

            }
        });

        enrolledStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEnrollments(titleIntent);
            }
        });



    }
    private void showEnrollments(String title) {

        Intent enrollmentsIntent=new Intent(PostItemActivity.this,EnrolledStudentsActivity.class);
        enrollmentsIntent.putExtra("title",title+"");
        startActivity(enrollmentsIntent);

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(PostItemActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
