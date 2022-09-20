package com.gamecentre.tankbattle.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.gamecentre.tankbattle.model.Game;
import com.gamecentre.tankbattle.wifidirect.WifiDirectManager;

public class ClientHandler extends Handler {

    Bundle messageData;

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        messageData = msg.getData();
        Object clientObject = messageData.getSerializable(CONST.GAME_DATA_KEY);
        if (clientObject instanceof Game) {
            MessageRegister.getInstance().registerNewMessage((Game)clientObject);
        }
    }

    public static void sendToServer(Object gameObject) {
        WifiDirectManager.clSender.sendMessage(gameObject);
    }
}
