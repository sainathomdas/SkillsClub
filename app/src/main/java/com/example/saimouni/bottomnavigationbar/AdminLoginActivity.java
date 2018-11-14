package com.example.saimouni.bottomnavigationbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminLoginActivity extends AppCompatActivity {


    private EditText usernameET,passwordET;
    private FirebaseAuth mAuth;
    private String username,password;
    private ProgressDialog progressDialog;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        usernameET = findViewById(R.id.userNameET);
        passwordET = findViewById(R.id.passwordET);
        login = findViewById(R.id.loginBtn);

        mAuth = FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                username = usernameET.getText().toString().toLowerCase().trim();
                password = passwordET.getText().toString().toLowerCase().trim();
                if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)){


                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AdminLoginActivity.this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("adminUserName",username);
                    editor.apply();
                    signIn();
                }
            }
        });
        
    }

    private void signIn() {
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            progressDialog.dismiss();
                            Intent mainIntent=new Intent(AdminLoginActivity.this,MainActivity.class);
                            /*sainath*/ mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(mainIntent);

                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            // If sign in fails, display a message to the user.
                            Log.w("Sainath", "signInWithCredential:failure", task.getException());
                            Toast.makeText(AdminLoginActivity.this,"Sign in Failed",Toast.LENGTH_LONG).show();

                        }

                        // ...
                    }
                });
    }
}
