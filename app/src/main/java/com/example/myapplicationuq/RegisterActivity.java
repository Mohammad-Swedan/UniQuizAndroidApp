package com.example.myapplicationuq;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplicationuq.HttpClients.RetrofitClient;
import com.example.myapplicationuq.Interfaces.ServerApi;
import com.example.myapplicationuq.Requests.RegisterRequest;
import com.example.myapplicationuq.Utils.PreferenceManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity {

    private ProgressBar progressBar;

    private EditText firstNameField, lastNameField, emailField, passwordField, confirmPasswordField;
    private View registerButton;
    private TextView haveAnAccountLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register); // Ensure your layout file is named correctly
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        firstNameField = findViewById(R.id.registerFirstNameField);
        lastNameField = findViewById(R.id.registerLastNameField);
        emailField = findViewById(R.id.registerEmailField);
        passwordField = findViewById(R.id.registerPasswordField);
        confirmPasswordField = findViewById(R.id.registerConfirmPasswordField);
        registerButton = findViewById(R.id.registerButton);
        haveAnAccountLabel = findViewById(R.id.haveAnAccountLabel);
        progressBar = findViewById(R.id.registerProgressBar);
        progressBar.setVisibility(View.GONE);


        // Set click listeners
        registerButton.setOnClickListener(this::register);
        haveAnAccountLabel.setOnClickListener(v -> {
            // Navigate back to login activity
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    public void register(View v) {
        progressBar.setVisibility(View.VISIBLE);

        String firstName = firstNameField.getText().toString().trim();
        String lastName  = lastNameField.getText().toString().trim();
        String email     = emailField.getText().toString().trim();
        String password  = passwordField.getText().toString().trim();
        String confirmPassword = confirmPasswordField.getText().toString().trim();

        // Validate input
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() ||
                password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        // Initialize Retrofit
        Retrofit retrofit = RetrofitClient.getClient(this,"https://uniquiz.runasp.net/");
        ServerApi serverApi = retrofit.create(ServerApi.class);

        // Create register request object
        RegisterRequest registerRequest = new RegisterRequest(email, firstName + " " + lastName, password);

        // Make API call
        Call<Void> call = serverApi.register(registerRequest);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Registration Successful. Please log in.", Toast.LENGTH_SHORT).show();

                    // Navigate to login activity
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);

                } else {
                    // Handle error response
                    String errorMessage = "Registration Failed.";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e("RegisterActivity", "Error: " + errorMessage);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Handle failure
                progressBar.setVisibility(View.GONE);
                Toast.makeText(RegisterActivity.this, "Registration Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("RegisterActivity", "Throwable: " + t.getMessage());
            }
        });
    }
}
