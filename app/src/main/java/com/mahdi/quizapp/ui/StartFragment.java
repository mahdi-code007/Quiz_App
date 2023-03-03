package com.mahdi.quizapp.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mahdi.quizapp.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import butterknife.BindView;
import butterknife.ButterKnife;

public class StartFragment extends Fragment {


    private static final String TAG = "START_LOG";
    @BindView(R.id.start_title)
    TextView startTitle;
    @BindView(R.id.start_progress)
    ProgressBar startProgress;
    @BindView(R.id.start_feedback)
    TextView startFeedback;

    private FirebaseAuth mAuth;
    private NavController navController;

    public StartFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_start, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        startFeedback.setText("Checking User Account...");

        navController = Navigation.findNavController(view);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startFeedback.setText("Creating Account...");
            // Create a new account
            mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        startFeedback.setText("Account Created...");
                        Toast.makeText(getContext(), "Account is crated", Toast.LENGTH_SHORT).show();
                        navController.navigate(R.id.action_startFragment_to_listFragment);
                    } else {
                        // wrong
                        Log.e(TAG, "START_LOG" + task.getException());
                        Toast.makeText(getContext(), task.getException() + "", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            startFeedback.setText("Logged in...");
            // Navigate to HomePage
            navController.navigate(R.id.action_startFragment_to_listFragment);
        }

    }
}