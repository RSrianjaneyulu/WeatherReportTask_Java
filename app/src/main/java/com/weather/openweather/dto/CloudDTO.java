package com.weather.openweather.dto;

import java.io.Serializable;

public class CloudDTO implements Serializable {

    private Long all;

    public Long getAll() {
        return all;
    }

    public void setAll(Long all) {
        this.all = all;
    }
}

