package com.example.knome;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends MainActivity {

    ProgressActivity object = new ProgressActivity();
    MainActivity mainObj = new MainActivity();
    private static final String TAG = "SignInActivity";

    public void signIn(String email, String password){

        Log.d(TAG, "signIn:" + email);

        if (!validateForm()) {
            return;
        }

        object.showProgressDialog();
        // [START sign_in_with_email]

        mAuth.signInWithEmailAndPassword(email, password)

                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override

                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information

                            Log.d(TAG, "signInWithEmail:success");

                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(SignInActivity.this, ShowActivity.class);
                            startActivity(intent);

                            updateUI(user);

                        } else {

                            // If sign in fails, display a message to the user.

                            Log.w(TAG, "signInWithEmail:failure", task.getException());

                            Toast.makeText(SignInActivity.this, "Authentication failed.",

                                    Toast.LENGTH_SHORT).show();

                            updateUI(null);

                        }


                        // [START_EXCLUDE]

                        if (!task.isSuccessful()) {

                            Log.w(TAG, "signUpWithEmail:failure", task.getException());
                            Toast.makeText(SignInActivity.this,"Sign-in Unsuccessful",Toast.LENGTH_SHORT).show();

                        }

                        object.hideProgressDialog();

                        // [END_EXCLUDE]

                    }

                });

        // [END sign_in_with_email]

    }
    private boolean validateForm() {
          String email = mainObj.mLoginEmail.getText().toString();
          String password = mLoginPass.getText().toString();

        boolean valid = true;

        if (TextUtils.isEmpty(email)) {
            mainObj.mLoginEmail.setError("Required.");
            valid = false;

        } else {
            mainObj.mLoginEmail.setError(null);
            //    Toast.makeText(this, "No email entered",Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(password)) {

            mLoginPass.setError("Required.");

            valid = false;

        } else {
            mLoginPass.setError(null);
        }
        return valid;

    }
    private void updateUI(FirebaseUser user) {
        object.hideProgressDialog();
        if(user!=null){
            Toast.makeText(SignInActivity.this,"User already signed-in",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(SignInActivity.this,"Sign-In unsuccessful",Toast.LENGTH_SHORT).show();
        }
    }
}