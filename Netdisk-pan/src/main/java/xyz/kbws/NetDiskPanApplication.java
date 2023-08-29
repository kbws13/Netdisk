package xyz.kbws;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author hsy
 * @date 2023/8/12
 */
@EnableAsync    //异步调用(大文件上传后异步调用组装起来)
@SpringBootApplication(scanBasePackages = "xyz.kbws")
@MapperScan(basePackages = {"xyz.kbws.mappers"})
@EnableTransactionManagement    //事务生效
@EnableScheduling   //定时任务
public class NetDiskPanApplication {
    public static void main(String[] args) {
        SpringApplication.run(NetDiskPanApplication.class, args);
    }
}
