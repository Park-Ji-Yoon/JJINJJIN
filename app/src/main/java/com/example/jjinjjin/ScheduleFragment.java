package com.example.jjinjjin;

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
 * Use the {@link ScheduleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScheduleFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    View view;
    TextView test;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ScheduleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScheduleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScheduleFragment newInstance(String param1, String param2) {
        ScheduleFragment fragment = new ScheduleFragment();
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
        view = inflater.inflate(R.layout.fragment_schedule, container, false);
        test = view.findViewById(R.id.test);

//        isSchool("미림여자정보과학고등학교");
        isSchool("구라학굥");

        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }
    public void isSchool(String schoolName){
        String url = "";
        String school_url = "https://open.neis.go.kr/hub/schoolInfo";
        final String[] school_code = {""};

        url = school_url + "?Type=" + "json"
                + "&pIndex=" + "1"
                + "&pSize=" + "100"
                + "&KEY=" + "7c47d7824e0d4a0eb908d2c186de56f1"
                + "&SCHUL_NM=" + schoolName;

        StringRequest now_stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Nullable
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray mealServiceDietInfo = jsonResponse.getJSONArray("schoolInfo");
                    JSONObject body = mealServiceDietInfo.getJSONObject(1);
                    JSONArray body_array = body.getJSONArray("row");
                    JSONObject dish = body_array.getJSONObject(0);
                    school_code[0] = dish.getString("SD_SCHUL_CODE");
                    Log.e("test", school_code[0]); // 테스트 코드
                    if(school_code[0] != ""){
                        // ---------------setText("")넣는 부분 ---------------------
                        test.setText("존재하는 학교입니다");
                        Log.e("존재O", school_code[0]);
                    }else{
                        test.setText("없는 학교입니다");
                        Log.e("존재X", school_code[0]);
                    }
                }catch(JSONException e) {
                    Log.e("JSONException - 존재X", school_code[0]);
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
}