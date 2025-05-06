package com.wakfu.emulator.protocol.messages.server;

import com.wakfu.emulator.protocol.Message;
import io.netty.buffer.ByteBuf;
import java.nio.charset.StandardCharsets;

public class ServerRegistrationMessage extends Message {
    private int serverId;
    private String serverName;
    private String secretKey;

    public ServerRegistrationMessage() {
        super(100); // ID du message serveur = 100
    }

    public ServerRegistrationMessage(int serverId, String serverName, String secretKey) {
        this();
        this.serverId = serverId;
        this.serverName = serverName;
        this.secretKey = secretKey;
    }

    public int getServerId() {
        return serverId;
    }

    public String getServerName() {
        return serverName;
    }

    public String getSecretKey() {
        return secretKey;
    }

    @Override
    public void serialize(ByteBuf buffer) {
        buffer.writeInt(serverId);

        byte[] nameBytes = serverName.getBytes(StandardCharsets.UTF_8);
        buffer.writeShort(nameBytes.length);
        buffer.writeBytes(nameBytes);

        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        buffer.writeShort(keyBytes.length);
        buffer.writeBytes(keyBytes);
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        this.serverId = buffer.readInt();

        int nameLength = buffer.readShort();
        byte[] nameBytes = new byte[nameLength];
        buffer.readBytes(nameBytes);
        this.serverName = new String(nameBytes, StandardCharsets.UTF_8);

        int keyLength = buffer.readShort();
        byte[] keyBytes = new byte[keyLength];
        buffer.readBytes(keyBytes);
        this.secretKey = new String(keyBytes, StandardCharsets.UTF_8);
    }
}