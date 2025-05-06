package com.wakfu.emulator.protocol.messages.server;

import com.wakfu.emulator.protocol.Message;
import io.netty.buffer.ByteBuf;
import java.nio.charset.StandardCharsets;

public class ServerRegistrationResponseMessage extends Message {
    private boolean accepted;
    private String message;

    public ServerRegistrationResponseMessage() {
        super(101); // ID du message serveur = 101
    }

    public ServerRegistrationResponseMessage(boolean accepted, String message) {
        this();
        this.accepted = accepted;
        this.message = message != null ? message : "";
    }

    public boolean isAccepted() {
        return accepted;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public void serialize(ByteBuf buffer) {
        buffer.writeBoolean(accepted);

        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        buffer.writeShort(messageBytes.length);
        buffer.writeBytes(messageBytes);
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        this.accepted = buffer.readBoolean();

        int messageLength = buffer.readShort();
        byte[] messageBytes = new byte[messageLength];
        buffer.readBytes(messageBytes);
        this.message = new String(messageBytes, StandardCharsets.UTF_8);
    }
}