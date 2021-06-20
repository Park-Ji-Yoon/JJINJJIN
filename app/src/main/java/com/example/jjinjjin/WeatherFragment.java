package com.example.jjinjjin;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import static android.content.Context.LOCATION_SERVICE;
import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

public class WeatherFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    View view;
    TextView school_txt;
    TextView loca_txt1;
    TextView loca_txt2;
    TextView date_txt;
    TextView now_temp;
    TextView temp_txt;
    TextView weather_txt;
    ImageView weather_img;
    ImageButton location_btn;
    ImageButton school_btn;

    int cnt = 0;

    private GpsTracker gpsTracker;

    public static final int GPS_ENABLE_REQUEST_CODE = 2001;
    public static final int PERMISSIONS_REQUEST_CODE = 100;
    public static String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

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

        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        }else {
            checkRunTimePermission();
        }

        school_txt = view.findViewById(R.id.school_txt);
        SetSchoolN();
        loca_txt1 = view.findViewById(R.id.loca_txt1);
        loca_txt2 = view.findViewById(R.id.loca_txt2);
        date_txt = view.findViewById(R.id.date_txt);
        now_temp = view.findViewById(R.id.now_temp);
        temp_txt = view.findViewById(R.id.temp_txt);
        weather_txt = view.findViewById(R.id.weather_txt);
        weather_img = view.findViewById(R.id.weather_img);
        location_btn = view.findViewById(R.id.location_btn);
        school_btn = view.findViewById(R.id.school_btn);


        String now_date = new SimpleDateFormat("yyyy년 MM월 dd일 E요일", Locale.KOREAN).format(new Date());
        date_txt.setText(now_date);

        int[] findxy = FindXYL();
        getWeather(String.valueOf(findxy[0]), String.valueOf(findxy[1]));

        location_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cnt = 0;

                school_btn.setBackgroundColor(Color.parseColor("#FFFFFF"));
                school_btn.setImageResource(R.drawable.school_yello);
                location_btn.setBackgroundColor(Color.parseColor("#FBBC07"));
                location_btn.setImageResource(R.drawable.location_white);

                int[] findxyl = FindXYL();
                getWeather(String.valueOf(findxyl[0]), String.valueOf(findxyl[1]));
            }
        });

        school_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cnt += 1;

                school_btn.setBackgroundColor(Color.parseColor("#FBBC07"));
                school_btn.setImageResource(R.drawable.school_white);
                location_btn.setBackgroundColor(Color.parseColor("#FFFFFF"));
                location_btn.setImageResource(R.drawable.location_yello);

                int[] findxys = FindXYS();
                getWeather(String.valueOf(findxys[0]), String.valueOf(findxys[1]));

                if(cnt == 1){
                    Toast.makeText(getContext(), "1번 더 클릭!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {
            boolean check_result = true;
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }
            if ( check_result ) {
            }
            else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[1])) {
                    Toast.makeText(view.getContext(), "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    getActivity().finish();
                }else {
                    Toast.makeText(view.getContext(), "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    void checkRunTimePermission(){
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[0])) {
                Toast.makeText(view.getContext(), "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    public String getCurrentAddress( double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(view.getContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(view.getContext(), "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(view.getContext(), "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }
        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(view.getContext(), "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";
        }
        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";

    }

    private void showDialogForLocationServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE:
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }
                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager)getActivity().getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void SetSchoolN(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //현재 유저의 정보 빼와야함
                                if(document.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    school_txt.setText(document.getString("school"));
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public int[] FindXYL() {
        int[] locaxyl = {0,0};
        String null_str = "no_location";

        gpsTracker = new GpsTracker(view.getContext());

        double latitude = gpsTracker.getLatitude();
        double longitude = gpsTracker.getLongitude();

        String address = getCurrentAddress(latitude, longitude);
        String[] add_list = address.split(" ");
        loca_txt1.setText(add_list[1]);
        loca_txt2.setText(add_list[2]);

        try{
            Workbook wb = Workbook.getWorkbook(getActivity().getBaseContext().getResources().getAssets().open("location.xls"));

            if(wb != null) {
                Sheet sheet = wb.getSheet(0);
                if (sheet != null) {
                    int rowTotal = sheet.getRows();

                    for (int row = 1; row < rowTotal; row++) {
                        if (loca_txt1.getText().toString().equals(sheet.getCell(0, row).getContents().trim()) && loca_txt2.getText().toString().equals(sheet.getCell(1, row).getContents().trim()) && null_str.trim().equals(sheet.getCell(2, row).getContents().trim())) {
                            locaxyl[0] = Integer.parseInt(sheet.getCell(3, row).getContents());
                            locaxyl[1] = Integer.parseInt(sheet.getCell(4, row).getContents());
                            break;
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
        return locaxyl;
    }

    public void SetSchoolLoca(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //현재 유저의 정보 빼와야함
                                if(document.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    loca_txt1.setText(document.getString("city"));
                                    loca_txt2.setText(document.getString("sigungu"));
                                    break;
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public int[] FindXYS() {
        SetSchoolLoca();

        int[] locaxys = {0,0};
        String null_str = "no_location";

        try{
            Workbook wb = Workbook.getWorkbook(getActivity().getBaseContext().getResources().getAssets().open("location.xls"));

            if(wb != null) {
                Sheet sheet = wb.getSheet(0);
                if (sheet != null) {
                    int rowTotal = sheet.getRows();

                    for (int row = 1; row < rowTotal; row++) {
                        if (loca_txt1.getText().toString().equals(sheet.getCell(0, row).getContents().trim()) && loca_txt2.getText().toString().equals(sheet.getCell(1, row).getContents().trim()) && null_str.trim().equals(sheet.getCell(2, row).getContents().trim())) {
                            locaxys[0] = Integer.parseInt(sheet.getCell(3, row).getContents());
                            locaxys[1] = Integer.parseInt(sheet.getCell(4, row).getContents());
                            break;
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
        return locaxys;
    }

    public void  getWeather(String x, String y) {
        final String url = "http://apis.data.go.kr/1360000/VilageFcstInfoService/getVilageFcst?serviceKey=FXg%2Ft9dxkuprU0w78fsjMQ6UuJka073zQlNU1CPrvzyR1ktiy4Pg7BbitEdn%2FwYeaVJ6oZmiJdgFuKeBVMqO2g%3D%3D&pageNo=1&numOfRows=62&dataType=JSON&base_date=";
        String ymd = "";
        String url_end08 = "&base_time=2000&nx=" + x + "&ny=" + y;
        String url_end924 = "&base_time=0200&nx=" + x + "&ny=" + y;
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