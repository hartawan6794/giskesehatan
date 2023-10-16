package com.example.giskesehatan.Interfaces;

import com.example.giskesehatan.Models.ApiResponse;
import com.example.giskesehatan.Models.LoginModel;
import com.example.giskesehatan.Models.CombineUserModel;
import com.example.giskesehatan.Models.SharedPreferenceModel;
import com.example.giskesehatan.Models.UserDetailModel;
import com.example.giskesehatan.Models.UserModel;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiServices {
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

//    @Multipart
//    @POST("uploadimage")
//    Call<ApiResponse> uploadImage(
//            @Header("Authorization") String bearerToken, // Header "Authorization" dengan token bearer
//            @Part MultipartBody.Part image,
//            @Part("id_user_detail") RequestBody id_user_detail
//    );
}