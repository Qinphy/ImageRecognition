package com.qinphy.recognition.controller;

import com.qinphy.recognition.entity.Bmp;
import com.qinphy.recognition.repository.BmpReader;
import com.qinphy.recognition.service.BmpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author: Qinphy
 * @Description: bmp图片相关需求方法
 * @date: 2020/6/24 9:53
 */
@Controller
public class BmpController {
    @Autowired
    private BmpService bmpService;
    private final String imgPath = "/home/qinphy/Recognition/images/";

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
        String filePath = imgPath + fileName;
        File upFile = new File(filePath);
        if (!upFile.getParentFile().exists()) upFile.mkdirs();


        try {
            file.transferTo(upFile);
            System.out.println("upload to linux!");
            Bmp bmp = BmpReader.readBmp(filePath);
            String result = bmpService.uploadHDFS(filePath);
            System.out.println("upload to HDFS!");
            bmpService.insert(bmp);
            System.out.println("upload to HBase!");
            if (result.equals("success")) return fileName;
            else return "fail";
        } catch (IOException e) {
            e.printStackTrace();
            return "fail";
        }
    }

    @RequestMapping("/allSearch/{name}")
    @ResponseBody
    public String AllSaerch(@PathVariable("name") String name) {
        String filePath = imgPath + name;
        try {
            Bmp bmp = BmpReader.readBmp(filePath);
            String file = bmpService.AllSearch(bmp);
            if ("".equals(file)) return "fail";
            else return imgPath + file;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return "fail";
    }

    @RequestMapping("/partSearch/{name}")
    @ResponseBody
    public String PartSearch(@PathVariable("name") String name) {
        String filePath = imgPath + name;
        try {
            Bmp bmp = BmpReader.readBmp(filePath);
            List<String> list = bmpService.PartSearch(bmp);

            String file = list.get(0);
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return "fail";
    }
}
