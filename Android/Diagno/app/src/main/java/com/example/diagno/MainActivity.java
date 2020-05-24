package com.example.diagno;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    //private FirebaseAuth mAuth;
    private DocumentReference userRef;

    //logs
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
       final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

       Toast.makeText(MainActivity.this, currentUser != null ? currentUser.toString() : null,Toast.LENGTH_LONG).show();

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if (currentUser != null)
                {
                    //MyBluetoothService.db.c
                    //currentUser.
                    MyBluetoothService.db = FirebaseFirestore.getInstance();
                    userRef = MyBluetoothService.db.collection("Patients").document(currentUser.getUid());
                    userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful())
                            {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot != null) {
                                    if(documentSnapshot.exists())
                                    {

                                        Log.d(TAG,"Doc exists!");
                                        Intent mIntent = new Intent(MainActivity.this,com.example.diagno.UserHomeActivity.class);
                                        startActivity(mIntent);
                                        Toast.makeText(MainActivity.this, "signed in " + currentUser.getEmail(), Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        Log.d(TAG,"Doc doesn't exist!");
                                        Intent mIntent = new Intent(MainActivity.this,com.example.diagno.Register.class);
                                        startActivity(mIntent);
                                        Toast.makeText(MainActivity.this, "register first " + currentUser.getEmail(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            else
                            {
                                Log.d(TAG,"Failed with ",task.getException());
                            }
                        }
                    });


                }
                else
                {
                    Intent mIntent = new Intent(MainActivity.this,com.example.diagno.LoginActivity.class);
                    startActivity(mIntent);
                    Toast.makeText(MainActivity.this, "Not signed in", Toast.LENGTH_SHORT).show();
                }
            }
        }, 5000);


    }
}
