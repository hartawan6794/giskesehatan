package com.example.giskesehatan.Models;

import java.util.List;

public class Leg {
    private Distance distance;
    private Duration duration;
    private String start_address;
    private String end_address;
    private List<Step> steps;

    public Distance getDistance() {
        return distance;
    }

    public Duration getDuration() {
        return duration;
    }

    public String getStartAddress() {
        return start_address;
    }

    public String getEndAddress() {
        return end_address;
    }

    public List<Step> getSteps() {
        return steps;
    }
}
