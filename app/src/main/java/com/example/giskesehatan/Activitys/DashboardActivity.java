package com.example.giskesehatan.Activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.viewpager2.widget.ViewPager2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.giskesehatan.Adapters.TempatKesehatanAdapter;
import com.example.giskesehatan.Helpers.AppConfig;
import com.example.giskesehatan.Helpers.GPSTracker;
import com.example.giskesehatan.Helpers.MyApiApplication;
import com.example.giskesehatan.Helpers.SharedPreference;
import com.example.giskesehatan.Interfaces.ApiServices;
import com.example.giskesehatan.Models.ApiResponse;
import com.example.giskesehatan.Models.ApiResponseWheater;
import com.example.giskesehatan.Models.CurrentWeather;
import com.example.giskesehatan.Models.Location;
import com.example.giskesehatan.Models.TempatKesehatanModel;
import com.example.giskesehatan.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DashboardActivity";
    private SharedPreference sharedPreference;

    //init komponent
    private AppCompatTextView tv_nm_lengkap, tv_email, tv_profile, tv_location, tv_temp;
    private CircleImageView img_user;

    //init component menu
    private CardView cv_puskesmas, cv_klinik, cv_rs, cv_rsia;

    //init tempat lokasi terkini
    private ViewPager2 vp_tempat_lokasi_terkini;
    private TempatKesehatanAdapter tempatKesehatanAdapter;
    private List<TempatKesehatanModel> tempatKesehatanModels;
    private ProgressDialog progressDialog;
    private ApiServices apiServices;
    private ScrollingPagerIndicator indicator_banner;

    private GPSTracker gpsTracker;

    private Handler handlerVp2 = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initComponents();

        if (sharedPreference.readSetting("id_user").isEmpty()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }

        setValueProfile();
        tv_profile.setOnClickListener(v -> profile());

        getDataTerkini();
        getWheater();
        cv_rs.setOnClickListener(this);
        cv_klinik.setOnClickListener(this);
        cv_puskesmas.setOnClickListener(this);
        cv_rsia.setOnClickListener(this);
    }

    private void getWheater() {
        ApiServices apiServicesWheather;
        String q        = gpsTracker.getLatitude() + "," + gpsTracker.getLongitude();
        String key      = getResources().getString(R.string.key_wheather);
        String host     = getResources().getString(R.string.host_wheather);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://weatherapi-com.p.rapidapi.com/")
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiServicesWheather = retrofit.create(ApiServices.class);

        Call<ApiResponseWheater> call = apiServicesWheather.getWheater(key,host,q);

        call.enqueue(new Callback<ApiResponseWheater>() {
            @Override
            public void onResponse(Call<ApiResponseWheater> call, Response<ApiResponseWheater> response) {
                ApiResponseWheater apiResponseWheater = response.body();
                Location location = apiResponseWheater.getLocation();
                tv_location.setText(location.getName());
                CurrentWeather currentWeather = apiResponseWheater.getCurrent();
                tv_temp.setText(String.valueOf(currentWeather.getTemp_c())+" \u2103");
            }

            @Override
            public void onFailure(Call<ApiResponseWheater> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t.getMessage());
            }
        });

    }

    private void getDataTerkini() {
        progressDialog.setMessage("Mohon tungu. . .");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String token = sharedPreference.readSetting("token");
        Call<ApiResponse<List<TempatKesehatanModel>>> call = apiServices.tempatKesehatanTerkini(AppConfig.keyToken(token));
        call.enqueue(new Callback<ApiResponse<List<TempatKesehatanModel>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<TempatKesehatanModel>>> call, Response<ApiResponse<List<TempatKesehatanModel>>> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    ApiResponse<List<TempatKesehatanModel>> apiResponse = response.body();
                    if (apiResponse.isStatus()) {
                        tempatKesehatanModels = apiResponse.getResult();
                        tempatKesehatanAdapter = new TempatKesehatanAdapter(DashboardActivity.this, tempatKesehatanModels,vp_tempat_lokasi_terkini, 1);
                        vp_tempat_lokasi_terkini.setAdapter(tempatKesehatanAdapter);
                        indicator_banner.attachToPager(vp_tempat_lokasi_terkini);
                    } else {
                        Toast.makeText(DashboardActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onResponse:" + apiResponse.getMessage());
                    }
                } else {
                    Toast.makeText(DashboardActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onResponse: success");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<TempatKesehatanModel>>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(DashboardActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onResponse:"+t.getMessage());
            }
        });
    }

    private void profile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void setValueProfile() {
        tv_email.setText(sharedPreference.readSetting("email"));
        tv_nm_lengkap.setText(AppConfig.capitalizeFirstLetter(sharedPreference.readSetting("nama_lengkap")));
        Glide.with(this)
                .load(AppConfig.BASE_URL_IMG_USER + sharedPreference.readSetting("img_user"))
                .centerCrop()
                .placeholder(R.drawable.girl)
                .into(img_user);
    }

    private void initComponents() {
        tv_email                    = findViewById(R.id.tv_email_user);
        tv_profile                  = findViewById(R.id.tv_profile);
        tv_nm_lengkap               = findViewById(R.id.tv_nm_lengkap);
        img_user                    = findViewById(R.id.iv_user_dashboard);
        vp_tempat_lokasi_terkini    = findViewById(R.id.rv_terkini);
        cv_klinik                   = findViewById(R.id.cv_rsia);
        cv_rsia                     = findViewById(R.id.cv_rs);
        cv_puskesmas                = findViewById(R.id.cv_puskesmas);
        cv_rs                       = findViewById(R.id.cv_klinik);
        indicator_banner            = findViewById(R.id.indicator_banner);
        tv_location                 = findViewById(R.id.tv_location);
        tv_temp                     = findViewById(R.id.tv_temp);
        sharedPreference            = new SharedPreference(this);
        progressDialog              = new ProgressDialog(this);
        gpsTracker                  = new GPSTracker(this);

        vp_tempat_lokasi_terkini.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                handlerVp2.removeCallbacks(sliderRunable);
                handlerVp2.postDelayed(sliderRunable,2500);
            }
        });

        MyApiApplication myApiApplication = (MyApiApplication) getApplication();
        apiServices = myApiApplication.getApiService();
    }

    private Runnable sliderRunable = new Runnable() {
        @Override
        public void run() {
            vp_tempat_lokasi_terkini.setCurrentItem(vp_tempat_lokasi_terkini.getCurrentItem() + 1);
        }
    };

    @Override
    public void onClick(View view) {

        Intent intent = new Intent(this, LayananKesehatanActivity.class);

        int id = view.getId();

        if (id == R.id.cv_puskesmas)
            intent.putExtra("title", "puskesmas");
        else if (id == R.id.cv_rs)
            intent.putExtra("title", "rumah sakit");
        else if (id == R.id.cv_rsia)
            intent.putExtra("title", "rumah sakit ibu anak");
        else
            intent.putExtra("title", "klinik");

        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onPause() {
        super.onPause();
        vp_tempat_lokasi_terkini.removeCallbacks(sliderRunable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        vp_tempat_lokasi_terkini.postDelayed(sliderRunable,2500);
    }
}