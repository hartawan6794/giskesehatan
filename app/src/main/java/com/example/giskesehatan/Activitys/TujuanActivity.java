package com.example.giskesehatan.Activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.giskesehatan.Helpers.GPSTracker;
import com.example.giskesehatan.Interfaces.ApiServices;
import com.example.giskesehatan.Models.Leg;
import com.example.giskesehatan.Models.Route;
import com.example.giskesehatan.Models.RoutesResponseModel;
import com.example.giskesehatan.Models.Step;
import com.example.giskesehatan.Models.TempatKesehatanModel;
import com.example.giskesehatan.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TujuanActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleMap.OnMapClickListener, LocationListener, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "TujuanActivity";

    //init location teks
    private AppCompatImageView iv_back, btn_call, btn_call_wa;
    private AppCompatTextView tv_destination_location, tv_current_location, tv_est_duration;
    //init google map
    private GoogleMap mMap;
    GoogleApiClient googleApiClient;
    private TempatKesehatanModel tempatKesehatanModel;
    private GPSTracker gpsTracker;
    List<LatLng> decodedPolyline = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tujuan);

        Intent intent = getIntent();
        tempatKesehatanModel = (TempatKesehatanModel) intent.getSerializableExtra("model");

        gpsTracker = new GPSTracker(this);

        initComponents();

        btn_call_wa.setOnClickListener(v -> callWa());
        btn_call.setOnClickListener(v -> caller());
        iv_back.setOnClickListener(V -> onBackPressed());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void caller() {
        if(checkPhonePermission()){
            String dial = "tel:" + tempatKesehatanModel.getNotelp();
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }else {
            checkPhonePermission();
        }
    }

    private void callWa() {
        if (tempatKesehatanModel.getNotelp().length() < 11) {
            Toast.makeText(this, "Nomor wa ini belum diset", Toast.LENGTH_SHORT).show();
        } else {
            sendMessageWhatsApp(tempatKesehatanModel.getNotelp());
        }
    }


    private void sendMessageWhatsApp(@NonNull String notelp) {
        String formatPhoneNumber = "+62" + notelp.substring(1);
        startActivity(
                new Intent(Intent.ACTION_VIEW, Uri.parse(
                        String.format("https://api.whatsapp.com/send?phone=%s", formatPhoneNumber)
                )
                )
        );
    }
    private void initComponents() {
        iv_back                     = findViewById(R.id.iv_back);
        tv_current_location         = findViewById(R.id.tv_current_location);
        tv_destination_location     = findViewById(R.id.tv_destination_location);
        tv_est_duration             = findViewById(R.id.tv_est_duration);
        btn_call                    = findViewById(R.id.btn_call);
        btn_call_wa                 = findViewById(R.id.btn_call_wa);
        tv_destination_location.setText(tempatKesehatanModel.getNama());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap; // Set up your current location
        LatLng currentLocation = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());

        LatLng destination = new LatLng(tempatKesehatanModel.getLatitude(), tempatKesehatanModel.getLongitude());
        // Move the camera to a position between the current location and the destination
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(currentLocation);
        builder.include(destination);
        LatLngBounds bounds = builder.build();
        int padding = 100; // Padding around the bounds in pixels
//        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));

        //Memulai Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        mMap.addMarker(new MarkerOptions()
                .position(destination)
                .title(tempatKesehatanModel.getNama())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));

        getRoutes(currentLocation, destination, googleMap);

        googleMap.setOnMapClickListener(this);
    }


    private void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        googleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    public void getRoutes(LatLng currentLocation, LatLng destination, GoogleMap googleMap) {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(loggingInterceptor); // Tambahkan interceptor logging ke HttpClient
        String origin = currentLocation.latitude + "," + currentLocation.longitude;
        String desti = destination.latitude + "," + destination.longitude;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiServices service = retrofit.create(ApiServices.class);
        Call<RoutesResponseModel> call = service.getDirection(origin, desti, getResources().getString(R.string.my_api_maps_direction), "driving");
        call.enqueue(new Callback<RoutesResponseModel>() {
            @Override
            public void onResponse(Call<RoutesResponseModel> call, Response<RoutesResponseModel> response) {
                RoutesResponseModel routeResponse = response.body();
                double totalDistance = 0.0;
                int totalDuration = 0;
                Log.d(TAG, "routeResponse: " + response.body());
                List<Route> routes = routeResponse.getRoutes();
                for (Route route : routes) {
                    List<Leg> legs = route.getLegs();
                    for (Leg leg : legs) {
                        double segmentDistance = leg.getDistance().getValue();
                        int segmentDuration = leg.getDuration().getValue();

                        totalDistance += segmentDistance;
                        totalDuration += segmentDuration;
                        List<Step> steps = leg.getSteps();
                        for (Step step : steps) {
                            decodedPolyline.addAll(decodePoly(step.getPolyline().getPoints()));

                        }
                    }
                }

                String est = "Est. Waktu "+(totalDuration / 60)+" Menit";
                String estJarak = "Est. Jarak "+(totalDistance / 1000)+" Km";

                tv_est_duration.setText(est);

                PolylineOptions polylineOptions = new PolylineOptions()
                        .addAll(decodedPolyline)
                        .width(20) // Lebar jalur
                        .color(getResources().getColor(R.color.primay))
                        .geodesic(true); // Warna jalur
                Polyline polyline = googleMap.addPolyline(polylineOptions);

            }

            @Override
            public void onFailure(Call<RoutesResponseModel> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });

    }

    //mendapatkan permission request call phone
    public static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 98;

    public boolean checkPhonePermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.CALL_PHONE)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_CALL_PHONE);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_CALL_PHONE);
            }
            return false;
        } else {
            return true;
        }
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}