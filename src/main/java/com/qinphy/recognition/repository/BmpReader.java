package com.qinphy.recognition.repository;

import com.qinphy.recognition.entity.Bmp;

import java.awt.*;
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

        byte[] a = new byte[4];
        bis.read(a);
        byte[] b = new byte[4];
        bis.read(b);

        int width = byteToInt(a);
        int height = byteToInt(b);

        int[][] data = new int[height][width];

        int skiper = 0;
        if (width * 3 / 4 != 0) {
            skiper = 4 - width * 3 % 4;
        }

        bis.skip(28);

        int[] counter = new int[256];
        for(int i = 0; i < counter.length; i++) counter[i] = 0;

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                int blue = bis.read();
                int green = bis.read();
                int red = bis.read();
                Color color = new Color(red, green, blue);
                data[i][j] = color.getRGB();
                counter[data[i][j]]++;
            }
            if (skiper != 0) bis.skip(skiper);
        }

        bis.close();

        Bmp bmp = new Bmp(name, data, counter);
        return bmp;
    }
}
