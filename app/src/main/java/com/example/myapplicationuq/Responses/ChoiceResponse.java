package com.example.myapplicationuq.Responses;

import java.io.Serializable;

public class ChoiceResponse implements Serializable {
    private Integer choiceID;
    private Integer questionID;
    private String choiceText;
    private Boolean isCorrect;

    public ChoiceResponse(Integer choiceID, Integer questionID, String choiceText, Boolean isCorrect) {
        this.choiceID = choiceID;
        this.questionID = questionID;
        this.choiceText = choiceText;
        this.isCorrect = isCorrect;
    }

    public ChoiceResponse() {

    }

    // Getters and Setters
    // ...

    public Integer getChoiceID() {
        return choiceID;
    }

    public void setChoiceID(Integer choiceID) {
        this.choiceID = choiceID;
    }

    public Integer getQuestionID() {
        return questionID;
    }

    public void setQuestionID(Integer questionID) {
        this.questionID = questionID;
    }

    public String getChoiceText() {
        return choiceText;
    }

    public void setChoiceText(String choiceText) {
        this.choiceText = choiceText;
    }

    public Boolean getCorrect() {
        return isCorrect;
    }

    public void setCorrect(Boolean correct) {
        isCorrect = correct;
    }
}
