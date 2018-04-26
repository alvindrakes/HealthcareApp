package com.alvindrakes.loginpage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by super on 3/15/2018.
 */

/**
 * Check if Google account is new or old, then redirect
 * to appropriate page
 */
public class LoginAuthentication extends Activity {
  
  User user;
  
  @Override
  public void onCreate (@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    //Allow checking of user data only once
    final boolean[] wait = {getIntent().getExtras().getBoolean("auth")};
    
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase.getInstance()
        .getReference()
        .child("users")
        .child(firebaseUser.getUid())
        .addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange (DataSnapshot dataSnapshot) {
            if (wait[0]) {
              user = dataSnapshot.getValue(User.class);
              //if user is new, direct to 2nd page of sign up
              if (user == null) {
                GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount
                    (getApplicationContext());
                User user = new User(acct.getDisplayName(), acct.getEmail());
                User.updateData(user);
                Toast.makeText(LoginAuthentication.this,
                               "Successfully created account",
                               Toast.LENGTH_SHORT).show();
                Intent infoIntent = new Intent(LoginAuthentication.this, Signup2.class);
                infoIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(infoIntent);
                wait[0] = false;
              }
              
              //login into the app
              else {
                Toast.makeText(LoginAuthentication.this, "Successfully login", Toast.LENGTH_SHORT)
                    .show();
                Intent infoIntent = new Intent(LoginAuthentication.this, MainActivity.class);
                finish();
                startActivity(infoIntent);
                wait[0] = false;
              }
            }
          }
          
          @Override
          public void onCancelled (DatabaseError databaseError) {
          }
        });
    finish();
  }
  
  @Override
  protected void onDestroy () {
    super.onDestroy();
    user = null;
  }
}
