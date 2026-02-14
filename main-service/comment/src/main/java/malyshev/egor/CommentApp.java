package malyshev.egor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackageClasses = InteractionApiManager.class)
@EnableDiscoveryClient
@SpringBootApplication
@ConfigurationPropertiesScan
public class CommentApp {
    public static void main(String[] args) {
        SpringApplication.run(CommentApp.class, args);
    }
}