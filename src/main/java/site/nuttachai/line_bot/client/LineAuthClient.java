package site.nuttachai.line_bot.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import site.nuttachai.line_bot.constant.LineConstant;
import site.nuttachai.line_bot.dto.line.LineTokenResponse;

@Component
@RequiredArgsConstructor
public class LineAuthClient {
    private final RestClient lineRestClient;

    public LineTokenResponse getLineToken(String jwtToken) {
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();

        data.add(LineConstant.GRANT_TYPE_PARAM, LineConstant.CLIENT_CREDENTIALS_GRANT_TYPE);
        data.add(LineConstant.CLIENT_ASSERTION_TYPE_PARAM, LineConstant.JWT_BEARER_ASSERTION_TYPE);
        data.add(LineConstant.CLIENT_ASSERTION_PARAM, jwtToken);

        return lineRestClient.post()
                .uri("/oauth2/v2.1/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(data)
                .retrieve()
                .body(LineTokenResponse.class);
    }
}
