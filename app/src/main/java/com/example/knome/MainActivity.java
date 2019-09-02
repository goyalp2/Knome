package com.example.knome;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends ProgressActivity {
    public FirebaseAuth mAuth;
    @BindView(R.id.signup) Button mSignupBtn;
    @BindView(R.id.email) EditText mLoginEmail;
    @BindView(R.id.password) EditText mLoginPass;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        Button mLoginBtn = findViewById(R.id.login);
        ButterKnife.bind(this);

        mSignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);

            }
        });
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = view.getId();
                if(i == R.id.login) {
                    hideKeyboard(view);
                    signIn(mLoginEmail.getText().toString(), mLoginPass.getText().toString());
                }
     /*           try {
                    signIn(loginEmail, loginPass);
                }catch (Exception e){
                    e.getMessage();
                    Toast.makeText(MainActivity.this,loginEmail,Toast.LENGTH_SHORT).show();
                } finally {
                    Toast.makeText(MainActivity.this,"No Email Captured",Toast.LENGTH_SHORT).show();
                }
     */       }
        });
    }
    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser currentUser=mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    public void signIn(String email, String password){

        Log.d(TAG, "signIn:" + email);

        if (!validateForm()) {
            return;
        }

        showSignInDialog();
        // [START sign_in_with_email]

        mAuth.signInWithEmailAndPassword(email, password)

                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override

                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information

                            Log.d(TAG, "signInWithEmail:success");

                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(MainActivity.this, ShowActivity.class);
                            startActivity(intent);

                            updateUI(user);

                        }

                        else {

                            // If sign in fails, display a message to the user.
                            hideProgressDialog();
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this,"Email or password invalid", Toast.LENGTH_SHORT).show();
                            updateUI(null);

                        }


                        // [START_EXCLUDE]

                        if (!task.isSuccessful()) {

                            hideProgressDialog();
                            Log.w(TAG, "signUpWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this,"Sign-in Unsuccessful",Toast.LENGTH_SHORT).show();

                        }

                        hideProgressDialog();

                        // [END_EXCLUDE]

                    }

                });

        // [END sign_in_with_email]

    }
    private boolean validateForm() {
        String email = this.mLoginEmail.getText().toString();
        String password = this.mLoginPass.getText().toString();

        boolean valid = true;

        if (TextUtils.isEmpty(email)) {
            this.mLoginEmail.setError("Required.");
            valid = false;

        } else {
            this.mLoginEmail.setError(null);
            //    Toast.makeText(this, "No email entered",Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(password)) {

            this.mLoginPass.setError("Required.");

            valid = false;

        } else {
            this.mLoginPass.setError(null);
        }
        return valid;

    }
    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if(user!=null){
            Toast.makeText(this,"User already signed-in",Toast.LENGTH_SHORT).show();
        } else {
            hideProgressDialog();
            Toast.makeText(this,"Sign-in unsuccessful",Toast.LENGTH_SHORT).show();
        }
    }

}
