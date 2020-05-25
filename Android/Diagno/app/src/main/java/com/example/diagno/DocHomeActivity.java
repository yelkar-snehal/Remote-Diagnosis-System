package com.example.diagno;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

public class DocHomeActivity extends BaseMenuActivity {

    private static final String TAG = "DocHomeActivity";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<String> patients  = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_home);
        setTitle("List of my patients");


    }


    @Override
    protected void onStart() {
        super.onStart();

        getName();
    }

    public void getName()
    {

        final String[] ret = new String[1];

        db.collection("Doctors").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).
                get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task< DocumentSnapshot > task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    //assert doc != null;
                    assert doc != null;
                    if (doc.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + doc.getString("Diagnosis"));
                        //ret[0] = doc.getString("First")+" "+doc.getString("Last");

                        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

                        //db = FirebaseFirestore.getInstance();

                        //final List<String> patients = new ArrayList<>();

                        // Create a reference to the cities collection
                        CollectionReference patientsRef = db.collection("Patients");

                        //Create a query against the collection.
                        Query query =patientsRef.whereEqualTo("DoctorName", doc.getString("First")+" "+doc.getString("Last"));

                        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                assert queryDocumentSnapshots != null;
                                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                    if (doc.get("First") != null && doc.get("Last") != null) {
                                        patients.add(doc.getString("First")+" "+doc.getString("Last"));

                                    }
                                }
                                Log.d(TAG, "your patients: " + patients);
                                layoutManager = new LinearLayoutManager(com.example.diagno.DocHomeActivity.this);
                                recyclerView.setHasFixedSize(true);
                                mAdapter = new MyAdapter(patients);
                                recyclerView.setLayoutManager(layoutManager);
                                recyclerView.setAdapter(mAdapter);

                            }
                        });
                    } else {
                        Log.d(TAG, "No such document");
                    }

                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

    }


}

