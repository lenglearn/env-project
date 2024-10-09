package com.briup.smart.env;

import com.briup.smart.env.client.Gather;
import com.briup.smart.env.entity.Environment;
import com.briup.smart.env.support.ConfigurationAware;
import com.briup.smart.env.support.PropertiesAware;
import com.briup.smart.env.util.Backup;
import com.briup.smart.env.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class GatherImpl implements Gather, PropertiesAware, ConfigurationAware {
    //将其他对象的实现类对象封装成成员变量；
    private Log log ;
    private Backup bk;
    //源数据的文件名
    private String fileName;
    private String backupFileData;
    private String backupFileGather;
    @Override
    public Collection<Environment> gather() throws Exception {

        //使用list因为默认容量为10使用每次都需要扩容增加代码的运行时效
        List<Environment> list = new LinkedList<>();
        //F:\JP培训\资料文件\XM\env-project\trunk\1.采集模块\数据文件\data-file
        File file = new File(fileName);
        if (!file.exists()){
            throw new RuntimeException("文件不存在,无法进行采集");
        }
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
//        String str;
        //备份模块进行数据的记录下次重那处开始读
        Object obj = bk.load(backupFileGather, Backup.LOAD_UNREMOVE);
        Object datas = bk.load(backupFileData,Backup.LOAD_REMOVE);
        if (datas != null){
            list.addAll((List<Environment>) datas);
        }

        long num = new BufferedReader
                (new FileReader(fileName)).
                lines().count();

        log.debug("本次读取的行数"+num);
        long line = 0;
        if (obj != null){
            line = (long) obj;
        }
        log.info("本次跳过的行数"+line);
        br.lines().skip(line).forEach(str->{
            String[] strs = str.split("[|]");
            String srcId = strs[0];
            String desId = strs[1];
            String devId = strs[2];
            String sensorAddress = strs[3];
            int count = Integer.parseInt(strs[4]);
            String cmd = strs[5];
            int status = Integer.parseInt(strs[7]);
            Timestamp gatherDate = new Timestamp(Long.parseLong(strs[8]));
            if ("16".equals(strs[3])) {
                String s1 = strs[6].substring(0, 4);
                //温度
                float data1 = ((Integer.parseInt(s1, 16)) * (0.00268127F)) - 46.85F;
                Environment e1 = new Environment("温度数据", srcId, desId, devId, sensorAddress, count, cmd, status, data1, gatherDate);
                list.add(e1);
                //湿度
                String s2 = strs[6].substring(4, 8);
                float data2 = ((Integer.parseInt(s2, 16)) * (0.00268127F)) - 46.85F;
                Environment e2 = new Environment("湿度数据", srcId, desId, devId, sensorAddress, count, cmd, status, data2, gatherDate);
                list.add(e2);
            } else if ("256".equals(strs[3])) {
                String name = "光照数据";
                float data = Integer.parseInt(strs[6].substring(0, 4), 16);
                Environment e = new Environment(name, srcId, desId, devId, sensorAddress, count, cmd, status, data, gatherDate);
                list.add(e);
            } else if ("1280".equals(strs[3])) {
                String name = "二氧化碳数据";
                float data = Integer.parseInt(strs[6].substring(0, 4), 16);
                Environment e = new Environment(name, srcId, desId, devId, sensorAddress, count, cmd, status, data, gatherDate);
                list.add(e);
            } else {
                throw new RuntimeException("数据存在问题");
            }
        });
        bk.store(backupFileGather, num , Backup.STORE_OVERRIDE);
        return list;
    }

    @Override
    public void setConfiguration(Configuration configuration) throws Exception {
        this.log = configuration.getLogger();
        this.bk = configuration.getBackup();
    }

    @Override
    public void init(Properties properties) throws Exception {
        this.fileName = properties.getProperty("fileName");
        this.backupFileData = properties.getProperty("backupFileData");
        this.backupFileGather= properties.getProperty("backupFileGather");

    }
}
