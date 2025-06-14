package com.example.allomaison.Security;

import com.example.allomaison.Config.JwtProperties;
import com.example.allomaison.DTOs.AdminDTO;
import com.example.allomaison.Mapper.AdminMapper;
import com.example.allomaison.Repositories.AdminRepository;
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
public class AdminJwtService {

    private final AdminRepository adminRepository;
    private final JwtProperties jwtProperties;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(AdminDTO admin) {
        return Jwts.builder()
                .setSubject("admin-auth")
                .claim("adminName", admin.getAdminName())
                .claim("adminId", admin.getAdminId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpirationMs()))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Optional<AdminDTO> parseToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String adminName = claims.get("adminName", String.class);
            Long adminId = claims.get("adminId", Long.class);

            return adminRepository.findById(adminId)
                    .filter(admin -> admin.getAdminName().equals(adminName))
                    .map(AdminMapper::toDTO);

        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
