package com.minude.example.nettydemo.server;

import com.minude.example.nettydemo.config.NettyProperties;
import com.minude.example.nettydemo.handle.ServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author minude
 * @version 1.0
 * @date 2019/5/15 15:15
 */
@Slf4j
public class NettyTcpServer {

    private static ServerSocketChannel serverSocketChannel;

    public static void run(NettyProperties properties) {

        // 创建mainReactor
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(properties.getBossCount());
        // 创建工作线程组
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(properties.getWorkerCount());
        final ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                // 组装NioEventLoopGroup
                .group(bossGroup, workerGroup)
                // 设置channel类型为NIO类型
                .channel(NioServerSocketChannel.class)
                // 设置连接配置参数, 长连接的最大数量
                .option(ChannelOption.SO_BACKLOG, properties.getBacklog())
                .childOption(ChannelOption.SO_KEEPALIVE, properties.isKeepAlive())
                .childOption(ChannelOption.TCP_NODELAY, true)
                // 配置入站、出站事件handler
                .childHandler(new ChannelInitializer<NioSocketChannel>() {

                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        // 配置入站、出站事件channel
                        ch.pipeline().addLast(new ServerHandler());
                    }
                });
        // 绑定端口，同步等待成功
        int port = properties.getTcpPort();
        ChannelFuture future;
        try {
            future = serverBootstrap.bind(port).sync();
            if (future.isSuccess()) {
                serverSocketChannel = (ServerSocketChannel) future.channel();
                log.info("server startup success on port: {}", port);
            } else {
                log.error("server startup failed on port: {}", port);
            }
            // 等待服务监听端口关闭, 这里会将线程阻塞
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 优雅地退出，释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * group sending
     *
     * @param msg the message you want to send
     */
    public static void sendMsg(Object msg) {

        if (Objects.isNull(serverSocketChannel)) {
            throw new IllegalStateException("serverSocketChannel is null");
        } else {
            serverSocketChannel.writeAndFlush(msg);
        }
    }
}
