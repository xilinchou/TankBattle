package com.gamecentre.tankbattle.connection;

import android.util.Log;

import com.gamecentre.tankbattle.utils.PlayerInfo;
import com.gamecentre.tankbattle.wifidirect.WifiDirectManager;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientConnectionThread extends Thread{

    public static Socket socket;
    String dstAddress;
    int dstPort = 8080;
    public static boolean serverStarted = false;
    String userName;
    public static ClientListenerThread clientListener;

    public ClientConnectionThread(String userName, String dstAddress) {
        this.userName = userName;
        this.dstAddress = dstAddress;
    }

    public ClientConnectionThread(String dstAddress) {
        this.userName = null;
        this.dstAddress = dstAddress;
    }

    @Override
    public void run() {
        if (socket == null) {
            try {
                if (dstAddress != null) {
                    socket = new Socket(dstAddress, dstPort);
                    if (socket.isConnected()) {
                        Log.d("CLIENT CONNECTION", "CONNECTED");
                        clientListener = new ClientListenerThread(socket);
                        clientListener.start();
                        PlayerInfo playerInfo = new PlayerInfo(userName);
                        WifiDirectManager.clSender = new ClientSenderThread(socket, playerInfo);
                        WifiDirectManager.clSender.start();
                        serverStarted = true;
                    }
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
