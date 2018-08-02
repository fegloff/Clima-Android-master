package com.londonappbrewery.climapm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class WeatherController extends AppCompatActivity {

    // Constants:

    // To identify and track or permission request we set a REQUEST CODE
    final int REQUEST_CODE = 123;
    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
    //http://api.openweathermap.org/data/2.5/weather?lat=4.71972&lon=-74.04900&APPID=3031f0e14e8d45bd0d4f8dce4f02eb5f
    // App ID to use OpenWeather data
    final String APP_ID = "3031f0e14e8d45bd0d4f8dce4f02eb5f";
    // Time between location updates (5000 milliseconds or 5 seconds)
    final long MIN_TIME = 5000;
    // Distance between location updates (1000m or 1km)
    final float MIN_DISTANCE = 1000;

    // TODO: Set LOCATION_PROVIDER here:
    String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;

    // Member Variables:
    TextView mCityLabel;
    ImageView mWeatherImage;
    TextView mTemperatureLabel;

    // TODO: Declare a LocationManager and a LocationListener here:
    //Component who start or stop requesting location updates
    LocationManager mLocationManager;

    // component who will be notified if the location already change
    LocationListener mLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_controller_layout);

        // Linking the elements in the layout to Java code
        mCityLabel = (TextView) findViewById(R.id.locationTV);
        mWeatherImage = (ImageView) findViewById(R.id.weatherSymbolIV);
        mTemperatureLabel = (TextView) findViewById(R.id.tempTV);
        ImageButton changeCityButton = (ImageButton) findViewById(R.id.changeCityButton);


        // TODO: Add an OnClickListener to the changeCityButton here:
        changeCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(WeatherController.this,ChangeCityController.class);
                startActivity(myIntent);
            }
        });
    }


    // TODO: Add onResume() here:
    @Override
    // onResume its execute just after onCreate but before the user can interact with the activiry
    protected void onResume() {
        super.onResume();
        Log.d("Clima", "OnResume called");

        Intent myIntent = getIntent();
        String city = myIntent.getStringExtra("City");

        if (city != null) {

        } else {
            Log.d("Clima", "Getting weather for current location");
            getWeatherForCurrentLocation();
        }

    }


    // TODO: Add getWeatherForNewCity(String city) here:
    private void getWeatherForNewCity(String city) {
        RequestParams params = new RequestParams();
        params.put("q", city);
        params.put("appid", APP_ID);
        letsDoSomeNetworking(params);
    }

    // TODO: Add getWeatherForCurrentLocation() here:
    private void getWeatherForCurrentLocation() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("Clima", "onLocationChanged() callback received");
                String longitude = String.valueOf(location.getLongitude());
                String latitude = String.valueOf(location.getLatitude());
                Log.d("Clima", "The longitud is " + longitude);
                Log.d("Clima", "The latitud is " + latitude);

                //Class that comes from 'com.loopj.android:android-async-http:1.4.9'
                RequestParams params = new RequestParams();
                params.put("lat", latitude);
                params.put("lon", longitude);
                params.put("appid", APP_ID);
                letsDoSomeNetworking(params);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("Clima", "onStatusChanged() callback received");

            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d("Clima", "onProviderEnabledChanged() callback received");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("Clima", "onProviderDisable() callback received");
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;
        }
        mLocationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {

            if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Clima","onRequestPermissionResult(): PERMISSION GRANTED!");
                getWeatherForCurrentLocation();;
            } else {
                Log.d("Clima","Permission Denied!");
            }

        }
    }

    // TODO: Add letsDoSomeNetworking(RequestParams params) here:
    private void letsDoSomeNetworking(RequestParams params) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(WEATHER_URL,params, new JsonHttpResponseHandler() {
           @Override
           public void onSuccess (int statusCode, Header[] headers, JSONObject response) {
               Log.d("Clima","Success: " + response.toString());
               WeatherDataModel weatherData = WeatherDataModel.fromJson(response);
               updateUI(weatherData);

           }

            @Override
            public void onFailure (int statusCode, Header[] headers, Throwable e, JSONObject response) {
               Log.e("Clima", "StatusCode: " + statusCode);
               Log.e("Clima","FAIL " + e.toString());
               Toast.makeText(WeatherController.this,"Request Fail",Toast.LENGTH_SHORT).show();

            }
        });
    }


    // TODO: Add updateUI() here:
    private void updateUI (WeatherDataModel weatherData) {
        mCityLabel.setText(weatherData.getCity());
        mTemperatureLabel.setText(weatherData.getTemperature());
        int resourceID = getResources().getIdentifier(weatherData.getIconName(),"drawable",getPackageName());
        mWeatherImage.setImageResource(resourceID);
    }


    // TODO: Add onPause() here:


    @Override
    //We are overriding this method in order to free some resources
    protected void onPause() {
        super.onPause();
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }
}
