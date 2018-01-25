package com.alvindrakes.loginpage;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by ming on 24/1/2018.
 */

public class Store extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store);

        FloatingActionButton back = (FloatingActionButton) findViewById(R.id.close_store);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent StartPageIntent = new Intent(Store.this, MainActivity.class);
                startActivity(StartPageIntent);
            }
        });
    }
}
