package com.gamecentre.tankbattle.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.gamecentre.tankbattle.R;
import com.gamecentre.tankbattle.tank.TankIntroActivity;

public class MainActivity extends AppCompatActivity {

    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        logo = (ImageView) findViewById(R.id.logo);

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
//        startActivity(new Intent(MainActivity.this, MenuActivity.class));
        startActivity(new Intent(MainActivity.this, TankIntroActivity.class));
        finish();
    }
}