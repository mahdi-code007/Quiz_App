package com.mahdi.quizapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mahdi.quizapp.model.QuizListModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class QuizListAdapter extends RecyclerView.Adapter<QuizListAdapter.QuizViewHolder> {


    private List<QuizListModel> quizListModels;
    private onQuizItemClick onQuizItemClick;

    public QuizListAdapter(QuizListAdapter.onQuizItemClick onQuizItemClick) {
        this.onQuizItemClick = onQuizItemClick;
    }

    public void setQuizListModels(List<QuizListModel> quizListModels) {
        this.quizListModels = quizListModels;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);

        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {

        holder.listTitle.setText(quizListModels.get(position).getName());

        holder.listDifficulty.setText(quizListModels.get(position).getLevel());

        String list_description = quizListModels.get(position).getDesc();

        if (list_description.length() > 150) {
            list_description.substring(0, 150);
        }
        holder.listDesc.setText(list_description + "...");

        String imageUrl = quizListModels.get(position).getImage();
        Glide
                .with(holder.itemView.getContext())
                .load(imageUrl)
                .centerCrop()
                .placeholder(R.drawable.placeholder_image)
                .into(holder.listImage);


    }

    @Override
    public int getItemCount() {
        if (quizListModels == null) {
            return 0;
        } else {
            return quizListModels.size();
        }
    }

    public class QuizViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.list_image)
        ImageView listImage;
        @BindView(R.id.list_title)
        TextView listTitle;
        @BindView(R.id.list_desc)
        TextView listDesc;
        @BindView(R.id.list_difficulty)
        TextView listDifficulty;
        @BindView(R.id.list_btn)
        Button listBtn;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            listBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onQuizItemClick.onItemClick(getAdapterPosition());
                }
            });
        }
    }

    public interface onQuizItemClick{
        void onItemClick(int position);
    }

}