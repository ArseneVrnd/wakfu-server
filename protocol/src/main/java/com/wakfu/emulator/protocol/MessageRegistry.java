package com.wakfu.emulator.protocol;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

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
    
    // Initialiser les messages connus
    static {
        // TODO: Enregistrer les messages connus
        // Exemple : registerMessage(1, VersionMessage::new);
    }
}