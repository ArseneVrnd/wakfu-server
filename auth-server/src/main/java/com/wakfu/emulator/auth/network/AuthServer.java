package com.wakfu.emulator.auth;

import com.wakfu.emulator.database.AccountManager;
import com.wakfu.emulator.database.CharacterManager;
import com.wakfu.emulator.database.DatabaseManager;
import com.wakfu.emulator.auth.network.AuthChannelInitializer;
import com.wakfu.emulator.auth.network.ServerChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AuthServer {
    private static final Logger logger = LogManager.getLogger(AuthServer.class);
    private static AuthServer instance;

    private final AuthConfig config;
    private DatabaseManager databaseManager;
    private AccountManager accountManager;
    private CharacterManager characterManager;
    private WorldServerManager worldServerManager;

    public static AuthServer getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        logger.info("Démarrage du serveur d'authentification Wakfu...");

        instance = new AuthServer();
        instance.start();
    }

    public AuthServer() {
        this.config = loadConfiguration();
        this.worldServerManager = new WorldServerManager();
    }

    public void start() {
        // Initialiser la base de données
        if (!initDatabase()) {
            logger.error("Échec de l'initialisation de la base de données. Arrêt du serveur.");
            return;
        }

        // Démarrer le serveur réseau pour les clients
        startClientServer();

        // Démarrer le serveur réseau pour les serveurs de monde
        startServerConnectionServer();

        logger.info("Serveur d'authentification démarré avec succès !");
    }

    private AuthConfig loadConfiguration() {
        logger.info("Chargement de la configuration...");
        return AuthConfig.load();
    }

    private boolean initDatabase() {
        logger.info("Initialisation de la connexion à la base de données...");
        databaseManager = new DatabaseManager(
                config.getDatabaseUrl(),
                config.getDatabaseUsername(),
                config.getDatabasePassword());

        boolean connected = databaseManager.connect();

        if (connected) {
            accountManager = new AccountManager(databaseManager);
            characterManager = new CharacterManager(databaseManager);
            logger.info("Connexion à la base de données établie avec succès");
        }

        return connected;
    }

    private void startClientServer() {
        logger.info("Démarrage du serveur client sur le port {}...", config.getServerPort());

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        new Thread(() -> {
            try {
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .option(ChannelOption.SO_BACKLOG, 128)
                        .childOption(ChannelOption.SO_KEEPALIVE, true)
                        .childHandler(new AuthChannelInitializer());

                // Démarrer le serveur
                ChannelFuture future = bootstrap.bind(config.getServerPort()).sync();
                logger.info("Serveur client démarré sur le port {}", config.getServerPort());

                // Attendre la fermeture du serveur
                future.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                logger.error("Interruption du serveur client: {}", e.getMessage(), e);
            } finally {
                // Fermer proprement les ressources
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            }
        }).start();
    }

    private void startServerConnectionServer() {
        // Port pour les connexions des serveurs de monde
        int serverPort = config.getServerPort() + 1;

        logger.info("Démarrage du serveur de connexion des serveurs de monde sur le port {}...", serverPort);

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        new Thread(() -> {
            try {
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .option(ChannelOption.SO_BACKLOG, 128)
                        .childOption(ChannelOption.SO_KEEPALIVE, true)
                        .childHandler(new ServerChannelInitializer());

                // Démarrer le serveur
                ChannelFuture future = bootstrap.bind(serverPort).sync();
                logger.info("Serveur de connexion des serveurs de monde démarré sur le port {}", serverPort);

                // Attendre la fermeture du serveur
                future.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                logger.error("Interruption du serveur de connexion des serveurs de monde: {}", e.getMessage(), e);
            } finally {
                // Fermer proprement les ressources
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            }
        }).start();
    }

    public AuthConfig getConfig() {
        return config;
    }

    public AccountManager getAccountManager() {
        return accountManager;
    }

    public CharacterManager getCharacterManager() {
        return characterManager;
    }

    public WorldServerManager getWorldServerManager() {
        return worldServerManager;
    }
}