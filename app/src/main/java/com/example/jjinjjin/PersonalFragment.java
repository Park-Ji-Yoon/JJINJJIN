package com.example.jjinjjin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class PersonalFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    //파이어스토어에 접근하기 위한 객체를 생성한다.
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    View view;

    TextView name;
    TextView school;
    TextView eduCode;
    TextView schoolCode;

    public PersonalFragment() {

    }

    public static PersonalFragment newInstance(String param1, String param2) {
        PersonalFragment fragment = new PersonalFragment();
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
        view = inflater.inflate(R.layout.fragment_personal, container, false);

        name = view.findViewById(R.id.name);
        school = view.findViewById(R.id.school);
//        eduCode = view.findViewById(R.id.eduCode);
//        schoolCode = view.findViewById(R.id.schoolCode);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("파베", "DocumentSnapshot data: " + document.getData());
                        Log.d("파베이름", "DocumentSnapshot data: " + document.getData().get("name").toString());
                        Log.d("파베이름", "DocumentSnapshot data: " + document.getData().get("school").toString());
                        name.setText(document.getData().get("name").toString());
                        school.setText(document.getData().get("school").toString());
                        eduCode.setText(document.getData().get("eduCode").toString());
                        schoolCode.setText(document.getData().get("schoolCode").toString());
                    } else {
                        Log.d("파베", "No such document");
                    }
                } else {
                    Log.d("파베", "get failed with ", task.getException());
                }
            }
        });


        return view;
    }
}