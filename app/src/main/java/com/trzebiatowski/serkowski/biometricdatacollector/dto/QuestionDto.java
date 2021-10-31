package com.trzebiatowski.serkowski.biometricdatacollector.dto;

import java.util.Objects;

public class QuestionDto {
    private String text;
    private String type;

    public QuestionDto(String text, String type) {
        this.text = text;
        this.type = type;
    }

    public QuestionDto() {
    }

    public String getText() {
        return text;
    }

    public String getType() {
        return type;
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
