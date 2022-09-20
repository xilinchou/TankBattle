package com.gamecentre.tankbattle.tank;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.gamecentre.tankbattle.R;
import com.gamecentre.tankbattle.sound.SoundManager;
import com.gamecentre.tankbattle.sound.Sounds;

public class TankPauseGameDialog extends Dialog implements View.OnTouchListener{


    public AppCompatActivity activity;
    public Dialog d;
    TankView mTankView;
    ImageView soundBtn, vibrateBtn;
    boolean sound, vibrate;
    LinearLayout view;

    private int state = 0;

    public TankTextView continueBtn, newGameBtn, endGameBtn;
    private TankTextView closeBtn;
    SharedPreferences settings;
    int games;


    public TankPauseGameDialog(AppCompatActivity a, TankView mTankView) {
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

        setContentView(R.layout.activity_tank_pause_game);
        setCancelable(false);

        view = findViewById(R.id.settings_dialog);

        continueBtn = findViewById(R.id.continueBtn);
        newGameBtn = findViewById(R.id.newGameBtn);
        endGameBtn = findViewById(R.id.endGameBtn);



        continueBtn.setOnTouchListener(this);
        newGameBtn.setOnTouchListener(this);
        endGameBtn.setOnTouchListener(this);

        soundBtn = findViewById(R.id.pSoundBtn);
        vibrateBtn = findViewById(R.id.pVibrateBtn);
        soundBtn.setOnTouchListener(this);
        vibrateBtn.setOnTouchListener(this);

        closeBtn = (TankTextView) findViewById(R.id.pauseCloseBtn);
        closeBtn.setOnTouchListener(this);

        settings = activity.getSharedPreferences("TankSettings", 0);
        games = settings.getInt(TankActivity.RETRY_COUNT,0);

        sound = settings.getBoolean(TankMenuActivity.PREF_MUTED,true);
        vibrate = settings.getBoolean(TankMenuActivity.PREF_VIBRATE,true);

        if(sound) {
            soundBtn.setBackground(ResourcesCompat.getDrawable(activity.getResources(),R.drawable.psound,null));
        }
        else {
            soundBtn.setBackground(ResourcesCompat.getDrawable(activity.getResources(),R.drawable.xsound,null));
        }

        if(vibrate) {
            vibrateBtn.setBackground(ResourcesCompat.getDrawable(activity.getResources(),R.drawable.pvibrate,null));
        }
        else {
            vibrateBtn.setBackground(ResourcesCompat.getDrawable(activity.getResources(),R.drawable.xvibrate,null));
        }


        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                SharedPreferences.Editor editor;
                switch (state) {
                    case 0:
                        mTankView.resumeNoAds();
                        ((TankActivity)activity).enableControls();
                        break;
                    case 1:
                        games--;
                        editor = settings.edit();
                        editor.putInt(TankActivity.RETRY_COUNT,games);
                        editor.commit();
                        mTankView.resumeNoAds();
                        mTankView.sendPlayerInfo(TankView.RESTART);
                        mTankView.retryStage();
                        break;
                    case 2:
                        games--;
                        editor = settings.edit();
                        editor.putInt(TankActivity.RETRY_COUNT,games);
                        editor.commit();
                        mTankView.notifyEndGame = true;
                        mTankView.resumeNoAds();
//                        ((TankActivity)activity).endGame();
                        break;
                }
            }
        });

        setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                ViewGroup.LayoutParams viewParam = view.getLayoutParams();
//                viewParam.height*;
//                int w = viewParam.width;
//                view.setLayoutParams(viewParam);
            }
        });


    }



    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent m) {

        if(m.getAction() == MotionEvent.ACTION_DOWN){
            SoundManager.playSound(Sounds.TANK.CLICK);
            SharedPreferences.Editor editor;
            int id = v.getId();
            if (id == R.id.continueBtn || id == R.id.pauseCloseBtn) {
                state = 0;
                dismiss();
            } else if (id == R.id.newGameBtn) {
                if (games > 0) {
                    state = 1;
                    dismiss();
                }
            } else if (id == R.id.endGameBtn) {
                state = 2;
                dismiss();
            } else if (id == R.id.pSoundBtn) {
                if (sound) {
                    sound = false;
                    soundBtn.setBackground(ResourcesCompat.getDrawable(activity.getResources(), R.drawable.xsound, null));
                } else {
                    sound = true;
                    soundBtn.setBackground(ResourcesCompat.getDrawable(activity.getResources(), R.drawable.psound, null));
                }
                mTankView.setSound(sound);
                editor = settings.edit();
                editor.putBoolean(TankMenuActivity.PREF_MUTED, sound);
                editor.commit();
            } else if (id == R.id.pVibrateBtn) {
                if (vibrate) {
                    vibrate = false;
                    vibrateBtn.setBackground(ResourcesCompat.getDrawable(activity.getResources(), R.drawable.xvibrate, null));
                } else {
                    vibrate = true;
                    vibrateBtn.setBackground(ResourcesCompat.getDrawable(activity.getResources(), R.drawable.pvibrate, null));
                }
                mTankView.setVibrate(vibrate);
                editor = settings.edit();
                editor.putBoolean(TankMenuActivity.PREF_VIBRATE, vibrate);
                editor.commit();
            }
        }
        return true;
    }
}
