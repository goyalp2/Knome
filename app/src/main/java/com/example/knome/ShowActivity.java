package com.example.knome;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class ShowActivity extends MainActivity implements FetchAddressTask.OnTaskCompleted {
    public FirebaseAuth mAuth;
    public final int REQUEST_LOCATION_PERMISSION = 1;
    public FusedLocationProviderClient mFusedLocationClient;
    public Boolean mTrackingLocation = false;
    private static final String TAG = "MainActivity";
    LocationManager locationManager ;
    boolean GpsStatus ;

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
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        CheckGpsStatus();
        if(GpsStatus == true){
            Toast.makeText(ShowActivity.this, "Keep GPS on", Toast.LENGTH_SHORT).show();
        }else{
            signOut();
            signOut_google();
            signOut_fb();
        }
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("34553582650-v0ntcv1f4bv0cjg7frsqmtu2tpkt5eir.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
                signOut_google();
                signOut_fb();
            }
        });

        mName.setText(getIntent().getStringExtra("name"));
        mEmail.setText(getIntent().getStringExtra("email"));

        if (!mTrackingLocation) {
            startTrackingLocation();
        } else {
            stopTrackingLocation();
        }

    }
    public void onDestroy(){
        super.onDestroy();
        this.finish();
    }
    @Override
    public void onBackPressed() {
    /*    mAuth.signOut();
        LoginManager.getInstance().logOut();
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        revokeAccess();
                    }
                });
    */    this.finish();
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
        }
        mLocation.setText(getString(R.string.address_text,getString(R.string.loading),System.currentTimeMillis()));
    }
    private void signOut() {

        mAuth.signOut();
        this.finish();
        //Intent intent = new Intent(ShowActivity.this,MainActivity.class);
        //startActivity(intent);
    }
    private void signOut_google() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        revokeAccess();
                    }
                });
        this.finish();
    }
    private void signOut_fb(){
        LoginManager.getInstance().logOut();
        this.finish();
    }
    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }
    private void stopTrackingLocation() {
        if (mTrackingLocation) {
            mTrackingLocation = false;
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            signOut();
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

    public void CheckGpsStatus(){

        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

}
