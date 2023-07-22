package com.solderbyte.openfit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.solderbyte.openfit.HttpClient.AsyncResponse;
import com.solderbyte.openfit.util.OpenFitIntent;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Weather {
    private static final String LOG_TAG = "OpenFit:Weather";

    private static String APIKEY = "2022a7ed0f5215c3ecc7f5e683796f2d";
    private static String APIURL = "http://api.openweathermap.org/data/2.5/weather";
    private static String QUERY = "?";
    private static String AMP = "&";
    private static String UNITS = "units=";

    private static String units = "imperial"; //Fahrenheit = imperial, Celsius = metric, Default = Kelvin

    private static String WEATHER = "weather";
    private static String WEATHER_MAIN = "main";
    private static String WEATHER_DESC = "description";
    private static String WEATHER_ICON = "icon";
    private static String MAIN = "main";
    private static String MAIN_TEMP = "temp";
    private static String MAIN_PRES = "pressure";
    private static String MAIN_HUMD = "humidity";
    private static String MAIN_TMIN = "temp_min";
    private static String MAIN_TMAX = "temp_max";
    private static String NAME = "name";

    private static String name = null;
    private static String tempCur = null;
    private static String tempMin = null;
    private static String tempMax = null;
    private static String humidity = null;
    private static String pressure = null;
    private static String weather = null;
    private static String description = null;
    private static String icon = null;
    private static String tempUnit = null;

    private static HttpClient http = null;
    private static Context context;

    public static void init(Context cntxt) {
        Log.d(LOG_TAG, "Initializing Weather");
        context = cntxt;
        http = new HttpClient(cntxt);
    }

    public static void getWeather(String query, final String location) {
        Log.d(LOG_TAG, "Getting weather info for: " + query + " - " + location);
        http.get(APIURL + QUERY + query + AMP + UNITS + units + AMP + APIKEY, new AsyncResponse() {
            @Override
            public void callback(JSONObject res) {
                Log.d(LOG_TAG, "Weather callback");
                if(res != null) {
                    try {
                        JSONObject main = res.getJSONObject(MAIN);

                        if(main.has(MAIN_TEMP)) {
                            tempCur = main.getString(MAIN_TEMP);
                        }
                        if(main.has(MAIN_TMIN)) {
                            tempMin = main.getString(MAIN_TMIN);
                        }
                        if(main.has(MAIN_TMAX)) {
                            tempMax = main.getString(MAIN_TMAX);
                        }
                        if(main.has(MAIN_HUMD)) {
                            humidity = main.getString(MAIN_HUMD);
                        }
                        if(main.has(MAIN_PRES)) {
                            pressure = main.getString(MAIN_PRES);
                        }

                        if(units.equals("imperial")) {
                            tempUnit = "°F";
                        }
                        else if(units.equals("metric")) {
                            tempUnit = "°C";
                        }
                        else {
                            tempUnit = "K";
                        }

                        JSONArray w = res.getJSONArray(WEATHER);
                        for(int i = 0; i < w.length(); i++) {
                            JSONObject wo = w.getJSONObject(i);
                            weather = wo.getString(WEATHER_MAIN);
                            description = wo.getString(WEATHER_DESC);
                            icon = wo.getString(WEATHER_ICON);
                        }

                        name = res.getString(NAME);
                    }
                    catch(JSONException e) {
                        e.printStackTrace();
                    }

                    Intent msg = new Intent(OpenFitIntent.INTENT_SERVICE_WEATHER);
                    msg.putExtra("name", name);
                    msg.putExtra("weather", weather);
                    msg.putExtra("description", description);
                    msg.putExtra("tempCur", tempCur);
                    msg.putExtra("tempMin", tempMin);
                    msg.putExtra("tempMax", tempMax);
                    msg.putExtra("humidity", humidity);
                    msg.putExtra("pressure", pressure);
                    msg.putExtra("icon", icon);
                    msg.putExtra("tempUnit", tempUnit);
                    msg.putExtra("location", location);
                    context.sendBroadcast(msg);
                }
            }
        });
    }

    public static void setUnits(String unit) {
        units = unit;
    }

    public static String getUnits() {
        return units;
    }
}
