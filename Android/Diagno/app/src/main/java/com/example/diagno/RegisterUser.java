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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RegisterUser extends AppCompatActivity {

    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private EditText mBirthdate;
    private EditText mAddress;
    private EditText mContactno;
    private EditText mHeight;
    private EditText mWeight;
    private EditText mDocName;
    private EditText mDocNum;

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
        setTitle("Register");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        radioGroup = findViewById(R.id.radioGroup);
        mBirthdate = findViewById(R.id.bdate);
        //setDate fromDate = new setDate(mBirthdate, this);

        mAddress = findViewById(R.id.Addr);
        mContactno = findViewById(R.id.Contactno);
        mHeight = findViewById(R.id.editHeight);
        mWeight = findViewById(R.id.editWeight);
        mDocName = findViewById(R.id.editDocName);
        mDocNum = findViewById(R.id.editDocNum);

        mRegister = findViewById(R.id.register);

       SharedPreferences sp =  getSharedPreferences("Info",Context.MODE_PRIVATE);

        s = new String[13];

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
                        mHeight.getText().toString().length() == 0 ||
                        mWeight.getText().toString().length() == 0 ||
                        mDocName.getText().toString().length() == 0 ||
                        mDocNum.getText().toString().length() == 0 ||
                        s[5].length() == 0)
                {
                    Toast.makeText(RegisterUser.this,"Some fields might be empty!",Toast.LENGTH_LONG).show();
                }
                else {
                    //Toast.makeText(RegisterUser.this, s6, Toast.LENGTH_LONG).show();
                    s[6] = mBirthdate.getText().toString();
                    s[7] = mAddress.getText().toString();
                    s[8] = mContactno.getText().toString();
                    s[9] = mHeight.getText().toString();
                    s[10] = mWeight.getText().toString();
                    s[11] = mDocName.getText().toString();
                    s[12] = mDocNum.getText().toString();
                    RegisterAcc(s);
                }

            }
        });



    }

    private void RegisterAcc(@NonNull final String []cred)
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

                            int years = 0;

                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy",Locale.US);
                            Date birthDate = null;
                            try {
                                birthDate = sdf.parse(cred[6]);
                                Age age = calculateAge(birthDate);
                                years = age.getYears();

                            } catch (ParseException e) {
                                //e.printStackTrace();
                                Log.e(TAG, "parse err",e);
                            }


                            Map <String, Object> muser = new HashMap<>();
                            muser.put("First",     s[0]);
                            muser.put("Last",      s[1]);
                            muser.put("UName",     s[2]);
                            muser.put("Gender",    s[5]);
                            muser.put("BirthDate", s[6]);
                            muser.put("Address",   s[7]);
                            muser.put("ContactNo", s[8]);
                            muser.put("Height",    s[9]);
                            muser.put("Weight",    s[10]);
                            muser.put("DoctorName",s[11]);
                            muser.put("DoctorNum", s[12]);
                            muser.put("Age",       years);
                            muser.put("Diagnosis", "Nothing to show");
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


    public void ChkButt(View view) {

        int radioId = radioGroup.getCheckedRadioButtonId() ;
        radioButton = findViewById(radioId);
        Toast.makeText(RegisterUser.this,"Selected gender: " + radioButton.getText().toString()+ "",Toast.LENGTH_SHORT).show();
        s[5] = radioButton.getText().toString();

    }

    private  Age calculateAge(Date birthDate)
    {
        int years = 0;
        int months = 0;
        int days = 0;

        //create calendar object for birth day
        Calendar birthDay = Calendar.getInstance();
        birthDay.setTimeInMillis(birthDate.getTime());

        //create calendar object for current day
        long currentTime = System.currentTimeMillis();
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(currentTime);

        //Get difference between years
        years = now.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);
        int currMonth = now.get(Calendar.MONTH) + 1;
        int birthMonth = birthDay.get(Calendar.MONTH) + 1;

        //Get difference between months
        months = currMonth - birthMonth;

        //if month difference is in negative then reduce years by one
        //and calculate the number of months.
        if (months < 0)
        {
            years--;
            months = 12 - birthMonth + currMonth;
            if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
                months--;
        } else if (months == 0 && now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
        {
            years--;
            months = 11;
        }

        //Calculate the days
        if (now.get(Calendar.DATE) > birthDay.get(Calendar.DATE))
            days = now.get(Calendar.DATE) - birthDay.get(Calendar.DATE);
        else if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
        {
            int today = now.get(Calendar.DAY_OF_MONTH);
            now.add(Calendar.MONTH, -1);
            days = now.getActualMaximum(Calendar.DAY_OF_MONTH) - birthDay.get(Calendar.DAY_OF_MONTH) + today;
        }
        else
        {
            days = 0;
            if (months == 12)
            {
                years++;
                months = 0;
            }
        }
        //Create new Age object
        return new Age(days, months, years);
    }

}
