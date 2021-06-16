package com.alipay.pojo;

import lombok.Data;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * includes host ip and name
 * @author linglan.xr
 * @version 1.0
 * @date 2021/6/9 下午12:09
 */
@Data
public class Host {
    private String hostAddress;
    private String hostName;

    public Host(){
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return;
        }
        this.hostAddress = address.getHostAddress();
        this.hostName = address.getHostName();
    }
}
