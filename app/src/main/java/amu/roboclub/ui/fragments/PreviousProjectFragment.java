package amu.roboclub.ui.fragments;

import amu.roboclub.models.Project;

public class PreviousProjectFragment extends CurrentProjectFragment {

    public PreviousProjectFragment() {
        // Required empty public constructor
    }

    public static PreviousProjectFragment newInstance() {
        PreviousProjectFragment fragment = new PreviousProjectFragment();
        return fragment;
    }

    @Override
    protected void loadProjects(){
        projects.add(new Project("Sample Project", "Areeb Jamal, Divy Prakash, Priya Varshney", "", "http://www.hostinger.in/static/images/logo-in.png"));

        pAdapter.notifyDataSetChanged();
    }

}