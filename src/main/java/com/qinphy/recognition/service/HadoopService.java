package com.qinphy.recognition.service;

import java.io.IOException;

/**
 * @author: Qinphy
 * @Description: HDFS相关方法
 * @date: 2020/6/24 14:00
 */
public interface HadoopService {
    String uploadHDFS(String path) throws IOException;
}
