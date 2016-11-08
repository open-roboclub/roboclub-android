package amu.roboclub.ui.fragments;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class PreviousProjectFragment extends CurrentProjectFragment {

    public PreviousProjectFragment() {
        // Required empty public constructor
    }

    public static PreviousProjectFragment newInstance() {
        PreviousProjectFragment fragment = new PreviousProjectFragment();
        return fragment;
    }

    @Override
    protected Query getDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference("projects").orderByChild("ongoing").equalTo(false);
    }

}