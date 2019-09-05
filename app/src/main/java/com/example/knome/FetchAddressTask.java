package com.example.knome;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FetchAddressTask extends AsyncTask<Location, Void, String> {
    private final String TAG = FetchAddressTask.class.getSimpleName();
    private Context mContext;
    private OnTaskCompleted mListener;
    FetchAddressTask (Context applicationContext, OnTaskCompleted listener){
        mContext = applicationContext;
        mListener = listener;
    }
    @Override
    protected String doInBackground(Location... locations) {
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        Location location = locations[0];
        List<Address> addresses = null;
        String resultMessage = "";
        try{
            addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
        }catch(IOException ioException){
            resultMessage = mContext.getString(R.string.service_not_avail);
            Log.e(TAG, resultMessage, ioException);
        }catch (IllegalArgumentException illegalArgumentException){
            resultMessage = mContext.getString(R.string.invalid_lat_long);
            Log.e(TAG, resultMessage + "." +
                    "Latitude =" + location.getLatitude() + ", Longitude =" + location.getLongitude(),
                    illegalArgumentException);
        }
        if(addresses == null || addresses.size() == 0){
            if(resultMessage.isEmpty()){
                resultMessage = mContext.getString(R.string.no_string_found);
                Log.e(TAG, resultMessage);
            }
        }
        else{
            Address address = addresses.get(0);
            ArrayList<String> addressString = new ArrayList<>();
            for(int i=0;i<=address.getMaxAddressLineIndex();i++){
                addressString.add(address.getAddressLine(i));
            }
            resultMessage= TextUtils.join("\n",addressString);
        }
        return resultMessage;
    }

    @Override
    protected void onPostExecute(String s) {
        mListener.onTaskCompleted(s);
        super.onPostExecute(s);
    }

    interface OnTaskCompleted{
        void onTaskCompleted(String result);
    }
}
