package com.briup.smart.env;

import com.briup.smart.env.client.Client;
import com.briup.smart.env.client.Gather;
import com.briup.smart.env.server.DBStore;
import com.briup.smart.env.server.Server;
import com.briup.smart.env.support.ConfigurationAware;
import com.briup.smart.env.support.PropertiesAware;
import com.briup.smart.env.util.Backup;
import com.briup.smart.env.util.Log;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ConfigurationImpl implements Configuration {
    private static Map<String, Object> modules;
    //保存每个模块的配置信息
    private static Properties properties;

    static {
        modules = new HashMap<>();
        properties = new Properties();
        SAXReader reader = new SAXReader();
        InputStream is = ConfigurationImpl.class.getClassLoader().getResourceAsStream("config.xml");
        try {
            Document ele = reader.read(is);
            Element root = ele.getRootElement();
            List<Element> elements1 = root.elements();
            for (Element element : elements1) {
                String name = element.getName();
                String url = element.attributeValue("className");
                Class<?> aClass = Class.forName(url);
                Constructor constructor = aClass.getConstructor();
                Object obj = constructor.newInstance();
                modules.put(name, obj);
                List<Element> child = element.elements();
                for (Element e : child) {
                    String childName = e.getName();
                    System.out.println(childName);
                    String values = e.getText();
                    System.out.println(values);
                    properties.setProperty(childName, values);
                    System.out.println("-----------------------------------");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            ConfigurationImpl con = ConfigurationImpl.class.newInstance();
            modules.forEach((k,v)->{
                try {
                    if (v instanceof ConfigurationAware){
                        ((ConfigurationAware)v).setConfiguration(con);
                    }
                    if (v instanceof PropertiesAware){
                        ((PropertiesAware)v).init(properties);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }catch (Exception e){
            throw new RuntimeException();
        }
    }
 /*  @Test
    public void method() throws Exception {
        Configuration c = new ConfigurationImpl();
        String s = "F:\\资料文件\\XM\\env-project\\trunk\\1.采集模块\\数据文件\\data-file";
       FileReader f = new FileReader(s);
       System.out.println(f.read());
    }*/


    @Override
    public Log getLogger() throws Exception {
        return (Log) modules.get("log");
    }

    @Override
    public Server getServer() throws Exception {
        return (Server) modules.get("server");
    }

    @Override
    public Client getClient() throws Exception {
        return (Client) modules.get("client");
    }

    @Override
    public DBStore getDbStore() throws Exception {
        return (DBStore) modules.get("dbStore");
    }

    @Override
    public Gather getGather() throws Exception {
        return (Gather) modules.get("gather");
    }

    @Override
    public Backup getBackup() throws Exception {
        return (Backup)modules.get("backUp");
    }
}
