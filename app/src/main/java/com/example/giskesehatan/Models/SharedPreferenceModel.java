package com.example.giskesehatan.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SharedPreferenceModel implements Serializable {
    @SerializedName("id_user")
    private String idUser;
    @SerializedName("email_user")
    private String emailUser;
    @SerializedName("nama_lengkap")
    private String deviceId;
    @SerializedName("username")
    private String username;
    @SerializedName("img_user")
    private String telpon;
    @SerializedName("bearer_token")
    private String bearerToken;

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getEmailUser() {
        return emailUser;
    }

    public void setEmailUser(String emailUser) {
        this.emailUser = emailUser;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTelpon() {
        return telpon;
    }

    public void setTelpon(String telpon) {
        this.telpon = telpon;
    }

    public String getBearerToken() {
        return bearerToken;
    }

    public void setBearerToken(String bearerToken) {
        this.bearerToken = bearerToken;
    }
}
