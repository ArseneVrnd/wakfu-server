package com.wakfu.emulator.world;

import com.wakfu.emulator.database.CharacterManager;
import com.wakfu.emulator.database.DatabaseManager;
import com.wakfu.emulator.world.auth.AuthTokenManager;
import com.wakfu.emulator.world.config.WorldConfig;
import com.wakfu.emulator.world.game.MapManager;
import com.wakfu.emulator.world.game.PlayerManager;
import com.wakfu.emulator.world.network.AuthServerClient;
import com.wakfu.emulator.world.network.WorldChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicInteger;

public class WorldServer {
    private static final Logger logger = LogManager.getLogger(WorldServer.class);
    private static WorldServer instance;

    private final WorldConfig config;
    private DatabaseManager databaseManager;
    private AuthServerClient authClient;
    private final AtomicInteger playerCount = new AtomicInteger(0);
    private int status = 1; // 1 = online
    private boolean registered = false;

    // Gestionnaires
    private AuthTokenManager authTokenManager;
    private CharacterManager characterManager;
    private MapManager mapManager;
    private PlayerManager playerManager;

    public static WorldServer getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        logger.info("Démarrage du serveur de monde Wakfu...");

        instance = new WorldServer();
        instance.start();
    }

    public WorldServer() {
        this.config = loadConfiguration();
    }

    public void start() {
        // Initialiser la base de données
        if (!initDatabase()) {
            logger.error("Échec de l'initialisation de la base de données. Arrêt du serveur.");
            return;
        }

        // Initialiser les gestionnaires
        initManagers();

        // Se connecter au serveur d'authentification
        connectToAuthServer();

        // Démarrer le serveur réseau
        startNetworkServer();

        logger.info("Serveur de monde démarré avec succès !");
    }

    private WorldConfig loadConfiguration() {
        logger.info("Chargement de la configuration...");
        return WorldConfig.load();
    }

    private boolean initDatabase() {
        logger.info("Initialisation de la connexion à la base de données...");
        databaseManager = new DatabaseManager(
                config.getDatabaseUrl(),
                config.getDatabaseUsername(),
                config.getDatabasePassword());

        boolean connected = databaseManager.connect();

        if (connected) {
            logger.info("Connexion à la base de données établie avec succès");
        }

        return connected;
    }

    private void initManagers() {
        logger.info("Initialisation des gestionnaires...");

        // Gestionnaire de jetons d'authentification
        authTokenManager = new AuthTokenManager();

        // Gestionnaire de personnages
        characterManager = new CharacterManager(databaseManager);

        // Gestionnaire de cartes
        mapManager = new MapManager();

        // Gestionnaire de joueurs
        playerManager = new PlayerManager(characterManager, mapManager);
    }

    private void connectToAuthServer() {
        logger.info("Connexion au serveur d'authentification...");
        authClient = new AuthServerClient(config);
        authClient.connect();
    }

    private void startNetworkServer() {
        logger.info("Démarrage du serveur réseau sur le port {}...", config.getServerPort());

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new WorldChannelInitializer());

            // Démarrer le serveur
            ChannelFuture future = bootstrap.bind(config.getServerPort()).sync();
            logger.info("Serveur réseau démarré sur le port {}", config.getServerPort());

            // Attendre la fermeture du serveur
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("Interruption du serveur: {}", e.getMessage(), e);
        } finally {
            // Fermer proprement les ressources
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();

            if (authClient != null) {
                authClient.disconnect();
            }

            if (databaseManager != null) {
                databaseManager.close();
            }

            if (authTokenManager != null) {
                authTokenManager.shutdown();
            }
        }
    }

    public void incrementPlayerCount() {
        int count = playerCount.incrementAndGet();
        logger.info("Joueur connecté. Total: {}", count);

        if (authClient != null && authClient.isConnected()) {
            authClient.sendStatusUpdate();
        }
    }

    public void decrementPlayerCount() {
        int count = playerCount.decrementAndGet();
        logger.info("Joueur déconnecté. Total: {}", count);

        if (authClient != null && authClient.isConnected()) {
            authClient.sendStatusUpdate();
        }
    }

    public int getPlayerCount() {
        return playerCount.get();
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
        logger.info("Statut du serveur mis à jour: {}", status);

        if (authClient != null && authClient.isConnected()) {
            authClient.sendStatusUpdate();
        }
    }

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    public WorldConfig getConfig() {
        return config;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public AuthServerClient getAuthClient() {
        return authClient;
    }

    public AuthTokenManager getAuthTokenManager() {
        return authTokenManager;
    }

    public CharacterManager getCharacterManager() {
        return characterManager;
    }

    public MapManager getMapManager() {
        return mapManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }
}