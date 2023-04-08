package com.weather.openweather.dto;

import java.io.Serializable;
import java.util.List;

public class CommonWeatherDTO implements Serializable {
    private CoordinatesDTO coord;
    private List<WeatherDTO> weather;
    private String base;
    private MainDTO main;
    private Long visibility;
    private WindDTO wind;
    private CloudDTO clouds;
    private Long dt;
    private SysDTO sys;
    private Long timezone;
    private Long id;
    private String name;
    private Long cod;

    public CoordinatesDTO getCoord() {
        return coord;
    }

    public void setCoord(CoordinatesDTO coord) {
        this.coord = coord;
    }

    public List<WeatherDTO> getWeather() {
        return weather;
    }

    public void setWeather(List<WeatherDTO> weather) {
        this.weather = weather;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public MainDTO getMain() {
        return main;
    }

    public void setMain(MainDTO main) {
        this.main = main;
    }

    public Long getVisibility() {
        return visibility;
    }

    public void setVisibility(Long visibility) {
        this.visibility = visibility;
    }

    public WindDTO getWind() {
        return wind;
    }

    public void setWind(WindDTO wind) {
        this.wind = wind;
    }

    public CloudDTO getClouds() {
        return clouds;
    }

    public void setClouds(CloudDTO clouds) {
        this.clouds = clouds;
    }

    public Long getDt() {
        return dt;
    }

    public void setDt(Long dt) {
        this.dt = dt;
    }

    public SysDTO getSys() {
        return sys;
    }

    public void setSys(SysDTO sys) {
        this.sys = sys;
    }

    public Long getTimezone() {
        return timezone;
    }

    public void setTimezone(Long timezone) {
        this.timezone = timezone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCod() {
        return cod;
    }

    public void setCod(Long cod) {
        this.cod = cod;
    }
}

