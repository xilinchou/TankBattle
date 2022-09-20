package com.gamecentre.tankbattle.tank;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.gamecentre.tankbattle.R;

import java.util.ArrayList;

public class TankDailyRewardDialog extends Dialog implements View.OnTouchListener {

    TankView mTankView;

    public AppCompatActivity activity;
    public Dialog dialog;
    public TankTextView watchBtn, cancelBtn;
    public ImageView watchBtn2;
    public RelativeLayout watch;
    SharedPreferences settings;
    int goldCount;
    int day;
    private boolean doubleReward = false;

    ArrayList<RelativeLayout> rewardCover = new ArrayList<>();

    ArrayList<RelativeLayout> reward = new ArrayList<>();


    private static String TOTAL_GOLD = "TOTAL GOLD";

    public TankDailyRewardDialog(AppCompatActivity a, TankView mTankView) {
        super(a);
        this.activity = a;
        this.mTankView = mTankView;
    }

    public TankDailyRewardDialog(AppCompatActivity a, int day) {
        super(a);
        this.activity = a;
        this.mTankView = null;
        this.dialog = null;
        this.day = day;
    }

    public TankDailyRewardDialog(AppCompatActivity a, Dialog d) {
        super(a);
        this.activity = a;
        this.mTankView = null;
        this.dialog = d;
    }




    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        setContentView(R.layout.activity_tank_daily_reward);
        setCancelable(false);

        settings = activity.getSharedPreferences("TankSettings", 0);

        watch = findViewById(R.id.watchBtn0);
        watchBtn = findViewById(R.id.watchRwdBtn);
        watchBtn2 = findViewById(R.id.watchRwdPlay);
        cancelBtn = findViewById(R.id.closeReward);
        watchBtn.setOnTouchListener(this);
        watchBtn2.setOnTouchListener(this);
        cancelBtn.setOnTouchListener(this);

        rewardCover.add(findViewById(R.id.day1));
        rewardCover.add(findViewById(R.id.day2));
        rewardCover.add(findViewById(R.id.day3));
        rewardCover.add(findViewById(R.id.day4));
        rewardCover.add(findViewById(R.id.day5));
        rewardCover.add(findViewById(R.id.day6));
        rewardCover.add(findViewById(R.id.day7));

//        reward.add(findViewById(R.id.day1rwd));
//        reward.add(findViewById(R.id.day2rwd));
//        reward.add(findViewById(R.id.day3rwd));
//        reward.add(findViewById(R.id.day4rwd));
//        reward.add(findViewById(R.id.day5rwd));
//        reward.add(findViewById(R.id.day6rwd));
//        reward.add(findViewById(R.id.day7rwd));

        for(int i = 1; i <= 7; i++) {
            if(i > day) {
                break;
            }
            if(i <= day) {
                rewardCover.get(i-1).setAlpha(0);
                if(i == day) {
//                    reward.get(i-1).setVisibility(View.VISIBLE);
                }
                else{
//                    reward.get(i-1).setVisibility(View.INVISIBLE);
                }
            }
            else {
                rewardCover.get(i-1).setAlpha(0.5f);
//                reward.get(i-1).setVisibility(View.INVISIBLE);
            }
        }




//        goldCountTxt = (TankTextView)findViewById(R.id.stashTxt);
//
//        settings = activity.getSharedPreferences("TankSettings", 0);
//
//        goldCount = settings.getInt(TankActivity.GOLD, 0);
//        goldCountTxt.setText(String.format("x%s", goldCount));

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                SharedPreferences.Editor editor = settings.edit();
                int bonus;
                switch (day) {
                    case 1:
                        bonus = settings.getInt(TankActivity.SHIELD,3);
                        bonus  = doubleReward ? bonus+2 : bonus+1;
                        editor.putInt(TankActivity.SHIELD,bonus);
                        break;
                    case 2:
                        bonus = settings.getInt(TankActivity.CLOCK,3);
                        bonus  = doubleReward ? bonus+2 : bonus+1;
                        editor.putInt(TankActivity.CLOCK,bonus);
                        break;
                    case 3:
                        bonus = settings.getInt(TankActivity.GRENADE,3);
                        bonus  = doubleReward ? bonus+2 : bonus+1;
                        editor.putInt(TankActivity.GRENADE,bonus);

                        bonus = settings.getInt(TankActivity.BOAT,3);
                        bonus  = doubleReward ? bonus+2 : bonus+1;
                        editor.putInt(TankActivity.BOAT,bonus);
                        break;
                    case 4:
                        bonus = settings.getInt(TankActivity.STAR,3);
                        bonus  = doubleReward ? bonus+2 : bonus+1;
                        editor.putInt(TankActivity.STAR,bonus);

                        bonus = settings.getInt(TankActivity.CLOCK,3);
                        bonus  = doubleReward ? bonus+2 : bonus+1;
                        editor.putInt(TankActivity.CLOCK,bonus);
                        break;
                    case 5:
                        bonus = settings.getInt(TankActivity.GUN,3);
                        bonus  = doubleReward ? bonus+2 : bonus+1;
                        editor.putInt(TankActivity.GUN,bonus);

                        bonus = settings.getInt(TankActivity.GOLD,3);
                        bonus  = doubleReward ? bonus+2 : bonus+1;
                        editor.putInt(TankActivity.GOLD,bonus);
                        break;
                    case 6:
                        bonus = settings.getInt(TankActivity.TANK,3);
                        bonus  = doubleReward ? bonus+2 : bonus+1;
                        editor.putInt(TankActivity.TANK,bonus);

                        bonus = settings.getInt(TankActivity.SHOVEL,3);
                        bonus  = doubleReward ? bonus+2 : bonus+1;
                        editor.putInt(TankActivity.SHOVEL,bonus);

                        bonus = settings.getInt(TankActivity.GOLD,3);
                        bonus  = doubleReward ? bonus+2 : bonus+1;
                        editor.putInt(TankActivity.GOLD,bonus);
                        break;
                    case 7:
                        bonus = settings.getInt(TankActivity.TANK,3);
                        bonus  = doubleReward ? bonus+2 : bonus+1;
                        editor.putInt(TankActivity.TANK,bonus);

                        bonus = settings.getInt(TankActivity.GUN,3);
                        bonus  = doubleReward ? bonus+2 : bonus+1;
                        editor.putInt(TankActivity.GUN,bonus);

                        bonus = settings.getInt(TankActivity.GOLD,3);
                        bonus  = doubleReward ? bonus+4 : bonus+2;
                        editor.putInt(TankActivity.GOLD,bonus);
                        break;
                }
                editor.apply();
            }
        });

    }


    public void setDoubleReward() {
        doubleReward = true;
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent m) {
        {
            if(m.getAction() == MotionEvent.ACTION_DOWN){
                int id = v.getId();
                if (id == R.id.watchRwdBtn || id == R.id.watchRwdPlay) {
                    boolean ret = ((TankMenuActivity)activity).showRewardedVideo(this);
                    if(ret) {
                        watch.setAlpha(0.2f);
                        watchBtn.setOnTouchListener(null);
                        watchBtn2.setOnTouchListener(null);
                    }
                }
                else if (id == R.id.closeReward) {
                    dismiss();
                }
            }
            return true;
        }
    }
}
