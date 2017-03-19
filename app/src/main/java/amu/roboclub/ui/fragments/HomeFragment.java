package amu.roboclub.ui.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import amu.roboclub.R;
import amu.roboclub.ui.adapters.PagerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;


public class HomeFragment extends Fragment {

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.pager)
    ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tabbed, container, false);

        ButterKnife.bind(this, root);

        final PagerAdapter adapter = new PagerAdapter(
                getActivity().getSupportFragmentManager(),
                IntroFragment.class,
                NewsFragment.class
        );
        adapter.setTitles(getString(R.string.intro), getString(R.string.news));
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

        viewPager.setCurrentItem(1, true);

        return root;
    }


}
