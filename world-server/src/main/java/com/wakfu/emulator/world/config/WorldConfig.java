package com.wakfu.emulator.world.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class WorldConfig {
    private static final Logger logger = LogManager.getLogger(WorldConfig.class);
    private static final String CONFIG_FILE = "config/world-server.json";

    private String databaseUrl;
    private String databaseUsername;
    private String databasePassword;
    private int serverPort;
    private int serverId;
    private String serverName;
    private String serverKey;
    private String authServerHost;
    private int authServerPort;

    public static WorldConfig load() {
        try {
            // Vérifier si le fichier de configuration existe
            File configFile = new File(CONFIG_FILE);
            if (!configFile.exists()) {
                // Créer le répertoire config s'il n'existe pas
                Files.createDirectories(Paths.get("config"));

                // Créer un fichier de configuration par défaut
                WorldConfig defaultConfig = new WorldConfig();
                defaultConfig.setDatabaseUrl("jdbc:postgresql://localhost:5432/wakfu_server");
                defaultConfig.setDatabaseUsername("wakfu_user");
                defaultConfig.setDatabasePassword("password");
                defaultConfig.setServerPort(com.wakfu.emulator.common.Constants.WORLD_SERVER_PORT);
                defaultConfig.setServerId(1);
                defaultConfig.setServerName("Wakfu Server");
                defaultConfig.setServerKey("secret_key");
                defaultConfig.setAuthServerHost("localhost");
                defaultConfig.setAuthServerPort(com.wakfu.emulator.common.Constants.AUTH_SERVER_PORT);

                ObjectMapper mapper = new ObjectMapper();
                mapper.writerWithDefaultPrettyPrinter().writeValue(configFile, defaultConfig);

                logger.info("Fichier de configuration par défaut créé: {}", CONFIG_FILE);
                return defaultConfig;
            }

            // Charger la configuration existante
            ObjectMapper mapper = new ObjectMapper();
            WorldConfig config = mapper.readValue(configFile, WorldConfig.class);

            logger.info("Configuration chargée depuis: {}", CONFIG_FILE);
            return config;
        } catch (IOException e) {
            logger.error("Erreur lors du chargement de la configuration: {}", e.getMessage(), e);

            // Retourner une configuration par défaut en cas d'erreur
            WorldConfig defaultConfig = new WorldConfig();
            defaultConfig.setDatabaseUrl("jdbc:postgresql://localhost:5432/wakfu_server");
            defaultConfig.setDatabaseUsername("wakfu_user");
            defaultConfig.setDatabasePassword("password");
            defaultConfig.setServerPort(com.wakfu.emulator.common.Constants.WORLD_SERVER_PORT);
            defaultConfig.setServerId(1);
            defaultConfig.setServerName("Wakfu Server");
            defaultConfig.setServerKey("secret_key");
            defaultConfig.setAuthServerHost("localhost");
            defaultConfig.setAuthServerPort(com.wakfu.emulator.common.Constants.AUTH_SERVER_PORT);

            return defaultConfig;
        }
    }

    // Getters et setters
    public String getDatabaseUrl() { return databaseUrl; }
    public void setDatabaseUrl(String databaseUrl) { this.databaseUrl = databaseUrl; }

    public String getDatabaseUsername() { return databaseUsername; }
    public void setDatabaseUsername(String databaseUsername) { this.databaseUsername = databaseUsername; }

    public String getDatabasePassword() { return databasePassword; }
    public void setDatabasePassword(String databasePassword) { this.databasePassword = databasePassword; }

    public int getServerPort() { return serverPort; }
    public void setServerPort(int serverPort) { this.serverPort = serverPort; }

    public int getServerId() { return serverId; }
    public void setServerId(int serverId) { this.serverId = serverId; }

    public String getServerName() { return serverName; }
    public void setServerName(String serverName) { this.serverName = serverName; }

    public String getServerKey() { return serverKey; }
    public void setServerKey(String serverKey) { this.serverKey = serverKey; }

    public String getAuthServerHost() { return authServerHost; }
    public void setAuthServerHost(String authServerHost) { this.authServerHost = authServerHost; }

    public int getAuthServerPort() { return authServerPort; }
    public void setAuthServerPort(int authServerPort) { this.authServerPort = authServerPort; }
}