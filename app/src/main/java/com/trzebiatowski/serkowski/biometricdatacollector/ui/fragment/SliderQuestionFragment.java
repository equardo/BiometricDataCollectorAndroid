package com.trzebiatowski.serkowski.biometricdatacollector.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.android.material.slider.Slider;
import com.trzebiatowski.serkowski.biometricdatacollector.R;
import com.trzebiatowski.serkowski.biometricdatacollector.listener.TouchEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SliderQuestionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SliderQuestionFragment extends Fragment implements QuestionFragment, Slider.OnChangeListener {

    private static final String ARG_QUESTION_TEXT = "question_text";
    private static final String ARG_VALUE_FROM = "value_from";
    private static final String ARG_VALUE_TO = "value_to";
    private static final String ARG_STEP_SIZE = "step_size";

    private String questionText;
    private int valueFrom;
    private int valueTo;
    private int stepSize;
    private float currentValue;
    private String currentFileSuffix;

    private View inf;
    private Slider answerSlider;

    public SliderQuestionFragment() {
        // Required empty public constructor
    }

    public static SliderQuestionFragment newInstance(String questionText, int valueFrom,
                                                     int valueTo, int stepSize) {
        SliderQuestionFragment fragment = new SliderQuestionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUESTION_TEXT, questionText);
        args.putInt(ARG_VALUE_FROM, valueFrom);
        args.putInt(ARG_VALUE_TO, valueTo);
        args.putInt(ARG_STEP_SIZE, stepSize);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            questionText = getArguments().getString(ARG_QUESTION_TEXT);
            valueFrom = getArguments().getInt(ARG_VALUE_FROM);
            valueTo = getArguments().getInt(ARG_VALUE_TO);
            stepSize = getArguments().getInt(ARG_STEP_SIZE);
            currentFileSuffix = getArguments().getString("currentFileSuffix");
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (container != null) {
            container.removeAllViews();
        }

        inf = inflater.inflate(R.layout.fragment_slider_question, container, false);

        answerSlider = inf.findViewById(R.id.answer_slider);

        TextView questionTextView = inf.findViewById(R.id.question_text);
        questionTextView.setText(questionText);

        answerSlider.setValueFrom(valueFrom);
        answerSlider.setValueTo(valueTo);
        answerSlider.setStepSize(stepSize);

        currentValue = valueFrom;

        answerSlider.addOnChangeListener(this);
        answerSlider.setOnTouchListener(new TouchEventListener(questionTextView, inf.getContext(), currentFileSuffix));

        return inf;
    }

    @Override
    public String getAnswer() {
        return String.valueOf(currentValue);
    }

    @Override
    public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
        currentValue = value;
    }
}