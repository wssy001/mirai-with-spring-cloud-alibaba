package cyou.wssy001.cloud.bot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableDiscoveryClient
@SpringBootApplication
@MapperScan("cyou.wssy001.cloud.bot.dao")
public class QQRobotApplication {

    public static void main(String[] args) {
        SpringApplication.run(QQRobotApplication.class, args);
    }

}
