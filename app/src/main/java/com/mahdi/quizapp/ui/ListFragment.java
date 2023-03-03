package com.mahdi.quizapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mahdi.quizapp.QuizListAdapter;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;


public class ListFragment extends Fragment implements QuizListAdapter.onQuizItemClick {


    @BindView(R.id.list_quiz_tv)
    TextView listQuizTv;
    @BindView(R.id.list_view_rv)
    RecyclerView listViewRv;
    @BindView(R.id.list_progressBar)
    ProgressBar listProgressBar;
    private QuizListViewModel quizListViewModel;
    private QuizListAdapter adapter;
    private Animation fadeInAnim;
    private Animation fadeOutAnim;

    private NavController navController;

    public ListFragment() {
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
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController= Navigation.findNavController(view);

        adapter = new QuizListAdapter(this);
        listViewRv.setLayoutManager(new LinearLayoutManager(getContext()));
        listViewRv.setHasFixedSize(true);

        listViewRv.setAdapter(adapter);

        fadeInAnim = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        fadeOutAnim = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        quizListViewModel = new ViewModelProvider(getActivity()).get(QuizListViewModel.class);
        quizListViewModel.getQuizListModelData().observe(getViewLifecycleOwner(), new Observer<List<QuizListModel>>() {
            @Override
            public void onChanged(List<QuizListModel> quizListModels) {
                // Load RecyclerView
                listViewRv.startAnimation(fadeInAnim);
                //listProgressBar.startAnimation(fadeOutAnim);
                listProgressBar.setVisibility(View.GONE);


                adapter.setQuizListModels(quizListModels);
                adapter.notifyDataSetChanged();

            }
        });
    }

    @Override
    public void onItemClick(int position) {

        ListFragmentDirections.ActionListFragmentToDetailsFragment action = ListFragmentDirections.actionListFragmentToDetailsFragment();
        action.setPosition(position);
        navController.navigate(action);
    }
}