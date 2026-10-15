package site.nuttachai.line_bot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.nuttachai.line_bot.dto.JwtResponse;
import site.nuttachai.line_bot.service.LineJwtService;

@RestController
@RequestMapping("/api/line")
@RequiredArgsConstructor
public class LineJwtController {

    private final LineJwtService lineJwtService;

    @GetMapping("/jwt")
    public ResponseEntity<JwtResponse> getJwt() {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(lineJwtService.generateJwt());
    }
}
