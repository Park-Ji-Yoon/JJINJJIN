package com.example.jjinjjin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jjinjjin.Model.ToDo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InputActivity extends AppCompatActivity {

    private EditText edit_task, edit_priority;
    private Button btn_add;

    FirebaseDatabase database;
    DatabaseReference todoDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        edit_task = findViewById(R.id.edit_task);
        edit_priority = findViewById(R.id.edit_priority);

        btn_add = findViewById(R.id.btn_add);

        database = FirebaseDatabase.getInstance();
        todoDb = database.getReference("ToDo");

        btn_add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                saveToFirebase();
            }
        });
    }
    private void saveToFirebase(){
        String task = edit_task.getText().toString();
        String priority = edit_priority.getText().toString();

        if(!TextUtils.isEmpty(task) && !TextUtils.isEmpty(priority)){
            ToDo toDo = new ToDo(task, priority);

            todoDb.push().setValue(toDo).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(InputActivity.this, "Task is added.", Toast.LENGTH_SHORT).show();

                    edit_task.setText("");
                    edit_priority.setText("");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(InputActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(this, "All fields should be filled", Toast.LENGTH_SHORT).show();
        }
    }
}