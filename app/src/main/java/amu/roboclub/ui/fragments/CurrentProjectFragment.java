package amu.roboclub.ui.fragments;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import amu.roboclub.ui.ProjectDetailActivity;
import amu.roboclub.R;
import amu.roboclub.models.Project;
import amu.roboclub.ui.viewholder.ProjectHolder;
import butterknife.BindView;
import butterknife.ButterKnife;


public class CurrentProjectFragment extends Fragment {

    int count;

    public static CurrentProjectFragment newInstance() {
        return new CurrentProjectFragment();
    }

    @BindView(R.id.main_content)
    CoordinatorLayout mainLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_project, container, false);

        count = 0;

        ButterKnife.bind(this, root);

        GridLayoutManager llm = new GridLayoutManager(getContext(), 1);
        llm.setReverseLayout(true);
        recyclerView.setLayoutManager(llm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        final Snackbar snackbar = Snackbar.make(mainLayout, R.string.loading_projects, Snackbar.LENGTH_INDEFINITE);
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
                    holder.team.setText(R.string.dividers);

                ProjectFragment.setImage(getContext(), holder.projectImg, project.image);

                holder.root.setOnClickListener(view -> {
                    if(project.description == null || project.description.equals(""))
                        return;

                    Intent intent = new Intent(getActivity(), ProjectDetailActivity.class);
                    intent.putExtra("project", project);
                    startActivity(intent);
                });
            }
        };

        recyclerView.setAdapter(projectAdapter);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            llm.setSpanCount(2);
        }

        getDatabaseReference().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                llm.smoothScrollToPosition(recyclerView, null, count++);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                count--;
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return root;
    }

    protected Query getDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference("projects").orderByChild("ongoing").equalTo(true);
    }


}