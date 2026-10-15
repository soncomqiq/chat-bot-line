package site.nuttachai.line_bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class LineBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(LineBotApplication.class, args);
    }

}
