/**
 * Класс WeatherController
 *
 * @author : Хильченко А.Н
 * @project : HW_7_MVC
 * @date : 18.12.2021
 * @comments :
 */

package controller;

import model.DBRow;
import model.SituateWeather;
import model.WeatherResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.sqlite.JDBC;

import java.sql.*;
import java.util.ArrayList;

public class WeatherController {

    private static class DBController {
        private final String PATH_TO_DB = "jdbc:sqlite:weather.db";
        private Connection connection;

        public DBController() throws SQLException {
            DriverManager.registerDriver(new JDBC());
            this.connection = DriverManager.getConnection(PATH_TO_DB);
            try (PreparedStatement preparedStatement = this.connection.prepareStatement(
                    " CREATE TABLE `weatherTable` (\n" +
                            "`city`\tTEXT,\n" +
                            " `localDate`\tTEXT,\n" +
                            " `weatherText`\tTEXT,\n" +
                            " `temperature`\tREAL\n" +
                            ");"
            )) {
                preparedStatement.execute();

            } catch (Exception e) {

            }
        }

        public void addRow(DBRow row) {
            try (PreparedStatement preparedStatement = this.connection.prepareStatement(
                    "INSERT INTO weatherTable('city','localDate','weatherText','temperature') VALUES (?,?,?,?)"
            )) {
                preparedStatement.setObject(1, row.getCity());
                preparedStatement.setObject(2, row.getLocalDate());
                preparedStatement.setObject(3, row.getWeatherText());
                preparedStatement.setObject(4, row.getTemperature());
                preparedStatement.execute();
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        public ArrayList<DBRow> getWeatherHistory(String city) {
            ArrayList<DBRow> result = new ArrayList<>();
            try (PreparedStatement preparedStatement = this.connection.prepareStatement(
                    "SELECT * FROM weatherTable WHERE city=?"
            )) {
                preparedStatement.setObject(1, city);
                ResultSet resultSet= preparedStatement.executeQuery();
                while (resultSet.next()) {
                    DBRow row = new DBRow(
                            resultSet.getString("city"),
                            resultSet.getString("localDate"),
                            resultSet.getString("weatherText"),
                            resultSet.getDouble("temperature")
                    );
                    result.add(row);
                }
            } catch (Exception e) {
                System.out.println(e);
            }

            return result;
        }

    }


    private static ObjectMapper objectMapper = new ObjectMapper();
    private static String APPID = "1c87217667796841773e3feec12cbc74";

    static String host = "api.openweathermap.org";
    static String segment1 = "data";
    static String segment2 = "2.5";
    static String segment3 = "forecast";

    public static WeatherResponse getWeatherFromCity(String city) throws IOException {
        String urlAddres = "http://api.openweathermap.org/data/2.5/forecast?q="+city+"&appid="+APPID+"&units=metric";
        StringBuffer content = new StringBuffer();
        try {
            URL url = new URL(urlAddres);
            URLConnection urlConn = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

            String line;
            while((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
            }
            bufferedReader.close();
        } catch(Exception e) {
            System.out.println("Такого города нет в openweathermap!");
            return new WeatherResponse();
        }
        WeatherResponse response = objectMapper.readValue(content.toString(), WeatherResponse.class);

        return response;
    }

    public static WeatherResponse getWeatherFromCityV2(String city) throws IOException {
        String urlAddres = "http://api.openweathermap.org/data/2.5/forecast?q="+city+"&appid="+APPID+"&units=metric";
        StringBuffer content = new StringBuffer();
        try {
            OkHttpClient client = new OkHttpClient();

            HttpUrl httpUrl = new HttpUrl.Builder()
                    .scheme("http")
                    .host(host)
                    .addPathSegment(segment1)
                    .addPathSegment(segment2)
                    .addPathSegment(segment3)
                    .addQueryParameter("q",city)
                    .addQueryParameter("lang","ru")
                    .addQueryParameter("units","metric")
                    .addQueryParameter("APPID",APPID)
                    .build();

            Request request = new Request.Builder()
                    .url(httpUrl)
                    .build();

            Response response = client.newCall(request).execute();
            String res = response.body().string();
            WeatherResponse result = objectMapper.readValue(res, WeatherResponse.class);
            DBController controller = new DBController();
            for (SituateWeather i: result.getList()) {
                controller.addRow(new DBRow(
                        result.getCity().getName(),
                        i.getDt_txt(),
                        i.getWeather().toString().replace("[", "").replace("]", ""),
                        i.getMain().getTemp()));
            }
            return result;

        } catch(Exception e) {
            return new WeatherResponse();
        }
    }

    public static void printCityHistory(String city) throws SQLException{
        DBController controller = new DBController();
        ArrayList<DBRow> result = controller.getWeatherHistory(city);
        for (DBRow i: result) {
            System.out.println(i.toCuteString());
        }
    }
}
