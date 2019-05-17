package com.minude.example.nettydemo.client;

import com.minude.example.nettydemo.handle.ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author minude
 * @version 1.0
 * @date 2019/5/16 11:18
 */
@Slf4j
public class NettyTcpClient {

    private int port;
    private String host;
    private SocketChannel socketChannel;
    private NioEventLoopGroup group;

    public NettyTcpClient(int port, String host) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        doConnect();
    }

    public void sendMessage(String msg) {

        if (socketChannel != null && socketChannel.isActive()) {
            socketChannel.writeAndFlush(Unpooled.wrappedBuffer(msg.getBytes(CharsetUtil.UTF_8))).addListener((ChannelFuture future) -> {
                if (future.isSuccess()) {
                    System.out.println("send success: " + msg);
                } else {
                    System.out.println("send failed: " + msg);
                }
            });
        } else {
            System.out.println("连接已断开");
        }
    }

    public void exit() {

        if (socketChannel != null) {
            if (!socketChannel.isActive()){
                System.out.println("bye");
                System.exit(0);
            }
            socketChannel.close();
            group.shutdownGracefully();
            System.out.println("already exit");
        }
    }

    public void doConnect() {

        Bootstrap b = new Bootstrap();
        group = new NioEventLoopGroup();
        b.channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .group(group).remoteAddress(host, port)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new StringDecoder());
                        socketChannel.pipeline().addLast(new ClientHandler());
                    }
                });
        b.connect().addListener((ChannelFuture f) -> {
            if (f.isSuccess()) {
                System.out.println("连接成功!");
                socketChannel = (SocketChannel) f.channel();
            } else {
                f.channel().eventLoop().schedule(this::doConnect, 10L, TimeUnit.SECONDS);
            }
        });

    }

}