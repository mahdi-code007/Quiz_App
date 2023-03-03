package com.mahdi.quizapp;

import com.mahdi.quizapp.model.QuestionModel;
import com.mahdi.quizapp.model.QuizListModel;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class QuizListViewModel extends ViewModel implements FirebaseRepository.OnFirebaseQuiz {


    private FirebaseRepository firebaseRepository = new FirebaseRepository(this);
    private MutableLiveData<List<QuizListModel>> quizListModelData = new MutableLiveData<>();

    public QuizListViewModel() {
        firebaseRepository.getQuizData();
    }

    public LiveData<List<QuizListModel>> getQuizListModelData() {
        return quizListModelData;
    }

    @Override
    public void quizListDataAdded(List<QuizListModel> quizListModels) {
        quizListModelData.setValue(quizListModels);
    }

    @Override
    public void onError(Exception e) {

    }
}

//    @Override
//    public void questionsDataAdded(List<QuestionModel> questionModels) {
//        questionsModelData.setValue(questionModels);
//    }
//
//    @Override
//    public void quizId(String quizId) {
//        this.quizId.setValue(quizId);
//    }
//}
//    private MutableLiveData<List<QuestionModel>> questionsModelData = new MutableLiveData<>();
//    private MutableLiveData<String> quizId = new MutableLiveData<>();
// private FirebaseRepository repository = new FirebaseRepository(this ,);
//    public LiveData<List<QuestionModel>> getQuestionsModelData() {
//        return questionsModelData;
//    }

//    public LiveData<String> getQuizId() {
//        return quizId;
//    }
