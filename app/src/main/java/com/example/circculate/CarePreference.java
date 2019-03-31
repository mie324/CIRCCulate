package com.example.circculate;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.circculate.Model.CarePreferenceAnswerModel;
import com.example.circculate.utils.Tools;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class CarePreference extends AppCompatActivity {
    private static final int MAX_STEP = 6;

    private ViewPager viewPager;
    private PreViewPagerAdapter viewPagerAdapter;
    private Button btnNext, btnPrevious;
    private AppCompatEditText content;
    private static final String TAG = "CarePreference";
    private String[] titleArray = {
            "What do I need to think about or do before I feel ready to have the conversation?",
            "What makes my life meaningful?",
            "What do I value most?",
            "What are the three most important things that I want my SDM, family, friends and/or health care providers to understand about my future personal or health care wishes?",
            "What concerns do I have about how my health may change in the future?",
            "Other thoughts:"
    };
    private FirebaseAuth mAuth;
    private String username;
    private String[] desArray = {
            "",
            "e.g. time with family or friends, faith, love for garden, music, art, work, hobbies, pet",
            "e.g. live independently, make my own decisions, enjoy a good meal, have my privacy upheld, recognize or talk with others",
            "",
            "",
            ""
    };
    private String[] answers = new String[MAX_STEP];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care_preference);

        username = getIntent().getStringExtra("username");
        mAuth = FirebaseAuth.getInstance();
        initComponents();
        Tools.setSystemBarColor(this, R.color.grey_5);
        Tools.setSystemBarLight(this);
        showIntroDialog();


    }

    private void showIntroDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_preference_intro);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        ((Button)dialog.findViewById(R.id.bt_got_it)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void initComponents(){
        viewPager = findViewById(R.id.view_pager);
        btnNext = findViewById(R.id.btn_next);
        btnPrevious = findViewById(R.id.btn_previous);
        viewPagerAdapter = new PreViewPagerAdapter();
        viewPager.setAdapter(viewPagerAdapter);
        content = findViewById(R.id.content);
        bottomProgressDots(0);

        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }

        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(content.getText().toString())){
                    content.setError("You must fill in the text");
                }else {
                    answers[viewPager.getCurrentItem()] = content.getText().toString();

                    int current = viewPager.getCurrentItem() + 1;

                    if(current < MAX_STEP){
                        content.setText(answers[current]);
                        viewPager.setCurrentItem(current);
                    }else {
                        //submit the result to db
                        for (String answer: answers){
                            Log.d(TAG, "onClick: " + answer);
                        }
                        submitNewPreference();
//                        finish();
                    }
                }

            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(viewPager.getCurrentItem() == 0){
                    return;
                }

                int current = viewPager.getCurrentItem() - 1;
                content.setText(answers[current]);
                viewPager.setCurrentItem(current);
            }
        });
    }


    private void submitNewPreference(){
        CarePreferenceAnswerModel carePreferenceAnswer = new CarePreferenceAnswerModel(mAuth.getUid(),
                username, answers);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("carepreferences").document(carePreferenceAnswer.getUserId()).set(carePreferenceAnswer)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplication(), "Successfully submit care preference.", Toast.LENGTH_SHORT).show();
                            finish();
                        }else {
                            Log.d(TAG, "onComplete: " + task.getException().toString());
                            Toast.makeText(getApplication(), "Something is wrong.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            bottomProgressDots(position);

            if(position == titleArray.length - 1){
                btnNext.setText("SUBMIT");
                btnNext.setBackgroundColor(getResources().getColor(R.color.orange_400));
                btnNext.setTextColor(Color.WHITE);
            }else {
                btnNext.setText("NEXT");
                btnNext.setBackgroundColor(getResources().getColor(R.color.grey_10));
                btnNext.setTextColor(getResources().getColor(R.color.grey_90));
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void bottomProgressDots(int currentIndex) {
        LinearLayout dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        ImageView[] dots = new ImageView[MAX_STEP];

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            int width_height = 15;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(width_height, width_height));
            params.setMargins(10, 10, 10, 10);
            dots[i].setLayoutParams(params);
            dots[i].setImageResource(R.drawable.shape_circle);
            dots[i].setColorFilter(getResources().getColor(R.color.grey_20), PorterDuff.Mode.SRC_IN);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[currentIndex].setImageResource(R.drawable.shape_circle);
            dots[currentIndex].setColorFilter(getResources().getColor(R.color.orange_400), PorterDuff.Mode.SRC_IN);
        }
    }

    public class PreViewPagerAdapter extends PagerAdapter{
        private LayoutInflater layoutInflater;

        public PreViewPagerAdapter(){

        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.item_stepper_wizard, container, false);

            ((TextView)view.findViewById(R.id.title)).setText(titleArray[position]);
            ((TextView)view.findViewById(R.id.description)).setText(desArray[position]);

            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return titleArray.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
