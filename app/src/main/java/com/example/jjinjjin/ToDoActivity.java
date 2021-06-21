package com.example.jjinjjin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jjinjjin.Model.ToDo;
import com.example.jjinjjin.ViewHolder.TodoViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



public class ToDoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton fab;

    FirebaseDatabase database;
    DatabaseReference todoDb;

    FirebaseRecyclerOptions<ToDo> options;
    FirebaseRecyclerAdapter<ToDo, TodoViewHolder> adpter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);

        recyclerView = findViewById(R.id.recyclerView);
        fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(ToDoActivity.this, InputActivity.class);
                startActivity(intent);
            }
        });

        database = FirebaseDatabase.getInstance();
        todoDb = database.getReference("ToDo");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        showTask();
    }

    private void showTask(){
        options = new FirebaseRecyclerOptions.Builder<ToDo>()
                .setQuery(todoDb, ToDo.class)
                .build();

        adpter = new FirebaseRecyclerAdapter<ToDo, TodoViewHolder>(options){
            @Override
            protected void onBindViewHolder(@NonNull TodoViewHolder holder, int position, @NonNull ToDo model){
                holder.text_task.setText(model.getTask());
                holder.text_priority.setText(model.getPriority());
            }

            @NonNull
            @Override
            public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i){
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.todo_row, viewGroup, false);
                return new TodoViewHolder(itemView);
            }
        };
        recyclerView.setAdapter(adpter);
    }
    @Override
    protected void onStart(){
        super.onStart();
        adpter.startListening();
    }

    @Override
    protected void onStop(){
        super.onStop();
        adpter.stopListening();
    }
}