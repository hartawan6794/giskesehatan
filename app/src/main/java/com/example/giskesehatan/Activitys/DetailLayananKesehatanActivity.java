package com.example.giskesehatan.Activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.example.giskesehatan.Helpers.AppConfig;
import com.example.giskesehatan.Helpers.GPSTracker;
import com.example.giskesehatan.Models.TempatKesehatanModel;
import com.example.giskesehatan.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class DetailLayananKesehatanActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleMap.OnMapClickListener, LocationListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "DetailLayananKesehatanActivity";

    //init layout_head
    private AppCompatImageView iv_img_layanan, iv_back, btn_call, btn_call_wa;
    private AppCompatTextView tv_name, tv_deskripsi, tv_kecamatan;
    private AppCompatButton btn_view_map;
    //init google map
    private GoogleMap mMap;
    GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Marker mCurrLocationMarker;
    private Location mLastLocation;
    private GPSTracker gpsTracker;
    private Double latitude,longitude;
    private TempatKesehatanModel tempatKesehatanModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_layanan_kesehatan);

        initComponents();

        iv_back.setOnClickListener(v -> onBackPressed());

        checkLocationPermission();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getDataIntent();
    }

    private void getDataIntent() {
        Intent intent = getIntent();
        tempatKesehatanModel = (TempatKesehatanModel) intent.getSerializableExtra("model");
        String text = tempatKesehatanModel.getGambar();
        int pos = text.indexOf("-");
        String result = text.substring(0, pos);
        Glide.with(this)
                .load(AppConfig.BASE_URL_IMG+result+"/"+tempatKesehatanModel.getGambar())
                .centerCrop()
                .placeholder(R.drawable.klinik_example)
                .into(iv_img_layanan);
        tv_name.setText(tempatKesehatanModel.getNama());
        tv_deskripsi.setText(tempatKesehatanModel.getDeskripsi());
        tv_kecamatan.setText("Kecamatan : "+tempatKesehatanModel.getKecamatan());
    }

    private void initComponents() {
        iv_img_layanan = findViewById(R.id.iv_img_layanan);
        iv_back = findViewById(R.id.iv_back);
        btn_call = findViewById(R.id.btn_call);
        btn_call_wa = findViewById(R.id.btn_call_wa);
        tv_name = findViewById(R.id.tv_name);
        tv_deskripsi = findViewById(R.id.tv_deskripsi);
        tv_kecamatan = findViewById(R.id.tv_kecamatan);
        btn_view_map = findViewById(R.id.btn_view_map);
        gpsTracker = new GPSTracker(this);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latLng = new LatLng(tempatKesehatanModel.getLatitude(), tempatKesehatanModel.getLongitude());
        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latLng.latitude, latLng.longitude)).zoom(13).build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

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
                        .position(latLng)
                        .title(tempatKesehatanModel.getNama())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));

        // Geser peta ke lokasi yang diklik
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

//        googleMap.setOnMapClickListener(this);
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

    //mendapatkan permission request location
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }
}