package com.example.diagno;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity
{




    private String mEmail;
    private String mPasswd;

    //logs
    private static final String TAG = "LoginActivity";

    //palette
    private EditText email_id;
    private EditText passwd;
    private Button signinb;
    private TextView register;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email_id = (EditText) findViewById(R.id.emailB);
        passwd = (EditText) findViewById(R.id.Passwd);

        signinb = (Button) findViewById(R.id.SignB);
        register = (TextView) findViewById(R.id.Register_tv);


        MyBluetoothService.mAuth = FirebaseAuth.getInstance();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mEmail = email_id.getText().toString();
                mPasswd = passwd.getText().toString();


                Register();
            }
        });

        signinb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mEmail = email_id.getText().toString();
                mPasswd = passwd.getText().toString();


                signIn();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        //FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);

       /* Intent mIntent = new Intent(LoginActivity.this, com.example.diagno.PatientHome.class);

        startActivity(mIntent);*/
    }



    private void Register() {

        Intent mIntent = new Intent(LoginActivity.this, com.example.diagno.Register.class);

        startActivity(mIntent);

       /* */
    }

    private void signIn() {
        MyBluetoothService.mAuth.signInWithEmailAndPassword(mEmail, mPasswd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = MyBluetoothService.mAuth.getCurrentUser();

                            //ntent mIntent = new Intent(LoginActivity.this, com.example.diagno.HomeActivity.class);
                            //startActivity(mIntent);
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });

        Intent mIntent = new Intent(LoginActivity.this, com.example.diagno.UserHomeActivity.class);

        startActivity(mIntent);

    }
}

