package com.gamecentre.tankbattle.tank;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.gamecentre.tankbattle.R;
import com.gamecentre.tankbattle.sound.SoundManager;
import com.gamecentre.tankbattle.sound.Sounds;
import com.google.gson.Gson;

import java.util.ArrayList;

public class TankSaveDialog extends Dialog implements View.OnClickListener{

    public AppCompatActivity activity;
    public Dialog d;
    public Button saveButton, no;
    SharedPreferences settings;
    ArrayList<String> savedNames;
    char[][] stage;
    EditText saveTextField;
    TextView saveNotify;

    public TankSaveDialog(AppCompatActivity a, ArrayList<String> savedNames, char[][] stage) {
        super(a);
        this.activity = a;
        this.savedNames = savedNames;
        this.stage = stage;
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

        setContentView(R.layout.activity_tank_save);
        setCancelable(false);

        saveButton = (Button) findViewById(R.id.saveBtn);
        no = (Button) findViewById(R.id.cancelBtn);
        saveButton.setOnClickListener(this);
        no.setOnClickListener(this);

        saveTextField = findViewById(R.id.saveName);

        saveTextField.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    saveButton.setText("SAVE");
                }
                return false;
            }
        });

        saveNotify = findViewById(R.id.saveNotify);
        settings = activity.getSharedPreferences("TankSettings", 0);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.saveBtn) {

            String saveName = saveTextField.getText().toString();
            if(saveName.equals("")) {
                SoundManager.playSound(Sounds.TANK.CLICK2);
                saveNotify.setText("Name cannot be empty!");
                return;
            }

            if(savedNames.contains(saveName) && saveButton.getText().toString().equals("OVERWRITE")) {
//                saveStageNames(savedNames);
                SoundManager.playSound(Sounds.TANK.CLICK);
                Log.d("SAVING", "Agreed to overwrite");
                saveStage(saveName,stage);
                dismiss();
            }

            else if(savedNames.contains(saveName)) {
                SoundManager.playSound(Sounds.TANK.CLICK);
                saveNotify.setText("Name already exists");
                saveButton.setText("OVERWRITE");
            }

            else if(!savedNames.contains(saveName)) {
                SoundManager.playSound(Sounds.TANK.CLICK);
                savedNames.add(saveName);
                saveStageNames(savedNames);
                saveStage(saveName,stage);
                dismiss();
            }



        }
        else if (id == R.id.cancelBtn) {
            SoundManager.playSound(Sounds.TANK.CLICK);
            saveButton.setText("SAVE");
            dismiss();
        }
    }

    private void saveStage(String name, char[][] stage) {
        SharedPreferences.Editor editor = settings.edit();
        Gson gson = new Gson();
        String json = gson.toJson(stage);
        editor.putString(name,json);
        editor.apply();
    }

    private void saveStageNames(ArrayList<String> stageNames) {
        SharedPreferences.Editor editor = settings.edit();
        Gson gson = new Gson();
        String json = gson.toJson(stageNames);
        editor.putString(TankMenuActivity.STAGE_NAMES,json);
        editor.apply();
    }
}
