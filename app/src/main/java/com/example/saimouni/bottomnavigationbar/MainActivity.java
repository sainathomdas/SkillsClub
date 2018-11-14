package com.example.saimouni.bottomnavigationbar;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener,MeFragment.OnFragmentInteractionListener,MyEnrollmentsFragment.OnFragmentInteractionListener {
    Fragment fragment;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;



    public BottomNavigationView navigation;
    ImageView dp;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {


            switch (item.getItemId()) {
                case R.id.navigation_home:

                    fragment = new HomeFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.menuEnrollments:
                    fragment=new MyEnrollmentsFragment();
                    loadFragment(fragment);

                    return true;
                case R.id.menuMe:
                    fragment = new MeFragment();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  mTextMessage = (TextView) findViewById(R.id.message);
         navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mAuth=FirebaseAuth.getInstance();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(mAuth.getCurrentUser()==null){
            Intent intent=new Intent(MainActivity.this,AdminAndStudentLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        }

        if(mAuth.getCurrentUser()!=null) {

            fragment = new HomeFragment();
            loadFragment(fragment);
        }



        invalidateOptionsMenu();

      /*    actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
       LayoutInflater inflator = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.custom_imag_view, null);
        actionBar.setCustomView(v);
*/


    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.icon_menu,menu);
         MenuItem addPost = menu.findItem(R.id.addPost);

if(mAuth.getCurrentUser()!=null) {
    if (mAuth.getCurrentUser().getProviders().get(0).equals("google.com")) {
        addPost.setVisible(false);
    } else
        Toast.makeText(this, "Long press on event to delete it", Toast.LENGTH_LONG).show();
}
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.infoBtn){
            Intent intent = new Intent(MainActivity.this,AboutUsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        if(item.getItemId() == R.id.addPost){
            Intent intent = new Intent(MainActivity.this,AddPostActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void setNavigationVisibility(boolean visible) {
        if (navigation.isShown() && !visible) {
            navigation.setVisibility(View.GONE);
        }
        else if (!navigation.isShown() && visible){
            navigation.setVisibility(View.VISIBLE);
        }
    }
}
