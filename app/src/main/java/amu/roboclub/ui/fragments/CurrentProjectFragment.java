package amu.roboclub.ui.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.graphics.drawable.VectorDrawableCompat;
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

import amu.roboclub.R;
import amu.roboclub.models.Project;
import amu.roboclub.ui.viewholder.ProjectHolder;
import amu.roboclub.utils.CircleTransform;


public class CurrentProjectFragment extends Fragment {

    public CurrentProjectFragment() {
        // Required empty public constructor
    }

    public static CurrentProjectFragment newInstance() {
        return new CurrentProjectFragment();
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

        final Snackbar snackbar = Snackbar.make(recyclerView, "Loading Projects", Snackbar.LENGTH_INDEFINITE);
        snackbar.show();

        FirebaseRecyclerAdapter projectAdapter = new FirebaseRecyclerAdapter<Project, ProjectHolder>(Project.class, R.layout.item_project, ProjectHolder.class, getDatabaseReference()) {

            @Override
            protected void populateViewHolder(final ProjectHolder holder, final Project project, int position) {
                if (snackbar.isShown())
                    snackbar.dismiss();
                holder.title.setText(project.name);

                if (project.team != null)
                    holder.team.setText(project.team);
                else
                    holder.team.setText("---");

                if (project.description != null)
                    holder.about.setText(project.description);
                else
                    holder.about.setVisibility(View.GONE);

                if (project.image == null || project.image.contains("robo.jpg"))
                    holder.projectImg.setImageDrawable(VectorDrawableCompat.create(getResources(), R.drawable.ic_gear, null));
                else
                    Picasso.with(getContext())
                            .load(project.getImage())
                            .transform(new CircleTransform())
                            .into(holder.projectImg);

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