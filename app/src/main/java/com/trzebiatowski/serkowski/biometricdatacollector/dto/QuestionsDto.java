package com.trzebiatowski.serkowski.biometricdatacollector.dto;

import java.util.ArrayList;
import java.util.Objects;

public class QuestionsDto {
    private ArrayList<QuestionDto> questions;

    public QuestionsDto(ArrayList<QuestionDto> questions) {
        this.questions = questions;
    }

    public QuestionsDto() {
    }

    public ArrayList<QuestionDto> getQuestions() {
        return questions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuestionsDto that = (QuestionsDto) o;
        return Objects.equals(questions, that.questions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(questions);
    }
}
