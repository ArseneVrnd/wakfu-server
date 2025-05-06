package com.wakfu.emulator.protocol.messages;

import com.wakfu.emulator.protocol.Message;
import com.wakfu.emulator.protocol.model.WorldServerInfo;
import io.netty.buffer.ByteBuf;
import java.nio.charset.StandardCharsets;

public class WorldServerListMessage extends Message {
    private WorldServerInfo server;

    public WorldServerListMessage() {
        super(4); // ID du message = 4
    }

    public WorldServerListMessage(WorldServerInfo server) {
        this();
        this.server = server;
    }

    public WorldServerInfo getServer() {
        return server;
    }

    @Override
    public void serialize(ByteBuf buffer) {
        // Écrire les informations du serveur
        buffer.writeInt(server.getId());

        byte[] nameBytes = server.getName().getBytes(StandardCharsets.UTF_8);
        buffer.writeShort(nameBytes.length);
        buffer.writeBytes(nameBytes);

        byte[] addressBytes = server.getAddress().getBytes(StandardCharsets.UTF_8);
        buffer.writeShort(addressBytes.length);
        buffer.writeBytes(addressBytes);

        buffer.writeInt(server.getPort());
        buffer.writeInt(server.getPlayerCount());
        buffer.writeInt(server.getStatus());
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        // Lire les informations du serveur
        int id = buffer.readInt();

        int nameLength = buffer.readShort();
        byte[] nameBytes = new byte[nameLength];
        buffer.readBytes(nameBytes);
        String name = new String(nameBytes, StandardCharsets.UTF_8);

        int addressLength = buffer.readShort();
        byte[] addressBytes = new byte[addressLength];
        buffer.readBytes(addressBytes);
        String address = new String(addressBytes, StandardCharsets.UTF_8);

        int port = buffer.readInt();
        int playerCount = buffer.readInt();
        int status = buffer.readInt();

        this.server = new WorldServerInfo(id, name, address, port, playerCount, status);
    }
}