package com.alvindrakes.loginpage;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.Window;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Login extends AppCompatActivity {
  private Button check;
  private FirebaseAuth mAuth;
  
  private EditText emailText;
  private EditText passwordText;
  private Button Sign_up_btn;
  @Override
  protected void onCreate (Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
      getSupportActionBar().hide();
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.login);
    
    check = (Button) findViewById(R.id.login);
    emailText = (EditText) findViewById(R.id.email);
    passwordText = (EditText) findViewById(R.id.password);
    Sign_up_btn = (Button) findViewById(R.id.sign_up_button);

    mAuth = FirebaseAuth.getInstance();
    
    check.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick (View view) {
        login();
      }
    });

    Sign_up_btn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick (View v) {
        Intent startIntent = new Intent(Login.this, SignupPage.class);
        startActivity(startIntent);
      }
    });
  }
  
  private void login () {
    mAuth.signInWithEmailAndPassword(emailText.getText().toString(),
                                     passwordText.getText().toString())
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete (@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
              // Sign in success, update UI with the signed-in user's information
              Log.d("LogIn", "signInWithEmail:success");
              Toast.makeText(Login.this, "Log in successfully.", Toast.LENGTH_SHORT).show();
  
              Intent intent = new Intent(getApplicationContext(), MainActivity.class);
              startActivity(intent);

            } else {
              // If sign in fails, display a message to the user.
              Log.w("LogIn", "signInWithEmail:failure", task.getException());
              Toast.makeText(Login.this, "Incorrect credentials.", Toast.LENGTH_SHORT).show();
            }
            
            // ...
          }
        });
    
    
  }
  
}
