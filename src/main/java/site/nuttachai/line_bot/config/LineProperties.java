package site.nuttachai.line_bot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "line.messaging")
public record LineProperties(
        String channelId,
        String keyId,
        Duration jwtLifetime,
        Duration channelTokenLifetime
) {
    public LineProperties {
        validateLifetime("jwt-lifetime", jwtLifetime, Duration.ofMinutes(30));
        validateLifetime("channel-token-lifetime", channelTokenLifetime, Duration.ofDays(30));
    }

    private static void validateLifetime(String property, Duration lifetime, Duration maximum) {
        if (lifetime == null || lifetime.compareTo(Duration.ofSeconds(1)) < 0
                || lifetime.compareTo(maximum) > 0) {
            throw new IllegalArgumentException(
                    "line.messaging." + property + " must be between 1 second and " + maximum + "."
            );
        }
    }
}
