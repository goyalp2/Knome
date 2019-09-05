package com.example.knome;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.util.VisibleForTesting;

public class ProgressActivity extends AppCompatActivity {
    @VisibleForTesting
    public ProgressDialog mProgressDialog;
    public ProgressDialog mSignInProgress;
    public void showProgressDialog() {

        if (mProgressDialog == null) {

            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.signing_up));
            mProgressDialog.setIndeterminate(true);

        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {

        if (mProgressDialog != null && mProgressDialog.isShowing()) {

            mProgressDialog.dismiss();
            Toast.makeText(this,"hideProgressDialog",Toast.LENGTH_SHORT).show();
         //   mProgressDialog.hide();
          //  mProgressDialog.dismiss();

        }

    }
    public void showSignInDialog(){
        if(mSignInProgress == null){
            mSignInProgress = new ProgressDialog(this);
            mSignInProgress.setMessage("Signing-in");
            mSignInProgress.setIndeterminate(true);
        }

        mSignInProgress.show();
    }






    public void hideKeyboard(View view) {

        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null) {

            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        }

    }



    @Override

    public void onStop() {

        super.onStop();

        hideProgressDialog();

    }



}
