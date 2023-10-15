package com.example.giskesehatan.Activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.giskesehatan.Helpers.AnimationHelper;
import com.example.giskesehatan.Helpers.AppConfig;
import com.example.giskesehatan.Helpers.MyApiApplication;
import com.example.giskesehatan.Interfaces.ApiServices;
import com.example.giskesehatan.Models.ApiResponse;
import com.example.giskesehatan.Models.LoginModel;
import com.example.giskesehatan.Models.RegisterModel;
import com.example.giskesehatan.Models.UserDetailModel;
import com.example.giskesehatan.Models.UserModel;
import com.example.giskesehatan.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private static final String TAG = "ForgotPasswordActivity";

    //init komponent
    private AppCompatTextView tv_kembali;
    //init komponent form reset password
    private AppCompatEditText ed_email;
    private AppCompatButton btn_reset;
    private LinearLayoutCompat layout_reset;

    //init komponent form change password
    private AppCompatEditText ed_pass, ed_conf_pass;
    private AppCompatButton btn_change;
    private LinearLayoutCompat layout_change;

    //progress dialoh
    private ProgressDialog progressDialog;
    //apiservice untuk mengirim data dari client ke serve
    private ApiServices apiService;

    //String id_user
    private String id_user = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initComponents();

        MyApiApplication myApiApplication = (MyApiApplication) getApplication();
        apiService = myApiApplication.getApiService();

        tv_kembali.setOnClickListener(v -> kembali());
        btn_reset.setOnClickListener(v -> checkEmail());
        btn_change.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String pass = ed_pass.getText().toString();
        String conf_pass = ed_conf_pass.getText().toString();
        String email = ed_email.getText().toString();

        if (pass.equals("")) {
            ed_pass.requestFocus();
            ed_pass.setError("Harap input password anda");
        } else {
            if (pass.length() < 4) {
                ed_pass.requestFocus();
                ed_pass.setError("Password harus lebih dari 4 karakter");
            } else {
                if (conf_pass.equals("")) {
                    ed_conf_pass.requestFocus();
                    ed_conf_pass.setError("Harap input konfirmasi password");
                } else {
                    if (!conf_pass.matches(pass)) {
                        ed_conf_pass.requestFocus();
                        ed_conf_pass.setError("Password dan konfirmasi tidak sama");
                    } else {
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage("Harap tunggu. . .");
                        progressDialog.show();

                        UserModel userModel = new UserModel();
                        userModel.setIdUser(id_user);
                        userModel.setPassword(pass);

                        Call<ApiResponse> call = apiService.resetpassword(userModel);
                        call.enqueue(new Callback<ApiResponse>() {
                            @Override
                            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                                progressDialog.dismiss();
                                if (response.isSuccessful()) {
                                    ApiResponse apiResponse = response.body();
                                    if (apiResponse.isStatus()) {
                                        Toast.makeText(ForgotPasswordActivity.this, "Berhasil merubah password anda", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(ForgotPasswordActivity.this, "Silahkan masuk dengan email dan password yang sudah anda ubah", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                        intent.putExtra("email", email);
                                        startActivity(intent);
                                        finish();
                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                    } else {
                                        Log.d(TAG, "pesan: " + apiResponse.getMessage());
                                        Toast.makeText(ForgotPasswordActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // Tangani jika respons tidak berhasil (kode status bukan 2xx)
                                    Toast.makeText(ForgotPasswordActivity.this, response.code(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ApiResponse> call, Throwable t) {
                                progressDialog.dismiss();

                                // Tangani jika respons tidak berhasil (kode status bukan 2xx)
                                Log.e(TAG, "Gagal Konek: " + t.getMessage());
                                // Toast.makeText(ForgotPasswordActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }
            }
        }
    }

    private void checkEmail() {

        String email = ed_email.getText().toString();

        if (email.equals("")) {
            ed_email.requestFocus();
            ed_email.setError("Harap masukan email");
        } else {
            if (!AppConfig.isEmailValid(email)) {
                ed_email.requestFocus();
                ed_email.setError("Email tidak valid");
            } else {
                progressDialog.setMessage("Silahkan Tunggu. . .");
                progressDialog.setCancelable(false);
                progressDialog.show();

                LoginModel loginModel = new LoginModel();
                loginModel.setEmail(email);

                Call<ApiResponse<List<UserModel>>> call = apiService.checkemail(loginModel);
                call.enqueue(new Callback<ApiResponse<List<UserModel>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<UserModel>>> call, Response<ApiResponse<List<UserModel>>> response) {
                        progressDialog.dismiss();
                        if (response.isSuccessful()) {
                            ApiResponse<List<UserModel>> apiResponse = response.body();
                            if (apiResponse.isStatus()) {
                                List<UserModel> userModels = apiResponse.getResult();
                                id_user = userModels.get(0).getIdUser();
                                AnimationHelper.fadeOutView(layout_reset, layout_change);
                                Toast.makeText(getApplicationContext(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d(TAG, "gagal: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<UserModel>>> call, Throwable t) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onFailure: " + t.getMessage());
                    }
                });

            }
        }

    }

    private void kembali() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void initComponents() {
        tv_kembali = findViewById(R.id.tv_kembali);
        progressDialog = new ProgressDialog(this);
        ed_email = findViewById(R.id.ed_email);
        btn_reset = findViewById(R.id.btn_reset);
        layout_reset = findViewById(R.id.layout_reset);
        ed_pass = findViewById(R.id.pass);
        ed_conf_pass = findViewById(R.id.conf_pass);
        btn_change = findViewById(R.id.btn_change);
        layout_change = findViewById(R.id.layout_change);
    }
}