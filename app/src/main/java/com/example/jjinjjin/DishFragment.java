package com.example.jjinjjin;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    View view;

    TextView schoolName;
    TextView todayDate;

    TextView breakfast;
    TextView lunch;
    TextView dinner;

    String today;
    String tomorrow;

    Button monday;
    Button tuesday;
    Button wednesday;
    Button thursday;
    Button friday;

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

        todayDate = view.findViewById(R.id.todayDate);
        schoolName = view.findViewById(R.id.schoolName);

        breakfast = view.findViewById(R.id.breakfast);
        lunch = view.findViewById(R.id.lunch);
        dinner = view.findViewById(R.id.dinner);

        monday = view.findViewById(R.id.monday);
        tuesday = view.findViewById(R.id.tuesday);
        wednesday = view.findViewById(R.id.wednesday);
        thursday = view.findViewById(R.id.thursday);
        friday = view.findViewById(R.id.friday);

        final String[] edu = new String[1];
        final String[] cod = new String[1];

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

                            Date currentTime = Calendar.getInstance().getTime();
                            String date_text = new SimpleDateFormat("yyyy년 MM월 ", Locale.getDefault()).format(currentTime);
                            todayDate.setText(date_text.concat(getWeek().concat("주")));
                            schoolName.setText(document.getData().get("school").toString());

                            try{
                                edu[0] = document.getData().get("eduCode").toString();
                                cod[0] = document.getData().get("schoolCode").toString();
                            }catch (NullPointerException e) {
                                Log.d("NullPointerException", e.toString());
                            }
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

        monday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getStudentDish(edu[0], cod[0], 1);
                    monday.setTextColor(Color.parseColor("#34A853"));
                    monday.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.radius_dish_white));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        tuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getStudentDish(edu[0], cod[0], 2);
                    tuesday.setTextColor(Color.parseColor("#34A853"));
                    tuesday.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.radius_dish_white));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        wednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getStudentDish(edu[0], cod[0], 3);
                    wednesday.setTextColor(Color.parseColor("#34A853"));
                    wednesday.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.radius_dish_white));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getStudentDish(edu[0], cod[0], 4);
                    thursday.setTextColor(Color.parseColor("#34A853"));
                    thursday.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.radius_dish_white));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        friday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getStudentDish(edu[0], cod[0], 5);
                    friday.setTextColor(Color.parseColor("#34A853"));
                    friday.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.radius_dish_white));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    public void getStudentDish(String school_code, String school_edu, int weekday) throws Exception {
        String nowtempUrl = "";
        String dish_url = "https://open.neis.go.kr/hub/mealServiceDietInfo";

        final String[] output = new String[2];

        Log.d("이거바", String.valueOf(weekday));

        String []w = weekCalendar(today);
        nowtempUrl = dish_url + "?Type=" + "json"
                + "&pIndex=" + "1"
                + "&pSize=" + "100"
                + "&ATPT_OFCDC_SC_CODE=" + school_code
                + "&SD_SCHUL_CODE=" + school_edu
                + "&KEY=" + "7abd1b38b28943c0a64f7784516d3feb"
                + "&MLSV_FROM_YMD=" + w[weekday]
                + "&MLSV_TO_YMD=" + w[weekday];

        switch(weekday){
            case 1:
                monday.setTextColor(Color.parseColor("#34A853"));
                monday.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.radius_dish_white));
                tuesday.setTextColor(Color.parseColor("#ffffff"));
                tuesday.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.radius_dish));
                wednesday.setTextColor(Color.parseColor("#ffffff"));
                wednesday.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.radius_dish));
                thursday.setTextColor(Color.parseColor("#ffffff"));
                thursday.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.radius_dish));
                friday.setTextColor(Color.parseColor("#ffffff"));
                friday.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.radius_dish));
                break;
            case 2:
                tuesday.setTextColor(Color.parseColor("#34A853"));
                tuesday.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.radius_dish_white));
                monday.setTextColor(Color.parseColor("#ffffff"));
                monday.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.radius_dish));
                wednesday.setTextColor(Color.parseColor("#ffffff"));
                wednesday.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.radius_dish));
                thursday.setTextColor(Color.parseColor("#ffffff"));
                thursday.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.radius_dish));
                friday.setTextColor(Color.parseColor("#ffffff"));
                friday.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.radius_dish));
                break;
            case 3:
                wednesday.setTextColor(Color.parseColor("#34A853"));
                wednesday.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.radius_dish_white));
                monday.setTextColor(Color.parseColor("#ffffff"));
                monday.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.radius_dish));
                tuesday.setTextColor(Color.parseColor("#ffffff"));
                tuesday.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.radius_dish));
                thursday.setTextColor(Color.parseColor("#ffffff"));
                thursday.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.radius_dish));
                friday.setTextColor(Color.parseColor("#ffffff"));
                friday.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.radius_dish));
                break;
            case 4:
                thursday.setTextColor(Color.parseColor("#34A853"));
                thursday.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.radius_dish_white));
                monday.setTextColor(Color.parseColor("#ffffff"));
                monday.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.radius_dish));
                tuesday.setTextColor(Color.parseColor("#ffffff"));
                tuesday.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.radius_dish));
                wednesday.setTextColor(Color.parseColor("#ffffff"));
                wednesday.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.radius_dish));
                friday.setTextColor(Color.parseColor("#ffffff"));
                friday.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.radius_dish));
                break;
            case 5:
                friday.setTextColor(Color.parseColor("#34A853"));
                friday.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.radius_dish_white));
                monday.setTextColor(Color.parseColor("#ffffff"));
                monday.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.radius_dish));
                tuesday.setTextColor(Color.parseColor("#ffffff"));
                tuesday.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.radius_dish));
                wednesday.setTextColor(Color.parseColor("#ffffff"));
                wednesday.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.radius_dish));
                thursday.setTextColor(Color.parseColor("#ffffff"));
                thursday.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.radius_dish));
                break;
            default:
                break;
        }

        final String finalNowtempUrl = nowtempUrl;
        Log.d("URL : ", finalNowtempUrl);
        StringRequest now_stringRequest = new StringRequest(Request.Method.POST, nowtempUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray mealServiceDietInfo = jsonResponse.getJSONArray("mealServiceDietInfo");
                    JSONObject body = mealServiceDietInfo.getJSONObject(1);
                    JSONArray body_array = body.getJSONArray("row");

                    String[] breakfast_arr = null;
                    String[] lunch_arr = null;
                    String[] dinner_arr = null;
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

    public static String getWeek(){
        Calendar c = Calendar.getInstance();
        String week = String.valueOf(c.get(Calendar.WEEK_OF_MONTH));
        return week;
    }
}