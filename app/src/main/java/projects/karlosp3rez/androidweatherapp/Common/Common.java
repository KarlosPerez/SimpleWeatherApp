package projects.karlosp3rez.androidweatherapp.Common;

import android.location.Location;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Common {

    //Se crea una API key desde la web de OpenWeatherMap
    public static final String APP_ID = "981b35f51fb519dc2415d752c23fccf4";
    public static final String UNIDAD_MEDIDA = "metric";
    public static Location localizacion_Actual = null;

    public static String convertirUnidadesAFecha(int dateTime) {
        Date date = new Date(dateTime*1000L);
        DateFormat sdf = new SimpleDateFormat("HH:mm dd EEE MM yyyy", Locale.US);
        return sdf.format(date);
    }

    public static String convertirUnidadesAHoras(int sunrise) {
        Date date = new Date(sunrise*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.US);
        return sdf.format(date);
    }
}
