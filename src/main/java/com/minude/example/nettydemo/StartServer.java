package com.minude.example.nettydemo;

import com.minude.example.nettydemo.config.NettyProperties;
import com.minude.example.nettydemo.server.NettyTcpServer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author minude
 * @version 1.0
 * @date 2019/5/15 15:03
 */
public class StartServer {

    public static void main(String[] args) throws IOException {

        InputStream in = StartServer.class.getClassLoader().getResourceAsStream("netty.properties");
        Properties p = new Properties();
        p.load(in);
        NettyTcpServer.run(new NettyProperties(p));
    }
}
