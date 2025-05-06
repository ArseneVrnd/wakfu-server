// Fichier à créer dans: auth-server/src/main/java/com/wakfu/emulator/auth/config/AuthConfig.java
package com.wakfu.emulator.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AuthConfig {
    private static final Logger logger = LogManager.getLogger(AuthConfig.class);
    private static final String CONFIG_FILE = "config/auth-server.json";

    private String databaseUrl;
    private String databaseUsername;
    private String databasePassword;
    private int serverPort;

    public static AuthConfig load() {
        try {
            // Vérifier si le fichier de configuration existe
            File configFile = new File(CONFIG_FILE);
            if (!configFile.exists()) {
                // Créer le répertoire config s'il n'existe pas
                Files.createDirectories(Paths.get("config"));

                // Créer un fichier de configuration par défaut
                AuthConfig defaultConfig = new AuthConfig();
                defaultConfig.setDatabaseUrl("jdbc:postgresql://localhost:5432/wakfu_server");
                defaultConfig.setDatabaseUsername("wakfu_user");
                defaultConfig.setDatabasePassword("password");
                defaultConfig.setServerPort(5558);

                ObjectMapper mapper = new ObjectMapper();
                mapper.writerWithDefaultPrettyPrinter().writeValue(configFile, defaultConfig);

                logger.info("Fichier de configuration par défaut créé: {}", CONFIG_FILE);
                return defaultConfig;
            }

            // Charger la configuration existante
            ObjectMapper mapper = new ObjectMapper();
            AuthConfig config = mapper.readValue(configFile, AuthConfig.class);

            logger.info("Configuration chargée depuis: {}", CONFIG_FILE);
            return config;
        } catch (IOException e) {
            logger.error("Erreur lors du chargement de la configuration: {}", e.getMessage(), e);

            // Retourner une configuration par défaut en cas d'erreur
            AuthConfig defaultConfig = new AuthConfig();
            defaultConfig.setDatabaseUrl("jdbc:postgresql://localhost:5432/wakfu_server");
            defaultConfig.setDatabaseUsername("wakfu_user");
            defaultConfig.setDatabasePassword("password");
            defaultConfig.setServerPort(5558);

            return defaultConfig;
        }
    }

    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public void setDatabaseUrl(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

    public String getDatabaseUsername() {
        return databaseUsername;
    }

    public void setDatabaseUsername(String databaseUsername) {
        this.databaseUsername = databaseUsername;
    }

    public String getDatabasePassword() {
        return databasePassword;
    }

    public void setDatabasePassword(String databasePassword) {
        this.databasePassword = databasePassword;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
}