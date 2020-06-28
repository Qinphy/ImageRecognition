package com.qinphy.recognition.service;

import com.qinphy.recognition.entity.Bmp;

import java.io.IOException;
import java.util.List;

/**
 * @author: Qinphy
 * @Description: Bmp涉及到的方法接口
 * @date: 2020/6/24 9:29
 */
public interface BmpService {
    void insert(Bmp bmp) throws IOException;
    Bmp select(String rowKey) throws IOException;
    String uploadHDFS(String path) throws IOException;
    String AllSearch(Bmp bmp) throws InterruptedException, IOException, ClassNotFoundException;
    List<String> PartSearch(Bmp bmp) throws InterruptedException, IOException, ClassNotFoundException;
    List<String> VagueSearch(Bmp bmp) throws IOException, InterruptedException, ClassNotFoundException;
}
