package com.trzebiatowski.serkowski.biometricdatacollector.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.trzebiatowski.serkowski.biometricdatacollector.R;
import com.trzebiatowski.serkowski.biometricdatacollector.TouchEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MultipleChoiceQuestionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MultipleChoiceQuestionFragment extends Fragment implements View.OnClickListener, QuestionFragment{

    private static final String ARG_QUESTION_TEXT = "question_text";
    private static final String ARG_POSSIBLE_ANSWERS = "possible_answers";

    private String questionText;
    private ArrayList<String> possible_answers = new ArrayList<>();
    private RadioGroup radioGroup;
    private String finalAnswer = "";
    private View inf;

    public MultipleChoiceQuestionFragment() {
        // Required empty public constructor
    }

    public static TextQuestionFragment newInstance(String questionText, ArrayList<String> possible_answers) {
        TextQuestionFragment fragment = new TextQuestionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUESTION_TEXT, questionText);
        args.putStringArrayList(ARG_POSSIBLE_ANSWERS, possible_answers);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (container != null) {
            container.removeAllViews();
        }

        if (getArguments() != null) {
            questionText = getArguments().getString(ARG_QUESTION_TEXT);
            possible_answers = getArguments().getStringArrayList(ARG_POSSIBLE_ANSWERS);
        }

        inf = inflater.inflate(R.layout.fragment_multiple_choice_question, container, false);

        TextView questionTextView = inf.findViewById(R.id.question_text);
        questionTextView.setText(questionText);

        radioGroup = inf.findViewById(R.id.answers_radio_group);

        for (String answer:possible_answers) {
            RadioButton rdbtn = new RadioButton(inf.getContext());
            rdbtn.setId(View.generateViewId());
            rdbtn.setText(answer);
            rdbtn.setOnTouchListener(new TouchEventListener(questionTextView, inf.getContext()));
            rdbtn.setOnClickListener(this);
            radioGroup.addView(rdbtn);
        }

        return inf;
    }

    public String getAnswer() {
        return finalAnswer;
    }

    @Override
    public void onClick(View v) {
        int selectedId = radioGroup.getCheckedRadioButtonId();

        // find the radiobutton by returned id
        RadioButton radioButton = inf.findViewById(selectedId);

        finalAnswer = (String) radioButton.getText();
    }
}