package com.londonappbrewery.climapm;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherDataModel {

    // TODO: Declare the member variables here
        private String mTemperature;
        private String mCity;
        private int mCondition;
        private String mIconName;

    // TODO: Create a WeatherDataModel from a JSON:

    public WeatherDataModel() {
    }

    public static WeatherDataModel fromJson (JSONObject weather) {

        try {
            WeatherDataModel weatherData = new WeatherDataModel();
            weatherData.setCity(weather.getString("name"));
            weatherData.setCondition(weather.getJSONArray("weather").getJSONObject(0).getInt("id"));
            weatherData.setIconName(updateWeatherIcon(weatherData.getCondition()));

            // The API return temperature in Kelvin, we have to change it to celsius
            int tempResult = (int) Math.rint(weather.getJSONObject("main").getDouble("temp") - 273.15);
            weatherData.setTemperature(Integer.toString(tempResult));

            return weatherData;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    // TODO: Uncomment to this to get the weather image name from the condition:
    private static String updateWeatherIcon(int condition) {

        if (condition >= 0 && condition < 300) {
            return "tstorm1";
        } else if (condition >= 300 && condition < 500) {
            return "light_rain";
        } else if (condition >= 500 && condition < 600) {
            return "shower3";
        } else if (condition >= 600 && condition <= 700) {
            return "snow4";
        } else if (condition >= 701 && condition <= 771) {
            return "fog";
        } else if (condition >= 772 && condition < 800) {
            return "tstorm3";
        } else if (condition == 800) {
            return "sunny";
        } else if (condition >= 801 && condition <= 804) {
            return "cloudy2";
        } else if (condition >= 900 && condition <= 902) {
            return "tstorm3";
        } else if (condition == 903) {
            return "snow5";
        } else if (condition == 904) {
            return "sunny";
        } else if (condition >= 905 && condition <= 1000) {
            return "tstorm3";
        }

        return "dunno";
    }

    // TODO: Create getter methods for temperature, city, and icon name:


    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public int getCondition() {
        return mCondition;
    }

    public void setCondition(int condition) {
        mCondition = condition;
    }

    public String getIconName() {
        return mIconName;
    }

    public void setIconName(String iconName) {
        mIconName = iconName;
    }

    public String getTemperature() {
        return mTemperature + "°";
    }

    public void setTemperature(String temperature) {
        mTemperature = temperature;
    }
}
