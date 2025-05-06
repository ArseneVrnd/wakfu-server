package com.wakfu.emulator.world.network;

import com.wakfu.emulator.protocol.handlers.MessageDecoder;
import com.wakfu.emulator.protocol.handlers.MessageEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class WorldChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) {
        ch.pipeline().addLast(
                new MessageDecoder(),
                new MessageEncoder(),
                new WorldClientHandler()
        );
    }
}