package com.example.weatherpro;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    final String API_KEY = "459f1792f081b9fbe1a59546803cfd87";
    final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather";

    final long TIME = 5000;
    final int REQUEST_CODE = 101;
    final float DISTANCE = 1000;

    String locationProvider = LocationManager.GPS_PROVIDER;

    TextView nameOfCity, weatherState, temperature;
    ImageView weatherIcon;

    RelativeLayout cityFinder;

    LocationManager mLocationManager;
    LocationListener mLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Variablen einen Wert geben
        weatherState = findViewById(R.id.weatherCondition);
        temperature = findViewById(R.id.temperature);
        weatherIcon = findViewById(R.id.weatherIcon);
        cityFinder = findViewById(R.id.cityFinder);
        nameOfCity = findViewById(R.id.cityName);

        cityFinder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, cityFinder.class);
                startActivity(intent);
            }
        });


    }

    /* @Override
    protected void onResume() {
        super.onResume();
        getWeatherForCurrentLocation();
    }*/

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        String city = intent.getStringExtra("City");
        if(city != null) {
            getWeatherForNewCity(city);
        } else {
            getWeatherForCurrentLocation();
        }
    }

    private void getWeatherForNewCity(String city) {
        RequestParams parameters = new RequestParams();
        parameters.put("q", city);
        parameters.put("appid", API_KEY);
        networking(parameters);
    }

    private void getWeatherForCurrentLocation() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                String latitude = String.valueOf(location.getLatitude());
                String longitude = String.valueOf(location.getLongitude());

                RequestParams parameters = new RequestParams();
                parameters.put("lat", latitude);
                parameters.put("lon", longitude);
                parameters.put("appid", API_KEY);

                networking(parameters);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                //Not Able to get Location
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
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.FUSED_PROVIDER, TIME, DISTANCE, mLocationListener);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode ==REQUEST_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Location wurde erfolgreich ermittelt", Toast.LENGTH_LONG).show();
                getWeatherForCurrentLocation();
            } else {
                //User hat die Permission Abgelehnt
            }
        }
    }
    private void networking(RequestParams parameters) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(WEATHER_URL, parameters, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Toast.makeText(MainActivity.this, "Daten wurden erfolgreich ermittelt", Toast.LENGTH_SHORT).show();
                Data data = Data.fromJson(response);
                updateUI(data);
                //super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                //super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    private void updateUI(Data data) {
        temperature.setText(data.getmTemperature());
        nameOfCity.setText(data.getmCity());
        weatherState.setText(data.getmWeatherType());
        int resourceID=getResources().getIdentifier(data.getmIcon(), "drawable", getPackageName());
        weatherIcon.setImageResource(resourceID);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mLocationManager!= null) {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }
}