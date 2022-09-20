package com.gamecentre.tankbattle.tank;

import java.util.HashMap;
import java.util.Map;

public class SpriteObjects {
    private Map<ObjectType,Sprite> tankObjects = new HashMap<>();

    private static SpriteObjects instance = new SpriteObjects();

    public static SpriteObjects getInstance() {
        return instance;
    }

    public void insert(ObjectType type, int x, int y, int w, int h, int fc, int fd, boolean l) {
        Sprite data = new Sprite(x,y,w,h,fc,fd,l);
        tankObjects.put(type,data);
    }

    public Sprite getData(ObjectType type) {
        return tankObjects.get(type);
    }
}
