package com.wakfu.emulator.protocol.messages;

import com.wakfu.emulator.protocol.Message;
import io.netty.buffer.ByteBuf;
import java.nio.charset.StandardCharsets;

public class CharacterCreationRequestMessage extends Message {
    private String name;
    private short classId;
    private short gender;

    public CharacterCreationRequestMessage() {
        super(7); // ID du message = 7
    }

    public String getName() {
        return name;
    }

    public short getClassId() {
        return classId;
    }

    public short getGender() {
        return gender;
    }

    @Override
    public void serialize(ByteBuf buffer) {
        byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
        buffer.writeShort(nameBytes.length);
        buffer.writeBytes(nameBytes);

        buffer.writeShort(classId);
        buffer.writeShort(gender);
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        int nameLength = buffer.readShort();
        byte[] nameBytes = new byte[nameLength];
        buffer.readBytes(nameBytes);
        this.name = new String(nameBytes, StandardCharsets.UTF_8);

        this.classId = buffer.readShort();
        this.gender = buffer.readShort();
    }
}