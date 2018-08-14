package projects.karlosp3rez.androidweatherapp.Retrofit;

import io.reactivex.Observable;
import projects.karlosp3rez.androidweatherapp.Model.WeatherForecastResult;
import projects.karlosp3rez.androidweatherapp.Model.WeatherResult;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IOpenWeatherMap {

    @GET("weather")
    Observable<WeatherResult> getWeatherByLatLng(@Query("lat") String latitud,
                                                 @Query("lon") String longitud,
                                                 @Query("appid") String appid,
                                                 @Query("units") String unidades);

    @GET("forecast")
    Observable<WeatherForecastResult> getForecastWeatherByLatLng(@Query("lat") String latitud,
                                                                 @Query("lon") String longitud,
                                                                 @Query("appid") String appid,
                                                                 @Query("units") String unidades);


}
