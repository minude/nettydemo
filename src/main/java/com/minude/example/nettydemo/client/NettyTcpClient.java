package com.minude.example.nettydemo.client;

import com.minude.example.nettydemo.handle.ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

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

    public NettyTcpClient(int port, String host) {
        this.host = host;
        this.port = port;
    }

    public void start() {

        Thread thread = new Thread(() -> {
            EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class)
                    // 保持连接
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    // 有数据立即发送
                    .option(ChannelOption.TCP_NODELAY, true)
                    // 绑定处理group
                    .group(eventLoopGroup).remoteAddress(host, port)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new ClientHandler());
                        }
                    });
            // 进行连接
            ChannelFuture future;
            try {
                future = bootstrap.connect(host, port).sync();
                // 判断是否连接成功
                if (future.isSuccess()) {
                    // 得到管道，便于通信
                    socketChannel = (SocketChannel) future.channel();
                    log.info("客户端开启成功...");
                } else {
                    log.error("客户端开启失败...");
                }
                // 等待客户端链路关闭，这里会将线程阻塞
                future.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                //优雅地退出，释放相关资源
                eventLoopGroup.shutdownGracefully();
            }
        });
        thread.start();
    }

    public void sendMessage(byte[] msg) {
        if (socketChannel != null) {
            socketChannel.writeAndFlush(Unpooled.wrappedBuffer(msg));
        }
    }
}