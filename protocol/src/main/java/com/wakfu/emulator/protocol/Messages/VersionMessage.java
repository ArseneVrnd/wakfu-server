// Fichier à créer dans: protocol/src/main/java/com/wakfu/emulator/protocol/messages/VersionMessage.java
package com.wakfu.emulator.protocol.messages;

import com.wakfu.emulator.protocol.Message;
import io.netty.buffer.ByteBuf;
import com.wakfu.emulator.common.Constants;
import com.wakfu.emulator.protocol.messages.VersionMessage;
import com.wakfu.emulator.protocol.messages.LoginRequestMessage;
import com.wakfu.emulator.protocol.messages.LoginResultMessage;

public class VersionMessage extends Message {
    private int protocolVersion;

    public VersionMessage() {
        super(1); // ID du message = 1
        this.protocolVersion = Constants.PROTOCOL_VERSION;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    @Override
    public void serialize(ByteBuf buffer) {
        buffer.writeInt(protocolVersion);
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        this.protocolVersion = buffer.readInt();
    }
}