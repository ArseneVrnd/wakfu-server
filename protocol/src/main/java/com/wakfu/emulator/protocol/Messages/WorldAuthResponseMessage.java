package com.wakfu.emulator.protocol.messages;

import com.wakfu.emulator.protocol.Message;
import io.netty.buffer.ByteBuf;
import java.nio.charset.StandardCharsets;

public class WorldAuthResponseMessage extends Message {
    private boolean success;
    private String errorMessage;

    public WorldAuthResponseMessage() {
        super(12); // ID du message = 12
    }

    public WorldAuthResponseMessage(boolean success, String errorMessage) {
        this();
        this.success = success;
        this.errorMessage = errorMessage != null ? errorMessage : "";
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public void serialize(ByteBuf buffer) {
        buffer.writeBoolean(success);

        byte[] errorBytes = errorMessage.getBytes(StandardCharsets.UTF_8);
        buffer.writeShort(errorBytes.length);
        buffer.writeBytes(errorBytes);
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        this.success = buffer.readBoolean();

        int errorLength = buffer.readShort();
        byte[] errorBytes = new byte[errorLength];
        buffer.readBytes(errorBytes);
        this.errorMessage = new String(errorBytes, StandardCharsets.UTF_8);
    }
}