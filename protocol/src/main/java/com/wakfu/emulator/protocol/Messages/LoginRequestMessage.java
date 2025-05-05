// Fichier à créer dans: protocol/src/main/java/com/wakfu/emulator/protocol/messages/LoginRequestMessage.java
package com.wakfu.emulator.protocol.messages;

import com.wakfu.emulator.protocol.Message;
import io.netty.buffer.ByteBuf;
import java.nio.charset.StandardCharsets;

public class LoginRequestMessage extends Message {
    private String username;
    private String password;

    public LoginRequestMessage() {
        super(2); // ID du message = 2
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public void serialize(ByteBuf buffer) {
        // Écrire le username
        byte[] usernameBytes = username.getBytes(StandardCharsets.UTF_8);
        buffer.writeShort(usernameBytes.length);
        buffer.writeBytes(usernameBytes);

        // Écrire le password
        byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
        buffer.writeShort(passwordBytes.length);
        buffer.writeBytes(passwordBytes);
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        // Lire le username
        int usernameLength = buffer.readShort();
        byte[] usernameBytes = new byte[usernameLength];
        buffer.readBytes(usernameBytes);
        this.username = new String(usernameBytes, StandardCharsets.UTF_8);

        // Lire le password
        int passwordLength = buffer.readShort();
        byte[] passwordBytes = new byte[passwordLength];
        buffer.readBytes(passwordBytes);
        this.password = new String(passwordBytes, StandardCharsets.UTF_8);
    }
}