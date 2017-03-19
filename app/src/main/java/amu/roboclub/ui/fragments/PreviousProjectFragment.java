package amu.roboclub.ui.fragments;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class PreviousProjectFragment extends CurrentProjectFragment {

    public static PreviousProjectFragment newInstance() {
        return new PreviousProjectFragment();
    }

    @Override
    protected Query getDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference("projects").orderByChild("ongoing").equalTo(false);
    }

}