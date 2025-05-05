package com.wakfu.emulator.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AuthServer {
    private static final Logger logger = LogManager.getLogger(AuthServer.class);
    
    public static void main(String[] args) {
        logger.info("Démarrage du serveur d'authentification Wakfu...");
        
        // Initialiser la configuration
        loadConfiguration();
        
        // Connexion à la base de données
        initDatabase();
        
        // Démarrer le serveur réseau
        startNetworkServer();
        
        logger.info("Serveur d'authentification démarré avec succès !");
    }
    
    private static void loadConfiguration() {
        logger.info("Chargement de la configuration...");
        // TODO: Implémenter le chargement de la configuration
    }
    
    private static void initDatabase() {
        logger.info("Initialisation de la connexion à la base de données...");
        // TODO: Implémenter la connexion à la base de données
    }
    
    private static void startNetworkServer() {
        logger.info("Démarrage du serveur réseau...");
        // TODO: Implémenter le serveur réseau avec Netty
    }
}