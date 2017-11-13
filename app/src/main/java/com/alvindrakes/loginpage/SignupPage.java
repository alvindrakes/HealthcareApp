package com.alvindrakes.loginpage;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
  
  private String name;
  private String email;
  private String password;
  private String checkPassword;
  private int age;
  private int weight;
  private int height;
  
  DatabaseReference database;
  
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
  
  public SignupPage (String name,
                     String email,
                     String password,
                     String checkPassword,
                     int age,
                     int weight,
                     int height) {
    this.name = name;
    this.email = email;
    this.password = password;
    this.checkPassword = checkPassword;
    this.age = age;
    this.weight = weight;
    this.height = height;
  }
  
  @Override
  protected void onCreate (Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.signup);
    
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
        } else if (createAccount(nameText.getText().toString(),
                                 emailText.getText().toString(),
                                 passwordText.getText().toString(),
                                 checkPasswordText.getText().toString(),
                                 Integer.parseInt(ageText.getText().toString()),
                                 Integer.parseInt(weightText.getText().toString()),
                                 Integer.parseInt(heightText.getText().toString()))) {
          
          Intent StartPageIntent = new Intent(SignupPage.this, MainActivity.class);
          startActivity(StartPageIntent);
        }
      }
    });
    
  }
  
  private boolean createAccount (String name,
                                 String email,
                                 String password,
                                 String checkPassword,
                                 int age,
                                 int weight,
                                 int height) {
    
    SignupPage newuser = new SignupPage(name, email, password, checkPassword, age, weight, height);
    
    if (!validateForm(newuser)) {
      return false;
    }
  
    authenticateAccount(email, password, newuser);
    
    String user_id = database.child("users").push().getKey();
    Map<String, Object> userValue = newuser.toMap();
    
    Map<String, Object> childUpdates = new HashMap<>();
    childUpdates.put("/users/" + user_id, userValue);
    
    database.updateChildren(childUpdates);
    return true;
  }
  
  @Exclude
  public Map<String, Object> toMap () {
    HashMap<String, Object> result = new HashMap<>();
    result.put("name", name);
    result.put("email", email);
    result.put("password", password);
    result.put("age", age);
    result.put("weight", weight);
    result.put("height", height);
    
    return result;
  }
  
  public boolean validateForm (final SignupPage newuser) {
    
    final boolean[] validate = {true};
    
    if (!EmailValidator.getInstance().isValid(newuser.email)) {
      emailText.setError("Invalid email");
      validate[0] = false;
    } else {
      Query query = database.child("users").orderByChild("email").equalTo(newuser.email);
      query.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange (DataSnapshot dataSnapshot) {
          if (dataSnapshot.exists()) {
            emailText.setError("Email already exist");
            validate[0] = false;
          }
        }
        
        @Override
        public void onCancelled (DatabaseError databaseError) {
        
        }
      });
    }
    
    if ((newuser.password).length() < 6) {
      passwordText.setError("Must be at least 6 characters long");
      validate[0] = false;
    }
    
    if (!Objects.equals(newuser.password, newuser.checkPassword)) {
      checkPasswordText.setError("Password does not match");
      validate[0] = false;
    }
    
    if (newuser.age < 1 || newuser.age > 100) {
      ageText.setError("Invalid age. Please enter a value between 1 to 100");
      validate[0] = false;
    }
    
    if (newuser.weight < 1 || newuser.weight > 300) {
      weightText.setError("Invalid weight. Please enter a value between 1 to 300");
      validate[0] = false;
    }
    
    if (newuser.height < 100 || newuser.height > 250) {
      heightText.setError("Invalid height. Please enter a value between 100 to 250");
      validate[0] = false;
    }
    
    
    return validate[0];
  }
  
  private void authenticateAccount (String email, String password, SignupPage newuser) {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete (@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
              Log.d("EmailPassword", "createUserWithEmail:success");
            } else {
              Log.w("EmailPassword", "createUserWithEmail:failure", task.getException());
            }
            
          }
        });
  }
}


