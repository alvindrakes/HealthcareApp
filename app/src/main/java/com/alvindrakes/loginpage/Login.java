package com.alvindrakes.loginpage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;


public class Login extends AppCompatActivity{
    private Button check;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        check = (Button) findViewById(R.id.login);
        check.setOnClickListener(new View.OnClickListener(){
            @Override
                    public void onClick(View view){
                login();
            }
        });
}
    private void login() {
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);


    }

}
