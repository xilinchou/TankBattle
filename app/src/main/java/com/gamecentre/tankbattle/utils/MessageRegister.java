package com.gamecentre.tankbattle.utils;

import android.view.MotionEvent;
import android.view.View;

import com.gamecentre.tankbattle.model.Game;
import com.gamecentre.tankbattle.tank.ServiceListener;

import java.util.ArrayList;

public class MessageRegister {

    private static final MessageRegister instance = new MessageRegister();
    private  RemoteMessageListener msgListener;
    private ButtonListener btnListener;
    private WifiDialogListener wdListener;
    private ArrayList<ServiceListener> srvListener = new ArrayList<>();
    private TransanctionListener transListener;

    public static MessageRegister getInstance() {
        return instance;
    }

    public void setMsgListener(RemoteMessageListener l) {
        msgListener = l;
    }

    public void setButtonListener(ButtonListener l) {btnListener = l;}

    public void setwifiDialogListener(WifiDialogListener l) {wdListener = l;}

    public void setServiceListener(ServiceListener l) {
        srvListener.add(l);
    }

    public void setTransListener(TransanctionListener l) {
        transListener = l;
    }



    public void registerNewMessage(Game message) {
        msgListener.onMessageReceived(message);
    }

    public void registerButtonAction(View v, MotionEvent m) {btnListener.onButtonPressed(v,m);}

    public void registerWifiDialog() {
        wdListener.onWifiDilogClosed();
    }

    public void registerServiceMessage(int games, long life_time, boolean h6) {
        for(ServiceListener l:srvListener) {
            l.onServiceMessageReceived(games, life_time, h6);
        }
    }

    public void registerTransactionListener() {
        transListener.onPurchaseSuccessful();
    }
}
