package com.example.weatherpro;

import org.json.JSONException;
import org.json.JSONObject;

public class Data {

    private String mTemperature, mIcon, mCity, mWeatherType;
    private int mContidtion;

    public static Data fromJson(JSONObject jsonObject) {
        try {
            Data data = new Data();
            data.mCity=jsonObject.getString("name");
            data.mContidtion=jsonObject.getJSONArray("weather").getJSONObject(0).getInt("id");
            data.mWeatherType=jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");
            data.mIcon= updateWeatherIcon(data.mContidtion);
            double tempResult = jsonObject.getJSONObject("main").getDouble("temp")-273.15;
            int roundedValue=(int)Math.rint(tempResult);
            data.mTemperature=Integer.toString(roundedValue);
            return data;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String updateWeatherIcon(int condition) {
        if (condition >= 0 && condition <= 300) {
            return "storm";
        }
        else if (condition >= 300 && condition <= 500) {
            return "rain";
        }
        else if (condition >= 500 && condition <= 600) {
            return "rain";
        }
        else if (condition >= 600 && condition <= 700) {
            return "snow";
        }
        else if (condition >= 700 && condition <= 771) {
            return "fog";
        }
        else if (condition >= 772 && condition <= 800) {
            return "rain_clouds";
        }
        else if (condition == 800) {
            return "sunny";
        }
        else if (condition >= 801 && condition <= 804) {
            return "light_cloudy";
        }
        else if (condition >= 900 && condition <= 902) {
            return "storm";
        }
        if (condition == 903) {
            return "snow";
        }
        if (condition == 904) {
            return "sunny";
        }
        if (condition >= 905 && condition <= 1000) {
            return "storm";
        }
        return "unaviable";
    }

    public String getmTemperature() {
        return mTemperature + "°C";
    }

    public String getmIcon() {
        return mIcon;
    }

    public String getmCity() {
        return mCity;
    }

    public String getmWeatherType() {
        return mWeatherType;
    }
}
