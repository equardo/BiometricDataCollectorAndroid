package com.trzebiatowski.serkowski.biometricdatacollector.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.trzebiatowski.serkowski.biometricdatacollector.R;

public class TextQuestionFragment extends Fragment implements QuestionFragment{

    private static final String ARG_QUESTION_TEXT = "question_text";
    private String questionText;
    private EditText answerTextView;

    public TextQuestionFragment() {
        // Required empty public constructor
    }

    public static TextQuestionFragment newInstance(String questionText) {
        TextQuestionFragment fragment = new TextQuestionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUESTION_TEXT, questionText);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (container != null) {
            container.removeAllViews();
        }

        if (getArguments() != null) {
            questionText = getArguments().getString(ARG_QUESTION_TEXT);
        }

        View inf = inflater.inflate(R.layout.fragment_text_question, container, false);

        answerTextView = inf.findViewById(R.id.answer_input);
        TextView questionTextView = inf.findViewById(R.id.question_text);
        questionTextView.setText(questionText);

        return inf;
    }

    public String getAnswer() {
        View v = getView();
        if(v != null) {
            answerTextView = v.findViewById(R.id.answer_input);
        }
        return answerTextView.getText().toString();
    }
}