package com.example.giskesehatan;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.giskesehatan.Helpers.SharedPreference;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private SharedPreference sharedPreference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreference = new SharedPreference(this);

        Log.d(TAG, "id_user: "+sharedPreference.readSetting("id_user"));
        Log.d(TAG, "token: "+sharedPreference.readSetting("token"));;
        Log.d(TAG, "token: "+sharedPreference.readSetting("email"));;
        Log.d(TAG, "token: "+sharedPreference.readSetting("username"));;
    }
}