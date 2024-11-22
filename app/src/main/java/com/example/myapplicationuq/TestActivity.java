package com.example.myapplicationuq;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplicationuq.Adapters.QuizAdapter;
import com.example.myapplicationuq.HttpClients.RetrofitClient;
import com.example.myapplicationuq.Interfaces.ServerApi;
import com.example.myapplicationuq.Responses.QuizResponse;
import com.example.myapplicationuq.Utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TestActivity extends AppCompatActivity implements QuizAdapter.OnItemClickListener {

    private TextView textViewMaterialName;
    private TextView textViewNoQuizzes; // TextView to display "No quiz added yet"
    private RecyclerView recyclerViewQuizzes;
    private ProgressBar progressBarQuizzes;
    private QuizAdapter quizAdapter;
    private PreferenceManager preferenceManager;
    private int materialId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        preferenceManager = new PreferenceManager(this);

        // Initialize views
        textViewMaterialName = findViewById(R.id.textViewMaterialName);
        textViewNoQuizzes = findViewById(R.id.textViewNoQuizzes);
        recyclerViewQuizzes = findViewById(R.id.recyclerViewQuizzes);
        progressBarQuizzes = findViewById(R.id.progressBarQuizzes);

        // Get data from Intent
        String materialName = getIntent().getStringExtra("material_name");
        materialId = getIntent().getIntExtra("material_id", -1); // Default to -1 if not found

        // Display material name
        if (materialName != null) {
            textViewMaterialName.setText(materialName);
        } else {
            textViewMaterialName.setText("No Material Name");
        }

        // Setup RecyclerView
        recyclerViewQuizzes.setLayoutManager(new LinearLayoutManager(this));
        quizAdapter = new QuizAdapter(new ArrayList<>(), this);
        recyclerViewQuizzes.setAdapter(quizAdapter);

        // Fetch quizzes
        fetchQuizzes();
    }

    private void fetchQuizzes() {
        if (materialId == -1) {
            Toast.makeText(this, "Invalid Material ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show ProgressBar
        progressBarQuizzes.setVisibility(View.VISIBLE);

        // Initialize Retrofit
        Retrofit retrofit = RetrofitClient.getClient(this, "https://uniquiz.runasp.net/");
        ServerApi serverApi = retrofit.create(ServerApi.class);

        // Make API call
        Call<List<QuizResponse>> call = serverApi.getQuizzesByMaterialId(materialId);

        call.enqueue(new Callback<List<QuizResponse>>() {
            @Override
            public void onResponse(Call<List<QuizResponse>> call, Response<List<QuizResponse>> response) {
                // Hide ProgressBar
                progressBarQuizzes.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<QuizResponse> quizzes = response.body();

                    if (quizzes.isEmpty()) {
                        textViewNoQuizzes.setVisibility(View.VISIBLE);
                        recyclerViewQuizzes.setVisibility(View.GONE);
                    } else {
                        textViewNoQuizzes.setVisibility(View.GONE);
                        recyclerViewQuizzes.setVisibility(View.VISIBLE);
                        quizAdapter.updateQuizzes(quizzes);
                    }
                } else {
                    String errorMessage = "Failed to retrieve quizzes.";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(TestActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e("TestActivity", "Error: " + errorMessage);
                }
            }

            @Override
            public void onFailure(Call<List<QuizResponse>> call, Throwable t) {
                // Hide ProgressBar
                progressBarQuizzes.setVisibility(View.GONE);
                Toast.makeText(TestActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TestActivity", "Throwable: " + t.getMessage());
            }
        });
    }

    @Override
    public void onItemClick(QuizResponse quiz) {
        // Handle quiz item click
        Toast.makeText(this, "Selected Quiz: " + quiz.getTitle(), Toast.LENGTH_SHORT).show();
        // Navigate to quiz details or start quiz activity
    }
}
