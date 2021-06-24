package com.example.jjinjjin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.jjinjjin.Model.ToDo;
import com.example.jjinjjin.ViewHolder.TodoViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    private RecyclerView recyclerView;
    private FloatingActionButton fab;

    FirebaseDatabase database;
    DatabaseReference todoDb;

    FirebaseRecyclerOptions<ToDo> options;
    FirebaseRecyclerAdapter<ToDo, TodoViewHolder> adpter;

    public ScheduleFragment() {
        // Required empty public constructor
    }

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

        recyclerView = view.findViewById(R.id.recyclerView);
        fab = view.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dialogView = View.inflate(view.getContext(),R.layout.dialog, null);
//                AlertDialog.Builder dlg = new AlertDialog.Builder(view.getContext());
//                dlg.setTitle("ToDo 입력");
//                dlg.setView(dialogView);
//                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        edit_task = (EditText)dialogView.findViewById(R.id.edit_task);
//                        edit_materials = (EditText)dialogView.findViewById(R.id.edit_materials);
//                        if(edit_task!=null && edit_materials!=null) saveToFirebase();
//                    }
//                });
//
//                dlg.setNegativeButton("취소", null);
//                dlg.show();

                Intent intent = new Intent(view.getContext(), InputActivity.class);
                startActivity(intent);

            }
        });

        database = FirebaseDatabase.getInstance();
        todoDb = database.getReference("ToDo");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        showTask();
        return view;

    }
    private void showTask() {
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
    public void onStart() {
        super.onStart();
        adpter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adpter.stopListening();
    }
}