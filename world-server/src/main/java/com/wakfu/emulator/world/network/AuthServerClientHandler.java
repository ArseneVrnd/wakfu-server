package com.wakfu.emulator.world.network;

import com.wakfu.emulator.protocol.Message;
import com.wakfu.emulator.protocol.messages.server.ServerRegistrationResponseMessage;
import com.wakfu.emulator.world.WorldServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AuthServerClientHandler extends SimpleChannelInboundHandler<Message> {
    private static final Logger logger = LogManager.getLogger(AuthServerClientHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
        logger.debug("Message reçu du serveur d'authentification: {}", msg.getClass().getSimpleName());

        if (msg instanceof ServerRegistrationResponseMessage) {
            handleRegistrationResponse(ctx, (ServerRegistrationResponseMessage) msg);
        } else {
            logger.warn("Message non géré du serveur d'authentification: {}", msg.getClass().getSimpleName());
        }
    }

    private void handleRegistrationResponse(ChannelHandlerContext ctx, ServerRegistrationResponseMessage msg) {
        if (msg.isAccepted()) {
            logger.info("Enregistrement accepté par le serveur d'authentification: {}", msg.getMessage());
            WorldServer.getInstance().setRegistered(true);
        } else {
            logger.error("Enregistrement refusé par le serveur d'authentification: {}", msg.getMessage());
            ctx.close();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        logger.warn("Connexion au serveur d'authentification perdue");
        WorldServer.getInstance().setRegistered(false);

        // La reconnexion sera gérée par AuthServerClient
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("Erreur dans la connexion au serveur d'authentification: {}",
                cause.getMessage(), cause);
        ctx.close();
    }
}