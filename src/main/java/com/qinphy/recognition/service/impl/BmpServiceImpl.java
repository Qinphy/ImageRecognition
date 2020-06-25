package com.qinphy.recognition.service.impl;

import com.qinphy.recognition.entity.Bmp;
import com.qinphy.recognition.repository.HBase;
import com.qinphy.recognition.repository.HadoopFileSystem;
import com.qinphy.recognition.service.BmpService;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author: Qinphy
 * @Description: bmp涉及的方法实现
 * @date: 2020/6/24 9:29
 */
@Service
public class BmpServiceImpl implements BmpService {
    // HDFS相关
    private HadoopFileSystem hadoopFileSystem;
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

        hadoopFileSystem = new HadoopFileSystem();
        if (hadoopFileSystem.isExist(path)) {
            return "exist";
        }
        hadoopFileSystem.put(path, hdfsPath + fileName);
        return "success";
    }

    @Override
    public void insert(Bmp bmp) throws IOException {
        String rowKey = bmp.getName();
        byte[] data = changeToByte(bmp.getData());
        byte[] counter = changeToByte(bmp.getCounter());
        byte[] w = changeToByte(bmp.getWidth());
        byte[] h = changeToByte(bmp.getHeight());

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

        int wid = changeToInt(w);
        int hei = changeToInt(h);
        int[][] imgData = changeToInt(data, wid, hei);
        int[] count = changeToInt(counter, 256);

        Bmp bmp = new Bmp(rowKey, imgData, count, wid, hei);
        return bmp;
    }

    private byte[] changeToByte(String str) {
        return str.getBytes();
    }

    private static byte[] changeToByte(int data) {
        byte[] b = new byte[4];
        b[3] = (byte)(data >> 24);
        b[2] = (byte)(data >> 16);
        b[1] = (byte)(data >> 8);
        b[0] = (byte)data;
        return b;
    }

    private byte[] changeToByte(int[] data) {
        byte[] b = new byte[4 * data.length];
        for (int i = 0; i < data.length; i++) {
            byte[] a = changeToByte(data[i]);
            for (int j = 0; j < a.length; j++) {
                b[i * 4 + j] = a[j];
            }
        }
        return b;
    }

    private byte[] changeToByte(int[][] data) {
        byte[] b = new byte[4 * data.length * data[0].length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                byte[] a = changeToByte(data[i][j]);
                for (int k = 0; k < a.length; k++) {
                    b[(i * data[i].length + j) * 4 + k] = a[k];
                }
            }
        }
        return b;
    }

    private int changeToInt(byte[] b) {
        int tmp1 = b[3] & 0xff << 24;
        int tmp2 = b[2] & 0xff << 16;
        int tmp3 = b[1] & 0xff << 8;
        int tmp4 = b[0] & 0xff;
        int num = tmp1 | tmp2 | tmp3 | tmp4;
        return num;
    }

    private int[] changeToInt(byte[] b, int length) {
        int[] data = new int[length];
        int cnt = 0;
        for (int i = 0; i < b.length;) {
            byte[] a = new byte[4];
            for (int j = 0; j < a.length; j++, i++) {
                a[j] = b[i];
            }
            data[cnt++] = changeToInt(a);
        }
        return data;
    }

    private int[][] changeToInt(byte[] b, int width, int height) {
        int[][] data = new int[height][width];

        int k = 0;
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                byte[] a = new byte[4];
                for (int l = 0; l < 4; l++) {
                    a[l] = b[k++];
                }
                data[i][j] = changeToInt(a);
            }
        }

        return data;
    }
}
