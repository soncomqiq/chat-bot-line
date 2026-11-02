package site.nuttachai.line_bot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.nuttachai.line_bot.client.LineAuthClient;
import site.nuttachai.line_bot.dto.JwtResponse;
import site.nuttachai.line_bot.dto.line.LineTokenResponse;
import site.nuttachai.line_bot.service.LineJwtService;
import site.nuttachai.line_bot.service.LineTokenService;

@RestController
@RequestMapping("/api/line")
@RequiredArgsConstructor
public class LineJwtController {

    private final LineTokenService lineTokenService;

    @GetMapping("/jwt")
    public ResponseEntity<String> getJwt() {
        String accessToken = lineTokenService.getAccessToken();
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(accessToken);
    }
}
