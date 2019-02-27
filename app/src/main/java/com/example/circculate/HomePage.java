package com.example.circculate;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
//import com.material.components.utils.Tools;

import com.google.firebase.auth.FirebaseAuth;
//import com.material.components.utils.Tools;

//import com.material.components.utils.Tools;

public class HomePage extends AppCompatActivity {

    private TextView mTextMessage;
    private BottomNavigationView navigation;
    private View search_bar;
    private ViewPager view_pager;
    private TabLayout tab_layout;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        mAuth = FirebaseAuth.getInstance();

        initToolbar();
        initComponent();
        switchToFavorites();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_logout, menu);
//        Tools.changeMenuIconColor(menu, getResources().getColor(R.color.grey_60));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_logout:
                Logout();
                return true;
            case R.id.action_addevent: {
                Log.d("select", "onOptionsItemSelected: click add event.");
                Intent intent = new Intent(this, AddEvent.class);
                startActivity(intent);
                return true;
            }
            default:
                return true;
        }
    }

    private void Logout() {
        mAuth.signOut();
        toLogIn();
    }

    private void toLogIn() {
        Intent intent = new Intent(this, LogIn.class);
        startActivity(intent);
    }


    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setNavigationIcon(R.drawable.ic_menu);
//        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.grey_60), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("Home Care");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//        Tools.setSystemBarColor(this, R.color.grey_5);
//        Tools.setSystemBarLight(this);
    }


    private void initComponent() {

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_recent:
                        //mTextMessage.setText(item.getTitle());
                        showToast("recent.");
                        switchToRecent();
                        return true;
                    case R.id.navigation_favorites:
                        showToast("favorites.");
                        switchToFavorites();
                        //mTextMessage.setText(item.getTitle());
                        return true;
                    case R.id.navigation_nearby:
                        showToast("nearby");
                        switchToNearby();
                        //mTextMessage.setText(item.getTitle());
                        return true;
                }
                return false;
            }
        });

        NestedScrollView nested_content = (NestedScrollView) findViewById(R.id.nested_scroll_view);
        nested_content.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY < oldScrollY) { // up
                    animateNavigation(false);

                }
                if (scrollY > oldScrollY) { // down
                    animateNavigation(true);

                }
            }
        });

        // display image



//        ((ImageButton) findViewById(R.id.bt_menu)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//
//        Tools.setSystemBarColor(this, R.color.grey_5);
//        Tools.setSystemBarLight(this);
    }


    boolean isNavigationHide = false;

    private void animateNavigation(final boolean hide) {
        if (isNavigationHide && hide || !isNavigationHide && !hide) return;
        isNavigationHide = hide;
        int moveY = hide ? (2 * navigation.getHeight()) : 0;
        navigation.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }



    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void switchToRecent(){
        getSupportActionBar().setTitle("Recordings");
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fragment_container, new RecentFragment()).commit();
    }

    public void switchToFavorites(){
        getSupportActionBar().setTitle("Calendar");
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fragment_container, new FavoritesFragment()).commit();
    }

    public void switchToNearby(){
        getSupportActionBar().setTitle("Timeline");
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fragment_container, new NearbyFragment()).commit();
    }


}