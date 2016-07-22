package amu.roboclub.ui.fragments;

import amu.roboclub.R;
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
    protected void loadProjects() {
        String[] titles = getActivity().getResources().getStringArray(R.array.previous_title);
        String[] teams = getActivity().getResources().getStringArray(R.array.previous_team);
        String[] about = getActivity().getResources().getStringArray(R.array.previous_about);
        String[] images = getActivity().getResources().getStringArray(R.array.previous_images);

        int min = Math.min(titles.length, teams.length);
        for(int i = 0; i < min; i++){

            try {
                projects.add(new Project(titles[i], teams[i], about[i], images[i]));
            } catch (Exception e) {
                projects.add(new Project(titles[i], teams[i], about[i]));
            }
        }

        pAdapter.notifyDataSetChanged();
    }

}