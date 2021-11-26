package com.trzebiatowski.serkowski.biometricdatacollector.ui.activity;

import static com.trzebiatowski.serkowski.biometricdatacollector.utility.FileOperations.readConfigFile;
import static com.trzebiatowski.serkowski.biometricdatacollector.utility.FileOperations.writeToFile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.trzebiatowski.serkowski.biometricdatacollector.R;
import com.trzebiatowski.serkowski.biometricdatacollector.receiver.StopDataCollectionReceiver;
import com.trzebiatowski.serkowski.biometricdatacollector.dto.ConfigFileDto;
import com.trzebiatowski.serkowski.biometricdatacollector.listener.TouchEventListener;
import com.trzebiatowski.serkowski.biometricdatacollector.dto.QuestionDto;
import com.trzebiatowski.serkowski.biometricdatacollector.ui.fragment.MultipleChoiceQuestionFragment;
import com.trzebiatowski.serkowski.biometricdatacollector.ui.fragment.QuestionFragment;
import com.trzebiatowski.serkowski.biometricdatacollector.ui.fragment.SliderQuestionFragment;
import com.trzebiatowski.serkowski.biometricdatacollector.ui.fragment.SurveyDoneFragment;
import com.trzebiatowski.serkowski.biometricdatacollector.ui.fragment.TextQuestionFragment;

import java.util.ArrayList;

public class SurveyActivity extends AppCompatActivity {

    private static final String ARG_QUESTION_TEXT = "question_text";
    private static final String ARG_POSSIBLE_ANSWERS = "possible_answers";
    private static final String ARG_VALUE_FROM = "value_from";
    private static final String ARG_VALUE_TO = "value_to";
    private static final String ARG_STEP_SIZE = "step_size";

    public static final String ANSWERS_DATA_FOLDER_NAME = "answers";

    private ArrayList<String> answers;

    private TouchEventListener touchEventListener;
    private FragmentManager manager;
    private ConfigFileDto configData;
    private String currentFileSuffix;
    private Bundle extras;

    public SurveyActivity() {
        super(R.layout.activity_survey);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        manager = getSupportFragmentManager();

        TextView tempText = findViewById(R.id.tempTextView);

        if (savedInstanceState == null) {
            configData = readConfigFile(this);
            if(extras == null) {
                currentFileSuffix = getIntent().getStringExtra("currentFileSuffix");
            }
            else {
                currentFileSuffix = extras.getString("currentFileSuffix");
            }
            answers = new ArrayList<>();
            nextQuestion(null, true);
        }

        touchEventListener = new TouchEventListener(tempText, getApplicationContext(), currentFileSuffix);
        findViewById(R.id.survey_activity_layout).setOnTouchListener(touchEventListener);
        findViewById(R.id.next_question_button).setOnTouchListener(touchEventListener);
    }

    public void nextQuestion(View v) {
        nextQuestion(v, false);
    }

    public void nextQuestion(View v, boolean firstCall) {

        if(!firstCall) {
            if (v.getTag().equals("noQuestionsLeft")) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return;
            }
            else {
                QuestionFragment fragment = (QuestionFragment) manager.findFragmentById(R.id.survey_fragment_container_view);
                answers.add(fragment.getAnswer());
            }
        }

        QuestionDto question;

        try {
            question = configData.getQuestions().remove(0);
        }
        catch (IndexOutOfBoundsException e) {
            setStopDataCollectionAlarm();
            saveAnswerData();
            setSurveyDoneView();
            return;
        }

        changeQuestionFragment(question);
    }

    private void saveAnswerData() {
        writeToFile(this, "[", ANSWERS_DATA_FOLDER_NAME, currentFileSuffix, false);
        for(int i = 0; i < answers.size(); i++) {
            String jsonObject = "";
            if( i!=0 ) {
                jsonObject += ",";
            }
            jsonObject += "\"" + answers.get(i) + "\"";
            writeToFile(this, jsonObject, ANSWERS_DATA_FOLDER_NAME, currentFileSuffix, false);
        }
        writeToFile(this, "]", ANSWERS_DATA_FOLDER_NAME, currentFileSuffix, false);
    }

    private void setStopDataCollectionAlarm() {

        AlarmManager alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, StopDataCollectionReceiver.class);
        intent.putExtra("collectionTime", configData.getCollectionTimeSeconds());
        intent.putExtra("timeBetweenSurveys", configData.getTimeBetweenSurveysMinutes());
        intent.putExtra("postponeTimeSeconds", configData.getPostponeTimeSeconds());
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 5,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmMgr.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() +
                        (long)configData.getCollectionTimeSeconds() * 1000, alarmIntent);

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
        args.putString("currentFileSuffix", currentFileSuffix);

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
            case "slider": {

                args.putInt(ARG_VALUE_FROM, question.getValueFrom());
                args.putInt(ARG_VALUE_TO, question.getValueTo());
                args.putInt(ARG_STEP_SIZE, question.getStepSize());

                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .add(R.id.survey_fragment_container_view, SliderQuestionFragment.class, args)
                        .commit();
                break;
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intents)
    {
        super.onNewIntent(intents);

        if (intents != null) {
            extras = intents.getExtras();
        }
    }

}