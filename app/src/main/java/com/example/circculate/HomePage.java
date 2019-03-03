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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
//import com.material.components.utils.Tools;

import com.example.circculate.Fragment.FavoritesFragment;
import com.example.circculate.Fragment.NearbyFragment;
import com.example.circculate.Fragment.RecentFragment;
import com.example.circculate.Model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
//import com.material.components.utils.Tools;

//import com.material.components.utils.Tools;

public class HomePage extends AppCompatActivity {


    private BottomNavigationView navigation;

    private FirebaseAuth mAuth;
    private static final String TAG = "HomePage";
    private FirebaseFirestore db;
    private UserModel user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
//        Bundle b = getIntent().getExtras();


        initToolbar();
        initComponent();
//        switchToFavorites();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_calender, menu);
//        Tools.changeMenuIconColor(menu, getResources().getColor(R.color.grey_60));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_logout:
                Logout();
                return true;
//            case R.id.action_allevents:
//                gotoAllEvents();
//                return true;
//            case R.id.action_yourevents:
//                gotoYourEvents();
//                return true;
            case R.id.action_addevent: {
                Log.d("select", "onOptionsItemSelected: click add event.");
                Intent intent = new Intent(this, AddEvent.class);
                intent.putExtra("loggedUser", user);
                startActivity(intent);
                return true;
            }
            default:
                return true;
        }
    }

//    private void gotoYourEvents() {
//        Intent intent = new Intent(this, YourEvents.class);
//        startActivity(intent);
//    }
//
//    private void gotoAllEvents() {
//        Intent intent = new Intent(this, AllEvents.class);
//        startActivity(intent);
//    }

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

        db.collection("users").document(mAuth.getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    reconstructUser(task.getResult());
                    switchToFavorites();
                    return;
                }else {
                    Intent intent = new Intent(getApplication(), LogIn.class);
                    startActivity(intent);
                }
            }
        });

    }

    private void reconstructUser(DocumentSnapshot result){
        this.user = result.toObject(UserModel.class);
    }

    boolean isNavigationHide = false;

    public void animateNavigation(final boolean hide) {
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
        Log.d(TAG, "switchToFavorites: " + user.getUsername());
        Bundle bundle = new Bundle();
        bundle.putSerializable("LoggedUser",user);
//        Log.d("username2", user.getUsername());
        FavoritesFragment fragment = new FavoritesFragment();
        fragment.setArguments(bundle);
        getSupportActionBar().setTitle("Calendar");
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    public void switchToNearby(){

        getSupportActionBar().setTitle("Timeline");
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fragment_container, new NearbyFragment()).commit();
    }


}