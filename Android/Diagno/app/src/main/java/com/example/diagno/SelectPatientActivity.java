package com.example.diagno;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SelectPatientActivity extends AppCompatActivity {

    private static final String TAG = "SelectPatientActivity";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private TextView mName;
    private TextView mHeight;
    private TextView mWeight;
    private TextView mVata;
    private TextView mPitta;
    private TextView mKapha;
    private TextView mTemp;
    private TextView mAge;

    private Button mDiagnose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_patient);
        setTitle("Patient Info");

        Bundle mBundle = getIntent().getExtras();

        mName = findViewById(R.id.textView1);
        mHeight = findViewById(R.id.textView2);
        mWeight = findViewById(R.id.textView3);
        mVata = findViewById(R.id.textView4);
        mPitta = findViewById(R.id.textView5);
        mKapha = findViewById(R.id.textView6);
        mTemp = findViewById(R.id.textView7);
        mAge = findViewById(R.id.textView8);
        mDiagnose = findViewById(R.id.buttonDiagnose);


        if (null != mBundle){
            final String name = mBundle.getString("Patient name");
            Log.d(TAG, "passed string: " + name);

            assert name != null;
            String[] names = name.split(" ");

            db = FirebaseFirestore.getInstance();

            CollectionReference patientsRef = db.collection("Patients");

            Query query =patientsRef.whereEqualTo("First", names[0]).whereEqualTo("Last",names[1]);

            query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    assert queryDocumentSnapshots != null;
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        if (doc.get("First") != null && doc.get("Last") != null)
                        {
                            Log.d(TAG,"Name: "+doc.getString("First")+" "+doc.getString("Last"));
                            mName.setText(String.format("Name: %s %s", doc.getString("First"), doc.getString("Last")));
                            Log.d(TAG,"Height "+doc.getString("Height")+" cm");
                            mHeight.setText(String.format("Height: %s cm", doc.getString("Height")));
                            Log.d(TAG,"Weight "+doc.getString("Weight")+ " kg");
                            mWeight.setText(String.format("Weight: %s kg", doc.getString("Weight")));

                            //key temp
                            String key = "";

                            for (Map.Entry<String, Object> params : doc.getData().entrySet())
                            {

                                if (params.getKey().equals("Sensor data"))
                                {
                                    //nested hashmap
                                    HashMap<String, Object> sensors = (HashMap<String, Object>) params.getValue();

                                    for(Map.Entry<String, Object> vals : sensors.entrySet())
                                    {
                                        Log.d(TAG, vals.getKey()+ " "+vals.getValue());
                                        key = vals.getKey();

                                        switch (key)
                                        {
                                            case "Vata":
                                                mVata.setText(String.format("Vata: %s bpm",vals.getValue().toString()));
                                                break;

                                            case "Pitta":
                                                mPitta.setText(String.format("Pitta: %s bpm",vals.getValue().toString()));
                                                break;

                                            case "Kapha":
                                                mKapha.setText(String.format("Kapha: %s bpm",vals.getValue().toString()));
                                                break;

                                            case "Temp":
                                                mTemp.setText(String.format("Temp: %s °F",vals.getValue().toString()));
                                                break;
                                        }

                                    }
                                }
                            }

                            Log.d(TAG,"Age "+ Objects.requireNonNull(doc.get("Age")).toString()+ " years");
                            mAge.setText(String.format("Age: %s years", Objects.requireNonNull(doc.get("Age")).toString()));
                        }
                    }


                }
            });


            mDiagnose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent mIntent = new Intent(SelectPatientActivity.this,com.example.diagno.DiagnoseActivity.class);
                    mIntent.putExtra("Name", name);
                    startActivity(mIntent);
                    finish();



                }
            });
        }


    }





}
