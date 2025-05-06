package com.wakfu.emulator.protocol.messages;

import com.wakfu.emulator.protocol.Message;
import io.netty.buffer.ByteBuf;
import java.nio.charset.StandardCharsets;

public class WorldAuthRequestMessage extends Message {
    private String authToken;

    public WorldAuthRequestMessage() {
        super(11); // ID du message = 11
    }

    public String getAuthToken() {
        return authToken;
    }

    @Override
    public void serialize(ByteBuf buffer) {
        byte[] tokenBytes = authToken.getBytes(StandardCharsets.UTF_8);
        buffer.writeShort(tokenBytes.length);
        buffer.writeBytes(tokenBytes);
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        int tokenLength = buffer.readShort();
        byte[] tokenBytes = new byte[tokenLength];
        buffer.readBytes(tokenBytes);
        this.authToken = new String(tokenBytes, StandardCharsets.UTF_8);
    }
}