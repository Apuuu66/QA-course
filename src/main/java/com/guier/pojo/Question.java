package com.guier.pojo;

import lombok.Data;

@Data
public class Question {
    private int id;
    private String question;
    private String answer;
    private String rev1;

    public Question(int id, String question, String answer) {
        this.id = id;
        this.question = question;
        this.answer = answer;
    }
}
