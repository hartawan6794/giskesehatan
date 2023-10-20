package com.example.giskesehatan.Activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.giskesehatan.Helpers.AppConfig;
import com.example.giskesehatan.Helpers.MyApiApplication;
import com.example.giskesehatan.Interfaces.ApiServices;
import com.example.giskesehatan.Models.ApiResponse;
import com.example.giskesehatan.Models.CombineUserModel;
import com.example.giskesehatan.Models.UserDetailModel;
import com.example.giskesehatan.Models.UserModel;
import com.example.giskesehatan.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private AppCompatEditText ed_email, ed_nm, ed_username, ed_pass, ed_conf_pass;
    private AppCompatButton btn_regis, btn_login;
    private CheckBox show_pass;
    private ProgressDialog progressDialog;
    private ApiServices apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initComponents();

        MyApiApplication myApiApplication = (MyApiApplication) getApplication();
        apiService = myApiApplication.getApiService();

        Log.d(TAG, "onCreate: "+apiService);
        btn_regis.setOnClickListener(view -> register());
        btn_login.setOnClickListener(view -> login());

        show_pass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    // Tampilkan kata sandi
                    ed_pass.setTransformationMethod(null);
                    ed_conf_pass.setTransformationMethod(null);
                    show_pass.setText("Sembunyikan password");
                } else {
                    // Sembunyikan kata sandi
                    ed_pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    ed_conf_pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    show_pass.setText("Tampilkan password");
                }
            }
        });

    }

    private void login() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void register() {
        String email = ed_email.getText().toString();
        String nm_user = ed_nm.getText().toString();
        String username = ed_username.getText().toString();
        String pass = ed_pass.getText().toString();
        String conf_pass = ed_conf_pass.getText().toString();
        String device_id = getDeviceId(this);
        if (email.equals("")) {
            ed_email.requestFocus();
            ed_email.setError("Harap mengisi form email");
        } else {
            if (!AppConfig.isEmailValid(email)) {
                ed_email.requestFocus();
                ed_email.setError("Email tidak valid");
            } else {
                if (nm_user.equals("")) {
                    ed_nm.requestFocus();
                    ed_nm.setError("Harap mengisi form nama lengkap");
                } else {
                    if (username.equals("")) {
                        ed_username.requestFocus();
                        ed_username.setError("Harap mengisi form username");
                    } else {
                        if (ed_username.getText().toString().contains(" ")) {
                            ed_username.requestFocus();
                            ed_username.setError("Masukan nomor username tanpa spasi");
                        } else {
                            if (pass.equals("")) {
                                ed_pass.requestFocus();
                                ed_pass.setError("Harap mengisi form password");
                            } else {
                                if (pass.length() < 4) {
                                    ed_pass.requestFocus();
                                    ed_pass.setError("Password harus lebih dari 4 karakter");
                                } else {
                                    if (conf_pass.equals("")) {
                                        ed_conf_pass.requestFocus();
                                        ed_conf_pass.setError("Harap mengisi form konfirmasi password");
                                    } else {
                                        if (!conf_pass.matches(pass)) {
                                            ed_conf_pass.requestFocus();
                                            ed_conf_pass.setError("Password dan konfirmasi tidak sama");
                                        } else {
                                            progressDialog.setCancelable(false);
                                            progressDialog.setMessage("Harap tunggu. . .");
                                            progressDialog.show();

                                            UserModel userModel = new UserModel();
                                            userModel.setEmailUser(email);
                                            userModel.setUsername(username);
                                            userModel.setPassword(pass);
                                            userModel.setDeviceId(device_id);
                                            UserDetailModel userDetailModel = new UserDetailModel();
                                            userDetailModel.setNamaLengkap(nm_user);

                                            CombineUserModel modelRegister = new CombineUserModel();
                                            modelRegister.setUserModel(userModel);
                                            modelRegister.setUserDetailModel(userDetailModel);

                                            Call<ApiResponse> call = apiService.register(modelRegister);
                                            call.enqueue(new Callback<ApiResponse>() {
                                                @Override
                                                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                                                    progressDialog.dismiss();
                                                    if (response.isSuccessful()) {
                                                        ApiResponse apiResponse = response.body();
                                                        // Lakukan sesuatu dengan data yang diterima
                                                        if (apiResponse.isStatus()) {
                                                            Toast.makeText(RegisterActivity.this, "Berhasil mendaftarkan akun", Toast.LENGTH_SHORT).show();
                                                            Toast.makeText(RegisterActivity.this, "Silahkan masuk dengan email dan password yang sudah anda buat", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                            intent.putExtra("email", email);
                                                            finish();
                                                            startActivity(intent);
                                                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                                        } else
                                                            Log.d(TAG, "pesan: "+apiResponse.getMessage());
                                                            Toast.makeText(RegisterActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        // Tangani jika respons tidak berhasil (kode status bukan 2xx)
                                                        Log.e(TAG, "Error body: " + response.errorBody());
                                                        Log.e(TAG, "Error message: " + response.message());
                                                        Log.e(TAG, "Error code: " + response.code());
                                                        Log.e(TAG, "body: " + response.body());
//                                                        Toast.makeText(RegisterActivity.this, response.code(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<ApiResponse> call, Throwable t) {
                                                    progressDialog.dismiss();

                                                    // Tangani jika respons tidak berhasil (kode status bukan 2xx)
                                                    Log.e(TAG, "Gagal Konek: " + t.getMessage());
//                                                    Toast.makeText(RegisterActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
    }

    private void initComponents() {
        ed_email        = findViewById(R.id.email);
        ed_nm           = findViewById(R.id.nm_user);
        ed_username     = findViewById(R.id.username);
        ed_pass         = findViewById(R.id.pass);
        ed_conf_pass    = findViewById(R.id.conf_pass);
        btn_login       = findViewById(R.id.btn_login);
        btn_regis       = findViewById(R.id.btn_regis);
        progressDialog  = new ProgressDialog(this);
        show_pass       = findViewById(R.id.checkBoxShowPassword);
    }

    public static String getDeviceId(Context context) {

        String id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return id;
    }
}