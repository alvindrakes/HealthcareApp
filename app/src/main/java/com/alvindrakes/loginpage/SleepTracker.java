package com.alvindrakes.loginpage;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import android.view.WindowManager;
<<<<<<< HEAD

import com.alvindrakes.loginpage.ui.AlarmLayout;

import trikita.anvil.Anvil;

import trikita.anvil.RenderableView;
=======
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alvindrakes.loginpage.Login;
import com.alvindrakes.loginpage.MainActivity;
import com.alvindrakes.loginpage.ProfilePage;
import com.alvindrakes.loginpage.R;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
>>>>>>> 0c3b063654574bff6de1a2449641e783bac97a37

public class SleepTracker extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //for navigation drawer
    private DrawerLayout myDrawer;
    private ActionBarDrawerToggle myToggle;
    private NavigationView navigationView;
    
    TextView userId;
    TextView userEmail;
    
    User user;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar bar = getSupportActionBar();
        bar.setTitle("Sleep");
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#6F2D84")));
        getSupportActionBar().show();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sleep_tracker);

        myDrawer = (DrawerLayout) findViewById(R.id.myDrawer);
        myToggle = new ActionBarDrawerToggle(this, myDrawer, R.string.open, R.string.close);

        navigationView = (NavigationView) findViewById(R.id.my_navigation);
        navigationView.bringToFront();

        navigationView.setNavigationItemSelectedListener(this);

        myDrawer.addDrawerListener(myToggle);
        myToggle.syncState();
    
        View headerView = navigationView.getHeaderView(0);
        userId = (TextView) headerView.findViewById(R.id.User_ID);
        userEmail = (TextView) headerView.findViewById(R.id.User_email);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
<<<<<<< HEAD

        // updateTheme();

        setContentView(new RenderableView(this) {
            public void view() {
                    AlarmLayout.view();
                }
            });
        }


    public void onResume() {
        super.onResume();
        Anvil.render();
=======
    
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
        
>>>>>>> 0c3b063654574bff6de1a2449641e783bac97a37
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (myToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.profile_page:
                Intent i = new Intent(SleepTracker.this, ProfilePage.class);
                startActivity(i);
                break;

            case R.id.setting_page:
                Intent h = new Intent(SleepTracker.this, com.alvindrakes.loginpage.SettingPage.class);
                startActivity(h);
                break;

            case R.id.home_page:
                Intent g = new Intent(SleepTracker.this, MainActivity.class);
                startActivity(g);
                break;

            case R.id.sleep_tracker:
                Intent b = new Intent(SleepTracker.this, SleepTracker.class);
                startActivity(b);
                break;
        }

        return true;
    }
}