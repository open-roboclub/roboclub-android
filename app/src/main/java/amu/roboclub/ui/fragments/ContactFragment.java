package amu.roboclub.ui.fragments;

import amu.roboclub.R;
import amu.roboclub.models.Contact;
import amu.roboclub.ui.adapters.ContactAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class ContactFragment extends Fragment {
    private List<Contact> contacts = new ArrayList<>();
    private RecyclerView recyclerView;
    private ContactAdapter cAdapter;

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

        cAdapter = new ContactAdapter(getContext(), contacts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(cAdapter);

        loadContacts();

        return root;
    }

    private void loadContacts() {
        contacts.add(new Contact(
                "Nishant Pratap Singh",
                "Co-Ordinator",
                "http://www.amuroboclub.in/img/members/NishantPratapSingh.jpg",
                new String[]{
                        "+91-9412272766",
                        "npsaligarh@gmail.com",
                        "https://facebook.com/NPSIN"}));
        contacts.add(new Contact(
                "Amar Upadhyay",
                "Co-Ordinator",
                "http://www.amuroboclub.in/img/members/AmarUpadhyay.jpg",
                new String[]{
                        "+91-9045414527",
                        "chat2amar@yahoo.in",
                        "https://www.facebook.com/amarupadhyay.happy",
                        "https://plus.google.com/u/0/107125332291865356506/posts",
                        "https://www.linkedin.com/profile/view?id=380171064"}));
        contacts.add(new Contact(
                "Mohd Talha",
                "Co-Ordinator",
                "http://www.amuroboclub.in/img/members/MohdTalha.jpg",
                new String[]{
                        "+91-8755174490",
                        "mohdtalha13@gmail.com",
                        "https://www.facebook.com/talha.mohd.5",
                        "https://www.linkedin.com/profile/view?id=403978334"}));

        cAdapter.notifyDataSetChanged();

    }


}
