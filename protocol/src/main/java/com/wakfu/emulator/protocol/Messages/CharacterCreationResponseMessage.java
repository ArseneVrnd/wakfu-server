package com.wakfu.emulator.protocol.messages;

import com.wakfu.emulator.protocol.Message;
import io.netty.buffer.ByteBuf;
import java.nio.charset.StandardCharsets;

public class CharacterCreationResponseMessage extends Message {
    private boolean success;
    private String errorMessage;
    private int characterId;

    public CharacterCreationResponseMessage() {
        super(8); // ID du message = 8
    }

    public CharacterCreationResponseMessage(boolean success, String errorMessage, int characterId) {
        this();
        this.success = success;
        this.errorMessage = errorMessage != null ? errorMessage : "";
        this.characterId = characterId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getCharacterId() {
        return characterId;
    }

    @Override
    public void serialize(ByteBuf buffer) {
        buffer.writeBoolean(success);

        byte[] errorBytes = errorMessage.getBytes(StandardCharsets.UTF_8);
        buffer.writeShort(errorBytes.length);
        buffer.writeBytes(errorBytes);

        buffer.writeInt(characterId);
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        this.success = buffer.readBoolean();

        int errorLength = buffer.readShort();
        byte[] errorBytes = new byte[errorLength];
        buffer.readBytes(errorBytes);
        this.errorMessage = new String(errorBytes, StandardCharsets.UTF_8);

        this.characterId = buffer.readInt();
    }
}