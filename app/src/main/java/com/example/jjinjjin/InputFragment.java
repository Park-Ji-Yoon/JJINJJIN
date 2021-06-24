package com.example.jjinjjin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jjinjjin.Model.ToDo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InputFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InputFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private EditText edit_task, edit_priority;
    private Button btn_add;

    FirebaseDatabase database;
    DatabaseReference todoDb;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    View view;

    public InputFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InputFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InputFragment newInstance(String param1, String param2) {
        InputFragment fragment = new InputFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_schedule, container, false);

        edit_task = view.findViewById(R.id.edit_task);
        edit_priority = view.findViewById(R.id.edit_priority);

        btn_add = view.findViewById(R.id.btn_add);

        database = FirebaseDatabase.getInstance();
        todoDb = database.getReference("ToDo");

        btn_add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                saveToFirebase();
            }
        });

        return view;
    }

    private void saveToFirebase(){
        String task = edit_task.getText().toString();
        String priority = edit_priority.getText().toString();

        if(!TextUtils.isEmpty(task) && !TextUtils.isEmpty(priority)){
            ToDo toDo = new ToDo(task, priority);

            todoDb.push().setValue(toDo).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(view.getContext(), "Task is added.", Toast.LENGTH_SHORT).show();

                    edit_task.setText("");
                    edit_priority.setText("");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(view.getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(view.getContext(), "All fields should be filled", Toast.LENGTH_SHORT).show();
        }
    }
}