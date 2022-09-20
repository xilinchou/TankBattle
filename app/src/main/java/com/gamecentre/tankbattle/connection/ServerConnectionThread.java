package com.gamecentre.tankbattle.connection;

import android.util.Log;

import com.gamecentre.tankbattle.utils.CONST;
import com.gamecentre.tankbattle.wifidirect.WifiDirectManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerConnectionThread extends Thread{

    static final int SocketServerPORT = 8080;
    public static Socket socket = null;
    public static boolean serverStarted = false;
    public static ServerSocket serverSocket;
    public static boolean allPlayersJoined = false;
    public static ServerListenerThread socketListener;

    public ServerConnectionThread() {

    }

    @Override
    public void run() {
        if (serverSocket == null) {
            try {
                serverSocket = new ServerSocket(SocketServerPORT);
                socket = serverSocket.accept();
                Log.d("SERVER CONNECTION", "CONNECTED");
                socketListener = new ServerListenerThread(socket);
                socketListener.start();
                WifiDirectManager.svSender = new ServerSenderThread(socket, CONST.GAME_NAME);
                WifiDirectManager.svSender.start();
                serverStarted = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
