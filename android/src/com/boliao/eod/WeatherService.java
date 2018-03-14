package com.boliao.eod;

import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class WeatherService extends Service implements LocationListener{
    private static final String TAG = "WeatherService";
    public static final String WEATHER_BROADCAST_ACTION = "com.boliao.eod.WEATHER_FORECAST";
    public static final String WEATHER_BROADCAST_EXTRAS_FORECAST = "forecast";
    public static final String WEATHER_BROADCAST_EXTRAS_LOCATION = "location";

    String urlStr = "https://api.data.gov.sg/v1/environment/2-hour-weather-forecast?date=2018-03-13";

    // location services
    private FusedLocationProviderClient locationClient;
    LocationManager locationManager;

    // init thread
    private WeatherWorkThread thread;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate(): starting worker thread");
        super.onCreate();

        // TODO NETWORKING
        // init location client
        locationManager = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
        catch (SecurityException e) {
            Log.d(TAG, e.getLocalizedMessage());
        }

        // init worker thread in background
        thread = new WeatherWorkThread();
        thread.start();
        thread.prepareHandler();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO NETWORKING
        // get weather from REST api in a background task placed on the handlerthread
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    String jsonStr = downloadUrlHTTP(urlStr);
                    Log.i(TAG, "DOWNLOADED json:\n" + jsonStr);

                    String forecastStr = getForecastFrom(jsonStr);
                    Log.i(TAG, "forecast from REST is " + forecastStr);

                    // TODO NETWORKING
                    // send LOCAL broadcast after done
                    Intent bc_intent = new Intent(WEATHER_BROADCAST_ACTION);
                    bc_intent.putExtra(WEATHER_BROADCAST_EXTRAS_FORECAST, forecastStr);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(bc_intent);
                } catch (IOException e) {
                    Log.d(TAG, e.getLocalizedMessage());
                }
            }
        };
        thread.postTask(r);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        thread.quit();
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Log.i(TAG, "Location is (" + location.getLatitude() + "," + location.getLongitude() + ")" );

            // get address from latlong
            Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                List<Address> addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses.size() > 0) {
                    Log.i(TAG, "LOCATION from latlong is " + addresses.get(0).getThoroughfare());
                }
                else
                    Log.i(TAG, "LOCATION from latlong error");
            } catch (IOException e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
        }
        else
            Log.d(TAG, "LOCATION iS NULL");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    /**
     * TODO NETWORKING
     * Boilerplate method given in slides.
     * @param urlStr
     * @return
     * @throws IOException
     */
    // Given a URL, establishes an HttpUrlConnection and retrieves
    // the the Json content and maybe return a String? (you can make it return whatever)
    private String downloadUrlHTTP(String urlStr) throws IOException {
        // do the download here

        // form the URL
        URL url = new URL(urlStr);

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            // send the request
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(15000);
            urlConnection.setReadTimeout(15000);
            urlConnection.connect();

            // get response
            //String response = null;
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                StringBuffer response = new StringBuffer();
                do {
                    line = in.readLine();
                    response.append(line);
                } while ( line != null);
                in.close();
                return response.toString();
            }
            else {
                return "ERROR http connection code=" + urlConnection.getResponseCode();
            }
        }
        finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    /**
     * TODO NETWORKING
     * TODO include find name from list
     * - note that this method may take some time if the array of data is huge and processing needs
     * to be done, e.g., server returns per second data of whole day across 50 states in US
     * Extract the forecast info we need from the json response.
     * @param jsonStr the json string
     */
    private String getForecastFrom(String jsonStr) {
        try {
            JSONObject json = new JSONObject(jsonStr);
            JSONArray forecasts = json.getJSONArray("items")
                                        .getJSONObject(0)
                                        .getJSONArray("forecasts");
            String forecastStr = forecasts.getJSONObject(0).getString("forecast");
            return forecastStr;
        } catch (JSONException e) {
            Log.d(TAG, "json exception:" + e.getLocalizedMessage());
        }
        return null;
    }
}
