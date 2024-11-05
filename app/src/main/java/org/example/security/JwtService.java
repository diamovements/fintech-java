package org.example.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.example.entity.InvalidToken;
import org.example.repository.InvalidTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${security.jwt.secret}")
    private String secretKey;
    @Value("${security.jwt.exp-remember}")
    private Long expRemember;
    @Value("${security.jwt.exp-noremember}")
    private Long expNoRemember;
    private final InvalidTokenRepository invalidTokenRepository;

    private SecretKey getSigningKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }


    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }


    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }


    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        if (isTokenExpired(token)) return false;
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()));
    }

    public String generateToken(UserDetails userDetails, Long expireMs) {
        Map<String, Object> extraClaims = new HashMap<>();
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expireMs))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateToken(UserDetails userDetails, boolean remember) {
        long expiration = remember ? expRemember : expNoRemember;
        return generateToken(userDetails, expiration);
    }

    public void invalidateToken(String token) {
        Date exp = extractExpiration(token);
        InvalidToken inv = new InvalidToken();
        inv.setToken(token);
        inv.setExpirationDate(exp.toInstant());
        invalidTokenRepository.save(inv);
    }
    public boolean isTokenInvalidated(String token) {
        return invalidTokenRepository.findByToken(token).isPresent();
    }
}
