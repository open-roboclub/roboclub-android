package amu.roboclub.ui.fragments;

import amu.roboclub.R;
import amu.roboclub.models.Project;
import amu.roboclub.ui.viewholder.ProjectHolder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;


public class CurrentProjectFragment extends Fragment {

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

        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        FirebaseRecyclerAdapter projectAdapter = new FirebaseRecyclerAdapter<Project, ProjectHolder>(Project.class, R.layout.item_project, ProjectHolder.class, getDatabaseReference()) {

            @Override
            protected void populateViewHolder(final ProjectHolder holder, final Project project, int position) {
                holder.title.setText(project.name);

                if (project.team != null)
                    holder.team.setText(project.team);
                else
                    holder.team.setVisibility(View.GONE);

                if (project.description != null)
                    holder.about.setText(project.description);
                else
                    holder.about.setVisibility(View.GONE);

                if (project.image != null)
                    Picasso.with(getContext()).load(project.getImage()).into(holder.projectImg);
                else
                    holder.projectImg.setVisibility(View.GONE);

                if (project.opened)
                    holder.hiddenView.setVisibility(View.VISIBLE);
                else
                    holder.hiddenView.setVisibility(View.GONE);

                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (holder.hiddenView.getVisibility() == View.VISIBLE) {
                            holder.hiddenView.setVisibility(View.GONE);
                            project.opened = false;
                        } else {
                            holder.hiddenView.setVisibility(View.VISIBLE);
                            project.opened = true;
                        }
                    }
                });
            }
        };

        recyclerView.setAdapter(projectAdapter);

        return root;
    }

    protected Query getDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference("projects").orderByChild("ongoing").equalTo(true);
    }


}