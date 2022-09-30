package com.gamecentre.tankbattle.tank;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Window;
import android.view.WindowManager;

import com.gamecentre.tankbattle.R;
import com.gamecentre.tankbattle.sound.SoundManager;

public class TankIntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        setContentView(R.layout.activity_tank_intro);

        loadSounds();

        new CountDownTimer(2000, 500) {
            @Override
            public void onTick(long millisUntilFinished) {}

            @Override
            public void onFinish() {
                menuActivity();
            }
        }.start();
    }

    private void menuActivity() {
        startActivity(new Intent(this, TankTypeActivity.class));
        finish();
    }

    private void loadSounds() {
        SoundManager.getInstance();
        SoundManager.initSounds(this);
        int[] sounds = {
                R.raw.tnkbackground,
                R.raw.tnkbonus,
                R.raw.tnkbrick,
                R.raw.tnkexplosion,
                R.raw.tnkfire,
                R.raw.tnkgameover,
                R.raw.tnkgamestart,
                R.raw.tnkscore,
                R.raw.tnksteel,
                R.raw.tnkpowerup,
                R.raw.tnkpause,
                R.raw.tnkearn_gold,
                R.raw.tnkbuy_item,
                R.raw.tnk1up,
                R.raw.tnkslide,
                R.raw.tnkfindgold,
                R.raw.tnkbomb,
                R.raw.tnkdropbomb,
                R.raw.tnkclick,
                R.raw.tnkclick2,
                R.raw.tnkclick3,
                R.raw.tnkconnect,

                R.raw.tnk_hve,
                R.raw.tnk_gamebkgnd,
                R.raw.tnk_fightscene1,
                R.raw.tnk_fightscene2,
                R.raw.tnk_fightscene3,
                R.raw.tnk_fightscene4,
                R.raw.tnk_fightscene5,
                R.raw.tnk_fightscene6,
                R.raw.tnk_fightscene7,

        };
        SoundManager.loadSounds(sounds);
    }
}