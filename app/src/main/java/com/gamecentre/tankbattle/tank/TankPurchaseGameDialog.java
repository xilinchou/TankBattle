package com.gamecentre.tankbattle.tank;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.gamecentre.tankbattle.R;
import com.gamecentre.tankbattle.sound.SoundManager;
import com.gamecentre.tankbattle.sound.Sounds;
import com.gamecentre.tankbattle.utils.CONST;
import com.gamecentre.tankbattle.utils.MessageRegister;

import java.text.SimpleDateFormat;

public class TankPurchaseGameDialog extends Dialog implements View.OnTouchListener, ServiceListener{


    public AppCompatActivity activity;
    public Dialog d;
    private boolean opened = false;
    TankView mTankView;

    public Button videoBtn2;
    public TankTextView videoBtn, gameBuy33, gameBuy63;
    public ImageView gameBuy32, gameBuy62;
    public TankTextView gameCounter, gameCountTxt, closeBtn;
    LinearLayout gameBuy3, gameBuy6;
    SharedPreferences settings;
    int tanks, games,golds;
    long time_left, life_time;

    long startTime = 0;
    int countTime = 5;
//    Handler timerHandler = new Handler();
//    Runnable timerRunnable = new Runnable() {
//        @Override
//        public void run() {
//            if(games >= CONST.Tank.MAX_GAME_COUNT){
//                timerHandler.removeCallbacks(this);
//                return;
//            }
//            long currentTime = System.currentTimeMillis();
//            time_left = (CONST.Tank.LIFE_DURATION_MINS*60000)-((currentTime - life_time) % (CONST.Tank.LIFE_DURATION_MINS*60000));
//
//            if(time_left < 1000) {
//                games++;
//                gameCountTxt.setText(String.valueOf(games));
//                SharedPreferences.Editor editor = settings.edit();
//                editor.putInt(TankActivity.RETRY_COUNT,games);
//            }
//            SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
//            gameCounter.setText(sdf.format(time_left));
//            timerHandler.postDelayed(this, 1000);
//        }
//    };

    public TankPurchaseGameDialog(AppCompatActivity a, TankView mTankView) {
        super(a);
        this.activity = a;
        this.mTankView = mTankView;
    }

    public TankPurchaseGameDialog(AppCompatActivity a) {
        super(a);
        this.activity = a;
        this.mTankView = null;
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

        setContentView(R.layout.activity_tank_purchase_game);
        setCancelable(false);

        videoBtn = (TankTextView) findViewById(R.id.videoGameBtn);
        videoBtn2 = (Button) findViewById(R.id.videoGameBtn2);

        gameBuy3 = (LinearLayout) findViewById(R.id.gameBuy3);
        gameBuy32 = (ImageView) findViewById(R.id.gameBuy32);
        gameBuy33 = (TankTextView) findViewById(R.id.gameBuy33);

        gameBuy6 = (LinearLayout) findViewById(R.id.gameBuy6);
        gameBuy62 = (ImageView) findViewById(R.id.gameBuy62);
        gameBuy63 = (TankTextView) findViewById(R.id.gameBuy63);

        videoBtn.setOnTouchListener(this);
        videoBtn2.setOnTouchListener(this);

        gameBuy3.setOnTouchListener(this);
        gameBuy32.setOnTouchListener(this);
        gameBuy32.setOnTouchListener(this);

        gameBuy6.setOnTouchListener(this);
        gameBuy62.setOnTouchListener(this);
        gameBuy62.setOnTouchListener(this);

        closeBtn = (TankTextView) findViewById(R.id.closeGameBuyBtn);
        closeBtn.setOnTouchListener(this);

        gameCountTxt = (TankTextView) findViewById(R.id.gameBuyCount);
        gameCounter = (TankTextView)findViewById(R.id.gameCounter);

        settings = activity.getSharedPreferences("TankSettings", 0);
        games = settings.getInt(TankActivity.RETRY_COUNT,0);
        golds = settings.getInt(TankActivity.GOLD,0);
        gameCountTxt.setText(String.valueOf(games));
//        if(tanks <= 0) {
//            goldBtn.setBackground(ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.shop,null));
//        }

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

                int games = settings.getInt(TankActivity.RETRY_COUNT,0);

                if(activity instanceof TankActivity) {
                    ((TankActivity) activity).retryCount.setText(String.valueOf(games));
                }
                else if(activity instanceof TankMenuActivity) {
                    ((TankMenuActivity) activity).retryTxt.setText(String.valueOf(games));
                }
                TankPurchaseGameDialog.this.opened = false;
            }
        });

