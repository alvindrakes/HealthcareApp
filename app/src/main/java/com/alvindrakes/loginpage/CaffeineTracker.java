package com.alvindrakes.loginpage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class CaffeineTracker extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //for navigation drawer
    private DrawerLayout myDrawer;
    private ActionBarDrawerToggle myToggle;
    private NavigationView navigationView;

    TextView showValue;
    Button increaseButton;
    Button decreaseButton;

    StatisticData dataValue;
    FirebaseUser firebaseUser;
    
    String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar bar = getSupportActionBar();
        bar.setTitle("Caffeine Tracker");
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#6F2D84")));
        getSupportActionBar().show();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_caffein_tracker);

        myDrawer = (DrawerLayout) findViewById(R.id.myDrawer);
        myToggle = new ActionBarDrawerToggle(this, myDrawer, R.string.open, R.string.close);

        navigationView = (NavigationView) findViewById(R.id.my_navigation);
        navigationView.bringToFront();

        navigationView.setNavigationItemSelectedListener(this);

        myDrawer.addDrawerListener(myToggle);
        myToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        showValue = (TextView) findViewById(R.id.CounterValue);
        increaseButton = (Button) findViewById(R.id.incButton);
        decreaseButton = (Button) findViewById(R.id.decButton);
    
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase.getInstance()
            .getReference()
            .child("users")
            .child(firebaseUser.getUid())
            .child("data")
            .child(date)
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange (DataSnapshot dataSnapshot) {
                    dataValue = dataSnapshot.getValue(StatisticData.class);
                    if (dataValue == null){
                        dataValue = new StatisticData();
                    }
                    showValue.setText(String.valueOf(dataValue.getCaffeine()));
                }
            
                @Override
                public void onCancelled (DatabaseError databaseError) {
                
                }
            });

        increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countIN(v);
            }
        });

        decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDE(v);
            }
        });

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
                Intent i = new Intent(CaffeineTracker.this, ProfilePage.class);
                startActivity(i);
                break;

            case R.id.setting_page:
                Intent h = new Intent(CaffeineTracker.this, SettingPage.class);
                startActivity(h);
                break;

            case R.id.home_page:
                Intent g = new Intent(CaffeineTracker.this, MainActivity.class);
                startActivity(g);
                break;

            case R.id.sleep_tracker:
                Intent b = new Intent(CaffeineTracker.this, SleepTracker.class);
                startActivity(b);
                break;

            case R.id.caffeine_tracker:
                Intent a = new Intent(CaffeineTracker.this, CaffeineTracker.class);
                startActivity(a);
                break;
        }

        return true;
    }

    public void  countIN(View view){
        //On each click increase the counter value
        dataValue.setCaffeine(dataValue.getCaffeine() + 1);
        StatisticData.updateData(dataValue, date, "caffeine");

    }
    public void  countDE(View view){
        //On each click decrease the counter value
        if (dataValue.getCaffeine() > 0) {
            dataValue.setCaffeine(dataValue.getCaffeine() - 1);
            StatisticData.updateData(dataValue, date, "caffeine");
        }

    }

}
