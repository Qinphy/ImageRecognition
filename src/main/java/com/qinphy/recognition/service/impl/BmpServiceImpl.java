package com.qinphy.recognition.service.impl;

import com.qinphy.recognition.entity.Bmp;
import com.qinphy.recognition.repository.HBase;
import com.qinphy.recognition.repository.HadoopFileSystem;
import com.qinphy.recognition.repository.MapReduce;
import com.qinphy.recognition.service.BmpService;
import com.qinphy.recognition.util.Change;
import org.apache.hadoop.fs.Path;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author: Qinphy
 * @Description: bmp涉及的方法实现
 * @date: 2020/6/24 9:29
 */
@Service
public class BmpServiceImpl implements BmpService {
    // HDFS相关
    private HadoopFileSystem hadoopFileSystem = new HadoopFileSystem();
    private final String hdfsPath = "/user/qinphy/images/";

    // HBase相关
    private HBase hBase = new HBase();
    private final String tableName = "imageMR";
    private final String image = "image";
    private final String statistic = "counter";
    private final String width = "width";
    private final String height = "height";

    @Override
    public String uploadHDFS(String path) throws IOException {
        int index = path.lastIndexOf('/');
        String fileName = path.substring(index + 1);

        if (hadoopFileSystem.isExist(path)) {
            return "exist";
        }
        hadoopFileSystem.put(path, hdfsPath + fileName);
        return "success";
    }

    @Override
    public void insert(Bmp bmp) throws IOException {
        String rowKey = bmp.getName();
        byte[] data = Change.changeToByte(bmp.getData());
        byte[] counter = Change.changeToByte(bmp.getCounter());
        byte[] w = Change.changeToByte(bmp.getWidth());
        byte[] h = Change.changeToByte(bmp.getHeight());

        hBase.insert(tableName, rowKey, image, data);
        hBase.insert(tableName, rowKey, statistic, counter);
        hBase.insert(tableName, rowKey, width, w);
        hBase.insert(tableName, rowKey, height, h);
    }

    @Override
    public Bmp select(String rowKey) throws IOException {
        byte[] w = hBase.getCell(tableName, rowKey, width);
        byte[] h = hBase.getCell(tableName, rowKey, height);
        byte[] data = hBase.getCell(tableName, rowKey, image);
        byte[] counter = hBase.getCell(tableName, rowKey, statistic);

        int wid = Change.changeToInt(w);
        int hei = Change.changeToInt(h);
        int[][] imgData = Change.changeToInt(data, wid, hei);
        int[] count = Change.changeToInt(counter, 256);

        Bmp bmp = new Bmp(rowKey, imgData, count, wid, hei);
        return bmp;
    }

    @Override
    public String AllSearch(Bmp bmp) throws InterruptedException, IOException, ClassNotFoundException {
        String hdfs = MapReduce.AllSearch(bmp);
        List<String> list = hadoopFileSystem.cat(hdfs + "/part-r-00000");
        String name = list.get(0);
        System.out.println(name);
        hadoopFileSystem.rm(hdfs);
        return name;
    }

    @Override
    public List<String> PartSearch(Bmp bmp) throws InterruptedException, IOException, ClassNotFoundException {
        String hdfs = MapReduce.AllSearch(bmp);
        List<String> list = hadoopFileSystem.cat(hdfs + "/part-r-00000");
        hadoopFileSystem.rm(hdfs);
        return list;
    }
}
