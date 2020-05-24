package com.example.diagno;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {


    //palette
    private EditText mNameF;
    private EditText mNameL;
    private EditText mEmail;
    private EditText mNameU;
    private EditText mPasswd;
    private EditText mCPasswd;

    private Button mbNext;
    private Button mbAcc;

    private String str;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mNameF = findViewById(R.id.name);
        mNameL = findViewById(R.id.namel);
        mEmail = findViewById(R.id.Email_id);
        mNameU = findViewById(R.id.u_name);
        mPasswd = findViewById(R.id.pass_wd);
        mCPasswd = findViewById(R.id.cpass_wd);

        mbNext = findViewById(R.id.Nextq);
        mbAcc = findViewById(R.id.accb);

        mbAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenDg();
                mVerify();
            }
        });

        mbNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(str == "Patient")
                {
                    Intent mintent1 = new Intent(Register.this, com.example.diagno.RegisterUser.class);
                    startActivity(mintent1);
                }
                else if(str == "Doctor")
                {
                    Intent mintent2 = new Intent(Register.this, com.example.diagno.RegisterDoc.class);
                    startActivity(mintent2);
                }

            }
        });
    }



    private void mVerify()
    {
        /*passwords match then store the entered info
        * in a shared pref
        * and enable next button*/
        if (mPasswd.getText().toString().equals(mCPasswd.getText().toString()))
        {
            SharedPreferences sp =  getSharedPreferences("Info",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("FirstName", mNameF.getText().toString());
            editor.putString("LastName", mNameL.getText().toString());
            editor.putString("UserName", mNameU.getText().toString());
            editor.putString("EmailID",mEmail.getText().toString());
            editor.putString("Password",mPasswd.getText().toString());
            editor.apply();

            mbNext.setEnabled(true);


        }
        else
        {
            Toast.makeText(Register.this,"Entered Passwords don't match",Toast.LENGTH_LONG).show();
            mbNext.setEnabled(false);
        }
    }

    protected void OpenDg()
    {


        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        alertDialog.setTitle("Account");
        alertDialog.setMessage("Choose account type");

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Patient", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                str = "Patient";

            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Doctor", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                str = "Doctor";

            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.cancel();
                mbNext.setEnabled(false);
            }
        });

        alertDialog.show();
    }


}
