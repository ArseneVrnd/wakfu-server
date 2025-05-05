package com.wakfu.emulator.protocol;

import io.netty.buffer.ByteBuf;

public abstract class Message {
    private final int id;
    
    public Message(int id) {
        this.id = id;
    }
    
    public int getId() {
        return id;
    }
    
    public abstract void serialize(ByteBuf buffer);
    
    public abstract void deserialize(ByteBuf buffer);
}