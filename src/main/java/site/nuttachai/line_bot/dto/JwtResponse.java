package site.nuttachai.line_bot.dto;

public record JwtResponse(String jwt, long expiresAt) {
}
