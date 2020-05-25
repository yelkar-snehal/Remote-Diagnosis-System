package com.example.diagno;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class BaseMenuActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        //return super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.Sign_out) {
            SignOut();
            return true;
        }

        return true;
    }


    private void SignOut()
    {
       // FirebaseAuth.getInstance().signOut();

        //Init and attach
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(authStateListener);

        //Call signOut()
        firebaseAuth.signOut();
    }

    FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            if (firebaseAuth.getCurrentUser() == null){
                //Do anything here which needs to be done after signout is complete
                //signOutComplete();
                Intent mIntent = new Intent(BaseMenuActivity.this, com.example.diagno.LoginActivity.class);
                startActivity(mIntent);
                Toast.makeText(BaseMenuActivity.this,"Signed out successfully",Toast.LENGTH_SHORT).show();
            }
            /*else {

                Toast.makeText(BaseMenuActivity.this,"Sign out failed",Toast.LENGTH_SHORT).show();
            }*/
        }
    };


}

