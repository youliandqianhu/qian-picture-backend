package com.qianhu.qianpicturebackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.qianhu.qianpicturebackend.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class QianPictureBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(QianPictureBackendApplication.class, args);
    }

}
