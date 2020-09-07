package com.mehnaz.dailyshoppinglist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class RegistrationActivity extends AppCompatActivity {
    private EditText name;
    private EditText email;
    private EditText password;
    private TextView login;
    private Button submit;

    private FirebaseAuth mAuth;

    private ProgressBar mDialog;
    private static final String TAG = RegistrationActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);





        name = findViewById(R.id.etNameRegistration);
        email = findViewById(R.id.etRegistrationEmail);
        password = findViewById(R.id.etPasswordRegistration);
        login = findViewById(R.id.tvLogin);
        submit = findViewById(R.id.btnRegistration);
        mDialog = findViewById(R.id.progress_circular);


        mAuth = FirebaseAuth.getInstance();

     submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String  userName = name.getText().toString().trim();
                String mEmail = email.getText().toString().trim();
                String  mPassword = password.getText().toString().trim();

//                if(TextUtils.isEmpty(userName)){
//                    name.setError("Required Field...");
//                    return;
//                }
                if(TextUtils.isEmpty(mEmail)){
                    email.setError("Required Field...");
                    return;
                }

                if(TextUtils.isEmpty(mPassword)){
                    password.setError("Required Field...");
                    return;
                }

                mDialog.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(mEmail,mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {

                       if(task.isSuccessful()){
                           startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                           Toast.makeText(getApplicationContext(),"Successful",Toast.LENGTH_SHORT).show();
                           mDialog.setVisibility(View.GONE);
                       }
                       else {
                           Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
                           mDialog.setVisibility(View.GONE);
                           Log.e(TAG, "onComplete: Failed=" + task.getException().getMessage());
                       }

                   }
               });
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

    }

}