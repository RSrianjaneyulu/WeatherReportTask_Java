package com.weather.openweather;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.weather.openweather.dto.CommonWeatherDTO;
import com.weather.openweather.dto.WeatherDTO;
import com.weather.openweather.helperclasses.Helper;
import org.apache.commons.lang3.StringUtils;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {
    private Activity context;
    private List<CommonWeatherDTO> commonWeatherList;
    private String currentDateTime, latitudeStr, longitudeStr, weatherIcon;

    public WeatherAdapter(Activity context, List<CommonWeatherDTO> commonWeatherList) {
        this.context = context;
        this.commonWeatherList = commonWeatherList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weather_data, parent, false);
        return new ViewHolder(view);
    }

    public void addAllData(List<CommonWeatherDTO> weatherData) {
        this.commonWeatherList = new ArrayList<>();
        this.commonWeatherList = weatherData;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            final CommonWeatherDTO weatherData = commonWeatherList.get(position);
            if (weatherData != null) {
                holder.iv_IWA_weatherIcon.setVisibility(View.VISIBLE);
                holder.iv_IWA_speedIcon.setVisibility(View.VISIBLE);
                holder.iv_IWA_pressureIcon.setVisibility(View.VISIBLE);
                if (weatherData.getDt() != null) {
                    currentDateTime = String.valueOf(weatherData.getDt());
                    if (StringUtils.isNotEmpty(currentDateTime)) {
                        holder.tv_IWA_currentDateAndTime.setText(Helper.convertUnixTimeToDateMonth(currentDateTime));
                    } else {
                        holder.tv_IWA_currentDateAndTime.setVisibility(View.GONE);
                    }
                } else {
                    holder.tv_IWA_currentDateAndTime.setVisibility(View.GONE);
                }
                latitudeStr = String.valueOf(weatherData.getCoord().getLat());
                longitudeStr = String.valueOf(weatherData.getCoord().getLon());
                if (StringUtils.isNotEmpty(latitudeStr) && StringUtils.isNotEmpty(longitudeStr)) {
                    Double latitude = Double.valueOf(latitudeStr);
                    Double longitude = Double.valueOf(longitudeStr);
                    Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                    List<Address> addresses = null;
                    try {
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        String address = addresses.get(0).getAddressLine(0);
                        String locality = addresses.get(0).getLocality();
                        String countryCode = addresses.get(0).getCountryCode();
                        //String countryName = addresses.get(0).getCountryName();
                        //String postalCode = addresses.get(0).getPostalCode();
                        holder.tv_IWA_currentCityName.setText(locality + ", " + countryCode);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                } else {
                    if (StringUtils.isNotEmpty(weatherData.getName()) && StringUtils.isNotEmpty(weatherData.getSys().getCountry())) {
                        holder.tv_IWA_currentCityName.setText(weatherData.getName() + ", " + weatherData.getSys().getCountry());
                    } else {
                        holder.tv_IWA_currentCityName.setVisibility(View.GONE);
                    }
                }
                if (weatherData.getWeather() != null && !weatherData.getWeather().isEmpty()) {
                    for (WeatherDTO weather : weatherData.getWeather()) {
                        weatherIcon = weather.getIcon();
                    }
                    if (StringUtils.isNotEmpty(weatherIcon)) {
                        //Glide.with(context).load(ServerUtil.weatherImageURL + "img/wn/" + weatherIcon + "@2x.png").into(holder.iv_IWA_weatherIcon);
                        holder.iv_IWA_weatherIcon.setImageResource(R.drawable.ic_baseline_cloud_circle_24);
                    } else {
                        holder.iv_IWA_weatherIcon.setImageResource(R.drawable.ic_baseline_cloud_circle_24);
                    }
                } else {
                    holder.iv_IWA_weatherIcon.setImageResource(R.drawable.ic_baseline_cloud_circle_24);
                }
                if (weatherData.getMain().getTemp() != null) {
                    Double fahrenheit, celsius;
                    fahrenheit = weatherData.getMain().getTemp();
                    celsius = ((fahrenheit - 32) * 5) / 9;
                    DecimalFormat df = new DecimalFormat("#.##");
                    holder.tv_IWA_temperature.setText(df.format(celsius) + "" + context.getResources().getString(R.string.temperaturecelsius_symbol));
                } else {
                    holder.tv_IWA_temperature.setVisibility(View.GONE);
                }
                if (weatherData.getMain().getFeels_like() != null) {
                    Double fahrenheit, celsius;
                    fahrenheit = weatherData.getMain().getFeels_like();
                    celsius = ((fahrenheit - 32) * 5) / 9;
                    DecimalFormat df = new DecimalFormat("#.##");
                    holder.tv_IWA_feesLikeData.setText(context.getResources().getString(R.string.feelslike_label) + df.format(celsius) + "" + context.getResources().getString(R.string.temperaturecelsius_symbol) + ".Clear sky. Light breeze");
                } else {
                    holder.tv_IWA_feesLikeData.setVisibility(View.GONE);
                }
                if (weatherData.getWind().getSpeed() != null) {
                    holder.tv_IWA_speedDetails.setText(weatherData.getWind().getSpeed() + "" + context.getResources().getString(R.string.speed_symbol));
                } else {
                    holder.tv_IWA_speedDetails.setVisibility(View.GONE);
                }
                if (weatherData.getMain().getPressure() != null) {
                    holder.tv_IWA_pressureDetails.setText(weatherData.getMain().getPressure() + "" + context.getResources().getString(R.string.pressure_symbol));
                } else {
                    holder.tv_IWA_pressureDetails.setVisibility(View.GONE);
                }
                if (weatherData.getMain().getHumidity() != null) {
                    holder.tv_IWA_humidityDetails.setText(context.getResources().getString(R.string.humidity_label) + weatherData.getMain().getHumidity() + "" + context.getResources().getString(R.string.percentage_symbol));
                } else {
                    holder.tv_IWA_humidityDetails.setVisibility(View.GONE);
                }
                if (weatherData.getSys().getType() != null) {
                    holder.tv_IWA_uvDetails.setText(context.getResources().getString(R.string.uv_label) + weatherData.getSys().getType() + "");
                } else {
                    holder.tv_IWA_uvDetails.setVisibility(View.GONE);
                }
                if (weatherData.getClouds().getAll() != null) {
                    String fahren = String.valueOf(weatherData.getClouds().getAll());
                    Double fahrenheit, celsius;
                    fahrenheit = Double.valueOf(fahren);
                    celsius = ((fahrenheit - 32) * 5) / 9;
                    DecimalFormat df = new DecimalFormat("#.##");
                    holder.tv_IWA_dewPointDetails.setText(context.getResources().getString(R.string.dewpoint_label) + df.format(celsius) + "" + context.getResources().getString(R.string.temperaturecelsius_symbol));
                } else {
                    holder.tv_IWA_dewPointDetails.setVisibility(View.GONE);
                }
                if (weatherData.getVisibility() != null) {
                    String kms = String.valueOf(weatherData.getVisibility());
                    Double kilometers = Double.valueOf(kms) / 1000;
                    DecimalFormat df = new DecimalFormat("#.##");
                    holder.tv_IWA_visibilityData.setText(context.getResources().getString(R.string.visibility_label) + df.format(kilometers) + "" + context.getResources().getString(R.string.km_label));
                } else {
                    holder.tv_IWA_visibilityData.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if (commonWeatherList != null) {
            return commonWeatherList.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CardView cv_IWA_weaatherView;
        TextView tv_IWA_currentDateAndTime, tv_IWA_currentCityName, tv_IWA_temperature, tv_IWA_feesLikeData, tv_IWA_speedDetails, tv_IWA_pressureDetails, tv_IWA_humidityDetails, tv_IWA_uvDetails, tv_IWA_dewPointDetails, tv_IWA_visibilityData;
        ImageView iv_IWA_weatherIcon, iv_IWA_speedIcon, iv_IWA_pressureIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cv_IWA_weaatherView = (CardView) itemView.findViewById(R.id.cv_IWA_weaatherView);
            tv_IWA_currentDateAndTime = (TextView) itemView.findViewById(R.id.tv_IWA_currentDateAndTime);
            tv_IWA_currentCityName = (TextView) itemView.findViewById(R.id.tv_IWA_currentCityName);
            iv_IWA_weatherIcon = (ImageView) itemView.findViewById(R.id.iv_IWA_weatherIcon);
            tv_IWA_temperature = (TextView) itemView.findViewById(R.id.tv_IWA_temperature);
            tv_IWA_feesLikeData = (TextView) itemView.findViewById(R.id.tv_IWA_feesLikeData);
            iv_IWA_speedIcon = (ImageView) itemView.findViewById(R.id.iv_IWA_speedIcon);
            tv_IWA_speedDetails = (TextView) itemView.findViewById(R.id.tv_IWA_speedDetails);
            iv_IWA_pressureIcon = (ImageView) itemView.findViewById(R.id.iv_IWA_pressureIcon);
            tv_IWA_pressureDetails = (TextView) itemView.findViewById(R.id.tv_IWA_pressureDetails);
            tv_IWA_humidityDetails = (TextView) itemView.findViewById(R.id.tv_IWA_humidityDetails);
            tv_IWA_uvDetails = (TextView) itemView.findViewById(R.id.tv_IWA_uvDetails);
            tv_IWA_dewPointDetails = (TextView) itemView.findViewById(R.id.tv_IWA_dewPointDetails);
            tv_IWA_visibilityData = (TextView) itemView.findViewById(R.id.tv_IWA_visibilityData);
        }
    }
}

