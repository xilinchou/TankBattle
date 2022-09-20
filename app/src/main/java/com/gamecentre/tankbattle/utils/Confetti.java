package com.gamecentre.tankbattle.utils;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.gamecentre.tankbattle.R;
import com.plattysoft.leonids.ParticleSystem;

public class Confetti {

    private boolean running;

    public ParticleSystem[] confetti;
    AppCompatActivity activity;


    public Confetti(AppCompatActivity activity) {
        this.activity = activity;
        ParticleSystem pink_confetti = new ParticleSystem(activity, 80, R.drawable.confeti2, 10000);
        pink_confetti.setSpeedModuleAndAngleRange(0f, 0.3f, 180, 180)
            .setRotationSpeed(144)
            .setAcceleration(0.00005f, 90);

        ParticleSystem white_confetti = new ParticleSystem(activity, 80, R.drawable.confeti3, 10000);
        white_confetti.setSpeedModuleAndAngleRange(0f, 0.3f, 0, 0)
                .setRotationSpeed(144)
                .setAcceleration(0.00005f, 90);

        ParticleSystem pink_star = new ParticleSystem(activity, 100, R.drawable.star_pink, 10000);
        pink_star.setSpeedModuleAndAngleRange(0f, 0.5f, 20, 180)
                .setRotationSpeed(144)
                .setAcceleration(0.00008f, 60);

        ParticleSystem white_star = new ParticleSystem(activity, 100, R.drawable.star_white, 10000);
        white_star.setSpeedModuleAndAngleRange(0f, 0.5f, 0, 180)
                .setRotationSpeed(144)
                .setAcceleration(0.00008f, 60);

        confetti = new ParticleSystem[4];
        confetti[0] = pink_confetti;
        confetti[1] = white_confetti;
        confetti[2] = pink_star;
        confetti[3] = white_star;
    }

    public void generate_confetti(View[] view) {
        if (running){
            return;
        }
        for(int i = 0; i < confetti.length; i++) {
            confetti[i].emit(view[i],15);
        }
        running = true;
    }

    public void stop_confetti() {
        for(int i = 0; i < confetti.length; i++) {
            confetti[i].stopEmitting();
        }
    }

    public void cancel_confetti() {
        if (!running){
            return;
        }
        for(int i = 0; i < confetti.length; i++) {
            confetti[i].cancel();
        }
        running = false;
    }
}
