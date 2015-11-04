package com.akramamirza.photobabble;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class PagerFragment extends Fragment {

    public PagerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_pager, container, false);
        ViewPager pager = (ViewPager) rootView.findViewById(R.id.pager);
        pager.setAdapter(new MyFragmentPagerAdapter(getChildFragmentManager()));
        pager.setCurrentItem(1); // set the position to second item so camera fragment is the first thing
        return rootView;
    }

    class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new ReceivedSnapsFragment();
                case 1:
                    return new CameraFragment();
                case 2:
                    return new StoriesFragment();
            }

            return new CameraFragment();
        }


        @Override
        public int getCount() { return 3; }

    }

}
