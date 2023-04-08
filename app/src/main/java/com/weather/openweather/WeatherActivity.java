package com.weather.openweather;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.weather.openweather.dto.CommonWeatherDTO;
import com.weather.openweather.helperclasses.Helper;
import com.weather.openweather.helperclasses.SPHelper;
import com.weather.openweather.networkcall.ActionCallBack;
import com.weather.openweather.networkcall.AsyncNetworkTask;
import com.weather.openweather.serverutils.ServerUtil;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.weather.openweather.serverutils.ServerUtil.retrievingDataMessage;

public class WeatherActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView tv_AW_cityNameLabel, tv_AW_noWeatherList;
    private EditText et_AW_enterCityName;
    private ImageView iv_AW_cityNameSearchIcon;
    private RecyclerView rv_AW_weatherList;
    private CommonWeatherDTO commonWeatherDTO = null;
    private WeatherAdapter weatherAdapter = null;
    private List<CommonWeatherDTO> commonWeatherList;
    private String cityName, cityDetails, appId = "4db19b747b0a3369815069fb2ef8d024";
    //private String appId = "6134f58493547c1c8a615102693ac120";
    private static WeatherActivity instance;
    private String[] citiesList = {"New York", "Chicago", "Boston", "San Diego", "Los Angeles", "San Francisco", "Philadelphia", "Austin", "Seattle", "Nashville", "Phoenix", "San Jose", "San Antonio", "Oklahoma City", "Las Vegas", "Baltimore", "Houston", "New Orleans", "Washington", "Washington, D.C.", "Columbus", "Dallas", "Indianapolis", "Atlanta", "Detroit", "Denver", "Honolulu", "Jacksonville", "Fort Worth", "El Paso", "Kansas City", "Milwaukee", "Memphis", "Sacramento", "Charlotte", "Portland", "Fresno", "Raleigh", "Albuquerque", "Tucson", "Colorado Springs", "Louisville", "Miami", "Tulsa", "Wichita", "Omaha", "Virginia Beach", "Salt Lake City", "Oakland", "Des Moines", "Charleston", "Mesa", "California", "Texas", "Florida", "Indiana", "Michigan", "Missouri", "New Jersey", "St. Petersburg", "Moreno Valley", "Tacoma", "Rochester", "Columbus", "Frisco", "Oxnard", "Sioux Falls", "Tallahassee", "Virginia", "Louisiana", "Illinois", "Aurora", "Santa Rosa", "Lancaster", "Springfield", "Hayward", "Clarksville", "Paterson", "Hollywood", "Mississippi", "Rockford", "Fullerton", "West Valley City", "Elizabeth", "Kent", "Miramar", "Midland", "Iowa", "Carrollton", "Fargo", "Pearland", "North Dakota", "Thousand Oaks", "Allentown", "Colorado", "Clovis", "Hartford", "Connecticut", "Wilmington", "Fairfield", "Cambridge", "Billings", "West Palm Beach", "Westminster", "Provo", "Lewisville", "New Mexico", "Odessa", "Nevada", "Greeley", "Tyler", "Oregon", "Rio Rancho", "Massachusetts", "New Bedford", "Longmont", "Hesperia", "Chico", "Burbank", "Murfreesboro", "Kansas", "Idaho", "Pittsburgh", "Detroit", "Pennsylvania", "Jacksonville", "Indianapolis", "Charlotte", "Jefferson City", "Arizona", "Ohio", "North Carolina", "Oklahoma", "Denver", "El Paso", "Kentucky", "Nevada", "Milwaukee", "Baltimore", "Wisconsin", "New Mexico", "Fresno", "Tucson", "Sacramento", "Nebraska", "Omaha", "Raleigh", "Oakland", "Miami", "Minneapolis", "Bakersfield", "Tampa", "New Orleans", "Honolulu", "Lexington", "Stockton", "St. Louis", "Lubbock", "Buffalo", "Glendale", "Hialeah", "Connecticut"};
    private List<String> cityList = null;
    private RequestQueue requestRueue = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        instance = this;
        requestRueue = Volley.newRequestQueue(WeatherActivity.this);
        tv_AW_cityNameLabel = findViewById(R.id.tv_AW_cityNameLabel);
        et_AW_enterCityName = findViewById(R.id.et_AW_enterCityName);
        iv_AW_cityNameSearchIcon = findViewById(R.id.iv_AW_cityNameSearchIcon);
        iv_AW_cityNameSearchIcon.setOnClickListener(this);
        rv_AW_weatherList = findViewById(R.id.rv_AW_weatherList);
        tv_AW_noWeatherList = findViewById(R.id.tv_AW_noWeatherList);
        weatherAdapter = new WeatherAdapter(this, commonWeatherList);
        rv_AW_weatherList.setLayoutManager(new LinearLayoutManager(this));
        rv_AW_weatherList.setAdapter(weatherAdapter);
        rv_AW_weatherList.setItemAnimator(new DefaultItemAnimator());
        weatherAdapter.notifyDataSetChanged();
        Helper.recyclerViewDivider(rv_AW_weatherList, this);
        if(citiesList != null) {
            cityList = Arrays.asList(citiesList);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                cityList.replaceAll(String::toLowerCase);
            }
        }
        try {
            if (ContextCompat.checkSelfPermission(WeatherActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(WeatherActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        et_AW_enterCityName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                cityName = text.toString().trim();
                if(StringUtils.isNotEmpty(cityName)){
                    tv_AW_cityNameLabel.setVisibility(View.VISIBLE);
                }else {
                    tv_AW_cityNameLabel.setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        final Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if ((SPHelper.getStringData(WeatherActivity.this, SPHelper.cityName, "").trim().length() > 0)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (Helper.checkInternetConnection(WeatherActivity.this)) {
                                        cityName = SPHelper.getStringData(WeatherActivity.this, SPHelper.cityName, "").trim();
                                        //getWeatherDetails(); // using/calling AsyncTask
                                        getWeatherDetailsInfo(); // using/calling Volley Library
                                    }
                                }
                            });
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        };
        timerThread.start();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    //Get Weather Details API Call - AsyncTask
    private void getWeatherDetails() {
        Map<String, Object> params = new HashMap<>();
        params.put("serverUrl", ServerUtil.getWeatherDetailsURL + "?" + "q" + "=" + cityName + "&" + "appid" + "=" + appId);
        params.put("operationType", "post");
        AsyncNetworkTask asyncNetworkTask = new AsyncNetworkTask(this, new ActionCallBack() {
            @Override
            public void onCallback(Map<String, Object> result) {
                try {
                    if (!result.isEmpty() && result.get("result") != null) {
                        commonWeatherList = new ArrayList<>();
                        commonWeatherDTO = new Gson().fromJson((String) result.get("result"), CommonWeatherDTO.class);
                        if (commonWeatherDTO != null){
                            SPHelper.saveStringData(instance, SPHelper.cityName, cityName);
                            commonWeatherList.add(commonWeatherDTO);
                            tv_AW_noWeatherList.setVisibility(View.GONE);
                            rv_AW_weatherList.setVisibility(View.VISIBLE);
                            weatherAdapter.addAllData(commonWeatherList);
                        }else {
                            tv_AW_noWeatherList.setVisibility(View.VISIBLE);
                            rv_AW_weatherList.setVisibility(View.GONE);
                            Helper.showShortToast(WeatherActivity.this, getResources().getString(R.string.no_weatherdata_label));
                        }
                    } else {
                        tv_AW_noWeatherList.setVisibility(View.VISIBLE);
                        rv_AW_weatherList.setVisibility(View.GONE);
                        Helper.showShortToast(WeatherActivity.this, getResources().getString(R.string.no_weatherdata_label));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, retrievingDataMessage);
        asyncNetworkTask.execute(params);
    }
    //Get Weather Details API Call - Volley Library
    private void getWeatherDetailsInfo() {
        String getWeatherReportURL = ServerUtil.getWeatherDetailsURL + "?" + "q" + "=" + cityName + "&" + "appid" + "=" + appId;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getWeatherReportURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response != null) {
                        commonWeatherList = new ArrayList<>();
                        commonWeatherDTO = new Gson().fromJson(response.toString(), CommonWeatherDTO.class);
                        if (commonWeatherDTO != null){
                            SPHelper.saveStringData(instance, SPHelper.cityName, cityName);
                            commonWeatherList.add(commonWeatherDTO);
                            tv_AW_noWeatherList.setVisibility(View.GONE);
                            rv_AW_weatherList.setVisibility(View.VISIBLE);
                            weatherAdapter.addAllData(commonWeatherList);
                        }else {
                            tv_AW_noWeatherList.setVisibility(View.VISIBLE);
                            rv_AW_weatherList.setVisibility(View.GONE);
                            Helper.showShortToast(WeatherActivity.this, getResources().getString(R.string.no_weatherdata_label));
                        }
                    }else {
                        tv_AW_noWeatherList.setVisibility(View.VISIBLE);
                        rv_AW_weatherList.setVisibility(View.GONE);
                        Helper.showShortToast(WeatherActivity.this, getResources().getString(R.string.no_weatherdata_label));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Helper.showShortToast(WeatherActivity.this, getResources().getString(R.string.no_weatherdata_label));
            }
        });
        requestRueue.add(jsonObjectRequest);
    }
    //cityname validation check
    private void cityNameValidationCheck(){
        cityName = et_AW_enterCityName.getText().toString().trim().toLowerCase();
        if (!StringUtils.isNotEmpty(cityName)) {
            et_AW_enterCityName.requestFocus();
            et_AW_enterCityName.setError(getResources().getString(R.string.cityname_required));
            return;
        }else if (cityName.length() < 3){
            et_AW_enterCityName.requestFocus();
            et_AW_enterCityName.setError(getResources().getString(R.string.valid_citynamecharacters));
            return;
        } else {
            et_AW_enterCityName.setError(null);
        }
        if (Helper.checkInternetConnection(WeatherActivity.this)) {
            if (StringUtils.isNotEmpty(cityName)) {
                if (cityName.contains(",")) {
                    String cityInfo[] = cityName.split(",");
                    cityDetails = cityInfo[0].trim().toLowerCase();
                } else {
                    cityDetails = cityName;
                }
                if (cityList.contains(cityDetails)) {
                    //getWeatherDetails(); // using/calling AsyncTask
                    getWeatherDetailsInfo(); // using/calling Volley Library
                } else {
                    Helper.showShortToast(WeatherActivity.this, getResources().getString(R.string.valid_cityname));
                    et_AW_enterCityName.setText("");
                    tv_AW_noWeatherList.setVisibility(View.VISIBLE);
                    rv_AW_weatherList.setVisibility(View.GONE);
                }
            }
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_AW_cityNameSearchIcon:
                cityNameValidationCheck();
                break;
            default:
                break;
        }
    }
    public static WeatherActivity getInstance() {
        return instance;
    }
}
