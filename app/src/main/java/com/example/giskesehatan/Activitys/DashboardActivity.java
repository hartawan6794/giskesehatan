package com.example.giskesehatan.Activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.example.giskesehatan.Helpers.AppConfig;
import com.example.giskesehatan.Helpers.SharedPreference;
import com.example.giskesehatan.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class DashboardActivity extends AppCompatActivity {

    private static final String TAG = "DashboardActivity";
    private SharedPreference sharedPreference;

    //init komponent
    private AppCompatTextView tv_nm_lengkap,tv_email,tv_profile;
    private CircleImageView img_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        sharedPreference = new SharedPreference(this);

        if(sharedPreference.readSetting("id_user").isEmpty()){
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        initComponents();
        setValueProfile();
        tv_profile.setOnClickListener(v -> profile());
    }

    private void profile() {
        Intent intent = new Intent(this,ProfileActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void setValueProfile() {
        tv_email.setText(sharedPreference.readSetting("email"));
        tv_nm_lengkap.setText(AppConfig.capitalizeFirstLetter(sharedPreference.readSetting("nama_lengkap")));
        Glide.with(this)
                .load(sharedPreference.readSetting("img_user"))
                .centerCrop()
                .placeholder(R.drawable.girl)
                .into(img_user);
    }

    private void initComponents() {
        tv_email        = findViewById(R.id.tv_email_user);
        tv_profile      = findViewById(R.id.tv_profile);
        tv_nm_lengkap   = findViewById(R.id.tv_nm_lengkap);
        img_user        = findViewById(R.id.iv_user_dashboard);
    }
}