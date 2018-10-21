package projects.karlosp3rez.androidweatherapp.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.label305.asynctask.SimpleAsyncTask;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

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

public class CityFragment extends Fragment {

    private List<String> listCity;
    private MaterialSearchBar searchBar;
    ImageView imgWeather;
    TextView txtCityName, txtHumidity, txtSunrise, txtSunset, txtPressure, txtTemperature,
            txtDescription, txtDateTime, txtWind, txtGeoCoord, txtWeatherDescription;
    CardView weatherPanel;
    ProgressBar load;

    CompositeDisposable compositeDisposable;
    IOpenWeatherMap iOpenWeatherMap;

    static CityFragment instance;

    public static CityFragment getInstance() {
        if(instance == null)
            instance = new CityFragment();
        return instance;
    }

    public CityFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        iOpenWeatherMap = retrofit.create(IOpenWeatherMap.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_city, container, false);
        inicializarUI(itemView);

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

        searchBar = (MaterialSearchBar) itemView.findViewById(R.id.searchBar);
        searchBar.setEnabled(false);

        new LoadCities().execute(); //AsyncTask class to load cities list
    }

    private class LoadCities extends SimpleAsyncTask<List<String>> {
        @Override
        protected List<String> doInBackgroundSimple() {
            listCity = new ArrayList<>();
            try {
                StringBuilder builder = new StringBuilder();
                InputStream is = getResources().openRawResource(R.raw.city_list);
                GZIPInputStream gzipInputStream = new GZIPInputStream(is);

                InputStreamReader reader = new InputStreamReader(gzipInputStream);
                BufferedReader in = new BufferedReader(reader);

                String readed;
                while((readed = in.readLine()) != null) {
                    builder.append(readed);
                }
                listCity = new Gson().fromJson(builder.toString(), new TypeToken<List<String>>(){}.getType());

            } catch (IOException e) {
                e.printStackTrace();
            }
            return listCity;
        }

        @Override
        protected void onSuccess(final List<String> listCity) {
            super.onSuccess(listCity);

            searchBar.setEnabled(true);
            searchBar.addTextChangeListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    List<String> suggest = new ArrayList<>();
                    for(String search : listCity) {
                        if(search.toLowerCase().contains(searchBar.getText().toLowerCase()))
                            suggest.add(search);
                    }
                    searchBar.setLastSuggestions(suggest);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
                @Override
                public void onSearchStateChanged(boolean enabled) {

                }

                @Override
                public void onSearchConfirmed(CharSequence text) {
                    cargarInformacionClimaPorCiudad(text.toString());
                    searchBar.setLastSuggestions(listCity);
                }

                @Override
                public void onButtonClicked(int buttonCode) {

                }
            });

            searchBar.setLastSuggestions(listCity);

            load.setVisibility(View.GONE);
        }
    }

    private void cargarInformacionClimaPorCiudad(String cityName) {
        compositeDisposable.add(iOpenWeatherMap.getWeatherByCityName(cityName,Common.APP_ID,
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
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }
}
