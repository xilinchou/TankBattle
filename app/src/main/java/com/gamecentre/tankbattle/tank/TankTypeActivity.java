package com.gamecentre.tankbattle.tank;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.gamecentre.tankbattle.R;
import com.gamecentre.tankbattle.sound.SoundManager;
import com.gamecentre.tankbattle.sound.Sounds;
import com.gamecentre.tankbattle.utils.CONST;

@SuppressLint("ClickableViewAccessibility")
public class TankTypeActivity extends AppCompatActivity {
    TankTextView classic, arcade;
    static final String TANK_TYPE = "TANK_TYPE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tank_type);

        findViewById(R.id.classic).setOnClickListener(onClickListener);
        findViewById(R.id.classic_lo).setOnClickListener(onClickListener);
        findViewById(R.id.arcade).setOnClickListener(onClickListener);
        findViewById(R.id.arcade_lo).setOnClickListener(onClickListener);


        this.findViewById(R.id.settingsBtn)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SoundManager.playSound(Sounds.TANK.CLICK);
                        openSettings(view);
                    }
                });

        this.findViewById(R.id.exitBtn)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SoundManager.playSound(Sounds.TANK.CLICK);
                        finishAffinity();
                    }
                });


        TimerBroadcastService.settings = getSharedPreferences("TankSettings", 0);

        long game6h = TimerBroadcastService.settings.getLong(TankActivity.LIFE_TIME_6H,0);
        if(game6h == 0) {
            SharedPreferences.Editor editor = TimerBroadcastService.settings.edit();
            editor.putLong(TankActivity.LIFE_TIME_6H,game6h);
            editor.commit();
        }


        int games = TimerBroadcastService.settings.getInt(TankActivity.RETRY_COUNT, CONST.Tank.MAX_GAME_COUNT);
        if(games == CONST.Tank.MAX_GAME_COUNT) {
            SharedPreferences.Editor editor = TimerBroadcastService.settings.edit();
            editor.putInt(TankActivity.RETRY_COUNT,games);
            editor.commit();
        }


        long life_time = TimerBroadcastService.settings.getLong(TankActivity.LIFE_TIME,0);
        if(life_time == 0) {
            SharedPreferences.Editor editor = TimerBroadcastService.settings.edit();
            editor.putLong(TankActivity.LIFE_TIME,life_time);
            editor.commit();
        }
        else {
            long current_time = System.currentTimeMillis();
            long time_passed = current_time - life_time;
            int added_games = (int)(time_passed/(CONST.Tank.LIFE_DURATION_MINS*60000));
            games += added_games;
            if(games > CONST.Tank.MAX_GAME_COUNT) {
                games = CONST.Tank.MAX_GAME_COUNT;
            }
            SharedPreferences.Editor editor = TimerBroadcastService.settings.edit();
            editor.putInt(TankActivity.RETRY_COUNT,games);
            editor.putLong(TankActivity.LIFE_TIME, (long) added_games * CONST.Tank.LIFE_DURATION_MINS*60000 + life_time);
            editor.commit();
        }



        startService(new Intent(TankTypeActivity.this, TimerBroadcastService.class));

        loadSoundSettings();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i = new Intent(TankTypeActivity.this, TankMenuActivity.class);
            int id = view.getId();
            if (id == R.id.classic || id == R.id.classic_lo) {
                SoundManager.playSound(Sounds.TANK.CLICK);
                i.putExtra(TankTypeActivity.TANK_TYPE, "CLASSIC");
                startActivity(i);
                finish();
            }
            else if (id == R.id.arcade || id == R.id.arcade_lo) {
                //TODO
//                SoundManager.playSound(Sounds.TANK.CLICK);
//                i.putExtra(TankTypeActivity.TANK_TYPE, "CAMPAIGN");
//                startActivity(i);
//                finish();
            }
        }
    };

    public void openSettings(View view) {
        TankSettingsDialog cdd = new TankSettingsDialog(this);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(cdd.getWindow().getAttributes());
        cdd.show();
        cdd.getWindow().setAttributes(lp);
    }

    private void loadSoundSettings() {
        SharedPreferences settings = getSharedPreferences("TankSettings", 0);

        boolean sound = settings.getBoolean(TankMenuActivity.PREF_MUTED,true);
        boolean vibrate = settings.getBoolean(TankMenuActivity.PREF_VIBRATE,true);

        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(TankMenuActivity.PREF_MUTED, sound);
        editor.putBoolean(TankMenuActivity.PREF_VIBRATE,vibrate);
        editor.apply();

        SoundManager.enableSound(sound);
    }
}