package com.example.jjinjjin;

import android.app.DownloadManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DishFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DishFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    TextView tvResult1;
    View view;

    public DishFragment() {
    }

    public static DishFragment newInstance(String param1, String param2) {
        DishFragment fragment = new DishFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dish, container, false);
        tvResult1 = view.findViewById(R.id.tvResult1);
        getStudentDish();
        return view;
    }

    public void getStudentDish(){
        String tempUrl = "";
        String nowtempUrl = "";
        String dish_url = "https://open.neis.go.kr/hub/mealServiceDietInfo";
//        String city = etCity.getText().toString().trim();
//        if (city.equals("")){
//            tvResult1.setText("첫 번째 항목 : 필수 기입");
//        }else{
        final String[] output = new String[2];
//            nowtempUrl = nowurl + "?q=" + city + "&appid=" + appid;
//            tempUrl = url + "?q=" + city + "&appid=" + appid;

        nowtempUrl = dish_url + "?Type=" + "json"
                + "&pIndex=" + "1"
                + "&pSize=" + "100"
                + "&ATPT_OFCDC_SC_CODE=" + "T10"
                + "&SD_SCHUL_CODE=" + "9296071"
                + "&KEY=" + "7abd1b38b28943c0a64f7784516d3feb";

        StringRequest now_stringRequest = new StringRequest(Request.Method.POST, nowtempUrl, new Response.Listener<String>() {
            @Nullable
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray mealServiceDietInfo = jsonResponse.getJSONArray("mealServiceDietInfo");
                    JSONObject body = mealServiceDietInfo.getJSONObject(1);
                    JSONArray body_array = body.getJSONArray("row");
                    JSONObject dish = body_array.getJSONObject(0);
                    String school_name = dish.getString("SCHUL_NM");
                    String lunch = dish.getString("DDISH_NM");
                    String[] lunch_arr = lunch.split("<br/>");

                    Log.e("Object : ", String.valueOf(dish));
                    Log.e("school name : ", school_name);
                    Log.e("lunch : ", lunch);
                    for(int i=0; i<lunch_arr.length; i++){
                        String imsi = lunch_arr[i];
                        Log.e("lunch_arr : ", imsi);
                    }
                    tvResult1.setText(lunch);

                }catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        });
        StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response", response);
                try{
                    double temp_max = 0.0, temp_min = 0.0;
                    int humidity = 0, clouds = 0;

                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.getJSONArray("list");

                    JSONObject jsonObject_D0 = jsonArray.getJSONObject(0);
                    JSONObject jsonObject_M0 = jsonObject_D0.getJSONObject("main");
                    JSONObject jsonObject_D1 = jsonArray.getJSONObject(1);
                    JSONObject jsonObject_M1 = jsonObject_D1.getJSONObject("main");
                    JSONObject jsonObject_D2 = jsonArray.getJSONObject(2);
                    JSONObject jsonObject_M2 = jsonObject_D2.getJSONObject("main");
                    JSONObject jsonObject_D3 = jsonArray.getJSONObject(3);
                    JSONObject jsonObject_M3 = jsonObject_D3.getJSONObject("main");
                    JSONObject jsonObject_D4 = jsonArray.getJSONObject(4);
                    JSONObject jsonObject_M4 = jsonObject_D4.getJSONObject("main");
                    JSONObject jsonObject_D5 = jsonArray.getJSONObject(5);
                    JSONObject jsonObject_M5 = jsonObject_D5.getJSONObject("main");
                    JSONObject jsonObject_D6 = jsonArray.getJSONObject(6);
                    JSONObject jsonObject_M6 = jsonObject_D6.getJSONObject("main");
                    JSONObject jsonObject_D7 = jsonArray.getJSONObject(7);
                    JSONObject jsonObject_M7 = jsonObject_D7.getJSONObject("main");
                    JSONObject jsonObject_D8 = jsonArray.getJSONObject(8);
                    JSONObject jsonObject_M8 = jsonObject_D8.getJSONObject("main");

                    double[] tempmax = new double[9];
                    tempmax[0] = jsonObject_M0.getDouble("temp_max")-273.15;
                    tempmax[1] = jsonObject_M1.getDouble("temp_max")-273.15;
                    tempmax[2] = jsonObject_M2.getDouble("temp_max")-273.15;
                    tempmax[3] = jsonObject_M3.getDouble("temp_max")-273.15;
                    tempmax[4] = jsonObject_M4.getDouble("temp_max")-273.15;
                    tempmax[5] = jsonObject_M5.getDouble("temp_max")-273.15;
                    tempmax[6] = jsonObject_M6.getDouble("temp_max")-273.15;
                    tempmax[7] = jsonObject_M7.getDouble("temp_max")-273.15;
                    tempmax[8] = jsonObject_M8.getDouble("temp_max")-273.15;
                    temp_max = tempmax[0];
                    for(int i = 0; i < tempmax.length; i++) {
                        if(temp_max < tempmax[i]) {
                            temp_max = tempmax[i];
                        }
                    }

                    double[] tempmin = new double[9];
                    tempmin[0] = jsonObject_M0.getDouble("temp_min")-273.15;
                    tempmin[1] = jsonObject_M1.getDouble("temp_min")-273.15;
                    tempmin[2] = jsonObject_M2.getDouble("temp_min")-273.15;
                    tempmin[3] = jsonObject_M3.getDouble("temp_min")-273.15;
                    tempmin[4] = jsonObject_M4.getDouble("temp_min")-273.15;
                    tempmin[5] = jsonObject_M5.getDouble("temp_min")-273.15;
                    tempmin[6] = jsonObject_M6.getDouble("temp_min")-273.15;
                    tempmin[7] = jsonObject_M7.getDouble("temp_min")-273.15;
                    tempmin[8] = jsonObject_M8.getDouble("temp_min")-273.15;
                    temp_min = tempmin[0];
                    for(int i = 0; i < tempmin.length; i++) {
                        if(temp_min > tempmin[i]) {
                            temp_min = tempmin[i];
                        }
                    }

                    int humidity0 = jsonObject_M0.getInt("humidity");
                    int humidity1 = jsonObject_M1.getInt("humidity");
                    int humidity2 = jsonObject_M2.getInt("humidity");
                    int humidity3 = jsonObject_M3.getInt("humidity");
                    int humidity4 = jsonObject_M4.getInt("humidity");
                    int humidity5 = jsonObject_M5.getInt("humidity");
                    int humidity6 = jsonObject_M6.getInt("humidity");
                    int humidity7 = jsonObject_M7.getInt("humidity");
                    int humidity8 = jsonObject_M8.getInt("humidity");
                    humidity = (humidity0 + humidity1 + humidity2 + humidity3 + humidity4 + humidity5 + humidity6 + humidity7 + humidity8)/9;

                    JSONObject jsonObjectClouds0 = jsonObject_D0.getJSONObject("clouds");
                    int clouds0 = jsonObjectClouds0.getInt("all");
                    JSONObject jsonObjectClouds1 = jsonObject_D1.getJSONObject("clouds");
                    int clouds1 = jsonObjectClouds1.getInt("all");
                    JSONObject jsonObjectClouds2 = jsonObject_D2.getJSONObject("clouds");
                    int clouds2 = jsonObjectClouds2.getInt("all");
                    JSONObject jsonObjectClouds3 = jsonObject_D3.getJSONObject("clouds");
                    int clouds3 = jsonObjectClouds3.getInt("all");
                    JSONObject jsonObjectClouds4 = jsonObject_D4.getJSONObject("clouds");
                    int clouds4 = jsonObjectClouds4.getInt("all");
                    JSONObject jsonObjectClouds5 = jsonObject_D5.getJSONObject("clouds");
                    int clouds5 = jsonObjectClouds5.getInt("all");
                    JSONObject jsonObjectClouds6 = jsonObject_D6.getJSONObject("clouds");
                    int clouds6 = jsonObjectClouds6.getInt("all");
                    JSONObject jsonObjectClouds7 = jsonObject_D7.getJSONObject("clouds");
                    int clouds7 = jsonObjectClouds7.getInt("all");
                    JSONObject jsonObjectClouds8 = jsonObject_D8.getJSONObject("clouds");
                    int clouds8 = jsonObjectClouds8.getInt("all");
                    clouds = (clouds0 + clouds1 + clouds2 + clouds3 + clouds4 + clouds5 + clouds6 + clouds7 + clouds8)/9;

                }catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(now_stringRequest);
        requestQueue.add(stringRequest);
    }
}