package com.alvindrakes.loginpage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class StartPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startpage);

        Button advanceToLoginPage = (Button) findViewById(R.id.log_in_button);
        Button advanceToSignup = (Button) findViewById(R.id.sign_up_button);
        advanceToSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent StartPageIntent = new Intent(StartPage.this, SignupPage.class);
                startActivity(StartPageIntent);
            }
        });
        advanceToLoginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent StartPageIntent = new Intent(StartPage.this, Login.class);
                startActivity(StartPageIntent);
            }
        });
    }
}

