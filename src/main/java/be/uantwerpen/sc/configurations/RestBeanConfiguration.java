package be.uantwerpen.sc.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestBeanConfiguration {

    // define REST bean for use in backends
    // laat toe om dit te autowiren
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
