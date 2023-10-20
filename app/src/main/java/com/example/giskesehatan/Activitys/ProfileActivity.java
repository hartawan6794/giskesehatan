package com.example.giskesehatan.Activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.giskesehatan.Helpers.AppConfig;
import com.example.giskesehatan.Helpers.MyApiApplication;
import com.example.giskesehatan.Helpers.SharedPreference;
import com.example.giskesehatan.Interfaces.ApiServices;
import com.example.giskesehatan.Models.ApiResponse;
import com.example.giskesehatan.Models.UserDetailModel;
import com.example.giskesehatan.R;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.security.PrivateKey;
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
    private AppCompatTextView tv_ubah, tv_username, tv_email;
    private CircleImageView cv_img_user;
    private AppCompatImageView iv_back;

    //init user detail component
    private AppCompatTextView tv_nm_lengkap, tv_nik, tv_date, tv_date_place, tv_gender, tv_phone;
    private LinearLayoutCompat layout_profile;

    //init shimmer animation effect
    private ShimmerFrameLayout shimmer_layout;

    //init logout button
    private CardView cv_logout;

    //init user img url untuk mengakses source dari server
    private String string_img_url = "";
    //init data tanggal lahir untuk mengakses source dari server
    private String string_date = "";

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
        cv_logout.setOnClickListener(v -> logout());

        //shimmer
        shimmer_layout.startShimmer();

        //syntax proses pengambilan data
        String token = AppConfig.keyToken(sharedPreference.readSetting("token"));
        String id_user = sharedPreference.readSetting("id_user");
        getdata(token, id_user);
    }

    private void logout() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set title dialog
        alertDialogBuilder.setTitle("Keluar Aplikasi?");

        // set pesan dari dialog
        alertDialogBuilder
                .setMessage("Klik Ya untuk keluar!")
                .setIcon(R.mipmap.ic_launcher)
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // jika tombol diklik, maka akan menutup activity ini
                        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        sharedPreference.deleteAllSettings();

                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // jika tombol ini diklik, akan menutup dialog
                        // dan tidak terjadi apa2
                        dialog.cancel();
                    }
                });

        // membuat alert dialog dari builder
        AlertDialog alertDialog = alertDialogBuilder.create();

        // menampilkan alert dialog
        alertDialog.show();
    }

    private void setValueProfile() {
        tv_email.setText(sharedPreference.readSetting("email"));
        tv_username.setText(sharedPreference.readSetting("username"));
    }

    private void formUser() {
        Intent intent = new Intent(this, FormUbahActivity.class);
        intent.putExtra("nama_lengkap", tv_nm_lengkap.getText().toString());
        intent.putExtra("nik", tv_nik.getText().toString());
        intent.putExtra("telpon", tv_phone.getText().toString());
        intent.putExtra("tanggal_lahir", string_date);
        intent.putExtra("tempat_lahir", tv_date_place.getText().toString());
        intent.putExtra("jns_kelamin", tv_gender.getText().toString());
        intent.putExtra("img_user", string_img_url);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void dashboard() {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void initComponents() {
        tv_ubah = findViewById(R.id.tv_ubah);
        tv_email = findViewById(R.id.tv_email_user);
        tv_username = findViewById(R.id.tv_username);
        cv_img_user = findViewById(R.id.cv_user_profile);
        iv_back = findViewById(R.id.iv_back);
        cv_logout = findViewById(R.id.cv_logout);
        tv_nm_lengkap = findViewById(R.id.tv_nm_lengkap);
        tv_nik = findViewById(R.id.tv_nik);
        tv_date = findViewById(R.id.tv_date);
        tv_date_place = findViewById(R.id.tv_date_place);
        tv_gender = findViewById(R.id.tv_gender);
        tv_phone = findViewById(R.id.tv_phone);
        shimmer_layout = findViewById(R.id.shimmer_layout);
        layout_profile = findViewById(R.id.layout_profile);
    }

    private void getdata(String token, String id_user) {
        UserDetailModel userDetailModel = new UserDetailModel();
        userDetailModel.setIdUserDetail(id_user);

        Call<ApiResponse<List<UserDetailModel>>> call = apiServices.user(token, userDetailModel);
        call.enqueue(new Callback<ApiResponse<List<UserDetailModel>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<UserDetailModel>>> call, Response<ApiResponse<List<UserDetailModel>>> response) {
                if (response.isSuccessful()) {
                    ApiResponse<List<UserDetailModel>> apiResponse = response.body();
                    if (apiResponse.isStatus()) {
                        List<UserDetailModel> userDetailModels = apiResponse.getResult();
                        for (UserDetailModel userDetailModel1 : userDetailModels) {
                            shimmer_layout.stopShimmer();
                            shimmer_layout.setVisibility(View.GONE);
                            layout_profile.setVisibility(View.VISIBLE);
                            string_img_url = userDetailModel1.getImgUser();
                            if (userDetailModel1.getTglLahir().equals("0000-00-00") || userDetailModel1.getTglLahir().equals("") || userDetailModel1.getTglLahir().equals("null")) {
                                string_date = "Belum di set";
                            } else {
                                string_date = userDetailModel1.getTglLahir();
                            }
                            tv_nm_lengkap.setText(AppConfig.capitalizeFirstLetter(userDetailModel1.getNamaLengkap()));
                            tv_nik.setText(userDetailModel1.getNik().equals("") ? "Belum diset" : userDetailModel1.getNik());
                            tv_date.setText(AppConfig.dateIndonesia(userDetailModel1.getTglLahir()));
                            tv_date_place.setText(userDetailModel1.getTmpLahir().equals("") ? "Belum diset" : userDetailModel1.getTmpLahir());
                            tv_gender.setText(userDetailModel1.getJnsKelamin().isEmpty() ? "Belum diset" : userDetailModel1.getJnsKelamin());
                            tv_phone.setText((userDetailModel1.getTelpon() == null) ? "Belum diset" : userDetailModel1.getTelpon());
                            Glide.with(ProfileActivity.this)
                                    .load(AppConfig.BASE_URL_IMG_USER + string_img_url)
                                    .placeholder(R.drawable.girl)
                                    .centerCrop()
                                    .into(cv_img_user);
                        }
                        Log.d(TAG, "pesan: " + apiResponse.getMessage());
                    } else {
                        Log.d(TAG, "pesan: " + apiResponse.getMessage());
                    }
                } else {
                    Log.d(TAG, "pesan: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<UserDetailModel>>> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        getdata(sharedPreference.readSetting("token"), sharedPreference.readSetting("id_user"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        getdata(sharedPreference.readSetting("token"), sharedPreference.readSetting("id_user"));
    }
}