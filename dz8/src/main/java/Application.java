/**
 * Класс Application
 *      Класс дял размещения точки входа.
 * @author : Хильченко А.Н
 * @project : HW_7_MVC
 * @date : 18.12.2021
 * @comments :
 *      Предназначен дял размещения точки входа и обработки входящих параметров. Параметры планирую передавать через
 *      командную строку.
 */

import java.io.IOException;
import java.sql.SQLException;

import view.WeatherViewer;

public class Application {
    public static void main(String[] args) throws IOException, SQLException {
        WeatherViewer.processCity();

    }

}

