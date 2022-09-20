package com.gamecentre.tankbattle.connection;

import android.os.Bundle;
import android.os.Message;

import com.gamecentre.tankbattle.model.Game;
import com.gamecentre.tankbattle.utils.CONST;
import com.gamecentre.tankbattle.utils.MessageRegister;
import com.gamecentre.tankbattle.utils.PlayerInfo;
import com.gamecentre.tankbattle.wifidirect.WifiDirectManager;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ServerListenerThread extends Thread{

    private Socket hostThreadSocket;
    private boolean RUN = true;
    ObjectInputStream ois;
    InputStream is = null;
    BufferedInputStream bis;

    ServerListenerThread(Socket soc) {
        hostThreadSocket = soc;
    }

    @Override
    public void run() {
        try{
            is = hostThreadSocket.getInputStream();
            bis = new BufferedInputStream(is);
            ois = new ObjectInputStream(is);
        }catch (IOException e) {
            e.printStackTrace();
        }
        while (RUN) {

            try {
                Object gameObject;
                Bundle data = new Bundle();
                gameObject = ois.readObject();
                if (gameObject != null) {
                    if (gameObject instanceof PlayerInfo) {
//                        data.putSerializable(CONST.PLAYER_INFO, (PlayerInfo) gameObject);
//                        Log.d("SERVER LISTENER", "GOT PLAYER");
//                        data.putInt(Constants.ACTION_KEY, CONST.PLAYER_INFO.PLAYER_LIST_UPDATE);
//                        ServerConnectionThread.socketUserMap.put(hostThreadSocket, ((PlayerInfo) gameObject).username);
                    } else {
                        data.putSerializable(CONST.GAME_DATA_KEY, (Game) gameObject);
                        MessageRegister.getInstance().registerNewMessage((Game)gameObject);
//                        Log.d("SERVER LISTENER", "GOT GAME");
                    }
                    Message msg = new Message();
                    msg.setData(data);
                    WifiDirectManager.serverHandler.sendMessage(msg);
                }

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void disconnect() {
        RUN = false;
        if(hostThreadSocket != null) {
            try {
                hostThreadSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
