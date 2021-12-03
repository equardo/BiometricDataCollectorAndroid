package com.trzebiatowski.serkowski.biometricdatacollector.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Objects;

public class ConfigFileDto {

    private final int collectionTimeSeconds;
    private final int timeBetweenSurveysMinutes;
    private final int postponeTimeSeconds;

    private final ArrayList<QuestionDto> questions;

    @JsonCreator
    public ConfigFileDto(@JsonProperty(value = "collectionTimeSeconds", required=true) int collectionTimeSeconds,
                         @JsonProperty(value = "timeBetweenSurveysMinutes", required=true) int timeBetweenSurveysMinutes,
                         @JsonProperty(value = "postponeTimeSeconds", required=true) int postponeTimeSeconds,
                         @JsonProperty(value = "questions", required=true) ArrayList<QuestionDto> questions) {
        this.collectionTimeSeconds = collectionTimeSeconds;
        this.timeBetweenSurveysMinutes = timeBetweenSurveysMinutes;
        this.postponeTimeSeconds = postponeTimeSeconds;
        this.questions = questions;
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
