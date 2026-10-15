package site.nuttachai.line_bot.config;

import com.infisical.sdk.InfisicalSdk;
import com.infisical.sdk.config.SdkConfig;
import com.infisical.sdk.util.InfisicalException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class InfisicalConfiguration {

    @Bean
    public InfisicalSdk infisicalSdk(InfisicalProperties infisicalProperties) throws InfisicalException {
        var sdk = new InfisicalSdk(new SdkConfig.Builder().build());

        sdk.Auth().UniversalAuthLogin(
                infisicalProperties.clientId(),
                infisicalProperties.clientSecret()
        );

        return sdk;
    }
}
