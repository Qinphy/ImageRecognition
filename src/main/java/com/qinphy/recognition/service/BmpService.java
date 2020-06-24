package com.qinphy.recognition.service;

import com.qinphy.recognition.entity.Bmp;

import java.io.IOException;

/**
 * @author: Qinphy
 * @Description: Bmp涉及到的方法接口
 * @date: 2020/6/24 9:29
 */
public interface BmpService {
    void insert(String tableName, String rowKey, String colFamily, Bmp bmp) throws IOException;
}
