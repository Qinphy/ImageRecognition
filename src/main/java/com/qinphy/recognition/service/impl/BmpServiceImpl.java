package com.qinphy.recognition.service.impl;

import com.qinphy.recognition.entity.Bmp;
import com.qinphy.recognition.repository.HBase;
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
    private HBase hBase;

    private static byte[] intToByte(int data) {
        byte[] b = new byte[4];
        b[3] = (byte)(data >> 24);
        b[2] = (byte)(data >> 16);
        b[1] = (byte)(data >> 8);
        b[0] = (byte)data;
        return b;
    }

    private byte[] changeToByte(String str) {
        return str.getBytes();
    }

    private byte[] changeToByte(int[] data) {
        byte[] b = new byte[4 * data.length];
        for (int i = 0; i < data.length; i++) {
            byte[] a = intToByte(data[i]);
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
                byte[] a = intToByte(data[i][j]);
                for (int k = 0; k < a.length; k++) {
                    b[(i * data[i].length + j) * 4 + k] = a[k];
                }
            }
        }
        return b;
    }

    @Override
    public void insert(String tableName, String rowKey, String colFamily, Bmp bmp) throws IOException {
        byte[] name = changeToByte(bmp.getName());
        byte[] data = changeToByte(bmp.getData());
        byte[] counter = changeToByte(bmp.getCounter());

        hBase.insert(tableName, rowKey, colFamily, name);
        hBase.insert(tableName, rowKey, colFamily, data);
        hBase.insert(tableName, rowKey, colFamily, counter);
    }
}
