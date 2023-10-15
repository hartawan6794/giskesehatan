package com.example.giskesehatan.Interfaces;

import com.example.giskesehatan.Models.ApiResponse;
import com.example.giskesehatan.Models.LoginModel;
import com.example.giskesehatan.Models.CombineUserModel;
import com.example.giskesehatan.Models.SharedPreferenceModel;
import com.example.giskesehatan.Models.UserDetailModel;
import com.example.giskesehatan.Models.UserModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiServices {
//    @GET("regis")
//    Call<ApiResponse<List<ModelPaket>>> getPaket();
//
//    @GET("notif")
//    Call<ApiResponse<List<ModelNotif>>> getNotif(@Query("id_user") String paramValue);
//
//    @POST("notif")
//    Call<ApiResponse> postData(@Body ModelNotif modelNotif);
//
//    @GET("permohonan")
//    Call<ApiResponse<List<ModelPelanggan>>> getPermohonan(@Query("id_user") String paramValue);
//
//    @GET("pembayaran")
////    Call<ApiResponse<List<ModelPembayaran>>> getPembayaran(@Query("id_pelanggan") String paramValue);
//
    @POST("login")
    Call<ApiResponse<List<SharedPreferenceModel>>> login(@Body LoginModel loginModel);

    @POST("register")
    Call<ApiResponse> register(@Body CombineUserModel combineUserModel);

    @POST("checkemail")
    Call<ApiResponse<List<UserModel>>> checkemail(@Body LoginModel loginModel);

    @POST("resetpassword")
    Call<ApiResponse> resetpassword(@Body UserModel userModel);


    @POST("user")
    Call<ApiResponse<List<UserDetailModel>>> user(@Header("Authorization") String token, @Body UserDetailModel userModel);
}