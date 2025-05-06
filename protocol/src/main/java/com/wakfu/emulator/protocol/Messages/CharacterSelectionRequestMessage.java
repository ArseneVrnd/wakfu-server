package com.wakfu.emulator.protocol.messages;

import com.wakfu.emulator.protocol.Message;
import io.netty.buffer.ByteBuf;

public class CharacterSelectionRequestMessage extends Message {
    private int characterId;

    public CharacterSelectionRequestMessage() {
        super(9); // ID du message = 9
    }

    public int getCharacterId() {
        return characterId;
    }

    @Override
    public void serialize(ByteBuf buffer) {
        buffer.writeInt(characterId);
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        this.characterId = buffer.readInt();
    }
}