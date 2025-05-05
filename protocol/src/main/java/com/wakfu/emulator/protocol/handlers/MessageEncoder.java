// Fichier à créer dans: protocol/src/main/java/com/wakfu/emulator/protocol/handlers/MessageEncoder.java
package com.wakfu.emulator.protocol.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import com.wakfu.emulator.protocol.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MessageEncoder extends MessageToByteEncoder<Message> {
    private static final Logger logger = LogManager.getLogger(MessageEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) {
        try {
            // Écrire l'ID du message
            out.writeInt(msg.getId());

            // Sérialiser le contenu du message
            msg.serialize(out);

            logger.debug("Message encodé: ID={}", msg.getId());
        } catch (Exception e) {
            logger.error("Erreur lors de l'encodage du message ID={}: {}", msg.getId(), e.getMessage(), e);
        }
    }
}