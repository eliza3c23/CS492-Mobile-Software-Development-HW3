package com.example.android.lifecycleweather.data;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.util.List;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import android.widget.ProgressBar;
import android.os.AsyncTask;

import com.example.android.lifecycleweather.ForecastAdapter;
import com.example.android.lifecycleweather.ForecastItemDetailActivity;
import com.example.android.lifecycleweather.MainActivity;
import com.example.android.lifecycleweather.utils.NetworkUtils;
import com.example.android.lifecycleweather.ForecastViewModel;
import com.example.android.lifecycleweather.utils.OpenWeatherMapUtils;

public class ForecastRepo extends ViewModel{
    private final static String TAG = ForecastViewModel.class.getSimpleName();
    private MutableLiveData<String> mSearchResults;
    private String mURL;
    private ProgressBar mLoadingIndicatorPB;

    private void loadSearchResults(){
        new AsyncTask<Void,Void,String>(){
            protected void OnPreExecute(){
                super.onPreExecute();
                mLoadingIndicatorPB.setVisibility(View.VISIBLE);
            }
            @Override
            protected String doInBackground(Void...voids){
                String forecastJSON = null;
                try {
                    forecastJSON = NetworkUtils.doHTTPGet(mURL);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return forecastJSON;
            }
            @Override
            protected void onPostExecute (String forecastJSON){
                mSearchResults.setValue(forecastJSON);
            }
        }
        .execute();
    }
    public void updateStatus(String url){
        if(!mURL.equals(url)){
            Log.d(TAG,"Unable to load");
            mURL = url;
            loadSearchResults();
        }else {
            if(mSearchResults.getValue() == " "){
                loadSearchResults();
            }
        }

    }


}
