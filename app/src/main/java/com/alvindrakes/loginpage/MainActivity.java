package com.alvindrakes.loginpage;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button IncBtn;
    Button DecBtn;
    Button ResetBtn;
    Button AccountBtn;
    TextView progress;
    TextView coins;
    int progress_value = 0;
    int coins_amount=0;
    CircleProgressBar Goal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.store);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent StartPageIntent = new Intent(MainActivity.this, Store.class);
                startActivity(StartPageIntent);
            }
        });

        Goal = (CircleProgressBar) findViewById(R.id.DailyGoal);
        IncBtn = (Button) findViewById(R.id.IncBtn);
        DecBtn = (Button) findViewById(R.id.DecBtn);
        ResetBtn = (Button) findViewById(R.id.ResetBtn);
        AccountBtn = (Button) findViewById(R.id.go_to_account_details);
        progress = (TextView) findViewById(R.id.Progress);
        coins = (TextView) findViewById(R.id.amount);

        ResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress_value = 0;
                Goal.setProgress(0);
                progress.setText(Integer.toString(progress_value));
            }
        });

        IncBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                Goal.setProgress(progress_value);
                if (progress_value != 1000)
                    progress_value++;
                progress.setText(Integer.toString(progress_value));
                if (progress_value == 1000) {
                    coins_amount++;
                    coins.setText(Integer.toString(coins_amount));
                }
                return false;
            }
            public boolean performClick() {
                return true;
            }
        });

        DecBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                Goal.setProgress(progress_value);
                if (progress_value != 0)
                    progress_value--;
                progress.setText(Integer.toString(progress_value));
                return false;
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
}
