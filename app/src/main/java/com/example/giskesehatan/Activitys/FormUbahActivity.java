package com.example.giskesehatan.Activitys;

import android.Manifest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
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
import com.example.giskesehatan.R;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;

public class FormUbahActivity extends AppCompatActivity {

    private static final String TAG = "FormUbahActivity";

    //init component yang ada di FormUbahActivity
    //init variable change photo profile dan title
    private CircleImageView cv_img_user;
    private AppCompatImageView iv_back, iv_simpan;
    //init varible data
    private TextInputEditText ed_nm_lengkap,ed_nik, ed_telpon,ed_tgl_lahir,ed_tmp_lahir;
    private AutoCompleteTextView ed_jns_kelamin;

    //Strorage permision code untuk mengecek apakah aplikasi sudah memberi izin atau belum
    private static final int STORAGE_PERMISSION_CODE = 123;
    private int SELECT_PICTURE = 200;
    private String gambar = "";

    //init configurasi yang dibutuhkan system
    private SharedPreference sharedPreference;
    private ApiServices apiServices;
    private ProgressDialog progressDialog;

    //init variable string untuk mendapatkan data dari profile activitu
    String string_nama_lengkap = "";
    String string_nik = "";
    String string_telpon = "";
    String string_tanggal_lahir = "";
    String string_tempat_lahir = "";
    String string_jns_kelamin = "";
    String string_img_user = "";
    //init variable file untuk mengirim data gambar ke server
    File imageFile = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_ubah);

        sharedPreference = new SharedPreference(this);
        initComponents();

        MyApiApplication myApiApplication = (MyApiApplication) getApplication();
        apiServices = myApiApplication.getApiService();

        String[] jnsKelamin = {"Laki-Laki", "Perempuan"}; // Ganti dengan daftar pilihan Anda
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, jnsKelamin);
        ed_jns_kelamin.setAdapter(adapter);
        // Set an OnClickListener to show the dropdown
        ed_jns_kelamin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ed_jns_kelamin.showDropDown();
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            string_nama_lengkap     = extras.getString("nama_lengkap");
            string_nik              = extras.getString("nik");
            string_telpon           = extras.getString("telpon");
            string_tanggal_lahir    = extras.getString("tanggal_lahir");
            string_tempat_lahir     = extras.getString("tempat_lahir");
            string_jns_kelamin      = extras.getString("jns_kelamin");
            string_img_user         = extras.getString("img_user");

            ed_nm_lengkap.setText(string_nama_lengkap);
            ed_nik.setText(string_nik);
            ed_telpon.setText(string_telpon);
            ed_tgl_lahir.setText(AppConfig.dateIndonesia(string_tanggal_lahir));
            ed_tmp_lahir.setText(string_tempat_lahir);
            ed_jns_kelamin.setText(string_jns_kelamin,false);
            Glide.with(this)
                    .load(AppConfig.BASE_URL_IMG_USER + string_img_user)
                    .placeholder(R.drawable.girl)
                    .centerCrop()
                    .into(cv_img_user);
        }
        cv_img_user.setOnClickListener(v -> choicePhoto());
        iv_back.setOnClickListener(v -> onBackPressed());
        iv_simpan.setOnClickListener(v -> kirimKeServer());
        ed_tgl_lahir.setOnClickListener(v -> showDatePickerDialog(string_tanggal_lahir));
    }

    private void showDatePickerDialog(String tanggal) {
        Calendar calendar = Calendar.getInstance();
        int year = 0;
        int month = 0;
        int day = 0;

        if(tanggal.equals("0000-00-00") || tanggal.isEmpty()){
             year = calendar.get(Calendar.YEAR);
             month = calendar.get(Calendar.MONTH);
             day = calendar.get(Calendar.DAY_OF_MONTH);
        }else{
            String[] parts = tanggal.split("-");
            year = Integer.parseInt(parts[0]);
            month = Integer.parseInt(parts[1]) - 1;
            day = Integer.parseInt(parts[2]);
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                android.R.style.Theme_Holo_Dialog,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        string_tanggal_lahir = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                        ed_tgl_lahir.setText(AppConfig.dateIndonesia(string_tanggal_lahir));
                    }
                }, year, month, day);

        datePickerDialog.show();
    }

    private void kirimKeServer() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set title dialog
        alertDialogBuilder.setTitle("Yakin data sudah sesuai ?");

        // set pesan dari dialog
        alertDialogBuilder
