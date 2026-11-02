package site.nuttachai.line_bot.component;


import com.infisical.sdk.InfisicalSdk;
import com.infisical.sdk.util.InfisicalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import site.nuttachai.line_bot.config.InfisicalProperties;

import java.util.Optional;

@Component
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

    public Optional<String> getSecret(String name) throws InfisicalException {

        var secret = sdk.Secrets().GetSecret(
                name,
                properties.projectId(),
                properties.environment(),
                properties.secretPath(),
                false,
                false,
                "shared"
        );

        // Should not be happened
        if (secret == null || secret.getSecretValue() == null) {
            throw new IllegalStateException(
                    "Invalid secret response: " + name
            );
        }

        String value = secret.getSecretValue();

        return value.isBlank()
                ? Optional.empty()
                : Optional.of(value);
    }

    public void updateSecret(String name, String value) throws InfisicalException {
        sdk.Secrets().UpdateSecret(
                name,
                properties.projectId(),
                properties.environment(),
                properties.secretPath(),
                value,
                null
        );
    }
}
