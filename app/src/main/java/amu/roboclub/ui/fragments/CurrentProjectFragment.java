package amu.roboclub.ui.fragments;

import amu.roboclub.R;
import amu.roboclub.models.Project;
import amu.roboclub.ui.adapters.ProjectAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class CurrentProjectFragment extends Fragment {
    protected List<Project> projects = new ArrayList<>();
    private RecyclerView recyclerView;
    protected ProjectAdapter pAdapter;

    public CurrentProjectFragment() {
        // Required empty public constructor
    }

    public static CurrentProjectFragment newInstance() {
        CurrentProjectFragment fragment = new CurrentProjectFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_project, container, false);

        recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);

        pAdapter = new ProjectAdapter(getContext(), projects);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(pAdapter);

        loadProjects();

        return root;
    }

    protected void loadProjects(){
        String[] titles = getActivity().getResources().getStringArray(R.array.current_title);
        String[] teams = getActivity().getResources().getStringArray(R.array.current_team);
        String[] about = getActivity().getResources().getStringArray(R.array.current_about);
        String[] images = getActivity().getResources().getStringArray(R.array.current_images);

        int min = titles.length;

        for(int i = 0; i < min; i++){
            try {
                projects.add(new Project(titles[i], teams[i], about[i], images[i]));
            } catch (Exception e) {
                projects.add(new Project(titles[i]));
            }
        }

        pAdapter.notifyDataSetChanged();;
    }


}