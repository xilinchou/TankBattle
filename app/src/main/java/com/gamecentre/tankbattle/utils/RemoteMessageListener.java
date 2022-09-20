package com.gamecentre.tankbattle.utils;

import com.gamecentre.tankbattle.model.Game;

public interface RemoteMessageListener {
    void onMessageReceived(Game message);
}

