package com.gamecentre.tankbattle.tank;

public interface ServiceListener {
    void onServiceMessageReceived(int games, long time_left, boolean h6);
}
