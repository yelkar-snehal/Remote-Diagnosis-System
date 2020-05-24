package com.example.diagno;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterUser extends AppCompatActivity {

    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private EditText mBirthdate;
    private EditText mAddress;
    private EditText mContactno;

    private Button mRegister;

    private String []s;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    //logs
    private static final String TAG = "RegisterUserActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        radioGroup = findViewById(R.id.radioGroup);
        mBirthdate = findViewById(R.id.bdate);
        mAddress = findViewById(R.id.Addr);
        mContactno = findViewById(R.id.Contactno);

        mRegister = findViewById(R.id.register);

       SharedPreferences sp =  getSharedPreferences("Info",Context.MODE_PRIVATE);

        s = new String[9];

        s[0] = sp.getString("FirstName","");
        s[1] = sp.getString("LastName","");
        s[2] = sp.getString("UserName","");
        s[3] = sp.getString("EmailID","");
        s[4] = sp.getString("Password","");

        //s6 = s1 + s2 + s3 + s4 + s5 ;

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mAddress.getText().toString().length() == 0 ||
                        mContactno.getText().toString().length() == 0 ||
                        mBirthdate.getText().toString().length() == 0 ||
                        s[5].length() == 0)
                {
                    Toast.makeText(RegisterUser.this,"Some fields might be empty!",Toast.LENGTH_LONG).show();
                }
                else {
                    //Toast.makeText(RegisterUser.this, s6, Toast.LENGTH_LONG).show();
                    s[6] = mBirthdate.getText().toString();
                    s[7] = mAddress.getText().toString();
                    s[8] = mContactno.getText().toString();
                    RegisterAcc(s);
                }

            }
        });



    }

    private void RegisterAcc(@NonNull String []cred)
    {
        mAuth.createUserWithEmailAndPassword(cred[3], cred[4])
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in sucEmailPasswordcess, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            Map<String, Object> sub = new HashMap<>();
                            sub.put("Vata", 0);
                            sub.put("Pitta", 0);
                            sub.put("Kapha", 0);
                            sub.put("Temp", 0);


                            Map <String, Object> muser = new HashMap<>();
                            muser.put("First",     s[0]);
                            muser.put("Last",      s[1]);
                            muser.put("Uname",     s[2]);
                            muser.put("Gender",    s[5]);
                            muser.put("Birthdate", s[6]);
                            muser.put("Address",   s[7]);
                            muser.put("ContactNo", s[8]);
                            muser.put("Doctor name", "xyz");
                            muser.put("EnquiryRaised", false);
                            muser.put("Sensor data", sub);



                            //db.collection("Patients").document(user.getUid()).set(user);


                            assert user != null;
                            db.collection("Patients").document(user.getUid())
                                    .set(muser)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(RegisterUser.this, "Storage successful.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(RegisterUser.this, "Storage failed",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            Toast.makeText(RegisterUser.this, "Authentication successful.",
                                    Toast.LENGTH_SHORT).show();

                            Intent mIntent = new Intent(RegisterUser.this,com.example.diagno.UserHomeActivity.class);
                            startActivity(mIntent);

                        }
                        else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterUser.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }


    protected void ChkButt(View view) {

        int radioId = radioGroup.getCheckedRadioButtonId() ;
        radioButton = findViewById(radioId);
        Toast.makeText(RegisterUser.this,"Selected gender: " + radioButton.getText().toString()+ "",Toast.LENGTH_SHORT).show();
        s[5] = radioButton.getText().toString();

    }

}
