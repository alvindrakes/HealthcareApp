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
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
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

import java.sql.Time;
import java.util.Calendar;

public class SleepTracker extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //for navigation drawer
    private DrawerLayout myDrawer;
    private ActionBarDrawerToggle myToggle;
    private NavigationView navigationView;
    
    TextView userId;
    TextView userEmail;
    
    User user;
    FirebaseUser firebaseUser;
    
    AlarmManager alarmManager;
    TimePicker timePicker;
    TextView updateText;
    Context context;
    PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar bar = getSupportActionBar();
        bar.setTitle("Sleep");
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
        
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        timePicker = (TimePicker) findViewById(R.id.time_picker);
        updateText = (TextView) findViewById(R.id.update_text);
        final Calendar calendar = Calendar.getInstance();
        Button alarmOn = (Button) findViewById(R.id.on_alarm);
        Button alarmOff = (Button) findViewById(R.id.off_alarm);
        
        final Intent alarmReceiver = new Intent(this.context, AlarmReceiver.class);
        
        alarmOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                int hour;
                int minute;
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
                
                String hourString = String.valueOf(hour);
                String minuteString = String.valueOf(minute);
                
                if (hour > 12)
                    hourString = String.valueOf(hour - 12);
                if (minute < 10)
                    minuteString = "0" + String.valueOf(minute);
                
                updateText.setText("Alarm set to " + hourString + ":" + minuteString);
                
                alarmReceiver.putExtra("extra", "alarm on");
                pendingIntent = PendingIntent.getBroadcast(SleepTracker.this, 0, alarmReceiver, PendingIntent.FLAG_UPDATE_CURRENT);
                
                alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
        
            }
        });
        
        alarmOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                updateText.setText("Alarm off");
                
                alarmManager.cancel(pendingIntent);
                
                alarmReceiver.putExtra("extra", "alarm off");
                sendBroadcast(alarmReceiver);
        
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