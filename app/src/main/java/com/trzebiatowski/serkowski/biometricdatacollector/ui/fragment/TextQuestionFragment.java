package com.trzebiatowski.serkowski.biometricdatacollector.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.trzebiatowski.serkowski.biometricdatacollector.R;
import com.trzebiatowski.serkowski.biometricdatacollector.listener.TouchEventListener;

public class TextQuestionFragment extends Fragment implements QuestionFragment{

    private static final String ARG_QUESTION_TEXT = "question_text";
    private static final String ARG_LISTENER = "listener";

    private String questionText;
    private EditText answerTextView;
    private TouchEventListener listener;

    public TextQuestionFragment() {
        // Required empty public constructor
    }

    public static TextQuestionFragment newInstance(String questionText, TouchEventListener listener) {
        TextQuestionFragment fragment = new TextQuestionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUESTION_TEXT, questionText);
        args.putSerializable(ARG_LISTENER, listener);
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
            listener = (TouchEventListener) getArguments().getSerializable(ARG_LISTENER);
        }

        View inf = inflater.inflate(R.layout.fragment_text_question, container, false);

        answerTextView = inf.findViewById(R.id.answer_input);
        TextView questionTextView = inf.findViewById(R.id.question_text);
        questionTextView.setText(questionText);

        listener.setContext(inf.getContext());
        inf.findViewById(R.id.answer_input).setOnTouchListener(listener);

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