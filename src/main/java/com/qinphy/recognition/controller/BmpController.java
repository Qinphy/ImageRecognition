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
    private final String uploadPath = "/home/qinphy/Recognition/uploads/";
    private final String imageUrl = "http://192.168.137.120/images/";

    /**
     * 上传图片，临时的，/home/qinphy/Recognition/uploads/
     * @param file 前端传来的图片
     * @return "fail":失败，fileName:上传的文件名称，成功！
     */
    @RequestMapping("/uploads")
    @ResponseBody
    public String uploads(@RequestParam(value = "file") MultipartFile file) {
        String fileName = file.getOriginalFilename();
        File upFile = new File(uploadPath + fileName);

        if (!upFile.getParentFile().exists()) upFile.mkdirs();

        try {
            file.transferTo(upFile);
            return fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return "fail";
        }
    }

    /**
     * 向图片库添加图片
     * @param file 前端传来的图片
     * @return "fail":失败，fileName:文件名称
     */
    @RequestMapping("/addImage")
    @ResponseBody
    public String uploadAdd(@RequestParam(value = "file") MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String filePath = imgPath + fileName;
        File upFile = new File(filePath);
        if (!upFile.getParentFile().exists()) upFile.mkdirs();

        try {
            file.transferTo(upFile);
            Bmp bmp = BmpReader.readBmp(filePath);

            bmpService.uploadHDFS(filePath);
            bmpService.insert(bmp);

            return fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return "fail";
        }
    }

    /**
     * 全图搜索
     * @param name 上传的图片
     * @return "fail":失败，fileName:成功
     */
    @RequestMapping("/allSearch/{name}")
    @ResponseBody
    public String AllSaerch(@PathVariable("name") String name) {
        String filePath = uploadPath + name;
        try {
            Bmp bmp = BmpReader.readBmp(filePath);
            String file = bmpService.AllSearch(bmp);
            if ("".equals(file)) return "fail";
            else if ("fail".equals(file)) return "fail";
            else return imageUrl + file;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return "fail";
    }

    /**
     * 局部搜索
     * @param name 上传的图片
     * @return "fail":失败，fileName:成功
     */
    @RequestMapping("/partSearch/{name}")
    @ResponseBody
    public String PartSearch(@PathVariable("name") String name) {
        String filePath = uploadPath + name;
        try {
            Bmp bmp = BmpReader.readBmp(filePath);
            List<String> list = bmpService.PartSearch(bmp);

            String file = list.get(0);

            System.out.println(file);
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
