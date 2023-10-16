package com.example.giskesehatan.Activitys;

import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.bumptech.glide.Glide;
import com.example.giskesehatan.Helpers.AppConfig;
import com.example.giskesehatan.Helpers.MyApiApplication;
import com.example.giskesehatan.Helpers.SharedPreference;
import com.example.giskesehatan.Interfaces.ApiServices;
import com.example.giskesehatan.Models.ApiResponse;
import com.example.giskesehatan.R;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;

public class FormUbahActivity extends AppCompatActivity {

    private static final String TAG = "FormUbahActivity";

    //init change photo profile
    private CircleImageView cv_img_user;

    //Strorage permision code untuk mengecek apakah aplikasi sudah memberi izin atau belum
    private static final int STORAGE_PERMISSION_CODE = 123;
    private int SELECT_PICTURE = 200;
    private String gambar = "";

    private SharedPreference sharedPreference;
    private ApiServices apiServices;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_ubah);

        sharedPreference = new SharedPreference(this);
        initComponents();

        cv_img_user.setOnClickListener(v -> choicePhoto());

        MyApiApplication myApiApplication = (MyApiApplication) getApplication();
        apiServices = myApiApplication.getApiService();

        AutoCompleteTextView edJnsKelamin = findViewById(R.id.ed_jns_kelamin);
        String[] jnsKelamin = {"Laki-Laki", "Perempuan"}; // Ganti dengan daftar pilihan Anda
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, jnsKelamin);
        edJnsKelamin.setAdapter(adapter);
        // Set an OnClickListener to show the dropdown
        edJnsKelamin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edJnsKelamin.showDropDown();
            }
        });
    }

    private void choicePhoto() {
        requestStoragePermission();
        pickImage();
    }

    private void pickImage() {
        final CharSequence[] options = {"Pilih Dari Gallery", "Batal"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Foto Profil");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Pilih Dari Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, SELECT_PICTURE);
                } else if (options[item].equals("Batal")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();

    }

    @SuppressLint("LongLogTag")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                final Uri selectedImageUri = data.getData();
                File imageFile = getFileFromUri(data.getData());
                Log.d(TAG, "file: " + imageFile);
                gambar = getFileName(selectedImageUri);
                Glide.with(this).load(selectedImageUri).fitCenter().placeholder(R.drawable.girl).into(cv_img_user);
                String bearerToken = AppConfig.keyToken(sharedPreference.readSetting("token")); // Ganti dengan token bearer yang sesuai
                String string_id_user_detail = sharedPreference.readSetting("id_user");

                OkHttpClient client = new OkHttpClient.Builder()
                        .retryOnConnectionFailure(false)
                        .build();

                AndroidNetworking.initialize(getApplicationContext(), client);

                AndroidNetworking.upload(AppConfig.BASE_URL+"uploadimage")
                        .addMultipartFile("image", imageFile)
                        .addMultipartParameter("id_user_bio", string_id_user_detail)
                        .addHeaders("Authorization",bearerToken)
                        .build()
                        .setUploadProgressListener(new UploadProgressListener() {
                            @Override
                            public void onProgress(long bytesUploaded, long totalBytes) {
//                                // do anything with progress
                                int peroses = (int) ((bytesUploaded / totalBytes) * 100);
                                progressDialog.setMessage("Sedang Mengupload \n" + peroses + " %");
                                progressDialog.setCancelable(false);
                                progressDialog.show();
                            }
                        })
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Boolean status = response.getBoolean("success");
                                    if (status) {
                                        Toast.makeText(FormUbahActivity.this, "Berhasil Mengupload", Toast.LENGTH_SHORT).show();
                                        onStart();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            @Override
                            public void onError(ANError anError) {
                                progressDialog.dismiss();
                                Log.d("error", "onError: "+anError.getErrorBody());
                            }
                        });

            }
        }
    }

    private File getFileFromUri(Uri uri) {
        File file = null;
        try {
            String filePath = getRealPathFromURI(uri); // Mendapatkan path file dari Uri

            if (filePath != null) {
                file = new File(filePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, projection, null, null, null);

        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String filePath = cursor.getString(column_index);
            cursor.close();
            return filePath;
        } else {
            return contentUri.getPath(); // Jika cursor null, gunakan path langsung
        }
    }


    @SuppressLint("Range")
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getApplicationContext().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }


    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    private void initComponents() {
        cv_img_user = findViewById(R.id.cv_img_user);
        progressDialog = new ProgressDialog(this);
    }
}