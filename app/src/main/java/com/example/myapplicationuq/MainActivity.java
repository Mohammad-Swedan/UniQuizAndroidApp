package com.example.myapplicationuq;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplicationuq.HttpClients.RetrofitClient;
import com.example.myapplicationuq.Interfaces.ServerApi;
import com.example.myapplicationuq.Requests.LoginRequest;
import com.example.myapplicationuq.Responses.LoginResponse;
import com.example.myapplicationuq.Utils.PreferenceManager;

import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        super.setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        progressBar = findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.GONE);
        preferenceManager = new PreferenceManager(this);

        TextView noAccountLabel = findViewById(R.id.createNewAccountLabel);
        noAccountLabel.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    /*public void openTest(View t)
    {
        Button btn = (Button)t;
        Intent intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }*/

    public void login(View v) {
        progressBar.setVisibility(View.VISIBLE);

        String emailOrUsername = ((EditText)findViewById(R.id.editTextUsernameEmailorusername)).getText().toString().trim();
        String password = ((EditText)findViewById(R.id.editTextPassword)).getText().toString().trim();

        // Validate input
        if (emailOrUsername.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both email/username and password.", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        // Initialize Retrofit
        Retrofit retrofit = RetrofitClient.getClient(this,"https://uniquiz.runasp.net/");
        ServerApi serverApi = retrofit.create(ServerApi.class);

        // Create login request object
        LoginRequest loginRequest = new LoginRequest(emailOrUsername, emailOrUsername, password);

        // Make API call
        Call<LoginResponse> call = serverApi.login(loginRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    // Store the token and permissions
                    String token = loginResponse.getToken();
                    Set<String> permissions = loginResponse.getPermissions();

                    preferenceManager.saveToken(token);
                    preferenceManager.savePermissions(permissions);

                    Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(MainActivity.this, MaterialsActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Handle error response
                    String errorMessage = "Login Failed.";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e("MainActivity", "Error: " + errorMessage);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Handle failure
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Login Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("MainActivity", "Throwable: " + t.getMessage());
            }
        });
    }



}