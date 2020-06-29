package com.qinphy.recognition.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author: Qinphy
 * @Description: 用于存储模糊查找的图片名称和相似度
 * @date: 2020/6/29 10:06
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Vague {
    private String fileName;
    private String rate;

    public String toString() {
        return fileName + ":" + rate;
    }
}
