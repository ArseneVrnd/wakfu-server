package com.wakfu.emulator.protocol;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import com.wakfu.emulator.protocol.messages.VersionMessage;
import com.wakfu.emulator.protocol.messages.LoginRequestMessage;
import com.wakfu.emulator.protocol.messages.LoginResultMessage;

public class MessageRegistry {
    private static final Map<Integer, Supplier<Message>> messageFactories = new HashMap<>();

    public static void registerMessage(int id, Supplier<Message> factory) {
        messageFactories.put(id, factory);
    }

    public static Message createMessage(int id) {
        Supplier<Message> factory = messageFactories.get(id);
        if (factory == null) {
            throw new IllegalArgumentException("Message avec ID " + id + " non enregistré");
        }
        return factory.get();
    }

    static {
        // Enregistrer les messages connus
        registerMessage(1, VersionMessage::new);
        registerMessage(2, LoginRequestMessage::new);
        registerMessage(3, LoginResultMessage::new);
    }
}