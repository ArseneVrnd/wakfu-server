// Fichier à créer dans: database/src/main/java/com/wakfu/emulator/database/DatabaseManager.java
package com.wakfu.emulator.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static final Logger logger = LogManager.getLogger(DatabaseManager.class);

    private String url;
    private String username;
    private String password;
    private Connection connection;

    public DatabaseManager(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public boolean connect() {
        try {
            // Charger le driver JDBC
            Class.forName("org.postgresql.Driver");

            // Établir la connexion
            connection = DriverManager.getConnection(url, username, password);
            logger.info("Connexion à la base de données établie avec succès");
            return true;
        } catch (ClassNotFoundException e) {
            logger.error("Driver PostgreSQL non trouvé: {}", e.getMessage(), e);
            return false;
        } catch (SQLException e) {
            logger.error("Erreur lors de la connexion à la base de données: {}", e.getMessage(), e);
            return false;
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
                logger.info("Connexion à la base de données fermée");
            } catch (SQLException e) {
                logger.error("Erreur lors de la fermeture de la connexion: {}", e.getMessage(), e);
            }
        }
    }
}