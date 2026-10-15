package site.nuttachai.line_bot.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.nuttachai.line_bot.config.LineProperties;
import site.nuttachai.line_bot.dto.JwtResponse;
import site.nuttachai.line_bot.exception.LineJwtGenerationException;

import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class LineJwtService {

    private static final String LINE_AUDIENCE = "https://api.line.me/";

    private final LineProperties lineProperties;
    private final RSAKey rsaKey;

    public JwtResponse generateJwt() {
        if (lineProperties.channelId() == null || lineProperties.channelId().isBlank()) {
            throw new LineJwtGenerationException("line.messaging.channel-id must be configured.");
        }
        if (lineProperties.keyId() == null || lineProperties.keyId().isBlank()) {
            throw new LineJwtGenerationException("line.messaging.key-id must be configured.");
        }

        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(lineProperties.jwtLifetime());

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .type(JOSEObjectType.JWT)
                .keyID(lineProperties.keyId())
                .build();

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer(lineProperties.channelId())
                .subject(lineProperties.channelId())
                .audience(LINE_AUDIENCE)
                .issueTime(Date.from(issuedAt))
                .expirationTime(Date.from(expiresAt))
                .claim("token_exp", lineProperties.channelTokenLifetime().toSeconds())
                .build();

        SignedJWT jwt = new SignedJWT(header, claims);
        try {
            jwt.sign(new RSASSASigner(rsaKey));
        } catch (JOSEException | IllegalArgumentException exception) {
            throw new LineJwtGenerationException("Unable to sign the LINE JWT.", exception);
        }

        return new JwtResponse(jwt.serialize(), expiresAt.getEpochSecond());
    }
}
