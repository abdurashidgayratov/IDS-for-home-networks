package uz.ids.homenetwork;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HomeNetworkApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(HomeNetworkApplication.class, args);
    }
}
