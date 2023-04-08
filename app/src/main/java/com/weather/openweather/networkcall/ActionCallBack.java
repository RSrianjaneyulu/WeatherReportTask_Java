package com.weather.openweather.networkcall;

import java.util.Map;

public interface ActionCallBack {
    void onCallback(Map<String, Object> result);
}
