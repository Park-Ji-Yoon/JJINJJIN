package com.example.jjinjjin;

import android.app.DownloadManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

public class DishFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    TextView tvResult1;
    TextView tvResult2;
    View view;
    Button btnSchoolName;
    EditText edSchoolName;

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
        tvResult2 = view.findViewById(R.id.tvResult2);
        btnSchoolName = view.findViewById(R.id.btnSchoolname);
        edSchoolName = view.findViewById(R.id.edSchoolName);

        btnSchoolName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edSchoolName.getText() != null) {
                    getSchoolCode();
                }
            }
        });

        return view;
    }

    public void getStudentDish(String school_code, String school_edu){
        String nowtempUrl = "";
        String dish_url = "https://open.neis.go.kr/hub/mealServiceDietInfo";

        final String[] output = new String[2];

        nowtempUrl = dish_url + "?Type=" + "json"
                + "&pIndex=" + "1"
                + "&pSize=" + "100"
                + "&ATPT_OFCDC_SC_CODE=" + school_edu
                + "&SD_SCHUL_CODE=" + school_code
                + "&KEY=" + "7abd1b38b28943c0a64f7784516d3feb";

        StringRequest now_stringRequest = new StringRequest(Request.Method.POST, nowtempUrl, new Response.Listener<String>() {
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
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(now_stringRequest);
//        requestQueue.add(stringRequest);
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

                    tvResult2.setText(school_code[0] + " " + school_code[1]);

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
//        requestQueue.add(stringRequest);
        return school_code;
    }
}