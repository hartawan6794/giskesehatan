package com.example.giskesehatan.Models;

import java.io.Serializable;

public class RegisterModel implements Serializable {
    private UserModel userModel;
    private UserDetailModel userDetailModel;

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public UserDetailModel getUserDetailModel() {
        return userDetailModel;
    }

    public void setUserDetailModel(UserDetailModel userDetailModel) {
        this.userDetailModel = userDetailModel;
    }
}
