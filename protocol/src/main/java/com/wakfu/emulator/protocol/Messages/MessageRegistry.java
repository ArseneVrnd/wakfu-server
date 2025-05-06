package com.wakfu.emulator.protocol;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import com.wakfu.emulator.protocol.messages.*;
import com.wakfu.emulator.protocol.messages.server.*;

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
        // Enregistrer les messages client->serveur
        registerMessage(1, VersionMessage::new);
        registerMessage(2, LoginRequestMessage::new);
        registerMessage(3, LoginResultMessage::new);
        registerMessage(4, WorldServerListMessage::new);
        registerMessage(5, CharacterListRequestMessage::new);
        registerMessage(6, CharacterListResponseMessage::new);
        registerMessage(7, CharacterCreationRequestMessage::new);
        registerMessage(8, CharacterCreationResponseMessage::new);
        registerMessage(9, CharacterSelectionRequestMessage::new);
        registerMessage(10, CharacterSelectionResponseMessage::new);
        registerMessage(11, WorldAuthRequestMessage::new);
        registerMessage(12, WorldAuthResponseMessage::new);
        // Enregistrer les messages serveur->serveur
        registerMessage(100, ServerRegistrationMessage::new);
        registerMessage(101, ServerRegistrationResponseMessage::new);
        registerMessage(102, ServerStatusUpdateMessage::new);
        registerMessage(103, AuthTokenValidationMessage::new);
        registerMessage(104, AuthTokenValidationResponseMessage::new);
    }
}