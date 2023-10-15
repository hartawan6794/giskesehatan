package com.example.giskesehatan.Activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.giskesehatan.Helpers.AppConfig;
import com.example.giskesehatan.Helpers.MyApiApplication;
import com.example.giskesehatan.Helpers.SharedPreference;
import com.example.giskesehatan.Interfaces.ApiServices;
import com.example.giskesehatan.Models.ApiResponse;
import com.example.giskesehatan.Models.UserDetailModel;
import com.example.giskesehatan.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    
    private static final String TAG = "ProfileActivity";
    private SharedPreference sharedPreference;
    private ApiServices apiServices;
    //init card title
    private AppCompatTextView tv_ubah,tv_nama,tv_email;
    private CircleImageView cv_img_user;
    private AppCompatImageView iv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sharedPreference = new SharedPreference(this);
        MyApiApplication myApiApplication = (MyApiApplication) getApplication();
        apiServices = myApiApplication.getApiService();

        initComponents();
        setValueProfile();

        iv_back.setOnClickListener(v -> dashboard());
        tv_ubah.setOnClickListener(v -> formUser());

        //syntax proses pengambilan data
        String token = AppConfig.keyToken(sharedPreference.readSetting("token"));
        String id_user = sharedPreference.readSetting("id_user");
        getdata(token,id_user);
    }

    private void setValueProfile() {

        tv_email.setText(sharedPreference.readSetting("email"));
        tv_nama.setText(AppConfig.capitalizeFirstLetter(sharedPreference.readSetting("nama_lengkap")));
        Glide.with(this)
                .load(sharedPreference.readSetting("img_user"))
                .centerCrop()
                .placeholder(R.drawable.girl)
                .into(cv_img_user);
    }
    private void formUser() {
        Toast.makeText(this, "Kehalaman form user", Toast.LENGTH_SHORT).show();
    }

    private void dashboard() {
        Intent intent = new Intent(this,DashboardActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }

    private void initComponents() {
        tv_ubah         = findViewById(R.id.tv_ubah);
        tv_email        = findViewById(R.id.tv_email_user);
        tv_nama         = findViewById(R.id.tv_nm_lengkap);
        cv_img_user     = findViewById(R.id.cv_user_profile);
        iv_back         = findViewById(R.id.iv_back);
    }

    private void getdata(String token, String id_user){
        UserDetailModel userDetailModel = new UserDetailModel();
        userDetailModel.setIdUserDetail(id_user);

        Call<ApiResponse<List<UserDetailModel>>> call = apiServices.user(token,userDetailModel);
        call.enqueue(new Callback<ApiResponse<List<UserDetailModel>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<UserDetailModel>>> call, Response<ApiResponse<List<UserDetailModel>>> response) {
                if(response.isSuccessful()){
                    ApiResponse<List<UserDetailModel>> apiResponse = response.body();
                    if(apiResponse.isStatus()){
                        List<UserDetailModel> userDetailModels = apiResponse.getResult();
                        for(UserDetailModel userDetailModel1 : userDetailModels){
                            Log.d(TAG, "onResponse: "+userDetailModel1);
                        }
                        Log.d(TAG, "pesan: "+apiResponse.getMessage());
                    }else{
                        Log.d(TAG, "pesan: "+apiResponse.getMessage());
                    }
                }else{
                    Log.d(TAG, "pesan: "+response.message());
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<List<UserDetailModel>>> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t.getMessage());
            }
        });
    }
}