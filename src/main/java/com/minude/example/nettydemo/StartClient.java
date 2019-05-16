package com.minude.example.nettydemo;

import com.minude.example.nettydemo.client.NettyTcpClient;

import java.util.Scanner;

/**
 * @author minude
 * @version 1.0
 * @date 2019/5/16 11:59
 */
public class StartClient {

    public static void main(String[] args) {

        NettyTcpClient client = new NettyTcpClient(8906, "localhost");
        client.start();
        Scanner sc = new Scanner(System.in);
        while (sc.hasNext()) {

            String str = sc.next();
            client.sendMessage(str.getBytes());
            System.out.println("send: " + str);
        }
    }
}
