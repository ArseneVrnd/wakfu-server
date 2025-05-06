package com.wakfu.emulator.world.network;

import com.wakfu.emulator.protocol.handlers.MessageDecoder;
import com.wakfu.emulator.protocol.handlers.MessageEncoder;
import com.wakfu.emulator.protocol.messages.server.ServerRegistrationMessage;
import com.wakfu.emulator.protocol.messages.server.ServerStatusUpdateMessage;
import com.wakfu.emulator.world.WorldServer;
import com.wakfu.emulator.world.config.WorldConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AuthServerClient {
    private static final Logger logger = LogManager.getLogger(AuthServerClient.class);

    private final WorldConfig config;
    private final EventLoopGroup group;
    private Channel channel;
    private final ScheduledExecutorService scheduler;
    private boolean connected = false;

    public AuthServerClient(WorldConfig config) {
        this.config = config;
        this.group = new NioEventLoopGroup();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void connect() {
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(
                                    new MessageDecoder(),
                                    new MessageEncoder(),
                                    new AuthServerClientHandler()
                            );
                        }
                    });

            logger.info("Connexion au serveur d'authentification: {}:{}",
                    config.getAuthServerHost(), config.getAuthServerPort());

            ChannelFuture future = bootstrap.connect(config.getAuthServerHost(), config.getAuthServerPort());
            future.addListener(f -> {
                if (f.isSuccess()) {
                    channel = future.channel();
                    logger.info("Connexion établie avec le serveur d'authentification");
                    connected = true;
                    registerServer();

                    // Programmer les mises à jour régulières
                    scheduleStatusUpdates();
                } else {
                    logger.error("Échec de la connexion au serveur d'authentification: {}",
                            f.cause().getMessage());
                    // Réessayer après un délai
                    scheduleReconnection();
                }
            });

        } catch (Exception e) {
            logger.error("Erreur lors de la connexion au serveur d'authentification: {}",
                    e.getMessage(), e);
            scheduleReconnection();
        }
    }

    private void registerServer() {
        if (channel != null && channel.isActive()) {
            ServerRegistrationMessage registrationMessage = new ServerRegistrationMessage(
                    config.getServerId(),
                    config.getServerName(),
                    config.getServerKey()
            );

            channel.writeAndFlush(registrationMessage);
            logger.info("Enregistrement auprès du serveur d'authentification: {} (ID: {})",
                    config.getServerName(), config.getServerId());
        }
    }

    private void scheduleStatusUpdates() {
        scheduler.scheduleAtFixedRate(() -> {
            if (channel != null && channel.isActive()) {
                sendStatusUpdate();
            }
        }, 30, 30, TimeUnit.SECONDS);
    }

    private void scheduleReconnection() {
        connected = false;
        scheduler.schedule(this::connect, 10, TimeUnit.SECONDS);
    }

    public void sendStatusUpdate() {
        if (channel != null && channel.isActive()) {
            WorldServer worldServer = WorldServer.getInstance();
            ServerStatusUpdateMessage statusMessage = new ServerStatusUpdateMessage(
                    worldServer.getPlayerCount(),
                    worldServer.getStatus()
            );

            channel.writeAndFlush(statusMessage);
            logger.debug("Mise à jour du statut envoyée: {} joueurs, statut {}",
                    worldServer.getPlayerCount(), worldServer.getStatus());
        }
    }

    public void disconnect() {
        if (channel != null) {
            channel.close();
        }
        scheduler.shutdown();
        group.shutdownGracefully();
    }

    public boolean isConnected() {
        return connected;
    }
}