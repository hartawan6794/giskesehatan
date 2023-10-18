package com.example.giskesehatan.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TempatKesehatanModel implements Serializable {


    @SerializedName("id")
    private String id;
    @SerializedName("nama")
    private String nama;
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
    @SerializedName("nm_tabel")
    private String nmTabel;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
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

    public String getNmTabel() {
        return nmTabel;
    }

    public void setNmTabel(String nmTabel) {
        this.nmTabel = nmTabel;
    }
}
