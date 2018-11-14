package com.example.saimouni.bottomnavigationbar;

import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EnrolledStudentsActivity extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener{

    String title;
    ListView listView;
    private FirebaseListAdapter<EnrolledStudents> adapter;
    private DatabaseReference databaseReference;
    private NetworkStateReceiver networkStateReceiver;
    private Snackbar snackbar;
    private ConstraintLayout enrolledStudentsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrolled_students);
        title=getIntent().getStringExtra("title");

        listView=(ListView)findViewById(R.id.listView);
        enrolledStudentsLayout=findViewById(R.id.enrolledStudentsLayout);

        databaseReference= FirebaseDatabase.getInstance().getReference().child("Enrolled").child(title+"");

        final ProgressDialog dialog=new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.show();

        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
        snackbar = Snackbar.make(enrolledStudentsLayout,"No internet connection",Snackbar.LENGTH_LONG);


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dialog.cancel();
                if(dataSnapshot.getChildrenCount()==0)
                    Toast.makeText(getApplicationContext(),"0 Students found",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //    databaseReference.addValueEventListener(valueEventListener);

        adapter=new FirebaseListAdapter<EnrolledStudents>(this,
                EnrolledStudents.class,
                R.layout.enrolled_students_model,
                databaseReference
        ) {
            @Override
            protected void populateView(View v, EnrolledStudents model, int position) {

                Log.d("Position",position+"");
                TextView tvName=(TextView)v.findViewById(R.id.studName);
                tvName.setText(model.getName());
                TextView tvHtno=(TextView)v.findViewById(R.id.studHtNo);
                tvHtno.setText(model.getHtNo());

                TextView tvBranch=(TextView)v.findViewById(R.id.studBranch);
                tvBranch.setText(model.getBranch());

                TextView tvSection=(TextView)v.findViewById(R.id.studSection);
                tvSection.setText(model.getSection());

                TextView tvCount=(TextView)v.findViewById(R.id.studentCount);
                tvCount.setText((position+1)+".");





            }
        };
        listView.setAdapter(adapter);


    }

    @Override
    public void networkAvailable() {
        snackbar.dismiss();
    }

    @Override
    public void networkUnavailable() {
        snackbar.show();
    }
}
