package com.weather.openweather.networkcall;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.webkit.CookieManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.weather.openweather.R;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AsyncNetworkTask extends AsyncTask<Map<String, Object>, String, Map<String, Object>> {

    ProgressDialog progressDialog;
    ActionCallBack func;
    private static final String COOKIES_HEADER = "Set-Cookie";
    private static final String COOKIE = "Cookie";
    private CookieManager cookieManager = CookieManager.getInstance();

    public AsyncNetworkTask(Context con, ActionCallBack callback, String loadingMessage) {
        progressDialog = new ProgressDialog(con);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            progressDialog.setProgressDrawable(con.getResources().getDrawable(R.drawable.custom_progress_background, null));
        }
        progressDialog.setMessage(loadingMessage);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        func = callback;
    }

    @Override
    protected void onPostExecute(Map<String, Object> result) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (func != null) {
            try {
                func.onCallback(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onPostExecute(result);
    }

    @Override
    protected final Map<String, Object> doInBackground(Map<String, Object>... params) {
        URL url = null;
        HttpURLConnection conn = null;
        String cookie = null;
        PrintWriter out = null;
        Map<String, Object> result = new HashMap<>();
        try {
            String serverUrl = (String) params[0].get("serverUrl");
            url = new URL(serverUrl);
            params[0].remove("serverUrl");
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(70000);
            conn.setConnectTimeout(70000);
            conn.setDoInput(true);
            cookie = cookieManager.getCookie(conn.getURL().getHost());
            if (cookie != null) {
                cookie = URLDecoder.decode(cookie);
            }
            conn.setRequestProperty(COOKIE, cookie);
            if ("get".equals(params[0].get("operationType"))) {
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
            } else if ("post".equals(params[0].get("operationType"))) {
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Accept", "application/json");
            }
            if (out != null) {
                out.close();
            }
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                List<String> cookieList = conn.getHeaderFields().get(COOKIES_HEADER);
                boolean isLogout = false;
                if (cookieList != null) {
                    String multipleCookie = "";
                    for (String cookieTemp : cookieList) {
                        if (cookieTemp != null && !cookieTemp.isEmpty()) {
                            multipleCookie = cookieTemp + ";" + multipleCookie;
                        }
                    }
                    if (!multipleCookie.isEmpty() && !isLogout) {
                        cookieManager.setCookie(conn.getURL().getHost(), URLEncoder.encode(multipleCookie));
                    }
                }
                JsonParser jsonParser = new JsonParser();
                if (conn.getInputStream() != null) {
                    conn.getHeaderFields();
                    JsonElement jsonElement = jsonParser.parse(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    if (jsonElement != null && !(jsonElement instanceof JsonNull)) {
                        JsonArray jsonArray = null;
                        JsonObject jsonObject = null;
                        try {
                            jsonObject = jsonElement.getAsJsonObject();
                        } catch (Exception je) {
                            jsonArray = jsonElement.getAsJsonArray();
                        }
                        if (jsonObject != null) {
                            try {
                                String resultValue = jsonObject.get("data").toString();
                                result.put("result", resultValue);
                            } catch (Exception e) {
                                result.put("result", jsonObject.toString());
                            }
                            try {
                                result.put("cookie", cookieManager.getCookie(conn.getURL().getHost()));
                            } catch (Exception e) {
                                Log.d("AWN-Cookie-Exception", e.getMessage());
                            }
                            Log.d("AsyncTask-Result:", (String) result.get("result"));
                        } else if (jsonArray != null) {
                            result.put("result", jsonArray.toString());
                            try {
                                result.put("cookie", cookieManager.getCookie(conn.getURL().getHost()));
                            } catch (Exception e) {
                                Log.d("AWN-Cookie-Exception", e.getMessage());
                            }
                            Log.d("AsyncTask-Result2:", (String) result.get("result"));
                        } else {
                            result.put("result", null);
                        }
                    } else {
                        result.put("error", "Problem while connecting to server!!!");
                    }
                }
            } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                result.put("error", "Invalid Credentials!!!");
            } else if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
                Map<String, List<String>> headers = conn.getHeaderFields();
                String msg = conn.getHeaderField("message");
                result.put("error", msg);
            } else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST && "5" != params[0].get("operationType")) {
                result.put("error", "No Records Found!!!");
            } else {
                result.put("error", "Problem while connecting to server!!!");
            }
        } catch (Exception e) {
            result.put("error", "Problem while connecting to server!!!");
        } finally {
            conn.disconnect();
        }
        return result;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPreExecute() {
        try {
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onPreExecute();
    }

}

