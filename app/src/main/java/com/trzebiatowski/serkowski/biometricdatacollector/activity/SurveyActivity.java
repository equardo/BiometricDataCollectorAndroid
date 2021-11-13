package com.trzebiatowski.serkowski.biometricdatacollector.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trzebiatowski.serkowski.biometricdatacollector.R;
import com.trzebiatowski.serkowski.biometricdatacollector.TouchEventListener;
import com.trzebiatowski.serkowski.biometricdatacollector.dto.QuestionDto;
import com.trzebiatowski.serkowski.biometricdatacollector.dto.QuestionsDto;
import com.trzebiatowski.serkowski.biometricdatacollector.fragment.MultipleChoiceQuestionFragment;
import com.trzebiatowski.serkowski.biometricdatacollector.fragment.QuestionFragment;
import com.trzebiatowski.serkowski.biometricdatacollector.fragment.SurveyDoneFragment;
import com.trzebiatowski.serkowski.biometricdatacollector.fragment.TextQuestionFragment;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SurveyActivity extends AppCompatActivity {

    private static final String ARG_QUESTION_TEXT = "question_text";
    private static final String ARG_POSSIBLE_ANSWERS = "possible_answers";
    private QuestionsDto questions;
    private ArrayList<String> answers;

    private TouchEventListener touchEventListener;
    private FragmentManager manager;

    public SurveyActivity() {
        super(R.layout.activity_survey);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        manager = getSupportFragmentManager();

        TextView tempText = findViewById(R.id.tempTextView);
        touchEventListener = new TouchEventListener(tempText, getApplicationContext());

        findViewById(R.id.survey_activity_layout).setOnTouchListener(touchEventListener);
        findViewById(R.id.next_question_button).setOnTouchListener(touchEventListener);

        if (savedInstanceState == null) {
            questions = readQuestions();
            answers = new ArrayList<>();
            nextQuestion(null, true);
        }
    }

    public void nextQuestion(View v) {
        nextQuestion(v, false);
    }

    public void nextQuestion(View v, boolean firstCall) {

        if(!firstCall) {
            if (v.getTag().equals("noQuestionsLeft")) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
            else {
                QuestionFragment fragment = (QuestionFragment) manager.findFragmentById(R.id.survey_fragment_container_view);
                answers.add(fragment.getAnswer());
            }
        }

        QuestionDto question;

        try {
            question = questions.getQuestions().remove(0);
        }
        catch (IndexOutOfBoundsException e) {
            setSurveyDoneView();
            return;
        }

        changeQuestionFragment(question);
    }

    private void setSurveyDoneView() {
        Button nextButton = findViewById(R.id.next_question_button);

        nextButton.setText(R.string.finish_survey_button_text);
        nextButton.setTag("noQuestionsLeft");

        manager.beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.survey_fragment_container_view, SurveyDoneFragment.class, null)
                .commit();
    }

    private void changeQuestionFragment(QuestionDto question) {

        Bundle args = new Bundle();
        args.putString(ARG_QUESTION_TEXT, question.getText());

        switch (question.getType()) {
            case "text": {
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .add(R.id.survey_fragment_container_view, TextQuestionFragment.class, args)
                        .commit();
                break;
            }
            case "multiple_choice": {

                if(!question.getAnswers().isEmpty()) {
                    args.putStringArrayList(ARG_POSSIBLE_ANSWERS, question.getAnswers());
                }

                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .add(R.id.survey_fragment_container_view, MultipleChoiceQuestionFragment.class, args)
                        .commit();
                break;
            }
        }
    }

    private QuestionsDto readQuestions(){
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