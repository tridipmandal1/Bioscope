package com.bioscope.backend.v01.security;


import com.bioscope.backend.v01.entities.UserEntity;
import com.bioscope.backend.v01.repos.UserRepository;
import com.bioscope.backend.v01.services.iface.TokenBlacklistService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String SECRET;

    @Value("${jwt.expiration}")
    private long EXPIRATION;

    @Value("${jwt.authorities}")
    private String AUTHORITIES;

    @Value("${jwt.issuer}")
    private String ISSUER;

    private final UserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;
    private final UserRepository userRepository;

    public JwtProvider(UserDetailsService userDetailsService, TokenBlacklistService tokenBlacklistService, UserRepository userRepository) {
        this.userDetailsService = userDetailsService;
        this.tokenBlacklistService = tokenBlacklistService;
        this.userRepository = userRepository;
    }


    public long getExpiration() {
        return this.EXPIRATION;
    }

    public String extractUsernameFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    public String generateAccessToken(UserDetails userDetails){
        return generateToken(userDetails, new HashMap<>());
    }

    private String generateToken(UserDetails userDetails, HashMap<String, Object> extractClaims) {
        return buildToken(userDetails, getExpiration());
    }
    public String generateRefreshToken(UserDetails userDetails) {
        return buildRefreshToken(userDetails, getExpiration() * 5);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Key getSigningKey() {
        byte[] secretBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(secretBytes);
    }

    private Claims extractAllClaims(String token) {

        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private String buildToken(UserDetails userDetails, long expiration) {

        return Jwts.builder()
                .claim(this.AUTHORITIES, userDetails.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority).toList())
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .setIssuer(this.ISSUER)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private String buildRefreshToken(UserDetails userDetails, long expiration) {
        return Jwts
                .builder()
                .setSubject(userDetails.getUsername())
                .setIssuer(this.ISSUER)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String createTicketToken(
            String hostId,
            Date expiration,
            String ticketId
    ) {
        return Jwts
                .builder()
                .setIssuer(this.ISSUER)
                .setSubject(hostId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expiration)
                .claim("Ticket-Id", ticketId)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extractTicketClaims(String token) {
        return extractAllClaims(token);
    }

    public String createVerificationToken(String email) {
        UserEntity userDetails = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return Jwts
                .builder()
                .setIssuer(this.ISSUER)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + (this.EXPIRATION * 6)))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isValidToken(String token, UserDetails userDetails) {
        final String username = userDetails.getUsername();
        return (username.equals(extractUsernameFromToken(token))
                && !isTokenExpired(token) && !tokenBlacklistService.isTokenBlacklisted(token));
    }
    public boolean validateToken(String token) {
        final String username = extractUsernameFromToken(token);
        if (!Objects.isNull(username) || !isTokenExpired(token)) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            return isValidToken(token, userDetails);
        }
        return false;
    }

    public String findTicketIdFromToken(String token) {
        final JwtParser parser = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build();

        final Jws<Claims> claimsJws = parser.parseClaimsJws(token);
        final Claims claims = claimsJws.getBody();

        return claims.get("Ticket-Id", String.class);
    }
    public UsernamePasswordAuthenticationToken authenticationToken(String token, UserDetails userDetails) {

        final JwtParser jwtParser = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build();
        final Jws<Claims> claimsJws = jwtParser.parseClaimsJws(token);
        final Claims claims = claimsJws.getBody();

        final Collection<? extends GrantedAuthority> authorities =
                ((List<?>) claims.get(this.AUTHORITIES)).stream()
                        .map(authority ->
                                new SimpleGrantedAuthority(authority.toString()))
                        .toList();
        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }
    public void blacklistToken(String token){
        tokenBlacklistService.addTokenToBlacklist(token);
    }

    public String getTokenFromHttpRequest(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (Objects.isNull(header) || !header.startsWith("Bearer ")) {
            return null;
        }
        return header.substring(7).trim();
    }
}
