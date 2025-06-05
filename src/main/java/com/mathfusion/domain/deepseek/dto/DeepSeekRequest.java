package com.mathfusion.domain.deepseek.dto;

public class DeepSeekRequest {
    private String question;

    public DeepSeekRequest() {}

    public DeepSeekRequest(String question) {
        this.question = question;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
