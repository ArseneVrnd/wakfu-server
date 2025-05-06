package com.wakfu.emulator.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class AccountManager {
    private static final Logger logger = LogManager.getLogger(AccountManager.class);
    private final DatabaseManager databaseManager;

    public AccountManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public boolean authenticate(String username, String password) {
        String sql = "SELECT * FROM accounts WHERE username = ? AND password = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password); // Note: Dans une application réelle, utilisez un hachage sécurisé

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de l'authentification: {}", e.getMessage(), e);
            return false;
        }
    }

    public int getAccountId(String username) {
        String sql = "SELECT id FROM accounts WHERE username = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération de l'ID du compte: {}", e.getMessage(), e);
        }

        return -1;
    }

    public void updateLastLogin(String username) {
        String sql = "UPDATE accounts SET last_login = CURRENT_TIMESTAMP WHERE username = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("Erreur lors de la mise à jour de la date de dernière connexion: {}", e.getMessage(), e);
        }
    }
}