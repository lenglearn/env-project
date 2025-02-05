package com.briup.smart.env;

import com.briup.smart.env.util.Backup;

import java.io.*;

public class BackupImpl implements Backup {

    //读
    @Override
    public Object load(String filePath, boolean del) throws Exception {
        File file = new File(filePath);
        if (!file.exists()){
            return null;
        }
        InputStream is = new FileInputStream(filePath);
        ObjectInputStream ois = new ObjectInputStream(is);
        Object obj = ois.readObject();
        if (ois!=null) ois.close();
        if (is !=null) is.close();
        //流没有关闭资源不会释放，文件则删除不了
        if (del) {
            file.delete();
        }
        return obj;
    }

    //写
    @Override
    public void store(String filePath, Object obj, boolean append) throws Exception {
        OutputStream os = new FileOutputStream(filePath, append);
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject(obj);
        if (oos != null) oos.close();
        if (os!= null) os.close();
    }

}