//                .setMessage("Klik Ya untuk keluar!")
                .setIcon(R.mipmap.ic_launcher)
                .setCancelable(false)
                .setPositiveButton("Ya",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // jika tombol diklik, maka akan menutup activity ini
                        uploadToServer();
                    }
                })
                .setNegativeButton("Tidak",new DialogInterface.OnClickListener() {
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

    private void uploadToServer() {
        String bearerToken = AppConfig.keyToken(sharedPreference.readSetting("token")); // Ganti dengan token bearer yang sesuai
        String string_id_user_detail = sharedPreference.readSetting("id_user");

        string_nama_lengkap     = ed_nm_lengkap.getText().toString();
        string_nik              = ed_nik.getText().toString();
        string_telpon           = ed_telpon.getText().toString();
        string_tempat_lahir     = ed_tmp_lahir.getText().toString();
        string_jns_kelamin      = ed_jns_kelamin.getText().toString();

        OkHttpClient client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(false)
                .build();

        AndroidNetworking.initialize(getApplicationContext(), client);

        AndroidNetworking.upload(AppConfig.BASE_URL + "uploadimage")
                .addMultipartFile("image", imageFile)
                .addMultipartParameter("id_user_detail", string_id_user_detail)
                .addMultipartParameter("nik", string_nik)
                .addMultipartParameter("nama_lengkap", string_nama_lengkap)
                .addMultipartParameter("tgl_lahir", string_tanggal_lahir)
                .addMultipartParameter("tmp_lahir", string_tempat_lahir)
                .addMultipartParameter("jns_kelamin", string_jns_kelamin)
                .addMultipartParameter("telpon", string_telpon)
                .addHeaders("Authorization", bearerToken)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
//                                // do anything with progress
                        int peroses = (int) ((bytesUploaded / totalBytes) * 100);
                        progressDialog.setMessage("Sedang Mengirim Data . . . \n" + peroses + " %");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                    }
                })
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Boolean status = response.getBoolean("success");
                            progressDialog.dismiss();
                            if (status) {
                                Toast.makeText(FormUbahActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(FormUbahActivity.this,ProfileActivity.class);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                            }else{
                                Toast.makeText(FormUbahActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        progressDialog.dismiss();
                        Log.d("error", "onError: " + anError.getErrorBody());
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
                imageFile = getFileFromUri(data.getData());
                Log.d(TAG, "file: " + imageFile);
                gambar = getFileName(selectedImageUri);
                Glide.with(this)
                        .load(selectedImageUri)
                        .fitCenter()
                        .placeholder(R.drawable.girl)
                        .into(cv_img_user);
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
        cv_img_user         = findViewById(R.id.cv_img_user);
        progressDialog      = new ProgressDialog(this);
        ed_nm_lengkap       = findViewById(R.id.ed_nama_lengkap);
        ed_nik              = findViewById(R.id.ed_nik);
        ed_tgl_lahir        = findViewById(R.id.ed_tanggal_lahir);
        ed_tmp_lahir        = findViewById(R.id.ed_tempat_lahir);
        ed_telpon           = findViewById(R.id.ed_telpon);
        ed_jns_kelamin      = findViewById(R.id.ed_jns_kelamin);
        iv_back             = findViewById(R.id.iv_back);
        iv_simpan           = findViewById(R.id.iv_simpan);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}