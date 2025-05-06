package com.wakfu.emulator.world.network;

import com.wakfu.emulator.protocol.Message;
import com.wakfu.emulator.protocol.messages.WorldAuthRequestMessage;
import com.wakfu.emulator.protocol.messages.WorldAuthResponseMessage;
import com.wakfu.emulator.world.WorldServer;
import com.wakfu.emulator.world.auth.AuthTokenManager;
import com.wakfu.emulator.world.game.Player;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldClientHandler extends SimpleChannelInboundHandler<Message> {
    private static final Logger logger = LogManager.getLogger(WorldClientHandler.class);
    private boolean authenticated = false;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.info("Nouvelle connexion client établie: {}", ctx.channel().remoteAddress());
        // Ne pas incrémenter le nombre de joueurs avant authentification
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
        logger.info("Message reçu de type: {}", msg.getClass().getSimpleName());

        if (msg instanceof WorldAuthRequestMessage) {
            handleAuthRequest(ctx, (WorldAuthRequestMessage) msg);
        } else if (!authenticated) {
            logger.warn("Message reçu d'un client non authentifié: {}", msg.getClass().getSimpleName());
            ctx.close();
        } else {
            // Traiter les autres messages de jeu ici
            logger.warn("Type de message non géré: {}", msg.getClass().getSimpleName());
        }
    }

    private void handleAuthRequest(ChannelHandlerContext ctx, WorldAuthRequestMessage msg) {
        String authToken = msg.getAuthToken();

        logger.info("Demande d'authentification avec le jeton: {}", authToken);

        AuthTokenManager tokenManager = WorldServer.getInstance().getAuthTokenManager();
        AuthTokenManager.AuthTokenInfo tokenInfo = tokenManager.validateToken(authToken);

        if (tokenInfo != null) {
            // Authentification réussie
            authenticated = true;

            // Connecter le joueur
            Player player = WorldServer.getInstance().getPlayerManager().connectPlayer(
                    tokenInfo.getAccountId(), tokenInfo.getCharacterId(), ctx.channel());

            if (player != null) {
                // Envoyer un message de succès
                ctx.writeAndFlush(new WorldAuthResponseMessage(true, ""));

                // Incrémenter le nombre de joueurs maintenant que l'authentification est validée
                WorldServer.getInstance().incrementPlayerCount();

                logger.info("Client authentifié: Compte ID: {}, Personnage ID: {}",
                        tokenInfo.getAccountId(), tokenInfo.getCharacterId());
            } else {
                // Échec lors de la connexion du joueur
                ctx.writeAndFlush(new WorldAuthResponseMessage(
                        false, "Erreur lors du chargement du personnage"));
                ctx.close();
            }
        } else {
            // Authentification échouée
            ctx.writeAndFlush(new WorldAuthResponseMessage(
                    false, "Jeton d'authentification invalide ou expiré"));
            ctx.close();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (authenticated) {
            // Déconnecter le joueur
            WorldServer.getInstance().getPlayerManager().disconnectPlayer(ctx.channel());

            // Décrémenter le nombre de joueurs
            WorldServer.getInstance().decrementPlayerCount();
        }

        logger.info("Client déconnecté: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("Erreur de connexion client: {}", cause.getMessage(), cause);
        ctx.close();
    }
}