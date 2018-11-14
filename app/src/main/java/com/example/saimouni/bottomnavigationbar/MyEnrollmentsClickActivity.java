package com.example.saimouni.bottomnavigationbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MyEnrollmentsClickActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView titleTV,descTV,dateTV;
    private Button disenrollBtn,enrolledStudentsBtn;
    private DatabaseReference databaseReference;
    private String title,date,desc,imageLink,htno;
    private AlertDialog alertDialog;
    private AlertDialog.Builder alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_enrollments_click);

        imageView = findViewById(R.id.myEnrollmentsClickImage);
        titleTV = findViewById(R.id.myEnrollmentsClickTitle);
        descTV = findViewById(R.id.myEnrollmentsClickDesc);
        dateTV = findViewById(R.id.myEnrollmentsClickDate);
        disenrollBtn = findViewById(R.id.disEnrollBtn);
        enrolledStudentsBtn = findViewById(R.id.myEnrollmentsClickEnrolledStudentsBtn);

        alert=new AlertDialog.Builder(MyEnrollmentsClickActivity.this);
        alert.setMessage("You will be no longer registered in this event");
        alert.setTitle("Confirm ?");
        alertDialog=alert.create();

        title = getIntent().getStringExtra("title");
        htno = getIntent().getStringExtra("htno");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Blog").child(title);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    desc = dataSnapshot.child("desc").getValue()+"";
                    date = dataSnapshot.child("date").getValue()+"";
                    imageLink  = dataSnapshot.child("image").getValue()+"";

                titleTV.setText(title);
                descTV.setText(desc+"\n\n\n");
                dateTV.setText(date);
                Picasso.get().load(imageLink).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        enrolledStudentsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyEnrollmentsClickActivity.this,EnrolledStudentsActivity.class);
                intent.putExtra("title",title);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        disenrollBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        DatabaseReference  dr = FirebaseDatabase.getInstance().getReference().child("Enrolled").child(title);
                        //dr.getRef().child(htno).removeValue();

                       dr.child(htno).removeValue();
                        final ProgressDialog progressDialog =new ProgressDialog(MyEnrollmentsClickActivity.this);
                        progressDialog.setMessage("Please wait...");
                        progressDialog.show();

                        Snackbar.make(findViewById(R.id.MyEnrollmentsClickActivityLayout),"DisEnrollment Success",Snackbar.LENGTH_SHORT).show();

                       new Handler().postDelayed(new Runnable() {
                           @Override
                           public void run() {

                               Intent intent = new Intent(MyEnrollmentsClickActivity.this,PostItemActivity.class);
                               intent.putExtra("titleIntent",title);
                               intent.putExtra("descIntent",desc);
                               intent.putExtra("imageIntent",imageLink);
                               intent.putExtra("dateIntent",date);
                               intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                               progressDialog.cancel();
                               startActivity(intent);
                           }
                       },2000);



                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                alert.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MyEnrollmentsClickActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
