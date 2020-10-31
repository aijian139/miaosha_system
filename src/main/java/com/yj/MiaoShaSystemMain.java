package com.yj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.yj.dao")
public class MiaoShaSystemMain {

    public static void main(String[] args) {
        SpringApplication.run(MiaoShaSystemMain.class,args);
    }
}
