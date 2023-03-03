package com.mahdi.quizapp;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mahdi.quizapp.model.QuestionModel;
import com.mahdi.quizapp.model.QuizListModel;

import java.util.List;

import androidx.annotation.NonNull;

public class FirebaseRepository {

    // Quiz
    private OnFirebaseQuiz onFirebaseQuiz;
    private FirebaseFirestore quizFirebaseFirestore = FirebaseFirestore.getInstance();
    private Query quizRef = quizFirebaseFirestore.collection("QuizList").whereEqualTo("visibility" , "public");

//    // Questions
//    private String quizId;
//    private OnFirebaseQuestions onFirebaseQuestions;
//    private FirebaseFirestore questionsFirebaseFirestore = FirebaseFirestore.getInstance();
//    private CollectionReference questionsRef = questionsFirebaseFirestore.collection("QuizList").document(quizId).collection("Questions");

    // Quiz data constructor
    public FirebaseRepository(OnFirebaseQuiz onFirebaseQuiz) {
        this.onFirebaseQuiz = onFirebaseQuiz;
    }

//    // Questions data constructor
//    public FirebaseRepository(OnFirebaseQuestions onFirebaseQuestions, String quizId) {
//        this.onFirebaseQuestions = onFirebaseQuestions;
//        this.quizId = quizId;
//    }

    public void getQuizData() {
        quizRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    onFirebaseQuiz.quizListDataAdded(task.getResult().toObjects(QuizListModel.class));
                } else {
                    onFirebaseQuiz.onError(task.getException());
                }

            }
        });
    }

//    public void getQuestionsData() {
//        questionsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    onFirebaseQuestions.questionsDataAdded(task.getResult().toObjects(QuestionModel.class));
//                } else {
//                    onFirebaseQuestions.onError(task.getException());
//                }
//            }
//        });
//    }

    public interface OnFirebaseQuiz {
        void quizListDataAdded(List<QuizListModel> quizListModels);

        void onError(Exception e);
    }

//    public interface OnFirebaseQuestions {
//        void questionsDataAdded(List<QuestionModel> questionModels);
//
//        void onError(Exception e);
//        void quizId(String quizId);
//    }
}
