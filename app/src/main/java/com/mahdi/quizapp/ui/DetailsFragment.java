package com.mahdi.quizapp.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mahdi.quizapp.model.QuizListModel;
import com.mahdi.quizapp.QuizListViewModel;
import com.mahdi.quizapp.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import butterknife.BindView;
import butterknife.ButterKnife;


public class DetailsFragment extends Fragment implements View.OnClickListener {


    @BindView(R.id.details_image)
    ImageView detailsImage;
    @BindView(R.id.details_title)
    TextView detailsTitle;
    @BindView(R.id.details_desc)
    TextView detailsDesc;
    @BindView(R.id.details_difficulty)
    TextView detailsDifficulty;
    @BindView(R.id.details_questions)
    TextView detailsQuestions;
    @BindView(R.id.details_score)
    TextView detailsScore;
    @BindView(R.id.details_difficulty_text)
    TextView detailsDifficultyText;
    @BindView(R.id.details_questions_text)
    TextView detailsQuestionsText;
    @BindView(R.id.details_score_text)
    TextView detailsScoreText;
    @BindView(R.id.details_start_btn)
    Button detailsStartBtn;
    private NavController navController;
    private QuizListViewModel quizListViewModel;
    private int position;
    private String quizId;
    private long totalQuestions = 0;

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;


    public DetailsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        if (getArguments() != null) {
            position = DetailsFragmentArgs.fromBundle(getArguments()).getPosition();
            //Toast.makeText(getContext(), "" + position, Toast.LENGTH_SHORT).show();
        }

        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        detailsStartBtn.setOnClickListener(this);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        quizListViewModel = new ViewModelProvider(getActivity()).get(QuizListViewModel.class);
        quizListViewModel.getQuizListModelData().observe(getViewLifecycleOwner(), new Observer<List<QuizListModel>>() {
            @Override
            public void onChanged(List<QuizListModel> quizListModels) {

                Glide.with(getContext())
                        .load(quizListModels.get(position).getImage())
                        .centerCrop()
                        .placeholder(R.drawable.placeholder_image)
                        .into(detailsImage);

                detailsTitle.setText(quizListModels.get(position).getName());
                detailsDesc.setText(quizListModels.get(position).getDesc());
                detailsDifficultyText.setText(quizListModels.get(position).getLevel());
                detailsQuestionsText.setText(String.valueOf(quizListModels.get(position).getQuestions()));

                quizId = quizListModels.get(position).getQuiz_id();
                totalQuestions = quizListModels.get(position).getQuestions();

                //LoadResultData
                loadResultData();

            }
        });
    }

    private void loadResultData() {
        firebaseFirestore
                .collection("QuizList")
                .document(quizId)
                .collection("Results")
                .document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        Long correct = document.getLong("correct");
                        Long wrong = document.getLong("wrong");
                        Long missed = document.getLong("unanswered");

                        //Calculate Progress
                        Long total = correct + wrong + missed;
                        Log.e("test", total.toString());
                        Long percent = (correct * 100) / total;
                        Log.e("test", percent.toString());

                        detailsScoreText.setText(percent + "%");

                    } else {
                        //Document Dose not Exist , and result should stay N/A
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.details_start_btn:
                DetailsFragmentDirections.ActionDetailsFragmentToQuizFragment action = DetailsFragmentDirections.actionDetailsFragmentToQuizFragment();
                action.setTotalQuestions(totalQuestions);
                action.setQuizId(quizId);
                navController.navigate(action);
                break;
        }
    }
}