// Fichier à créer dans: database/src/main/java/com/wakfu/emulator/database/AccountManager.java
package com.wakfu.emulator.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class AccountManager {
    private static final Logger logger = LogManager.getLogger(AccountManager.class);
    private DatabaseManager databaseManager;

    public AccountManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public boolean authenticate(String username, String password) {
        String query = "SELECT password FROM accounts WHERE username = ? AND is_banned = false";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    String hashedPassword = hashPassword(password);

                    return storedPassword.equals(hashedPassword);
                }
            }

            return false;
        } catch (SQLException e) {
            logger.error("Erreur lors de l'authentification: {}", e.getMessage(), e);
            return false;
        }
    }

    public boolean createAccount(String username, String password, String email) {
        String query = "INSERT INTO accounts (username, password, email) VALUES (?, ?, ?)";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, hashPassword(password));
            stmt.setString(3, email);

            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.error("Erreur lors de la création du compte: {}", e.getMessage(), e);
            return false;
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            logger.error("Algorithme de hachage non disponible: {}", e.getMessage(), e);
            return password; // Fallback non sécurisé, uniquement pour éviter les erreurs
        }
    }

    public void updateLastLogin(String username) {
        String query = "UPDATE accounts SET last_login = CURRENT_TIMESTAMP WHERE username = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("Erreur lors de la mise à jour de la dernière connexion: {}", e.getMessage(), e);
        }
    }
}