package com.example.giskesehatan.Activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Intent;
import android.os.Bundle;

import com.example.giskesehatan.Helpers.AppConfig;
import com.example.giskesehatan.R;

public class TempatKesehatanActivity extends AppCompatActivity {

    private static final String TAG = "TempatKes...Activity";

    //init title
    private AppCompatTextView tv_title;
    private AppCompatImageView iv_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tempat_kesehatan);

        initComponents();

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            tv_title.setText(AppConfig.capitalizeFirstLetter(extras.getString("title"))+ "Terdekat");
        }

        iv_back.setOnClickListener(v -> onBackPressed());
    }

    private void initComponents() {
        tv_title            = findViewById(R.id.tv_title);
        iv_back             = findViewById(R.id.iv_back);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(TempatKesehatanActivity.this,DashboardActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        super.onBackPressed();
    }
}