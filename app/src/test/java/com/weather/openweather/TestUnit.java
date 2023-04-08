package com.weather.openweather;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class TestUnit {
    CityDetails citiInfo = new CityDetails();
    String city = citiInfo.cityData("Chicago");
    String cityName = "Chicago";
    @Test
    public void testCity() {
        //check for equality
        assertEquals(city, cityName);
    }
}
