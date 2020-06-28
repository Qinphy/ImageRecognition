package com.qinphy.recognition.util;

import com.qinphy.recognition.entity.Split;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Qinphy
 * @Description: int,String,byte的转换
 * @date: 2020/6/26 9:42
 */
public class Change {
    /**
     * String转byte[]
     * @param str 字符串
     * @return byte[]
     */
    public static byte[] changeToByte(String str) {
        return str.getBytes();
    }

    /**
     * byte[]转String
     * @param b byte[]
     * @return String
     */
    public static String changeToString(byte[] b) {
        String data = Bytes.toString(b);
        return data;
    }

    /**
     * int转byte[]
     * @param data int数据
     * @return byte[4]
     */
    public static byte[] changeToByte(int data) {
        byte[] b = new byte[4];
        b[3] = (byte)(data >> 24);
        b[2] = (byte)(data >> 16);
        b[1] = (byte)(data >> 8);
        b[0] = (byte)data;
        return b;
    }

    /**
     * int[]转byte[]
     * @param data int数组
     * @return byte[]
     */
    public static byte[] changeToByte(int[] data) {
        byte[] b = new byte[4 * data.length];
        for (int i = 0; i < data.length; i++) {
            byte[] a = changeToByte(data[i]);
            for (int j = 0; j < a.length; j++) {
                b[i * 4 + j] = a[j];
            }
        }
        return b;
    }

    /**
     * int[][]转byte[]
     * @param data int二维数组
     * @return byte[]
     */
    public static byte[] changeToByte(int[][] data) {
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

    /**
     * byte[4]转int
     * @param b byte[4]
     * @return int
     */
    public static int changeToInt(byte[] b) {
        int tmp1 = b[3] & 0xff << 24;
        int tmp2 = b[2] & 0xff << 16;
        int tmp3 = b[1] & 0xff << 8;
        int tmp4 = b[0] & 0xff;
        int num = tmp1 | tmp2 | tmp3 | tmp4;
        return num;
    }

    /**
     * byte[]转int[]
     * @param b byte数组
     * @param length int[]的长度(便于重载)
     * @return int[]
     */
    public static int[] changeToInt(byte[] b, int length) {
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

    /**
     * byte[]转int[][]
     * @param b byte[]
     * @param width int的宽度，列数
     * @param height int的高度，行数
     * @return int[][]
     */
    public static int[][] changeToInt(byte[] b, int width, int height) {
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

    /**
     * 拆分int[][]数组
     * @param data 待拆分的数组
     * @param width 需要拆分的宽度
     * @param height 需要拆分的高度
     * @return List<int[][]>每一块的队列
     */
    public static List<int[][]> getPart(int[][] data, int width, int height) {
        List<int[][]> list = new ArrayList<int[][]>();
        for (int i = 0; i < data.length - height; i++) {
            for (int j = 0; j < data[i].length - width; j++) {
                int[][] part = new int[height][width];
                for (int ii = 0; ii < height; ii++) {
                    for (int jj = 0; jj < width; jj++) {
                        part[ii][jj] = data[ii + i][jj + j];
                    }
                }
                list.add(part);
            }
        }
        return list;
    }

    public static boolean split(byte[] by, int width, int height, int w, int h, byte[] data) {
//        List<Split> list = new ArrayList<Split>();
        width *= 4;
        w *= 4;

        for (int i = 0; i < height - h + 1; i++) {
            for (int j = 0; j < width - w + 1; j += 4) {
                byte[] b = new byte[h * w];
                boolean f = true;
                for (int ii = 0; ii < h; ii++) {
                    for (int jj = 0; jj < w; jj++) {
                        if (data[ii * w + jj] != by[(i + ii) * width + jj + j]) {
                            f = false;
                            break;
                        }
                    }
                    if (!f) break;
                }
                if(f) {
                    return true;
                }
            }
        }
        return false;
    }
}