//        life_time = ((TankActivity)activity).settings.getLong(TankActivity.LIFE_TIME,System.currentTimeMillis());


//        startTime = System.currentTimeMillis();
//        timerHandler.postDelayed(timerRunnable, 0);
        MessageRegister.getInstance().setServiceListener(this);
        opened = true;

    }

    public void onServiceMessageReceived(int games, long time_left, boolean h6) {
        if(opened){
            Log.d("SERVICE MESSAGE D", String.valueOf(games) + " " + time_left);
            SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
            if(h6){
                gameCounter.setText("");
            }
            else{
                gameCounter.setText(sdf.format(time_left));
            }

            gameCountTxt.setText(String.valueOf(games));
        }
    }

    public void setGoldCount() {
//        golds = settings.getInt(TankActivity.GOLD,0);
//        goldCountTxt.setText(String.format(Locale.US,"x%d",golds));
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent m) {

        if(m.getAction() == MotionEvent.ACTION_DOWN){
            int id = v.getId();
            if (id == R.id.videoGameBtn || id == R.id.videoGameBtn2) {
                if (activity instanceof TankActivity) {
                    ((TankActivity) activity).showRewardedVideo(this);
                } else if (activity instanceof TankMenuActivity) {
                    ((TankMenuActivity) activity).showRewardedVideo(this);
                }
            }
            else if (id == R.id.gameBuy3 || id == R.id.gameBuy32 || id == R.id.gameBuy33) {
                int cost = Integer.parseInt(activity.getResources().getString(R.string.game3_gold).replace("x", ""));
                if (golds >= cost) {
                    SoundManager.playSound(Sounds.TANK.CLICK);
                    int amnt = Integer.parseInt(activity.getResources().getString(R.string.game15Amnt).replace("+", ""));
                    golds = settings.getInt(TankActivity.GOLD, 0);
                    games = settings.getInt(TankActivity.RETRY_COUNT, 0);

                    golds -= cost;
                    games += amnt;
                    gameCountTxt.setText(String.valueOf(games));

                    SharedPreferences.Editor editor = settings.edit();
                    editor.putInt(TankActivity.GOLD, golds);
                    editor.putInt(TankActivity.RETRY_COUNT, games);
                    editor.commit();
                } else if (activity instanceof TankActivity) {
                    SoundManager.playSound(Sounds.TANK.CLICK2);
                    ((TankActivity) activity).openStore(this);
                }
                else{
                    SoundManager.playSound(Sounds.TANK.CLICK2);
                    ((TankMenuActivity) activity).openStore();
                }
            }
            else if (id == R.id.gameBuy6 || id == R.id.gameBuy62 || id == R.id.gameBuy63) {
                int cost = Integer.parseInt(activity.getResources().getString(R.string.game6h_gold).replace("x", ""));
                if (golds >= cost) {
                    SoundManager.playSound(Sounds.TANK.CLICK);
                    golds -= cost;
                    gameCountTxt.setText(String.valueOf(CONST.Tank.MAX_GAME_COUNT));
                    long time_6h = System.currentTimeMillis() + CONST.Tank.LIFE_DURATION_6HRS;
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putInt(TankActivity.GOLD, golds);
                    editor.putLong(TankActivity.LIFE_TIME_6H, time_6h);
                    editor.putInt(TankActivity.RETRY_COUNT, CONST.Tank.MAX_GAME_COUNT);
                    editor.commit();
                }
                else if (activity instanceof TankActivity) {
                    SoundManager.playSound(Sounds.TANK.CLICK2);
                    ((TankActivity) activity).openStore(this);
                }
                else{
                    SoundManager.playSound(Sounds.TANK.CLICK2);
                    ((TankMenuActivity) activity).openStore();
                }
            }
            else if (id == R.id.closeGameBuyBtn) {
                SoundManager.playSound(Sounds.TANK.CLICK);
                dismiss();
            }
        }
        return true;
    }
}
