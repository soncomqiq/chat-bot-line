package site.nuttachai.line_bot.dto.line;

public record StoredLineToken(
        String accessToken,
        String tokenType,
        String tokenKeyId,
        long expiresAtEpochSecond
) {}