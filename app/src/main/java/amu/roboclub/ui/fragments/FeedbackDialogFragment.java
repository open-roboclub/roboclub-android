package amu.roboclub.ui.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import amu.roboclub.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedbackDialogFragment extends BottomSheetDialogFragment {

    public FeedbackDialogFragment() {
    }

    public static FeedbackDialogFragment newInstance() {
        return new FeedbackDialogFragment();
    }

    @BindView(R.id.feedbackEdt)
    EditText edt;
    @BindView(R.id.send)
    ImageView send;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("feedback");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.bottomsheet_feedback, container, false);

        ButterKnife.bind(this, root);

        final FeedbackDialogFragment current = this;

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String feedback = edt.getText().toString();
                if (feedback.isEmpty()) {
                    Toast.makeText(getContext(), "Can't send empty feedback!", Toast.LENGTH_SHORT).show();
                    return;
                }

                databaseReference.push().setValue(feedback, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if(databaseError != null)
                            Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getContext(), "Feedback Posted", Toast.LENGTH_SHORT).show();
                    }
                });

                current.dismiss();

            }
        });
        return root;
    }
}
