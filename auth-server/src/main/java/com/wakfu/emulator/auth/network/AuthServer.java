// Fichier à modifier: auth-server/src/main/java/com/wakfu/emulator/auth/AuthServer.java
package com.wakfu.emulator.auth;

import com.wakfu.emulator.database.AccountManager;
import com.wakfu.emulator.database.DatabaseManager;
import com.wakfu.emulator.protocol.handlers.MessageDecoder;
import com.wakfu.emulator.protocol.handlers.MessageEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AuthServer {
    // Une seule méthode main
    public static void main(String[] args) {
        logger.info("Démarrage du serveur d'authentification Wakfu...");

        instance = new AuthServer();
        instance.start();
    }


    public static void main(String[] args) {
        logger.info("Démarrage du serveur d'authentification Wakfu...");

        AuthServer server = new AuthServer();
        server.start();
    }

    public void start() {
        // Charger la configuration
        config = loadConfiguration();

        // Initialiser la base de données
        if (!initDatabase()) {
            logger.error("Échec de l'initialisation de la base de données. Arrêt du serveur.");
            return;
        }

        // Démarrer le serveur réseau
        startNetworkServer();

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
            logger.info("Connexion à la base de données établie avec succès");
        }

        return connected;
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
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(
                                    new MessageDecoder(),
                                    new MessageEncoder(),
                                    new AuthClientHandler()
                            );
                        }
                    });

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

            if (databaseManager != null) {
                databaseManager.close();
            }
        }
    }
}