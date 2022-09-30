/**
 * Класс Start
 *      Класс для размещения точки входа.
 * @author : Хильченко А.Н
 * @project : HW_7_MVC
 * @date : 18.12.2021
 * @comments :
 *      Предназначен для размещения точки входа и обработки входящих параметров. Параметры планирую передавать через
 *      командную строку.
 */

import java.io.IOException;
import view.WeatherViewer;

public class Start {
    public static void main(String[] args) throws IOException {
        WeatherViewer.processCity();
    }

}
