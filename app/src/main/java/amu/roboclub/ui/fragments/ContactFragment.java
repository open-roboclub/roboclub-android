package amu.roboclub.ui.fragments;

import amu.roboclub.R;
import amu.roboclub.models.Contact;
import amu.roboclub.ui.viewholder.ContactHolder;
import amu.roboclub.utils.CircleTransform;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


public class ContactFragment extends Fragment {
    private RecyclerView recyclerView;

    public ContactFragment() {
        // Required empty public constructor
    }

    public static ContactFragment newInstance() {
        ContactFragment fragment = new ContactFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_contact, container, false);

        recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        DatabaseReference contactReference = FirebaseDatabase.getInstance().getReference("team/16");
        FirebaseRecyclerAdapter contactAdapter = new FirebaseRecyclerAdapter<Contact, ContactHolder>(Contact.class, R.layout.item_contact, ContactHolder.class, contactReference) {

            @Override
            protected void populateViewHolder(final ContactHolder holder, final Contact contact, int position) {
                holder.name.setText(contact.name);
                holder.position.setText(contact.position);

                Drawable mPlaceholderDrawable = ResourcesCompat.getDrawable(
                        getContext().getResources(),
                        R.drawable.ic_avatar, null);

                Picasso.with(getContext()).load(contact.thumbnail)
                        .placeholder(mPlaceholderDrawable)
                        .transform(new CircleTransform())
                        .into(holder.avatar);

                LinearLayout contactPanel = (LinearLayout) holder.root.findViewById(R.id.contactPanel);
                contactPanel.removeAllViews();

                if (contact.links == null)
                    return;

                for (String link : contact.links.values()) {
                    ImageButton im = new ImageButton(getContext());

                    int[] attrs = new int[]{R.attr.selectableItemBackgroundBorderless};
                    TypedArray typedArray = getActivity().obtainStyledAttributes(attrs);
                    im.setBackgroundResource(typedArray.getResourceId(0, 0));
                    im.setPadding(20, 20, 20, 20);

                    Intent i = null;

                    if (link.contains("facebook")) {
                        im.setImageResource(R.drawable.ic_facebook);
                        DrawableCompat.setTint(im.getDrawable(), ContextCompat.getColor(getContext(), R.color.fb_blue));

                        i = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(link));
                    } else if (link.contains("linkedin")) {
                        im.setImageResource(R.drawable.ic_linkedin);
                        DrawableCompat.setTint(im.getDrawable(), ContextCompat.getColor(getContext(), R.color.li_blue));

                        i = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(link));
                    } else if (link.contains("plus.google")) {
                        im.setImageResource(R.drawable.ic_gplus);
                        DrawableCompat.setTint(im.getDrawable(), ContextCompat.getColor(getContext(), R.color.gp_red));

                        i = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(link));
                    } else if (link.contains("+91")) {
                        im.setImageResource(R.drawable.ic_phone);
                        DrawableCompat.setTint(im.getDrawable(), ContextCompat.getColor(getContext(), R.color.ph_blue));

                        i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + link));
                    } else if (link.contains("@")) {
                        im.setImageResource(R.drawable.ic_email);
                        DrawableCompat.setTint(im.getDrawable(), ContextCompat.getColor(getContext(), R.color.em_red));

                        i = new Intent(Intent.ACTION_SEND);
                        i.setType("text/plain");
                        i.putExtra(Intent.EXTRA_EMAIL, new String[]{link});
                        i.putExtra(Intent.EXTRA_SUBJECT, "To Co-Ordinator, AMU RoboClub");
                    }


                    if (i != null) {
                        contactPanel.addView(im);
                        final Intent intent = i;
                        im.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    getActivity().startActivity(intent);
                                } catch (ActivityNotFoundException e) {
                                    Snackbar.make(holder.root, "No app can handle the request", Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        };

        recyclerView.setAdapter(contactAdapter);

        return root;
    }


}
