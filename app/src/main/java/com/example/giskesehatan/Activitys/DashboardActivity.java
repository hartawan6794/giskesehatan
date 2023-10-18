package com.example.giskesehatan.Activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.viewpager2.widget.ViewPager2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.giskesehatan.Adapters.TempatKesehatanAdapter;
import com.example.giskesehatan.Helpers.AppConfig;
import com.example.giskesehatan.Helpers.MyApiApplication;
import com.example.giskesehatan.Helpers.SharedPreference;
import com.example.giskesehatan.Interfaces.ApiServices;
import com.example.giskesehatan.Models.ApiResponse;
import com.example.giskesehatan.Models.TempatKesehatanTerkiniModel;
import com.example.giskesehatan.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DashboardActivity";
    private SharedPreference sharedPreference;

    //init komponent
    private AppCompatTextView tv_nm_lengkap, tv_email, tv_profile;
    private CircleImageView img_user;

    //init component menu
    private CardView cv_puskesmas, cv_klinik, cv_rs, cv_rsia;

    //init tempat lokasi terkini
    private ViewPager2 vp_tempat_lokasi_terkini;
    private TempatKesehatanAdapter tempatKesehatanAdapter;
    private ProgressDialog progressDialog;
    private ApiServices apiServices;
    private ScrollingPagerIndicator indicator_banner;

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

        MyApiApplication myApiApplication = (MyApiApplication) getApplication();
        apiServices = myApiApplication.getApiService();

        setValueProfile();
        tv_profile.setOnClickListener(v -> profile());

        getDataTerkini();

        cv_rs.setOnClickListener(this);
        cv_klinik.setOnClickListener(this);
        cv_puskesmas.setOnClickListener(this);
        cv_rsia.setOnClickListener(this);
    }

    private void getDataTerkini() {
        progressDialog.setMessage("Mohon tungu. . .");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String token = sharedPreference.readSetting("token");
        Call<ApiResponse<List<TempatKesehatanTerkiniModel>>> call = apiServices.tempatKesehatanTerkini(AppConfig.keyToken(token));
        call.enqueue(new Callback<ApiResponse<List<TempatKesehatanTerkiniModel>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<TempatKesehatanTerkiniModel>>> call, Response<ApiResponse<List<TempatKesehatanTerkiniModel>>> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    ApiResponse<List<TempatKesehatanTerkiniModel>> apiResponse = response.body();
                    if (apiResponse.isStatus()) {
                        List<TempatKesehatanTerkiniModel> tempatKesehatanTerkiniModels = apiResponse.getResult();
                        tempatKesehatanAdapter = new TempatKesehatanAdapter(DashboardActivity.this, tempatKesehatanTerkiniModels, vp_tempat_lokasi_terkini);
                        vp_tempat_lokasi_terkini.setAdapter(tempatKesehatanAdapter);
                        indicator_banner.attachToPager(vp_tempat_lokasi_terkini);
                    } else {
                        Log.d(TAG, "onResponse:" + apiResponse.getMessage());
                    }
                } else {
                    Log.d(TAG, "onResponse: success");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<TempatKesehatanTerkiniModel>>> call, Throwable t) {
                progressDialog.dismiss();
                Log.d(TAG, "onResponse: success");
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
        tv_email = findViewById(R.id.tv_email_user);
        tv_profile = findViewById(R.id.tv_profile);
        tv_nm_lengkap = findViewById(R.id.tv_nm_lengkap);
        img_user = findViewById(R.id.iv_user_dashboard);
        vp_tempat_lokasi_terkini = findViewById(R.id.vp2_terkini);
        indicator_banner = findViewById(R.id.indicator_banner);
        sharedPreference = new SharedPreference(this);
        progressDialog = new ProgressDialog(this);
        cv_klinik = findViewById(R.id.cv_rsia);
        cv_rsia = findViewById(R.id.cv_rs);
        cv_puskesmas = findViewById(R.id.cv_puskesmas);
        cv_rs = findViewById(R.id.cv_klinik);

    }

    @Override
    public void onClick(View view) {

        Intent intent = new Intent(this, TempatKesehatanActivity.class);

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
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }
}