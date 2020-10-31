package com.yj.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Accessors(chain = true)
public class Stock {
    private String id;
    private String name;
    private Integer count; //库存
    private Integer sale; //已售
    private Integer version; // 版本
}
