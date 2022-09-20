package com.gamecentre.tankbattle.tank;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;

import com.gamecentre.tankbattle.R;

public class TankPlayersDialog extends Dialog implements View.OnClickListener{
    CheckBox soundCheck;
    CheckBox vibrateCheck;

    public AppCompatActivity activity;
    public Dialog d;
    public Button yes, no;
    SharedPreferences settings;

    public TankPlayersDialog(AppCompatActivity a) {
        super(a);
        this.activity = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        setContentView(R.layout.activity_tank_players);
        setCancelable(false);

        yes = (Button) findViewById(R.id.saveBtn);
        no = (Button) findViewById(R.id.cancelBtn);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);

        settings = activity.getSharedPreferences("TankSettings", 0);
        loadStoredSettings(settings);

        soundCheck = (CheckBox) findViewById(R.id.enableSound);
        vibrateCheck = (CheckBox) findViewById(R.id.enableVibrate);
    }

    void loadStoredSettings(SharedPreferences settings) {

        boolean sound = settings.getBoolean(TankMenuActivity.PREF_MUTED,true);
        CheckBox soundCheck = (CheckBox) findViewById(R.id.enableSound);
        soundCheck.setChecked(sound);

        boolean vibrate = settings.getBoolean(TankMenuActivity.PREF_VIBRATE,true);
        CheckBox vibrateCheck = (CheckBox) findViewById(R.id.enableVibrate);
        vibrateCheck.setChecked(vibrate);

        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(TankMenuActivity.PREF_MUTED, sound);
        editor.putBoolean(TankMenuActivity.PREF_VIBRATE,vibrate);
        editor.apply();

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.saveBtn) {
            SharedPreferences.Editor editor = settings.edit();

            if (soundCheck.isChecked()) {
                editor.putBoolean(TankMenuActivity.PREF_MUTED, true);
            } else {
                editor.putBoolean(TankMenuActivity.PREF_MUTED, false);
            }


            if (vibrateCheck.isChecked()) {
                editor.putBoolean(TankMenuActivity.PREF_VIBRATE, true);
            } else {
                editor.putBoolean(TankMenuActivity.PREF_VIBRATE, false);
            }
            editor.apply();
            dismiss();
        }
        else if (id == R.id.cancelBtn) {
            dismiss();
        }
    }
}
