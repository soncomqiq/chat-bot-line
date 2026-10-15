package site.nuttachai.line_bot.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "infisical")
public record InfisicalProperties(
        String clientId,
        String clientSecret,
        String projectId,
        String environment,
        String secretPath
) {
}
