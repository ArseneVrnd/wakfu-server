package com.wakfu.emulator.world.game;

import com.wakfu.emulator.database.model.Character;
import io.netty.channel.Channel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Player {
    private static final Logger logger = LogManager.getLogger(Player.class);

    private final int accountId;
    private final Character character;
    private final Channel channel;
    private GameMap currentMap;
    private int x;
    private int y;
    private boolean inGame = false;

    public Player(int accountId, Character character, Channel channel) {
        this.accountId = accountId;
        this.character = character;
        this.channel = channel;

        // Initialiser la position à partir des données du personnage
        try {
            if (character.getPosition() != null) {
                this.x = character.getPosition().get("x").asInt();
                this.y = character.getPosition().get("y").asInt();
            } else {
                // Position par défaut
                this.x = 0;
                this.y = 0;
            }
        } catch (Exception e) {
            logger.error("Erreur lors de l'initialisation de la position du joueur: {}", e.getMessage(), e);
            this.x = 0;
            this.y = 0;
        }
    }

    public int getAccountId() {
        return accountId;
    }

    public Character getCharacter() {
        return character;
    }

    public Channel getChannel() {
        return channel;
    }

    public GameMap getCurrentMap() {
        return currentMap;
    }

    public void setCurrentMap(GameMap currentMap) {
        this.currentMap = currentMap;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isInGame() {
        return inGame;
    }

    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }

    public void sendMessage(Object message) {
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(message);
        }
    }

    public void disconnect() {
        if (channel != null) {
            channel.close();
        }
    }

    // Méthode appelée lors de la déconnexion du joueur
    public void onDisconnect() {
        if (currentMap != null) {
            currentMap.removePlayer(this);
        }

        // Sauvegarder la position du joueur
        savePosition();

        logger.info("Joueur déconnecté: {} (ID: {})",
                character.getName(), character.getId());
    }

    // Sauvegarder la position du joueur dans la base de données
    private void savePosition() {
        try {
            // Mettre à jour l'objet JSON de position
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.node.ObjectNode positionNode = mapper.createObjectNode();
            positionNode.put("mapId", currentMap != null ? currentMap.getId() : 0);
            positionNode.put("x", x);
            positionNode.put("y", y);

            // TODO: Mettre à jour la position dans la base de données

            logger.debug("Position du joueur sauvegardée: {} (ID: {}), Map: {}, X: {}, Y: {}",
                    character.getName(), character.getId(),
                    currentMap != null ? currentMap.getId() : 0, x, y);
        } catch (Exception e) {
            logger.error("Erreur lors de la sauvegarde de la position du joueur: {}", e.getMessage(), e);
        }
    }
}