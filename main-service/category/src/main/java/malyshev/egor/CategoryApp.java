package malyshev.egor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
@ConfigurationPropertiesScan
public class CategoryApp {
    public static void main(String[] args) {
        System.setProperty("spring.main.allow-bean-definition-overriding", "true");
        SpringApplication.run(CategoryApp.class, args);
    }
}