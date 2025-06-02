package com.example.allomaison.Security;

import com.example.allomaison.Config.JwtProperties;
import com.example.allomaison.DTOs.UserDTO;
import com.example.allomaison.Repositories.UserRepository;
import com.example.allomaison.Mapper.UserMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final UserRepository userRepository;
    private final JwtProperties jwtProperties;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UserDTO user) {
        return Jwts.builder()
                .setSubject("user-auth")
                .claim("userName", user.getUserName())
                .claim("userId", user.getUserId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpirationMs()))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Optional<UserDTO> parseToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String userName = claims.get("userName", String.class);
            Long userId = claims.get("userId", Long.class);

            return userRepository.findById(userId)
                    .filter(user -> user.getUserName().equals(userName))
                    .map(UserMapper::toDTO);

        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
