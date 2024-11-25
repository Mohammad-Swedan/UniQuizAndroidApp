package com.example.myapplicationuq.Responses;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class QuizResponse implements Serializable {

    @SerializedName("quizID")
    private Integer quizID;

    @SerializedName("materialID")
    private Integer materialID;

    @SerializedName("writerID")
    private Integer writerID;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("likes")
    private Integer likes;

    @SerializedName("dislikes")
    private Integer dislikes;

    @SerializedName("createdAt")
    private String createdAt;

    // Getters and Setters
    public Integer getQuizID() {
        return quizID;
    }

    public void setQuizID(Integer quizID) {
        this.quizID = quizID;
    }

    public Integer getMaterialID() {
        return materialID;
    }

    public void setMaterialID(Integer materialID) {
        this.materialID = materialID;
    }

    public Integer getWriterID() {
        return writerID;
    }

    public void setWriterID(Integer writerID) {
        this.writerID = writerID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description != null ? description : "No description available";
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getLikes() {
        return likes != null ? likes : 0;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Integer getDislikes() {
        return dislikes != null ? dislikes : 0;
    }

    public void setDislikes(Integer dislikes) {
        this.dislikes = dislikes;
    }

    public String getCreatedAt() {
        return createdAt != null ? createdAt : "";
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
