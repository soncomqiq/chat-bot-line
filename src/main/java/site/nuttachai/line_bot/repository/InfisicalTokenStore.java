package site.nuttachai.line_bot.repository;

import com.infisical.sdk.util.InfisicalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import site.nuttachai.line_bot.component.InfisicalSecretService;
import site.nuttachai.line_bot.constant.StoreKey;
import site.nuttachai.line_bot.dto.line.StoredLineToken;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InfisicalTokenStore {

    private final ObjectMapper objectMapper;

    private final InfisicalSecretService secrets;

    public Optional<StoredLineToken> load() {
        try {
            Optional<String> secret = secrets.getSecret(StoreKey.LINE_CHANNEL_TOKEN_STATE_KEY);

            if (secret.isEmpty()) {
                return Optional.empty();
            }

            StoredLineToken storedLineToken = objectMapper.readValue(secret.get(), StoredLineToken.class);

            return Optional.of(storedLineToken);
        } catch (InfisicalException e) {
            throw new IllegalStateException(
                    "Unable to read LINE token state from Infisical", e
            );
        }
    }

    public void save(StoredLineToken lineToken) {
        String json = objectMapper.writeValueAsString(lineToken);
        try {
            secrets.updateSecret(StoreKey.LINE_CHANNEL_TOKEN_STATE_KEY, json);
        } catch (InfisicalException e) {
            throw new IllegalStateException(
                    "Unable to save LINE token state to Infisical", e);
        }
    }
}
