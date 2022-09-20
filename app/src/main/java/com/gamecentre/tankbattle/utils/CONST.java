package com.gamecentre.tankbattle.utils;

public class CONST {
    public static class Direction {
        public static final int UP = 0;
        public static final int RIGHT = 1;
        public static final int DOWN = 2;
        public static final int LEFT = 3;
    }

    public static class Tank {
        public static final int MAX_GAME_COUNT = 5;
        public static final int LIFE_DURATION_MINS = 30;
        public static final long LIFE_DURATION_6HRS = 6*60*60*1000;
    }

    public static String GAME_DATA_KEY = "GAME_DATA";
    public static String GAME_NAME = "TANK";
    public static String PLAYER_INFO = "PLAYER_INFO";
    public static String STRING_INFO = "STRING_INFO";
}
