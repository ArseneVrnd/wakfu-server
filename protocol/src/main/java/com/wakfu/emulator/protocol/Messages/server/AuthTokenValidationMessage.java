package com.wakfu.emulator.protocol.messages.server;

import com.wakfu.emulator.protocol.Message;
import io.netty.buffer.ByteBuf;
import java.nio.charset.StandardCharsets;

public class AuthTokenValidationMessage extends Message {
    private String authToken;
    private int accountId;
    private int characterId;

    public AuthTokenValidationMessage() {
        super(103); // ID du message serveur = 103
    }

    public AuthTokenValidationMessage(String authToken, int accountId, int characterId) {
        this();
        this.authToken = authToken;
        this.accountId = accountId;
        this.characterId = characterId;
    }

    public String getAuthToken() {
        return authToken;
    }

    public int getAccountId() {
        return accountId;
    }

    public int getCharacterId() {
        return characterId;
    }

    @Override
    public void serialize(ByteBuf buffer) {
        byte[] tokenBytes = authToken.getBytes(StandardCharsets.UTF_8);
        buffer.writeShort(tokenBytes.length);
        buffer.writeBytes(tokenBytes);

        buffer.writeInt(accountId);
        buffer.writeInt(characterId);
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        int tokenLength = buffer.readShort();
        byte[] tokenBytes = new byte[tokenLength];
        buffer.readBytes(tokenBytes);
        this.authToken = new String(tokenBytes, StandardCharsets.UTF_8);

        this.accountId = buffer.readInt();
        this.characterId = buffer.readInt();
    }
}