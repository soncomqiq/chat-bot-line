package site.nuttachai.line_bot.service;


import com.infisical.sdk.InfisicalSdk;
import com.infisical.sdk.util.InfisicalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.nuttachai.line_bot.config.InfisicalProperties;

@Service
@RequiredArgsConstructor
public class InfisicalSecretService {
    private final InfisicalSdk sdk;
    private final InfisicalProperties properties;

    public String getRequiredSecret(String name) throws InfisicalException {
        var secret = sdk.Secrets().GetSecret(
                name,
                properties.projectId(),
                properties.environment(),
                properties.secretPath(),
                false,
                false,
                "shared"
        );

        if (secret == null ||
                secret.getSecretValue() == null ||
                secret.getSecretValue().isBlank()) {

            throw new IllegalStateException(
                    "Missing required secret: " + name
            );
        }

        return secret.getSecretValue();
    }
}
