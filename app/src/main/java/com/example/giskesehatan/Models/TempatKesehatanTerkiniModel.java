package com.example.giskesehatan.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TempatKesehatanTerkiniModel implements Serializable {

    @SerializedName("id_klinik")
    private String idKlinik;
    @SerializedName("nama_klinik")
    private String nmKlinik;
    @SerializedName("kecamatan")
    private String kecamatan;
    @SerializedName("deskripsi")
    private String deskripsi;
    @SerializedName("Latitude")
    private Double latitude;
    @SerializedName("longitude")
    private Double longitude;
    @SerializedName("gambar")
    private String gambar;
    @SerializedName("notelp")
    private String notelp;

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getIdKlinik() {
        return idKlinik;
    }

    public void setIdKlinik(String idKlinik) {
        this.idKlinik = idKlinik;
    }

    public String getNmKlinik() {
        return nmKlinik;
    }

    public void setNmKlinik(String nmKlinik) {
        this.nmKlinik = nmKlinik;
    }

    public String getKecamatan() {
        return kecamatan;
    }

    public void setKecamatan(String kecamatan) {
        this.kecamatan = kecamatan;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }
    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getNotelp() {
        return notelp;
    }

    public void setNotelp(String notelp) {
        this.notelp = notelp;
    }
}
