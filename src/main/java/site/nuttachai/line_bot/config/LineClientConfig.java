package site.nuttachai.line_bot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class LineClientConfig {

    @Value("${client.line.api-base-url}")
    private String lineBasedUrl;

    @Value("${client.line.connect-timeout}")
    private Duration lineConnectTimeout;

    @Value("${client.line.read-timeout}")
    private Duration lineReadTimeout;

    @Bean
    public RestClient lineRestClient(RestClient.Builder builder) {
        HttpClient httpClient = HttpClient.newBuilder().connectTimeout(lineConnectTimeout).build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(lineReadTimeout);
        return builder.baseUrl(lineBasedUrl).requestFactory(requestFactory).build();
    }
}
