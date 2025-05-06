package com.wakfu.emulator.protocol.messages;

import com.wakfu.emulator.protocol.Message;
import io.netty.buffer.ByteBuf;

public class CharacterListRequestMessage extends Message {

    public CharacterListRequestMessage() {
        super(5); // ID du message = 5
    }

    @Override
    public void serialize(ByteBuf buffer) {
        // Pas de données supplémentaires à envoyer
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        // Pas de données supplémentaires à lire
    }
}