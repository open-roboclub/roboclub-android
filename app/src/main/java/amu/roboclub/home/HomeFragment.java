package amu.roboclub.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import amu.roboclub.R;
import amu.roboclub.news.NewsFragment;
import amu.roboclub.ui.adapters.PagerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;


public class HomeFragment extends Fragment {

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.pager)
    ViewPager viewPager;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

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
