package com.alvindrakes.loginpage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class SignupPage extends AppCompatActivity {
  
  private String name;
  private String email;
  private String password;
  private int age;
  private int weight;
  private int height;

  DatabaseReference database;
  
  public SignupPage () {
    // Default constructor required for calls to DataSnapshot.getValue(User.class)
  }
  
  public SignupPage (String name, String email, String password, int age, int weight, int height) {
    this.name = name;
    this.email = email;
    this.password = password;
    this.age = age;
    this.weight = weight;
    this.height = height;
  }
  
  @Override
  protected void onCreate (Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.signup);
    
    database = FirebaseDatabase.getInstance().getReference();
    
    final EditText nameText = (EditText) findViewById(R.id.nameform);
    final EditText emailText = (EditText) findViewById(R.id.emailform);
    final EditText passwordText = (EditText) findViewById(R.id.passform);
    final EditText ageText = (EditText) findViewById(R.id.agef);
    final EditText weightText = (EditText) findViewById(R.id.weightf);
    final EditText heightText = (EditText) findViewById(R.id.heightf);
    
    Button register = (Button) findViewById(R.id.register);
    
    register.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick (View view) {
        createAccount(nameText.getText().toString(),
                      emailText.getText().toString(),
                      passwordText.getText().toString(),
                      Integer.parseInt(ageText.getText().toString()),
                      Integer.parseInt(weightText.getText().toString()),
                      Integer.parseInt(heightText.getText().toString()));
        Intent StartPageIntent = new Intent(SignupPage.this, MainActivity.class);
        startActivity(StartPageIntent);
      }
    });
    
  }
  
  private void createAccount (String name, String email, String password, int age, int weight,
                              int height) {
    
    SignupPage newuser = new SignupPage(name, email, password, age, weight, height);
    String user_id = database.child("users").push().getKey();
    Map<String, Object> postValues = newuser.toMap();
  
    Map<String, Object> childUpdates = new HashMap<>();
    childUpdates.put("/users/" + user_id, postValues);
    
    database.updateChildren(childUpdates);

  }
  
  @Exclude
  public Map<String, Object> toMap() {
    HashMap<String, Object> result = new HashMap<>();
    result.put("name", name);
    result.put("email", email);
    result.put("password", password);
    result.put("age", age);
    result.put("weight", weight);
    result.put("height", height);
    
    return result;
  }
}


