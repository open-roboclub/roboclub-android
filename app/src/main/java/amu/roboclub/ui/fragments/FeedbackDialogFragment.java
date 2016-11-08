package amu.roboclub.ui.fragments;

import amu.roboclub.R;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class FeedbackDialogFragment extends BottomSheetDialogFragment {

    public static FeedbackDialogFragment newInstance() {
        FeedbackDialogFragment f = new FeedbackDialogFragment();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottomsheet_feedback, container, false);

        final EditText edt = (EditText) v.findViewById(R.id.feedbackEdt);
        ImageView send = (ImageView) v.findViewById(R.id.send);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String feedback = edt.getText().toString();
                if(feedback.isEmpty()){
                    Toast.makeText(getContext(), "Can't send empty feedback!", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        emailIntent.setType("vnd.android.cursor.item/email");
                        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"amuroboclub@gmail.com"});
                        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Feedback from App");
                        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, feedback);
                        startActivity(emailIntent);
                    } catch (ActivityNotFoundException error) {
                        Toast.makeText(getContext(), "No App can handle this!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        return v;
    }
}
