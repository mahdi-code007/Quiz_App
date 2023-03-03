package com.mahdi.quizapp.ui;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mahdi.quizapp.R;
import com.mahdi.quizapp.model.QuestionModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QuizFragment extends Fragment {

    @BindView(R.id.quiz_close_btn)
    ImageButton quizCloseBtn;
    @BindView(R.id.quiz_title)
    TextView quizTitle;
    @BindView(R.id.quiz_question_number_title)
    TextView quizQuestionNumberTitle;
    @BindView(R.id.quiz_question_number)
    TextView quizQuestionNumber;
    @BindView(R.id.quiz_question_progress)
    ProgressBar quizQuestionProgress;
    @BindView(R.id.quiz_question_time_to_answer)
    TextView quizQuestionTimeToAnswer;
    @BindView(R.id.quiz_question)
    TextView quizQuestion;
    @BindView(R.id.quiz_option_one)
    Button quizOptionOne;
    @BindView(R.id.quiz_option_two)
    Button quizOptionTwo;
    @BindView(R.id.quiz_option_three)
    Button quizOptionThree;
    @BindView(R.id.quiz_question_feedback)
    TextView quizQuestionFeedback;
    @BindView(R.id.quiz_next_btn)
    Button quizNextBtn;


    private NavController navController;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;
    private String currentUserID;

    private List<QuestionModel> allQuestionsList = new ArrayList<>();
    private List<QuestionModel> questionsToAnswerList = new ArrayList<>();
    private long totalQuestionsToAnswer = 0;
    private String quizId;
    private String quizName;
    private CountDownTimer countDownTimer;

    private Boolean canAnswer = false;
    private int currentQuestion = 0;

    private int correctAnswers = 0;
    private int wrongAnswers = 0;
    private int notAnswered = 0;


    public QuizFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);
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
            totalQuestionsToAnswer = QuizFragmentArgs.fromBundle(getArguments()).getTotalQuestions();
            quizId = QuizFragmentArgs.fromBundle(getArguments()).getQuizId();
            quizName = QuizFragmentArgs.fromBundle(getArguments()).getQuizName();
        }

        firebaseFirestore
                .collection("QuizList")
                .document(quizId)
                .collection("Questions").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    allQuestionsList = task.getResult().toObjects(QuestionModel.class);

                    pickQuestions();
                    loadUi();
                } else {
                    quizTitle.setText("Error " + task.getException().getMessage());
                }
            }
        });

    }

    private void loadUi() {
        // Quiz data loaded , Load the Ui
        quizTitle.setText(quizName);
        quizQuestion.setText("Load Firs Question");

        //Enable Options
        enableOptions();

        //Load First Question
        loadQuestion(1);
    }

    private void loadQuestion(int questionNum) {
        //Set questions Number
        quizQuestionNumber.setText(String.valueOf(questionNum));

        //Load Questions Text
        quizQuestion.setText(questionsToAnswerList.get(questionNum - 1).getQuestion());

        //Load Options
        quizOptionOne.setText(questionsToAnswerList.get(questionNum - 1).getOption_a());
        quizOptionTwo.setText(questionsToAnswerList.get(questionNum - 1).getOption_b());
        quizOptionThree.setText(questionsToAnswerList.get(questionNum - 1).getOption_c());

        //Question loaded , set can answer
        canAnswer = true;
        currentQuestion = questionNum;

        //Start Question Timer
        startTimer(questionNum);
    }

    private void startTimer(int questionNum) {
        //Set Timer Text
        Long timeToAnswer = questionsToAnswerList.get(questionNum - 1).getTimer();
        quizQuestionTimeToAnswer.setText(timeToAnswer.toString());

        //Show Time ProgressBar
        quizQuestionProgress.setVisibility(View.VISIBLE);

        //Start CountDown
        countDownTimer = new CountDownTimer(timeToAnswer * 1000, 10) {
            @Override
            public void onTick(long l) {
                //Update Time
                quizQuestionTimeToAnswer.setText(String.valueOf(l / 1000));

                //Progress in percent
                Long percent = l / (timeToAnswer * 10);
                quizQuestionProgress.setProgress(percent.intValue());

            }

            @Override
            public void onFinish() {
                //Time Up , cannot answer question anymore
                canAnswer = false;

                notAnswered++;
                quizQuestionFeedback.setText("لقد انتهي الوقت أذهب للسؤال التالي");
                quizQuestionFeedback.setTextColor(getResources().getColor(R.color.colorPrimary));
                showNextBtn();
            }
        };

        countDownTimer.start();

    }

    private void enableOptions() {
        // Show All Options Buttons
        quizOptionOne.setVisibility(View.VISIBLE);
        quizOptionTwo.setVisibility(View.VISIBLE);
        quizOptionThree.setVisibility(View.VISIBLE);

        // Enable All Buttons
        quizOptionOne.setEnabled(true);
        quizOptionTwo.setEnabled(true);
        quizOptionThree.setEnabled(true);

        // Hide Feedback and next Button
        quizQuestionFeedback.setVisibility(View.INVISIBLE);
        quizNextBtn.setVisibility(View.INVISIBLE);
        quizNextBtn.setEnabled(false);
    }

    private void pickQuestions() {

        for (int i = 0; i < totalQuestionsToAnswer; i++) {
            int randomNumber = getRandomInteger(allQuestionsList.size(), 0);
            questionsToAnswerList.add(allQuestionsList.get(randomNumber));
            allQuestionsList.remove(randomNumber);

            Log.d("Questions", "questions: " + i + " : " + questionsToAnswerList.get(i).getQuestion());
        }
    }

    private static int getRandomInteger(int maximum, int minimum) {
        return ((int) (Math.random()) * (maximum - minimum)) + minimum;
    }


    @OnClick({R.id.quiz_option_one, R.id.quiz_option_two, R.id.quiz_option_three, R.id.quiz_next_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.quiz_option_one:
                verifyAnswer(quizOptionOne);
                break;
            case R.id.quiz_option_two:
                verifyAnswer(quizOptionTwo);
                break;
            case R.id.quiz_option_three:
                verifyAnswer(quizOptionThree);
                break;
            case R.id.quiz_next_btn:
                if (currentQuestion == totalQuestionsToAnswer) {
                    //Load Results
                    submitResults();
                } else {
                    currentQuestion++;
                    loadQuestion(currentQuestion);
                    restOptions();
                }

                break;
        }
    }

    private void submitResults() {
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("correct", correctAnswers);
        resultMap.put("wrong", wrongAnswers);
        resultMap.put("unanswered", notAnswered);

        firebaseFirestore
                .collection("QuizList")
                .document(quizId)
                .collection("Results")
                .document(currentUserID).set(resultMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //Go to result page
                    QuizFragmentDirections.ActionQuizFragmentToResultFragment action = QuizFragmentDirections.actionQuizFragmentToResultFragment();
                    action.setQuizId(quizId);
                    navController.navigate(action);
                } else {
                    //Show Error
                    quizTitle.setText(task.getException().getMessage());
                }
            }
        });
    }

    private void restOptions() {
        quizOptionOne.setBackground(getResources().getDrawable(R.drawable.outline_light_btn_bg));
        quizOptionTwo.setBackground(getResources().getDrawable(R.drawable.outline_light_btn_bg));
        quizOptionThree.setBackground(getResources().getDrawable(R.drawable.outline_light_btn_bg));

        quizOptionOne.setTextColor(getResources().getColor(R.color.colorLightText));
        quizOptionTwo.setTextColor(getResources().getColor(R.color.colorLightText));
        quizOptionThree.setTextColor(getResources().getColor(R.color.colorLightText));

        quizQuestionFeedback.setVisibility(View.INVISIBLE);
        quizNextBtn.setVisibility(View.INVISIBLE);
        quizNextBtn.setEnabled(false);
    }

    private void verifyAnswer(Button selectedAnswerBtn) {
        //Check Answer
        if (canAnswer) {
            //Set Answer Btn Text Color to Black
            selectedAnswerBtn.setTextColor(getResources().getColor(R.color.colorDark));

            if (questionsToAnswerList.get(currentQuestion - 1).getAnswer().equals(selectedAnswerBtn.getText())) {
                //Correct Answer
                correctAnswers++;
                selectedAnswerBtn.setBackground(getResources().getDrawable(R.drawable.correct_answer_btn_bg, null));

                //Set Feedback text and color
                quizQuestionFeedback.setText("Correct Answer");
                quizQuestionFeedback.setTextColor(getResources().getColor(R.color.colorPrimary));

            } else {
                //Wrong Answer
                wrongAnswers++;
                selectedAnswerBtn.setBackground(getResources().getDrawable(R.drawable.wrong_answer_btn_bg, null));

                //Set Feedback text and color
                quizQuestionFeedback.setText("Wrong Answer \n \n Correct Answer : " + questionsToAnswerList.get(currentQuestion - 1).getAnswer());
                quizQuestionFeedback.setTextColor(getResources().getColor(R.color.colorAccent));
            }

            //Set can answer to false
            canAnswer = false;

            // Stop tha timer
            countDownTimer.cancel();

            //Show Next Button
            showNextBtn();
        }
    }

    private void showNextBtn() {
        if (currentQuestion == totalQuestionsToAnswer) {
            quizNextBtn.setText("Submit Results");
        }
        quizQuestionFeedback.setVisibility(View.VISIBLE);
        quizNextBtn.setVisibility(View.VISIBLE);
        quizNextBtn.setEnabled(true);
    }


}