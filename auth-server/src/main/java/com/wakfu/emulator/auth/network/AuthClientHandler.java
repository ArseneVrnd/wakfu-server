package com.wakfu.emulator.auth.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import com.wakfu.emulator.auth.AuthServer;
import com.wakfu.emulator.database.AccountManager;
import com.wakfu.emulator.database.CharacterManager;
import com.wakfu.emulator.database.model.Character;
import com.wakfu.emulator.protocol.Message;
import com.wakfu.emulator.protocol.messages.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AuthClientHandler extends SimpleChannelInboundHandler<Message> {
    private static final Logger logger = LogManager.getLogger(AuthClientHandler.class);
    private String username;
    private int accountId = -1;
    private boolean authenticated = false;

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
        } else if (msg instanceof LoginRequestMessage) {
            handleLoginRequest(ctx, (LoginRequestMessage) msg);
        } else if (msg instanceof CharacterListRequestMessage) {
            handleCharacterListRequest(ctx, (CharacterListRequestMessage) msg);
        } else if (msg instanceof CharacterCreationRequestMessage) {
            handleCharacterCreationRequest(ctx, (CharacterCreationRequestMessage) msg);
        } else {
            logger.warn("Type de message non géré: {}", msg.getClass().getSimpleName());
        }
    }

    private void handleVersionMessage(ChannelHandlerContext ctx, VersionMessage msg) {
        logger.info("Version du client: {}", msg.getProtocolVersion());

        // Vérifier la version du protocole
        if (msg.getProtocolVersion() != com.wakfu.emulator.common.Constants.PROTOCOL_VERSION) {
            logger.warn("Version du protocole non prise en charge: {}", msg.getProtocolVersion());
            ctx.close();
        }
    }

    private void handleLoginRequest(ChannelHandlerContext ctx, LoginRequestMessage msg) {
        username = msg.getUsername();
        String password = msg.getPassword();

        logger.info("Tentative de connexion pour l'utilisateur: {}", username);

        // Récupérer le AccountManager depuis le serveur
        AccountManager accountManager = AuthServer.getInstance().getAccountManager();

        // Vérifier les identifiants
        boolean authenticated = accountManager.authenticate(username, password);

        if (authenticated) {
            logger.info("Authentification réussie pour l'utilisateur: {}", username);
            this.authenticated = true;
            this.accountId = accountManager.getAccountId(username);

            // Mettre à jour la date de dernière connexion
            accountManager.updateLastLogin(username);

            // Envoyer un message de succès
            ctx.writeAndFlush(new LoginResultMessage(true, ""));

            // Envoyer les informations du serveur unique
            ctx.writeAndFlush(new WorldServerListMessage(
                    AuthServer.getInstance().getWorldServerManager().getWorldServer()));
        } else {
            logger.info("Échec de l'authentification pour l'utilisateur: {}", username);

            // Envoyer un message d'échec
            ctx.writeAndFlush(new LoginResultMessage(false, "Nom d'utilisateur ou mot de passe incorrect."));
        }
    }

// Ajouter cette méthode à la classe AuthClientHandler existante

    private void handleCharacterSelection(ChannelHandlerContext ctx, CharacterSelectionRequestMessage msg) {
        if (!authenticated || accountId == -1) {
            logger.warn("Tentative de sélection de personnage sans authentification");
            ctx.close();
            return;
        }

        int characterId = msg.getCharacterId();
        logger.info("Demande de sélection du personnage ID: {} par le compte {}", characterId, accountId);

        CharacterManager characterManager = AuthServer.getInstance().getCharacterManager();

        // Vérifier que le personnage existe et appartient au compte
        Character character = characterManager.getCharacterById(characterId);

        if (character == null) {
            ctx.writeAndFlush(new CharacterSelectionResponseMessage(
                    false, "Personnage introuvable", null, 0, null));
            return;
        }

        if (character.getAccountId() != accountId) {
            logger.warn("Tentative d'accès à un personnage d'un autre compte. " +
                            "Compte: {}, ID Personnage: {}, Compte du personnage: {}",
                    accountId, characterId, character.getAccountId());

            ctx.writeAndFlush(new CharacterSelectionResponseMessage(
                    false, "Vous n'êtes pas autorisé à jouer ce personnage", null, 0, null));
            return;
        }

        // Récupérer les informations du serveur de monde
        WorldServerInfo worldServer = AuthServer.getInstance().getWorldServerManager().getWorldServer();

        if (worldServer.getStatus() != 1) { // 1 = en ligne
            ctx.writeAndFlush(new CharacterSelectionResponseMessage(
                    false, "Le serveur de monde n'est pas disponible", null, 0, null));
            return;
        }

        // Générer un jeton d'authentification temporaire
        String authToken = generateAuthToken(accountId, characterId);

        // Envoyer les informations de connexion au serveur de monde
        ctx.writeAndFlush(new CharacterSelectionResponseMessage(
                true, "", worldServer.getAddress(), worldServer.getPort(), authToken));

        logger.info("Sélection de personnage réussie: ID={}, redirection vers {}:{}",
                characterId, worldServer.getAddress(), worldServer.getPort());
    }

    private String generateAuthToken(int accountId, int characterId) {
        // En production, utilisez un système de jeton sécurisé (JWT par exemple)
        // Pour simplifier, nous utilisons une chaîne aléatoire
        String timestamp = String.valueOf(System.currentTimeMillis());
        String token = accountId + "-" + characterId + "-" + timestamp;

        // Dans une vraie application, vous stockeriez ce jeton pour validation ultérieure
        // par le serveur de monde

        return token;
    }

    // Modifier la méthode channelRead0 pour ajouter la gestion de ce message
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
        logger.info("Message reçu de type: {}", msg.getClass().getSimpleName());

        // Traiter les différents types de messages
        if (msg instanceof VersionMessage) {
            handleVersionMessage(ctx, (VersionMessage) msg);
        } else if (msg instanceof LoginRequestMessage) {
            handleLoginRequest(ctx, (LoginRequestMessage) msg);
        } else if (msg instanceof CharacterListRequestMessage) {
            handleCharacterListRequest(ctx, (CharacterListRequestMessage) msg);
        } else if (msg instanceof CharacterCreationRequestMessage) {
            handleCharacterCreationRequest(ctx, (CharacterCreationRequestMessage) msg);
        } else if (msg instanceof CharacterSelectionRequestMessage) {
            handleCharacterSelection(ctx, (CharacterSelectionRequestMessage) msg);
        } else {
            logger.warn("Type de message non géré: {}", msg.getClass().getSimpleName());
        }
    }