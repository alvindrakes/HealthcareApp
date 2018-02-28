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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Window;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    TextView ageInfo;
    TextView weightInfo;
    TextView heightInfo;
    EditText ageInfoEdit;
    EditText weightInfoEdit;
    EditText heightInfoEdit;
    
    TextView userId;
    TextView userEmail;
    
    FirebaseUser firebaseUser;
    
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
        weightInfo = (TextView) findViewById(R.id.weightInfo);
        heightInfo = (TextView) findViewById(R.id.heightInfo);
        ageInfoEdit = (EditText) findViewById(R.id.ageInfo_edit);
        weightInfoEdit = (EditText) findViewById(R.id.weightInfo_edit);
        heightInfoEdit = (EditText) findViewById(R.id.heightInfo_edit);
        userId = (TextView) headerView.findViewById(R.id.User_ID);
        userEmail = (TextView) headerView.findViewById(R.id.User_email);
    
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


    
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
    
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                insertData();
                viewingData();
            }
        });
    
    
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                if (validateInfo()) {
                    User.updateData(user);
                    while (clock) {
                    }
                    viewingData();
                    Toast.makeText(ProfilePage.this, "Account details updated", Toast.LENGTH_SHORT).show();
                    clock = true;
                }
            }
        });

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
                    userEmail.setText(user.getEmail());
                }
            
                @Override
                public void onCancelled (DatabaseError databaseError) {
                
                }
            });
    }
    
    private void insertData () {
        
        ageInfo.setText(Integer.toString(user.getAge()));
        ageInfoEdit.setText(Integer.toString(user.getAge()));
        heightInfo.setText(Integer.toString(user.getHeight()));
        heightInfoEdit.setText(Integer.toString(user.getHeight()));
        weightInfo.setText(Integer.toString(user.getWeight()));
        weightInfoEdit.setText(Integer.toString(user.getWeight()));
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
        }

        return true;
    }
}
