package site.nuttachai.line_bot;

import com.jayway.jsonpath.JsonPath;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import site.nuttachai.line_bot.config.LineProperties;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class LineBotApplicationTests {

    private static Path privateKeyFile;

    @Autowired
    private RSAKey rsaKey;

    @Autowired
    private LineProperties lineProperties;

    @Autowired
    private WebApplicationContext context;

    @DynamicPropertySource
    static void lineProperties(DynamicPropertyRegistry registry) throws IOException, JOSEException {
        privateKeyFile = Files.createTempFile("line-test-key-", ".jwk");
        Files.writeString(privateKeyFile, new RSAKeyGenerator(2048).generate().toJSONString());
        registry.add("line.messaging.channel-id", () -> "test-channel");
        registry.add("line.messaging.key-id", () -> "test-key");
        registry.add("line.messaging.private-key-path", () -> privateKeyFile.toString());
    }

    @AfterAll
    static void deletePrivateKeyFile() throws IOException {
        if (privateKeyFile != null) {
            Files.deleteIfExists(privateKeyFile);
        }
    }

    @Test
    void contextLoads() {
        assertThat(rsaKey.isPrivate()).isTrue();
        assertThat(lineProperties.channelId()).isEqualTo("test-channel");
        assertThat(lineProperties.keyId()).isEqualTo("test-key");
        assertThat(lineProperties.jwtLifetime()).isEqualTo(Duration.ofMinutes(30));
        assertThat(lineProperties.channelTokenLifetime()).isEqualTo(Duration.ofDays(1));
    }

    @Test
    void jwtEndpointReturnsSignedJwtWithExpiryAndDisablesCaching() throws Exception {
        MvcResult result = MockMvcBuilders.webAppContextSetup(context).build()
                .perform(get("/api/line/jwt"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(jsonPath("$.jwt").isString())
                .andExpect(jsonPath("$.expiresAt").isNumber())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        String token = JsonPath.read(json, "$.jwt");
        Number expiresAt = JsonPath.read(json, "$.expiresAt");
        SignedJWT jwt = SignedJWT.parse(token);

        assertThat(jwt.verify(new RSASSAVerifier(rsaKey.toPublicJWK()))).isTrue();
        assertThat(jwt.getHeader().getKeyID()).isEqualTo("test-key");
        assertThat(jwt.getJWTClaimsSet().getIssuer()).isEqualTo("test-channel");
        assertThat(jwt.getJWTClaimsSet().getExpirationTime().toInstant().getEpochSecond())
                .isEqualTo(expiresAt.longValue());
    }

}
