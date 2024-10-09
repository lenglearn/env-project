package com.briup.smart.env;

import com.briup.smart.env.entity.Environment;
import com.briup.smart.env.server.DBStore;
import com.briup.smart.env.server.Server;
import com.briup.smart.env.support.ConfigurationAware;
import com.briup.smart.env.support.PropertiesAware;
import com.briup.smart.env.util.Log;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerImpl implements Server, ConfigurationAware, PropertiesAware {
    private Log log;
    private DBStore dbStore;

    private ServerSocket server;
    private boolean flag = true;
    private ExecutorService service;

    private int receivePort;
    private int shutDownPort;

    @Override
    public void receive() throws Exception {
        this.service = Executors.newFixedThreadPool(5);
        this.server = new ServerSocket(receivePort);
        while (flag) {
            System.out.println("测试数字端口号"+receivePort);
            Socket so = server.accept();
            System.out.println("连接成功");
            service.submit(new Runnable() {
                @Override
                public void run() {
                    synchronized (ServerImpl.class) {
                        try {
                            InputStream is = so.getInputStream();
                            ObjectInputStream ois = new ObjectInputStream(is);
                            Object o = ois.readObject();
                            List<Environment> list = null;

                            if (o instanceof List) {
                                list = (List) o;
                            }
                            try {
                                dbStore.saveDB(list);
                            }catch (Exception e){
                                log.error("入库代码出现异常");
                            }
                            for (Environment e : list) {
                                System.out.println(e);
                            }
                            System.out.println(list.size());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                    }
                }
            });
        }
        service.shutdown();
    }

    @Override
    public void shutdown() throws Exception {
        Runnable r = () -> {
            try {
                ServerSocket serverDown = new ServerSocket(shutDownPort);
                serverDown.accept();
                log.info("服务器即将关闭");
                server.close();
                flag = false;
                if (serverDown!=null) serverDown.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        new Thread(r).start();
    }

    @Override
    public void setConfiguration(Configuration configuration) throws Exception {
        this.log = configuration.getLogger();
        this.dbStore = configuration.getDbStore();
    }

    @Override
    public void init(Properties properties) throws Exception {
        this.receivePort = Integer.parseInt(properties.getProperty("receivePort"));
        this.shutDownPort = Integer.parseInt(properties.getProperty("shutDownPort"));
    }

}
