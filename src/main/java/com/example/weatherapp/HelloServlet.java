package com.example.weatherapp;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "helloServlet", value = "/hello-servlet")
public class HelloServlet extends HttpServlet {
    private String message;

    public void init() {
        message = "Hello World!";
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        // Hello
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>" + message + "</h1>");
        out.println("</body></html>");
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");


        //Api Setup
         String apiKey = "919108f40a98916ccc30ab4f5f7ffd8e";

         //Get the City from the Input
        String inputData = request.getParameter("city");

        // Creating Api Url
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q="+inputData+"&appid="+apiKey;
        try {
            //Api Integration
            URL url = new URL(apiUrl);

            // Establishing Url Connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Reading Data from Network
            InputStream inputStream = connection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);


            // Store Data In String
            StringBuilder responseData = new StringBuilder();

            // Scan Single data
            Scanner scanner = new Scanner(inputStreamReader);

            while (scanner.hasNext()) {
                responseData.append(scanner.nextLine());
            }
            scanner.close();
            System.out.println(responseData);

            // TypeCasting Data
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(responseData.toString(), JsonObject.class);
            System.out.println(jsonObject);
            //Date & Time
            long dateTimestamp = jsonObject.get("dt").getAsLong() * 1000;
            String date = new Date(dateTimestamp).toString();
            String cityName = jsonObject.get("name").getAsString();

            //Temperature
            double temperatureKelvin = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
            int temperatureCelsius = (int) (temperatureKelvin - 273.15);

            //Humidity
            int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();

            //Wind Speed
            double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();

            //Weather Condition
            String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();

            // Set the data as request attributes (for sending to the jsp page)
            request.setAttribute("date", date);
            request.setAttribute("city", cityName);
            request.setAttribute("temperature", temperatureCelsius);
            request.setAttribute("weatherCondition", weatherCondition);
            request.setAttribute("humidity", humidity);
            request.setAttribute("windSpeed", windSpeed);
            request.setAttribute("weatherData", jsonObject.toString());
System.out.println(weatherCondition);
            connection.disconnect();
            // Forward the request to the weather.jsp page for rendering
            request.getRequestDispatcher("index.jsp").forward(request, response);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }

    }

    public void destroy() {
    }
}