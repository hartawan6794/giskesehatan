package com.example.giskesehatan.Interfaces;

import com.example.giskesehatan.Models.ApiResponse;
import com.example.giskesehatan.Models.ApiResponseWheater;
import com.example.giskesehatan.Models.LoginModel;
import com.example.giskesehatan.Models.CombineUserModel;
import com.example.giskesehatan.Models.RoutesResponseModel;
import com.example.giskesehatan.Models.SharedPreferenceModel;
import com.example.giskesehatan.Models.TempatKesehatanModel;
import com.example.giskesehatan.Models.UserDetailModel;
import com.example.giskesehatan.Models.UserModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

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
    Call<ApiResponse<List<UserDetailModel>>> user(@Header("Authorization") String token,
                                                  @Body UserDetailModel userModel);

    @POST("datatempat")
    Call<ApiResponse<List<TempatKesehatanModel>>> tempatKesehatanTerkini(@Header("Authorization") String token,
                                                                         @Body TempatKesehatanModel tempatKesehatanModel);

    @POST("getlayanan")
    Call<ApiResponse<List<TempatKesehatanModel>>> getlayanan(@Header("Authorization") String token,
                                                             @Body TempatKesehatanModel tempatKesehatanModel);

    @POST("search")
    Call<ApiResponse<List<TempatKesehatanModel>>> search(@Header("Authorization") String token,
                                                         @Body TempatKesehatanModel tempatKesehatanModel);

    @GET("maps/api/directions/json?")
    Call<RoutesResponseModel> getDirection(@Query("origin") String origin,
                                           @Query("destination") String destination,
                                           @Query("key") String key,
                                           @Query("mode") String mode);

    @GET("current.json")
    Call<ApiResponseWheater> getWheater(@Header("X-RapidAPI-Key") String key,
                                        @Header("X-RapidAPI-Host") String host,
                                        @Query("q") String q);
}