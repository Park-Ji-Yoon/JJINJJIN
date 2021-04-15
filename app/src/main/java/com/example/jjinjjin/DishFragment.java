package com.example.jjinjjin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DishFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    TextView tvDate;
    TextView tvSchoolName;
    TextView cardView01;
    TextView cardView02;
    TextView cardView03;
    View view;
    Button btnSchoolName;
    EditText edSchoolName;

    String today;
    String tomorrow;

    public DishFragment() {
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
        tvDate = view.findViewById(R.id.tvDate);
        tvSchoolName = view.findViewById(R.id.tvSchoolName);
        cardView01 = view.findViewById(R.id.info_text01);
        cardView02 = view.findViewById(R.id.info_text02);
        cardView03 = view.findViewById(R.id.info_text03);
//        tvDish = view.findViewById(R.id.tvDish);
//        btnSchoolName = view.findViewById(R.id.btnSchoolname);
//        edSchoolName = view.findViewById(R.id.edSchoolName);

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyyMMdd");
        today = mFormat.format(cal.getTime());
        cal.add(Calendar.DATE, 1);
        tomorrow = mFormat.format(cal.getTime());

        getStudentDish("B10", "7010569");

//        btnSchoolName.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(edSchoolName.getText() != null) {
//                    getSchoolCode();
//                }
//            }
//        });

        return view;
    }

    public void getStudentDish(String school_code, String school_edu){
        String nowtempUrl = "";
        String dish_url = "https://open.neis.go.kr/hub/mealServiceDietInfo";

        final String[] output = new String[2];

        Log.e("today : ", today);
        Log.e("tomorrow : ", tomorrow);

        nowtempUrl = dish_url + "?Type=" + "json"
                + "&pIndex=" + "1"
                + "&pSize=" + "100"
                + "&ATPT_OFCDC_SC_CODE=" + school_code
                + "&SD_SCHUL_CODE=" + school_edu
                + "&KEY=" + "7abd1b38b28943c0a64f7784516d3feb"
                + "&MLSV_FROM_YMD=" + today
                + "&MLSV_TO_YMD=" + tomorrow;

        StringRequest now_stringRequest = new StringRequest(Request.Method.POST, nowtempUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
//                    https://open.neis.go.kr/hub/mealServiceDietInfo?Type=json&pIndex=1&pSize=100&ATPT_OFCDC_SC_CODE=T10&SD_SCHUL_CODE=9296071&MLSV_FROM_YMD=20210411&MLSV_TO_YMD=20210413
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray mealServiceDietInfo = jsonResponse.getJSONArray("mealServiceDietInfo");
                    JSONObject body = mealServiceDietInfo.getJSONObject(1);
                    JSONArray body_array = body.getJSONArray("row");
                    JSONObject breakfast = body_array.getJSONObject(0);
                    JSONObject lunch = body_array.getJSONObject(1);
                    JSONObject dinner = body_array.getJSONObject(2);
                    String school_name = breakfast.getString("SCHUL_NM");
                    String breakfast_menu = breakfast.getString("DDISH_NM");
                    String lunch_menu = lunch.getString("DDISH_NM");
                    String dinner_menu = dinner.getString("DDISH_NM");
                    String[] breakfast_arr = breakfast_menu.split("<br/>");
                    String[] lunch_arr = lunch_menu.split("<br/>");
                    String[] dinner_arr = dinner_menu.split("<br/>");

                    Log.e("breakfast_menu : ", breakfast_menu);

                    String breakfast_result = "";
                    String lunch_result = "";
                    String dinner_result = "";

                    for(int i=0; i<breakfast_arr.length; i++) {
                        breakfast_result += breakfast_arr[i] + "\n";
                    }
                    for(int i=0; i<lunch_arr.length; i++) {
                        lunch_result += lunch_arr[i] + "\n";
                    }
                    for(int i=0; i<dinner_arr.length; i++) {
                        dinner_result += dinner_arr[i] + "\n";
                    }

                    Date currentTime = Calendar.getInstance().getTime();
                    String date_text = new SimpleDateFormat("yyyy년 MM월 dd일 급식", Locale.getDefault()).format(currentTime);
                    tvDate.setText(date_text);
                    tvSchoolName.setText(school_name);
                    cardView01.setText(breakfast_result);
                    cardView02.setText(lunch_result);
                    cardView03.setText(dinner_result);

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
    public String[] getSchoolCode(){
        String nowtempUrl = "";
        String dish_url = "https://open.neis.go.kr/hub/schoolInfo";
        final String[] school_code = {"", ""};

        nowtempUrl = dish_url + "?Type=" + "json"
                + "&pIndex=" + "1"
                + "&pSize=" + "100"
                + "&KEY=" + "7c47d7824e0d4a0eb908d2c186de56f1"
                + "&SCHUL_NM=" + edSchoolName.getText();

        StringRequest now_stringRequest = new StringRequest(Request.Method.POST, nowtempUrl, new Response.Listener<String>() {
            @Nullable
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray mealServiceDietInfo = jsonResponse.getJSONArray("schoolInfo");
                    JSONObject body = mealServiceDietInfo.getJSONObject(1);
                    JSONArray body_array = body.getJSONArray("row");
                    JSONObject dish = body_array.getJSONObject(0);
                    school_code[0] = dish.getString("ATPT_OFCDC_SC_CODE");
                    school_code[1] = dish.getString("SD_SCHUL_CODE");

                    Log.e("school_edu", school_code[0]);
                    Log.e("school_code", school_code[1]);

//                    tvResult2.setText(school_code[0] + " " + school_code[1]);

                    getStudentDish(school_code[0], school_code[1]);

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
        return school_code;
    }
}