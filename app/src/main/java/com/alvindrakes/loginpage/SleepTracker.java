package com.alvindrakes.loginpage;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SleepTracker extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //for navigation drawer
    private DrawerLayout myDrawer;
    private ActionBarDrawerToggle myToggle;
    private NavigationView navigationView;
    
    public static boolean alarmSet;
    
    TextView userId;
    TextView userEmail;
    
    User user;
    FirebaseUser firebaseUser;
    StatisticData dataValue;
    
    AlarmManager alarmManager;
    TimePicker timePicker;
    TextView updateText;
    Context context;
    PendingIntent pendingIntent;
    
    Button alarmOff;
    public static String hourString;
    public static String minuteString;
    
    String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar bar = getSupportActionBar();
        bar.setTitle("Sleep Tracker");
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#6F2D84")));
        getSupportActionBar().show();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sleep_tracker);
        this.context = this;

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

                }
            
                @Override
                public void onCancelled (DatabaseError databaseError) {
                
                }
            });
        
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        timePicker = (TimePicker) findViewById(R.id.time_picker);
        updateText = (TextView) findViewById(R.id.update_text);
        Button alarmOn = (Button) findViewById(R.id.on_alarm);
        alarmOff = (Button) findViewById(R.id.off_alarm);
        
        final Intent alarmReceiver = new Intent(this.context, AlarmReceiver.class);
        
        alarmOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                int hour;
                int minute;
                Calendar calendar = Calendar.getInstance();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                    calendar.set(Calendar.MINUTE, timePicker.getMinute());
                    hour=timePicker.getHour();
                    minute=timePicker.getMinute();
                }
                else {
                    calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                    calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
                    hour=timePicker.getCurrentHour();
                    minute=timePicker.getCurrentMinute();
                }
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                if(calendar.before(Calendar.getInstance())) {
                    calendar.add(Calendar.DATE, 1);
                }
    
                hourString = String.valueOf(hour);
                minuteString = String.valueOf(minute);
    
                if (hour > 12) {
                    hourString = String.valueOf(hour - 12);
                }
                if (minute < 10) {
                    minuteString = "0" + String.valueOf(minute);
                }
    
                updateText.setText("Alarm set to " + hourString + ":" + minuteString);
                Log.e("Calendar", String.valueOf(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(calendar.getTime())));
                
                alarmReceiver.putExtra("extra", "alarm on");
                alarmReceiver.putExtra("sleep", String.valueOf(Calendar.getInstance().getTimeInMillis()));
                pendingIntent = PendingIntent.getBroadcast(SleepTracker.this, 0, alarmReceiver, PendingIntent.FLAG_UPDATE_CURRENT);
    
                alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
    
                alarmOff.setVisibility(View.VISIBLE);
    
                alarmSet = true;
            }
        });
        
        alarmOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                updateText.setText("Alarm off");
                
                alarmManager.cancel(pendingIntent);
                
                alarmReceiver.putExtra("extra", "alarm off");
                alarmReceiver.putExtra("sleepData", dataValue.getSleepData());
                alarmReceiver.putExtra("coin", user.getCoin());
                sendBroadcast(alarmReceiver);
                
                alarmOff.setVisibility(View.INVISIBLE);
        
                alarmSet = false;
            }
        });
    }
    
    @Override
    protected void onStart () {
        super.onStart();
        if (alarmSet) {
            alarmOff.setVisibility(View.VISIBLE);
            updateText.setText("Alarm set to " + hourString + ":" + minuteString);
        }
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

            case R.id.caffeine_tracker:
                Intent a = new Intent(SleepTracker.this, CaffeineTracker.class);
                startActivity(a);
                break;
        }

        return true;
    }
    
    @Override
    protected void onDestroy () {
        super.onDestroy();
        user = null;
        dataValue = null;
    }
}