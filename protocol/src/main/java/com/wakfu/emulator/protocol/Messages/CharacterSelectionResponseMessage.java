package com.wakfu.emulator.protocol.messages;

import com.wakfu.emulator.protocol.Message;
import io.netty.buffer.ByteBuf;
import java.nio.charset.StandardCharsets;

public class CharacterSelectionResponseMessage extends Message {
    private boolean success;
    private String errorMessage;
    private String worldServerHost;
    private int worldServerPort;
    private String authToken;

    public CharacterSelectionResponseMessage() {
        super(10); // ID du message = 10
    }

    public CharacterSelectionResponseMessage(boolean success, String errorMessage,
                                             String worldServerHost, int worldServerPort, String authToken) {
        this();
        this.success = success;
        this.errorMessage = errorMessage != null ? errorMessage : "";
        this.worldServerHost = worldServerHost != null ? worldServerHost : "";
        this.worldServerPort = worldServerPort;
        this.authToken = authToken != null ? authToken : "";
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getWorldServerHost() {
        return worldServerHost;
    }

    public int getWorldServerPort() {
        return worldServerPort;
    }

    public String getAuthToken() {
        return authToken;
    }

    @Override
    public void serialize(ByteBuf buffer) {
        buffer.writeBoolean(success);

        byte[] errorBytes = errorMessage.getBytes(StandardCharsets.UTF_8);
        buffer.writeShort(errorBytes.length);
        buffer.writeBytes(errorBytes);

        if (success) {
            byte[] hostBytes = worldServerHost.getBytes(StandardCharsets.UTF_8);
            buffer.writeShort(hostBytes.length);
            buffer.writeBytes(hostBytes);

            buffer.writeInt(worldServerPort);

            byte[] tokenBytes = authToken.getBytes(StandardCharsets.UTF_8);
            buffer.writeShort(tokenBytes.length);
            buffer.writeBytes(tokenBytes);
        }
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        this.success = buffer.readBoolean();

        int errorLength = buffer.readShort();
        byte[] errorBytes = new byte[errorLength];
        buffer.readBytes(errorBytes);
        this.errorMessage = new String(errorBytes, StandardCharsets.UTF_8);

        if (success) {
            int hostLength = buffer.readShort();
            byte[] hostBytes = new byte[hostLength];
            buffer.readBytes(hostBytes);
            this.worldServerHost = new String(hostBytes, StandardCharsets.UTF_8);

            this.worldServerPort = buffer.readInt();

            int tokenLength = buffer.readShort();
            byte[] tokenBytes = new byte[tokenLength];
            buffer.readBytes(tokenBytes);
            this.authToken = new String(tokenBytes, StandardCharsets.UTF_8);
        }
    }
}