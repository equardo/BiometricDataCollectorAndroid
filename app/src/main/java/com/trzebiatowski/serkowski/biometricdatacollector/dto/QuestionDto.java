package com.trzebiatowski.serkowski.biometricdatacollector.dto;

import java.util.ArrayList;
import java.util.Objects;

public class QuestionDto {

    private String text;
    private String type;
    private ArrayList<String> answers = new ArrayList<>();

    public QuestionDto(String text, String type, ArrayList<String> answers) {
        this.text = text;
        this.type = type;
        this.answers = answers;
    }

    public QuestionDto() {
    }

    public String getText() {
        return text;
    }

    public String getType() {
        return type;
    }

    public ArrayList<String> getAnswers() {
        return answers;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAnswers(ArrayList<String> answers) {
        this.answers = answers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuestionDto that = (QuestionDto) o;
        return Objects.equals(text, that.text) && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, type);
    }
}
