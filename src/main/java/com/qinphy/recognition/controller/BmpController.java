package com.qinphy.recognition.controller;

import com.qinphy.recognition.service.HadoopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @author: Qinphy
 * @Description: bmp图片相关需求方法
 * @date: 2020/6/24 9:53
 */
@Controller
public class BmpController {
    @Autowired
    private HadoopService hadoopService;

    @RequestMapping("/uploads")
    @ResponseBody
    public String uploads(@RequestParam(value = "file") MultipartFile file) {
        String fileName = file.getOriginalFilename();
        File upFile = new File("/home/qinphy/Recognition/uploads/" + fileName);

        if (!upFile.getParentFile().exists()) upFile.mkdirs();

        try {
            file.transferTo(upFile);
            return "success";
        } catch (IOException e) {
            e.printStackTrace();
            return "fail";
        }
    }

    @RequestMapping("/addup")
    @ResponseBody
    public String uploadAdd(@RequestParam(value = "file") MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String filePath = "/home/qinphy/Recognition/images/" + fileName;
        File upFile = new File(filePath);

        if (!upFile.getParentFile().exists()) upFile.mkdirs();

        try {
            file.transferTo(upFile);
            String result = hadoopService.uploadHDFS(filePath);
            if (result.equals("success")) return "success";
            else return "fail";
        } catch (IOException e) {
            e.printStackTrace();
            return "fail";
        }
    }
}
