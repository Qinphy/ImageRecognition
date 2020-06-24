package com.qinphy.recognition.entity;

import lombok.*;

/**
 * @author: Qinphy
 * @Description: This is bmp image File message class.
 * @date: 2020/6/24 8:47
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Bmp {
    private String name;
    private int[][] data;
    private int[] counter;
}
