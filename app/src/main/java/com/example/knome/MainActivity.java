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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends ProgressActivity {
    public FirebaseAuth mAuth;
    @BindView(R.id.signup) Button mSignupBtn;
    @BindView(R.id.email) EditText mLoginEmail;
    @BindView(R.id.password) EditText mLoginPass;
    private static final String TAG = "MainActivity";
    LocationManager locationManager ;
    Context context;
    boolean GpsStatus ;
    private int RC_SIGN_IN = 1;
    public GoogleSignInClient mGoogleSignInClient;
    public CallbackManager mCallbackManager;
    SignInButton mGoogleBtn;

    public String personName;
    public String personEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        Button mLoginBtn = findViewById(R.id.login);
        mGoogleBtn = findViewById(R.id.Gsign_in_Btn);
        LoginButton mFbBtn = findViewById(R.id.fb_login_button);
        mFbBtn.setReadPermissions("email", "public_profile");
        context = getApplicationContext();
        ButterKnife.bind(MainActivity.this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("34553582650-v0ntcv1f4bv0cjg7frsqmtu2tpkt5eir.apps.googleusercontent.com")
                .requestEmail()
                .requestProfile()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mCallbackManager = CallbackManager.Factory.create();
        mFbBtn.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("Main", response.toString());
                                setProfileToView(object);
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }


            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });



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
                if (i == R.id.login) {
                    hideKeyboard(view);
                    signIn(mLoginEmail.getText().toString(), mLoginPass.getText().toString());
                }
            }
        });
        mGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();
            }
        });

    }
    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser currentUser=mAuth.getCurrentUser();
        updateUI(currentUser);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI_google(account);
    }

    public void onDestroy(){
        super.onDestroy();
        this.finish();
    }
    @Override
    public void onBackPressed() {
       this.finish();
    }

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            CheckGpsStatus() ;
                            if (GpsStatus == true) {
                                Intent intent = new Intent(MainActivity.this, ShowActivity.class);
                                intent.putExtra("name",personName);
                                intent.putExtra("email",personEmail);
                                startActivity(intent);
                            } else{
                                Toast.makeText(MainActivity.this,"Please turn on the location",Toast.LENGTH_SHORT).show();
                                updateUI(user);
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);
            // Signed in successfully, show authenticated UI.
            updateUI_google(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI_google(null);
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        showProgressDialog();
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(MainActivity.this);
                            if (acct != null) {
                                personName = acct.getDisplayName();
                                personEmail = acct.getEmail();
                            }
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            CheckGpsStatus() ;
                            if (GpsStatus == true) {
                                Intent intent = new Intent(MainActivity.this, ShowActivity.class);
                                intent.putExtra("name",personName);
                                intent.putExtra("email",personEmail);
                                startActivity(intent);
                            } else{
                                Toast.makeText(MainActivity.this,"Please turn on the location",Toast.LENGTH_SHORT).show();
                                updateUI_google(acct);
                            }


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI_google(null);
                        }

                        // ...
                    }
                });
        hideProgressDialog();
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
                            personEmail = mLoginEmail.getText().toString();
                            CheckGpsStatus() ;
                            if (GpsStatus == true) {
                                Intent intent = new Intent(MainActivity.this, ShowActivity.class);
                                intent.putExtra("email",personEmail);
                                startActivity(intent);
                            } else{
                                hideProgressDialog();
                                Toast.makeText(MainActivity.this,"Please turn on the location",Toast.LENGTH_SHORT).show();

                            }

                            updateUI(user);

                        }

                        else {

                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());

                            Toast.makeText(MainActivity.this,"Email or password invalid", Toast.LENGTH_SHORT).show();
                            updateUI(null);

                        }


                        // [START_EXCLUDE]

                        if (!task.isSuccessful()) {

                            Log.w(TAG, "signUpWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this,"Sign-in Unsuccessful",Toast.LENGTH_SHORT).show();
                            updateUI(null);
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
        }
    }

    private void updateUI_google(GoogleSignInAccount account) {
        hideProgressDialog();
        if(account == null) {
            mGoogleBtn.setVisibility(View.VISIBLE);
        }
    }
    public void CheckGpsStatus(){

        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void setProfileToView(JSONObject jsonObject) {
        try {
            personName = jsonObject.getString("name");
            personEmail = jsonObject.getString("email");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
