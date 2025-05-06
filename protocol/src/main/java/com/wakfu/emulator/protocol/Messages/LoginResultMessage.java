// Fichier à créer dans: protocol/src/main/java/com/wakfu/emulator/protocol/messages/LoginResultMessage.java
package com.wakfu.emulator.protocol.messages;

import com.wakfu.emulator.protocol.Message;
import io.netty.buffer.ByteBuf;
import com.wakfu.emulator.protocol.messages.VersionMessage;
import com.wakfu.emulator.protocol.messages.LoginRequestMessage;
import com.wakfu.emulator.protocol.messages.LoginResultMessage;

public class LoginResultMessage extends Message {
    private boolean success;
    private String errorMessage;

    public LoginResultMessage() {
        super(3); // ID du message = 3
    }

    public LoginResultMessage(boolean success, String errorMessage) {
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

        byte[] errorBytes = errorMessage.getBytes();
        buffer.writeShort(errorBytes.length);
        buffer.writeBytes(errorBytes);
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        this.success = buffer.readBoolean();

        int errorLength = buffer.readShort();
        byte[] errorBytes = new byte[errorLength];
        buffer.readBytes(errorBytes);
        this.errorMessage = new String(errorBytes);
    }
}