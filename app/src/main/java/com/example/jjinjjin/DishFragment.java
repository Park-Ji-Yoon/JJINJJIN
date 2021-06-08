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

import androidx.annotation.NonNull;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DishFragment extends Fragment {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static final String TAG = "FragmentIndividual";

    Button withdrawal;
    FirebaseAuth firebaseAuth;

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

        final Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyyMMdd");
        today = mFormat.format(cal.getTime());
        cal.add(Calendar.DATE, 1);
        tomorrow = mFormat.format(cal.getTime());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.e("user : ", user.getUid());
            Log.e("user : ", user.getEmail());
        } else {
            Log.d("else : ", "get failed with 3");
        }

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {
                            int weekday = cal.get(Calendar.DAY_OF_WEEK);
//                            if(weekday == 1 || weekday == 7){
//                                cardView01.setText("오늘은 급식이 없습니다.");
//                                cardView02.setVisibility(View.GONE);
//                                cardView03.setVisibility(View.GONE);
//                            }
                            Date currentTime = Calendar.getInstance().getTime();
                            String date_text = new SimpleDateFormat("yyyy년 MM월 dd일 급식", Locale.getDefault()).format(currentTime);
                            tvDate.setText(date_text);
                            tvSchoolName.setText(document.getData().get("school").toString());
                            getStudentDish(document.getData().get("eduCode").toString(), document.getData().get("schoolCode").toString());
                            Log.e("school : ", document.getData().get("school").toString());
                            Log.e("eduCode : ", document.getData().get("eduCode").toString());
                            Log.e("schoolCode : ", document.getData().get("schoolCode").toString());
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

//        getStudentDish("B10", "7010569");

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
}