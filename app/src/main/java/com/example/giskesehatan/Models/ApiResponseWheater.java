package com.example.giskesehatan.Models;

public class ApiResponseWheater {
    private Location location;
    private CurrentWeather current;

    // Tambahkan constructor, getter, dan setter sesuai kebutuhan


    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public CurrentWeather getCurrent() {
        return current;
    }

    public void setCurrent(CurrentWeather current) {
        this.current = current;
    }
}
