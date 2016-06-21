package amu.roboclub.ui.fragments;

import amu.roboclub.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PreviousProjectFragment extends Fragment {

    public PreviousProjectFragment() {
        // Required empty public constructor
    }

    public static PreviousProjectFragment newInstance() {
        PreviousProjectFragment fragment = new PreviousProjectFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_previous_project, container, false);
    }


}