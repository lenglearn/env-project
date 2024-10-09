package com.briup.smart.env;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.briup.smart.env.entity.Environment;
import com.briup.smart.env.server.DBStore;
import com.briup.smart.env.support.ConfigurationAware;
import com.briup.smart.env.support.PropertiesAware;
import com.briup.smart.env.util.Backup;
import com.briup.smart.env.util.Log;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Properties;

public class DBStoreImpl implements DBStore, PropertiesAware, ConfigurationAware {
    private Log log;
    private Backup bk;
    private String backupFileData;
    @Override
    public void saveDB(Collection<Environment> collection) throws Exception {
        log.info("开始入库");
        if (collection == null || collection.size() == 0){
            log.debug("集合为空无法入库");
            return;
        }
        try {
            Properties pro = new Properties();
            InputStream is = DBStoreImpl.class.getClassLoader().getResourceAsStream("druid.properties");
            pro.load(is);
            DataSource dataSource = DruidDataSourceFactory.createDataSource(pro);
            Connection conn = dataSource.getConnection();
            //修改为手动提交
            conn.setAutoCommit(false);
            PreparedStatement ps = null;
            int num = 1;
            String sql = "insert into env_detail_" + num + " values(?,?,?,?,?,?,?,?,?,?)";
            ps = conn.prepareStatement(sql);
            int count = 1;
            for (Environment e : collection) {
                int day = getDay(e.getGatherDate());
                if (num != day) {
                    ps.executeBatch();
                    ps.clearBatch();
                    num = day;
                    sql = "insert into env_detail_" + num + " values(?,?,?,?,?,?,?,?,?,?)";
                    ps = conn.prepareStatement(sql);
                }
                ps.setString(1, e.getName());
                ps.setString(2, e.getSrcId());
                ps.setString(3, e.getDesId());
                ps.setString(4, e.getDevId());
                ps.setString(5, e.getSensorAddress());
                ps.setInt(6, e.getCount());
                ps.setString(7, e.getCmd());
                ps.setFloat(8, e.getData());
                ps.setInt(9, e.getStatus());
                ps.setTimestamp(10, e.getGatherDate());
                ps.addBatch();
                //若处理的批处理数据1000条则先进行处理一批处理1000条
                if (count % 1001 == 0){
                    ps.executeBatch();
                }
            }
            ps.executeBatch();
            conn.commit();//只需要总的提交一次就可以
            conn.close();
        }catch (Exception e){
            log.debug("入库出现异常"+e.getMessage());
            log.info("入口异常");
            bk.store(backupFileData,collection, Backup.STORE_OVERRIDE);
        }
    }
    public static int getDay(Timestamp gatherDate) {
        String s = gatherDate.toString();
        String[] ss = s.split(" ");
        String[] sss = ss[0].split("-");
        return Integer.parseInt(sss[2]);
    }

    @Override
    public void setConfiguration(Configuration configuration) throws Exception {
        this.log = configuration.getLogger();
        this.bk = configuration.getBackup();
    }

    @Override
    public void init(Properties properties) throws Exception {
        this.backupFileData = properties.getProperty("backupFileData");
    }
}
