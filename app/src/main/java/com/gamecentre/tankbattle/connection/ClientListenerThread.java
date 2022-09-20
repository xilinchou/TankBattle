package com.gamecentre.tankbattle.connection;

import android.os.Bundle;
import android.os.Message;

import com.gamecentre.tankbattle.model.Game;
import com.gamecentre.tankbattle.utils.CONST;
import com.gamecentre.tankbattle.wifidirect.WifiDirectManager;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ClientListenerThread extends Thread{

    Socket socket;
    private boolean RUN = true;
    ObjectInputStream ois;
    InputStream is = null;
    BufferedInputStream bis;

    ClientListenerThread(Socket soc) {
        socket = soc;
    }

    @Override
    public void run() {
        try {
            is = socket.getInputStream();
            bis = new BufferedInputStream(is);
            ois = new ObjectInputStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (RUN) {
            try {

                Bundle data = new Bundle();
                Object serverObject = (Object) ois.readObject();
                if (serverObject != null) {
                    if (serverObject instanceof String) {
//                        data.putSerializable(CONST.STRING_INFO, (String) serverObject);
                        //                        Log.d("CLIENT LISTENER", "GOT STRING");
                    } else if (serverObject instanceof Game) {
                        data.putSerializable(CONST.GAME_DATA_KEY, (Game) serverObject);
//                        MessageRegister.getInstance().registerNewMessage((Game)serverObject);
//                        Log.d("CLIENT LISTENER", "GOT GAME");
                    }
                    Message msg = new Message();
                    msg.setData(data);
                    WifiDirectManager.clientHandler.sendMessage(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void disconnect() {
        RUN = false;
        if(socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
