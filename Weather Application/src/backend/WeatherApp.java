package backend;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.http.HttpClient;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

// retreive weather data from API - this backend logic will fetch the latest weather
// data from the external API and return it. The GUI will
// display this data to the user
public class WeatherApp {
    // -------step-1------------
    // fetch weather data for given location
    public static JSONObject getWeatherData(String locationName) {
        // get location coordinates using the geolocation API
        // get location coordinates using the geolocation API
        JSONArray locationData = getLocationData(locationName);
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        // build API request URL with location coordinates
        String urlString = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=" + latitude + "&longitude=" + longitude +
                "&hourly=temperature_2m,relativehumidity_2m,weathercode,windspeed_10m&timezone=America%2FLos_Angeles";

        try {
            // call api and get response
            HttpURLConnection conn = fetchApiResposne(urlString);
            // check for response status
            if (conn.getResponseCode() != 200) {
                System.out.println("Error: Could not connect to API");
                return null;
            }

            // store resulting json data
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while (scanner.hasNext()) {
                // read and store into the string builder
                resultJson.append(scanner.nextLine());
            }
            // close scanner
            scanner.close();

            // close url connection
            conn.disconnect();
            // parse through our data
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));
            // retrieve hourly data
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");

            // we want to get the current hour's data
            // so we need to get the index of our current hour
            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);

            // get temperature
            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            // get weather code
            // will be used to find the weather description
            JSONArray weathercode = (JSONArray) hourly.get("weathercode");
            String weatherCondition = convertWeatherCode((long) weathercode.get(index));

            // ------------step 11--------------------
            // get humidity
            JSONArray relativeHumidity = (JSONArray) hourly.get("relativehumidity_2m");
            long humidity = (long) relativeHumidity.get(index);

            // get windspeed
            JSONArray windspeedData = (JSONArray) hourly.get("windspeed_10m");
            double windspeed = (double) windspeedData.get(index);
            // build the weather json data object that we are going to access in our
            // frontend
            // to store the data we will need to give the value and id in a way
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);

            return weatherData;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // retieves geographic cordinates for given location name
    public static JSONArray getLocationData(String locationName) {
        locationName = locationName.replaceAll(" ", "+");
        // build API request URL with location coordinates
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName + "&count=10&language=en&format=json";

        try {
            // call the api and get responce
            // to make a HTTP request like our API call, we need a HTTP client like
            // HTTPURLConnection class
            // we are going to make a separate method to instantiate it because we will be
            // doing this multiple time in this class

            HttpURLConnection conn = fetchApiResposne(urlString);
            // chack responce status
            // 200 means succesfully connection
            if (conn.getResponseCode() != 200) {
                System.out.println("could not conect to Api");
                return null;
            } else {
                // store the responce of the api result
                StringBuilder resultsJson = new StringBuilder();
                // we will use a scanner to read the json data that is returnesd from our api
                // Call
                // we do this by using a while loop and using the hasNext()

                Scanner scanner = new Scanner(conn.getInputStream());
                // read and store the resulting jason data into our string builder
                // if thir is jason data to be read, then we store it into out resultjason
                while (scanner.hasNext()) {
                    resultsJson.append(scanner.nextLine());
                }
                // close scanner
                scanner.close();

                // close scanner
                conn.disconnect();

                JSONParser parser = new JSONParser();
                JSONObject resulJsonObj = (JSONObject) parser.parse(String.valueOf(resultsJson));

                JSONArray locationData = (JSONArray) resulJsonObj.get("results");
                return locationData;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // could not find location
        return null;

    }

    private static HttpURLConnection fetchApiResposne(String urlString) {
        try {
            // attempt to create a connection
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // ser requast method
            conn.setRequestMethod("GET");
            // conect to API
            conn.connect();
            return conn;
        } catch (IOException e) {
            e.printStackTrace();
        }

        // could not make any connection
        return null;
    }

    private static int findIndexOfCurrentTime(JSONArray timelist) {
        String currentTime = getCurrentTime();
        // iterate through the time list and see which one matches our current time
        for (int i = 0; i < timelist.size(); i++) {
            String time = (String) timelist.get(i);
            if (time.equalsIgnoreCase(currentTime)) {
                // return the index
                return i;
            }
        }

        return 0;
    }

    public static String getCurrentTime() {
        // get current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();

        // format date to be 2023-09-02T00:00 (this is how is is read in the API)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        // format and print the current date and time
        String formattedDateTime = currentDateTime.format(formatter);

        return formattedDateTime;

    }

    private static String convertWeatherCode(long weathercode) {
        String weatherCondition = "";
        if (weathercode == 0L) {
            // clear
            weatherCondition = "Clear";
        } else if (weathercode > 0L && weathercode <= 3L) {
            // cloudy
            weatherCondition = "Cloudy";
        } else if ((weathercode >= 51L && weathercode <= 67L)
                || (weathercode >= 80L && weathercode <= 99L)) {
            // rain
            weatherCondition = "Rain";
        } else if (weathercode >= 71L && weathercode <= 77L) {
            // snow
            weatherCondition = "Snow";
        }

        return weatherCondition;
    }

}