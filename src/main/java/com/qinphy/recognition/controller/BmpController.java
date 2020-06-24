package com.qinphy.recognition.controller;

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

    @RequestMapping("/upload")
    @ResponseBody
    public String uploads(@RequestParam(value = "file") MultipartFile file) {
        String fileName = file.getOriginalFilename();
        File upFile = new File("/uploads/" + fileName);

        if (!upFile.getParentFile().exists()) upFile.mkdirs();

        try {
            file.transferTo(upFile);
            return "success";
        } catch (IOException e) {
            e.printStackTrace();
            return "fail";
        }
    }
}
