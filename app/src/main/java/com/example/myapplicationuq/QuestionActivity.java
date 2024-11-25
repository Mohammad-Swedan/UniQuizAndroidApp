package com.example.myapplicationuq;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplicationuq.Adapters.QuestionAdapter;
import com.example.myapplicationuq.HttpClients.RetrofitClient;
import com.example.myapplicationuq.Interfaces.ServerApi;
import com.example.myapplicationuq.Responses.ChoiceResponse;
import com.example.myapplicationuq.Responses.QuestionResponse;
import com.example.myapplicationuq.Utils.PreferenceManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class QuestionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private QuestionAdapter adapter;
    private ProgressBar progressBar;
    private PreferenceManager preferenceManager;
    private List<QuestionResponse> questionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        // Initialize UI components
        recyclerView = findViewById(R.id.recyclerViewQuestions);
        progressBar = findViewById(R.id.progressBar);
        preferenceManager = new PreferenceManager(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get the quiz ID from Intent
        int quizId = getIntent().getIntExtra("quizID", -1);
        if (quizId == -1) {
            Toast.makeText(this, "Invalid quiz ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Fetch questions
        fetchQuestions(quizId);
    }

    private void fetchQuestions(int quizId) {
        progressBar.setVisibility(View.VISIBLE);

        // Initialize Retrofit instance
        Retrofit retrofit = RetrofitClient.getClient(this, "https://uniquiz.runasp.net/");
        ServerApi apiService = retrofit.create(ServerApi.class);

        Call<List<QuestionResponse>> call = apiService.getQuestionsByQuizId(quizId);
        call.enqueue(new Callback<List<QuestionResponse>>() {
            @Override
            public void onResponse(Call<List<QuestionResponse>> call, Response<List<QuestionResponse>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    questionList = response.body();

                    if (questionList.isEmpty()) {
                        Toast.makeText(QuestionActivity.this, "No questions available for this quiz.", Toast.LENGTH_SHORT).show();
                    } else {
                        adapter = new QuestionAdapter(QuestionActivity.this, questionList);
                        adapter.setOnSubmitClickListener(() -> {
                            // Handle submission
                            handleSubmit();
                        });
                        recyclerView.setAdapter(adapter);
                    }
                } else {
                    String errorMessage = "Failed to fetch questions. ";
                    errorMessage += "Response code: " + response.code();

                    try {
                        if (response.errorBody() != null) {
                            errorMessage += ", Error body: " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(QuestionActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                    Log.e("QuestionActivity", "Error: " + errorMessage);
                }
            }

            @Override
            public void onFailure(Call<List<QuestionResponse>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(QuestionActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("QuestionActivity", "Throwable: " + t.getMessage());
            }
        });
    }

    private void handleSubmit() {
        Map<Integer, Object> userAnswers = adapter.getUserAnswers();
        boolean allAnswered = true;
        List<Integer> unansweredQuestions = new ArrayList<>();

        // Check if all questions are answered
        for (int i = 0; i < questionList.size(); i++) {
            QuestionResponse question = questionList.get(i);
            Object answer = userAnswers.get(question.getQuestionID());
            if (answer == null) {
                allAnswered = false;
                unansweredQuestions.add(i + 1); // 1-indexed
                continue;
            }

            // Additional checks
            if (question.getQuestionType() == QuestionAdapter.TYPE_MULTIPLE_CHOICE) {
                if (answer instanceof Set) {
                    if (((Set<?>) answer).isEmpty()) {
                        allAnswered = false;
                        unansweredQuestions.add(i + 1);
                    }
                } else {
                    allAnswered = false;
                    unansweredQuestions.add(i + 1);
                }
            }

            if (question.getQuestionType() == QuestionAdapter.TYPE_SINGLE_ANSWER) {
                if (answer instanceof String) {
                    if (((String) answer).trim().isEmpty()) {
                        allAnswered = false;
                        unansweredQuestions.add(i + 1);
                    }
                } else {
                    allAnswered = false;
                    unansweredQuestions.add(i + 1);
                }
            }
        }

        if (!allAnswered) {
            // Notify the user about unanswered questions
            String message;
            if (unansweredQuestions.size() == 1) {
                message = "Please answer all questions before submitting.\nUnanswered question: " + unansweredQuestions.get(0);
            } else {
                message = "Please answer all questions before submitting.\nUnanswered questions: " + unansweredQuestions;
            }
            // Show AlertDialog instead of Toast
            showUnansweredDialog(message, unansweredQuestions.get(0) - 1);
            return;
        }

        // Calculate the score
        int totalQuestions = questionList.size();
        int correctAnswers = 0;
        Set<Integer> incorrectQuestionIDs = new HashSet<>();

        for (QuestionResponse question : questionList) {
            Object userAnswer = userAnswers.get(question.getQuestionID());
            if (isAnswerCorrect(question, userAnswer)) {
                correctAnswers++;
            } else {
                incorrectQuestionIDs.add(question.getQuestionID());
            }
        }

        // Prepare the result message
        int scorePercentage = (correctAnswers * 100) / totalQuestions;
        String resultMessage = "You scored " + correctAnswers + " out of " + totalQuestions + " (" + scorePercentage + "%)";

        // Show the result in a dialog
        showResultDialog(resultMessage);

        // Highlight incorrect answers
        adapter.setIncorrectQuestionIDs(incorrectQuestionIDs);

        // Disable further interaction
        adapter.setSubmitted(true);
    }

    private boolean isAnswerCorrect(QuestionResponse question, Object userAnswer) {
        switch (question.getQuestionType()) {
            case QuestionAdapter.TYPE_SINGLE_CHOICE:
            case QuestionAdapter.TYPE_TRUE_FALSE:
                // For single choice and true/false, userAnswer is the choiceID (Integer)
                if (!(userAnswer instanceof Integer)) return false;
                int selectedChoiceID = (Integer) userAnswer;
                for (ChoiceResponse choice : question.getChoices()) {
                    if (choice.getChoiceID().equals(selectedChoiceID)) {
                        return choice.getCorrect();
                    }
                }
                break;

            case QuestionAdapter.TYPE_MULTIPLE_CHOICE:
                // For multiple choice, userAnswer is a Set of choiceIDs
                if (!(userAnswer instanceof Set)) return false;
                Set<Integer> selectedChoices = (Set<Integer>) userAnswer;
                Set<Integer> correctChoices = new HashSet<>();
                for (ChoiceResponse choice : question.getChoices()) {
                    if (choice.getCorrect()) {
                        correctChoices.add(choice.getChoiceID());
                    }
                }
                return selectedChoices.equals(correctChoices);

            case QuestionAdapter.TYPE_SINGLE_ANSWER:
                // For single answer (text input), compare userAnswer with correct answer
                if (!(userAnswer instanceof String)) return false;
                String userInput = ((String) userAnswer).trim().toLowerCase();
                String correctAnswer = "";
                for (ChoiceResponse choice : question.getChoices()) {
                    if (choice.getCorrect()) {
                        correctAnswer = choice.getChoiceText().trim().toLowerCase();
                        break;
                    }
                }
                return userInput.equals(correctAnswer);

            default:
                return false;
        }
        return false;
    }

    private void showResultDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quiz Results");
        builder.setMessage(message);

        // Optionally, add buttons to the dialog
//        builder.setPositiveButton("OK", (dialog, which) -> {
//            dialog.dismiss();
//            // You can add more actions here if needed
//        });

        // If you want to add a "Retry" or "View Incorrect Answers" button, you can do so
        builder.setNegativeButton("View Answers", (dialog, which) -> {
            dialog.dismiss();
            // Implement functionality to view incorrect answers if desired
            // For example, navigate to a different activity or scroll to incorrect questions
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showUnansweredDialog(String message, int firstUnansweredPosition) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Incomplete Quiz");
        builder.setMessage(message);

        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
            // Scroll to the first unanswered question
            recyclerView.scrollToPosition(firstUnansweredPosition);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
