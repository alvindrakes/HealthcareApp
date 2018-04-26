package com.alvindrakes.loginpage;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingPage extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {
  
  Button signOutBtn;
  
  //for navigation drawer
  private DrawerLayout myDrawer;
  private ActionBarDrawerToggle myToggle;
  private NavigationView navigationView;
  TextView userId;
  TextView userEmail;
  
  //Firebase variables
  User user;
  FirebaseUser firebaseUser;
  GoogleSignInClient googleSignInClient;
  
  @Override
  protected void onCreate (Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ActionBar bar = getSupportActionBar();
    bar.setTitle("Settings");
    bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#6F2D84")));
    getSupportActionBar().show();
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                         WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.activity_setting_page);
    
    //Initialize Side pane
    myDrawer = (DrawerLayout) findViewById(R.id.myDrawer);
    myToggle = new ActionBarDrawerToggle(this, myDrawer, R.string.open, R.string.close);
    navigationView = (NavigationView) findViewById(R.id.my_navigation);
    navigationView.bringToFront();
    navigationView.setNavigationItemSelectedListener(this);
    myDrawer.addDrawerListener(myToggle);
    myToggle.syncState();
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    View headerView = navigationView.getHeaderView(0);
    userId = (TextView) headerView.findViewById(R.id.User_ID);
    userEmail = (TextView) headerView.findViewById(R.id.User_email);
    
    //Initialize variables
    signOutBtn = (Button) findViewById(R.id.signOutBtn);
    
    //===============Data Retrieval from firebase ==============
    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN);
    FirebaseDatabase.getInstance()
        .getReference()
        .child("users")
        .child(firebaseUser.getUid())
        .addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange (DataSnapshot dataSnapshot) {
            user = dataSnapshot.getValue(User.class);
            userId.setText(user.getName());
            userEmail.setText(user.getEmail());
          }
          
          @Override
          public void onCancelled (DatabaseError databaseError) {
          
          }
        });
    //===============End of data Retrieval from firebase ==============
    
    //Signout from user's account
    signOutBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick (View v) {
        
        //Sign out from firebase and/or google
        if (googleSignInClient != null) googleSignInClient.signOut();
        FirebaseAuth.getInstance().signOut();
        
        Toast.makeText(SettingPage.this, "Log out successfully", Toast.LENGTH_SHORT).show();
        //Transition back to login page
        Intent startIntent = new Intent(SettingPage.this, Login.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startIntent);
      }
    });
    
  }
  
  //Side Pane
  @Override
  public boolean onOptionsItemSelected (MenuItem item) {
    
    if (myToggle.onOptionsItemSelected(item)) {
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
  
  //Side pane navigation
  public boolean onNavigationItemSelected (MenuItem item) {
    int id = item.getItemId();
    
    switch (id) {
      case R.id.profile_page:
        Intent i = new Intent(SettingPage.this, ProfilePage.class);
        startActivity(i);
        break;
      
      case R.id.setting_page:
        Intent h = new Intent(SettingPage.this, SettingPage.class);
        startActivity(h);
        break;
      
      case R.id.home_page:
        Intent g = new Intent(SettingPage.this, MainActivity.class);
        startActivity(g);
        break;
      
      case R.id.sleep_tracker:
        Intent b = new Intent(SettingPage.this, SleepTracker.class);
        startActivity(b);
        break;
      
      case R.id.caffeine_tracker:
        Intent a = new Intent(SettingPage.this, CaffeineTracker.class);
        startActivity(a);
        break;
    }
    
    return true;
  }
  
  @Override
  protected void onDestroy () {
    super.onDestroy();
    user = null;
  }
}
