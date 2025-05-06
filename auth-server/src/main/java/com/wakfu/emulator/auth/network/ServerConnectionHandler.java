package com.wakfu.emulator.auth.network;

import com.wakfu.emulator.auth.AuthServer;
import com.wakfu.emulator.protocol.Message;
import com.wakfu.emulator.protocol.messages.server.ServerRegistrationMessage;
import com.wakfu.emulator.protocol.messages.server.ServerRegistrationResponseMessage;
import com.wakfu.emulator.protocol.messages.server.ServerStatusUpdateMessage;
import com.wakfu.emulator.protocol.model.WorldServerInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerConnectionHandler extends SimpleChannelInboundHandler<Message> {
    private static final Logger logger = LogManager.getLogger(ServerConnectionHandler.class);
    private int serverId = -1;
    private String serverName = "";
    private boolean authenticated = false;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
        logger.info("Message reçu du serveur de monde: {}", msg.getClass().getSimpleName());

        if (msg instanceof ServerRegistrationMessage) {
            handleServerRegistration(ctx, (ServerRegistrationMessage) msg);
        } else if (msg instanceof ServerStatusUpdateMessage) {
            handleServerStatusUpdate(ctx, (ServerStatusUpdateMessage) msg);
        } else {
            logger.warn("Type de message non géré: {}", msg.getClass().getSimpleName());
        }
    }

    private void handleServerRegistration(ChannelHandlerContext ctx, ServerRegistrationMessage msg) {
        serverId = msg.getServerId();
        serverName = msg.getServerName();
        String secretKey = msg.getSecretKey();

        logger.info("Demande d'enregistrement du serveur: {} (ID: {})", serverName, serverId);

        // Vérifier la clé secrète (à implémenter dans une vraie application)
        boolean validKey = validateServerKey(serverId, secretKey);

        if (validKey) {
            authenticated = true;

            // Mettre à jour les informations du serveur dans le gestionnaire
            WorldServerInfo serverInfo = new WorldServerInfo(
                    serverId,
                    serverName,
                    ctx.channel().remoteAddress().toString(),
                    AuthServer.getInstance().getWorldServerManager().getWorldServer().getPort(),
                    0,
                    1  // 1 = en ligne
            );

            // Accepter l'enregistrement
            ctx.writeAndFlush(new ServerRegistrationResponseMessage(true,
                    "Serveur enregistré avec succès"));

            logger.info("Serveur de monde enregistré: {} (ID: {})", serverName, serverId);
        } else {
            // Refuser l'enregistrement
            ctx.writeAndFlush(new ServerRegistrationResponseMessage(false,
                    "Clé d'authentification invalide"));

            logger.warn("Tentative d'enregistrement avec une clé invalide: {} (ID: {})",
                    serverName, serverId);

            // Fermer la connexion
            ctx.close();
        }
    }

    private void handleServerStatusUpdate(ChannelHandlerContext ctx, ServerStatusUpdateMessage msg) {
        if (!authenticated) {
            logger.warn("Mise à jour de statut d'un serveur non authentifié");
            ctx.close();
            return;
        }

        int playerCount = msg.getPlayerCount();
        int status = msg.getStatus();

        logger.debug("Mise à jour du statut du serveur {} (ID: {}): {} joueurs, statut {}",
                serverName, serverId, playerCount, status);

        // Mettre à jour les informations dans le gestionnaire de serveurs
        AuthServer.getInstance().getWorldServerManager().updatePlayerCount(playerCount);
        AuthServer.getInstance().getWorldServerManager().updateStatus(status);
    }

    private boolean validateServerKey(int serverId, String secretKey) {
        // Pour la simplicité, on accepte toujours la clé dans cet exemple
        // Dans une vraie application, vous voudriez vérifier la clé dans une base de données
        return true;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (authenticated) {
            logger.info("Serveur de monde déconnecté: {} (ID: {})", serverName, serverId);

            // Mettre le serveur hors ligne
            AuthServer.getInstance().getWorldServerManager().updateStatus(0); // 0 = hors ligne
        } else {
            logger.info("Connexion serveur non authentifiée fermée");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("Erreur dans la connexion serveur: {}", cause.getMessage(), cause);
        ctx.close();
    }
}