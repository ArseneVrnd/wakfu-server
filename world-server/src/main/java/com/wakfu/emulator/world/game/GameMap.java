package com.wakfu.emulator.world.game;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameMap {
    private static final Logger logger = LogManager.getLogger(GameMap.class);

    private final int id;
    private final String name;
    private final int width;
    private final int height;
    private final List<Player> players = new CopyOnWriteArrayList<>();

    public GameMap(int id, String name, int width, int height) {
        this.id = id;
        this.name = name;
        this.width = width;
        this.height = height;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    public void addPlayer(Player player) {
        if (!players.contains(player)) {
            players.add(player);
            player.setCurrentMap(this);

            logger.debug("Joueur ajouté à la carte {}: {} (ID: {})",
                    id, player.getCharacter().getName(), player.getCharacter().getId());

            // Notifier les autres joueurs de l'arrivée d'un nouveau joueur
            broadcastPlayerJoined(player);
        }
    }

    public void removePlayer(Player player) {
        if (players.remove(player)) {
            player.setCurrentMap(null);

            logger.debug("Joueur retiré de la carte {}: {} (ID: {})",
                    id, player.getCharacter().getName(), player.getCharacter().getId());

            // Notifier les autres joueurs du départ d'un joueur
            broadcastPlayerLeft(player);
        }
    }

    // Diffuser un message à tous les joueurs sur la carte
    public void broadcast(Object message, Player... excludedPlayers) {
        List<Player> excluded = List.of(excludedPlayers);

        for (Player player : players) {
            if (!excluded.contains(player)) {
                player.sendMessage(message);
            }
        }
    }

    // Diffuser l'arrivée d'un nouveau joueur
    private void broadcastPlayerJoined(Player newPlayer) {
        // TODO: Créer et envoyer un message pour notifier l'arrivée d'un joueur
    }

    // Diffuser le départ d'un joueur
    private void broadcastPlayerLeft(Player player) {
        // TODO: Créer et envoyer un message pour notifier le départ d'un joueur
    }

    // Vérifier si les coordonnées sont valides sur la carte
    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
}