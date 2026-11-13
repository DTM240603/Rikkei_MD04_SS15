package ra.edu.hw.md04.ss15.service;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ra.edu.hw.md04.ss15.dto.LoginRequest;
import ra.edu.hw.md04.ss15.dto.LoginResponse;
import ra.edu.hw.md04.ss15.entity.RefreshToken;
import ra.edu.hw.md04.ss15.entity.User;
import ra.edu.hw.md04.ss15.repository.RefreshTokenRepository;
import ra.edu.hw.md04.ss15.repository.UserRepository;

@Service
public class AuthService {

    private static final long REFRESH_TOKEN_TTL_DAYS = 7;

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .filter(account -> passwordEncoder.matches(request.password(), account.getPasswordHash()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));

        Instant now = Instant.now();
        String refreshToken = generateRefreshToken();
        refreshTokenRepository.save(new RefreshToken(
                hashToken(refreshToken),
                user,
                now.plus(REFRESH_TOKEN_TTL_DAYS, ChronoUnit.DAYS),
                now
        ));

        return new LoginResponse(
                jwtService.generateAccessToken(user.getUsername(), user.getRole()),
                refreshToken,
                "Bearer",
                JwtService.ACCESS_TOKEN_TTL_SECONDS
        );
    }

    private String generateRefreshToken() {
        byte[] tokenBytes = new byte[64];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    private String hashToken(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(token.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(digest);
        } catch (java.security.NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }
}
