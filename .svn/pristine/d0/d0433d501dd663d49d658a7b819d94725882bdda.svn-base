package com.briup.smart.env;

import com.briup.smart.env.client.Client;
import com.briup.smart.env.entity.Environment;
import com.briup.smart.env.support.ConfigurationAware;
import com.briup.smart.env.support.PropertiesAware;
import com.briup.smart.env.util.Log;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.Properties;

public class ClientImpl implements Client, ConfigurationAware, PropertiesAware {
    private Log log;
    private String host;
    private int port;

    @Override
    public void send(Collection<Environment> collection) throws Exception {
        //创建日志对象
        Socket client = new Socket(host,port);
        log.debug("开启客户端");
        OutputStream os = client.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject(collection);
        client.close();
    }

    @Override
    public void setConfiguration(Configuration configuration) throws Exception {
        this.log = configuration.getLogger();
    }

    @Override
    public void init(Properties properties) throws Exception {
        this.host = properties.getProperty("host");
        this.port = Integer.parseInt(properties.getProperty("port"));
    }
    public String get(){
        return this.host;
    }
}