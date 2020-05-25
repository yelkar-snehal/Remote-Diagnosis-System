package com.example.diagno;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DiagnoseActivity extends AppCompatActivity {

    private static final String TAG = "DiagnoseActivity";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private  String[] states = {"Balanced","Balanced","Balanced"};
    private  String result;
    String[] names;

    private Button mClose;
    private Button mSend;
    private EditText comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.alertbox_diagnose);
        Bundle mBundle = getIntent().getExtras();
        assert mBundle != null;
        final String name = mBundle.getString("Name");
        setTitle(name);
        assert name != null;
        names = name.split(" ");

        RadioGroup vata = (RadioGroup) findViewById(R.id.radioGroup2);
        RadioGroup pitta = (RadioGroup) findViewById(R.id.radioGroup3);
        RadioGroup kapha = (RadioGroup) findViewById(R.id.radioGroup4);

        mClose = (findViewById(R.id.buttonclose));
        mSend = (findViewById(R.id.buttonok));

        comments = (findViewById(R.id.dispdiag));


        vata.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                states[0] = ((RadioButton) findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString();

            }
        });

        pitta.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                states[1] = ((RadioButton) findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString();
            }
        });


        kapha.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                states[2] = ((RadioButton) findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString();
            }
        });

        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Diagnose obj = new Diagnose(states[0],states[1],states[2],comments.getText().toString());
                Log.d(TAG,states[0]+states[1]+states[2]+comments.getText().toString());
                result = obj.returnDiagnosis();
                SaveDiag(result);

            }
        });

        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(DiagnoseActivity.this,com.example.diagno.DocHomeActivity.class);
                startActivity(mIntent);
                finish();

            }
        });

    }

    public void SaveDiag(String result)
    {
        final String res = result;
        CollectionReference patientsRef = db.collection("Patients");

        Query query =patientsRef.whereEqualTo("First", names[0]).whereEqualTo("Last",names[1]);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                assert queryDocumentSnapshots != null;
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    if (doc.get("First") != null && doc.get("Last") != null)
                    {
                       DocumentReference d = doc.getReference();
                       d.update(
                            "EnquiryRaised", false,
                            "Diagnosis", res


                    )
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //Toast.makeText(UserHomeActivity.this, "Storage successful.",
                                    //Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "Stored");
                                    Toast.makeText(DiagnoseActivity.this,"Stored",Toast.LENGTH_SHORT).show();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(DiagnoseActivity.this,"couldn't store",Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "Could not store");
                                }
                            });
                    }
                }


            }
        });

    }
}
