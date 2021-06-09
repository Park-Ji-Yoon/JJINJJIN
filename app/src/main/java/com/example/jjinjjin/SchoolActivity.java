package com.example.jjinjjin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SchoolActivity extends AppCompatActivity {

    Button school_change_btn;

    TextView account_school;
    TextView account_school_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school);

        school_change_btn = findViewById(R.id.school_change_btn);

        account_school = findViewById(R.id.account_school);
        account_school_code = findViewById(R.id.account_school_code);

        DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        account_school.setText(document.getData().get("school").toString());
                        account_school_code.setText(document.getData().get("schoolcode").toString());
                    } else {
                        Log.d("Firebase", "No such document");
                    }
                } else {
                    Log.d("Firebase", "get failed with ", task.getException());
                }
            }
        });

        school_change_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChangeSchoolActivity.class);
                intent.putExtra("previous_school", account_school.getText());
                startActivity(intent);
            }
        });
    }
}
