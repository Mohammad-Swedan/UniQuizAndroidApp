package com.example.myapplicationuq.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplicationuq.R;
import com.example.myapplicationuq.Responses.QuizResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {

    private List<QuizResponse> quizList;
    private OnItemClickListener listener;

    // Define interface for click events
    public interface OnItemClickListener {
        void onItemClick(QuizResponse quiz);
    }

    public QuizAdapter(List<QuizResponse> quizList, OnItemClickListener listener) {
        this.quizList = quizList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quiz, parent, false);
        return new QuizViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        QuizResponse quiz = quizList.get(position);
        holder.textViewQuizTitle.setText(quiz.getTitle());
        holder.textViewQuizDescription.setText(quiz.getDescription());

        holder.textViewLikes.setText("Likes: " + quiz.getLikes());
        holder.textViewDislikes.setText("Dislikes: " + quiz.getDislikes());

        // Format createdAt date
        String formattedDate = formatDate(quiz.getCreatedAt());
        holder.textViewCreatedAt.setText("Created At: " + formattedDate);

        // Handle item click
        holder.itemView.setOnClickListener(v -> listener.onItemClick(quiz));
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    public static class QuizViewHolder extends RecyclerView.ViewHolder {
        TextView textViewQuizTitle, textViewQuizDescription, textViewLikes, textViewDislikes, textViewCreatedAt;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewQuizTitle = itemView.findViewById(R.id.textViewQuizTitle);
            textViewQuizDescription = itemView.findViewById(R.id.textViewQuizDescription);
            textViewLikes = itemView.findViewById(R.id.textViewLikes);
            textViewDislikes = itemView.findViewById(R.id.textViewDislikes);
            textViewCreatedAt = itemView.findViewById(R.id.textViewCreatedAt);
        }
    }

    // Method to update the list
    public void updateQuizzes(List<QuizResponse> quizzes) {
        this.quizList = quizzes;
        notifyDataSetChanged();
    }

    // Helper method to format date
    private String formatDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) return "N/A";
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); // Adjust format if needed
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm");
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "Invalid Date";
        }
    }
}
