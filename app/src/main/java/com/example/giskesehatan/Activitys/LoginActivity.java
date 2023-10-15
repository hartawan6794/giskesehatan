package com.example.giskesehatan.Activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.example.giskesehatan.Helpers.AppConfig;
import com.example.giskesehatan.Helpers.MyApiApplication;
import com.example.giskesehatan.Helpers.SharedPreference;
import com.example.giskesehatan.Interfaces.ApiServices;
import com.example.giskesehatan.MainActivity;
import com.example.giskesehatan.Models.ApiResponse;
import com.example.giskesehatan.Models.LoginModel;
import com.example.giskesehatan.Models.UserModel;
import com.example.giskesehatan.R;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    //init tag variable
    private static final String TAG = "LoginActivity";

    //init komponent yang ada di xml
    private AppCompatEditText ed_email, ed_password;
    private AppCompatButton btn_login, btn_register;
    private AppCompatTextView tv_forgot;
    //progress dialoh
    private ProgressDialog progressDialog;
    //apiservice untuk mengirim data dari client ke serve
    private ApiServices apiService;
    //init SharedPreference untuk menyimpan variable statis dinamis ke dalam app
    private SharedPreference sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initComponents();

        btn_login.setOnClickListener(v -> login());
        btn_register.setOnClickListener(v -> register());
        tv_forgot.setOnClickListener(v -> forgot());
        MyApiApplication myApiApplication = (MyApiApplication) getApplication();
        apiService = myApiApplication.getApiService();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String email = extras.getString("email");
            ed_email.setText(email);
        }

    }

    private void forgot() {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void login() {

        if (ed_email.getText().toString().equals("")) {
            ed_email.setError("Harap Mengisi Email Anda");
            ed_email.requestFocus();
        } else {
            if (ed_password.getText().toString().equals("")) {
                ed_password.setError("Harap Mengisi Password Anda");
                ed_password.requestFocus();
            } else {
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Mohon Tunggu Sebentar. . .");
                progressDialog.show();

                LoginModel loginModel = new LoginModel();
                loginModel.setEmail(ed_email.getText().toString());
                loginModel.setPassword(ed_password.getText().toString());

                Call<ApiResponse<List<UserModel>>> call = apiService.login(loginModel);
                call.enqueue(new Callback<ApiResponse<List<UserModel>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<UserModel>>> call, Response<ApiResponse<List<UserModel>>> response) {
                        progressDialog.dismiss();
                        if (response.isSuccessful()) {
                            ApiResponse<List<UserModel>> apiResponse = response.body();
                            if (apiResponse.isStatus()) {
                                List<UserModel> userModels = apiResponse.getResult();
                                sharedPreference.addUpdateSettings("id_user", userModels.get(0).getIdUser());
                                sharedPreference.addUpdateSettings("token", userModels.get(0).getBearerToken());
                                sharedPreference.addUpdateSettings("email", userModels.get(0).getEmailUser());
                                sharedPreference.addUpdateSettings("username", userModels.get(0).getUsername());
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Error : " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Tangani jika respons tidak berhasil (kode status bukan 2xx)
                            Log.e(TAG, "Error Message: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<UserModel>>> call, Throwable t) {
                        progressDialog.dismiss();
                        // Tangani jika permintaan gagal
                        Log.e("API", "Request failure: " + t.getMessage());
                    }
                });
            }
        }


    }

    private void register() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void initComponents() {
        sharedPreference    = new SharedPreference(this);
        progressDialog      = new ProgressDialog(this);
        ed_email            = findViewById(R.id.email);
        ed_password         = findViewById(R.id.pass);
        btn_login           = findViewById(R.id.btn_login);
        btn_register        = findViewById(R.id.btn_signup);
        tv_forgot           = findViewById(R.id.tv_forgot);
    }
}