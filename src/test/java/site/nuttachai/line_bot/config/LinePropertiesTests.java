package site.nuttachai.line_bot.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class LinePropertiesTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(PropertiesConfiguration.class);

    @Test
    void bindsCustomLifetimes() {
        contextRunner.withPropertyValues(
                "line.messaging.jwt-lifetime=5m",
                "line.messaging.channel-token-lifetime=7d"
        ).run(context -> {
            assertThat(context).hasNotFailed();
            LineProperties properties = context.getBean(LineProperties.class);
            assertThat(properties.jwtLifetime()).isEqualTo(Duration.ofMinutes(5));
            assertThat(properties.channelTokenLifetime()).isEqualTo(Duration.ofDays(7));
        });
    }

    @Test
    void acceptsMaximumLifetimes() {
        contextRunner.withPropertyValues(
                "line.messaging.jwt-lifetime=30m",
                "line.messaging.channel-token-lifetime=30d"
        ).run(context -> assertThat(context).hasNotFailed());
    }

    @ParameterizedTest
    @CsvSource({
            "jwt-lifetime, 0s", "jwt-lifetime, -1s", "jwt-lifetime, 500ms", "jwt-lifetime, 1801s",
            "channel-token-lifetime, 0s", "channel-token-lifetime, -1s",
            "channel-token-lifetime, 500ms", "channel-token-lifetime, 31d"
    })
    void rejectsInvalidLifetimesAtStartup(String property, String value) {
        contextRunner.withPropertyValues(
                "line.messaging.jwt-lifetime=30m",
                "line.messaging.channel-token-lifetime=1d",
                "line.messaging." + property + "=" + value
        ).run(context -> {
            assertThat(context).hasFailed();
            assertThat(context.getStartupFailure())
                    .hasRootCauseInstanceOf(IllegalArgumentException.class)
                    .hasStackTraceContaining("line.messaging." + property + " must be between");
        });
    }

    @ParameterizedTest
    @CsvSource({"jwt-lifetime, channel-token-lifetime=1d", "channel-token-lifetime, jwt-lifetime=30m"})
    void rejectsMissingLifetimesAtStartup(String missingProperty, String configuredProperty) {
        contextRunner.withPropertyValues("line.messaging." + configuredProperty).run(context -> {
            assertThat(context).hasFailed();
            assertThat(context.getStartupFailure())
                    .hasRootCauseInstanceOf(IllegalArgumentException.class)
                    .hasStackTraceContaining("line.messaging." + missingProperty + " must be between");
        });
    }

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(LineProperties.class)
    static class PropertiesConfiguration {}
}
