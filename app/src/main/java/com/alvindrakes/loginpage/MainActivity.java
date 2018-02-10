package com.alvindrakes.loginpage;


import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataSourcesResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import com.Alvindrakes.HealthcareApp.UnityPlayerActivity;

public class MainActivity extends AppCompatActivity implements OnDataPointListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    Button IncBtn;
    Button SaveBtn;
    Button AccountBtn;
    Button signOutBtn;
    TextView progress;
    TextView coins;
    TextView dayValue;
    EditText heartData;
    CircleProgressBar Goal;
    
    User user;
    FirebaseUser firebaseUser;
    GoogleSignInClient googleSignInClient;
    
    StatisticData data = new StatisticData();

    //Member variables for GoogleAPIs
    private static final int REQUEST_OAUTH = 1;
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;
    private GoogleApiClient mApiClient;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        //Initialize the GoogleApiClient instance by adding the Fitness Sensors API, defining a scope, and registering the application callbacks
        if (savedInstanceState != null)
        {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.SENSORS_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    
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
        signOutBtn = (Button) findViewById(R.id.signOutBtn);
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
    
                if (data.getSteps() < 20){
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
  
      signOutBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick (View v) {
          FirebaseAuth.getInstance().signOut();
          googleSignInClient.revokeAccess()
              .addOnCompleteListener(MainActivity.this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete (@NonNull Task<Void> task) {
                  Toast.makeText(MainActivity.this, "Log out successfully", Toast.LENGTH_SHORT).show();
                  Intent startIntent = new Intent(MainActivity.this, StartPage.class);
                  startActivity(startIntent);
                }
              });
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

    //connect to Google's backend
    @Override
    protected void onStart() {
        super.onStart();
        mApiClient.connect();
    }

    //Abstract methods from GoogleApi's interfaces
    @Override
    public void onConnected(Bundle bundle) {
        DataSourcesRequest dataSourceRequest = new DataSourcesRequest.Builder()
                .setDataTypes( DataType.TYPE_STEP_COUNT_CUMULATIVE )
                .setDataSourceTypes( DataSource.TYPE_DERIVED )
                .build();

        ResultCallback<DataSourcesResult> dataSourcesResultCallback = new ResultCallback<DataSourcesResult>() {
            @Override
            public void onResult(DataSourcesResult dataSourcesResult) {
                for( DataSource dataSource : dataSourcesResult.getDataSources() ) {
                    if( DataType.TYPE_STEP_COUNT_CUMULATIVE.equals( dataSource.getDataType() ) ) {
                        registerFitnessDataListener(dataSource, DataType.TYPE_STEP_COUNT_CUMULATIVE);
                    }
                }
            }
        };

        Fitness.SensorsApi.findDataSources(mApiClient, dataSourceRequest)
                .setResultCallback(dataSourcesResultCallback);
    }


    //method to find step count every 3 seconds, when new data is available,listener is triggered;If no new data is found, the OnDataPointListener is not triggered and the Fitness API waits another three seconds before checking again.
    private void registerFitnessDataListener(DataSource dataSource, DataType dataType) {

        SensorRequest request = new SensorRequest.Builder()
                .setDataSource( dataSource )
                .setDataType( dataType )
                .setSamplingRate( 3, TimeUnit.SECONDS )
                .build();

        Fitness.SensorsApi.add( mApiClient, request, this )
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.e( "GoogleFit", "SensorApi successfully added" );
                        }
                    }
                });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        if( !authInProgress )
        {
            try {
                authInProgress = true;
                connectionResult.startResolutionForResult( MainActivity.this, REQUEST_OAUTH );
            } catch(IntentSender.SendIntentException e ) {

            }
        }
        else
            {
                Log.e( "GoogleFit", "authInProgress" );
            }
    }

    //when a change in counter is detected, loop through the variable and display step counter
    @Override
    public void onDataPoint(DataPoint dataPoint) {
        for( final Field field : dataPoint.getDataType().getFields() ) {
            final Value value = dataPoint.getValue( field );
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Field: " + field.getName() + " Value: " + value, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    //user either grants application permission to use their data or they close the dialog, canceling the process
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode == REQUEST_OAUTH ) {
            authInProgress = false;
            if( resultCode == RESULT_OK ) {
                if( !mApiClient.isConnecting() && !mApiClient.isConnected() ) {
                    mApiClient.connect();
                }
            } else if( resultCode == RESULT_CANCELED ) {
                Log.e( "GoogleFit", "RESULT_CANCELED" );
            }
        } else {
            Log.e("GoogleFit", "requestCode NOT request_oauth");
        }
    }

    //disconnecting from the SensorApi and Google Play Services when done using them
    @Override
    protected void onStop() {
        super.onStop();

        Fitness.SensorsApi.remove( mApiClient, this )
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            mApiClient.disconnect();
                        }
                    }
                });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(AUTH_PENDING, authInProgress);
    }


    // go to game when button is clicked
    public void GoToUnity(View view)
    {
        Intent intent = new Intent(this, UnityPlayerActivity.class);
        startActivity(intent);
        System.out.print("Game is running !!!!!!!");
    }
}
