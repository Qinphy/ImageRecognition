package com.qinphy.recognition.repository;

import com.qinphy.recognition.entity.Bmp;
import com.qinphy.recognition.util.Change;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author: Qinphy
 * @Description: 读取图片信息
 * @date: 2020/6/24 8:59
 */
public class BmpReader {

    public static int byteToInt(byte[] b) {
        int tmp1 = b[3] & 0xff;
        int tmp2 = b[2] & 0xff;
        int tmp3 = b[1] & 0xff;
        int tmp4 = b[0] & 0xff;
        int num = tmp1 << 24 | tmp2 << 16 | tmp3 << 8 | tmp4;
        return num;
    }

    /**
     * 读取图片
     * @param path 图片路径
     * @throws IOException
     */
    public static Bmp readBmp(String path) throws IOException {
        int index = path.lastIndexOf('/');
        String name = path.substring(index + 1);

        FileInputStream fis = new FileInputStream(path);
        BufferedInputStream bis = new BufferedInputStream(fis);
        bis.skip(18);
        byte[] b = new byte[4];
        bis.read(b);
        byte[] b2 = new byte[4];
        bis.read(b2);

        int width = byteToInt(b);
        int height = byteToInt(b2);
        int[][] data = new int[height][width];

        bis.skip(28 + 1024);

        int[] counter = new int[256];
        for (int i = 0; i < 256; i++) counter[i] = 0;

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                int color = bis.read();
                counter[color]++;
                data[i][j] = color;
            }
        }

        Bmp bmp = new Bmp(name, data, counter, width, height);
        return bmp;
    }
}
