package com.example.jjinjjin;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import static android.content.Context.LOCATION_SERVICE;
import static androidx.constraintlayout.motion.utils.Oscillator.TAG;
import static com.example.jjinjjin.WeatherFragment.GPS_ENABLE_REQUEST_CODE;
import static com.example.jjinjjin.WeatherFragment.PERMISSIONS_REQUEST_CODE;
import static com.example.jjinjjin.WeatherFragment.REQUIRED_PERMISSIONS;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    View view;
    TextView mschool_txt;
    TextView mname_txt;
    TextView mloca_txt1;
    TextView mloca_txt2;
    TextView mdate_txt;
    TextView mnow_temp;
    TextView mtemp_txt;
    TextView mweather_txt;
    ImageView mweather_img;

    TextView breakfast;
    TextView lunch;
    TextView dinner;

    String today;
    String tomorrow;

    int cnt = 0;

    private GpsTracker mgpsTracker;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        findSchoolInfo();

        if (!checkLocationServicesStatus()) {

            showDialogForLocationServiceSetting();
        }else {

            checkRunTimePermission();
        }

        mschool_txt = view.findViewById(R.id.mschool_txt);
        mname_txt = view.findViewById(R.id.mname_txt);
        SetName();
        mloca_txt1 = view.findViewById(R.id.mloca_txt1);
        mloca_txt2 = view.findViewById(R.id.mloca_txt2);
        mdate_txt = view.findViewById(R.id.mdate_txt);
        mnow_temp = view.findViewById(R.id.mnow_temp);
        mtemp_txt = view.findViewById(R.id.mtemp_txt);
        mweather_txt = view.findViewById(R.id.mweather_txt);
        mweather_img = view.findViewById(R.id.mweather_img);

        breakfast = view.findViewById(R.id.breakfast);
        lunch = view.findViewById(R.id.lunch);
        dinner = view.findViewById(R.id.dinner);

        final Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyyMMdd");
        today = mFormat.format(cal.getTime());
        cal.add(Calendar.DATE, 1);
        tomorrow = mFormat.format(cal.getTime());

        String now_date = new SimpleDateFormat("yyyy년 MM월 dd일 E요일", Locale.KOREAN).format(new Date());
        mdate_txt.setText(now_date);

        int[] findxy = FindXYL();
        getWeather(String.valueOf(findxy[0]), String.valueOf(findxy[1]));

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

    public void SetName(){
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
                                    mschool_txt.setText(document.getString("school"));
                                    mname_txt.setText(document.getString("name") + "님, 안녕하세요");
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

        mgpsTracker = new GpsTracker(view.getContext());

        double latitude = mgpsTracker.getLatitude();
        double longitude = mgpsTracker.getLongitude();

        String address = getCurrentAddress(latitude, longitude);
        String[] add_list = address.split(" ");
        mloca_txt1.setText(add_list[1]);
        mloca_txt2.setText(add_list[2]);

        try{
            Workbook wb = Workbook.getWorkbook(getActivity().getBaseContext().getResources().getAssets().open("location.xls"));

            if(wb != null) {
                Sheet sheet = wb.getSheet(0);
                if (sheet != null) {
                    int rowTotal = sheet.getRows();

                    for (int row = 1; row < rowTotal; row++) {
                        if (mloca_txt1.getText().toString().equals(sheet.getCell(0, row).getContents().trim()) && mloca_txt2.getText().toString().equals(sheet.getCell(1, row).getContents().trim()) && null_str.trim().equals(sheet.getCell(2, row).getContents().trim())) {
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

                    mnow_temp.setText(t3h+"°C");

                    String weather_info = tmn + "°C / " + tmx + "°C\n강수 확률은 " + pop + "%입니다.";
                    mtemp_txt.setText(weather_info);

                    if (sky.equals("1") && Integer.parseInt(pop) <= 30){
                        mweather_img.setImageResource(R.drawable.s1r1);
                    }else if(sky.equals("1") && Integer.parseInt(pop) >= 60){
                        mweather_img.setImageResource(R.drawable.s1r3);
                    }else if(sky.equals("1")){
                        mweather_img.setImageResource(R.drawable.s1r2);
                    }else if(sky.equals("3") && Integer.parseInt(pop) <= 30){
                        mweather_img.setImageResource(R.drawable.s2r1);
                    }else if(sky.equals("3") && Integer.parseInt(pop) >= 60){
                        mweather_img.setImageResource(R.drawable.s2r3);
                    }else if(sky.equals("3")){
                        mweather_img.setImageResource(R.drawable.s2r2);
                    }else if(sky.equals("4") && Integer.parseInt(pop) <= 30){
                        mweather_img.setImageResource(R.drawable.s3r1);
                    }else if(sky.equals("4") && Integer.parseInt(pop) >= 60){
                        mweather_img.setImageResource(R.drawable.s3r3);
                    }else if(sky.equals("4")){
                        mweather_img.setImageResource(R.drawable.s3r2);
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
                    mweather_txt.setText(clothes_info);
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

    public void findSchoolInfo(){
        final String[] edu = new String[1];
        final String[] cod = new String[1];

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {

                            try{
                                edu[0] = document.getData().get("eduCode").toString();
                                cod[0] = document.getData().get("schoolCode").toString();
                            }catch (NullPointerException e) {
                                Log.d("NullPointerException", e.toString());
                            }
                            Log.e("school : ", document.getData().get("school").toString());
                            edu[0] = document.getData().get("educode").toString();
                            cod[0] = document.getData().get("schoolcode").toString();
                            try {
                                Calendar cal = Calendar.getInstance();
                                int weekday = cal.get(Calendar.DAY_OF_WEEK) - 1;
                                getStudentDish(edu[0], cod[0], weekday);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.d("else : ", "No such document1");
                        }
                    }else{
                        Log.d("else : ", "No such document2");
                    }
                } else {
                    Log.d("else : ", "get failed with ", task.getException());
                }
            }
        });
    }

    public void getStudentDish(String school_code, String school_edu, int weekday) throws Exception {
        String nowtempUrl = "";
        String dish_url = "https://open.neis.go.kr/hub/mealServiceDietInfo";

        final String[] output = new String[2];

        Log.e("today : ", today);
        Log.e("tomorrow : ", tomorrow);

        String []w = weekCalendar(today);
        nowtempUrl = dish_url + "?Type=" + "json"
                + "&pIndex=" + "1"
                + "&pSize=" + "100"
                + "&ATPT_OFCDC_SC_CODE=" + school_code
                + "&SD_SCHUL_CODE=" + school_edu
                + "&KEY=" + "7abd1b38b28943c0a64f7784516d3feb"
                + "&MLSV_FROM_YMD=" + w[1]
                + "&MLSV_TO_YMD=" + w[1];

        Log.d("더블유", Arrays.toString(w));
        Log.d("스쿨 코드", school_code);
        Log.d("교육청 코드", school_edu);
        Log.d("유얼엘 : ", nowtempUrl);

        StringRequest now_stringRequest = new StringRequest(Request.Method.POST, nowtempUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
//                    https://open.neis.go.kr/hub/mealServiceDietInfo?Type=json&pIndex=1&pSize=100&ATPT_OFCDC_SC_CODE=T10&SD_SCHUL_CODE=9296071&MLSV_FROM_YMD=20210411&MLSV_TO_YMD=20210413
                    JSONObject jsonResponse = new JSONObject(response);
                    Log.d("급쉭", jsonResponse.toString());
                    JSONArray mealServiceDietInfo = jsonResponse.getJSONArray("mealServiceDietInfo");
                    JSONObject body = mealServiceDietInfo.getJSONObject(1);
                    JSONArray body_array = body.getJSONArray("row");

                    String[] breakfast_arr = null;
                    String[] lunch_arr = null;
                    String[] dinner_arr = null;
                    Log.d("랜트 : ", String.valueOf(body_array.length()));
                    switch(body_array.length()){
                        case 0:
                            break;
                        case 1:
                            JSONObject obj = body_array.getJSONObject(0);

                            if(obj.getString("MMEAL_SC_NM").equals("조식")){
                                breakfast_arr = obj.getString("DDISH_NM").split("<br/>");
                                lunch_arr = new String[]{"중식이 없습니다"};
                                dinner_arr = new String[]{"석식이 없습니다"};
                            }else if(obj.getString("MMEAL_SC_NM").equals("중식")){
                                lunch_arr = obj.getString("DDISH_NM").split("<br/>");
                                breakfast_arr = new String[]{"조식이 없습니다"};
                                dinner_arr = new String[]{"석식이 없습니다"};
                            }else if(obj.getString("MMEAL_SC_NM").equals("석식")){
                                dinner_arr = obj.getString("DDISH_NM").split("<br/>");
                                breakfast_arr = new String[]{"조식이 없습니다"};
                                lunch_arr = new String[]{"중식이 없습니다"};
                            }
                            break;
                        case 2:
                            JSONObject obj0 = body_array.getJSONObject(0);
                            JSONObject obj1 = body_array.getJSONObject(1);
                            if(obj0.getString("MMEAL_SC_NM").equals("조식")){
                                breakfast_arr = obj0.getString("DDISH_NM").split("<br/>");
                                if(obj1.getString("MMEAL_SC_NM").equals("석식")) {
                                    dinner_arr = obj1.getString("DDISH_NM").split("<br/>");
                                }else {
                                    lunch_arr = obj1.getString("DDISH_NM").split("<br/>");
                                }
                            }else if(obj0.getString("MMEAL_SC_NM").equals("중식")){
                                lunch_arr = obj0.getString("DDISH_NM").split("<br/>");
                                dinner_arr = obj1.getString("DDISH_NM").split("<br/>");
                            }
                            break;
                        case 3:
                            breakfast_arr = body_array.getJSONObject(0).getString("DDISH_NM").split("<br/>");
                            lunch_arr = body_array.getJSONObject(1).getString("DDISH_NM").split("<br/>");
                            dinner_arr = body_array.getJSONObject(2).getString("DDISH_NM").split("<br/>");
                            break;
                        default:
                            break;
                    }

                    String breakfast_result = "";
                    String lunch_result = "";
                    String dinner_result = "";

                    if(breakfast_arr != null){
                        for(int i=0; i<breakfast_arr.length; i++) {
                            breakfast_result += breakfast_arr[i] + "\n";
                        }
                        breakfast.setText(breakfast_result);
                    }

                    if(lunch_arr != null) {
                        for (int i = 0; i < lunch_arr.length; i++) {
                            lunch_result += lunch_arr[i] + "\n";
                        }
                        lunch.setText(lunch_result);
                    }

                    if(dinner_arr != null) {
                        for (int i = 0; i < dinner_arr.length; i++) {
                            dinner_result += dinner_arr[i] + "\n";
                        }
                        dinner.setText(dinner_result);
                    }
                    Log.d("점쉼", Arrays.toString(lunch_arr));
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
    }

    public String[] weekCalendar(String yyyymmdd) throws Exception{

        Calendar cal = Calendar.getInstance();
        int toYear = 0;
        int toMonth = 0;
        int toDay = 0;
        if(yyyymmdd == null || yyyymmdd.equals("")){   //파라메타값이 없을경우 오늘날짜
            toYear = cal.get(cal.YEAR);
            toMonth = cal.get(cal.MONTH)+1;
            toDay = cal.get(cal.DAY_OF_MONTH);

            int yoil = cal.get(cal.DAY_OF_WEEK); //요일나오게하기(숫자로)

            if(yoil != 1){   //해당요일이 일요일이 아닌경우
                yoil = yoil-2;
            }else{           //해당요일이 일요일인경우
                yoil = 7;
            }
            cal.set(toYear, toMonth-1, toDay-yoil);  //해당주월요일로 세팅
        }else{
            int yy =Integer.parseInt(yyyymmdd.substring(0, 4));
            int mm =Integer.parseInt(yyyymmdd.substring(4, 6))-1;
            int dd =Integer.parseInt(yyyymmdd.substring(6, 8));
            cal.set(yy, mm,dd);
        }
        String[] arrYMD = new String[7];

        int inYear = cal.get(cal.YEAR);
        int inMonth = cal.get(cal.MONTH);
        int inDay = cal.get(cal.DAY_OF_MONTH);
        int yoil = cal.get(cal.DAY_OF_WEEK); //요일나오게하기(숫자로)
        if(yoil != 1){   //해당요일이 일요일이 아닌경우
            yoil = yoil-2;
        }else{           //해당요일이 일요일인경우
            yoil = 7;
        }
        inDay = inDay-yoil;
        for(int i = 0; i < 7;i++){
            cal.set(inYear, inMonth, inDay+i);  //
            String y = Integer.toString(cal.get(cal.YEAR));
            String m = Integer.toString(cal.get(cal.MONTH)+1);
            String d = Integer.toString(cal.get(cal.DAY_OF_MONTH));
            if(m.length() == 1) m = "0" + m;
            if(d.length() == 1) d = "0" + d;

            arrYMD[i] = y+m +d;
            System.out.println("ymd ="+ y+m+d);

        }

        return arrYMD;
    }
}