package com.wakfu.emulator.auth;

import com.wakfu.emulator.protocol.model.WorldServerInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldServerManager {
    private static final Logger logger = LogManager.getLogger(WorldServerManager.class);

    // Un seul serveur de monde
    private WorldServerInfo worldServer;

    public WorldServerManager() {
        // Initialiser le serveur unique
        this.worldServer = new WorldServerInfo(
                1,
                "Wakfu Server",
                "localhost",
                com.wakfu.emulator.common.Constants.WORLD_SERVER_PORT,
                0,  // Nombre de joueurs initial
                1   // 1 = en ligne
        );

        logger.info("Serveur de monde initialisé: {} ({}:{})",
                worldServer.getName(), worldServer.getAddress(), worldServer.getPort());
    }

    public void updatePlayerCount(int playerCount) {
        this.worldServer = new WorldServerInfo(
                worldServer.getId(),
                worldServer.getName(),
                worldServer.getAddress(),
                worldServer.getPort(),
                playerCount,
                worldServer.getStatus()
        );
    }

    public void updateStatus(int status) {
        this.worldServer = new WorldServerInfo(
                worldServer.getId(),
                worldServer.getName(),
                worldServer.getAddress(),
                worldServer.getPort(),
                worldServer.getPlayerCount(),
                status
        );

        logger.info("Statut du serveur {} mis à jour: {}", worldServer.getName(), status);
    }

    public WorldServerInfo getWorldServer() {
        return worldServer;
    }
}