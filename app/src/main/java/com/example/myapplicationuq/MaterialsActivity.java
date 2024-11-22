//// MaterialsActivity.java
//package com.example.myapplicationuq;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.ProgressBar;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.myapplicationuq.Adapters.MaterialsAdapter;
//import com.example.myapplicationuq.HttpClients.RetrofitClient;
//import com.example.myapplicationuq.Interfaces.ServerApi;
//import com.example.myapplicationuq.Responses.MaterialResponse;
//import com.example.myapplicationuq.Utils.PreferenceManager;
//
//import java.util.List;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//
//public class MaterialsActivity extends AppCompatActivity {
//
//    private RecyclerView recyclerViewMaterials;
//    private MaterialsAdapter materialsAdapter;
//    private ProgressBar progressBar;
//    private PreferenceManager preferenceManager;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_home_matirial);
//
//        // Initialize PreferenceManager
//        preferenceManager = new PreferenceManager(this);
//
//        // Initialize views
//        recyclerViewMaterials = findViewById(R.id.recyclerViewMaterials);
//        progressBar = findViewById(R.id.progressBar);
//
//        // Setup RecyclerView
//        recyclerViewMaterials.setLayoutManager(new LinearLayoutManager(this));
//        //Error : new java.Util.List<>()
//        materialsAdapter = new MaterialsAdapter(new java.util.ArrayList<>());
//        recyclerViewMaterials.setAdapter(materialsAdapter);
//
//        // Fetch Materials
//        fetchAllMaterials();
//    }
//
//    private void fetchAllMaterials() {
//        // Show ProgressBar
//        progressBar.setVisibility(View.VISIBLE);
//
//        // Initialize Retrofit
//        Retrofit retrofit = RetrofitClient.getClient( this,"https://uniquiz.runasp.net/");
//        ServerApi serverApi = retrofit.create(ServerApi.class);
//
//        // Make API call
//        Call<List<MaterialResponse>> call = serverApi.getAllMaterials();
//
//        call.enqueue(new Callback<List<MaterialResponse>>() {
//            @Override
//            public void onResponse(Call<List<MaterialResponse>> call, Response<List<MaterialResponse>> response) {
//                // Hide ProgressBar
//                progressBar.setVisibility(View.GONE);
//
//                if (response.isSuccessful() && response.body() != null) {
//                    List<MaterialResponse> materials = response.body();
//                    materialsAdapter.updateMaterials(materials);
//                } else {
//                    String errorMessage = "Failed to retrieve materials.";
//                    try {
//                        if (response.errorBody() != null) {
//                            errorMessage = response.errorBody().string();
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    Toast.makeText(MaterialsActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
//                    Log.e("MaterialsActivity", "Error: " + errorMessage);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<MaterialResponse>> call, Throwable t) {
//                // Hide ProgressBar
//                progressBar.setVisibility(View.GONE);
//                Toast.makeText(MaterialsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//                Log.e("MaterialsActivity", "Throwable: " + t.getMessage());
//            }
//        });
//    }
//
//
//}


package com.example.myapplicationuq;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplicationuq.Adapters.MaterialsAdapter;
import com.example.myapplicationuq.HttpClients.RetrofitClient;
import com.example.myapplicationuq.Interfaces.ServerApi;
import com.example.myapplicationuq.Responses.MaterialResponse;
import com.example.myapplicationuq.Utils.PreferenceManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MaterialsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMaterials;
    private MaterialsAdapter materialsAdapter;
    private ProgressBar progressBar;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_matirial);

        // Initialize PreferenceManager
        preferenceManager = new PreferenceManager(this);

        // Initialize views
        recyclerViewMaterials = findViewById(R.id.recyclerViewMaterials);
        progressBar = findViewById(R.id.progressBar);

        // Setup RecyclerView
        recyclerViewMaterials.setLayoutManager(new LinearLayoutManager(this));
        materialsAdapter = new MaterialsAdapter(new java.util.ArrayList<>());
        recyclerViewMaterials.setAdapter(materialsAdapter);

        // Fetch Materials
        fetchAllMaterials();
    }

    private void fetchAllMaterials() {
        // Show ProgressBar
        progressBar.setVisibility(View.VISIBLE);

        // Initialize Retrofit
        Retrofit retrofit = RetrofitClient.getClient(this, "https://uniquiz.runasp.net/");
        ServerApi serverApi = retrofit.create(ServerApi.class);

        // Make API call
        Call<List<MaterialResponse>> call = serverApi.getAllMaterials();

        call.enqueue(new Callback<List<MaterialResponse>>() {
            @Override
            public void onResponse(Call<List<MaterialResponse>> call, Response<List<MaterialResponse>> response) {
                // Hide ProgressBar
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<MaterialResponse> materials = response.body();
                    materialsAdapter.updateMaterials(materials);
                } else {
                    String errorMessage = "Failed to retrieve materials.";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(MaterialsActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e("MaterialsActivity", "Error: " + errorMessage);
                }
            }

            @Override
            public void onFailure(Call<List<MaterialResponse>> call, Throwable t) {
                // Hide ProgressBar
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MaterialsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("MaterialsActivity", "Throwable: " + t.getMessage());
            }
        });
    }
}
