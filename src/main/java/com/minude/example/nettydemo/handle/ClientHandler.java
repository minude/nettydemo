package com.minude.example.nettydemo.handle;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.InetSocketAddress;

/**
 * @author minude
 * @version 1.0
 * @date 2019/5/16 11:24
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        ByteBuf m = (ByteBuf) msg;
        byte[] data = new byte[m.readableBytes()];
        m.readBytes(data);
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        String message = new String(data);
        System.out.println("received message : " + message + "\nfrom: " + socketAddress.getHostString() + ":" + socketAddress.getPort());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("server channelReadComplete..");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (null != cause) {
            cause.printStackTrace();
        }
        if (null != ctx) {
            ctx.close();
        }
    }
}