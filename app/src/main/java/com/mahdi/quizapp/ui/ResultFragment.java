package com.mahdi.quizapp.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mahdi.quizapp.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ResultFragment extends Fragment {


    @BindView(R.id.results_title)
    TextView resultsTitle;
    @BindView(R.id.results_progress)
    ProgressBar resultsProgress;
    @BindView(R.id.results_percent)
    TextView resultsPercent;
    @BindView(R.id.results_correct_text)
    TextView resultsCorrectText;
    @BindView(R.id.results_wrong_text)
    TextView resultsWrongText;
    @BindView(R.id.results_missed_text)
    TextView resultsMissedText;
    @BindView(R.id.results_correct_value)
    TextView resultsCorrectValue;
    @BindView(R.id.results_wrong_value)
    TextView resultsWrongValue;
    @BindView(R.id.results_missed_value)
    TextView resultsMissedValue;
    @BindView(R.id.results_home_btn)
    Button resultsHomeBtn;
    private NavController navController;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;
    private String quizId;
    private String currentUserID;

    public ResultFragment() {
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
        View view = inflater.inflate(R.layout.fragment_result, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        //Get User ID
        if (auth.getCurrentUser() != null) {
            currentUserID = auth.getCurrentUser().getUid();
        } else {
            //Go Back Home Page
        }

        if (getArguments() != null) {
            quizId = ResultFragmentArgs.fromBundle(getArguments()).getQuizId();
        }

        firebaseFirestore.collection("QuizList")
                .document(quizId).collection("Results")
                .document(currentUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot result = task.getResult();

                    Long correct = result.getLong("correct");
                    Long wrong = result.getLong("wrong");
                    Long missed = result.getLong("unanswered");

                    resultsCorrectValue.setText(correct.toString());
                    resultsWrongValue.setText(wrong.toString());
                    resultsMissedValue.setText(missed.toString());

                    //Calculate Progress
                    Long total = correct + wrong + missed;
                    Log.e("test" , total.toString());
                    Long percent = (correct*100)/total;
                    Log.e("test" , percent.toString());

                    resultsPercent.setText(percent.toString());
                    resultsProgress.setProgress(percent.intValue());
                }
            }
        });

    }

    @OnClick(R.id.results_home_btn)
    public void onViewClicked() {
        navController.navigate(R.id.action_resultFragment_to_listFragment);
    }
}