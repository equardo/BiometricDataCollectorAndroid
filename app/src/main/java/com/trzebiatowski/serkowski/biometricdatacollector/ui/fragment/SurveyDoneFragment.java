package com.trzebiatowski.serkowski.biometricdatacollector.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trzebiatowski.serkowski.biometricdatacollector.R;

public class SurveyDoneFragment extends Fragment {

    public SurveyDoneFragment() {
        // Required empty public constructor
    }

    public static SurveyDoneFragment newInstance() {
        return new SurveyDoneFragment();
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_survey_done, container, false);
    }
}