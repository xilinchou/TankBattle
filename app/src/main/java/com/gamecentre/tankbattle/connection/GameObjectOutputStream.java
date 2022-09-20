package com.gamecentre.tankbattle.connection;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class GameObjectOutputStream extends ObjectOutputStream {
    // Constructor of this class
    // 1. Default
    GameObjectOutputStream() throws IOException
    {

        // Super keyword refers to parent class instance
        super();
    }

    // Constructor of this class
    // 1. Parameterized constructor
    GameObjectOutputStream(OutputStream o) throws IOException
    {
        super(o);
    }

    // Method of this class
    public void writeStreamHeader() throws IOException
    {
        return;
    }
}
