// Fichier à créer dans: protocol/src/main/java/com/wakfu/emulator/protocol/handlers/MessageDecoder.java
package com.wakfu.emulator.protocol.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import com.wakfu.emulator.protocol.Message;
import com.wakfu.emulator.protocol.MessageRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class MessageDecoder extends ByteToMessageDecoder {
    private static final Logger logger = LogManager.getLogger(MessageDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        // Vérifier qu'il y a suffisamment de données pour au moins l'ID du message
        if (in.readableBytes() < 4) {
            return;
        }

        // Marquer la position actuelle pour pouvoir revenir en arrière si nécessaire
        in.markReaderIndex();

        // Lire l'ID du message
        int messageId = in.readInt();

        try {
            // Créer le message correspondant
            Message message = MessageRegistry.createMessage(messageId);

            // Désérialiser le message
            message.deserialize(in);

            // Ajouter le message à la liste des objets décodés
            out.add(message);

            logger.debug("Message décodé: ID={}", messageId);
        } catch (Exception e) {
            logger.error("Erreur lors du décodage du message ID={}: {}", messageId, e.getMessage(), e);
            in.resetReaderIndex(); // Revenir à la position marquée
        }
    }
}