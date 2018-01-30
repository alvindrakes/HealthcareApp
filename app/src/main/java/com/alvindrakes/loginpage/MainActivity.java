package com.alvindrakes.loginpage;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements OnDataPointListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    Button IncBtn;
    Button SaveBtn;
    Button AccountBtn;
    TextView progress;
    TextView coins;
    TextView dayValue;
    EditText heartData;
    CircleProgressBar Goal;
    
    User user;
    FirebaseUser firebaseUser;
    
    StatisticData data = new StatisticData();

    //Member variables for GoogleAPIs
    private static final int REQUEST_OAUTH = 1;
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;
    private GoogleApiClient mApiClient;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
    
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase.getInstance()
            .getReference()
            .child("users")
            .child(firebaseUser.getUid())
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange (DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(User.class);
                    dayValue.setText(Integer.toString(user.getDay()));
                    coins.setText(Integer.toString(user.getCoin()));
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

        Goal = (CircleProgressBar) findViewById(R.id.DailyGoal);
        IncBtn = (Button) findViewById(R.id.IncBtn);
        SaveBtn = (Button) findViewById(R.id.SaveBtn);
        AccountBtn = (Button) findViewById(R.id.go_to_account_details);
        progress = (TextView) findViewById(R.id.Progress);
        coins = (TextView) findViewById(R.id.amount);
        dayValue = (TextView) findViewById(R.id.dayValue);
        heartData = (EditText) findViewById(R.id.heartData);
        
        SaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data.setHeartData(Integer.parseInt(heartData.getText().toString().trim()));
                StatisticData.updateData(data,user.getDay());
                
                data.setSteps(0);
                Goal.setProgress(0);
                progress.setText(Integer.toString(data.getSteps()));
                
                user.setDay(user.getDay()+1);
                
                Log.d("Status", "Save success");
                Toast.makeText(MainActivity.this, "Save successfully.", Toast.LENGTH_SHORT).show();
            }
        });

        IncBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                data.setSteps(data.getSteps()+1);
                progress.setText(Integer.toString(data.getSteps()));
    
                if (data.getSteps() < 1000){
                    Goal.setProgress(data.getSteps());
                }
                else {
                    user.setCoin(user.getCoin() + 1);
                    StatisticData.updateCoin(user.getCoin());
                    coins.setText(Integer.toString(user.getCoin()));
                }
                return false;
            }
            public boolean performClick() {
                return true;
            }
        });
        
        AccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Intent AccountIntent = new Intent(MainActivity.this, Account.class);
                startActivity(AccountIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Abstract methods from GoogleApi's interfaces
    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onDataPoint(DataPoint dataPoint) {

    }
}
