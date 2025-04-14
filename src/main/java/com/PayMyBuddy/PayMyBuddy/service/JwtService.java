package com.PayMyBuddy.PayMyBuddy.service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;



import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	    private static final String SECRET_KEY = "967b97b01e669117c11efe16d205b3725aed0dc27be6b867b85634ff189222f0";

	    public String generateToken(UserDetails user) {
	    	return Jwts.builder()
	                .setSubject(user.getUsername())
	                .setIssuedAt(new Date(System.currentTimeMillis()))
	                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) 
	                .signWith(getKey(), SignatureAlgorithm.HS256)
	                .compact();	         
	    }

	    private Key getKey() {
	        byte[] decodedKey = Decoders.BASE64.decode(SECRET_KEY);
	        return Keys.hmacShaKeyFor(decodedKey);
	    }

	    public boolean validateToken(String token, UserDetails userDetails) {
	        final String username = extractUsername(token);
	        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	    }

	    public String extractUsername(String token) {
	        return extractClaim(token, Claims::getSubject);
	    }

	    public Date extractExpiration(String token) {
	        return extractClaim(token, Claims::getExpiration);
	    }

	    public boolean isTokenExpired(String token) {
	        return extractExpiration(token).before(new Date());
	    }

	    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
	        final Claims claims = Jwts.parserBuilder()
	                .setSigningKey(getKey())  
	                .build()
	                .parseClaimsJws(token)
	                .getBody();
	        return claimsResolver.apply(claims);
	    }
	
}
