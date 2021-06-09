package com.example.jjinjjin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AccountActivity extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    TextView account_name;
    TextView account_email;
    TextView account_school;
    TextView account_school_code;
    TextView account_edu_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        account_name = findViewById(R.id.account_name);
        account_email = findViewById(R.id.account_email);
        account_school = findViewById(R.id.account_school);
        account_school_code = findViewById(R.id.account_school_code);
        account_edu_code = findViewById(R.id.account_edu_code);

        Button pw_change_btn = findViewById(R.id.pw_change_btn);

        DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        account_name.setText(document.getData().get("name").toString());
                        account_email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                        account_school.setText(document.getData().get("school").toString());
                        account_school_code.setText(document.getData().get("schoolcode").toString());
                        account_edu_code.setText(document.getData().get("educode").toString());
                        Log.d("테스트", document.getData().get("schoolcode").toString());
                    } else {
                        Log.d("Firebase", "No such document");
                    }
                } else {
                    Log.d("Firebase", "get failed with ", task.getException());
                }
            }
        });

        pw_change_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                if(email.length() > 0){
                    mAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        startToast("이메일을 전송되었습니다. 이메일을 확인해주세요");
                                    }
                                }
                            });
                }else{
                    startToast("이메일 전송에 실패했습니다.");
                }
            }
        });
    }

    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}