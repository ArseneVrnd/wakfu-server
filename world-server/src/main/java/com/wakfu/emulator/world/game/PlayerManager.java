package com.wakfu.emulator.world.game;

import com.wakfu.emulator.database.CharacterManager;
import io.netty.channel.Channel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager {
    private static final Logger logger = LogManager.getLogger(PlayerManager.class);

    // Map des joueurs connectés (ID du personnage -> Player)
    private final Map<Integer, Player> connectedPlayers = new ConcurrentHashMap<>();
    // Map des canaux associés aux joueurs (Channel -> ID du personnage)
    private final Map<Channel, Integer> playerChannels = new ConcurrentHashMap<>();

    private final CharacterManager characterManager;
    private final MapManager mapManager;

    public PlayerManager(CharacterManager characterManager, MapManager mapManager) {
        this.characterManager = characterManager;
        this.mapManager = mapManager;
    }

    public Player connectPlayer(int accountId, int characterId, Channel channel) {
        // Vérifier si le joueur est déjà connecté
        Player existingPlayer = connectedPlayers.get(characterId);
        if (existingPlayer != null) {
            logger.warn("Tentative de double connexion pour le personnage ID: {}", characterId);
            existingPlayer.disconnect();
            removePlayer(existingPlayer);
        }

        // Charger le personnage depuis la base de données
        com.wakfu.emulator.database.model.Character character = characterManager.getCharacterById(characterId);
        if (character == null) {
            logger.error("Personnage introuvable: ID {}", characterId);
            return null;
        }

        // Créer un nouveau joueur
        Player player = new Player(accountId, character, channel);

        // Ajouter le joueur à la carte par défaut
        GameMap defaultMap = mapManager.getDefaultMap();
        defaultMap.addPlayer(player);

        // Enregistrer le joueur
        connectedPlayers.put(characterId, player);
        playerChannels.put(channel, characterId);

        logger.info("Joueur connecté: {} (ID: {})", character.getName(), characterId);

        return player;
    }

    public void disconnectPlayer(Channel channel) {
        Integer characterId = playerChannels.get(channel);
        if (characterId != null) {
            Player player = connectedPlayers.get(characterId);
            if (player != null) {
                player.onDisconnect();
                removePlayer(player);
            }
            playerChannels.remove(channel);
        }
    }

    private void removePlayer(Player player) {
        connectedPlayers.remove(player.getCharacter().getId());
        playerChannels.remove(player.getChannel());
    }

    public Player getPlayerByCharacterId(int characterId) {
        return connectedPlayers.get(characterId);
    }

    public Player getPlayerByChannel(Channel channel) {
        Integer characterId = playerChannels.get(channel);
        if (characterId != null) {
            return connectedPlayers.get(characterId);
        }
        return null;
    }

    public Collection<Player> getAllPlayers() {
        return Collections.unmodifiableCollection(connectedPlayers.values());
    }

    public int getPlayerCount() {
        return connectedPlayers.size();
    }
}