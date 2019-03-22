package com.example.circculate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.widget.Toast;
//import com.material.components.utils.Tools;

import com.example.circculate.Fragment.FavoritesFragment;
import com.example.circculate.Fragment.NearbyFragment;
import com.example.circculate.Fragment.RecentFragment;
import com.example.circculate.Model.NotificationModel;
import com.example.circculate.Model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
//import com.material.components.utils.Tools;

//import com.material.components.utils.Tools;

public class HomePage extends AppCompatActivity {


    private BottomNavigationView navigation;
    private ArrayList<NotificationModel> notifications;
    private FirebaseAuth mAuth;
    private static final String TAG = "HomePage";
    private FirebaseFirestore db;
    private UserModel user;
    private TextView notificationNumHolder;
    private ListPopupWindow lpw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
//        Bundle b = getIntent().getExtras();
        Log.d(TAG, "onCreate: home page create");
        notifications = new ArrayList<>();

        initToolbar();
        initComponent();
//        switchToFavorites();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(msgReceiver,
                new IntentFilter("Data"));

    }

    private BroadcastReceiver msgReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String title = intent.getExtras().getString("title");
            String body = intent.getExtras().getString("body");
            Log.d(TAG, "onReceive: title: " + title + ", body: " + body);
            NotificationModel newNotification = new NotificationModel(title, body);
            notifications.add(newNotification);
            String numNotify = notifications.size() < 99 ? Integer.toString(notifications.size()) : "99+";
            notificationNumHolder.setText(numNotify);

            if(notifications.size() > 0){
                notificationNumHolder.setVisibility(View.VISIBLE);
            }
            //next steps:
            //1 create a list of notification objects
            //2 when received a notification, create an obj and push into the list
            //3 when notification button is clicked, start the notification display activity
            //4 add all the notification object into the intent and start the intent
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: homepage stop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: homepage on pause");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_calender, menu);
//        Tools.changeMenuIconColor(menu, getResources().getColor(R.color.grey_60));
        final MenuItem notificationItem = menu.findItem(R.id.action_notify);
        View actionView = notificationItem.getActionView();
        if(actionView == null){
            Log.d(TAG, "onCreateOptionsMenu: action view null");
        }
        notificationNumHolder = (TextView)actionView.findViewById(R.id.notification_badge);
        
        notificationNumHolder.setVisibility(View.INVISIBLE);

        notificationItem.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("select", "to notification activity");
                Intent intent = new Intent(getApplication(), Notification.class);
                intent.putExtra("notifications", notifications);
                startActivity(intent);
            }
        });
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

            case R.id.action_notify:{
                Log.d("select", "to notification activity");
                Intent intent = new Intent(this, Notification.class);
                intent.putExtra("notifications", notifications);
                startActivity(intent);
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

    @Override
    protected void onResume() {
        super.onResume();
        notifications.clear();
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
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if(task.isSuccessful()){
                    String currentToken = task.getResult().getToken();
                    Log.d(TAG, "onComplete: " + task.getResult().getToken());
                    if(!currentToken.equals(user.getTokenId())){
                        user.setTokenId(currentToken);
                        db.collection("users").document(mAuth.getUid()).set(user);
                    }
                }
            }
        });
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
        Bundle bundle = new Bundle();
        bundle.putSerializable("LoggedUser",user);
        RecentFragment recentFragment = new RecentFragment();
        recentFragment.setArguments(bundle);

        manager.beginTransaction().replace(R.id.fragment_container, recentFragment).commit();
    }

    public void switchToFavorites(){
        Log.d(TAG, "switchToFavorites: " + user.getUsername());
        Bundle bundle = new Bundle();
        bundle.putSerializable("LoggedUser",user);
//        Log.d("username2", user.getUsername());
        FavoritesFragment fragment = new FavoritesFragment();
        fragment.setArguments(bundle);
        getSupportActionBar().setTitle("Welcome, " + user.getUsername());
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    public void switchToNearby(){

        getSupportActionBar().setTitle("Timeline");
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fragment_container, new NearbyFragment()).commit();
    }


//    @Override
//    public boolean onTouch(View view, MotionEvent motionEvent) {
//        final int DRAWABLE_RIGHT = 2;
//
//        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
//            if (motionEvent.getX() >= (view.getWidth() - ((EditText) view)
//                    .getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
//                lpw.show();
//                return true;
//            }
//        }
//        return false;
//    }
}