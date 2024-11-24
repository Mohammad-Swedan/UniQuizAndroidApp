package com.example.myapplicationuq.Interfaces;

import com.example.myapplicationuq.Requests.LoginRequest;
import com.example.myapplicationuq.Requests.RegisterRequest;
import com.example.myapplicationuq.Responses.LoginResponse;
import com.example.myapplicationuq.Responses.MaterialResponse;
import com.example.myapplicationuq.Responses.QuestionResponse;
import com.example.myapplicationuq.Responses.QuizResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ServerApi {
    @POST("/api/Auth/Login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @GET("api/Material/All")
    Call<List<MaterialResponse>> getAllMaterials();

    @GET("api/Quiz/All/{MaterialID}")
    Call<List<QuizResponse>> getQuizzesByMaterialId(@Path("MaterialID") int materialId);

    @GET("api/Qustion/All/{quizID}")
    Call<List<QuestionResponse>> getQuestionsByQuizId(@Path("quizID") int quizId);

    @POST("/api/Auth/Register")
    Call<Void> register(@Body RegisterRequest registerRequest);

    //@POST("/api/Auth/Register")
    //Call<RegisterResponse> register(@Body RegisterRequest registerRequest);
}