package com.example.jjinjjin;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    TextView desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mAuth = FirebaseAuth.getInstance();
        desc = findViewById(R.id.desc);

        resetPwFun();
    }
    private void resetPwFun(){
        final String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        if(email.length() > 0){
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                startToast("이메일이 전송되었습니다");
                                String s = String.format("%s로 비밀번호 변경 이메일이 전송되었습니다", email);
                                desc.setText(s);
                            }
                        }
                    });
        }else{
            startToast("이메일 전송에 실패했습니다");
        }
    }
    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}