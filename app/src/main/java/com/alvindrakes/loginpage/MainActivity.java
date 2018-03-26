package com.alvindrakes.loginpage;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.Alvindrakes.HealthcareApp.UnityPlayerActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener, SensorEventListener, StepListener {

    TextView userId;
    TextView userEmail;
    TextView progress;
    TextView coins;
    TextView dateText;

    //Data from Firebase
    User user;
    StatisticData dataValue;
    
    //Firebase Authentication
    FirebaseUser firebaseUser;
    
    //Google Sign-In Authentication
    GoogleSignInClient googleSignInClient;

    //for navigation drawer
    private DrawerLayout myDrawer;
    private ActionBarDrawerToggle myToggle;
    private NavigationView navigationView;

    //Fields for step tracker
    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private static final String TEXT_NUM_STEPS = "Number of Steps: ";
    private int numSteps;
    private TextView TvSteps;
    private ProgressBar progress_of_steps;
    
    String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    
    
    Context mContext;
    View mTextView;
    Activity mActivity;

    // LocationHelper locationHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        locationHelper=new LocationHelper(MainActivity.this);
//        locationHelper.checkpermission();


//        mContext = getApplicationContext();
//        mTextView = (TextView) findViewById(R.id.ConstraintLayout);
//        mActivity = MainActivity.this;
    
    
        //=======================Pedometer============
        // Get an instance of the SensorManager
        TvSteps = (TextView) findViewById(R.id.tv_steps);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);
    
        sensorManager.registerListener(MainActivity.this, accel, SensorManager.SENSOR_DELAY_FASTEST);
        progress_of_steps = (ProgressBar)findViewById(R.id.steps_progress);
    
        //============== End of Pedometer==============
    
        myDrawer = (DrawerLayout) findViewById(R.id.myDrawer);
        myToggle = new ActionBarDrawerToggle(this, myDrawer, R.string.open, R.string.close);
    
        navigationView = (NavigationView) findViewById(R.id.my_navigation);
        navigationView.bringToFront();
    
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
    
        myDrawer.addDrawerListener(myToggle);
        myToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#6F2D84")));
        bar.setTitle("Home");
        getSupportActionBar().show();
    

        progress = (TextView) findViewById(R.id.Progress);
        coins = (TextView) findViewById(R.id.amount);
        dateText = (TextView) findViewById(R.id.dateValue);
        userId = (TextView) headerView.findViewById(R.id.User_ID);
        userEmail = (TextView) headerView.findViewById(R.id.User_email);
    
        dateText.setText(date);
    
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
                    coins.setText(String.valueOf(user.getCoin()));
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
                    TvSteps.setText(TEXT_NUM_STEPS + dataValue.getSteps());
                    progress_of_steps.setProgress(dataValue.getSteps());
    
                }
            
                @Override
                public void onCancelled (DatabaseError databaseError) {
                
                }
            });
    
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.store);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent StartPageIntent = new Intent(MainActivity.this, Store.class);
                StartPageIntent.putExtra("coin",user.getCoin());
                startActivity(StartPageIntent);
            }
        });
    
        
        //----------------Game section---------------------------------
        
        Button LaunchButton = (Button) findViewById(R.id.launchGame);
        
        LaunchButton.setOnClickListener(new View.OnClickListener() {
          public void onClick(View v) { GoToUnity(v); }
        });
        
        //---------------------------------------------------------------

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void step(long timeNs) {
        if (dataValue == null){
            dataValue = new StatisticData();
        }
        dataValue.setSteps(dataValue.getSteps() + 1);
        user.setTotalSteps(user.getTotalSteps() + 1);
        
        if (dataValue.getSteps() % 10 == 0)
            user.setCoin(user.getCoin() + 1);
        
        if (progress_of_steps.getMax() == dataValue.getSteps()){
            user.setCoin(user.getCoin() + 100);
            Toast.makeText(this, "100 coins for reaching goal!", Toast.LENGTH_LONG).show();
        }
        
        TvSteps.setText(TEXT_NUM_STEPS + dataValue.getSteps());
        progress_of_steps.setProgress(dataValue.getSteps());
        
        User.updateData(user);
        StatisticData.updateData(dataValue, date, "steps");
    
        Intent locationService = new Intent(MainActivity.this, MyLocationUsingLocationAPI.class);
        startActivity(locationService);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.my_menu, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (myToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // TODO need to implement the buttons to move to other pages
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {

            case R.id.home_page:
                Intent g = new Intent(MainActivity.this, MainActivity.class);
                startActivity(g);
                break;


            case R.id.sleep_tracker:
                Intent b = new Intent(MainActivity.this, SleepTracker.class);
                startActivity(b);
                break;

            case R.id.profile_page:
                Intent i = new Intent(MainActivity.this, ProfilePage.class);
                startActivity(i);
                break;

            case R.id.setting_page:
                Intent h = new Intent(MainActivity.this, SettingPage.class);
                startActivity(h);
                break;
        }
        return true;
    }
    
    
    
    // go to game when button is clicked
    public void GoToUnity(View view)
    {
        Intent intent = new Intent(this, UnityPlayerActivity.class);
        startActivity(intent);
        System.out.print("Game is running !!!!!!!");
    }
    
}
