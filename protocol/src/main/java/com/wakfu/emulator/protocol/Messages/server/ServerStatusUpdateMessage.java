package com.wakfu.emulator.protocol.messages.server;

import com.wakfu.emulator.protocol.Message;
import io.netty.buffer.ByteBuf;

public class ServerStatusUpdateMessage extends Message {
    private int playerCount;
    private int status; // 0 = offline, 1 = online, 2 = maintenance

    public ServerStatusUpdateMessage() {
        super(102); // ID du message serveur = 102
    }

    public ServerStatusUpdateMessage(int playerCount, int status) {
        this();
        this.playerCount = playerCount;
        this.status = status;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public void serialize(ByteBuf buffer) {
        buffer.writeInt(playerCount);
        buffer.writeInt(status);
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        this.playerCount = buffer.readInt();
        this.status = buffer.readInt();
    }
}