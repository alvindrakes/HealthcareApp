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
import android.view.Window;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.validator.routines.EmailValidator;

public class SignupPage extends AppCompatActivity {
  
  DatabaseReference database;
  FirebaseAuth auth;
  
  EditText nameText;
  EditText emailText;
  EditText passwordText;
  EditText checkPasswordText;
  EditText ageText;
  EditText weightText;
  EditText heightText;
  
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
    ageText = (EditText) findViewById(R.id.agef);
    weightText = (EditText) findViewById(R.id.weightf);
    heightText = (EditText) findViewById(R.id.heightf);
    
    Button register = (Button) findViewById(R.id.register);
    
    
    register.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick (View view) {
        if (TextUtils.isEmpty(nameText.getText().toString())) {
          nameText.setError("Field is empty");
        } else if (TextUtils.isEmpty(emailText.getText().toString())) {
          emailText.setError("Field is empty");
        } else if (TextUtils.isEmpty(passwordText.getText().toString())) {
          passwordText.setError("Field is empty");
        } else if (TextUtils.isEmpty(ageText.getText().toString())) {
          ageText.setError("Field is empty");
        } else if (TextUtils.isEmpty(weightText.getText().toString())) {
          weightText.setError("Field is empty");
        } else if (TextUtils.isEmpty(heightText.getText().toString())) {
          heightText.setError("Field is empty");
        } else {
          User user = new User(nameText.getText().toString().trim(),
                               emailText.getText().toString().trim(),
                               passwordText.getText().toString(),
                               Integer.parseInt(ageText.getText().toString().trim()),
                               Integer.parseInt(weightText.getText().toString().trim()),
                               Integer.parseInt(heightText.getText().toString().trim()));
          
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
    
    if (user.getAge() < 1 || user.getAge() > 100) {
      ageText.setError("Invalid age. Please enter a value between 1 to 100");
      validate = false;
    }
    
    if (user.getWeight() < 1 || user.getWeight() > 300) {
      weightText.setError("Invalid weight. Please enter a value between 1 to 300");
      validate = false;
    }
    
    if (user.getHeight() < 100 || user.getHeight() > 250) {
      heightText.setError("Invalid height. Please enter a value between 100 to 250");
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
              Intent StartPageIntent = new Intent(SignupPage.this, MainActivity.class);
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


