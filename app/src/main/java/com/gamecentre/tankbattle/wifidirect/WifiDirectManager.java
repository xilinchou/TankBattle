package com.gamecentre.tankbattle.wifidirect;

import android.Manifest;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.gamecentre.tankbattle.connection.ClientConnectionThread;
import com.gamecentre.tankbattle.connection.ClientSenderThread;
import com.gamecentre.tankbattle.connection.ServerConnectionThread;
import com.gamecentre.tankbattle.connection.ServerSenderThread;
import com.gamecentre.tankbattle.model.Game;
import com.gamecentre.tankbattle.sound.SoundManager;
import com.gamecentre.tankbattle.sound.Sounds;
import com.gamecentre.tankbattle.utils.ClientHandler;
import com.gamecentre.tankbattle.utils.ServerHandler;

import java.util.ArrayList;
import java.util.List;

public class WifiDirectManager implements WifiP2pManager.ConnectionInfoListener, WifiP2pManager.PeerListListener {

    private static final String TAG = "WifiDirectManager";
    private static final WifiDirectManager instance = new WifiDirectManager();
    private AsyncTask<Void, Void, Integer> task = null;
    private boolean isServer;
    private String hostAddress = null;

    AppCompatActivity activity;
    IntentFilter mIntentFilter = new IntentFilter();
    private WifiP2pManager wManager = null;
    private WifiP2pManager.Channel wChannel;
    WiFiDirectBroadcastReceiver bReceiver;

    private final List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    ArrayAdapter adapter;
    private List<String> peersName;
    private WifiP2pDevice connectedDevice = null;

    TextView conStateView = null;

    public static WifiDirectManager getInstance() {
        return instance;
    }

    public static ServerHandler serverHandler;
    public static ClientHandler clientHandler;
    public static ServerConnectionThread svConn;
    public static ClientConnectionThread clConn;
    public static ServerSenderThread svSender;
    public static ClientSenderThread clSender;

    public void initialize(AppCompatActivity activity) {
        this.activity = activity;
        wManager = (WifiP2pManager) activity.getSystemService(Context.WIFI_P2P_SERVICE);
        wChannel = wManager.initialize(activity, activity.getMainLooper(), null);
        bReceiver = new WiFiDirectBroadcastReceiver(wManager, wChannel, activity);

        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    public void connect(TextView conState) {
        this.conStateView = conState;
        connect(connectedDevice);
    }

    public void connect(WifiP2pDevice peer) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d("Connection", "Permission not granted");
            return;
        }

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = connectedDevice.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        config.groupOwnerIntent = 15;
//        connectedDevice = peer;

        wManager.connect(wChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                //success logic
                Log.d("Connection: ", "Connection successful");
            }

            @Override
            public void onFailure(int reason) {
                //failure logic
                Log.d("Connection: ", "Connection unsuccessful");
                if(conStateView != null) {
                    conStateView.setText("Connection Failed");
                }
            }
        });
    }

    public void discoverPeers() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d("Player Search", "Permission not granted");
            return;
        }
        wManager.discoverPeers(wChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d("Player Search", "Got players");
            }

            @Override
            public void onFailure(int reasonCode) {
                Log.d("Player Search", "Did not get any players");
            }
        });
    }

    public void registerBReceiver(){
        activity.registerReceiver(bReceiver, mIntentFilter);
    }

    public void unregisterBReceiver() {
        activity.unregisterReceiver(bReceiver);
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        // String from WifiP2pInfo struct
        String groupOwnerAddress = info.groupOwnerAddress.getHostAddress();

        // After the group negotiation, we can determine the group owner
        // (server).

        hostAddress = info.groupOwnerAddress.getHostAddress();

        Log.d("Group owner: ",hostAddress);
//        TextView conState = (TextView)activity.findViewById(R.id.connStatus);
        if(conStateView != null) {
            SoundManager.playSound(Sounds.TANK.CONNECT);
            conStateView.setText("Connected");
        }

        if (info.groupFormed && info.isGroupOwner) {
            // Do whatever tasks are specific to the group owner.
            // One common case is creating a group owner thread and accepting
            // incoming connections.
            serverHandler = new ServerHandler();
            if(ServerConnectionThread.socketListener != null) {
                ServerConnectionThread.socketListener.disconnect();
            }
            svConn = new ServerConnectionThread();
            svConn.start();
//            task = ConnectionManager.getInstance().new ServerAsyncTask(activity);
//            task.execute();
            setIsServer(true);
            Log.d("Group Info", "This is the group leader");

        } else if (info.groupFormed) {
            // The other device acts as the peer (client). In this case,
            // you'll want to create a peer thread that connects
            // to the group owner.
            clientHandler = new ClientHandler();
            if(ClientConnectionThread.clientListener != null) {
                ClientConnectionThread.clientListener.disconnect();
            }
            clConn = new ClientConnectionThread(connectedDevice.deviceName,hostAddress);
            clConn.start();
//            task = ConnectionManager.getInstance().new ClientAsyncTask(activity, hostAddress);
//            task.execute();
            setIsServer(false);
            Log.d("Group Info", "This is not the group leader");
        }
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
        Log.d("Device: ", "Devices available");
        peers.clear();
        peers.addAll(wifiP2pDeviceList.getDeviceList());
        peersName.clear();
        Log.d("Device: ", String.valueOf(wifiP2pDeviceList.getDeviceList().size()) + " devices available");
        for (WifiP2pDevice peer : peers) {
            Log.d("Device: ", peer.deviceName);
            peersName.add(peer.deviceName);
        }
        adapter.notifyDataSetChanged();
    }

    public void registerDeviceView(ArrayAdapter adapter, List<String>names) {
        this.adapter = adapter;
        this.peersName = names;
    }

    public void getHostAddress() {

    }

