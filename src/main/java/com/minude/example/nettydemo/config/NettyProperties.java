package com.minude.example.nettydemo.config;

import io.netty.util.internal.StringUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Properties;

/**
 * @author minude
 * @version 1.0
 * @date 2019/5/15 15:06
 */
@Data
@NoArgsConstructor
public class NettyProperties {

    /**
     * TcpServer 监听的端口
     */
    private int tcpPort = 8905;
    /**
     * boss线程
     */
    private int bossCount = 1;
    /**
     * 工作线程
     */
    private int workerCount = 4;
    /**
     * 是否保持长连接
     */
    private boolean keepAlive = true;
    /**
     * 长连接最大数量
     */
    private int backlog = 1024;

    public NettyProperties(Properties properties) {
        String port = properties.getProperty("netty.port");
        String boss = properties.getProperty("netty.bossCount");
        String worker = properties.getProperty("netty.workerCount");
        String alive = properties.getProperty("netty.keepAlive");
        String backlogCount = properties.getProperty("netty.backlog");
        if (!StringUtil.isNullOrEmpty(port)) {
            this.tcpPort = Integer.valueOf(port);
        }
        if (!StringUtil.isNullOrEmpty(boss)) {
            this.bossCount = Integer.valueOf(boss);
        }
        if (!StringUtil.isNullOrEmpty(worker)) {
            this.workerCount = Integer.valueOf(worker);
        }
        if (!StringUtil.isNullOrEmpty(alive)) {
            this.keepAlive = Boolean.valueOf(alive);
        }
        if (!StringUtil.isNullOrEmpty(backlogCount)) {
            this.backlog = Integer.valueOf(backlogCount);
        }
    }
}
