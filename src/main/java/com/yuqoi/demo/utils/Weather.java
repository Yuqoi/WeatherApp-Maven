package com.yuqoi.demo.utils;

import javafx.scene.paint.Color;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Weather {
    private final Long weatherCode;
    private final double temperature;
    private final double precipitation;

    public Weather(String cityName) throws IOException, ParseException, org.json.simple.parser.ParseException {
        JSONObject returned = getWeatherData(cityName);
        assert returned != null;
        temperature = (double) returned.get("temperature");
        precipitation = (double) returned.get("precipitation");
        weatherCode = (Long) returned.get("weather");
    }

    public Long getWeatherCode() {
        return weatherCode;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getPrecipitation() {
        return precipitation;
    }


    public static String getWeatherIcon(Long weatherCode){
        int check = weatherCode.intValue();
        return switch (check) {
            case 0 -> "clear.png";
            case 1, 2, 3, 45, 48 -> "cloudy.png";
            case 51, 53, 55, 56, 57, 61, 63, 65, 66, 67 -> "rain.png";
            case 71, 73, 75, 77, 85, 86 -> "snow.png";
            case 95, 96, 99 -> "thunderstorm.png";
            default -> "";
        };
    }

    // method for letting user know if a temperature is below 0
    public static Color getTemperatureLevel(double temperature){
        if (temperature <= 0){
            return Color.rgb(255,0,0, 0.8);
        }else{
            return Color.rgb(255,255,255);
        }
    }

    public static JSONObject getWeatherData(String locationName) throws IOException, ParseException, org.json.simple.parser.ParseException {

        JSONArray locationData = getLocationData(locationName);

        assert locationData != null;
        JSONObject location = (JSONObject) locationData.getFirst();

        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        String stringURL = "https://api.open-meteo.com/v1/forecast?latitude=" +
                latitude+"&longitude=" +
                longitude+ "&hourly=temperature_2m,precipitation,weather_code";

        HttpsURLConnection conn = fetchApiResonse(stringURL);
        assert conn != null;
        if (!(conn.getResponseCode() == 200)){return null;}

        StringBuilder sb = new StringBuilder();
        Scanner sc = new Scanner(conn.getInputStream());
        while (sc.hasNext()){
            sb.append(sc.nextLine());
        }
        sc.close();
        conn.disconnect();


        JSONParser locationParser = new JSONParser();
        JSONObject locationObj = (JSONObject) locationParser.parse(String.valueOf(sb));
        JSONObject hourly = (JSONObject) locationObj.get("hourly");

        JSONArray timeObj = (JSONArray) hourly.get("time");

        //index to map data
        int index = getIndex(timeObj, getCurrentTime());

        // temp
        JSONArray tempObj = (JSONArray) hourly.get("temperature_2m");
        Double temp = (Double) tempObj.get(index);

        // prec
        JSONArray precObj = (JSONArray) hourly.get("precipitation");
        Double precipitation = (Double) precObj.get(index);

        //
        JSONArray weatherObj = (JSONArray) hourly.get("weather_code");
        Long weatherCode = (Long) weatherObj.get(index);

        JSONObject returnData = new JSONObject();
        returnData.put("temperature", temp);
        returnData.put("precipitation", precipitation);
        returnData.put("weather", weatherCode);
        return returnData;
    }

        //-------------------------------------------------------
        //  PRIVATE CLASSES
        //  Only used for things inside the class
        //-------------------------------------------------------

    private static int getIndex(JSONArray obj, String currentTime){
        for (int i = 0; i < obj.size(); i++) {
            String time = obj.get(i).toString();
            if (time.equalsIgnoreCase(currentTime)){
                return i;
            }
        }
        return 0;
    }
    private static JSONArray getLocationData(String locationName){
        locationName = locationName.replaceAll(" ", "+");

        String stringUrl = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName +"&count=10&language=en&format=json";
        try{
            HttpsURLConnection conn = fetchApiResonse(stringUrl);
            assert conn != null;
            if (!(conn.getResponseCode() == 200)){
                System.out.println("nie dziala"); return null;}

            StringBuilder resultString = new StringBuilder();
            Scanner sc = new Scanner(conn.getInputStream());
            while(sc.hasNext()){
                resultString.append(sc.nextLine());
            }
            sc.close();
            conn.disconnect();

            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultString));

            return (JSONArray) resultJsonObj.get("results");
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    private static HttpsURLConnection fetchApiResonse(String stringUrl) {
        try{
            URL url = new URL(stringUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            conn.connect();
            return conn;
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }
    private static String getCurrentTime(){
        LocalDateTime currDataTime = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");
        return currDataTime.format(dtf);

    }
}