//    public void showDeviceWindow(AppCompatActivity activity) {
//        WifiDialog wd = new WifiDialog(activity);
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(wd.getWindow().getAttributes());
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
//        wd.show();
//        wd.getWindow().setAttributes(lp);
//    }

    public WifiP2pDevice getDevice(String peerName) {
        if (peers.size() != 0) {
            for (WifiP2pDevice peer : peers) {
                if (peer.deviceName.equals(peerName)) {
                    Log.d("Device: ", "Selected " + peer.deviceName);
                    connectedDevice = peer;
                    return connectedDevice;
                }
            }
        }
        return null;
    }

    public String getDeviceName() {
        if(connectedDevice != null) {
            return connectedDevice.deviceName;
        }
        return null;
    }

    public boolean isConnected() {
        switch (connectedDevice.status){
            case WifiP2pDevice.CONNECTED:
                Log.d("WIFI STATE: ","CONNECTED");
                break;
            case WifiP2pDevice.AVAILABLE:
                Log.d("WIFI STATE: ","AVAILABLE");
                break;
            case WifiP2pDevice.INVITED:
                Log.d("WIFI STATE: ","INVITED");
                break;
            case WifiP2pDevice.UNAVAILABLE:
                Log.d("WIFI STATE: ","UNAVAILABLE");
                break;
            case WifiP2pDevice.FAILED:
                Log.d("WIFI STATE: ","FAILED");
                break;
        }
        return connectedDevice != null && connectedDevice.status == WifiP2pDevice.CONNECTED;
    }

    public void cancelDisconnect() {
        /*
         * A cancel abort request by user. Disconnect i.e. removeGroup if
         * already connected. Else, request WifiP2pManager to abort the ongoing
         * request
         */
        if (wManager != null) {
            if (connectedDevice == null
                    || connectedDevice.status == WifiP2pDevice.CONNECTED) {
                disconnect();
            } else if (connectedDevice.status == WifiP2pDevice.AVAILABLE
                    || connectedDevice.status == WifiP2pDevice.INVITED) {
                wManager.cancelConnect(wChannel, new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onFailure(int reasonCode) {
                    }
                });
            }
        }
    }

    public void disconnect() {
        disconnectOnly();
//        this.onChannelDisconnected();
    }

    private void disconnectOnly() {
        wManager.removeGroup(wChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onFailure(int reasonCode) {
                Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);
            }

            @Override
            public void onSuccess() {
            }

        });
        terminateTask();
        this.connectedDevice = null;
    }

    private void terminateTask() {
        if(svSender != null) {
            svSender.disconnect();
        }
        if(clSender != null) {
            clSender.disconnect();
        }
    }

    private void setIsServer(boolean isServer) {
        this.isServer = isServer;
    }

    public boolean isServer() {
        return isServer;
    }

    public void sendMessage(Game gameObject) {
        if(isServer()) {
            if(ServerConnectionThread.serverStarted) {
                ServerHandler.sendToClient(gameObject);
            }
        }
        else {
//            ConnectionManager.getInstance().pushOutData(message);
            if(ClientConnectionThread.serverStarted) {
                ClientHandler.sendToServer(gameObject);
            }
        }

    }

    public void sendMessage(String message) {
        if(isServer()) {
            if(ServerConnectionThread.serverStarted) {
                ServerHandler.sendToClient(message);
            }
        }
        else {
//            ConnectionManager.getInstance().pushOutData(message);
            if(ClientConnectionThread.serverStarted) {
                ClientHandler.sendToServer(message);
            }
        }

    }
}
