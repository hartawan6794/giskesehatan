package com.example.giskesehatan.Models;

import java.util.List;

public class Step {
    private Distance distance;
    private Duration duration;
    private String html_instructions;
    private Polyline polyline;

    public Distance getDistance() {
        return distance;
    }

    public Duration getDuration() {
        return duration;
    }

    public String getHtmlInstructions() {
        return html_instructions;
    }

    public Polyline getPolyline() {
        return polyline;
    }
}
