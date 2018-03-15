package com.alvindrakes.loginpage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by super on 3/15/2018.
 */

public class GoogleSigninAuthentication extends Activity {
  
  User user;
  
  @Override
  public void onCreate (@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  
    Log.e("entered","test");
    
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase.getInstance()
        .getReference()
        .child("users")
        .child(firebaseUser.getUid())
        .addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange (DataSnapshot dataSnapshot) {
            Log.e("entered2","test");
            user = dataSnapshot.getValue(User.class);
            
            if (user == null) {
              GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
              User user = new User(acct.getDisplayName(), acct.getEmail());
              User.updateData(user);
              Toast.makeText(GoogleSigninAuthentication.this, "Successfully created account", Toast.LENGTH_SHORT).show();
              Intent infoIntent = new Intent(GoogleSigninAuthentication.this, Signup2.class);
              startActivity(infoIntent);
            }
            else {
              Toast.makeText(GoogleSigninAuthentication.this, "Successfully login", Toast.LENGTH_SHORT).show();
              Intent infoIntent = new Intent(GoogleSigninAuthentication.this, MainActivity.class);
              startActivity(infoIntent);
            }
          }
        
          @Override
          public void onCancelled (DatabaseError databaseError) {
          }
        });
    finish();
  }
}
