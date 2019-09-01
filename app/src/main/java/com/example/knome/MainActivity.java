package com.example.knome;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    public FirebaseAuth mAuth;
    @BindView(R.id.login) Button mLoginBtn;
    @BindView(R.id.signup) Button mSignupBtn;
    @BindView(R.id.email) EditText mLoginEmail;
    //EditText mLoginEmail = findViewById(R.id.email);
    ProgressActivity object = new ProgressActivity();
    EditText mLoginPass = findViewById(R.id.password);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        ProgressActivity object = new ProgressActivity();
        SignInActivity SignInObject = new SignInActivity();
        ButterKnife.bind(this);
    //    Button signup=findViewById(R.id.signup);
        //        signup.setClickable(true);
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
                String loginEmail = mLoginEmail.getText().toString();
                String loginPass = mLoginPass.getText().toString();
                SignInObject.signIn(loginEmail,loginPass);
            }
        });
    }
    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser currentUser=mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    private void updateUI(FirebaseUser user) {
        object.hideProgressDialog();
        if(user!=null){
            Toast.makeText(this,"User already signed-in",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,"Sign-in unsuccessful",Toast.LENGTH_SHORT).show();
        }
    }

}
