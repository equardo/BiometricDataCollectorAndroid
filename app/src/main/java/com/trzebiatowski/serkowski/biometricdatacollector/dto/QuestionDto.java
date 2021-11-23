package com.trzebiatowski.serkowski.biometricdatacollector.dto;

import java.util.ArrayList;
import java.util.Objects;

public class QuestionDto {

    private String text;
    private String type;
    private ArrayList<String> answers = new ArrayList<>();
    private int valueFrom;
    private int valueTo;
    private int stepSize;

    public QuestionDto(String text, String type, ArrayList<String> answers, int valueFrom,
                       int valueTo, int stepSize) {
        this.text = text;
        this.type = type;
        this.answers = answers;
        this.valueFrom = valueFrom;
        this.valueTo = valueTo;
        this.stepSize = stepSize;
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

    public int getValueFrom() {
        return valueFrom;
    }

    public void setValueFrom(int valueFrom) {
        this.valueFrom = valueFrom;
    }

    public int getValueTo() {
        return valueTo;
    }

    public void setValueTo(int valueTo) {
        this.valueTo = valueTo;
    }

    public int getStepSize() {
        return stepSize;
    }

    public void setStepSize(int stepSize) {
        this.stepSize = stepSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuestionDto that = (QuestionDto) o;
        return valueFrom == that.valueFrom && valueTo == that.valueTo && stepSize == that.stepSize && Objects.equals(text, that.text) && Objects.equals(type, that.type) && Objects.equals(answers, that.answers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, type, answers, valueFrom, valueTo, stepSize);
    }
}
