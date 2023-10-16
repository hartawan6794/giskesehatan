package com.example.giskesehatan.Helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppConfig {
    public final static String BASE_URL = "http://cmsgis.hanazumaedzaulfa.com/public/api/";
//    public final static String BASE_URL_LOCAL = "http://192.168.186.114/kesehatan/public/api/";

    public static boolean isEmailValid(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }
    public static String dateIndonesia(String tanggal){
        String waktuIndonesia = "";
        // Buat formatter untuk mengurai string waktu
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        if(tanggal.equals("0000-00-00") || tanggal.equals("") || tanggal.equals("null")){
            waktuIndonesia = "Belum di set";
        }else {
            try {
                // Parse string waktu ke objek Date
                Date date = formatter.parse(tanggal);

                // Tentukan zona waktu Indonesia (Waktu Indonesia Barat - WIB)
                TimeZone timeZone = TimeZone.getTimeZone("Asia/Jakarta");

                // Atur zona waktu objek Date
                formatter.setTimeZone(timeZone);
                // Buat formatter baru untuk format tampilan "dd MMMM yyyy"
                SimpleDateFormat displayFormatter = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));

                // Format ulang objek Date ke string dengan zona waktu Indonesia
                waktuIndonesia = displayFormatter.format(date);

                // Sekarang Anda memiliki waktu dengan zona waktu Indonesia (WIB)
//            System.out.println("Waktu Indonesia: " + waktuIndonesia);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return waktuIndonesia;
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

    public static String keyToken(String token){
        String key = "Bearer " + token;

        return key;
    }

}
