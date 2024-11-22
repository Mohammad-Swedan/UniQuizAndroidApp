// package: com.example.myapplicationuq

package com.example.myapplicationuq;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplicationuq.Adapters.QuestionAdapter;
import com.example.myapplicationuq.HttpClients.RetrofitClient;
import com.example.myapplicationuq.Interfaces.ServerApi;
import com.example.myapplicationuq.Responses.QuestionResponse;
import com.example.myapplicationuq.Utils.PreferenceManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class QuestionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private QuestionAdapter adapter;
    private ProgressBar progressBar;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_question);

        // Adjust window insets for edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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
                    List<QuestionResponse> questionList = response.body();
                    adapter = new QuestionAdapter(QuestionActivity.this, questionList);
                    recyclerView.setAdapter(adapter);
                } else {
                    String errorMessage = "Failed to fetch questions.";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(QuestionActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
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
}
