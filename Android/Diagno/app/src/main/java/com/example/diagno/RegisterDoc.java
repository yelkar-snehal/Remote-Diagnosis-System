package com.example.diagno;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterDoc extends AppCompatActivity
{

    //layout elements
    private Button mButtonRegDoc;
    private EditText mTextClinicName;
    private EditText mTextClinicAdd;
    private EditText mContactNum;

    //logs
    private static final String TAG = "RegisterDocActivity";

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    //class var
    private String[] s;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_doc);
        setTitle("Register");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        mButtonRegDoc = (findViewById(R.id.buttonRegDoc));
        mTextClinicName = (findViewById(R.id.editClinicName));
        mTextClinicAdd = (findViewById(R.id.editClinicAdd));
        mContactNum = (findViewById(R.id.editContact));

        //get info from prev activity
        SharedPreferences sp =  getSharedPreferences("Info", Context.MODE_PRIVATE);

        s = new String[8];

        s[0] = sp.getString("FirstName","");
        s[1] = sp.getString("LastName","");
        s[2] = sp.getString("UserName","");
        s[3] = sp.getString("EmailID","");
        s[4] = sp.getString("Password","");

        //Toast.makeText(RegisterDoc.this, s[3]+s[4], Toast.LENGTH_LONG).show();


        //on button click
        mButtonRegDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check if all fields are filled
                if(0 == mTextClinicAdd.getText().toString().length() ||
                        0 == mTextClinicName.getText().toString().length() ||
                        0 == mContactNum.getText().toString().length()
                )
                {
                    Toast.makeText(RegisterDoc.this,"Some fields might be empty!",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    s[5] = mContactNum.getText().toString();
                    s[6] = mTextClinicName.getText().toString();
                    s[7] = mTextClinicAdd.getText().toString();

                    RegDocAcc(s);
                }

            }
        });

    }

    //Register method
    private void RegDocAcc(@NonNull String []cred)
    {
        Toast.makeText(RegisterDoc.this, s[0]+s[1]+s[2]+s[3]+s[4]+s[5]+s[6]+s[7], Toast.LENGTH_LONG).show();
        mAuth.createUserWithEmailAndPassword(cred[3], cred[4])
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in sucEmailPasswordcess, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();


                            Map <String, Object> muser = new HashMap<>();
                            muser.put("First",              s[0]);
                            muser.put("Last",               s[1]);
                            muser.put("Uname",              s[2]);
                            muser.put("Contact Number",     s[5]);
                            muser.put("Clinic Name",        s[6]);
                            muser.put("Clinic Address",     s[7]);


                            assert user != null;
                            db.collection("Doctors").document(user.getUid())
                                    .set(muser)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(RegisterDoc.this, "Storage successful.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(RegisterDoc.this, "Storage failed",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            Toast.makeText(RegisterDoc.this, "Authentication successful.",
                                    Toast.LENGTH_SHORT).show();

                            //Intent mIntent = new Intent(RegisterDoc.this,com.example.diagno.DocHomeActivity.class);
                            //startActivity(mIntent);

                        }
                        else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterDoc.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
