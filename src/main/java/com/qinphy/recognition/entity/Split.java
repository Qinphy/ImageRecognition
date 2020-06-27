package com.qinphy.recognition.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author: Qinphy
 * @Description: 分割后的小图片
 * @date: 2020/6/27 19:20
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Split {
    private byte[] data;
    private int sum;
}
