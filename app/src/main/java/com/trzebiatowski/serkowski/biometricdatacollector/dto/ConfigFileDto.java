package com.trzebiatowski.serkowski.biometricdatacollector.dto;

import java.util.ArrayList;
import java.util.Objects;

public class ConfigFileDto {

    private int collectionTimeSeconds;
    private int timeBetweenSurveysMinutes;
    private int postponeTimeSeconds;

    private ArrayList<QuestionDto> questions;

    public ConfigFileDto(int collectionTimeSeconds, int timeBetweenSurveysMinutes, int postponeTimeSeconds, ArrayList<QuestionDto> questions) {
        this.collectionTimeSeconds = collectionTimeSeconds;
        this.timeBetweenSurveysMinutes = timeBetweenSurveysMinutes;
        this.postponeTimeSeconds = postponeTimeSeconds;
        this.questions = questions;
    }

    public ConfigFileDto() {
    }

    public int getCollectionTimeSeconds() {
        return collectionTimeSeconds;
    }

    public int getTimeBetweenSurveysMinutes() {
        return timeBetweenSurveysMinutes;
    }

    public ArrayList<QuestionDto> getQuestions() {
        return questions;
    }

    public int getPostponeTimeSeconds() {
        return postponeTimeSeconds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigFileDto that = (ConfigFileDto) o;
        return collectionTimeSeconds == that.collectionTimeSeconds && timeBetweenSurveysMinutes == that.timeBetweenSurveysMinutes &&
                postponeTimeSeconds == that.postponeTimeSeconds && Objects.equals(questions, that.questions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(collectionTimeSeconds, timeBetweenSurveysMinutes, postponeTimeSeconds, questions);
    }
}
