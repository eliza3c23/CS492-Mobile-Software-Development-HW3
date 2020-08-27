package com.example.android.lifecycleweather;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.SharedPreferences;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceFragmentCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.lifecycleweather.data.ForecastItem;
import com.example.android.lifecycleweather.data.WeatherPreferences;
import com.example.android.lifecycleweather.utils.NetworkUtils;
import com.example.android.lifecycleweather.utils.OpenWeatherMapUtils;

import com.example.android.lifecycleweather.data.ForecastRepo;



import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ForecastAdapter.OnForecastItemClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView mForecastLocationTV;
    private RecyclerView mForecastItemsRV;
    private ProgressBar mLoadingIndicatorPB;
    private TextView mLoadingErrorMessageTV;
    private ForecastAdapter mForecastAdapter;
    private SharedPreferences mPreferences;
    private ForecastViewModel mForecastViewModel;
    private ForecastRepo mForecastRepo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Remove shadow under action bar.
        getSupportActionBar().setElevation(0);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        WeatherPreferences.setLocation(mPreferences.getString("pref_location","Corvallis,US"));
        WeatherPreferences.setLocation(mPreferences.getString("pref_temp_units","@string/pref_temp_units_fahrenheit_value"));

        mForecastLocationTV = findViewById(R.id.tv_forecast_location);
        mForecastLocationTV.setText(WeatherPreferences.getDefaultForecastLocation());

        mLoadingIndicatorPB = findViewById(R.id.pb_loading_indicator);
        mLoadingErrorMessageTV = findViewById(R.id.tv_loading_error_message);
        mForecastItemsRV = findViewById(R.id.rv_forecast_items);

        mForecastAdapter = new ForecastAdapter(this);
        mForecastItemsRV.setAdapter(mForecastAdapter);
        mForecastItemsRV.setLayoutManager(new LinearLayoutManager(this));
        mForecastItemsRV.setHasFixedSize(true);


        mForecastViewModel = new ViewModelProvider(this).get(ForecastViewModel.class);
        mForecastViewModel.getSearchResults().observe(this, new Observer<String>(){
            @Override
            public void onChanged(@Nullable String s){
                mLoadingIndicatorPB.setVisibility(View.INVISIBLE);
                if(s!=null){
                    mLoadingErrorMessageTV.setVisibility(View.INVISIBLE);
                    mForecastItemsRV.setVisibility(View.VISIBLE);
                    ArrayList<ForecastItem> forecastItems = OpenWeatherMapUtils.parseForecastJSON(s);
                    mForecastAdapter.updateForecastItems(forecastItems);
                }else{
                    mForecastItemsRV.setVisibility(View.INVISIBLE);
                    mLoadingErrorMessageTV.setVisibility(View.VISIBLE);
                }

            }

        });

        //loadForecast();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mForecastRepo.updateStatus(getURL());
        mForecastLocationTV.setText(WeatherPreferences. getDefaultForecastLocation());
    }

    @Override
    public void onForecastItemClick(ForecastItem forecastItem) {
        Intent intent = new Intent(this, ForecastItemDetailActivity.class);
        intent.putExtra(OpenWeatherMapUtils.EXTRA_FORECAST_ITEM, forecastItem);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_location:
                showForecastLocation();
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
/*
    public void loadForecast() {
        String openWeatherMapForecastURL = OpenWeatherMapUtils.buildForecastURL(
                WeatherPreferences.getDefaultForecastLocation(),
                WeatherPreferences.getDefaultTemperatureUnits()
        );
        Log.d(TAG, "got forecast url: " + openWeatherMapForecastURL);
        new OpenWeatherMapForecastTask().execute(openWeatherMapForecastURL);
    }*/

    public String getURL(){
        String location = mPreferences.getString(
                getString(R.string.pref_location_key), getString(R.string.pref_location_default)
        );
        String units = mPreferences.getString(
                getString(R.string.pref_temp_units_key), getString(R.string.pref_temp_units_default)
        );
        WeatherPreferences.setLocation(location);
        WeatherPreferences.setUnits(units);

        String openWeatherMapForecastURL = OpenWeatherMapUtils.buildForecastURL(
                location,
                units
        );
        return openWeatherMapForecastURL;
    }

    public void showForecastLocation() {
        Uri geoUri = Uri.parse("geo:0,0").buildUpon()
                .appendQueryParameter("q", WeatherPreferences.getDefaultForecastLocation())
                .build();
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, geoUri);
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

    class OpenWeatherMapForecastTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicatorPB.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            String openWeatherMapURL = params[0];
            String forecastJSON = null;
            try {
                forecastJSON = NetworkUtils.doHTTPGet(openWeatherMapURL);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return forecastJSON;
        }

        @Override
        protected void onPostExecute(String forecastJSON) {
            mLoadingIndicatorPB.setVisibility(View.INVISIBLE);
            if (forecastJSON != null) {
                mLoadingErrorMessageTV.setVisibility(View.INVISIBLE);
                mForecastItemsRV.setVisibility(View.VISIBLE);
                ArrayList<ForecastItem> forecastItems = OpenWeatherMapUtils.parseForecastJSON(forecastJSON);
                mForecastAdapter.updateForecastItems(forecastItems);
            } else {
                mForecastItemsRV.setVisibility(View.INVISIBLE);
                mLoadingErrorMessageTV.setVisibility(View.VISIBLE);
            }
        }
    }
}
