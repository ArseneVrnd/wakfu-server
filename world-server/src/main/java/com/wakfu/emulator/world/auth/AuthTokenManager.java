package com.wakfu.emulator.world.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AuthTokenManager {
    private static final Logger logger = LogManager.getLogger(AuthTokenManager.class);

    // Stockage des jetons d'authentification avec les informations associées
    private final Map<String, AuthTokenInfo> tokens = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static class AuthTokenInfo {
        private final int accountId;
        private final int characterId;
        private final long expirationTime;

        public AuthTokenInfo(int accountId, int characterId, long expirationTime) {
            this.accountId = accountId;
            this.characterId = characterId;
            this.expirationTime = expirationTime;
        }

        public int getAccountId() {
            return accountId;
        }

        public int getCharacterId() {
            return characterId;
        }

        public long getExpirationTime() {
            return expirationTime;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }
    }

    public AuthTokenManager() {
        // Nettoyer les jetons expirés toutes les minutes
        scheduler.scheduleAtFixedRate(this::cleanupExpiredTokens, 1, 1, TimeUnit.MINUTES);
    }

    public void registerToken(String token, int accountId, int characterId) {
        // Les jetons expirent après 5 minutes
        long expirationTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5);
        tokens.put(token, new AuthTokenInfo(accountId, characterId, expirationTime));

        logger.info("Jeton d'authentification enregistré pour le compte {} et le personnage {}",
                accountId, characterId);
    }

    public AuthTokenInfo validateToken(String token) {
        AuthTokenInfo tokenInfo = tokens.get(token);

        if (tokenInfo == null) {
            logger.warn("Tentative d'utilisation d'un jeton d'authentification inexistant");
            return null;
        }

        if (tokenInfo.isExpired()) {
            logger.warn("Tentative d'utilisation d'un jeton d'authentification expiré");
            tokens.remove(token);
            return null;
        }

        // Supprimer le jeton après utilisation
        tokens.remove(token);

        logger.info("Jeton d'authentification validé pour le compte {} et le personnage {}",
                tokenInfo.getAccountId(), tokenInfo.getCharacterId());

        return tokenInfo;
    }

    private void cleanupExpiredTokens() {
        long now = System.currentTimeMillis();
        int count = 0;

        for (Map.Entry<String, AuthTokenInfo> entry : tokens.entrySet()) {
            if (entry.getValue().getExpirationTime() < now) {
                tokens.remove(entry.getKey());
                count++;
            }
        }

        if (count > 0) {
            logger.debug("{} jetons d'authentification expirés supprimés", count);
        }
    }

    public void shutdown() {
        scheduler.shutdown();
    }
}