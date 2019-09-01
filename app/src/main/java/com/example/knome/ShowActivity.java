package com.example.knome;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class ShowActivity extends MainActivity {
    public FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        TextView mName = findViewById(R.id.name);
        TextView mEmail = findViewById(R.id.email);
        TextView mLocation = findViewById(R.id.location);
        mAuth=FirebaseAuth.getInstance();
    }
}
