package projects.karlosp3rez.androidweatherapp.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import projects.karlosp3rez.androidweatherapp.Common.Common;
import projects.karlosp3rez.androidweatherapp.Model.WeatherResult;
import projects.karlosp3rez.androidweatherapp.R;
import projects.karlosp3rez.androidweatherapp.Retrofit.IOpenWeatherMap;
import projects.karlosp3rez.androidweatherapp.Retrofit.RetrofitClient;
import retrofit2.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 */
public class TodayWeatherFragment extends Fragment {

    ImageView imgWeather;
    TextView txtCityName, txtHumidity, txtSunrise, txtSunset, txtPressure, txtTemperature,
            txtDescription, txtDateTime, txtWind, txtGeoCoord, txtWeatherDescription;
    CardView weatherPanel;
    ProgressBar load;

    CompositeDisposable compositeDisposable;
    IOpenWeatherMap iOpenWeatherMap;

    static TodayWeatherFragment instance;

    public static TodayWeatherFragment getInstance() {
        if(instance == null)
            instance = new TodayWeatherFragment();
        return instance;
    }

    public TodayWeatherFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        iOpenWeatherMap = retrofit.create(IOpenWeatherMap.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.fragment_today_weather, container, false);

        inicializarUI(itemView);
        cargarInformacionClimaHoy();

        return itemView;
    }

    private void inicializarUI(View itemView) {
        imgWeather = itemView.findViewById(R.id.imgWeather);
        txtCityName = itemView.findViewById(R.id.txtCityName);
        txtWeatherDescription = itemView.findViewById(R.id.txtWeatherDescription);
        txtWind = itemView.findViewById(R.id.txtWind);
        txtHumidity =  itemView.findViewById(R.id.txtHumidity);
        txtSunrise = itemView.findViewById(R.id.txtSunrise);
        txtSunset =  itemView.findViewById(R.id.txtSunset);
        txtPressure = itemView.findViewById(R.id.txtPressure);
        txtCityName = itemView.findViewById(R.id.txtCityName);
        txtTemperature = itemView.findViewById(R.id.txtTemperature);
        txtDescription = itemView.findViewById(R.id.txtDescription);
        txtDateTime = itemView.findViewById(R.id.txtDateTime);
        txtGeoCoord = itemView.findViewById(R.id.txtGeoCoord);

        weatherPanel = itemView.findViewById(R.id.weather_panel);
        load = itemView.findViewById(R.id.loading);
    }

    private void cargarInformacionClimaHoy() {
        compositeDisposable.add(iOpenWeatherMap.getWeatherByLatLng(String.valueOf(Common.localizacion_Actual.getLatitude()),
                String.valueOf(Common.localizacion_Actual.getLongitude()),Common.APP_ID,
                Common.UNIDAD_MEDIDA)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherResult>() {
                    @Override
                    public void accept(WeatherResult weatherResult) throws Exception {
                        mostrarInformacionClimaHoy(weatherResult);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void mostrarInformacionClimaHoy(WeatherResult weatherResult) {
        //Cargar imagen
        Picasso.get().load(new StringBuilder(Common.IMG_URL).append(weatherResult.getWeather().get(0).getIcon())
                .append(".png").toString()).into(imgWeather);
        //Cargar información a los campos
        txtCityName.setText(weatherResult.getName());
        txtDescription.setText(new StringBuilder(getString(R.string.hint_weather_in)).append(" ")
                .append(weatherResult.getName()).toString());
        txtTemperature.setText(new StringBuilder(
                String.valueOf(weatherResult.getMain().getTemp())).append("°C").toString());
        txtDateTime.setText(Common.convertirUnidadesAFecha(weatherResult.getDt()));
        txtWeatherDescription.setText(weatherResult.getWeather().get(0).getDescription());
        txtPressure.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getPressure())).append(getString(R.string.pressure_unit)).toString());
        txtHumidity.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getHumidity())).append(" %").toString());
        txtSunrise.setText(Common.convertirUnidadesAHoras(weatherResult.getSys().getSunrise()));
        txtSunset.setText(Common.convertirUnidadesAHoras(weatherResult.getSys().getSunset()));
        txtGeoCoord.setText(new StringBuilder(weatherResult.getCoord().toString()).toString());
        txtWind.setText(new StringBuilder(getString(R.string.hint_speed)).append(weatherResult.getWind().getSpeed())
                .append(getString(R.string.hint_speed_unit)));
        //Mostrar panel
        weatherPanel.setVisibility(View.VISIBLE);
        load.setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

}
