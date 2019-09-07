package com.example.knome;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignupActivity extends ProgressActivity {

    private FirebaseAuth mAuth;

    private static final String TAG = "SignupActivity";
    @BindView(R.id.firstName_inp) EditText mFirstNameInp;
    @BindView(R.id.lastName_inp) EditText mLastNameInp;
    @BindView(R.id.email_inp) EditText mEmailInp;
    @BindView(R.id.pass_inp) EditText mPassInp;
    @BindView(R.id.confirm_pwd_inp) EditText mConfirmPwdInp;
    @BindView(R.id.submit) Button mSubmit;
    LocationManager locationManager ;
    boolean GpsStatus ;
    Context context;

    public String personName;
    public String personEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();
        ButterKnife.bind(SignupActivity.this);


        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = view.getId();
                if(i == R.id.submit){

                    createAccount(mEmailInp.getText().toString(),mConfirmPwdInp.getText().toString());
                }

            }
        });
                Toolbar myToolbar = findViewById(R.id.my_toolbar);
                setSupportActionBar(myToolbar);

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
        //Toast.makeText(this,"User already registered",Toast.LENGTH_SHORT).show();
    }

    public void onDestroy(){
        super.onDestroy();
        this.finish();
    }

    private void createAccount(String email, String password) {

        Log.d(TAG, "createAccount:" + email);

        if (!validateForm()) {

            return;

        }
        showProgressDialog();
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information

                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                                //Toast.makeText(SignupActivity.this,"User already signed-in",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignupActivity.this, ShowActivity.class);
                                startActivity(intent);
                                SignupActivity.this.finish();
                                updateUI(user);
                        } else {

                            // If sign in fails, display a message to the user.

                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(SignupActivity.this, "Already Signed-Up. Please Sign-in",Toast.LENGTH_SHORT).show();

                        }
                        // [START_EXCLUDE]

                        if (!task.isSuccessful()) {

                            Log.w(TAG, "signUpWithEmail:failure", task.getException());
                            /*Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                            startActivity(intent);*/
                            SignupActivity.this.finish();

                        }

                    }
                });
        hideProgressDialog();
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if(user != null){
            Toast.makeText(SignupActivity.this,"User signed-in",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(SignupActivity.this,"Please Sign-up",Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateForm() {
        String password = mPassInp.getText().toString();
        String confirmPwd= mConfirmPwdInp.getText().toString();
        String email = mEmailInp.getText().toString();

        boolean valid = true;

        if (TextUtils.isEmpty(email)) {
            mEmailInp.setError("Required.");
            valid = false;

        } else {
            mEmailInp.setError(null);
        //    Toast.makeText(this, "No email entered",Toast.LENGTH_SHORT).show();
        }



        if(!password.equals(confirmPwd))
        {
            mPassInp.setError("Password does not match");
            Toast.makeText(this,"Please enter matching password",Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password)) {

            mPassInp.setError("Required.");

            valid = false;

        } else {
            mConfirmPwdInp.setError(null);
        }
        return valid;

    }
}
