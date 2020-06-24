package com.qinphy.recognition.service.impl;

import com.qinphy.recognition.repository.HadoopFileSystem;
import com.qinphy.recognition.service.HadoopService;
import org.apache.hadoop.conf.Configuration;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author: Qinphy
 * @Description: HDFS相关方法实现
 * @date: 2020/6/24 14:01
 */
@Service
public class HadoopServiceImpl implements HadoopService {
    private HadoopFileSystem hadoopFileSystem;
    private final String hdfsPath = "/user/qinphy/images/";

    
    @Override
    public String uploadHDFS(String path) throws IOException {
        Configuration conf  = new Configuration();
        hadoopFileSystem = new HadoopFileSystem(conf);
        if (hadoopFileSystem.isEmpty(path)) {
            return "exist";
        }
        hadoopFileSystem.put(path, hdfsPath);
        return "success";
    }
}
