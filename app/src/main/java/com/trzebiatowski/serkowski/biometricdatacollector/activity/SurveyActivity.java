package com.trzebiatowski.serkowski.biometricdatacollector.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.os.Bundle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trzebiatowski.serkowski.biometricdatacollector.R;
import com.trzebiatowski.serkowski.biometricdatacollector.dto.QuestionDto;
import com.trzebiatowski.serkowski.biometricdatacollector.dto.QuestionsDto;
import com.trzebiatowski.serkowski.biometricdatacollector.fragment.TextQuestionFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SurveyActivity extends AppCompatActivity {

    private QuestionsDto questions;

    public SurveyActivity() {
        super(R.layout.activity_survey);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        questions = ReadQuestions();

        if (savedInstanceState == null) {
            
            QuestionDto question = questions.getQuestions().remove(0);

            if(question.getType().equals("text")) {
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .add(R.id.survey_fragment_container_view, TextQuestionFragment.class, null)
                        .commit();
            }
        }
    }

    public QuestionsDto ReadQuestions(){
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            AssetManager am = getApplicationContext().getAssets();
            InputStream stream = am.open("biometricdatacollector-survey.json");

            BufferedReader r = new BufferedReader(new InputStreamReader(stream));
            StringBuilder total = new StringBuilder();

            for (String line; (line = r.readLine()) != null; ) {
                total.append(line).append('\n');
            }

            return objectMapper.readValue(total.toString(), QuestionsDto.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}