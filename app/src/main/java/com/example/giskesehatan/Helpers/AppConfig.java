package com.example.giskesehatan.Helpers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppConfig {
    public final static String BASE_URL = "http://internetbill.hanazumaedzaulfa.com/";
    public final static String BASE_URL_LOCAL = "http://192.168.186.114/kesehatan/public/api/";
    public final static String BASE_URL_BANNER = "http://cms.hanazumaedzaulfa.com/public/img/banner/";

    public static boolean isEmailValid(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

    public static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        // Ambil huruf pertama dan ubah ke huruf besar
        String firstLetter = input.substring(0, 1).toUpperCase();

        // Ambil sisa string setelah huruf pertama dan jadikan huruf kecil
        String restOfString = input.substring(1).toLowerCase();

        // Gabungkan kembali kedua bagian
        return firstLetter + restOfString;
    }

}
