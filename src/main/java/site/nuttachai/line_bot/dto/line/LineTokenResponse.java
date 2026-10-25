package site.nuttachai.line_bot.dto.line;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LineTokenResponse(
        @JsonProperty("access_token") String access_token,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("expires_in") long expiresIn,
        @JsonProperty("key_id") String kid
) {
}
