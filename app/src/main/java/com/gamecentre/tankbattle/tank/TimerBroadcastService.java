package com.gamecentre.tankbattle.tank;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.gamecentre.tankbattle.utils.CONST;
import com.gamecentre.tankbattle.utils.MessageRegister;

public class TimerBroadcastService extends Service {

    private final static String TAG = "BroadcastService";

    long time_left, life_time;
    int games;
    public static SharedPreferences settings;

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {

            long currentTime = System.currentTimeMillis();

            long game6h = settings.getLong(TankActivity.LIFE_TIME_6H,0);
            if(game6h > 0 && game6h > currentTime) { //6 hours has not elasped
                life_time = game6h - currentTime;
                games = CONST.Tank.MAX_GAME_COUNT;
            }
            else {
                life_time = settings.getLong(TankActivity.LIFE_TIME, 0); // time to next lift when not in 6h mode
                games = settings.getInt(TankActivity.RETRY_COUNT, CONST.Tank.MAX_GAME_COUNT);
            }


            // Not in 6h mode and games should be less that max_game_count
            if(life_time != 0 && games < CONST.Tank.MAX_GAME_COUNT) {
                currentTime = System.currentTimeMillis();
                time_left = (CONST.Tank.LIFE_DURATION_MINS * 60000) - ((currentTime - life_time) % (CONST.Tank.LIFE_DURATION_MINS * 60000));

                if (time_left < 1000) {
                    games++;
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putInt(TankActivity.RETRY_COUNT, games);
                    editor.putLong(TankActivity.LIFE_TIME, System.currentTimeMillis());
                    editor.commit();
                }
//                Log.d("SERVICE","sending message");
                Log.d("SERVICE","alive false " + games + " " + life_time);
                MessageRegister.getInstance().registerServiceMessage(games,time_left,false);
            }
            else {
                if(game6h > currentTime) {
                    Log.d("SERVICE","alive true " + games + " " + life_time);
                    MessageRegister.getInstance().registerServiceMessage(games, life_time, true);
                }
                else {
                    Log.d("SERVICE","alive false" + games + " " + life_time);
                    MessageRegister.getInstance().registerServiceMessage(games,time_left,false);
                }
            }

            timerHandler.postDelayed(this, 1000);
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "Starting timer...");

        timerHandler.postDelayed(timerRunnable, 0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}
