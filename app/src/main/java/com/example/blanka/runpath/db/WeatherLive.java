package com.example.blanka.runpath.db;

/**
 * Created by BLANKA on 2017/4/12 0012.
 */

public class WeatherLive {
    private String weather;
    private String temperature;
    private String windDirection;
    private String humidity;
    private String windPower;

    public  String getWindPower() {
        return windPower;
    }

    public void setWindPower(String windPower) {
        windPower = windPower;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }
}
