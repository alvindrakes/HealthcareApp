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


public class CaffeineTracker extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //for navigation drawer
    private DrawerLayout myDrawer;
    private ActionBarDrawerToggle myToggle;
    private NavigationView navigationView;

    TextView showValue;
    Button increaseButton;
    Button decreaseButton;
    int counter = 0 ;//click value add counter


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
        counter++;
        showValue.setText(Integer.toString(counter));

    }
    public void  countDE(View view){
        //On each click decrease the counter value
        counter--;
        if (counter<0)
            counter=0;
        showValue.setText(Integer.toString(counter));

    }
    public void  reset(View view){
        //On each click resets
        counter=0;
        showValue.setText(String.valueOf(counter));
    }

}
