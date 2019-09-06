package com.example.knome;

import android.Manifest;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;


public class ShowActivity extends MainActivity implements FetchAddressTask.OnTaskCompleted {
    public FirebaseAuth mAuth;
    public final int REQUEST_LOCATION_PERMISSION = 1;
    public FusedLocationProviderClient mFusedLocationClient;
    public AnimatorSet mAnimRotate;
    public Boolean mTrackingLocation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        mAuth=FirebaseAuth.getInstance();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        TextView mName = findViewById(R.id.ShowName);
        TextView mEmail = findViewById(R.id.ShowEmail);
        TextView mLocation = findViewById(R.id.ShowLocation);
        Button mLogoutBtn = findViewById(R.id.logout);
        mAnimRotate = (AnimatorSet) AnimatorInflater.loadAnimator(this,R.animator.rotate);
        mAnimRotate.setTarget(mLocation);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
        if (!mTrackingLocation) {
            startTrackingLocation();
        } else {
            stopTrackingLocation();
        }
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (mTrackingLocation) {
                new FetchAddressTask(ShowActivity.this, ShowActivity.this)
                        .execute(locationResult.getLastLocation());
            }
        }
    };

    private void startTrackingLocation(){
        TextView mLocation = findViewById(R.id.ShowLocation);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            mTrackingLocation = true;
            mFusedLocationClient.requestLocationUpdates
                    (getLocationRequest(),
                            mLocationCallback,
                            null /* Looper */);
        /*    mFusedLocationClient.getLastLocation().addOnSuccessListener(
                    new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                new FetchAddressTask(ShowActivity.this,
                                        ShowActivity.this).execute(location);
                            } else {
                                mLocation.setText(R.string.no_location);
                            }
                        }
                    });  */
        }
        mLocation.setText(getString(R.string.address_text,getString(R.string.loading),System.currentTimeMillis()));
        mAnimRotate.start();
    }
    private void signOut() {

        mAuth.signOut();
        Intent intent = new Intent(ShowActivity.this,MainActivity.class);
        startActivity(intent);
    }
    private void stopTrackingLocation() {
        if (mTrackingLocation) {
            mTrackingLocation = false;
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            signOut();
            mAnimRotate.end();
        }
    }
    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    @Override
    public void onRequestPermissionsResult (int requestCode,
                                            @NonNull String[] permissions, @NonNull int[] grantResults){
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                // If the permission is granted, get the location,
                // otherwise, show a Toast
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startTrackingLocation();
                } else {
                    Toast.makeText(this, "Location permission required", Toast.LENGTH_SHORT).show();
                    signOut();
                }
                break;
        }
    }

    @Override
    public void onTaskCompleted(String result){
        TextView mLocation = findViewById(R.id.ShowLocation);
        if(mTrackingLocation == false){
            mLocation.setText(R.string.no_location);
        }else {
            mLocation.setText(getString(R.string.address_text, result, System.currentTimeMillis()));
        }
    }
}
