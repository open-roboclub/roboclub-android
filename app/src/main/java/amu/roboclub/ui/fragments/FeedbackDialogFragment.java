package amu.roboclub.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import amu.roboclub.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedbackDialogFragment extends BottomSheetDialogFragment {

    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("feedback");
    @BindView(R.id.feedbackEdt)
    EditText edt;
    @BindView(R.id.send)
    ImageView send;

    public static FeedbackDialogFragment newInstance() {
        return new FeedbackDialogFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.bottomsheet_feedback, container, false);

        ButterKnife.bind(this, root);

        final FeedbackDialogFragment current = this;
        final Context context = getActivity().getApplicationContext();

        send.setOnClickListener(view -> {
            String feedback = edt.getText().toString();
            if (feedback.isEmpty()) {
                Toast.makeText(getContext(), R.string.empty_feedback, Toast.LENGTH_SHORT).show();
                return;
            }

            databaseReference.push().setValue(feedback, (databaseError, databaseReference) -> {
                String message = databaseError != null ? databaseError.getMessage() : context.getString(R.string.feedback_posted);
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            });

            current.dismiss();

        });
        return root;
    }
}
