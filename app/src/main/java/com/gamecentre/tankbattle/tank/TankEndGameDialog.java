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
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.gamecentre.tankbattle.R;

import java.util.Locale;

public class TankEndGameDialog extends Dialog implements View.OnTouchListener{


    public AppCompatActivity activity;
    public Dialog d;
    TankView mTankView;

    public Button videoBtn2;
    public TankTextView videoBtn, goldBtn;
    public ImageView  goldBtn2;
    private TankTextView timerTxt, goldCountTxt, closeBtn;
    SharedPreferences settings;
    int tanks, golds;


    public TankEndGameDialog(AppCompatActivity a, TankView mTankView) {
        super(a);
        this.activity = a;
        this.mTankView = mTankView;
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

        setContentView(R.layout.activity_tank_end_game);
        setCancelable(false);

        videoBtn = (TankTextView) findViewById(R.id.videoBtn);
        goldBtn = (TankTextView) findViewById(R.id.goldBtn);
        videoBtn2 = (Button) findViewById(R.id.videoBtn2);
        goldBtn2 = (ImageView) findViewById(R.id.goldBtn2);
        videoBtn.setOnTouchListener(this);
        goldBtn.setOnTouchListener(this);
        videoBtn2.setOnTouchListener(this);
        goldBtn2.setOnTouchListener(this);

        closeBtn = (TankTextView) findViewById(R.id.closeBtn);
        closeBtn.setOnTouchListener(this);

        goldCountTxt = (TankTextView) findViewById(R.id.retryGoldCnt);

//        timerTxt = (TankTextView)findViewById(R.id.timer);

        settings = activity.getSharedPreferences("TankSettings", 0);
        golds = settings.getInt(TankActivity.GOLD,0);
        goldCountTxt.setText(String.format(Locale.ENGLISH,"x%d",golds));
//        if(tanks <= 0) {
//            goldBtn.setBackground(ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.shop,null));
//        }

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if(TankView.CHECKING_RETRY != 2 && TankView.CHECKING_RETRY != 3) {
                    TankView.CHECKING_RETRY = 4;
                }
                mTankView.resumeNoAds();
            }
        });
    }

    public TankView getTankView() {
        return this.mTankView;
    }

    public void setGoldCount() {
        golds = settings.getInt(TankActivity.GOLD,0);
        goldCountTxt.setText(String.format(Locale.US,"x%d",golds));
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent m) {

        if(m.getAction() == MotionEvent.ACTION_DOWN){
            int id = v.getId();
            if (id == R.id.videoBtn || id == R.id.videoBtn2) {
                ((TankActivity) activity).showRewardedVideo(this);
            }
            else if (id == R.id.goldBtn || id == R.id.goldBtn2) {
                int cost = Integer.parseInt(activity.getResources().getString(R.string.playOnGoldCost).replace("x", ""));
                if (golds >= cost) {
                    TankView.CHECKING_RETRY = 3;
                    mTankView.updateP1Lives(Integer.parseInt(activity.getResources().getString(R.string.retryGoldAmnt).replace("x", "")));
                    golds = settings.getInt(TankActivity.GOLD, 0);
                    golds -= cost;
                    goldCountTxt.setText(String.format(Locale.ENGLISH, "x%d", golds));
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putInt(TankActivity.GOLD, golds);
                    editor.commit();
                    ((TankActivity) activity).updateBonusStack();
                } else {
                    ((TankActivity) activity).openStore(this);
                }
            }
            else if (id == R.id.closeBtn) {
                dismiss();
            }
        }
        return true;
    }
}
