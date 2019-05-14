package amu.roboclub.project.list;

import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import amu.roboclub.R;
import amu.roboclub.project.Project;
import butterknife.BindView;
import butterknife.ButterKnife;


public class CurrentProjectFragment extends Fragment {

    @BindView(R.id.main_content)
    CoordinatorLayout mainLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private FirebaseRecyclerAdapter projectAdapter;

    public static CurrentProjectFragment newInstance() {
        return new CurrentProjectFragment();
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_project, container, false);


        ButterKnife.bind(this, root);

        GridLayoutManager llm = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(llm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        final Snackbar snackbar = Snackbar.make(mainLayout, R.string.loading_projects, Snackbar.LENGTH_INDEFINITE);
        snackbar.show();

        FirebaseRecyclerOptions<Project> options = new FirebaseRecyclerOptions.Builder<Project>()
                .setQuery(getDatabaseReference(), Project.class)
                .build();

        projectAdapter = new FirebaseRecyclerAdapter<Project, ProjectHolder>(options) {

            @NonNull
            @Override
            public ProjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_project, parent, false);

                return new ProjectHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ProjectHolder holder, int position, @NonNull Project project) {
                if (snackbar.isShown())
                    snackbar.dismiss();

                holder.setProject(getContext(), project);
            }

            @NonNull
            @Override
            public Project getItem(int position) {
                return super.getItem(getItemCount() - 1 - position);
            }
        };

        recyclerView.setAdapter(projectAdapter);
        projectAdapter.startListening(); // TODO: Move to LifecycleOwner or AAC

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            llm.setSpanCount(2);
        }

        return root;
    }

    protected Query getDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference("projects").orderByChild("ongoing").equalTo(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (projectAdapter != null) {
            projectAdapter.stopListening();
        }
    }
}