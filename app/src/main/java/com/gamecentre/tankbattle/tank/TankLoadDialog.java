package com.gamecentre.tankbattle.tank;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.gamecentre.tankbattle.R;
import com.gamecentre.tankbattle.sound.SoundManager;
import com.gamecentre.tankbattle.sound.Sounds;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class TankLoadDialog extends Dialog implements View.OnTouchListener{

    public AppCompatActivity activity;
    public Dialog d;
    public Button loadName, delName, close;
    SharedPreferences settings;
    ArrayList<String> savedNames;
    char[][] stage;
    ListView stageListView;
    ArrayAdapter adapter;
    String stageName;
    ConstructionFragment fragment;

    public TankLoadDialog(AppCompatActivity a, ConstructionFragment f) {
        super(a);
        this.activity = a;
        this.fragment = f;
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

        setContentView(R.layout.activity_tank_load);
        setCancelable(false);

        settings = activity.getSharedPreferences("TankSettings", 0);
        savedNames = loadStageNames();

        loadName = (Button) findViewById(R.id.loadName);
        loadName.setOnTouchListener(this);

        delName = (Button) findViewById(R.id.delName);
        delName.setOnTouchListener(this);

        close = (Button) findViewById(R.id.closeLoad);
        close.setOnTouchListener(this);

        stageListView = findViewById(R.id.stageList);

        adapter = new ArrayAdapter<String>(stageListView.getContext(),
                R.layout.save_list_view, savedNames);
        stageListView.setAdapter(adapter);

        stageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                stageName = (String) ((TextView) view).getText();
                int count = adapterView.getCount();
                for(int c = 0; c < count; c++) {
                    ((TextView) adapterView.getChildAt(c)).setBackground(null);
                }
                view.setBackgroundColor(Color.LTGRAY);
            }
        });



    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent m) {
        int id = v.getId();
        if (id == R.id.loadName) {
            if(stageName != null) {
                SoundManager.playSound(Sounds.TANK.CLICK);
                stage = loadStage(stageName);
                fragment.updateStage(stage);
                dismiss();
            }
            else {
                SoundManager.playSound(Sounds.TANK.CLICK2);
            }
        }

        if (id == R.id.delName) {
            if(stageName != null) {
                SoundManager.playSound(Sounds.TANK.CLICK);
                deleteStage(stageName);
            }
            else {
                SoundManager.playSound(Sounds.TANK.CLICK2);
            }
        }

        if (id == R.id.closeLoad) {
            SoundManager.playSound(Sounds.TANK.CLICK);
            dismiss();
        }
        return true;
    }

    private char[][] loadStage(String name) {
        String stage = settings.getString(name,null);
        if(stage == null) {
            return new char[26][26];
        }
        Type type = new TypeToken<char[][]>() {}.getType();
        Gson gson = new Gson();
        return gson.fromJson(stage,type);
    }

    private ArrayList<String> loadStageNames() {
        String stageNames = settings.getString(TankMenuActivity.STAGE_NAMES,null);
        if(stageNames == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        Gson gson = new Gson();
        return gson.fromJson(stageNames,type);
    }

    private void deleteStage(String name) {
        SharedPreferences.Editor editor = settings.edit();
        if(savedNames.contains(name)) {
            savedNames.remove(name);
            editor.remove(name);
            editor.commit();
            saveStageNames(savedNames);
            adapter.notifyDataSetChanged();
        }
    }

    private void saveStageNames(ArrayList<String> stageNames) {
        SharedPreferences.Editor editor = settings.edit();
        Gson gson = new Gson();
        String json = gson.toJson(stageNames);
        editor.putString(TankMenuActivity.STAGE_NAMES,json);
        editor.apply();
    }
}
