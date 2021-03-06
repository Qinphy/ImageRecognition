package com.qinphy.recognition.repository;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * @author: Qinphy
 * @Description: This Hadoop's HDFS HadoopFileSystem methods.
 * @date: 2020/6/23 16:05
 */
public class HadoopFileSystem {
    private Configuration conf;

    public HadoopFileSystem() {
        this.conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://192.168.137.120:9000");
    }

    /**
     * 判断文件/路径是否存在
     * @param path 文件/路径
     * @return true/false
     * @throws IOException
     */
    public boolean isExist(String path) throws IOException {
        FileSystem fs = FileSystem.get(conf);
        return fs.exists(new Path(path));
    }

    /**
     * 判断目录是否为空
     * @param path 目录路径
     * @return true/false
     * @throws IOException
     */
    public boolean isEmpty(String path) throws IOException {
        FileSystem fs = FileSystem.get(conf);
        Path dirPath = new Path(path);
        RemoteIterator<LocatedFileStatus> remoteIterator = fs.listFiles(dirPath, true);
        if (remoteIterator.hasNext()) {
            return false;
        }
        return true;
    }

    /**
     * 从本地上传文件到HDFS
     * @param localPath 本地路径
     * @param hdfsPath HDFS系统路径
     * @throws IOException
     */
    public void put(String localPath, String hdfsPath) throws IOException {
        Path local = new Path(localPath);
        Path hdfs = new Path(hdfsPath);
        FileSystem fs = FileSystem.get(conf);
        fs.copyFromLocalFile(false, true, local, hdfs);
    }

    /**
     * 从HDFS下载文件到本地
     * @param localPath 本地路径
     * @param hdfsPath HDFS文件路径
     * @throws IOException
     */
    public void get(String localPath, String hdfsPath) throws IOException {
        FileSystem fs = FileSystem.get(conf);
        Path local = new Path(localPath);
        Path hdfs = new Path(hdfsPath);
        fs.copyToLocalFile(hdfs, local);
    }

    /**
     * 追加文件
     * @param localPath 本地文件路径
     * @param hdfsPath HDFS系统文件路径
     * @throws IOException
     */
    public void append(String localPath, String hdfsPath) throws IOException {
        Path hdfs = new Path(hdfsPath);
        FileSystem fs = FileSystem.get(conf);
        FileInputStream inputStream = new FileInputStream(localPath);
        FSDataOutputStream outputStream = fs.append(hdfs);
        byte data[] = new byte[1024];
        int read = -1;
        while((read = inputStream.read(data)) > 0) {
            outputStream.write(data, 0, read);
        }
        outputStream.close();
    }

    /**
     * 在HDFS中创建文件夹
     * @param path HDFS中的路径
     * @return true/false 创建是否成功
     * @throws IOException
     */
    public boolean mkdir(String path) throws IOException {
        FileSystem fs = FileSystem.get(conf);
        Path hdfs = new Path(path);
        return fs.mkdirs(hdfs);
    }

    /**
     * 在HDFS中创建文件
     * @param path HDFS中的文件路径
     * @throws IOException
     */
    public void touchz(String path) throws IOException {
        Path hdfs = new Path(path);
        FileSystem fs = FileSystem.get(conf);
        FSDataOutputStream out = fs.create(hdfs);
        out.close();
    }

    /**
     * 删除目录或文件
     * @param path HDFS中的目录路径或文件路径
     * @return true/false 删除是否成功
     * @throws IOException
     */
    public boolean rm(String path) throws IOException {
        FileSystem fs = FileSystem.get(conf);
        Path hdfs = new Path(path);
        return fs.delete(hdfs, true);
    }

    /**
     * 读取HDFS中文件内容
     * @param hdfsFilePath hdfs文件路径
     * @return List<String>每一行的List
     */
    public List<String> cat(String hdfsFilePath) throws IOException {
        Path hdfsPath = new Path(hdfsFilePath);
        FileSystem fs = FileSystem.get(conf);
        FSDataInputStream in = fs.open(hdfsPath);
        BufferedReader d = new BufferedReader(new InputStreamReader(in));

        List<String> list = new ArrayList<String>();
        String line;
        while((line = d.readLine()) != null) {
            while(!Character.isUpperCase(line.charAt(0))
                    && !Character.isDigit(line.charAt(0))
                    && !Character.isLowerCase(line.charAt(0))) {
                line = line.substring(1);
            }
            list.add(line);
        }
        fs.close();
        return list;
    }
}
