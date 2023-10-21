package com.example.giskesehatan.Activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.giskesehatan.Adapters.TempatKesehatanAdapter;
import com.example.giskesehatan.Helpers.AppConfig;
import com.example.giskesehatan.Helpers.GPSTracker;
import com.example.giskesehatan.Helpers.MyApiApplication;
import com.example.giskesehatan.Helpers.SharedPreference;
import com.example.giskesehatan.Interfaces.ApiServices;
import com.example.giskesehatan.Models.ApiResponse;
import com.example.giskesehatan.Models.TempatKesehatanModel;
import com.example.giskesehatan.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LayananKesehatanActivity extends AppCompatActivity {

    private static final String TAG = "TempatKes...Activity";

    //init title
    private AppCompatTextView tv_title;
    private AppCompatImageView iv_back;

    private SharedPreference sharedPreference;

    //init list tempat layanan kesehatan
    private RecyclerView rv_layanan_kesehatan;
    private TempatKesehatanAdapter tempatKesehatanAdapter;
    private ProgressDialog progressDialog;

    //init mendapatkan data retrofit2
    private ApiServices apiServices;
    private String string_table = "";
    private LinearLayoutCompat layout_empty;
    private GPSTracker gpsTracker;

    //init search component
    private SearchView sv_tempat_kesehatan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tempat_kesehatan);

        initComponents();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            tv_title.setText(AppConfig.capitalizeFirstLetter(extras.getString("title")) + " Terdekat");
        }

        iv_back.setOnClickListener(v -> onBackPressed());
        String token = sharedPreference.readSetting("token");
        string_table = getTable(extras.getString("title"));

        getData(token, string_table);

        sv_tempat_kesehatan.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle submit event (e.g., perform search)
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Sedang mencari data. . .");
                progressDialog.show();
                TempatKesehatanModel tempatKesehatanModel = new TempatKesehatanModel();
                tempatKesehatanModel.setTabel(string_table);
                tempatKesehatanModel.setNama(query);
                Call<ApiResponse<List<TempatKesehatanModel>>> call = apiServices.search(AppConfig.keyToken(token), tempatKesehatanModel);
                call.enqueue(new Callback<ApiResponse<List<TempatKesehatanModel>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<TempatKesehatanModel>>> call, Response<ApiResponse<List<TempatKesehatanModel>>> response) {
                        progressDialog.dismiss();
                        if (response.isSuccessful()) {
                            ApiResponse<List<TempatKesehatanModel>> apiResponse = response.body();
                            if (apiResponse.isStatus()) {
                                List<TempatKesehatanModel> tempatKesehatanModels = apiResponse.getResult();
                                if (tempatKesehatanModels.size() > 0) {
                                    rv_layanan_kesehatan.setVisibility(View.VISIBLE);
                                    layout_empty.setVisibility(View.GONE);
                                } else {
                                    rv_layanan_kesehatan.setVisibility(View.GONE);
                                    layout_empty.setVisibility(View.VISIBLE);
                                }
                                tempatKesehatanAdapter = new TempatKesehatanAdapter(LayananKesehatanActivity.this, tempatKesehatanModels,null, 2);
                                rv_layanan_kesehatan.setAdapter(tempatKesehatanAdapter);
                                tempatKesehatanAdapter.notifyDataSetChanged();
                                Log.d(TAG, "Message: " + apiResponse.getMessage());
                            } else {
                                rv_layanan_kesehatan.setVisibility(View.GONE);
                                layout_empty.setVisibility(View.VISIBLE);
                                tempatKesehatanAdapter.notifyDataSetChanged();
                                Log.d(TAG, "Message: " + apiResponse.getMessage());
                            }
                        } else {
                            Log.d(TAG, "onResponse: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<TempatKesehatanModel>>> call, Throwable t) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onFailure: " + t.getMessage());
                    }
                });

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle text change (e.g., filter results as the user types)
                if (newText.isEmpty()) {
                    getData(token, string_table);
                }
                return true;
            }

        });
    }

    private void getData(String token, String table) {
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sedang mengambil data. . .");
        progressDialog.show();

        TempatKesehatanModel tempatKesehatanModel = new TempatKesehatanModel();
        tempatKesehatanModel.setTabel(table);
        Call<ApiResponse<List<TempatKesehatanModel>>> call = apiServices.getlayanan(AppConfig.keyToken(token), tempatKesehatanModel);
        call.enqueue(new Callback<ApiResponse<List<TempatKesehatanModel>>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<ApiResponse<List<TempatKesehatanModel>>> call, Response<ApiResponse<List<TempatKesehatanModel>>> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    ApiResponse<List<TempatKesehatanModel>> apiResponse = response.body();
                    if (apiResponse.isStatus()) {
                        List<TempatKesehatanModel> tempatKesehatanModels = apiResponse.getResult();
                        List<TempatKesehatanModel> filterModel = new ArrayList<>();
                        for (TempatKesehatanModel tempatKesehatanModel1 : tempatKesehatanModels) {
                            if (calculateDistanceUser(gpsTracker.getLatitude(),
                                    gpsTracker.getLongitude(),
                                    tempatKesehatanModel1.getLatitude(),
                                    tempatKesehatanModel1.getLongitude())) {
                                filterModel.add(tempatKesehatanModel1);
                            }
                        }
                        if (tempatKesehatanModels.size() > 1) {
                            rv_layanan_kesehatan.setVisibility(View.VISIBLE);
                            layout_empty.setVisibility(View.GONE);
                        } else {
                            rv_layanan_kesehatan.setVisibility(View.GONE);
                            layout_empty.setVisibility(View.VISIBLE);
                        }
                        tempatKesehatanAdapter = new TempatKesehatanAdapter(LayananKesehatanActivity.this, filterModel, null,2);
                        rv_layanan_kesehatan.setAdapter(tempatKesehatanAdapter);
                        tempatKesehatanAdapter.notifyDataSetChanged();
                        Log.d(TAG, "Message: " + apiResponse.getMessage());
                    } else {
                        Log.d(TAG, "Message: " + apiResponse.getMessage());
                    }
                } else {
                    Log.d(TAG, "onResponse: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<TempatKesehatanModel>>> call, Throwable t) {
                progressDialog.dismiss();
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void initComponents() {
        gpsTracker              = new GPSTracker(this);
        tv_title                = findViewById(R.id.tv_title);
        sv_tempat_kesehatan     = findViewById(R.id.sv_tempat_kesehatan);
        iv_back                 = findViewById(R.id.iv_back);
        rv_layanan_kesehatan    = findViewById(R.id.rv_layanan_kesehatan);
        layout_empty            = findViewById(R.id.layout_empty);
        sharedPreference        = new SharedPreference(this);
        progressDialog          = new ProgressDialog(this);

        rv_layanan_kesehatan.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        MyApiApplication myApiApplication = (MyApiApplication) getApplication();
        apiServices = myApiApplication.getApiService();
        sv_tempat_kesehatan.setQueryHint("Cari. . .");
    }

    private String getTable(String title) {
        String table = "";
        if (title.equals("puskesmas"))
            table = "tbl_puskesmas";
        else if (title.equals("klinik"))
            table = "tbl_klinik";
        else if (title.equals("rumah sakit"))
            table = "tbl_rumah_sakit";
        else if (title.equals("rumah sakit ibu anak"))
            table = "tbl_rumah_sakit_ibu_anak";
        else
            table = "tbl_klinik";
        return table;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(LayananKesehatanActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        super.onBackPressed();
    }

    private boolean calculateDistanceUser(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        float[] results = new float[1];
        Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results);
        if (results[0] <= 2000) {
            return true;
        } else {
            return false;
        }

    }

}