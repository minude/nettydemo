package com.minude.example.nettydemo.handle;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author minude
 * @version 1.0
 * @date 2019/5/15 15:14
 */
@Sharable
@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        System.out.println("new connection from: " + socketAddress.getHostString() + ":" + socketAddress.getPort());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        System.out.println("connection from: " + socketAddress.getHostString() + ":" + socketAddress.getPort() + " closed");
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        ByteBuf m = (ByteBuf) msg;
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        String message = m.toString(CharsetUtil.UTF_8);
        System.out.println("received message : " + message + "\nfrom: " + socketAddress.getHostString() + ":" + socketAddress.getPort());
        ctx.write(Unpooled.wrappedBuffer(("message has been processed: " + message).getBytes()));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

        ctx.flush();
        System.out.println("server channelReadComplete..");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        log.error("server occur exception:" + cause.getMessage());
        cause.printStackTrace();
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        System.out.println("client from " + socketAddress.getHostString() + ":" + socketAddress.getPort() + "has been closed");
        ctx.close(); // 关闭发生异常的连接
    }
}
