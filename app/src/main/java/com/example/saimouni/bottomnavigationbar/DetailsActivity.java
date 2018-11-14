package com.example.saimouni.bottomnavigationbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,NetworkStateReceiver.NetworkStateReceiverListener{

    DatabaseReference databaseReference;

    private NetworkStateReceiver networkStateReceiver;
    private Snackbar snackbar;


    String departments[]={"CSE","ECE","EEE","MECH","CIVIL"};
    String sections[]={"A","B","C","D"};

    ArrayAdapter<String> branchAdapter;
    ArrayAdapter<String> sectionAdapter;


    EditText name,htno;
    Button submitBtn;
    //enrollmentsBtn;
    String title,desc,image,date,studname,rollno,branch,section;
    ProgressDialog progressDialog;
    Spinner branchSpinner,sectionSpinner;

    private RelativeLayout detailsActivityLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        name=(EditText)findViewById(R.id.name);
        htno=(EditText)findViewById(R.id.htno);
        submitBtn=(Button)findViewById(R.id.submitBtn);

        title=getIntent().getStringExtra("title");
        desc = getIntent().getStringExtra("desc");
        image = getIntent().getStringExtra("image");
        date = getIntent().getStringExtra("date");


        branchSpinner=(Spinner)findViewById(R.id.branch);
        sectionSpinner=(Spinner)findViewById(R.id.section);
        detailsActivityLayout=findViewById(R.id.detailsActivityLayout);

        branchAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,departments);
        sectionAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,sections);



        sectionSpinner.setAdapter(sectionAdapter);
        branchSpinner.setAdapter(branchAdapter);
        sectionSpinner.setOnItemSelectedListener(this);
        branchSpinner.setOnItemSelectedListener(this);

        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
        snackbar = Snackbar.make(detailsActivityLayout,"No internet connection",Snackbar.LENGTH_LONG);





        databaseReference= FirebaseDatabase.getInstance().getReference().child("Enrolled");


        Log.d("SAINATH",title+"");

        progressDialog=new ProgressDialog(this);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadDetails();
            }
        });



    }


    private void uploadDetails() {

        final String mName=name.getText().toString().trim().toUpperCase();
        final String mHtNo=String.valueOf(htno.getText()).toUpperCase();


        Log.d("SAINATH",mHtNo);

        if(TextUtils.isEmpty(mName))
            Toast.makeText(this,"Plz enter ur name",Toast.LENGTH_SHORT).show();
        if(TextUtils.isEmpty(mHtNo))
            Toast.makeText(this,"Plz enter ur HtNo",Toast.LENGTH_SHORT).show();

        if(!TextUtils.isEmpty(mName) && !TextUtils.isEmpty(mHtNo)){


            final DatabaseReference student=databaseReference.child(title+"").child(mHtNo);




            ValueEventListener valueEventListener=new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    progressDialog.setMessage("Uploading...");
                    progressDialog.show();
                    if(!dataSnapshot.exists()){
                        //if student is not already enrolled

                        student.child("name").setValue(mName+"");
                        student.child("htNo").setValue(mHtNo);
                        student.child("branch").setValue(branch+"");
                        student.child("section").setValue(section);

                        final ProgressDialog pd = new ProgressDialog(DetailsActivity.this);
                        pd.setIndeterminate(true);
                        pd.setCancelable(false);
                        pd.setMessage("enrolling");

                        //to hide keyboard
                        // Check if no view has focus:
                        View view = DetailsActivity.this.getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }

                        Snackbar.make(detailsActivityLayout,"Enrollment Success",Snackbar.LENGTH_SHORT).show();
                        pd.show();


                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                 Intent intent = new Intent(DetailsActivity.this,MyEnrollmentsClickActivity.class);
                                 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                 intent.putExtra("title",title);
                                 intent.putExtra("htno",mHtNo);
                                 pd.cancel();

                                 startActivity(intent);
                            }
                        },2000);

                    }
                    else{
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                        v.vibrate(300);

                        Snackbar.make(detailsActivityLayout,"Already enrolled",Snackbar.LENGTH_LONG).show();
                        htno.setBackgroundResource(R.drawable.red_border);

                    }


                    progressDialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            student.addListenerForSingleValueEvent(valueEventListener);

        }

    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        TextView tv=   (TextView)view;
        Spinner spinner = (Spinner) parent;
        if(spinner.getId() == R.id.section)
        {
            section=tv.getText().toString();
        }
        else if(spinner.getId() == R.id.branch)
        {
            branch=tv.getText().toString();
        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
