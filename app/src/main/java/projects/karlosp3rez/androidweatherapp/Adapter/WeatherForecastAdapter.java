package projects.karlosp3rez.androidweatherapp.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import projects.karlosp3rez.androidweatherapp.Common.Common;
import projects.karlosp3rez.androidweatherapp.Model.WeatherForecastResult;
import projects.karlosp3rez.androidweatherapp.R;

public class WeatherForecastAdapter extends RecyclerView.Adapter<WeatherForecastAdapter.ForecastViewHolder> {

    private Context context;
    private WeatherForecastResult weatherForecastResult;

    public WeatherForecastAdapter(Context context, WeatherForecastResult weatherForecastResult) {
        this.context = context;
        this.weatherForecastResult = weatherForecastResult;
    }

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_weather_forecast,parent,false);
        return new ForecastViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        //Cargar imagen
        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/").append(weatherForecastResult.list.get(position).weather.get(0).getIcon())
                .append(".png").toString()).into(holder.imgWeather);

        holder.txtDateTime.setText(new StringBuilder(Common.convertirUnidadesAFecha(weatherForecastResult.list.get(position).dt)));
        holder.txtDescription.setText(new StringBuilder(weatherForecastResult.list.get(position).weather.get(0).getDescription()));
        holder.txtTemperature.setText(new StringBuilder(String.valueOf(weatherForecastResult.list.get(position).main.getTemp())).append("°C"));
    }

    @Override
    public int getItemCount() {
        return weatherForecastResult.list.size();
    }

    class ForecastViewHolder extends RecyclerView.ViewHolder {

        TextView txtDateTime, txtDescription, txtTemperature;
        ImageView imgWeather;

        ForecastViewHolder(View itemView) {
            super(itemView);
            imgWeather = itemView.findViewById(R.id.imgWeather);
            txtDateTime = itemView.findViewById(R.id.txtDate);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            txtTemperature = itemView.findViewById(R.id.txtTemperature);
        }
    }
}
