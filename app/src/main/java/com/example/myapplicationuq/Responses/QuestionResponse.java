package com.example.myapplicationuq.Responses;

import java.io.Serializable;
import java.util.List;

public class QuestionResponse implements Serializable {
    private Integer questionID;
    private Integer quizID;
    private Integer questionType;
    private String questionText;
    private List<ChoiceResponse> choices;

    public QuestionResponse(Integer questionID, Integer quizID, Integer questionType, String questionText, List<ChoiceResponse> choices) {
        this.questionID = questionID;
        this.quizID = quizID;
        this.questionType = questionType;
        this.questionText = questionText;
        this.choices = choices;
    }

    public QuestionResponse() {

    }


    public Integer getQuestionID() {
        return questionID;
    }

    public void setQuestionID(Integer questionID) {
        this.questionID = questionID;
    }

    public Integer getQuizID() {
        return quizID;
    }

    public void setQuizID(Integer quizID) {
        this.quizID = quizID;
    }

    public Integer getQuestionType() {
        return questionType;
    }

    public void setQuestionType(Integer questionType) {
        this.questionType = questionType;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public List<ChoiceResponse> getChoices() {
        return choices;
    }

    public void setChoices(List<ChoiceResponse> choices) {
        this.choices = choices;
    }
}

