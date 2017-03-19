package amu.roboclub.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import amu.roboclub.R;
import amu.roboclub.ui.adapters.PagerAdapter;
import amu.roboclub.utils.CircleTransform;
import butterknife.BindView;
import butterknife.ButterKnife;


public class ProjectFragment extends Fragment {

    public ProjectFragment() {
        // Required empty public constructor
    }

    public static ProjectFragment newInstance() {
        return new ProjectFragment();
    }

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.pager)
    ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_tabbed, container, false);

        ButterKnife.bind(this, root);

        PagerAdapter adapter = new PagerAdapter(
                getActivity().getSupportFragmentManager(),
                PreviousProjectFragment.class,
                CurrentProjectFragment.class
        );
        adapter.setTitles(getString(R.string.completed), getString(R.string.ongoing));
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

        return root;
    }

    public static void setImage(Context context, ImageView imageView, String imgUrl) {
        if (imgUrl == null || imgUrl.contains("robo.jpg"))
            imageView.setImageDrawable(VectorDrawableCompat.create(context.getResources(), R.drawable.ic_gear, null));
        else
            Picasso.with(context)
                    .load(imgUrl)
                    .transform(new CircleTransform())
                    .into(imageView);
    }


}
