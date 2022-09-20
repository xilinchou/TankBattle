package com.gamecentre.tankbattle.utils;

public class MessageParser {

//    private Rect paddle;
//    private float[] ball;
//
//    private static final MessageParser instance = new MessageParser();
//
//    public static MessageParser getInstance() {
//        return instance;
//    }
//
//    public void parseMessage(String message) {
//        Gson gson = new Gson();
//        PongGameMsg pms = gson.fromJson(message, PongGameMsg.class);
//        Log.d("Message Received: ",pms.toString());
//        paddle = pms.getPaddle();
//        ball = pms.getBall();
//    }
//
//    public String getMessage(float[] ball, Rect paddle) {
//        String message;
//        Gson gson = new Gson();
//        PongGameMsg pms = new PongGameMsg();
//        pms.setBall(ball);
//        pms.setPaddle(paddle);
//
//        message = gson.toJson(pms);
//        Log.d("Message to Send: ",message);
//        return message;
//    }
//
//    public String getMessage(int bl, int bt, int br, int pl, int pt, int pr, int pb) {
//        String message;
//        Gson gson = new Gson();
//        PongGameMsg pms = new PongGameMsg(bl, bt, br, pl, pt, pr, pb);
//
//        message = gson.toJson(pms);
//        Log.d("Message to Send: ",message);
//        return message;
//    }
//
//    public Rect getPaddle() {
//        return paddle;
//    }
//
//    public float[] getBall() {
//        return ball;
//    }
}
