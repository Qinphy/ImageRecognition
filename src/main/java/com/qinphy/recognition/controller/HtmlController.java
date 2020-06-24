package com.qinphy.recognition.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author: Qinphy
 * @Description: 静态页面管理
 * @date: 2020/6/23 21:16
 */
@Controller
public class HtmlController {

    @RequestMapping("/index.html")
    public String index() {
        return "index";
    }

    @RequestMapping("/map")
    public String mapreduce() {
        return "mapreduce";
    }

    @RequestMapping("/rdd")
    public String spark() {
        return "spark";
    }

    @RequestMapping("/add")
    public String add() {
        return "imgbox";
    }
}
