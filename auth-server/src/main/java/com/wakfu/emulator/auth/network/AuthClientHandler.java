// Fichier à créer dans: auth-server/src/main/java/com/wakfu/emulator/auth/network/AuthClientHandler.java
package com.wakfu.emulator.auth.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import com.wakfu.emulator.protocol.Message;
import com.wakfu.emulator.protocol.messages.VersionMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AuthClientHandler extends SimpleChannelInboundHandler<Message> {
    private static final Logger logger = LogManager.getLogger(AuthClientHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // Envoyer un message de version au client quand il se connecte
        logger.info("Nouvelle connexion client établie: {}", ctx.channel().remoteAddress());

        // Envoyer un message VersionMessage au client
        ctx.writeAndFlush(new VersionMessage());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
        logger.info("Message reçu de type: {}", msg.getClass().getSimpleName());

        // Traiter les différents types de messages
        if (msg instanceof VersionMessage) {
            handleVersionMessage(ctx, (VersionMessage) msg);
        } else {
            logger.warn("Type de message non géré: {}", msg.getClass().getSimpleName());
        }
    }

    // Ajoutez la gestion du message de connexion:

    private void handleLoginRequest(ChannelHandlerContext ctx, LoginRequestMessage msg) {
        String username = msg.getUsername();
        String password = msg.getPassword();

        logger.info("Tentative de connexion pour l'utilisateur: {}", username);

        // Récupérer le AccountManager depuis le serveur
        AccountManager accountManager = AuthServer.getInstance().getAccountManager();

        // Vérifier les identifiants
        boolean authenticated = accountManager.authenticate(username, password);

        if (authenticated) {
            logger.info("Authentification réussie pour l'utilisateur: {}", username);

            // Mettre à jour la date de dernière connexion
            accountManager.updateLastLogin(username);

            // Envoyer un message de succès
            ctx.writeAndFlush(new LoginResultMessage(true, ""));
        } else {
            logger.info("Échec de l'authentification pour l'utilisateur: {}", username);

            // Envoyer un message d'échec
            ctx.writeAndFlush(new LoginResultMessage(false, "Nom d'utilisateur ou mot de passe incorrect."));
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
        logger.info("Message reçu de type: {}", msg.getClass().getSimpleName());

        // Traiter les différents types de messages
        if (msg instanceof VersionMessage) {
            handleVersionMessage(ctx, (VersionMessage) msg);
        } else if (msg instanceof LoginRequestMessage) {
            handleLoginRequest(ctx, (LoginRequestMessage) msg);
        } else {
            logger.warn("Type de message non géré: {}", msg.getClass().getSimpleName());
        }
    }