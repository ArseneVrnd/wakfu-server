package com.wakfu.emulator.protocol.messages.server;

import com.wakfu.emulator.protocol.Message;
import io.netty.buffer.ByteBuf;
import java.nio.charset.StandardCharsets;

public class AuthTokenValidationResponseMessage extends Message {
    private boolean valid;
    private String errorMessage;

    public AuthTokenValidationResponseMessage() {
        super(104); // ID du message serveur = 104
    }

    public AuthTokenValidationResponseMessage(boolean valid, String errorMessage) {
        this();
        this.valid = valid;
        this.errorMessage = errorMessage != null ? errorMessage : "";
    }

    public boolean isValid() {
        return valid;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public void serialize(ByteBuf buffer) {
        buffer.writeBoolean(valid);

        byte[] messageBytes = errorMessage.getBytes(StandardCharsets.UTF_8);
        buffer.writeShort(messageBytes.length);
        buffer.writeBytes(messageBytes);
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        this.valid = buffer.readBoolean();

        int messageLength = buffer.readShort();
        byte[] messageBytes = new byte[messageLength];
        buffer.readBytes(messageBytes);
        this.errorMessage = new String(messageBytes, StandardCharsets.UTF_8);
    }
}