package com.example.jjinjjin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ChangeSchoolActivity extends AppCompatActivity {

    TextView previous_school;
    TextView new_school;
    Button schoolSearchBtn;
    TextView navText;
    TextView success;
    Button change_btn;

    boolean isClickedSearchBtn = false;

    String new_school_code = "";

    String school;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_school);

        previous_school = findViewById(R.id.previous_school);
        new_school = findViewById(R.id.new_school);
        schoolSearchBtn = findViewById(R.id.schoolSearchBtn);
        navText = findViewById(R.id.navText);
        success = findViewById(R.id.success);
        change_btn = findViewById(R.id.change_btn);

        success.setText("");

        Intent intent = getIntent();
        String previous = intent.getExtras().getString("previous_school");
        previous_school.setText("이전 학교 : " + previous);

        schoolSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                school = ((EditText)findViewById(R.id.schoolEditText)).getText().toString();
                new_school_code = getSchoolCode(school);
                isClickedSearchBtn = true;
                new_school.setText("재설정한 학교 : " + school);
                navText.setText("학교 검색이 완료되었습니다.");
            }
        });

        change_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isClickedSearchBtn == true){
                    updateSchool(school);

                    Intent intent = new Intent(getApplication(), MainActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(), "학교 검색읗 해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public void onBackPressed() {}

    public String getSchoolCode(String schoolName){
        String nowtempUrl = "";
        String dish_url = "https://open.neis.go.kr/hub/schoolInfo";
        final String[] school_code = new String[1];

        nowtempUrl = dish_url + "?Type=" + "json"
                + "&pIndex=" + "1"
                + "&pSize=" + "100"
                + "&KEY=" + "7c47d7824e0d4a0eb908d2c186de56f1"
                + "&SCHUL_NM=" + schoolName;

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
                    school_code[0] = dish.getString("SD_SCHUL_CODE");

                }catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(now_stringRequest);
        return school_code[0];
    }
    
    public void updateSchool(String school){
        DocumentReference washingtonRef = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        washingtonRef
                .update("school", school)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "학교 재설정이 완료되었습니다", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "학교 재설정에 실패했습니다", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}