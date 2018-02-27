package com.alvindrakes.loginpage;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import org.apache.commons.validator.routines.EmailValidator;

public class SignupPage extends AppCompatActivity {
  
  DatabaseReference database;
  FirebaseAuth auth;
  
  EditText nameText;
  EditText emailText;
  EditText passwordText;
  EditText checkPasswordText;

  
  public SignupPage () {
    // Default constructor required for calls to DataSnapshot.getValue(User.class)
  }
  
  @Override
  protected void onCreate (Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getSupportActionBar().hide();
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.signup);
    
    auth = FirebaseAuth.getInstance();
    
    database = FirebaseDatabase.getInstance().getReference();
    
    nameText = (EditText) findViewById(R.id.nameform);
    emailText = (EditText) findViewById(R.id.emailform);
    passwordText = (EditText) findViewById(R.id.passform);
    checkPasswordText = (EditText) findViewById(R.id.cform);
    
    Button register = (Button) findViewById(R.id.Next);

    
    register.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick (View view) {
        if (TextUtils.isEmpty(nameText.getText().toString())) {
          nameText.setError("Field is empty");
        } else if (TextUtils.isEmpty(emailText.getText().toString())) {
          emailText.setError("Field is empty");
        } else if (TextUtils.isEmpty(passwordText.getText().toString())) {
          passwordText.setError("Field is empty");
        } else {
          User user = new User(nameText.getText().toString().trim(),
                               emailText.getText().toString().trim(),
                  passwordText.getText().toString().trim());
          createAccount(user);
        }
      }
    });
    
  }
  
  private void createAccount (User user) {
    
    if (!validateForm(user)) {
      return;
    }
    authenticateAccount(user);

  }
  
  public boolean validateForm (User user) {
    
    boolean validate = true;
    
    if (!EmailValidator.getInstance().isValid(user.getEmail())) {
      emailText.setError("Invalid email");
      validate = false;
    }
    
    if ((user.getPassword()).length() < 6) {
      passwordText.setError("Must be at least 6 characters long");
      validate = false;
    }
    
    if (!Objects.equals(user.getPassword(), checkPasswordText.getText().toString())) {
      checkPasswordText.setError("Password does not match");
      validate = false;
    }
    return validate;
  }
  
  private void authenticateAccount (final User user) {
    
    auth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete (@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
              
              User.updateData(user);


              Log.d("EmailPassword", "createUserWithEmail:success");
              Toast.makeText(SignupPage.this, "Account created", Toast.LENGTH_SHORT).show();
              Intent StartPageIntent = new Intent(SignupPage.this, Signup2.class);
              startActivity(StartPageIntent);
            } else {
              Log.w("EmailPassword", "createUserWithEmail:failure", task.getException());
              Toast.makeText(SignupPage.this, "Failed to create account", Toast.LENGTH_SHORT)
                  .show();
              emailText.setError("Email already exist");
            }
            
          }
        });
  }
  
}


