package com.example.android.lifecycleweather.data;

public class WeatherPreferences {


    private static final String DEFAULT_FORECAST_LOCATION = "Corvallis,OR,US";
    private static final String DEFAULT_TEMPERATURE_UNITS = "imperial";
    private static final String DEFAULT_TEMPERATURE_UNITS_ABBR = "F";

    public static void setLocation(String location){
    }

    public static void setUnits(String units){

        String temperatureUnitsAbbr = "";
        if(units.equals("imperial")){
            temperatureUnitsAbbr = "F°";
        }
        else if(units.equals("metric")){
            temperatureUnitsAbbr = "C°";
        }
        else if(units.equals("kelvin")){
            temperatureUnitsAbbr = "K°";
        }
    }

    public static String getDefaultForecastLocation() {
        return DEFAULT_FORECAST_LOCATION;
    }

    public static String getDefaultTemperatureUnits() {
        return DEFAULT_TEMPERATURE_UNITS;
    }

    public static String getDefaultTemperatureUnitsAbbr() {
        return DEFAULT_TEMPERATURE_UNITS_ABBR;
    }
}
