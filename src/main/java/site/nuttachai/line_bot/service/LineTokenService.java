package site.nuttachai.line_bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.nuttachai.line_bot.client.LineAuthClient;
import site.nuttachai.line_bot.dto.line.LineTokenResponse;
import site.nuttachai.line_bot.dto.line.StoredLineToken;
import site.nuttachai.line_bot.repository.InfisicalTokenStore;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class LineTokenService {

    private final InfisicalTokenStore tokenStore;
    private final LineJwtService lineJwtService;

    private final Object tokenLock = new Object();
    private final LineAuthClient lineAuthClient;

    private volatile StoredLineToken cachedToken;

    public String getAccessToken() {
        StoredLineToken token = cachedToken;

        if (isUsable(token)) {
            return token.accessToken();
        }

        synchronized (tokenLock) {
            token = cachedToken;

            // ตรวจซ้ำ เพราะ cache อาจเปลี่ยนระหว่างรอล็อก
            if (isUsable(token)) {
                return token.accessToken();
            }

            // โหลด Infisical เฉพาะเมื่อยังไม่มี cache
            if (token == null) {
                token = tokenStore.load().orElse(null);
            }

            // ไม่มี token หรือใกล้หมดอายุ จึงขอใหม่
            if (!isUsable(token)) {
                token = issueAndSaveToken();
            }

            cachedToken = token;
            return token.accessToken();
        }
    }

    private boolean isUsable(StoredLineToken token) {
        return token != null
                && token.expiresAtEpochSecond()
                > Instant.now().getEpochSecond() + 60;
    }

    private StoredLineToken issueAndSaveToken() {
        var jwtResponse = lineJwtService.generateJwt();

        Instant startedAt = Instant.now();
        LineTokenResponse response = lineAuthClient.getLineToken(jwtResponse.jwt());

        var token = new StoredLineToken(
                response.accessToken(),
                response.tokenType(),
                response.kid(),
                startedAt.plusSeconds(response.expiresIn()).toEpochMilli()
        );

        tokenStore.save(token);
        return token;
    }
}
