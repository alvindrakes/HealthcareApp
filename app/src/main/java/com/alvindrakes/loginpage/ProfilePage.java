package com.alvindrakes.loginpage;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfilePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //for navigation drawer
    private DrawerLayout myDrawer;
    private ActionBarDrawerToggle myToggle;
    private NavigationView navigationView;
    
    User user;
    
    boolean clock = true;

    Button editBtn;
    Button cancelBtn;
    Button updateBtn;
    Button s0;
    Button s1;
    int x;
    Button s2;
    int bmi;
    Button s3;
    TextView ageInfo;
    TextView weightInfo;
    TextView heightInfo;
    TextView sleepInfo;
    EditText ageInfoEdit;
    EditText weightInfoEdit;
    EditText heightInfoEdit;
    TextView totalSteps;
    ImageView sprite;
    TextView bmiInfo;
    TextView userId;
    TextView coin;
    TextView userEmail;
    FirebaseUser firebaseUser;
    Transaction transaction;
    ImageView spacesuit1;
    ImageView spacesuit2;
    ImageView spacesuit3;
    ImageView spacesuit4;
    int indicatorSpacesuit;
    
    String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar bar = getSupportActionBar();
        bar.setTitle("Profile");
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#6F2D84")));
        getSupportActionBar().show();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile_page);

        myDrawer = (DrawerLayout) findViewById(R.id.myDrawer);
        myToggle = new ActionBarDrawerToggle(this, myDrawer, R.string.open, R.string.close);

        navigationView = (NavigationView) findViewById(R.id.my_navigation);
        navigationView.bringToFront();

        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        
        myDrawer.addDrawerListener(myToggle);
        myToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    

        editBtn = (Button) findViewById(R.id.edit_account_button);
        cancelBtn = (Button) findViewById(R.id.cancel_edit_account_button);
        updateBtn = (Button) findViewById(R.id.update_account_button);
        ageInfo = (TextView) findViewById(R.id.ageInfo);
        sleepInfo = (TextView) findViewById(R.id.sleepInfo);
        weightInfo = (TextView) findViewById(R.id.weightInfo);
        bmiInfo = (TextView) findViewById(R.id.bmiInfo);
        heightInfo = (TextView) findViewById(R.id.heightInfo);
        ageInfoEdit = (EditText) findViewById(R.id.ageInfo_edit);
        weightInfoEdit = (EditText) findViewById(R.id.weightInfo_edit);
        heightInfoEdit = (EditText) findViewById(R.id.heightInfo_edit);
        userId = (TextView) headerView.findViewById(R.id.User_ID);
        userEmail = (TextView) headerView.findViewById(R.id.User_email);
        coin = (TextView) findViewById(R.id.amount3) ;
        totalSteps = (TextView) findViewById(R.id.stepsInfo) ;
        sprite = (ImageView) findViewById(R.id.sprite);
        s0 = (Button) findViewById(R.id.s0);
        s1 = (Button) findViewById(R.id.s1);
        s2 = (Button) findViewById(R.id.s2);
        s3 = (Button) findViewById(R.id.s3);
        spacesuit1 = findViewById(R.id.sold1);
        spacesuit2 = findViewById(R.id.sold2);
        spacesuit3 = findViewById(R.id.sold3);
        spacesuit4 = findViewById(R.id.sold4);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //Prompt for transaction
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        //If buy button is clicked
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                if (user.getCoin() >= 100){
                    user.setCoin(user.getCoin() - 100);
                    User.updateCoin(user.getCoin());
                    sprite.setBackgroundResource(x);
                    if (indicatorSpacesuit == 1)
                        transaction.setSpacesuit1(true);
                    if (indicatorSpacesuit == 2)
                        transaction.setSpacesuit2(true);
                    if (indicatorSpacesuit == 3)
                        transaction.setSpacesuit3(true);
                    if (indicatorSpacesuit == 4)
                        transaction.setSpacesuit4(true);
                    Transaction.updateData(transaction);
                }
                else
                    Toast.makeText(ProfilePage.this, "Insufficient coins", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.setTitle("Buy the item?");
        builder.setMessage("Buy the item for 100 coins?");
        final AlertDialog dialog = builder.create();

        //Edit data
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                insertData();
                updatingData();
                ageInfoEdit.setError(null);
                weightInfoEdit.setError(null);
                heightInfoEdit.setError(null);
            }
        });

        //Cancel edits
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                insertData();
                viewingData();
            }
        });

        //Update edited data
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                if (TextUtils.isEmpty(ageInfoEdit.getText().toString())) {
                    ageInfoEdit.setError("Field is empty");
                } else if (TextUtils.isEmpty(weightInfoEdit.getText().toString())) {
                    weightInfoEdit.setError("Field is empty");
                } else if (TextUtils.isEmpty(heightInfoEdit.getText().toString())) {
                    heightInfoEdit.setError("Field is empty");
                } else if (validateInfo()) {
                    User.updateData(user);
                    while (clock) {
                    }
                    viewingData();
                    Toast.makeText(ProfilePage.this, "Account details updated", Toast.LENGTH_SHORT).show();
                    clock = true;
                }
            }
        });
        //============== show or purchase sprite=============
        s0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                x = (R.drawable.spritew);
                if (transaction.isSpacesuit1())
                    sprite.setBackgroundResource(x);
                else {
                    indicatorSpacesuit = 1;
                    dialog.show();
                }
            }
        });
        s1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                x = (R.drawable.spriter);
                if (transaction.isSpacesuit2())
                    sprite.setBackgroundResource(x);
                else {
                    indicatorSpacesuit = 2;
                    dialog.show();
                }
            }
        });
        s2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                x = (R.drawable.spriteg);
                if (transaction.isSpacesuit3())
                    sprite.setBackgroundResource(x);
                else {
                    indicatorSpacesuit = 3;
                    dialog.show();
                }
            }
        });
        s3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                x = (R.drawable.spriteb);
                if (transaction.isSpacesuit4())
                    sprite.setBackgroundResource(x);
                else {
                    indicatorSpacesuit = 4;
                    dialog.show();
                }
            }
        });
        //=============== End of show or purchase sprite=============


        //=============== Data Retrieval from firebase ==============
        FirebaseDatabase.getInstance()
            .getReference()
            .child("users")
            .child(firebaseUser.getUid())
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange (DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(User.class);
                    insertData();
                    clock = false;
                    userId.setText(user.getName());
                    coin.setText(String.valueOf(user.getCoin()));
                    userEmail.setText(user.getEmail());
                    totalSteps.setText(String.valueOf(user.getTotalSteps()));
                    bmiInfo.setText(String.valueOf(bmi_calculation(user.getHeight(),user.getWeight())));
                    if (user.getSleepToday() != 0){
                        int hour = user.getSleepToday()/3600;
                        int minute = (user.getSleepToday() % 3600) / 60;
                        sleepInfo.setText(String.valueOf(hour) + "hours " + String.valueOf(minute) +"minutes");
    
                    }
                    else
                        sleepInfo.setText("No info");
                }

                @Override
                public void onCancelled (DatabaseError databaseError) {

                }
            });
    
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase.getInstance()
            .getReference()
            .child("users")
            .child(firebaseUser.getUid())
            .child("transaction")
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange (DataSnapshot dataSnapshot) {
                    transaction = dataSnapshot.getValue(Transaction.class);
                    if (transaction == null){
                        transaction = new Transaction();
                    }
                    if (transaction.isSpacesuit1())
                        spacesuit1.setVisibility(View.VISIBLE);
                    if (transaction.isSpacesuit2())
                        spacesuit2.setVisibility(View.VISIBLE);
                    if (transaction.isSpacesuit3())
                        spacesuit3.setVisibility(View.VISIBLE);
                    if (transaction.isSpacesuit4())
                        spacesuit4.setVisibility(View.VISIBLE);
                }
            
                @Override
                public void onCancelled (DatabaseError databaseError) {
                
                }
            });
    }
    //===============End of data Retrieval from firebase ==============

    //Display user data
    private void insertData () {
    
        if (user.getAge() == 0) {
            ageInfo.setText("No info");
            ageInfoEdit.setText(null);
        } else {
            ageInfo.setText(String.valueOf(user.getAge()));
            ageInfoEdit.setText(String.valueOf(user.getAge()));
        }
        if (user.getHeight() == 0) {
            heightInfo.setText("No info");
            heightInfoEdit.setText(null);
        } else {
            heightInfo.setText(String.valueOf(user.getHeight()));
            heightInfoEdit.setText(String.valueOf(user.getHeight()));
        }
        if (user.getWeight() == 0) {
            weightInfo.setText("No info");
            weightInfoEdit.setText(null);
        } else {
            weightInfo.setText(String.valueOf(user.getWeight()));
            weightInfoEdit.setText(String.valueOf(user.getWeight()));
        }
    }
    
    private void updatingData () {
        ageInfo.setVisibility(View.INVISIBLE);
        ageInfoEdit.setVisibility(View.VISIBLE);
        heightInfo.setVisibility(View.INVISIBLE);
        heightInfoEdit.setVisibility(View.VISIBLE);
        weightInfo.setVisibility(View.INVISIBLE);
        weightInfoEdit.setVisibility(View.VISIBLE);
        editBtn.setVisibility(View.INVISIBLE);
        updateBtn.setVisibility(View.VISIBLE);
        cancelBtn.setVisibility(View.VISIBLE);
    }
    
    private void viewingData () {
        ageInfo.setVisibility(View.VISIBLE);
        ageInfoEdit.setVisibility(View.INVISIBLE);
        heightInfo.setVisibility(View.VISIBLE);
        heightInfoEdit.setVisibility(View.INVISIBLE);
        weightInfo.setVisibility(View.VISIBLE);
        weightInfoEdit.setVisibility(View.INVISIBLE);
        editBtn.setVisibility(View.VISIBLE);
        updateBtn.setVisibility(View.INVISIBLE);
        cancelBtn.setVisibility(View.INVISIBLE);
    }

    //Data validation
    private boolean validateInfo () {
        
        boolean validate = true;
        
        int age = Integer.parseInt(ageInfoEdit.getText().toString().trim());
        if (age < 1 || age > 100) {
            ageInfoEdit.setError("Invalid age. Please enter a value between 1 to 100");
            validate = false;
        } else {
            user.setAge(age);
        }
        
        int weight = Integer.parseInt(weightInfoEdit.getText().toString().trim());
        if (weight < 1 || weight > 300) {
            weightInfoEdit.setError("Invalid weight. Please enter a value between 1 to 300");
            validate = false;
        } else {
            user.setWeight(weight);
        }
        
        int height = Integer.parseInt(heightInfoEdit.getText().toString().trim());
        if (height < 100 || height > 250) {
            heightInfoEdit.setError("Invalid height. Please enter a value between 100 to 250");
            validate = false;
        } else {
            user.setHeight(height);
        }
        
        return validate;
    }

    //Side Pane
    @Override
    protected void onDestroy () {
        super.onDestroy();
        user = null;
        transaction = null;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (myToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.profile_page:
                Intent i = new Intent(ProfilePage.this, ProfilePage.class);
                startActivity(i);
                break;

            case R.id.setting_page:
                Intent h = new Intent(ProfilePage.this, SettingPage.class);
                startActivity(h);
                break;

            case R.id.home_page:
                Intent g = new Intent(ProfilePage.this, MainActivity.class);
                startActivity(g);
                break;

            case R.id.sleep_tracker:
                Intent b = new Intent(ProfilePage.this, SleepTracker.class);
                startActivity(b);
                break;

            case R.id.caffeine_tracker:
                Intent a = new Intent(ProfilePage.this, CaffeineTracker.class);
                startActivity(a);
                break;
        }

        return true;
    }
    //Method for Bmi calculation
    public float bmi_calculation(int h, int w)
    {
        float x = (float) h/100;
        x = w/(x*x);
        int y = (int)x;
        return y;
    }
}
