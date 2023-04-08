package com.weather.openweather.dto;

import java.io.Serializable;

public class WindDTO implements Serializable {
    private Double speed;
    private Long deg;

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Long getDeg() {
        return deg;
    }

    public void setDeg(Long deg) {
        this.deg = deg;
    }
}
