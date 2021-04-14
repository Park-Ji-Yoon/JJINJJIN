package com.example.jjinjjin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.usage.NetworkStats;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    String[] schoolInfo = {"", ""};
    boolean isClickedSearchBtn = false;
    TextView navText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.signUpButton).setOnClickListener(onClickListener);
        findViewById(R.id.schoolSearchBtn).setOnClickListener(onClickListener);
        navText = findViewById(R.id.navText);
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }
    View.OnClickListener onClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                // 회원가입 클릭 이벤트
                case R.id.signUpButton:
                    if(isClickedSearchBtn == true){
                        signUp();
                    }else{
                        startToast("학교 검색을 해야 합니다.");
                    }
                    break;
                // 학교 검색 버튼 클릭 이벤트    
                case R.id.schoolSearchBtn: 
                    String school = ((EditText)findViewById(R.id.schoolEditText)).getText().toString();
                    schoolInfo = getSchoolCode(school);
                    isClickedSearchBtn = true;
                    navText.setText("학교 검색이 완료되었습니다.");
                    break;
            }
        }
    };
    private void signUp(){
        String email = ((EditText) findViewById(R.id.emailEditText)).getText().toString();
        String password = ((EditText) findViewById(R.id.passwordEditText)).getText().toString();
        String passwordCheck = ((EditText) findViewById(R.id.passwordCheckEditText)).getText().toString();
        String name = ((EditText)findViewById(R.id.nameEditText)).getText().toString();
        String school = ((EditText)findViewById(R.id.schoolEditText)).getText().toString();

        if (email.length() > 0 && password.length() > 0 && passwordCheck.length() > 0&& name.length() > 0 && school.length() > 0) {

            if (password.equals(passwordCheck)) {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Log.d("SignUp","이메일 비번 저장성공");
                                    // startToast("회원가입을 성공했습니다!");
                                    //UI
                                } else {
                                    if (task.getException() != null) {
                                        startToast(task.getException().toString());
                                    }
                                    //UI
                                }

                                // ...
                            }
                        });
            } else {
                startToast("비밀번호가 일치하지 않습니다.");
            }

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            MemberInfo memberInfo = new MemberInfo(name, school, schoolInfo[0], schoolInfo[1]);

            db.collection("users").document(user.getUid()).set(memberInfo)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("SignUp","회원정보 저장 성공");
                            startMyActivity(MainActivity.class);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("SignUp","회원정보 저장 실패");
                        }
                    });

        }else{
            startToast("정보를 모두 입력해 주세요.");
        }
    }
    private void profileUpdate(){
        String name = ((EditText)findViewById(R.id.nameEditText)).getText().toString();
        String school = ((EditText)findViewById(R.id.schoolEditText)).getText().toString();

        if(name.length() > 0 && school.length() > 0){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            MemberInfo memberInfo = new MemberInfo(name, school,"0", "0");

            if(user != null){
                db.collection("users").document(user.getUid()).set(memberInfo)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startToast("회원정보가 저장되었습니다");
                                startMyActivity(MainActivity.class);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                startToast("회원정보를 저장하지 못했습니다");
                            }
                        });
            }

        }else{
            startToast("회원정보를 모두 입력해주세요");
        }
    }
    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    private void startMyActivity(Class c){
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    
    // 학교명 매개변수로 받아서 학교, 교육청 코드 return하는 메서드
    public String[] getSchoolCode(String schoolName){
        String nowtempUrl = "";
        String dish_url = "https://open.neis.go.kr/hub/schoolInfo";
        final String[] school_code = {"", ""};

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
                    school_code[1] = dish.getString("ATPT_OFCDC_SC_CODE");

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
        return school_code;
    }
}