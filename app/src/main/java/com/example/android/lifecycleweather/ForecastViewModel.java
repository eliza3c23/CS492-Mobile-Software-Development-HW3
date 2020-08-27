package com.example.android.lifecycleweather;

import android.util.Log;
import android.widget.ProgressBar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import androidx.lifecycle.ViewModel;

public class ForecastViewModel extends ViewModel {
    private MutableLiveData<String> mSearchResults;
    private String mURL;
    private ProgressBar mLoadingIndicatorPB;

    public ForecastViewModel(ProgressBar loadingIndicatorPB ,String url){

        mSearchResults = new MutableLiveData<String>();
        mSearchResults.setValue(null);
        mURL = url;
        mLoadingIndicatorPB = loadingIndicatorPB;
    }

    public LiveData<String> getSearchResults(){
        return mSearchResults;
    }

}
