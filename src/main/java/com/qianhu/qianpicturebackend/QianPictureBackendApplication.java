package com.qianhu.qianpicturebackend;

import org.apache.shardingsphere.spring.boot.ShardingSphereAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

// TODO 暂时放弃分库分表。
// TODO 实际上存在很多问题，比如我原先的公共图库spaceId都为null,加了分库分表后spaceId是不能为null的,也就是我空间的增删改查都要求spaceId有个默认值(0就行)，然后之后的判断也要判断spaceId为0，比较麻烦
@SpringBootApplication(exclude = {ShardingSphereAutoConfiguration.class})
//@SpringBootApplication
@EnableAsync
@MapperScan("com.qianhu.qianpicturebackend.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class QianPictureBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(QianPictureBackendApplication.class, args);
    }

}
