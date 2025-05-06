package com.wakfu.emulator.protocol.messages;

import com.wakfu.emulator.protocol.Message;
import io.netty.buffer.ByteBuf;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CharacterListResponseMessage extends Message {
    public static class CharacterSummary {
        private int id;
        private String name;
        private int level;
        private short classId;
        private short gender;

        public CharacterSummary(int id, String name, int level, short classId, short gender) {
            this.id = id;
            this.name = name;
            this.level = level;
            this.classId = classId;
            this.gender = gender;
        }

        // Getters
        public int getId() { return id; }
        public String getName() { return name; }
        public int getLevel() { return level; }
        public short getClassId() { return classId; }
        public short getGender() { return gender; }
    }

    private List<CharacterSummary> characters;

    public CharacterListResponseMessage() {
        super(6); // ID du message = 6
        this.characters = new ArrayList<>();
    }

    public CharacterListResponseMessage(List<CharacterSummary> characters) {
        this();
        this.characters = characters;
    }

    public List<CharacterSummary> getCharacters() {
        return characters;
    }

    @Override
    public void serialize(ByteBuf buffer) {
        buffer.writeShort(characters.size());

        for (CharacterSummary character : characters) {
            buffer.writeInt(character.getId());

            byte[] nameBytes = character.getName().getBytes(StandardCharsets.UTF_8);
            buffer.writeShort(nameBytes.length);
            buffer.writeBytes(nameBytes);

            buffer.writeInt(character.getLevel());
            buffer.writeShort(character.getClassId());
            buffer.writeShort(character.getGender());
        }
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        int count = buffer.readShort();

        for (int i = 0; i < count; i++) {
            int id = buffer.readInt();

            int nameLength = buffer.readShort();
            byte[] nameBytes = new byte[nameLength];
            buffer.readBytes(nameBytes);
            String name = new String(nameBytes, StandardCharsets.UTF_8);

            int level = buffer.readInt();
            short classId = buffer.readShort();
            short gender = buffer.readShort();

            characters.add(new CharacterSummary(id, name, level, classId, gender));
        }
    }
}