package projects.karlosp3rez.androidweatherapp.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import projects.karlosp3rez.androidweatherapp.Adapter.WeatherForecastAdapter;
import projects.karlosp3rez.androidweatherapp.Common.Common;
import projects.karlosp3rez.androidweatherapp.Model.WeatherForecastResult;
import projects.karlosp3rez.androidweatherapp.R;
import projects.karlosp3rez.androidweatherapp.Retrofit.IOpenWeatherMap;
import projects.karlosp3rez.androidweatherapp.Retrofit.RetrofitClient;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForecastFragment extends Fragment {

    CompositeDisposable compositeDisposable;
    IOpenWeatherMap iOpenWeatherMap;

    TextView txtCityName, txtGeoCoord;
    RecyclerView forecastRecycler;

    static ForecastFragment instance;

    public static ForecastFragment getInstance() {
        if(instance == null)
            instance = new ForecastFragment();
        return instance;
    }

    public ForecastFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        iOpenWeatherMap = retrofit.create(IOpenWeatherMap.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_forecast, container, false);

        txtCityName = itemView.findViewById(R.id.txtCityName);
        txtGeoCoord = itemView.findViewById(R.id.txtGeoCoord);

        forecastRecycler = (RecyclerView) itemView.findViewById(R.id.recycler_forecast);
        forecastRecycler.setHasFixedSize(true);
        forecastRecycler.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        cargarInformacionPronosticoClima();

        return itemView;
    }

    private void cargarInformacionPronosticoClima() {
        compositeDisposable.add(iOpenWeatherMap.getForecastWeatherByLatLng(String.valueOf(Common.localizacion_Actual.getLatitude()),
                String.valueOf(Common.localizacion_Actual.getLongitude()),
                Common.APP_ID,Common.UNIDAD_MEDIDA)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<WeatherForecastResult>() {
            @Override
            public void accept(WeatherForecastResult weatherForecastResult) throws Exception {
                mostrarInformacionPronosticoClima(weatherForecastResult);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.d("ERROR", throwable.getMessage());
            }
        }));
    }

    private void mostrarInformacionPronosticoClima(WeatherForecastResult weatherForecastResult) {
        txtCityName.setText(new StringBuilder(weatherForecastResult.city.name));
        txtGeoCoord.setText(new StringBuilder(weatherForecastResult.city.coord.toString()));

        WeatherForecastAdapter adapter = new WeatherForecastAdapter(getContext(),weatherForecastResult);
        forecastRecycler.setAdapter(adapter);
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
