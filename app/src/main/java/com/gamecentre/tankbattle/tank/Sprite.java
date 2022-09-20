package com.gamecentre.tankbattle.tank;

public class Sprite {
    public int x;
    public int y;
    public int w;
    public int h;
    public int frame_count;
    public boolean loop;
    public int frame_time;

    public Sprite(int x, int y, int w, int h, int fc, int fd, boolean l) {
        this.x = (int)(x*TankView.RESIZE);
        this.y = (int)(y*TankView.RESIZE);
        this.w = (int)(w*TankView.RESIZE);
        this.h = (int)(h*TankView.RESIZE);
        this.frame_count = fc;
        frame_time = fd;
        this.loop = l;
    }
}
