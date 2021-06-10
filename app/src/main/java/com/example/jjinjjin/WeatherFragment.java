package com.example.jjinjjin;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jxl.Image;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeatherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeatherFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    View view;
    TextView date_txt;
    TextView now_temp;
    TextView temp_txt;
    TextView weather_txt;
    ImageView weather_img;
    ImageButton location_btn;
    ImageButton school_btn;

    private LocationManager locationManager;
    private static final int REQUEST_CODE_LOCATION = 2;

    public WeatherFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WeatherFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WeatherFragment newInstance(String param1, String param2) {
        WeatherFragment fragment = new WeatherFragment();
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
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_weather, container, false);

        date_txt = view.findViewById(R.id.date_txt);
        now_temp = view.findViewById(R.id.now_temp);
        temp_txt = view.findViewById(R.id.temp_txt);
        weather_txt = view.findViewById(R.id.weather_txt);
        weather_img = view.findViewById(R.id.weather_img);
        location_btn = view.findViewById(R.id.location_btn);
        school_btn = view.findViewById(R.id.school_btn);

        String now_date = new SimpleDateFormat("yyyy년 MM월 dd일 E요일", Locale.KOREAN).format(new Date());
        date_txt.setText(now_date);

//        locationManager = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
//
//        Location userLocation = FindLocation();
//        double latitude = userLocation.getLatitude();
//        double longitude = userLocation.getLongitude();
//
//        Log.d("1현 위치 : ", latitude + "," + longitude + "");
//
        int[] findxy = FindXY(1, 1);
        getWeather(String.valueOf(findxy[0]), String.valueOf(findxy[1]));

        return view;
    }

    private Location FindLocation() {
        Location currentLocation = null;

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, this.REQUEST_CODE_LOCATION);
            FindLocation();
        }
        else {
            String locationProvider = LocationManager.GPS_PROVIDER;
            currentLocation = locationManager.getLastKnownLocation(locationProvider);
        }
        return currentLocation;
    }

    private int[] FindXY(double x, double y) {
        int[] resultxy = {0, 0};
        String resultloca = "";

        final Geocoder geocoder = new Geocoder(getContext(), Locale.KOREAN);
        List<Address> list = null;
        try{
            list = geocoder.getFromLocation(
                    37.4663, // 위도
                    126.9329, // 경도
                    1); // 얻어올 값의 개수
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (list != null) {
            if (list.size()!=0) {
                resultloca = list.get(0).getThoroughfare();
            }
        }

        try{
            Workbook wb = Workbook.getWorkbook(getActivity().getBaseContext().getResources().getAssets().open("location.xls"));

            if(wb != null) {
                Sheet sheet = wb.getSheet(0);
                if (sheet != null) {
                    int rowTotal = sheet.getRows();

                    for (int row = 1; row < rowTotal; row++) {
                        if (resultloca.equals(sheet.getCell(2, row).getContents())) {
                            resultxy[0] = Integer.parseInt(sheet.getCell(3, row).getContents());
                            resultxy[1] = Integer.parseInt(sheet.getCell(4, row).getContents());
                            Log.d("시트 오류 : ", "찾을 수 없음");
                        }
                    }
                } else {
                    Log.d("시트 오류 : ", "찾을 수 없음");
                }
            }else{
                Log.d("엑셀 오류 : ", "찾을 수 없음");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }
        return resultxy;
    }

    public void  getWeather(String x, String y) {
        final String url = "http://apis.data.go.kr/1360000/VilageFcstInfoService/getVilageFcst?serviceKey=FXg%2Ft9dxkuprU0w78fsjMQ6UuJka073zQlNU1CPrvzyR1ktiy4Pg7BbitEdn%2FwYeaVJ6oZmiJdgFuKeBVMqO2g%3D%3D&pageNo=1&numOfRows=62&dataType=JSON&base_date=";
        String ymd = "";
        final String url_end08 = "&base_time=2000&nx=" + x + "&ny=" + y;
        final String url_end924 = "&base_time=0200&nx=" + x + "&ny=" + y;
        String weatherUrl = "";

        final int nowH = Integer.parseInt(new SimpleDateFormat("H").format(new Date(System.currentTimeMillis())));

        if(nowH >= 0 && nowH <= 8){
            ymd = new SimpleDateFormat("yyyyMMdd").format(new Date(new Date().getTime()+(1000*60*60*24*-1)));
            weatherUrl = url + ymd + url_end08;
        }else{
            ymd = new SimpleDateFormat("yyyyMMdd").format(new Date());
            weatherUrl = url + ymd + url_end924;
        }


        StringRequest stringRequest = new StringRequest(Request.Method.GET, weatherUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    String pop = "", sky = "", t3h = "", tmn = "" ,tmx = "";

                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.getJSONObject("response").getJSONObject("body")
                            .getJSONObject("items").getJSONArray("item");

                    switch (nowH){
                        case 0: case 1: case 2:
                            pop = jsonArray.getJSONObject(0).getString("fcstValue");
                            sky = jsonArray.getJSONObject(5).getString("fcstValue");
                            t3h = jsonArray.getJSONObject(6).getString("fcstValue");
                            tmn = jsonArray.getJSONObject(27).getString("fcstValue");
                            tmx = jsonArray.getJSONObject(57).getString("fcstValue");
                            break;

                        case 3: case 4: case 5:
                            pop = jsonArray.getJSONObject(11).getString("fcstValue");
                            sky = jsonArray.getJSONObject(14).getString("fcstValue");
                            t3h = jsonArray.getJSONObject(15).getString("fcstValue");
                            tmn = jsonArray.getJSONObject(27).getString("fcstValue");
                            tmx = jsonArray.getJSONObject(57).getString("fcstValue");
                            break;

                        case 6: case 7: case 8:
                            pop = jsonArray.getJSONObject(20).getString("fcstValue");
                            sky = jsonArray.getJSONObject(25).getString("fcstValue");
                            t3h = jsonArray.getJSONObject(26).getString("fcstValue");
                            tmn = jsonArray.getJSONObject(27).getString("fcstValue");
                            tmx = jsonArray.getJSONObject(57).getString("fcstValue");
                            break;

                        case 9: case 10: case 11:
                            pop = jsonArray.getJSONObject(12).getString("fcstValue");
                            sky = jsonArray.getJSONObject(15).getString("fcstValue");
                            t3h = jsonArray.getJSONObject(16).getString("fcstValue");
                            tmn = jsonArray.getJSONObject(7).getString("fcstValue");
                            tmx = jsonArray.getJSONObject(37).getString("fcstValue");
                            break;

                        case 12: case 13: case 14:
                            pop = jsonArray.getJSONObject(21).getString("fcstValue");
                            sky = jsonArray.getJSONObject(26).getString("fcstValue");
                            t3h = jsonArray.getJSONObject(27).getString("fcstValue");
                            tmn = jsonArray.getJSONObject(7).getString("fcstValue");
                            tmx = jsonArray.getJSONObject(37).getString("fcstValue");
                            break;

                        case 15: case 16: case 17:
                            pop = jsonArray.getJSONObject(32).getString("fcstValue");
                            sky = jsonArray.getJSONObject(35).getString("fcstValue");
                            t3h = jsonArray.getJSONObject(36).getString("fcstValue");
                            tmn = jsonArray.getJSONObject(7).getString("fcstValue");
                            tmx = jsonArray.getJSONObject(37).getString("fcstValue");
                            break;

                        case 18: case 19: case 20:
                            pop = jsonArray.getJSONObject(42).getString("fcstValue");
                            sky = jsonArray.getJSONObject(47).getString("fcstValue");
                            t3h = jsonArray.getJSONObject(48).getString("fcstValue");
                            tmn = jsonArray.getJSONObject(7).getString("fcstValue");
                            tmx = jsonArray.getJSONObject(37).getString("fcstValue");
                            break;

                        case 21: case 22: case 23:
                            pop = jsonArray.getJSONObject(53).getString("fcstValue");
                            sky = jsonArray.getJSONObject(56).getString("fcstValue");
                            t3h = jsonArray.getJSONObject(57).getString("fcstValue");
                            tmn = jsonArray.getJSONObject(7).getString("fcstValue");
                            tmx = jsonArray.getJSONObject(37).getString("fcstValue");
                            break;
                    }

                    now_temp.setText(t3h+"°C");

                    String weather_info = tmn + "°C / " + tmx + "°C\n강수 확률은 " + pop + "%입니다.";
                    temp_txt.setText(weather_info);

                    if (sky.equals("1") && Integer.parseInt(pop) <= 30){
                        weather_img.setImageResource(R.drawable.s1r1);
                    }else if(sky.equals("1") && Integer.parseInt(pop) >= 60){
                        weather_img.setImageResource(R.drawable.s1r3);
                    }else if(sky.equals("1")){
                        weather_img.setImageResource(R.drawable.s1r2);
                    }else if(sky.equals("3") && Integer.parseInt(pop) <= 30){
                        weather_img.setImageResource(R.drawable.s2r1);
                    }else if(sky.equals("3") && Integer.parseInt(pop) >= 60){
                        weather_img.setImageResource(R.drawable.s2r3);
                    }else if(sky.equals("3")){
                        weather_img.setImageResource(R.drawable.s2r2);
                    }else if(sky.equals("4") && Integer.parseInt(pop) <= 30){
                        weather_img.setImageResource(R.drawable.s3r1);
                    }else if(sky.equals("4") && Integer.parseInt(pop) >= 60){
                        weather_img.setImageResource(R.drawable.s3r3);
                    }else if(sky.equals("4")){
                        weather_img.setImageResource(R.drawable.s3r2);
                    }

                    double avg_temp = (Double.parseDouble(tmn)+Double.parseDouble(tmx))/2;
                    String clothes_info = "👔 오늘의 코디?\n\t\t\t\t-> ";
                    if(avg_temp >= 27.0){
                        clothes_info += "민소매, 반팔, 반바지, 린넨";
                    }else if(avg_temp >= 23.0){
                        clothes_info += "얇은 긴팔, 반팔, 면바지";
                    }else if(avg_temp >= 20.0){
                        clothes_info += "후드티, 셔츠, 슬랙스, 원피스";
                    }else if(avg_temp >= 17.0){
                        clothes_info += "가디건, 얇은 자켓, 슬랙스";
                    }else if(avg_temp >= 12.0){
                        clothes_info += "자켓, 두꺼운 가디건, 니트";
                    }else if(avg_temp >= 10.0){
                        clothes_info += "트렌치코트, 항공점퍼, 얇은 코트";
                    }else if(avg_temp >= 6.0){
                        clothes_info += "겨울 코트, 경량패딩, 가죽자켓";
                    }else{
                        clothes_info += "패딩, 목도리, 장갑, 기모바지";
                    }
                    weather_txt.setText(clothes_info);
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
        requestQueue.add(stringRequest);
    }
}