package com.example.circculate.Fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.circculate.Model.UserModel;
import com.example.circculate.R;

import java.util.ArrayList;
import java.util.List;



public class RecentFragment extends Fragment {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private UserModel user;
    private static final String TAG = "recentFragment";
    private RecrodFragment recordFragment;
    private LibraryFrament libraryFrament;

    public RecentFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static RecentFragment newInstance() {
        RecentFragment fragment = new RecentFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_recent, container, false);

        viewPager = root.findViewById(R.id.record_view_pager);
        user = (UserModel) getArguments().getSerializable("LoggedUser");
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getChildFragmentManager());
        recordFragment = new RecrodFragment();
        libraryFrament = new LibraryFrament();

        adapter.addFragment(recordFragment, "Record", user);
        adapter.addFragment(libraryFrament, "Library", user);
//        adapter.addFragment(calendarFrag, "Calendar", user);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    recordFragment.onRefresh();
                }
                if (position == 1) {
                    libraryFrament.onRefresh();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if (user != null) {
            Log.d(TAG, "onCreateView: user not null");
        }
        tabLayout = root.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        // Inflate the layout for this fragment
        return root;
    }


    @Override
    public void onDetach() {
        super.onDetach();

    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager manager) {
            super(manager);

        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title, UserModel user) {
            if (user != null) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("LoggedUser", user);
                fragment.setArguments(bundle);
                Log.d(TAG, "addFragment: add user to bundle");
            }

            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }


    }
}
