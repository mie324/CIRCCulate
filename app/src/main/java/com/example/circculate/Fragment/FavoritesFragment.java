package com.example.circculate.Fragment;



import android.os.Bundle;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;

import android.view.View;
import android.view.ViewGroup;

import com.example.circculate.Model.UserModel;
import com.example.circculate.R;

import java.util.ArrayList;
import java.util.List;



/**
 * A simple {@link Fragment} subclass.
 */
public class FavoritesFragment extends Fragment {
    //new
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private UserModel user;
    private static final String TAG = "calendarFragment";
    private CalendarFragment calendarFrag;
    private AllEventFragment allFrag;
    private MyEventFragment myFrag;
    public FavoritesFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_add, menu);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_favorites, container, false);

        viewPager = root.findViewById(R.id.view_pager);
        user = (UserModel) getArguments().getSerializable("LoggedUser");
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getChildFragmentManager());

        calendarFrag = new CalendarFragment();
        allFrag = new AllEventFragment();
        myFrag = new MyEventFragment();
        adapter.addFragment(allFrag, "All Events", user);
        adapter.addFragment(myFrag, "My Events", user);
        adapter.addFragment(calendarFrag, "Calendar", user);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 2){
                    calendarFrag.onRefresh();
                }
                if(position == 0){
                    allFrag.onRefresh();
                }

                if(position == 1){
                    myFrag.onRefresh();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if(user != null){
            Log.d(TAG, "onCreateView: user not null");
        }
        tabLayout = root.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        // Inflate the layout for this fragment
        return root;
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
            if(user != null){
                Bundle bundle = new Bundle();
                bundle.putSerializable("LoggedUser",user);
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
