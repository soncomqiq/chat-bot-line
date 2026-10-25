package site.nuttachai.line_bot.config;

import com.infisical.sdk.util.InfisicalException;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import site.nuttachai.line_bot.exception.InvalidLineKeyException;
import site.nuttachai.line_bot.service.InfisicalSecretService;

import java.text.ParseException;

@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class LineKeyConfiguration {

    private final InfisicalSecretService secrets;

    @Bean
    public RSAKey lineMessagingRSAKey() throws InfisicalException {
        String json = secrets.getRequiredSecret(
                "LINE_PRIVATE_JWK"
        );

        RSAKey key;
        try {
            key = RSAKey.parse(json);
        } catch (ParseException exception) {
            throw new InvalidLineKeyException("The LINE private key file must contain a valid RSA JWK.", exception);
        }

        if (!key.isPrivate()) {
            throw new InvalidLineKeyException("The LINE RSA JWK must include a private key.");
        }

        return key;
    }
}
